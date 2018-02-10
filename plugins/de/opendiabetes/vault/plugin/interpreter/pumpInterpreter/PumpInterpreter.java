/*
 * Copyright (C) 2017 Jens Heuschkel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.plugin.interpreter.pumpInterpreter;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.plugin.importer.medtronic.MedtronicAnnotatedVaultEntry;
import de.opendiabetes.vault.plugin.importer.medtronic.MedtronicCSVValidator;
import de.opendiabetes.vault.plugin.interpreter.vaultInterpreter.VaultInterpreter;
import de.opendiabetes.vault.plugin.util.SlidingWindow;
import de.opendiabetes.vault.plugin.util.SortVaultEntryByDate;
import de.opendiabetes.vault.plugin.util.TimestampUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mswin
 */
public class PumpInterpreter extends VaultInterpreter {
    /**
     * Conversion factor from minutes to milliseconds.
     */
    private static final int MS_PER_MINUTE = 60000;

    /**
     * Conversion factor from hours to milliseconds.
     */
    private static final long HOUR_TO_MS = 3600000;

    /**
     * Conversion factor from hours to milliseconds.
     */
    private static final long HOUR_TO_MIN = 60;

    /**
     * //TODO javadoc
     */
    private boolean fillCanulaAsNewKatheder;

    /**
     * //TODO javadoc
     */
    private int fillCanulaCooldown;


    /**
     * {@inheritDoc}
     */
    @Override
    public List<VaultEntry> interpret(final List<VaultEntry> result) {
        List<VaultEntry> data = result;

        // sort by date
        Collections.sort(data, new SortVaultEntryByDate());

        LOG.finer("Start basal interpretation");
        data = applyTempBasalEvents(data);
        Collections.sort(data, new SortVaultEntryByDate());
        data = considerSuspendAsBasalOff(data);
        Collections.sort(data, new SortVaultEntryByDate());
        LOG.finer("Start fill canula interpretation");
        data = fillCanulaInterpretation(data);
        Collections.sort(data, new SortVaultEntryByDate());
        LOG.finer("Start CGM Alert interpretation");
        data = addCgmValueToCgmAlertOnMedtronicPumps(data);
        Collections.sort(data, new SortVaultEntryByDate());
        LOG.finer("Start CGM elevation calculation");
        data = calculateCgmElevation(data);
        Collections.sort(data, new SortVaultEntryByDate());

        LOG.finer("Pump data interpretation finished");
        return data;
    }

    /**
     * //TODO javadoc
     *
     * @param importedData
     * @return
     */
    private List<VaultEntry> fillCanulaInterpretation(final List<VaultEntry> importedData) {
        if (importedData == null || importedData.isEmpty()) {
            return importedData;
        }

        VaultEntry rewindHandle = null;
        VaultEntry primeHandle = null;
        VaultEntry latestFillCanulaHandle = null;
        VaultEntry canulaFillAsPumpFillCandidate = null;
        int cooldown = 0;
        List<VaultEntry> fillEvents = new ArrayList<>();

        // configure options        
        if (fillCanulaAsNewKatheder) {
            // ignore cooldown if option is disabled
            cooldown = fillCanulaCooldown * MS_PER_MINUTE;
        }

        // check if handle prefill is needed
        VaultEntry rewindFromDb = getDb().queryLatestEventBefore(importedData.get(0).getTimestamp(),
                VaultEntryType.PUMP_REWIND);
        VaultEntry primeFromDb = getDb().queryLatestEventBefore(importedData.get(0).getTimestamp(),
                VaultEntryType.PUMP_PRIME);
        if (rewindFromDb != null) {
            if (((primeFromDb != null
                    && rewindFromDb.getTimestamp().after(primeFromDb.getTimestamp()))
                    || primeFromDb == null)
                    && (importedData.get(0).getTimestamp().getTime()
                    - rewindFromDb.getTimestamp().getTime()) < 3 * HOUR_TO_MS) {
                // rewind without prime has no fill event --> add as handle when its within 3 hours
                rewindHandle = rewindFromDb;

            } else if (primeFromDb != null && (rewindFromDb.getTimestamp().getTime()
                    - importedData.get(0).getTimestamp().getTime()) < cooldown) {
                // rewind has prime (so it has a fill event) but is within cooldown
                // --> prefill handles and delete their fill event
                // TODO find and kill fill event in db
                rewindHandle = rewindFromDb;
                primeHandle = primeFromDb;
            }
        }

        // go through timeline
        for (VaultEntry item : importedData) {

            // reset handles if cooldown is over
            if (rewindHandle != null && primeHandle != null
                    && ((item.getTimestamp().getTime() - rewindHandle.getTimestamp().getTime())
                    > cooldown)) {

                Date fillDate;
                if (latestFillCanulaHandle != null) {
                    fillDate = latestFillCanulaHandle.getTimestamp();
                } else {
                    fillDate = primeHandle.getTimestamp();
                }
                fillEvents.add(new VaultEntry(VaultEntryType.PUMP_FILL_INTERPRETER,
                        fillDate,
                        VaultEntry.VALUE_UNUSED));
                rewindHandle = null;
                primeHandle = null;
                latestFillCanulaHandle = null;
                canulaFillAsPumpFillCandidate = null;
            }

            // reverse cooldown for canula as ne katheder interpretation
            if (fillCanulaAsNewKatheder
                    && canulaFillAsPumpFillCandidate != null
                    && ((item.getTimestamp().getTime()
                    - canulaFillAsPumpFillCandidate.getTimestamp().getTime())
                    > cooldown)) {
                // there is no rewind within the range --> new fill event
                fillEvents.add(canulaFillAsPumpFillCandidate);
            }

            // find new pairs
            if (item.getType() == VaultEntryType.PUMP_REWIND) {
                // filling line starts with rewind
                rewindHandle = item;
            } else if (item.getType() == VaultEntryType.PUMP_PRIME) {
                // is pump rewinded?
                if (rewindHandle != null) {
                    // is pump already primed?
                    if (primeHandle == null) {
                        // no --> this is the prime
                        primeHandle = item;
                    } else {
                        // yes --> must be fill canula
                        latestFillCanulaHandle = item;
                    }
                } else if (fillCanulaAsNewKatheder) {
                    // no prime event? --> new katheder (if enabled)
                    canulaFillAsPumpFillCandidate = new VaultEntry(VaultEntryType.PUMP_FILL_INTERPRETER,
                            item.getTimestamp(),
                            VaultEntry.VALUE_UNUSED);
                }
            }
        }

        // process last prime entrys
        if (rewindHandle != null && primeHandle != null) {
            Date fillDate;
            if (latestFillCanulaHandle != null) {
                fillDate = latestFillCanulaHandle.getTimestamp();
            } else {
                fillDate = primeHandle.getTimestamp();
            }
            fillEvents.add(new VaultEntry(VaultEntryType.PUMP_FILL_INTERPRETER,
                    fillDate,
                    VaultEntry.VALUE_UNUSED));
        }

        //merge
        importedData.addAll(fillEvents);

        // sort by date again <-- not neccesary because database will do it
        //Collections.sort(result, new SortVaultEntryByDate());
        return importedData;
    }

    /**
     * //TODO javadoc
     *
     * @param data
     * @return
     */
    private List<VaultEntry> considerSuspendAsBasalOff(final List<VaultEntry> data) {
        // suspends will stop basal rate --> add basal 0 point
        // after suspension, pump has new basal event by itselve
        // while suspension, pump does not create basal profile events :)
        if (data == null || data.isEmpty()) {
            return data;
        }

        List<VaultEntry> basalEvents = new ArrayList<>();
        List<VaultEntry> killedBasalEvents = new ArrayList<>();

        for (VaultEntry suspendItem : data) {
            if (suspendItem.getType() == VaultEntryType.PUMP_SUSPEND) {

                // find corresponding unsuspend
                VaultEntry unsuspendEvent = null;
                for (VaultEntry unspendItem : data) {
                    if (unspendItem.getType() == VaultEntryType.PUMP_UNSUSPEND
                            && unspendItem.getTimestamp().after(suspendItem.getTimestamp())) {
                        // found it
                        unsuspendEvent = unspendItem;
                        break;
                    }
                }

                // at start add basal 0
                basalEvents.add(new VaultEntry(VaultEntryType.BASAL_INTERPRETER,
                        suspendItem.getTimestamp(), 0.0));

                VaultEntry lastKnownBasalEntry = null;
                if (unsuspendEvent != null) {
                    // kill basal items between the suspension           
                    for (VaultEntry killItem : data) {
                        if ((killItem.getType() == VaultEntryType.BASAL_PROFILE
                                || killItem.getType() == VaultEntryType.BASAL_MANUAL)
                                && TimestampUtils.withinDateTimeSpan(
                                suspendItem.getTimestamp(),
                                unsuspendEvent.getTimestamp(),
                                killItem.getTimestamp())) {
                            // found basal item within suspention time span
                            killedBasalEvents.add(killItem);
                            lastKnownBasalEntry = killItem; // update last known basal entry
                        } else if ((killItem.getType() == VaultEntryType.BASAL_PROFILE
                                || killItem.getType() == VaultEntryType.BASAL_MANUAL)
                                && killItem.getTimestamp().after(unsuspendEvent.getTimestamp())) {
                            // we can stop when we exit the time span
                            break;
                        }
                    }
                } else {
                    // didn't find corresponding unsuspend item
                    LOG.log(Level.WARNING,
                            "Found no unsuspend item. "
                                    + "Cannot kill basal profile items: {0}",
                            suspendItem.toString());
                    break;
                }

                // at end set basal to old value
                if (lastKnownBasalEntry == null) {
                    // no profile elements within the suspension 
                    // --> we have to search the last known one before the suspenstion
                    for (VaultEntry basalEntry : data) {
                        if (basalEntry.getType() == VaultEntryType.BASAL_MANUAL
                                || basalEntry.getType() == VaultEntryType.BASAL_PROFILE) { // no interpreter basal items, since suspension will interrupt tmp basal
                            if (suspendItem.getTimestamp().after(basalEntry.getTimestamp())) {
                                lastKnownBasalEntry = basalEntry;
                            } else if (suspendItem.getTimestamp().before(basalEntry.getTimestamp())) { // we passed the suspension time point --> stop the search
                                break;
                            }
                        }
                    }

                    if (lastKnownBasalEntry == null) {
                        // still nothing found, search in DB
                        // query db
                        Date ts1 = TimestampUtils.addMinutesToTimestamp(data.get(0).getTimestamp(), -1 * 5 * HOUR_TO_MIN); // start 5 hours before with the search
                        Date ts2 = data.get(0).getTimestamp(); // we search just until the current dataset starts
                        List<VaultEntry> dbBasalData = getDb().queryBasalBetween(ts1, ts2);

                        // search for profile entry
                        for (VaultEntry basalEntry : dbBasalData) { // no interpreter basal items, since suspension will interrupt tmp basal
                            if (suspendItem.getTimestamp().after(basalEntry.getTimestamp())) {
                                lastKnownBasalEntry = basalEntry;
                            } else if (suspendItem.getTimestamp().before(basalEntry.getTimestamp())) { // we passed the suspension time point --> stop the search
                                break;
                            }
                        }
                    }
                }

                // add restore element
                if (lastKnownBasalEntry != null) {
                    basalEvents.add(new VaultEntry(VaultEntryType.BASAL_INTERPRETER,
                            TimestampUtils.createCleanTimestamp(unsuspendEvent.getTimestamp()),
                            lastKnownBasalEntry.getValue()));
                } else {
                    LOG.log(Level.WARNING,
                            "Cant find a basal item to restore suspension for: {0}",
                            suspendItem.toString());
                }
            }
        }
        data.removeAll(killedBasalEvents);
        data.addAll(basalEvents);
        return data;
    }

    /**
     * //TODO javadoc
     *
     * @param data
     * @return
     */
    private List<VaultEntry> applyTempBasalEvents(final List<VaultEntry> data) {
        // if tmp basal ocures, real basal rate must be calculated
        // it is possible, that tmp basal rate events have an effect on db data <-- ?
        if (data == null || data.isEmpty()) {
            return data;
        }
        List<VaultEntry> basalEvents = new ArrayList<>();
        List<VaultEntry> historicBasalProfileEvents = new ArrayList<>();
        List<VaultEntry> killedBasalEvents = new ArrayList<>();

        for (VaultEntry item : data) {
            if (item.getType() == VaultEntryType.BASAL_MANUAL
                    && item instanceof MedtronicAnnotatedVaultEntry) {
                MedtronicAnnotatedVaultEntry basalItem
                        = (MedtronicAnnotatedVaultEntry) item;

                // get affected historic elements from this dataset
                List<VaultEntry> affectedHistoricElements = new ArrayList<>();
                for (int i = historicBasalProfileEvents.size() - 1; i >= 0; i--) {
                    VaultEntry historicItem = historicBasalProfileEvents.get(i);

                    if (basalItem.getDuration() > (basalItem.getTimestamp().getTime()
                            - historicItem.getTimestamp().getTime())) {
                        // kill event and save value for percentage calculation
                        killedBasalEvents.add(historicItem);
                        affectedHistoricElements.add(historicItem);
                    } else {
                        // yungest now available element is not affected 
                        // --> no other remaining elements are affected
                        // add to affected list for calculation but don't kill it
                        affectedHistoricElements.add(historicItem);
                        break;
                    }
                }
                if (affectedHistoricElements.isEmpty()) {
                    // try to get it from DB
                    VaultEntry tmpItem = getDb().queryLatestEventBefore(TimestampUtils.createCleanTimestamp(
                            new Date(Math.round(
                                    basalItem.getTimestamp().getTime()
                                            - basalItem.getDuration()))),
                            VaultEntryType.BASAL_PROFILE);

                    if (tmpItem != null) {
                        affectedHistoricElements.add(tmpItem);
                    } else if ((basalItem.getRawType() == MedtronicCSVValidator.TYPE.BASAL_TMP_PERCENT //TODO is this interpreter only for medtronic data
                            && basalItem.getValue() > 0)) {
                        LOG.log(Level.WARNING, "Could not calculate tmp basal, "
                                        + "because no profile elements are found\n{0}",
                                basalItem.toString());
                        killedBasalEvents.add(item); // kill the item since we cannot calculate its meaning
                        continue;
                    }
                }

                // apply changes
                switch (basalItem.getRawType()) {
                    case BASAL_TMP_PERCENT:
                        // calculate new rate
                        Date startTimestamp = TimestampUtils.createCleanTimestamp(
                                new Date((long) (basalItem.getTimestamp().getTime()
                                        - basalItem.getDuration())));

                        // calculate basal value
                        double newBasalValue = 0;
                        // first item need special treatment, since we need to use the start
                        // timestamp of the tmp basal rate, not the timestamp of the profile item
                        if (basalItem.getValue() > 0 && !affectedHistoricElements.isEmpty()) {
                            double currentBasalValue = affectedHistoricElements.get(
                                    affectedHistoricElements.size() - 1).getValue();
                            newBasalValue = currentBasalValue
                                    * basalItem.getValue() * 0.01;
                        }
                        // add item
                        basalEvents.add(new VaultEntry(
                                VaultEntryType.BASAL_MANUAL,
                                startTimestamp,
                                newBasalValue));

                        // add the changes of basal rate within the tmp percentage timespan
                        if (basalItem.getValue() > 0) {
                            for (int i = 0; i < affectedHistoricElements.size() - 2; i++) {
                                double currentBasalValue = affectedHistoricElements.get(i)
                                        .getValue();
                                newBasalValue = currentBasalValue
                                        * basalItem.getValue() * 0.01;
                                basalEvents.add(new VaultEntry(
                                        VaultEntryType.BASAL_MANUAL,
                                        affectedHistoricElements.get(i).getTimestamp(),
                                        newBasalValue));
                            }
                        }

                        // restore rate from jungest profile event afterwords
                        if (affectedHistoricElements.size() > 0) {
                            basalEvents.add(new VaultEntry(
                                    VaultEntryType.BASAL_PROFILE,
                                    basalItem.getTimestamp(),
                                    affectedHistoricElements.get(0).getValue()));
                        }
                        break;

                    case BASAL_TMP_RATE:
                        // add new rate
                        basalEvents.add(new VaultEntry(
                                VaultEntryType.BASAL_MANUAL,
                                new Date((long) (basalItem.getTimestamp().getTime()
                                        - basalItem.getDuration())),
                                basalItem.getValue()));

                        // restore rate from jungest profile event afterwords
                        if (affectedHistoricElements.size() > 0) {
                            basalEvents.add(new VaultEntry(
                                    VaultEntryType.BASAL_PROFILE,
                                    basalItem.getTimestamp(),
                                    affectedHistoricElements.get(0).getValue()));
                        }
                        break;
                    default:
                        Logger.getLogger(this.getClass().getName()).severe("ASSERTION ERROR!");
                        throw new AssertionError();
                }

                killedBasalEvents.add(item);
            } else if (item.getType() == VaultEntryType.BASAL_PROFILE) {
                historicBasalProfileEvents.add(item);
            }
        }

        data.removeAll(killedBasalEvents);
        data.addAll(basalEvents);
        return data;
    }


    /**
     * //TODO javadoc
     *
     * @param data
     * @return
     */
    private List<VaultEntry> addCgmValueToCgmAlertOnMedtronicPumps(final List<VaultEntry> data) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        double lastCgmValue = -1;
        for (VaultEntry item : data) {
            switch (item.getType()) {
                case GLUCOSE_CGM:
                    lastCgmValue = item.getValue();
                    break;
                case GLUCOSE_CGM_ALERT:
                    if (lastCgmValue > 0) {
                        item.setValue(lastCgmValue);
                    } else {
                        item.setValue(100);
                        LOG.log(Level.WARNING, "No CGM Value available for Alert: {0}", item.toString());
                    }
                    break;
                default:
                    break;

            }
        }

        return data;
    }


    /**
     * //TODO javadoc
     *
     * @param data
     * @return
     */
    private List<VaultEntry> calculateCgmElevation(final List<VaultEntry> data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        //TODO add filter size to config
        SlidingWindow sw = new SlidingWindow(30, VaultEntryType.GLUCOSE_CGM, 5.0);
        List<VaultEntry> elevationItems = new ArrayList<>();

        for (VaultEntry item : data) {
            double elevation = sw.updateValue1WindowElevation(item);
            if (Math.abs(elevation) > 0.0) {
                elevationItems.add(new VaultEntry(
                        VaultEntryType.GLUCOSE_ELEVATION_30,
                        item.getTimestamp(),
                        elevation));
            }
        }
        data.addAll(elevationItems);

        return data;
    }

    /**
     * adds {@link this#fillCanulaAsNewKatheder} and {@link this#fillCanulaCooldown}
     * {@inheritDoc}
     */
    @Override
    public boolean loadConfiguration(Properties configuration) {
        if (!super.loadConfiguration(configuration)) return false;
        if (!configuration.containsKey("fillCanulaAsNewKatheder") || !configuration.containsKey("fillCanulaCooldown")) return false;
        fillCanulaAsNewKatheder = Boolean.parseBoolean(configuration.getProperty("fillCanulaAsNewKatheder"));
        fillCanulaCooldown = Integer.parseInt(configuration.getProperty("fillCanulaCooldown"));
        return true;
    }
}

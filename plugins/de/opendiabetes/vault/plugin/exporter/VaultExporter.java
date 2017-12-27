package de.opendiabetes.vault.plugin.exporter;

import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryAnnotation;
import de.opendiabetes.vault.container.csv.ExportEntry;
import de.opendiabetes.vault.container.csv.VaultCsvEntry;
import de.opendiabetes.vault.data.VaultDao;
import de.opendiabetes.vault.plugin.util.EasyFormatter;
import de.opendiabetes.vault.plugin.util.TimestampUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements functionality shared by the Exporters exporting from the Vault database.
 */
public abstract class VaultExporter extends CSVFileExporter {
    /**
     * Buffer for the entries before they get exported.
     */
    private List<VaultEntry> delayBuffer = new ArrayList<>();
    /**
     * The {@link de.opendiabetes.vault.data.VaultDao} database to export from.
     */
    private VaultDao database;

    /**
     * Setter for the {@link VaultExporter#database}.
     *
     * @param database The database to be set.
     */
    private void setDatabase(final VaultDao database) {
        this.database = database;
    }

    /**
     * This variant of setAdditional sets the database used by the {@link VaultExporter}s.
     *
     * @param object The database to be set.
     * @throws IllegalArgumentException Thrown if the object passed is not an instance of {@link VaultDao}.
     */
    @Override
    public void setAdditional(final Object object) throws IllegalArgumentException {
        if (object instanceof VaultDao) {
            setDatabase((VaultDao) object);
        } else {
            throw new IllegalArgumentException("Wrong argument given, only objects of type VaultDao are applicable!");
        }
    }

    /**
     * Queries the data from the Vault database.
     *
     * @return The queried data.
     */
    protected List<VaultEntry> queryData() {
        List<VaultEntry> entries;

        try {
            // query entries
            if (this.getIsPeriodRestricted()) {
                entries = database.queryVaultEntrysBetween(this.getExportPeriodFrom(),
                        this.getExportPeriodTo());
            } else {
                entries = database.queryAllVaultEntrys();
            }

            return entries;
        } catch (NullPointerException exception) {
            LOG.log(Level.SEVERE, "No database to query data from!" + exception);
            return null;
        }
    }

    /**
     * Prepares data queried from the database for export.
     *
     * @param data The data to be prepared.
     * @return The entries ready for export.
     */
    @Override
    protected List<ExportEntry> prepareData(final List<VaultEntry> data) {
        // Status update constants
        final int queryDataProgress = 20;
        final int startPrepareProgress = 40;
        final int prepareDoneProgress = 60;

        List<ExportEntry> returnValues = new ArrayList<>();

        this.notifyStatus(queryDataProgress, "Querying data");

        List<VaultEntry> tmpValues = queryData();
        if (tmpValues == null || tmpValues.isEmpty()) {
            return null;
        }

        // list is ordered by timestamp from database (or should be ordered otherwise)
        Date fromTimestamp = tmpValues.get(0).getTimestamp();
        Date toTimestamp = tmpValues.get(tmpValues.size() - 1).getTimestamp();

        this.notifyStatus(startPrepareProgress, "Preparing data for export");

        if (!tmpValues.isEmpty()) {
            int i = 0;
            delayBuffer = new ArrayList<>();
            while (!fromTimestamp.after(toTimestamp)) {
                // start new time slot (1m slots)
                VaultCsvEntry tmpCsvEntry = new VaultCsvEntry();
                tmpCsvEntry.setTimestamp(fromTimestamp);

                // add delayed items
                if (!delayBuffer.isEmpty()) {
                    // need to copy the buffer since the loop may create new entries
                    VaultEntry[] delayedEntries = delayBuffer.toArray(new VaultEntry[]{});
                    delayBuffer.clear();
                    for (VaultEntry delayedItem : delayedEntries) {
                        tmpCsvEntry = processVaultEntry(tmpCsvEntry, delayedItem);

                    }
                }

                // search and add vault entries for this time slot
                VaultEntry tmpEntry = tmpValues.get(i);
                while (fromTimestamp.equals(tmpEntry.getTimestamp())) {
                    if (i < tmpValues.size() - 1) {
                        i++;
                    } else {
                        i--;
                        break;
                    }
                    tmpCsvEntry = processVaultEntry(tmpCsvEntry, tmpEntry);
                    tmpEntry = tmpValues.get(i);
                }

                // save entry if not empty
                if (!tmpCsvEntry.isEmpty()) {
                    returnValues.add(tmpCsvEntry);
                    LOG.log(Level.FINE, "Export entry: {0}", tmpCsvEntry.toCsvString());
                }

                // add 1 minute to timestamp for next time slot
                fromTimestamp = TimestampUtils.addMinutesToTimestamp(fromTimestamp, 1);
            }
        }

        this.notifyStatus(prepareDoneProgress, "Preparation of data successful");

        return returnValues;
    }

    /**
     * Processes the VaultEntries retrieved by {@link #queryData()}.
     *
     * @param csvEntry The {@link VaultCsvEntry} to process the data to.
     * @param entry The {@link VaultEntry} to process.
     * @return The processed {@link VaultEntry}.
     */
    private VaultCsvEntry processVaultEntry(final VaultCsvEntry csvEntry, final VaultEntry entry) {
//            VaultCsvEntry tmpCsvEntry = vaultCsvEntry;
        switch (entry.getType()) {
            case GLUCOSE_CGM_ALERT:
                csvEntry.setCgmAlertValue(entry.getValue());
                break;
            case GLUCOSE_CGM:
                // TODO why does this happen
                // --> when more than one cgm value per minute is available
                // but cgm ticks are every 15 minutes ...
                if (csvEntry.getCgmValue()
                        == VaultCsvEntry.UNINITIALIZED_DOUBLE) {
                    csvEntry.setCgmValue(entry.getValue());
                } else {
                    LOG.log(Level.WARNING,
                            "Found multiple CGM Value for timepoint {0}, Drop {1}, hold: {2}",
                            new Object[]{entry.getTimestamp().toString(),
                                    entry.toString(),
                                    csvEntry.toString()});
                }
                break;
            case GLUCOSE_CGM_CALIBRATION:
                csvEntry.addGlucoseAnnotation(entry.getType().toString()
                        + "="
                        + EasyFormatter.formatDouble(entry.getValue()));
                break;
            case GLUCOSE_CGM_RAW:
                csvEntry.setCgmRawValue(entry.getValue());
                break;
            case GLUCOSE_BG:
            case GLUCOSE_BG_MANUAL:
                // TODO why does this happen ?
                // it often happens with identical values, but db has been cleaned before ...
                if (csvEntry.getBgValue()
                        == VaultCsvEntry.UNINITIALIZED_DOUBLE) {
                    csvEntry.setBgValue(entry.getValue());
                    csvEntry.addGlucoseAnnotation(entry.getType().toString());
                } else {
                    LOG.log(Level.WARNING,
                            "Found multiple BG Value for timepoint {0}, Drop {1}, hold: {2}",
                            new Object[]{entry.getTimestamp().toString(),
                                    entry.toString(),
                                    csvEntry.toString()});
                }
                break;
            case GLUCOSE_BOLUS_CALCULATION:
                csvEntry.setBolusCalculationValue(entry.getValue());
                break;
            case GLUCOSE_ELEVATION_30:
                csvEntry.addGlucoseAnnotation(entry.getType().toString()
                        + "="
                        + EasyFormatter.formatDouble(entry.getValue()));
                break;
            case CGM_CALIBRATION_ERROR:
            case CGM_SENSOR_FINISHED:
            case CGM_SENSOR_START:
            case CGM_CONNECTION_ERROR:
            case CGM_TIME_SYNC:
                csvEntry.addGlucoseAnnotation(entry.getType().toString());
                break;
            case BASAL_MANUAL:
            case BASAL_PROFILE:
            case BASAL_INTERPRETER:
                csvEntry.setBasalValue(entry.getValue());
                csvEntry.addBasalAnnotation(entry.getType().toString());
                break;
            case BOLUS_SQUARE:
                if (csvEntry.getBolusValue() != VaultCsvEntry.UNINITIALIZED_DOUBLE) {
                    // delay entry if bolus is already set for this time slot
                    delayBuffer.add(entry);
                    LOG.log(Level.INFO, "Delayed bolus entry: {0}", entry.toString());
                    break;
                }
                csvEntry.setBolusValue(entry.getValue());
                csvEntry.addBolusAnnotation(
                        entry.getType().toString()
                                + "="
                                + EasyFormatter.formatDouble(entry.getValue2()));
                break;
            case BOLUS_NORMAL:
                if (csvEntry.getBolusValue() != VaultCsvEntry.UNINITIALIZED_DOUBLE) {
                    // delay entry if bolus is already set for this time slot
                    delayBuffer.add(entry);
                    LOG.log(Level.INFO, "Delayed bolus entry: {0}", entry.toString());
                    break;
                }
                csvEntry.setBolusValue(entry.getValue());
                csvEntry.addBolusAnnotation(
                        entry.getType().toString());
                break;
            case MEAL_BOLUS_CALCULATOR:
            case MEAL_MANUAL:
                csvEntry.setMealValue(entry.getValue());
                break;
            case EXERCISE_BICYCLE:
            case EXERCISE_WALK:
            case EXERCISE_RUN:
            case EXERCISE_MANUAL:
            case EXERCISE_OTHER:
                csvEntry.setExerciseTimeValue(entry.getValue());
                csvEntry.addExerciseAnnotation(entry.getType().toString());
                for (VaultEntryAnnotation item : entry.getAnnotations()) {
                    csvEntry.addExerciseAnnotation(item.toStringWithValue());
                }
                break;
            case PUMP_FILL:
            case PUMP_FILL_INTERPRETER:
            case PUMP_NO_DELIVERY:
            case PUMP_REWIND:
            case PUMP_UNTRACKED_ERROR:
            case PUMP_SUSPEND:
            case PUMP_UNSUSPEND:
            case PUMP_AUTONOMOUS_SUSPEND:
            case PUMP_RESERVOIR_EMPTY:
            case PUMP_TIME_SYNC:
                csvEntry.addPumpAnnotation(entry.getType().toString());
                break;
            case PUMP_PRIME:
                csvEntry.addPumpAnnotation(entry.getType().toString()
                        + "="
                        + EasyFormatter.formatDouble(entry.getValue()));
                break;
            case PUMP_CGM_PREDICTION:
                csvEntry.setPumpCgmPredictionValue(entry.getValue());
                break;
            case HEART_RATE:
                csvEntry.setHeartRateValue(entry.getValue());
                break;
            case HEART_RATE_VARIABILITY:
                csvEntry.setHeartRateVariabilityValue(entry.getValue());
                csvEntry.setStressBalanceValue(entry.getValue2());
                break;
            case STRESS:
                csvEntry.setStressValue(entry.getValue());
                break;
            case LOC_FOOD:
            case LOC_HOME:
            case LOC_OTHER:
            case LOC_SPORTS:
            case LOC_TRANSITION:
            case LOC_WORK:
                csvEntry.addLocationAnnotation(entry.getType().toString());
                break;
            case SLEEP_DEEP:
            case SLEEP_LIGHT:
            case SLEEP_REM:
                csvEntry.addSleepAnnotation(entry.getType().toString());
                csvEntry.setSleepValue(entry.getValue());
                break;
            case ML_CGM_PREDICTION:
                csvEntry.setMlCgmValue(entry.getValue());
                break;
            case DM_INSULIN_SENSITIVITY:
                csvEntry.setInsulinSensitivityFactor(entry.getValue());
                break;
            case OTHER_ANNOTATION:
                // will be handled by annotations
                break;
            default:
                LOG.severe("TYPE ASSERTION ERROR!");
                throw new AssertionError();
        }

        if (!entry.getAnnotations().isEmpty()) {
            for (VaultEntryAnnotation annotation : entry.getAnnotations()) {
                switch (annotation.getType()) {
                    case GLUCOSE_BG_METER_SERIAL:
                    case GLUCOSE_RISE_20_MIN:
                    case GLUCOSE_RISE_LAST:
                        csvEntry.addGlucoseAnnotation(annotation.toStringWithValue());
                        break;
                    case EXERCISE_GoogleBicycle:
                    case EXERCISE_GoogleRun:
                    case EXERCISE_GoogleWalk:
                    case EXERCISE_TrackerBicycle:
                    case EXERCISE_TrackerRun:
                    case EXERCISE_TrackerWalk:
                    case EXERCISE_AUTOMATIC_OTHER:
                        csvEntry.addExerciseAnnotation(annotation.toStringWithValue());
                        break;
                    case ML_PREDICTION_TIME_BUCKET_SIZE:
                        csvEntry.addMlAnnotation(annotation.toStringWithValue());
                        break;
                    case PUMP_ERROR_CODE:
                    case PUMP_INFORMATION_CODE:
                        csvEntry.addPumpAnnotation(annotation.toStringWithValue());
                        break;
                    case CGM_VENDOR_DEXCOM:
                    case CGM_VENDOR_LIBRE:
                    case CGM_VENDOR_MEDTRONIC:
                        csvEntry.addGlucoseAnnotation(annotation.toStringWithValue());
                        break;
                    case USER_TEXT:
                        csvEntry.addOtherAnnotation(annotation.toStringWithValue());
                    default:
                        LOG.severe("ANNOTATION ASSERTION ERROR!");
                        throw new AssertionError();
                }
            }
        }
        return csvEntry;
    }
}

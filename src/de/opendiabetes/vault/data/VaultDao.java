/*
 * Copyright (C) 2017 OpenDiabetes
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.opendiabetes.vault.data;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.HsqldbDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.logger.Log;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.opendiabetes.vault.container.RawEntry;
import de.opendiabetes.vault.container.VaultEntry;
import de.opendiabetes.vault.container.VaultEntryType;
import de.opendiabetes.vault.plugin.util.TimestampUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class implements an interface to the database used.
 * It does so by using the Database Access Objects defined in {@link Dao}.
 *
 * @author Jens Heuschkel
 * @author Lucas Buschlinger
 */
public final class VaultDao {

    /**
     * Error code indicating that an error occurred.
     */
    public static final long RESULT_ERROR = -1;
    /**
     * The link to the database used.
     */
    private static final String DATABASE_URL = "jdbc:hsqldb:mem:odvault";
    //private static final String DATABASE_URL = "jdbc:hsqldb:file:./test.db";
    /**
     * The {@link Logger} used by this Vault Database Access Object.
     */
    private static final Logger LOG = Logger.getLogger(VaultDao.class.getName());
    /**
     * The instance of the Database Access Object used herein.
     */
    private static VaultDao INSTANCE = null;

    /**
     * The {@link ConnectionSource} used herein.
     */
    private ConnectionSource connectionSource;
    /**
     * The Database Access Object ({@link Dao}) for data in the form of {@link VaultEntry} used herein.
     */
    private Dao<VaultEntry, Long> vaultDao;
    /**
     * The Database Access Object ({@link Dao}) for data in the form of {@link RawEntry} used herein.
     */
    private Dao<RawEntry, Long> rawDao;

    /**
     * Default constructor for the Vault Database Access Object.
     * It is declared private as {@link #initializeDb()} has to be used to set up the database access.
     */
    private VaultDao() {
    }

    /**
     * Getter for the {@link #INSTANCE} of the database.
     * If the database has not been initialized by calling {@link #initializeDb()} this will terminate the execution.
     *
     * @return The instance of the Vault Database Access Object, provided it has been initialized by {@link #initializeDb()}.
     */
    public static VaultDao getInstance() {
        if (INSTANCE == null) {
            LOG.severe("Database is not initialized. Call VaultDao.initializeDb first!");
            System.exit(-1);
        }
        return INSTANCE;
    }

    /**
     * Closes the connection to the database and thus finalizes the Vault Database Access Object {@link #INSTANCE}.
     *
     * @throws IOException Thrown if the connection to the database can not be closed.
     */
    public static void finalizeDb() throws IOException {
        INSTANCE.connectionSource.close();
    }

    /**
     * Initializes the connection to the database and sets up the Vault Database Access Object {@link #INSTANCE}.
     *
     * @throws SQLException Thrown if the database can not be successfully initialized.
     */
    public static void initializeDb() throws SQLException {
        //TODO combine logging
        System.setProperty(LoggerFactory.LOG_TYPE_SYSTEM_PROPERTY,
                LoggerFactory.LogType.LOCAL.toString());
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY,
                Log.Level.INFO.toString());

        INSTANCE = new VaultDao();
        INSTANCE.initDb();
    }

    /**
     * Initializes the actual database as a {@link JdbcConnectionSource}.
     *
     * @throws SQLException Thrown if the database can not be successfully initialized.
     */
    private void initDb() throws SQLException {
        // create a connection source to our database
        connectionSource = new JdbcConnectionSource(DATABASE_URL, "sa", "",
                new HsqldbDatabaseType());
        // instantiate the DAO
        vaultDao = DaoManager.createDao(connectionSource, VaultEntry.class);
        if (!vaultDao.isTableExists()) {
            TableUtils.createTableIfNotExists(connectionSource, VaultEntry.class);
        } else {
            LOG.warning("Found existing DB for VaultEntries. Reusing it!!");
        }

        rawDao = DaoManager.createDao(connectionSource, RawEntry.class);
        if (!rawDao.isTableExists()) {
            TableUtils.createTableIfNotExists(connectionSource, RawEntry.class);
        }
//        TableUtils.createTableIfNotExists(connectionSource, SliceEntry.class);
    }

    /**
     * Puts {@link VaultEntry}s into the database.
     *
     * @param entry The {@link VaultEntry} to be put into the database.
     * @return The ID of respective entry or {@link #RESULT_ERROR}.
     */
    public long putEntry(final VaultEntry entry) {
        try {
            return vaultDao.createIfNotExists(entry).getId();
        } catch (SQLException exception) {
            LOG.log(Level.SEVERE, "Error saving entry:\n" + entry.toString(), exception);
            return RESULT_ERROR;
        }
    }

    /**
     * Puts {@link RawEntry}s into the database.
     *
     * @param entry The {@link RawEntry} to be put into the database.
     * @return The ID of the respective entry or {@link #RESULT_ERROR}.
     */
    public long putRawEntry(final RawEntry entry) {
        // TODO rethink raw entry tracking
        return 0;
//        try {
//            return rawDao.createIfNotExists(entry).getId();
//        } catch (SQLException exception) {
//            LOG.log(Level.SEVERE, "Error saving entry:\n" + entry.toString(), exception);
//            return RESULT_ERROR;
//        }
    }

    /**
     * Searches the database for duplicate entries and removes them accordingly.
     *
     * @return True if no duplicate entries were found or all duplicate entries were successfully removed from the database.
     *          False if a duplicate entry could not be removed.
     */
    public boolean removeDuplicates() {
        // DELETE FROM MyTable WHERE RowId NOT IN (SELECT MIN(RowId) FROM MyTable GROUP BY Col1, Col2, Col3);
        // but we need a workaround for the or mapper
        try {
            PreparedQuery<VaultEntry> query
                    = vaultDao.queryBuilder().orderBy("timestamp", true)
                    .prepare();
            CloseableIterator<VaultEntry> iterator = vaultDao.iterator(query);

            Date startGenerationTimestamp = null;
            List<VaultEntry> tmpList = new ArrayList<>();
            List<Long> duplicateId = new ArrayList<>();
            while (iterator.hasNext()) {
                VaultEntry entry = iterator.next();
                if (startGenerationTimestamp == null) {
                    // start up
                    startGenerationTimestamp = entry.getTimestamp();
                    tmpList.add(entry);
                } else if (!startGenerationTimestamp
                        .equals(entry.getTimestamp())) {
                    // not same timestamp --> new line generation
                    startGenerationTimestamp = entry.getTimestamp();
                    tmpList.clear();
                    tmpList.add(entry);
                } else {
                    // same timestamp --> check if it is a duplicate
                    for (VaultEntry item : tmpList) {
                        if (item.equals(entry)) {
                            // duplicate --> delete and move on
                            duplicateId.add(entry.getId());
                            break;
                        }
                    }
                }
            }

            // delete duplicates
            int lines = vaultDao.deleteIds(duplicateId);
            LOG.log(Level.INFO, "Removed {0} duplicates", lines);
        } catch (SQLException exception) {
            LOG.log(Level.SEVERE, "Error while db query", exception);
            return false;
        }

        return true;
    }

    /**
     * This method is used to query {@link VaultEntry}s which are of a given type and lie in a specified period.
     * The types to be queried for are glucose types:
     * <ul>
     *     <li>{@link VaultEntryType#GLUCOSE_BG}</li>
     *     <li>{@link VaultEntryType#GLUCOSE_CGM}</li>
     *     <li>{@link VaultEntryType#GLUCOSE_CGM_ALERT}</li>
     * </ul>
     *
     * @param from The start of the period to query entries from.
     * @param to The end of the period to query entries from.
     * @return All {@link VaultEntry} which are of the required type and lie in the specified period.
     */
    //TODO OTHER TYPES? Let's ask Jens @next meeting
    public List<VaultEntry> queryGlucoseBetween(final Date from, final Date to) {
        List<VaultEntry> returnValues = null;
        try {
            PreparedQuery<VaultEntry> query
                    = vaultDao.queryBuilder().orderBy("timestamp", true)
                    .where()
                    .eq(VaultEntry.TYPE_FIELD_NAME, VaultEntryType.GLUCOSE_BG)
                    .or()
                    .eq(VaultEntry.TYPE_FIELD_NAME, VaultEntryType.GLUCOSE_CGM)
                    .or()
                    .eq(VaultEntry.TYPE_FIELD_NAME, VaultEntryType.GLUCOSE_CGM_ALERT)
                    .and()
                    .between(VaultEntry.TIMESTAMP_FIELD_NAME, from, to)
                    .prepare();
            returnValues = vaultDao.query(query);
        } catch (SQLException exception) {
            LOG.log(Level.SEVERE, "Error while db query", exception);
        }
        return returnValues;
    }

    /**
     * This method is used to query {@link VaultEntry}s which are of a given type and lie in a specified period.
     * The types to be queried for are exercise types:
     * <ul>
     *     <li>{@link VaultEntryType#EXERCISE_BICYCLE}</li>
     *     <li>{@link VaultEntryType#EXERCISE_RUN}</li>
     *     <li>{@link VaultEntryType#EXERCISE_WALK}</li>
     *     <li>{@link VaultEntryType#EXERCISE_MANUAL}</li>
     * </ul>
     *
     * @param from The start of the period to query entries from.
     * @param to The end of the period to query entries from.
     * @return All {@link VaultEntry} which are of the required type and lie in the specified period.
     */
    //TODO OTHER TYPES? Let's ask Jens @next meeting
    public List<VaultEntry> queryExerciseBetween(final Date from, final Date to) {
        List<VaultEntry> returnValues = null;
        try {
            PreparedQuery<VaultEntry> query
                    = vaultDao.queryBuilder().orderBy("timestamp", true)
                    .where()
                    .eq(VaultEntry.TYPE_FIELD_NAME, VaultEntryType.EXERCISE_BICYCLE)
                    .or()
                    .eq(VaultEntry.TYPE_FIELD_NAME, VaultEntryType.EXERCISE_RUN)
                    .or()
                    .eq(VaultEntry.TYPE_FIELD_NAME, VaultEntryType.EXERCISE_WALK)
                    .or()
                    .eq(VaultEntry.TYPE_FIELD_NAME, VaultEntryType.EXERCISE_MANUAL)
                    .and()
                    .between(VaultEntry.TIMESTAMP_FIELD_NAME, from, to)
                    .prepare();
            returnValues = vaultDao.query(query);
        } catch (SQLException exception) {
            LOG.log(Level.SEVERE, "Error while db query", exception);
        }
        return returnValues;
    }

    /**
     * This is used to query a single Event ({@link VaultEntry} before a given point in time.
     * This is useful to determine why certain events might have happened (correlation of events).
     *
     * @param timestamp The point in time to get the preceding event from.
     * @param type The Type of {@link VaultEntry} to query for.
     * @return The event of the {@link VaultEntryType} preceding the specified point in time.
     */
    public VaultEntry queryLatestEventBefore(final Date timestamp, final VaultEntryType type) {
        VaultEntry returnValue = null;
        try {

            PreparedQuery<VaultEntry> query
                    = vaultDao.queryBuilder().orderBy("timestamp", false)
                    .limit(1L)
                    .where()
                    .eq(VaultEntry.TYPE_FIELD_NAME, type)
                    .and()
                    .le(VaultEntry.TIMESTAMP_FIELD_NAME, timestamp)
                    .prepare();
            List<VaultEntry> tmpList = vaultDao.query(query);
            if (tmpList.size() > 0) {
                returnValue = tmpList.get(0);
            }
        } catch (SQLException exception) {
            LOG.log(Level.SEVERE, "Error while db query", exception);
        }
        return returnValue;
    }

    /**
     * This is used to query all {@link VaultEntry}s currently store in the database.
     *
     * @return The full list of all {@link VaultEntry}s in the database.
     */
    public List<VaultEntry> queryAllVaultEntries() {
        List<VaultEntry> returnValues = new ArrayList<>();
        try {

            PreparedQuery<VaultEntry> query
                    = vaultDao.queryBuilder().orderBy("timestamp", true)
                    .prepare();
            returnValues = vaultDao.query(query);
        } catch (SQLException exception) {
            LOG.log(Level.SEVERE, "Error while db query", exception);
        }
        return returnValues;
    }

    /**
     * This is used to retrieve an entry from the database by its identifier.
     *
     * @param id The ID of the {@link VaultEntry} to be retrieved.
     * @return The {@link VaultEntry} with respective ID or null.
     */
    public VaultEntry queryVaultEntryById(final long id) {
        List<VaultEntry> returnValues;
        try {
            PreparedQuery<VaultEntry> query
                    = vaultDao.queryBuilder().orderBy("timestamp", true)
                    .limit(1L)
                    .where()
                    .eq(VaultEntry.ID_FIELD_NAME, id)
                    .prepare();
            returnValues = vaultDao.query(query);
        } catch (SQLException exception) {
            LOG.log(Level.SEVERE, "Error while db query", exception);
            return null;
        }
        return returnValues.get(0);
    }

    /**
     * This is used to retrieve all {@link VaultEntry}s from the database which lie in the specified period of time, no matter their type.
     *
     * @param from The start of the time period to query entries from.
     * @param to The end of the time period to query entries from.
     * @return List of all {@link VaultEntry}s which lie in the specified time period.
     */
    public List<VaultEntry> queryVaultEntriesBetween(final Date from, final Date to) {
        List<VaultEntry> returnValues = new ArrayList<>();
        try {
            Date fromTimestamp = TimestampUtils.createCleanTimestamp(from);
            Date toTimestamp = TimestampUtils.createCleanTimestamp(to);

            PreparedQuery<VaultEntry> query
                    = vaultDao.queryBuilder().orderBy("timestamp", true)
                    .where()
                    .between(VaultEntry.TIMESTAMP_FIELD_NAME, fromTimestamp, toTimestamp)
                    .prepare();
            returnValues = vaultDao.query(query);
        } catch (SQLException exception) {
            LOG.log(Level.SEVERE, "Error while db query", exception);
        }
        return returnValues;
    }
    /**
     * This method is used to query {@link VaultEntry}s which are of a given type and lie in a specified period.
     * The types to be queried for are basal types:
     * <ul>
     *     <li>{@link VaultEntryType#BASAL_MANUAL}</li>
     *     <li>{@link VaultEntryType#BASAL_PROFILE}</li>
     *     <li>{@link VaultEntryType#BASAL_INTERPRETER}</li>
     * </ul>
     *
     * @param from The start of the period to query entries from.
     * @param to The end of the period to query entries from.
     * @return All {@link VaultEntry} which are of the required type and lie in the specified period.
     */
    public List<VaultEntry> queryBasalBetween(final Date from, final Date to) {
        List<VaultEntry> returnValues = null;
        try {
            PreparedQuery<VaultEntry> query
                    = vaultDao.queryBuilder().orderBy("timestamp", true)
                    .where()
                    .eq(VaultEntry.TYPE_FIELD_NAME, VaultEntryType.BASAL_MANUAL)
                    .or()
                    .eq(VaultEntry.TYPE_FIELD_NAME, VaultEntryType.BASAL_PROFILE)
                    .or()
                    .eq(VaultEntry.TYPE_FIELD_NAME, VaultEntryType.BASAL_INTERPRETER)
                    .and()
                    .between(VaultEntry.TIMESTAMP_FIELD_NAME, from, to)
                    .prepare();
            returnValues = vaultDao.query(query);
        } catch (SQLException exception) {
            LOG.log(Level.SEVERE, "Error while db query", exception);
        }
        return returnValues;
    }

    /**
     * This is used to remove a specific entry from the database.
     *
     * @param historyEntry The {@link VaultEntry} to be removed from the database.
     * @return True if the entry was removed, false if not.
     */
    public boolean removeEntry(final VaultEntry historyEntry) {
        try {
            vaultDao.deleteById(historyEntry.getId());
            LOG.log(Level.INFO, "Removed dntry: {0}", historyEntry.toString());
        } catch (SQLException exception) {
            LOG.log(Level.SEVERE, "Error while db query", exception);
            return false;
        }

        return true;
    }
}

/**
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
package de.opendiabetes.vault.container;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class defines a raw data entry.
 */
@DatabaseTable(tableName = "RawValues")
public class RawEntry {

    // For the QueryBuilder to be able to find the fields.
    /**
     * Name of the field holding the timestamp.
     */
    public static final String TIMESTAMP_FIELD_NAME = "timestamp";
    /**
     * Name of the field holding the value.
     */
    public static final String VALUE_FIELD_NAME = "value";
    /**
     * Name of the field holding a flag whether the data is interpreted.
     */
    public static final String IS_INTERPRETED_FIELD_NAME = "isInterpreted";

    /**
     * The ID of the RawEntry.
     */
    @DatabaseField(generatedId = true)
    private long id;

    /**
     * The value of the RawEntry.
     */
    @DatabaseField(columnName = VALUE_FIELD_NAME, canBeNull = false, dataType = DataType.LONG_STRING)
    private String value;

    /**
     * Indicates whether the RawEntry is interpreted or not.
     */
    @DatabaseField(columnName = IS_INTERPRETED_FIELD_NAME, canBeNull = false)
    private boolean interpreted;

    /**
     * Default constructor.
     */
    public RawEntry() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    /**
     * Constructor for RawEntry, setting the value and whether the RawEntry is interpreted.
     *
     * @param value       The value that value will be set to.
     * @param interpreted The value that interpreted will be set to.
     */
    public RawEntry(final String value, final boolean interpreted) {
        this.value = value.trim();
        this.interpreted = interpreted;
    }

    /**
     * Getter for ID.
     *
     * @return The ID of the RawEntry.
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for ID.
     *
     * @param id The value that ID will be set to.
     */
    public void setId(final long id) {
        this.id = id;
    }

    /**
     * Returns whether the RawEntry is interpreted.
     *
     * @return True if RawEntry is interpreted, false otherwise.
     */
    public boolean isInterpreted() {
        return interpreted;
    }

}

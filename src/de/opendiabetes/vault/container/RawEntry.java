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
package de.opendiabetes.vault.container;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class defines a raw data entry
 * @author mswin
 */
@DatabaseTable(tableName = "RawValues")
public class RawEntry {

    // for QueryBuilder to be able to find the fields
    public static final String TIMESTAMP_FIELD_NAME = "timestamp";
    public static final String VALUE_FIELD_NAME = "value";
    public static final String IS_INTERPRETED_FIELD_NAME = "isInterpreted";

    /**
     * The id of the RawEntry
     */
    @DatabaseField(generatedId = true)
    private long id;

    /**
     * The value of the RawEntry
     */
    @DatabaseField(columnName = VALUE_FIELD_NAME, canBeNull = false, dataType = DataType.LONG_STRING)
    private String value;

    /**
     * Indicates whether the RawEntry is interpreted or not
     */
    @DatabaseField(columnName = IS_INTERPRETED_FIELD_NAME, canBeNull = false)
    private boolean interpreted;

    public RawEntry() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    /**
     * Constructor for RawEntry, setting the value and whether the RawEntry is interpreted
     * @param value The value that value will be set to
     * @param interpreted The value that interpreted will be set to
     */
    public RawEntry(String value, boolean interpreted) {
        this.value = value.trim();
        this.interpreted = interpreted;
    }

    /**
     * Setter for id
     * @param id The value that id will be set to
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter for id
     * @return The id of the RawEntry
     */
    public long getId() {
        return id;
    }

    /**
     * Returns whether the RawEntry is interpreted
     * @return true if RawEntry is interpreted, false otherwise
     */
    public boolean isInterpreted() {
        return interpreted;
    }

}

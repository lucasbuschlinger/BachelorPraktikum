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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * This class defines a vault entry
 *
 * @author mswin
 */
@DatabaseTable(tableName = "VaultEntries")
public class VaultEntry implements Serializable {

    public static final double VALUE_UNUSED = -5.0;
    public static final long ID_UNUSED = -5L;
    // for QueryBuilder to be able to find the fields
    public static final String ID_FIELD_NAME = "id";
    public static final String TYPE_FIELD_NAME = "type";
    public static final String TIMESTAMP_FIELD_NAME = "timestamp";
    public static final String VALUE_FIELD_NAME = "value";
    public static final String VALUE2_FIELD_NAME = "value2";
    public static final String RAW_ID_FIELD_NAME = "rawId";
    public static final String ANNOTATION_FIELD_NAME = "annotation";
    private final Gson gson;
    /**
     * The id of the VaultEntry
     */
    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private long id;

    /**
     * The VaultEntryType of the VaultEntry
     */
    @DatabaseField(columnName = TYPE_FIELD_NAME, canBeNull = false, dataType = DataType.ENUM_INTEGER)
    private VaultEntryType type;

    /**
     * The timestamp of the VaultEntry
     */
    @DatabaseField(columnName = TIMESTAMP_FIELD_NAME, canBeNull = false)
    private Date timestamp;

    /**
     * The first value of the VaultEntry
     */
    @DatabaseField(columnName = VALUE_FIELD_NAME, canBeNull = false)
    private double value;

    /**
     * The second value of the VaultEntry
     */
    @DatabaseField(columnName = VALUE2_FIELD_NAME, canBeNull = false)
    private double value2 = VALUE_UNUSED;

    /**
     * The rawId of the VaultEntry
     */
    @DatabaseField(columnName = RAW_ID_FIELD_NAME, canBeNull = false)
    private long rawId = ID_UNUSED;

    /**
     * The VaultEntry's List of VaultEntryAnnotation
     */
    private List<VaultEntryAnnotation> annotations = new ArrayList<>();

    /**
     * The VaultEntry's annotations as json
     */
    @DatabaseField(dataType = DataType.LONG_STRING, columnName = ANNOTATION_FIELD_NAME)
    private String annotationsAsJson = "";

    /**
     * The no-argument constructor of VaultEntry
     */
    public VaultEntry() {
        // all persisted classes must define a no-arg constructor with at least package visibility
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(VaultEntryAnnotation.class, new VaultEntryAnnotationGsonAdapter());
        gson = gb.create();
    }

    /**
     * A constructor of VaultEntry, setting the type, timestamp and value of the VaultEntry
     *
     * @param type      The parameter that type will be set to
     * @param timestamp The parameter that timestamp will be set to
     * @param value     The parameter that value will be set to
     */
    public VaultEntry(VaultEntryType type, Date timestamp, double value) {
        this();
        this.type = type;
        this.timestamp = timestamp;
        this.value = value;
    }

    /**
     * A constructor of VaultEntry, setting the type and timestamp of the VaultEntry as well as the default for value
     *
     * @param type      The parameter that type will be set to
     * @param timestamp The parameter that timestamp will be set to
     */
    public VaultEntry(VaultEntryType type, Date timestamp) {
        this();
        this.type = type;
        this.timestamp = timestamp;
        this.value = VALUE_UNUSED;
    }

    /**
     * A constructor of VaultEntry, setting the type, timestamp, value and value2 of the VaultEntry
     *
     * @param type      The parameter that type will be set to
     * @param timestamp The parameter that timestamp will be set to
     * @param value     The parameter that value will be set to
     * @param value2    The parameter that value2 will be set to
     */
    public VaultEntry(VaultEntryType type, Date timestamp, double value, double value2) {
        this(type, timestamp, value);
        this.value2 = value2;
    }

    /**
     * A constructor of VaultEntry, setting the type, timestamp, value and annotations of the VaultEntry
     *
     * @param type        The parameter that type will be set to
     * @param timestamp   The parameter that timestamp will be set to
     * @param value       The parameter that value will be set to
     * @param annotations The parameter that annotations will be set to
     */
    public VaultEntry(VaultEntryType type, Date timestamp, double value, List<VaultEntryAnnotation> annotations) {
        this(type, timestamp, value);
        this.annotations = annotations;
    }

    /**
     * A constructor of VaultEntry, setting the type, timestamp, value, value2 and annotations of the VaultEntry
     *
     * @param type        The parameter that type will be set to
     * @param timestamp   The parameter that timestamp will be set to
     * @param value       The parameter that value will be set to
     * @param value2      The parameter that value2 will be set to
     * @param annotations The parameter that annotations will be set to
     */
    public VaultEntry(VaultEntryType type, Date timestamp, double value, double value2, List<VaultEntryAnnotation> annotations) {
        this(type, timestamp, value);
        this.value2 = value2;
        this.annotations = annotations;
    }

    /**
     * A constructor of VaultEntry, copying all values of the VaultEntry passed as argument
     *
     * @param copy The VaultEntry whose fields will be copied
     */
    public VaultEntry(VaultEntry copy) {
        this();
        this.type = copy.type;
        this.timestamp = copy.timestamp;
        this.value = copy.value;
        this.value2 = copy.value2;
        this.annotations = copy.annotations;
    }

    /**
     * Getter for id
     *
     * @return the id of the VaultEntry
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for type
     *
     * @return The type of the VaultEntry
     */
    public VaultEntryType getType() {
        return type;
    }

    /**
     * Setter for type
     *
     * @param type The value that type will be set to
     */
    public void setType(VaultEntryType type) {
        this.type = type;
    }

    /**
     * Getter for timestamp
     *
     * @return The timestamp of the VaultEntry
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for value
     *
     * @return The value of the VaultEntry
     */
    public double getValue() {
        return value;
    }


    /**
     * Setter for value
     *
     * @param value The parameter that value will be set to
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Getter for rawId
     *
     * @return The rawId of the VaultEntry
     */
    public long getRawId() {
        return rawId;
    }

    /**
     * Setter for rawId
     *
     * @param rawId The value that rawId will be set to
     */
    public void setRawId(long rawId) {
        this.rawId = rawId;
    }

    /**
     * Getter for value2
     *
     * @return value2 of the VaultEntry
     */
    public double getValue2() {
        return value2;
    }

    /**
     * Setter for value2
     *
     * @param value2 The parameter that value2 will be set to
     */
    public void setValue2(double value2) {
        this.value2 = value2;
    }

    /**
     * Getter for annotations
     *
     * @return annotations of the VaultEntry
     */
    public List<VaultEntryAnnotation> getAnnotations() {
        annotationsFromJason();
        return annotations;
    }

    /**
     * Adds an annotation to the current list of annotations
     *
     * @param annotation The annotation to be added
     */
    public void addAnnotation(VaultEntryAnnotation annotation) {
        this.annotations.add(annotation);
        annotationsToJson();
    }

    /**
     * Sets the VaultEntry's list of annotations
     *
     * @param annotations The list of annotations to be used
     */
    public void setAnnotation(ArrayList<VaultEntryAnnotation> annotations) {
        this.annotations = annotations;
        annotationsToJson();
    }

    /**
     * Getter for annotationsAsJson
     *
     * @return The VaultEntry's annotationsAsJson
     */
    public String getAnnotationsAsJson() {
        return annotationsAsJson;
    }

    // TODO JavaDoc for methods below?
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.type);
        hash = 67 * hash + Objects.hashCode(this.timestamp);
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.value2) ^ (Double.doubleToLongBits(this.value2) >>> 32));
        hash = 67 * hash + (int) (this.rawId ^ (this.rawId >>> 32));
        hash = 67 * hash + Objects.hashCode(this.annotations);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VaultEntry other = (VaultEntry) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.timestamp, other.timestamp)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.value2, other.value2)) {
            return false;
        }
        return Objects.equals(this.annotations, other.annotations);
    }

    @Override
    public String toString() {
        return "VaultEntry{" + "id=" + id + ", type=" + type + ", timestamp=" + timestamp + ", value=" + value + ", value2=" + value2 + ", rawId=" + rawId + ", annotaion=" + annotations + '}';
    }

    /**
     * Converts the List of VaultEntryAnnotation to json and saves it in annotationsAsJson
     */
    private void annotationsToJson() {
        annotationsAsJson = gson.toJson(annotations);
    }

    /**
     * Converts the annotations from json to List of VaultEntryAnnotation
     */
    private void annotationsFromJason() {
        if (!annotationsAsJson.isEmpty()) {
            annotations = gson.fromJson(annotationsAsJson,
                    new TypeToken<List<VaultEntryAnnotation>>() {
                    }.getType());
        }
        annotationsAsJson = "";
    }

    /**
     * Sets annotationsAsJson and converts the annotations from json to List of VaultEntryAnnotation
     *
     * @param asString
     */

    // fix typo?
    public void setAnnotaionFromJson(String asString) {
        if (asString != null && !asString.isEmpty()) {
            this.annotationsAsJson = asString;
            annotationsFromJason();
        }
    }

}

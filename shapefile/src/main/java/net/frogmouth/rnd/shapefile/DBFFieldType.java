package net.frogmouth.rnd.shapefile;

import java.io.IOException;

/**
 * DBF Field Type.
 *
 * <p>This is used for the field type (conceptually equal to column type in a database table).
 *
 * <p>The encoding is an ASCII letter, which is treated as a byte value.
 */
public enum DBFFieldType {
    /**
     * Character.
     *
     * <p>This is used for string-type values.
     */
    Character(0x43, 'C', "Character"),
    /** Date. */
    Date(0x44, 'D', "Date"),
    /** Float */
    Float(0x46, 'F', "Float"),
    /**
     * Numeric.
     *
     * <p>This is typically integer.
     */
    Numeric(0x4E, 'N', "Numeric");

    private final byte encodedValue;
    private final char shortCode;
    private final String name;

    private DBFFieldType(int encodedValue, char shortCode, String name) {
        this.encodedValue = (byte) encodedValue;
        this.shortCode = shortCode;
        this.name = name;
    }

    /**
     * Get the encoded value for this enumeration.
     *
     * @return the encoded value as a byte.
     */
    public byte getEncodedValue() {
        return encodedValue;
    }

    /**
     * Get the short code for this enumeration value.
     *
     * <p>This is a single character. Note that this isn't what you want for encoding purposes - see
     * {@link getEncodedValue()}.
     *
     * @return the short code for this value
     */
    public char getShortCode() {
        return shortCode;
    }

    /**
     * Get the name for this enumeration value.
     *
     * @return the name for this value
     */
    public String getName() {
        return name;
    }

    /**
     * Field Type lookup.
     *
     * @param encoded the encoded byte value.
     * @return the corresponding enumeration value
     * @throws IOException if the value was not valid
     */
    public static DBFFieldType lookupType(byte encoded) throws IOException {
        for (DBFFieldType type : DBFFieldType.values()) {
            if (type.getEncodedValue() == encoded) {
                return type;
            }
        }
        throw new IOException(String.format("Cannot parse 0x%02x as a field type", encoded));
    }
}

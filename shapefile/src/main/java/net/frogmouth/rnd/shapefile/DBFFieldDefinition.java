package net.frogmouth.rnd.shapefile;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Definition for a single column in the DBF.
 *
 * <p>This corresponds to the "Field Subrecords Structure" in the DBF header.
 */
public record DBFFieldDefinition(
        String fieldName, DBFFieldType fieldType, int fieldLengthBinary, int fieldDecimalCount) {

    static DBFFieldDefinition fromDataInputStream(DataInputStream dis) throws IOException {
        byte[] fieldNameBytes = new byte[11];
        dis.read(fieldNameBytes);
        String fieldName = new String(fieldNameBytes, StandardCharsets.US_ASCII).trim();
        DBFFieldType fieldType = DBFFieldType.lookupType(dis.readByte());
        dis.skipBytes(16 - 12);
        int fieldLengthBinary = dis.readByte() & 0xFF;
        int fieldDecimalCount = dis.readByte() & 0xFF;
        dis.skipBytes(32 - 18);
        return new DBFFieldDefinition(fieldName, fieldType, fieldLengthBinary, fieldDecimalCount);
    }

    @Override
    public String toString() {
        return String.format(
                "%s (%c) - %d, %d",
                fieldName, fieldType.getShortCode(), fieldLengthBinary, fieldDecimalCount);
    }
}

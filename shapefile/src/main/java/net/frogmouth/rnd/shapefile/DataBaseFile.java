package net.frogmouth.rnd.shapefile;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Base File (DBF) part of the shapefile.
 *
 * <p>This holds the attributes for each geometry.
 */
public class DataBaseFile {

    private LocalDate lastUpdate;
    private final List<DBFFieldDefinition> fieldDefinitions = new ArrayList<>();

    /** Constructor. */
    public DataBaseFile() {}

    /**
     * Create DataBaseFile from a file.
     *
     * @param file the file to read from
     * @return the corresponding DataBaseFile.
     * @throws FileNotFoundException if the file could not be opened
     * @throws IOException if the file is not valid
     */
    public static DataBaseFile fromFile(File file) throws FileNotFoundException, IOException {
        return fromInputStream(new FileInputStream(file));
    }

    /**
     * Create DataBaseFile from an input stream.
     *
     * @param inputStream the input stream to read from
     * @return the corresponding DataBaseFile.
     * @throws IOException if the file is not valid
     */
    public static DataBaseFile fromInputStream(InputStream inputStream) throws IOException {
        DataBaseFile dbf = new DataBaseFile();
        try (DataInputStream dis = new DataInputStream(inputStream)) {
            byte dbfFileType = dis.readByte();
            System.out.println(String.format("dbfFileType: 0x%02x", dbfFileType));
            int lastUpdateYear = 1900 + (dis.readByte() & 0xFF);
            byte lastUpdateMonth = dis.readByte();
            byte lastUpdateDay = dis.readByte();
            dbf.setLastUpdate(LocalDate.of(lastUpdateYear, lastUpdateMonth, lastUpdateDay));
            System.out.println("Last update: " + dbf.getLastUpdate().toString());
            long numRecords = readUint32LittleEndian(dis);
            System.out.println(String.format("numRecords: 0x%08x (%d)", numRecords, numRecords));
            int firstDataRecordOffset = readUint16LittleEndian(dis);
            System.out.println(
                    String.format(
                            "firstDataRecordOffset: 0x%04x (%d)",
                            firstDataRecordOffset, firstDataRecordOffset));
            int singleRecordLength = readUint16LittleEndian(dis);
            System.out.println(
                    String.format(
                            "singleRecordLength: 0x%04x (%d)",
                            singleRecordLength, singleRecordLength));
            dis.skipNBytes(28 - 12);
            byte tableFlags = dis.readByte();
            System.out.println(String.format("tableFlags: 0x%02x", tableFlags));
            byte codePageMark = dis.readByte();
            System.out.println(String.format("codePageMark: 0x%02x", codePageMark));

            dis.skipNBytes(32 - 30);
            int fieldSubrecordBytes = firstDataRecordOffset - 33;
            int numFieldSubrecords = fieldSubrecordBytes / 32;
            for (int i = 0; i < numFieldSubrecords; i++) {
                DBFFieldDefinition fieldDefinition = DBFFieldDefinition.fromDataInputStream(dis);
                dbf.addFieldDefinition(fieldDefinition);
                System.out.println("field definition: " + fieldDefinition.toString());
            }
            System.out.println(String.format("End code: 0x%02x", dis.readByte()));
            for (int j = 0; j < numRecords; j++) {
                byte deletedFlag = dis.readByte();
                System.out.println(String.format("Deleted flag: 0x%02x", deletedFlag));
                for (DBFFieldDefinition fieldDefinition : dbf.fieldDefinitions) {
                    byte[] bytes = new byte[fieldDefinition.fieldLengthBinary()];
                    dis.read(bytes);
                    System.out.println(
                            fieldDefinition.fieldName()
                                    + ":"
                                    + new String(bytes, StandardCharsets.US_ASCII));
                }
            }
            System.out.println("Available: " + dis.available());
        }
        return dbf;
    }

    private static long readUint32LittleEndian(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[Integer.BYTES];
        dis.read(bytes);
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return (bb.getInt() & 0xFFFFFFFFL);
    }

    private static int readUint16LittleEndian(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[Short.BYTES];
        dis.read(bytes);
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return (bb.getShort() & 0xFFFF);
    }

    /**
     * Add a field definition to this DBF.
     *
     * @param fieldDefinition the field definition to add
     */
    public void addFieldDefinition(DBFFieldDefinition fieldDefinition) {
        fieldDefinitions.add(fieldDefinition);
    }

    /**
     * Get the field definitions for this DBF.
     *
     * @return the field definitions as a List.
     */
    public List<DBFFieldDefinition> getFieldDefinitions() {
        return new ArrayList<>(this.fieldDefinitions);
    }

    /**
     * Get the last update to the database file.
     *
     * <p>The update is only tracked to the resolution of a day, and does not have an explicit time
     * zone.
     *
     * @return the last update as a date.
     */
    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Set the last update to the database file.
     *
     * <p>The update is only tracked to the resolution of a day, and does not have an explicit time
     * zone.
     *
     * @param lastUpdate the last update as a date.
     */
    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}

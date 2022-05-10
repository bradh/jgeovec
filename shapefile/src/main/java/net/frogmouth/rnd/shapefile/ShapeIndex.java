package net.frogmouth.rnd.shapefile;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Shape Index file.
 *
 * <p>This is used to look up geometry data.
 */
public class ShapeIndex {

    private FileHeader fileHeader;
    private List<IndexRecord> indexRecords = new ArrayList<>();

    /** Constructor. */
    public ShapeIndex() {}

    /**
     * Create a ShapeIndex instance from a file.
     *
     * @param file the file to read from
     * @return the corresponding ShapeIndex
     * @throws FileNotFoundException if the file could not be found
     * @throws IOException if parsing fails.
     */
    public static ShapeIndex fromFile(File file) throws FileNotFoundException, IOException {
        return fromInputStream(new FileInputStream(file));
    }

    /**
     * Create a ShapeIndex instance from an input stream.
     *
     * @param inputStream the input stream to read from
     * @return the corresponding ShapeIndex
     * @throws IOException if parsing fails.
     */
    public static ShapeIndex fromInputStream(InputStream inputStream) throws IOException {
        ShapeIndex shapeIndex = new ShapeIndex();
        try (DataInputStream dis = new DataInputStream(inputStream)) {
            shapeIndex.setFileHeader(FileHeader.fromDataInputStream(dis));
            int bytesRemaining = shapeIndex.getFileHeader().getFileLength() - FileHeader.BYTES;
            while (bytesRemaining > 0) {
                int offset = dis.readInt();
                bytesRemaining -= Integer.BYTES;
                int contentLength = dis.readInt() * Short.BYTES;
                bytesRemaining -= Integer.BYTES;
                IndexRecord indexRecord = new IndexRecord(offset, contentLength);
                shapeIndex.addIndexRecord(indexRecord);
            }
        }
        return shapeIndex;
    }

    /**
     * Get the file header.
     *
     * @return the file header for this ShapeIndex.
     */
    public FileHeader getFileHeader() {
        return fileHeader;
    }

    /**
     * Set the file header.
     *
     * @param fileHeader the file header for this ShapeIndex.
     */
    public void setFileHeader(FileHeader fileHeader) {
        this.fileHeader = fileHeader;
    }

    /**
     * Get the records.
     *
     * @return the records for this ShapeIndex.
     */
    public List<IndexRecord> getIndexRecords() {
        return new ArrayList<>(indexRecords);
    }

    /**
     * Add an index record.
     *
     * @param indexRecord the index record to add.
     */
    public void addIndexRecord(IndexRecord indexRecord) {
        indexRecords.add(indexRecord);
    }
}

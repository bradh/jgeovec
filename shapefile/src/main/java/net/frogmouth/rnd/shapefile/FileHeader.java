package net.frogmouth.rnd.shapefile;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Shapefile / Shape Index file header.
 *
 * <p>This structure is common to both of those files.
 */
public class FileHeader {

    private static final int SHP_FILE_CODE = 9994;
    private static final int UNUSED_COUNT = 5;
    private static final int NUM_BOUNDING_BOX_VALUES = 8;
    /**
     * FileHeader size in bytes.
     *
     * <p>This is counted from the start of the file.
     */
    static int BYTES = 100;

    private int fileLength;
    private int version;
    private ShapeType shapeType;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private Double minZ;
    private Double maxZ;
    private Double minM;
    private Double maxM;

    /** Constructor. */
    public FileHeader() {}
    ;

    /**
     * Parse a file header from a data input stream.
     *
     * @param dis the data input stream to read the header values from
     * @return the corresponding FileHeader.
     * @throws IOException if parsing fails.
     */
    public static FileHeader fromDataInputStream(final DataInputStream dis) throws IOException {
        FileHeader fh = new FileHeader();
        int fileCode = dis.readInt();
        if (fileCode != FileHeader.SHP_FILE_CODE) {
            throw new IOException(
                    String.format("Bad file code - probably not shapefile: 0x%08x", fileCode));
        }
        for (int i = 0; i < FileHeader.UNUSED_COUNT; i++) {
            dis.readInt();
        }
        int fileLength = dis.readInt() * Short.BYTES;
        fh.setFileLength(fileLength);
        int versionBigEndian = dis.readInt();
        fh.setVersion(Integer.reverseBytes(versionBigEndian));
        int shapeTypeBigEndian = dis.readInt();
        int shapeTypeLittleEndian = Integer.reverseBytes(shapeTypeBigEndian);
        ShapeType shapeType = ShapeType.lookupValue(shapeTypeLittleEndian);
        if (shapeType == null) {
            throw new IOException(
                    String.format(
                            "Unsupport geometry type, perhaps not shapefile: 0x%08x",
                            shapeTypeLittleEndian));
        }
        fh.setShapeType(shapeType);
        byte[] bboxValues = dis.readNBytes(FileHeader.NUM_BOUNDING_BOX_VALUES * Double.BYTES);
        ByteBuffer bbox = ByteBuffer.wrap(bboxValues).order(ByteOrder.LITTLE_ENDIAN);
        fh.setMinX(bbox.getDouble(0 * Double.BYTES));
        fh.setMinY(bbox.getDouble(1 * Double.BYTES));
        fh.setMaxX(bbox.getDouble(2 * Double.BYTES));
        fh.setMaxY(bbox.getDouble(3 * Double.BYTES));
        if (shapeType.hasZ()) {
            fh.setMinZ(bbox.getDouble(4 * Double.BYTES));
            fh.setMaxZ(bbox.getDouble(5 * Double.BYTES));
        }
        if (shapeType.hasM()) {
            fh.setMinM(bbox.getDouble(6 * Double.BYTES));
            fh.setMaxM(bbox.getDouble(7 * Double.BYTES));
        }
        return fh;
    }

    /**
     * Get the file length.
     *
     * @return the file length in bytes.
     */
    public int getFileLength() {
        return fileLength;
    }

    /**
     * Set the file length.
     *
     * @param length the file length in bytes.
     */
    public void setFileLength(int length) {
        this.fileLength = length;
    }

    /**
     * Get the version value for this file header.
     *
     * @return the version as an integer
     */
    public int getVersion() {
        return version;
    }

    /**
     * Set the version value for this file header.
     *
     * @param version the version as an integer.
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Get the shape type.
     *
     * <p>Each shapefile can only hold a single geometry type, which is identified by this value.
     *
     * @return the shape type as an enumerated value.
     */
    public ShapeType getShapeType() {
        return shapeType;
    }

    /**
     * Set the shape type.
     *
     * <p>Each shapefile can only hold a single geometry type, which is identified by this value.
     *
     * @param shapeType the shape type as an enumerated value.
     */
    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    /**
     * Get the minimum X value.
     *
     * <p>For a latitude / longitude coordinate reference system, this will be the minimum longitude
     * value.
     *
     * @return the minimum X value.
     */
    public double getMinX() {
        return minX;
    }

    /**
     * Set the minimum X value.
     *
     * <p>For a latitude / longitude coordinate reference system, this is the minimum longitude
     * value.
     *
     * @param minX the minimum X value.
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * Get the minimum Y value.
     *
     * <p>For a latitude / longitude coordinate reference system, this will be the minimum latitude
     * value.
     *
     * @return the minimum Y value.
     */
    public double getMinY() {
        return minY;
    }

    /**
     * Set the minimum Y value.
     *
     * <p>For a latitude / longitude coordinate reference system, this is the minimum latitude
     * value.
     *
     * @param minY the minimum Y value.
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * Get the maximum X value.
     *
     * <p>For a latitude / longitude coordinate reference system, this will be the maximum longitude
     * value.
     *
     * @return the maximum X value.
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * Set the maximum X value.
     *
     * <p>For a latitude / longitude coordinate reference system, this is the maximum longitude
     * value.
     *
     * @param maxX the maximum X value.
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     * Get the maximum Y value.
     *
     * <p>For a latitude / longitude coordinate reference system, this will be the maximum latitude
     * value.
     *
     * @return the maximum Y value.
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * Set the maximum Y value.
     *
     * <p>For a latitude / longitude coordinate reference system, this is the maximum latitude
     * value.
     *
     * @param maxY the maximum Y value.
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    /**
     * Get the minimum Z value.
     *
     * <p>This is the smallest elevation or height.
     *
     * @return the minimum Z value.
     */
    public Double getMinZ() {
        return minZ;
    }

    /**
     * Set the minimum Z value.
     *
     * <p>This is the smallest elevation or height.
     *
     * @param minZ the minimum Z value.
     */
    public void setMinZ(Double minZ) {
        this.minZ = minZ;
    }

    /**
     * Get the maximum Z value.
     *
     * <p>This is the greatest elevation or height.
     *
     * @return the maximum Z value.
     */
    public Double getMaxZ() {
        return maxZ;
    }

    /**
     * Set the maximum Z value.
     *
     * <p>This is the greatest elevation or height.
     *
     * @param maxZ the maximum Z value.
     */
    public void setMaxZ(Double maxZ) {
        this.maxZ = maxZ;
    }

    /**
     * Get the minimum M value.
     *
     * <p>This is the smallest measure value.
     *
     * @return the minimum M value.
     */
    public Double getMinM() {
        return minM;
    }

    /**
     * Set the minimum M value.
     *
     * <p>This is the smallest measure value.
     *
     * @param minM the minimum M value.
     */
    public void setMinM(Double minM) {
        this.minM = minM;
    }

    /**
     * Get the maximum M value.
     *
     * <p>This is the greatest measure value.
     *
     * @return the maximum M value.
     */
    public Double getMaxM() {
        return maxM;
    }

    /**
     * Set the maximum M value.
     *
     * <p>This is the greatest measure value.
     *
     * @param maxM the maximum M value.
     */
    public void setMaxM(Double maxM) {
        this.maxM = maxM;
    }
}

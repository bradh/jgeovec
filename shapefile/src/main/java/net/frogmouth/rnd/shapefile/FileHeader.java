package net.frogmouth.rnd.shapefile;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FileHeader {

    static final int SHP_FILE_CODE = 9994;
    static final int UNUSED_COUNT = 5;
    static final int NUM_BOUNDING_BOX_VALUES = 8;
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

    public int getFileLength() {
        return fileLength;
    }

    public void setFileLength(int length) {
        this.fileLength = length;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public Double getMinZ() {
        return minZ;
    }

    public void setMinZ(Double minZ) {
        this.minZ = minZ;
    }

    public Double getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(Double maxZ) {
        this.maxZ = maxZ;
    }

    public Double getMinM() {
        return minM;
    }

    public void setMinM(Double minM) {
        this.minM = minM;
    }

    public Double getMaxM() {
        return maxM;
    }

    public void setMaxM(Double maxM) {
        this.maxM = maxM;
    }
}

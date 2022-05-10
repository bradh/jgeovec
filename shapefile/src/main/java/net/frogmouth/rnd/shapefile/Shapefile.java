package net.frogmouth.rnd.shapefile;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import net.frogmouth.rnd.simplefeaturesaccess.LineString;
import net.frogmouth.rnd.simplefeaturesaccess.Point;

/**
 * Shapefile.
 *
 * <p>This models the .shp part of the shapefile set.
 */
public class Shapefile {

    private static final int NUM_VALUES_IN_POINT = 2;
    private static final int NUM_VALUES_IN_POINTM = 3;
    private static final int NUM_VALUES_IN_POINTZ = 3;
    private static final int NUM_VALUES_IN_POINTZM = 4;
    private static final int POLYLINE_HEADER_LEN = 4 * Double.BYTES + 2 * Integer.BYTES;
    private FileHeader fileHeader;

    /** Constructor. */
    public Shapefile() {}

    /**
     * Create a Shapefile instance from a file.
     *
     * @param file the file to read from
     * @return the corresponding Shapefile
     * @throws FileNotFoundException if the file could not be found
     * @throws IOException if parsing fails.
     */
    public static Shapefile fromFile(File file) throws FileNotFoundException, IOException {
        return fromInputStream(new FileInputStream(file));
    }

    /**
     * Create a Shapefile instance from an input stream.
     *
     * @param inputStream the input stream to read from
     * @return the corresponding Shapefile
     * @throws IOException if parsing fails.
     */
    public static Shapefile fromInputStream(InputStream inputStream) throws IOException {
        Shapefile shapefile = new Shapefile();
        try (DataInputStream dis = new DataInputStream(inputStream)) {
            shapefile.setFileHeader(FileHeader.fromDataInputStream(dis));
            int bytesRemaining = shapefile.getFileHeader().getFileLength() - FileHeader.BYTES;
            while (bytesRemaining > 0) {
                int recordNumber = dis.readInt();
                bytesRemaining -= Integer.BYTES;
                int contentLength = dis.readInt() * Short.BYTES;
                bytesRemaining -= Integer.BYTES;
                int recordShapeTypeBigEndian = dis.readInt();
                bytesRemaining -= Integer.BYTES;
                int recordShapeTypeLittleEndian = Integer.reverseBytes(recordShapeTypeBigEndian);
                ShapeType recordShapeType = ShapeType.lookupValue(recordShapeTypeLittleEndian);
                // we already read the 4 bytes for the Shape Type at the start of the record
                int recordBytesLen = contentLength - Integer.BYTES;
                switch (recordShapeType) {
                    case Point -> {
                        int numBytes = NUM_VALUES_IN_POINT * Double.BYTES;
                        byte[] recordBytes = dis.readNBytes(numBytes);
                        bytesRemaining -= numBytes;
                        ByteBuffer recordValues =
                                ByteBuffer.wrap(recordBytes).order(ByteOrder.LITTLE_ENDIAN);
                        double x = recordValues.getDouble(0);
                        double y = recordValues.getDouble(1 * Double.BYTES);
                        Point point = new Point(x, y);
                        System.out.println(point.toString());
                    }
                    case PointM -> {
                        int numBytes = NUM_VALUES_IN_POINTM * Double.BYTES;
                        byte[] recordBytes = dis.readNBytes(numBytes);
                        bytesRemaining -= numBytes;
                        ByteBuffer recordValues =
                                ByteBuffer.wrap(recordBytes).order(ByteOrder.LITTLE_ENDIAN);
                        double x = recordValues.getDouble(0);
                        double y = recordValues.getDouble(1 * Double.BYTES);
                        double m = recordValues.getDouble(2 * Double.BYTES);
                        Point point = Point.fromXYM(x, y, m);
                        System.out.println(point.toString());
                    }
                    case PointZ -> {
                        // This could be POINTZ, or POINTZM - the M is optional
                        int numBytes = NUM_VALUES_IN_POINTZ * Double.BYTES;
                        boolean hasM = false;
                        if (recordBytesLen == NUM_VALUES_IN_POINTZM * Double.BYTES) {
                            numBytes = NUM_VALUES_IN_POINTZM * Double.BYTES;
                            hasM = true;
                        }
                        byte[] recordBytes = dis.readNBytes(numBytes);
                        bytesRemaining -= numBytes;
                        ByteBuffer recordValues =
                                ByteBuffer.wrap(recordBytes).order(ByteOrder.LITTLE_ENDIAN);
                        double x = recordValues.getDouble(0);
                        double y = recordValues.getDouble(1 * Double.BYTES);
                        double z = recordValues.getDouble(2 * Double.BYTES);
                        if (hasM) {
                            double m = recordValues.getDouble(3 * Double.BYTES);
                            Point point = new Point(x, y, z, m);
                            System.out.println(point.toString());
                        } else {
                            Point point = Point.fromXYZ(x, y, z);
                            System.out.println(point.toString());
                        }
                    }
                    case PolyLine -> {
                        bytesRemaining = processPolyLine(dis, bytesRemaining);
                    }
                    case PolyLineM -> {
                        bytesRemaining = processPolyLineM(dis, bytesRemaining);
                    }
                    default -> {
                        dis.skipBytes(recordBytesLen);
                        bytesRemaining -= recordBytesLen;
                        System.out.println("Need to handle " + recordShapeType.toString());
                    }
                }
            }
        }
        return shapefile;
    }

    private static int processPolyLine(final DataInputStream dis, int bytesRemaining)
            throws UnsupportedOperationException, IOException {
        byte[] fixedBytes = dis.readNBytes(POLYLINE_HEADER_LEN);
        bytesRemaining -= POLYLINE_HEADER_LEN;
        ByteBuffer fixedBytesLE = ByteBuffer.wrap(fixedBytes).order(ByteOrder.LITTLE_ENDIAN);
        Box box = Box.fromByteBuffer(fixedBytesLE);
        int numParts = fixedBytesLE.getInt(Box.BYTES);
        int numPoints = fixedBytesLE.getInt(Box.BYTES + Integer.BYTES);
        System.out.println(box.toString());
        int variableBytesSize =
                numParts * Integer.BYTES + numPoints * NUM_VALUES_IN_POINT * Double.BYTES;
        byte[] variableBytes = dis.readNBytes(variableBytesSize);
        bytesRemaining -= variableBytesSize;
        ByteBuffer variableBytesLE = ByteBuffer.wrap(variableBytes).order(ByteOrder.LITTLE_ENDIAN);
        List<Integer> partOffsets = new ArrayList<>();
        List<Point> points = new ArrayList<>();
        int variableBytesOffset = 0;
        for (int partNumber = 0; partNumber < numParts; partNumber++) {
            partOffsets.add(variableBytesLE.getInt(variableBytesOffset));
            variableBytesOffset += Integer.BYTES;
        }
        for (int pointNumber = 0; pointNumber < numPoints; pointNumber++) {
            double x = variableBytesLE.getDouble(variableBytesOffset);
            variableBytesOffset += Double.BYTES;
            double y = variableBytesLE.getDouble(variableBytesOffset);
            variableBytesOffset += Double.BYTES;
            Point point = new Point(x, y);
            points.add(point);
        }
        if (partOffsets.size() == 1) {
            LineString lineString = new LineString(points);
            System.out.println(lineString.toString());
        } else if (partOffsets.size() > 1) {
            throw new UnsupportedOperationException("TODO: multilinestring");
            // TODO: Multilinestring case
        }
        return bytesRemaining;
    }

    private static int processPolyLineM(final DataInputStream dis, int bytesRemaining)
            throws UnsupportedOperationException, IOException {
        // TODO: this looks to be the same as processPolyLine
        byte[] fixedBytes = dis.readNBytes(POLYLINE_HEADER_LEN);
        bytesRemaining -= POLYLINE_HEADER_LEN;
        ByteBuffer fixedBytesLE = ByteBuffer.wrap(fixedBytes).order(ByteOrder.LITTLE_ENDIAN);
        Box box = Box.fromByteBuffer(fixedBytesLE);
        int numParts = fixedBytesLE.getInt(Box.BYTES);
        int numPoints = fixedBytesLE.getInt(Box.BYTES + Integer.BYTES);
        System.out.println(box.toString());
        int variableBytesSize =
                numParts * Integer.BYTES + numPoints * NUM_VALUES_IN_POINT * Double.BYTES;
        byte[] variableBytes = dis.readNBytes(variableBytesSize);
        bytesRemaining -= variableBytesSize;
        ByteBuffer variableBytesLE = ByteBuffer.wrap(variableBytes).order(ByteOrder.LITTLE_ENDIAN);
        List<Integer> partOffsets = new ArrayList<>();
        List<Point> basePoints = new ArrayList<>();
        int variableBytesOffset = 0;
        for (int partNumber = 0; partNumber < numParts; partNumber++) {
            partOffsets.add(variableBytesLE.getInt(variableBytesOffset));
            variableBytesOffset += Integer.BYTES;
        }
        for (int pointNumber = 0; pointNumber < numPoints; pointNumber++) {
            double x = variableBytesLE.getDouble(variableBytesOffset);
            variableBytesOffset += Double.BYTES;
            double y = variableBytesLE.getDouble(variableBytesOffset);
            variableBytesOffset += Double.BYTES;
            Point point = new Point(x, y);
            basePoints.add(point);
        }
        // M part starts here - everything above is common with PolyLine
        byte[] mBytes = dis.readNBytes((2 + numPoints) * Double.BYTES);
        bytesRemaining -= mBytes.length;
        ByteBuffer mRangeBytesLE = ByteBuffer.wrap(mBytes).order(ByteOrder.LITTLE_ENDIAN);
        // TODO: work out how to expose the M range.
        double minM = mRangeBytesLE.getDouble(0 * Double.BYTES);
        double maxM = mRangeBytesLE.getDouble(1 * Double.BYTES);
        List<Point> points = new ArrayList<>();
        for (int pointNumber = 0; pointNumber < numPoints; pointNumber++) {
            double m = variableBytesLE.getDouble((2 + pointNumber) * Double.BYTES);
            Point basePoint = basePoints.get(pointNumber);
            Point point = basePoint.withM(m);
            points.add(point);
        }
        if (partOffsets.size() == 1) {
            LineString lineString = new LineString(points);
            System.out.println(lineString.toString());
        } else if (partOffsets.size() > 1) {
            throw new UnsupportedOperationException("TODO: multilinestring");
            // TODO: Multilinestring case
        }
        return bytesRemaining;
    }

    /**
     * Get the file header.
     *
     * @return the file header for this Shapefile.
     */
    public FileHeader getFileHeader() {
        return fileHeader;
    }

    /**
     * Set the file header.
     *
     * @param fileHeader the file header for this Shapefile.
     */
    public void setFileHeader(FileHeader fileHeader) {
        this.fileHeader = fileHeader;
    }
}

package net.frogmouth.rnd.shapefile;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import net.frogmouth.rnd.simplefeaturesaccess.Point;

public class Shapefile {

    private static final int NUM_VALUES_IN_POINT = 2;
    private static final int NUM_VALUES_IN_POINTM = 3;
    private static final int NUM_VALUES_IN_POINTZ = 3;
    private static final int NUM_VALUES_IN_POINTZM = 4;
    private FileHeader fileHeader;

    public Shapefile() {}

    public static Shapefile fromFile(File file) throws FileNotFoundException, IOException {
        return fromInputStream(new FileInputStream(file));
    }

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

    public FileHeader getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(FileHeader fileHeader) {
        this.fileHeader = fileHeader;
    }
}

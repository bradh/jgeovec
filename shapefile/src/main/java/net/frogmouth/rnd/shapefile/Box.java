package net.frogmouth.rnd.shapefile;

import java.nio.ByteBuffer;

/**
 * (Bounding) Box record structure.
 *
 * <p>This is used within several different geometry types.
 */
public record Box(double minX, double minY, double maxX, double maxY) {

    public static final int BYTES = 4 * Double.BYTES;

    public static Box fromByteBuffer(final ByteBuffer bb, final int offset) {
        double x1 = bb.getDouble(0 * Double.BYTES + offset);
        double y1 = bb.getDouble(1 * Double.BYTES + offset);
        double x2 = bb.getDouble(2 * Double.BYTES + offset);
        double y2 = bb.getDouble(3 * Double.BYTES + offset);
        return new Box(x1, y1, x2, y2);
    }

    public static Box fromByteBuffer(final ByteBuffer bb) {
        return fromByteBuffer(bb, 0);
    }
}

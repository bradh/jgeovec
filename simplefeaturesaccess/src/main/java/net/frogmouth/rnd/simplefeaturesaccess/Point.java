package net.frogmouth.rnd.simplefeaturesaccess;

/**
 * Point.
 *
 * <p>A Point is a 0-dimensional geometric object and represents a single location in coordinate
 * space. A Point has an x-coordinate value, a y-coordinate value. If called for by the associated
 * Spatial Reference System, it may also have coordinate values for z and m.
 *
 * <p>The boundary of a Point is the empty set.
 */
public record Point(double x, double y, Double z, Double m) {

    public Point(double x, double y) {
        this(x, y, null, null);
    }

    public static Point fromXYZ(double x, double y, double z) {
        return new Point(x, y, z, null);
    }

    public static Point fromXYM(double x, double y, double m) {
        return new Point(x, y, null, m);
    }

    public boolean hasZ() {
        return z != null;
    }

    public boolean hasM() {
        return m != null;
    }

    @Override
    public String toString() {
        if (hasZ() && hasM()) {
            return String.format("POINT ZM(%f, %f, %f, %f)", x, y, z, m);
        } else if (hasZ()) {
            return String.format("POINT Z(%f, %f, %f)", x, y, z);
        } else if (hasM()) {
            return String.format("POINT M(%f, %f, %f)", x, y, m);
        } else {
            return String.format("POINT(%f, %f)", x, y);
        }
    }
}

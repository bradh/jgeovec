package net.frogmouth.rnd.shapefile;

/**
 * Enumeration of potential shape types.
 *
 * <p>Each shapefile contains exactly one type of value (e.g., its all points, or its all polygons,
 * never a mix of points and polygons).
 */
public enum ShapeType {
    NullShape(0, "Null Shape", false, false),
    Point(1, "Point", false, false),
    PolyLine(3, "PolyLine", false, false),
    Polygon(5, "Polygon", false, false),
    MultiPoint(8, "MultiPoint", false, false),
    PointZ(11, "PointZ", true, true),
    PolyLineZ(13, "PolyLineZ", true, true),
    PolygonZ(15, "PolygonZ", true, true),
    MultiPointZ(18, "MultiPointZ", true, true),
    PointM(21, "PointM", false, true),
    PolyLineM(23, "PolyLineM", false, true),
    PolygonM(25, "PolygonM", false, true),
    MultiPointM(28, "MultiPointM", false, true),
    MultiPatch(31, "MultiPatch", true, true);

    private final int value;
    private final String description;
    private final boolean hasZ;
    private final boolean hasM;

    private ShapeType(int value, String description, boolean hasZ, boolean hasM) {
        this.value = value;
        this.description = description;
        this.hasZ = hasZ;
        this.hasM = hasM;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasZ() {
        return hasZ;
    }

    public boolean hasM() {
        return hasM;
    }

    public static ShapeType lookupValue(int value) {
        for (ShapeType shapeType : ShapeType.values()) {
            if (value == shapeType.getValue()) {
                return shapeType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return description;
    }
}

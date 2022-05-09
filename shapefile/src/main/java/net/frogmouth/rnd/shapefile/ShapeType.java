package net.frogmouth.rnd.shapefile;

/**
 * Enumeration of potential shape types.
 *
 * <p>Each shapefile contains exactly one type of value (e.g., its all points, or its all polygons,
 * never a mix of points and polygons).
 */
public enum ShapeType {
    NullShape(0, "Null Shape", false, false),
    /** Point. */
    Point(1, "Point", false, false),
    /**
     * Polyline, or MultiPolyline.
     *
     * <p>LineString / MultiLineString is equivalent.
     */
    PolyLine(3, "PolyLine", false, false),
    /** Polygon, or MultiPolygon. */
    Polygon(5, "Polygon", false, false),
    /** Multiple Points. */
    MultiPoint(8, "MultiPoint", false, false),
    /**
     * Point with Z.
     *
     * <p>This can also contain an "M" part. M usually means some kind of measurement, not usually
     * altitude or height.
     *
     * <p>Z usually means altitude or height.
     */
    PointZ(11, "PointZ", true, true),
    /**
     * Polyline or MultiPolyline with Z.
     *
     * <p>LineString / MultiLineString is equivalent.
     *
     * <p>This can also contain an "M" part. M usually means some kind of measurement, not usually
     * altitude or height.
     *
     * <p>Z usually means altitude or height.
     */
    PolyLineZ(13, "PolyLineZ", true, true),
    /**
     * Polygon or MultiPolygon with Z.
     *
     * <p>This can also contain an "M" part. M usually means some kind of measurement, not usually
     * altitude or height.
     *
     * <p>Z usually means altitude or height.
     */
    PolygonZ(15, "PolygonZ", true, true),
    /**
     * MultiPoint with Z.
     *
     * <p>This can also contain an "M" part. M usually means some kind of measurement, not usually
     * altitude or height.
     *
     * <p>Z usually means altitude or height.
     */
    MultiPointZ(18, "MultiPointZ", true, true),
    /**
     * Point with M.
     *
     * <p>M usually means some kind of measurement, not usually altitude or height.
     */
    PointM(21, "PointM", false, true),
    /**
     * Polyline or MultiPolyline with M.
     *
     * <p>LineString / MultiLineString is equivalent.
     *
     * <p>M usually means some kind of measurement, not usually altitude or height
     */
    PolyLineM(23, "PolyLineM", false, true),
    /**
     * Polygon or MultiPolygon with M.
     *
     * <p>M usually means some kind of measurement, not usually altitude or height
     */
    PolygonM(25, "PolygonM", false, true),
    /**
     * MultiPoint with M.
     *
     * <p>M usually means some kind of measurement, not usually altitude or height.
     */
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

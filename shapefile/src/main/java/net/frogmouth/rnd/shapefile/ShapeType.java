package net.frogmouth.rnd.shapefile;

/**
 * Enumeration of potential shape types.
 *
 * <p>Each shapefile contains exactly one type of value (e.g., its all points, or its all polygons,
 * never a mix of points and polygons). As a special case, files can also contain null shapes.
 */
public enum ShapeType {
    /**
     * Null Shape.
     *
     * <p>A shape type of 0 indicates a null shape, with no geometric data for the shape. Each
     * feature type (point, line, polygon, etc.) supports nulls - it is valid to have points and
     * null points in the same shapefile. Often null shapes are place holders; they are used during
     * shapefile creation and are populated with geometric data soon after they are created.
     */
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
    /**
     * MultiPatch.
     *
     * <p>A MultiPatch consists of a number of surface patches. Each surface patch describes a
     * surface. The surface patches of a MultiPatch are referred to as its parts, and the type of
     * part controls how the order of vertices of an MultiPatch part is interpreted.
     */
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

    /**
     * Get the encoded value for this shape type.
     *
     * @return the encoded value as an integer.
     */
    public int getValue() {
        return value;
    }

    /**
     * Get a text description for this shape type.
     *
     * @return a short text description as a String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Whether this shape type has a Z value.
     *
     * <p>Z usually means altitude or height.
     *
     * @return true if the value has a Z value, otherwise false.
     */
    public boolean hasZ() {
        return hasZ;
    }

    /**
     * Whether this shape type potentially has an M value.
     *
     * <p>M usually means some kind of measurement, not usually altitude or height.
     *
     * <p>Values that support Z have M as optional. Each instance may or may not have valid M.
     *
     * @return true if the value has a Z value, otherwise false.
     */
    public boolean hasM() {
        return hasM;
    }

    /**
     * Look up a shape type from the encoded value.
     *
     * @param value the integer value to look up
     * @return the corresponding shape type, or null if not found.
     */
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

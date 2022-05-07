package net.frogmouth.rnd.simplefeaturesaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineString {
    private final List<Point> points;

    public LineString(List<Point> pointList) {
        points = new ArrayList<>(pointList);
    }

    public int getNumPoints() {
        return points.size();
    }

    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    public Point getPointN(int n) {
        return points.get(n);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (points.get(0).hasM() && points.get(0).hasZ()) {
            sb.append("LINESTRING ZM(");
        } else if (points.get(0).hasZ()) {
            sb.append("LINESTRING Z(");
        } else if (points.get(0).hasM()) {
            sb.append("LINESTRING M(");
        } else {
            sb.append("LINESTRING (");
        }
        sb.append(
                points.stream().map(Point::getStringCoordinates).collect(Collectors.joining(",")));
        sb.append(")");
        return sb.toString();
    }
}

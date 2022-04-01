package net.frogmouth.rnd.shapefile;

import static org.testng.Assert.*;

import java.io.IOException;
import org.testng.annotations.Test;

public class ShapeIndexTest {

    @Test
    public void checkPoint() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        ShapeIndex shapeIndex =
                ShapeIndex.fromInputStream(classloader.getResourceAsStream("simplepoint.shx"));
        FileHeader fileHeader = shapeIndex.getFileHeader();
        assertEquals(fileHeader.getVersion(), 1000);
        assertEquals(fileHeader.getShapeType(), ShapeType.Point);
        assertEquals(fileHeader.getMinX(), 150.473999, 0.000001);
        assertEquals(fileHeader.getMinY(), -23.376772, 0.000001);
        assertEquals(fileHeader.getMaxX(), 150.519745, 0.000001);
        assertEquals(fileHeader.getMaxY(), -23.319978, 0.000001);
        assertNull(fileHeader.getMinZ());
        assertNull(fileHeader.getMaxZ());
        assertNull(fileHeader.getMinM());
        assertNull(fileHeader.getMaxM());
        assertEquals(shapeIndex.getIndexRecords().size(), 2);
        assertEquals(shapeIndex.getIndexRecords().get(0).offset(), 50);
        assertEquals(shapeIndex.getIndexRecords().get(0).contentLength(), 20);
        assertEquals(shapeIndex.getIndexRecords().get(1).offset(), 64);
        assertEquals(shapeIndex.getIndexRecords().get(1).contentLength(), 20);
    }

    @Test
    public void checkPolygon() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        ShapeIndex shapeIndex =
                ShapeIndex.fromInputStream(classloader.getResourceAsStream("polygon.shx"));
        FileHeader fileHeader = shapeIndex.getFileHeader();
        assertEquals(fileHeader.getVersion(), 1000);
        assertEquals(fileHeader.getShapeType(), ShapeType.Polygon);
        assertEquals(fileHeader.getMinX(), 149.756131, 0.000001);
        assertEquals(fileHeader.getMinY(), -22.704336, 0.000001);
        assertEquals(fileHeader.getMaxX(), 151.032563, 0.000001);
        assertEquals(fileHeader.getMaxY(), -22.036113, 0.000001);
        assertNull(fileHeader.getMinZ());
        assertNull(fileHeader.getMaxZ());
        assertNull(fileHeader.getMinM());
        assertNull(fileHeader.getMaxM());
        assertEquals(shapeIndex.getIndexRecords().size(), 1);
        assertEquals(shapeIndex.getIndexRecords().get(0).offset(), 50);
        assertEquals(shapeIndex.getIndexRecords().get(0).contentLength(), 128);
    }
}

package net.frogmouth.rnd.shapefile;

import static org.testng.Assert.*;

import java.io.IOException;
import org.testng.annotations.Test;

public class ShapefileLinestringTest {

    @Test
    public void checkHeader() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        Shapefile shapefile =
                Shapefile.fromInputStream(classloader.getResourceAsStream("simplelinestring.shp"));
        FileHeader uut = shapefile.getFileHeader();
        assertEquals(uut.getVersion(), 1000);
        assertEquals(uut.getShapeType(), ShapeType.PolyLine);
        assertEquals(uut.getMinX(), 150.790347, 0.000001);
        assertEquals(uut.getMinY(), -23.183681, 0.000001);
        assertEquals(uut.getMaxX(), 150.938363, 0.000001);
        assertEquals(uut.getMaxY(), -23.158527, 0.000001);
        assertNull(uut.getMinZ());
        assertNull(uut.getMaxZ());
        assertNull(uut.getMinM());
        assertNull(uut.getMaxM());
        // TODO: check linestring result
    }
}

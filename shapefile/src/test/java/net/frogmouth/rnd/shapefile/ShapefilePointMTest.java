package net.frogmouth.rnd.shapefile;

import static org.testng.Assert.*;

import java.io.IOException;
import org.testng.annotations.Test;

public class ShapefilePointMTest {

    @Test
    public void checkHeader() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        Shapefile shapefile =
                Shapefile.fromInputStream(classloader.getResourceAsStream("pointm.shp"));
        FileHeader uut = shapefile.getFileHeader();
        assertEquals(uut.getVersion(), 1000);
        assertEquals(uut.getShapeType(), ShapeType.PointM);
        assertEquals(uut.getMinX(), 150.473999, 0.000001);
        assertEquals(uut.getMinY(), -23.376772, 0.000001);
        assertEquals(uut.getMaxX(), 150.519745, 0.000001);
        assertEquals(uut.getMaxY(), -23.319978, 0.000001);
        assertNull(uut.getMinZ());
        assertNull(uut.getMaxZ());
        assertEquals(uut.getMinM(), 0.0);
        assertEquals(uut.getMaxM(), 0.0);
    }
}

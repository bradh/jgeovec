package net.frogmouth.rnd.shapefile;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.List;
import org.testng.annotations.Test;

public class DataBaseFileTest {

    @Test
    public void checkParse() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        DataBaseFile dataBaseFile =
                DataBaseFile.fromInputStream(classloader.getResourceAsStream("simplepoint.dbf"));
        assertNotNull(dataBaseFile);
        List<DBFFieldDefinition> fieldDefs = dataBaseFile.getFieldDefinitions();
        assertNotNull(fieldDefs);
        assertEquals(fieldDefs.size(), 2);
        assertEquals(fieldDefs.get(0).fieldName(), "id");
        assertEquals(fieldDefs.get(1).fieldName(), "Name");
    }
}

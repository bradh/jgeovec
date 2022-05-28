package net.frogmouth.rnd.shapefile;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRow {

    private final List<DatabaseField> fields = new ArrayList<>();

    public void addField(DatabaseField field) {
        fields.add(field);
    }

    public List<DatabaseField> getFields() {
        return fields;
    }

    public String toString(List<DBFFieldDefinition> fieldDefinitions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            sb.append(fieldDefinitions.get(i).fieldName());
            sb.append(": ");
            sb.append(fields.get(i).toString());
            sb.append("| ");
        }
        return sb.toString();
    }
}

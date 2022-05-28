package net.frogmouth.rnd.shapefile;

public record DatabaseField(String value) {

    @Override
    public String toString() {
        return value;
    }
}

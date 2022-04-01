package net.frogmouth.rnd.shapefile;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ShapeIndex {

    private FileHeader fileHeader;
    private List<IndexRecord> indexRecords = new ArrayList<>();

    public ShapeIndex() {}

    public static ShapeIndex fromFile(File file) throws FileNotFoundException, IOException {
        return fromInputStream(new FileInputStream(file));
    }

    public static ShapeIndex fromInputStream(InputStream inputStream) throws IOException {
        ShapeIndex shapeIndex = new ShapeIndex();
        try (DataInputStream dis = new DataInputStream(inputStream)) {
            shapeIndex.setFileHeader(FileHeader.fromDataInputStream(dis));
            int bytesRemaining = shapeIndex.getFileHeader().getFileLength() - FileHeader.BYTES;
            while (bytesRemaining > 0) {
                int offset = dis.readInt();
                bytesRemaining -= Integer.BYTES;
                int contentLength = dis.readInt() * Short.BYTES;
                bytesRemaining -= Integer.BYTES;
                IndexRecord indexRecord = new IndexRecord(offset, contentLength);
                shapeIndex.addIndexRecord(indexRecord);
            }
        }
        return shapeIndex;
    }

    public FileHeader getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(FileHeader fileHeader) {
        this.fileHeader = fileHeader;
    }

    public List<IndexRecord> getIndexRecords() {
        return new ArrayList<>(indexRecords);
    }

    public void addIndexRecord(IndexRecord indexRecord) {
        indexRecords.add(indexRecord);
    }
}

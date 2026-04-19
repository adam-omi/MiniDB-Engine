import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MiniDBEngine {
    private String tableName;
    private ObjectMapper mapper;
    private List<Map<String, Object>> tableData;
    private File tableFile;

    public MiniDBEngine(String tableName) {
        this.tableName = tableName;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.tableFile = new File(tableName + ".json");
        this.tableData = new ArrayList<>();
        load();
    }

    public boolean exists() {
        return tableFile.exists();
    }

    private void load() {
        if (tableFile.exists()) {
            try {
                tableData = mapper.readValue(tableFile, new TypeReference<List<Map<String, Object>>>(){});
            } catch (IOException e) {
                System.out.println("Error reading table " + tableName);
            }
        }
    }

    public void save() {
        try {
            mapper.writeValue(tableFile, tableData);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getAll() {
        return tableData;
    }

    public void insert(Map<String, Object> row) {
        tableData.add(row);
        save();
        System.out.println("Query OK, 1 row inserted into " + tableName);
    }

    public void update(Map<String, Object> updates, String col, String val) {
        int count = 0;
        for (Map<String, Object> row : tableData) {
            if (String.valueOf(row.get(col)).equals(val)) {
                row.putAll(updates);
                count++;
            }
        }
        if (count > 0) save();
        System.out.println("Query OK, " + count + " rows updated.");
    }

    public void delete(String col, String val) {
        int initial = tableData.size();
        tableData.removeIf(row -> String.valueOf(row.get(col)).equals(val));
        if (tableData.size() < initial) save();
        System.out.println("Query OK, " + (initial - tableData.size()) + " rows deleted.");
    }
}
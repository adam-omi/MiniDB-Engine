import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLParser {

    // Regex Patterns
    private static final Pattern SELECT = Pattern.compile("(?i)SELECT \\* FROM (\\w+)(?: WHERE (\\w+)=['\"]?([^'\"]+)['\"]?)?");
    private static final Pattern INSERT = Pattern.compile("(?i)INSERT INTO (\\w+) \\((.+)\\) VALUES \\((.+)\\)");
    private static final Pattern UPDATE = Pattern.compile("(?i)UPDATE (\\w+) SET (.+) WHERE (\\w+)=['\"]?([^'\"]+)['\"]?");
    private static final Pattern DELETE = Pattern.compile("(?i)DELETE FROM (\\w+) WHERE (\\w+)=['\"]?([^'\"]+)['\"]?");
    private static final Pattern JOIN   = Pattern.compile("(?i)SELECT \\* FROM (\\w+) JOIN (\\w+) ON (\\w+)=(\\w+)");

    public void execute(String cmd) {
        // Safety Check 1: Empty input
        if (cmd == null || cmd.trim().isEmpty()) return;

        // Safety Check 2: Semicolon
        if (!cmd.trim().endsWith(";")) {
            System.out.println("Error: Missing semicolon at end of command.");
            return;
        }

        String cleanCmd = cmd.trim().substring(0, cmd.trim().length() - 1); // Remove ;

        if (JOIN.matcher(cleanCmd).find()) handleJoin(cleanCmd);
        else if (cleanCmd.toUpperCase().startsWith("SELECT")) handleSelect(cleanCmd);
        else if (cleanCmd.toUpperCase().startsWith("INSERT")) handleInsert(cleanCmd);
        else if (cleanCmd.toUpperCase().startsWith("UPDATE")) handleUpdate(cleanCmd);
        else if (cleanCmd.toUpperCase().startsWith("DELETE")) handleDelete(cleanCmd);
        else System.out.println("Error: Unknown command.");
    }

    private void handleInsert(String cmd) {
        Matcher m = INSERT.matcher(cmd);
        if (m.find()) {
            String table = m.group(1);
            String[] cols = m.group(2).split(",");
            String[] vals = m.group(3).split(",");

            // Safety Check 3: Mismatched columns/values
            if (cols.length != vals.length) {
                System.out.println("Error: Column count (" + cols.length + ") does not match Value count (" + vals.length + ")");
                return;
            }

            Map<String, Object> row = new LinkedHashMap<>();
            for (int i=0; i<cols.length; i++) {
                row.put(cols[i].trim(), vals[i].trim().replaceAll("^['\"]|['\"]$", ""));
            }
            new MiniDBEngine(table).insert(row);
        } else {
            System.out.println("Syntax Error in INSERT.");
        }
    }

    private void handleSelect(String cmd) {
        Matcher m = SELECT.matcher(cmd);
        if (m.find()) {
            String table = m.group(1);
            MiniDBEngine db = new MiniDBEngine(table);
            
            // Safety Check 4: Table existence
            if (!db.exists()) {
                System.out.println("Error: Table '" + table + "' does not exist.");
                return;
            }

            String wCol = m.group(2);
            String wVal = m.group(3);
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map<String, Object> row : db.getAll()) {
                if (wCol == null || String.valueOf(row.get(wCol)).equals(wVal)) {
                    result.add(row);
                }
            }
            print(result);
        } else {
            System.out.println("Syntax Error in SELECT.");
        }
    }

    private void handleUpdate(String cmd) {
        Matcher m = UPDATE.matcher(cmd);
        if (m.find()) {
            String table = m.group(1);
            MiniDBEngine db = new MiniDBEngine(table);
            
            if (!db.exists()) {
                System.out.println("Error: Table '" + table + "' not found.");
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            for (String pair : m.group(2).split(",")) {
                String[] parts = pair.split("=");
                updates.put(parts[0].trim(), parts[1].trim().replaceAll("^['\"]|['\"]$", ""));
            }
            db.update(updates, m.group(3), m.group(4));
        } else {
            System.out.println("Syntax Error in UPDATE.");
        }
    }

    private void handleDelete(String cmd) {
        Matcher m = DELETE.matcher(cmd);
        if (m.find()) {
            MiniDBEngine db = new MiniDBEngine(m.group(1));
            if (!db.exists()) {
                 System.out.println("Error: Table not found.");
                 return;
            }
            db.delete(m.group(2), m.group(3));
        } else {
            System.out.println("Syntax Error in DELETE.");
        }
    }

    private void handleJoin(String cmd) {
        Matcher m = JOIN.matcher(cmd);
        if (m.find()) {
            String t1 = m.group(1);
            String t2 = m.group(2);
            
            MiniDBEngine db1 = new MiniDBEngine(t1);
            MiniDBEngine db2 = new MiniDBEngine(t2);

            if (!db1.exists() || !db2.exists()) {
                System.out.println("Error: One or both tables do not exist.");
                return;
            }

            List<Map<String, Object>> res = new ArrayList<>();
            for (Map<String, Object> r1 : db1.getAll()) {
                for (Map<String, Object> r2 : db2.getAll()) {
                    String v1 = String.valueOf(r1.get(m.group(3))); // Col 1
                    String v2 = String.valueOf(r2.get(m.group(4))); // Col 2
                    if (v1.equals(v2)) {
                        Map<String, Object> merge = new LinkedHashMap<>(r1);
                        merge.putAll(r2);
                        res.add(merge);
                    }
                }
            }
            print(res);
        }
    }

    private void print(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) { System.out.println("Empty set."); return; }
        System.out.println("--------------------------------");
        System.out.println(rows.get(0).keySet());
        System.out.println("--------------------------------");
        for (Map<String, Object> r : rows) System.out.println(r.values());
        System.out.println("--------------------------------");
    }
}
import java.util.Scanner;

public class MiniDBClient {
    public static void main(String[] args) {
        // Initialize parser without arguments
        SQLParser parser = new SQLParser(); 
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== MiniDB (Safe Mode) ===");
        System.out.println("Commands end with ;");
        
        while (true) {
            System.out.print("minidb> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                parser.execute(input);
            } catch (Exception e) {
                System.out.println("Unexpected Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
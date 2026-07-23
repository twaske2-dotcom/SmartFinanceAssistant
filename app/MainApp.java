import java.sql.*;
import java.util.Scanner;

public class MainApp {

    static final String URL ="jdbc:oracle:thin:@localhost:1521/XE";;
    static final String USER = "system";
    static final String PASS = "123";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            // Load Oracle Driver
            Class.forName("oracle.jdbc.OracleDriver");

            // Connect DB
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Database Connected Successfully!");

            while (true) {
                System.out.println("\n===== MAIN MENU =====");
                System.out.println("1. Student Management");
                System.out.println("2. Employee Management");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                int ch = sc.nextInt();

                switch (ch) {
                    case 1:
                        studentMenu(con);
                        break;

                    case 2:
                        employeeMenu(con);
                        break;

                    case 3:
                        con.close();
                        sc.close();
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver not found.");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ================= STUDENT MENU =================
    static void studentMenu(Connection con) {
        while (true) {
            try {
                System.out.println("\n--- STUDENT MENU ---");
                System.out.println("1. Insert");
                System.out.println("2. View");
                System.out.println("3. Back");
                System.out.print("Enter choice: ");

                int ch = sc.nextInt();

                switch (ch) {
                    case 1:
                        System.out.print("Enter ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter Marks: ");
                        int marks = sc.nextInt();

                        String sql = "INSERT INTO student VALUES (?, ?, ?)";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setInt(1, id);
                        ps.setString(2, name);
                        ps.setInt(3, marks);

                        int rows = ps.executeUpdate();

                        if (rows > 0)
                            System.out.println("Student Inserted Successfully!");
                        else
                            System.out.println("Insert Failed!");

                        ps.close();
                        break;

                    case 2:
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery("SELECT * FROM student");

                        System.out.println("\nID\tNAME\tMARKS");
                        System.out.println("-------------------------");

                        while (rs.next()) {
                            System.out.println(
                                    rs.getInt(1) + "\t" +
                                    rs.getString(2) + "\t" +
                                    rs.getInt(3));
                        }

                        rs.close();
                        st.close();
                        break;

                    case 3:
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (SQLException e) {
                System.out.println("Database Error: " + e.getMessage());
            }
        }
    }

    // ================= EMPLOYEE MENU =================
    static void employeeMenu(Connection con) {
        while (true) {
            try {
                System.out.println("\n--- EMPLOYEE MENU ---");
                System.out.println("1. Insert");
                System.out.println("2. View");
                System.out.println("3. Back");
                System.out.print("Enter choice: ");

                int ch = sc.nextInt();

                switch (ch) {
                    case 1:
                        System.out.print("Enter ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter Salary: ");
                        int salary = sc.nextInt();

                        String sql = "INSERT INTO employee VALUES (?, ?, ?)";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setInt(1, id);
                        ps.setString(2, name);
                        ps.setInt(3, salary);

                        int rows = ps.executeUpdate();

                        if (rows > 0)
                            System.out.println("Employee Inserted Successfully!");
                        else
                            System.out.println("Insert Failed!");

                        ps.close();
                        break;

                    case 2:
                        Statement st = con.createStatement();
                        ResultSet rs = st.executeQuery("SELECT * FROM employee");

                        System.out.println("\nID\tNAME\tSALARY");
                        System.out.println("-------------------------");

                        while (rs.next()) {
                            System.out.println(
                                    rs.getInt(1) + "\t" +
                                    rs.getString(2) + "\t" +
                                    rs.getInt(3));
                        }

                        rs.close();
                        st.close();
                        break;

                    case 3:
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (SQLException e) {
                System.out.println("Database Error: " + e.getMessage());
            }
        }
    }
}
import java.sql.*;
import java.util.Scanner;

public class DatabaseLab9 {

    static final String STUDENT_DB = "jdbc:sqlite:student.db";
    static final String EMPLOYEE_DB = "jdbc:sqlite:employee.db";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");

            Connection studentCon = DriverManager.getConnection(STUDENT_DB);
            Connection employeeCon = DriverManager.getConnection(EMPLOYEE_DB);
            System.out.println("Databases Connected Successfully!");

            // Create Tables and Triggers
            setupStudentDB(studentCon);
            setupEmployeeDB(employeeCon);

            while (true) {
                System.out.println("\n========== MAIN MENU ==========");
                System.out.println("1. Student Management System");
                System.out.println("2. Employee Management System");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                int ch = sc.nextInt();

                switch (ch) {
                    case 1: studentMenu(studentCon); break;
                    case 2: employeeMenu(employeeCon); break;
                    case 3:
                        studentCon.close();
                        employeeCon.close();
                        sc.close();
                        System.out.println("Exiting... Bye!");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("SQLite Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // ============================================================
    //                  STUDENT DATABASE SETUP
    // ============================================================

    static void setupStudentDB(Connection con) throws SQLException {
        Statement st = con.createStatement();

        // CREATE TABLE
        st.execute("CREATE TABLE IF NOT EXISTS student (" +
                   "id INTEGER PRIMARY KEY, " +
                   "name TEXT NOT NULL, " +
                   "marks INTEGER, " +
                   "created_at TEXT)");

        // TRIGGER — auto set timestamp on insert
        st.execute("CREATE TRIGGER IF NOT EXISTS student_timestamp " +
                   "AFTER INSERT ON student " +
                   "BEGIN " +
                   "UPDATE student SET created_at = datetime('now') " +
                   "WHERE id = NEW.id; " +
                   "END;");

        st.close();
        System.out.println("Student DB ready! (Table + Trigger created)");
    }

    // ============================================================
    //                  STUDENT MENU
    // ============================================================

    static void studentMenu(Connection con) {
        while (true) {
            try {
                System.out.println("\n--- STUDENT MANAGEMENT SYSTEM ---");
                System.out.println("1. Insert Student  (Stored Procedure)");
                System.out.println("2. View Students   (Cursor)");
                System.out.println("3. Update Marks");
                System.out.println("4. Delete Student");
                System.out.println("5. Get Grade       (Function)");
                System.out.println("6. Back to Main Menu");
                System.out.print("Enter choice: ");

                int ch = sc.nextInt();

                switch (ch) {
                    case 1: insertStudent(con); break;
                    case 2: viewStudents(con); break;
                    case 3: updateStudentMarks(con); break;
                    case 4: deleteStudent(con); break;
                    case 5: getGrade(); break;
                    case 6: return;
                    default: System.out.println("Invalid choice!");
                }

            } catch (SQLException e) {
                System.out.println("Database Error: " + e.getMessage());
            }
        }
    }

    // INSERT STUDENT — Stored Procedure style
    static void insertStudent(Connection con) throws SQLException {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Marks: ");
        int marks = sc.nextInt();

        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO student (id, name, marks) VALUES (?, ?, ?)");
        ps.setInt(1, id);
        ps.setString(2, name);
        ps.setInt(3, marks);

        int rows = ps.executeUpdate();
        if (rows > 0)
            System.out.println("Student Inserted! (Trigger auto-set timestamp)");
        else
            System.out.println("Insert Failed!");
        ps.close();
    }

    // VIEW STUDENTS — Cursor style (ResultSet)
    static void viewStudents(Connection con) throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM student");

        System.out.println("\nID\tNAME\t\tMARKS\tCREATED AT");
        System.out.println("------------------------------------------------");

        boolean found = false;
        while (rs.next()) {   // CURSOR fetch row by row
            found = true;
            System.out.println(
                rs.getInt("id")         + "\t" +
                rs.getString("name")    + "\t\t" +
                rs.getInt("marks")      + "\t" +
                rs.getString("created_at"));
        }

        if (!found) System.out.println("No records found!");
        rs.close();
        st.close();
    }

    // UPDATE MARKS
    static void updateStudentMarks(Connection con) throws SQLException {
        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        System.out.print("Enter New Marks: ");
        int marks = sc.nextInt();

        PreparedStatement ps = con.prepareStatement(
            "UPDATE student SET marks = ? WHERE id = ?");
        ps.setInt(1, marks);
        ps.setInt(2, id);

        int rows = ps.executeUpdate();
        System.out.println(rows > 0 ? "Marks Updated!" : "ID not found!");
        ps.close();
    }

    // DELETE STUDENT
    static void deleteStudent(Connection con) throws SQLException {
        System.out.print("Enter Student ID to delete: ");
        int id = sc.nextInt();

        PreparedStatement ps = con.prepareStatement(
            "DELETE FROM student WHERE id = ?");
        ps.setInt(1, id);

        int rows = ps.executeUpdate();
        System.out.println(rows > 0 ? "Student Deleted!" : "ID not found!");
        ps.close();
    }

    // FUNCTION — Calculate Grade
    static void getGrade() {
        System.out.print("Enter Marks: ");
        int marks = sc.nextInt();
        System.out.println("Grade: " + calculateGrade(marks));
    }

    static String calculateGrade(int marks) {
        if (marks >= 90) return "A+";
        else if (marks >= 80) return "A";
        else if (marks >= 70) return "B";
        else if (marks >= 60) return "C";
        else if (marks >= 50) return "D";
        else return "F (Fail)";
    }

    // ============================================================
    //                  EMPLOYEE DATABASE SETUP
    // ============================================================

    static void setupEmployeeDB(Connection con) throws SQLException {
        Statement st = con.createStatement();

        // CREATE TABLE
        st.execute("CREATE TABLE IF NOT EXISTS employee (" +
                   "id INTEGER PRIMARY KEY, " +
                   "name TEXT NOT NULL, " +
                   "department TEXT, " +
                   "salary REAL, " +
                   "joined_at TEXT)");

        // TRIGGER — auto set join date on insert
        st.execute("CREATE TRIGGER IF NOT EXISTS employee_join_date " +
                   "AFTER INSERT ON employee " +
                   "BEGIN " +
                   "UPDATE employee SET joined_at = datetime('now') " +
                   "WHERE id = NEW.id; " +
                   "END;");

        st.close();
        System.out.println("Employee DB ready! (Table + Trigger created)");
    }

    // ============================================================
    //                  EMPLOYEE MENU
    // ============================================================

    static void employeeMenu(Connection con) {
        while (true) {
            try {
                System.out.println("\n--- EMPLOYEE MANAGEMENT SYSTEM ---");
                System.out.println("1. Insert Employee   (Stored Procedure)");
                System.out.println("2. View Employees    (Cursor)");
                System.out.println("3. Update Salary");
                System.out.println("4. Delete Employee");
                System.out.println("5. Net Salary        (Function)");
                System.out.println("6. High Salary List  (Cursor Filter)");
                System.out.println("7. Back to Main Menu");
                System.out.print("Enter choice: ");

                int ch = sc.nextInt();

                switch (ch) {
                    case 1: insertEmployee(con); break;
                    case 2: viewEmployees(con); break;
                    case 3: updateSalary(con); break;
                    case 4: deleteEmployee(con); break;
                    case 5: calcNetSalary(); break;
                    case 6: highSalaryList(con); break;
                    case 7: return;
                    default: System.out.println("Invalid choice!");
                }

            } catch (SQLException e) {
                System.out.println("Database Error: " + e.getMessage());
            }
        }
    }

    // INSERT EMPLOYEE — Stored Procedure style
    static void insertEmployee(Connection con) throws SQLException {
        System.out.print("Enter ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Department: ");
        String dept = sc.nextLine();
        System.out.print("Enter Salary: ");
        double salary = sc.nextDouble();

        PreparedStatement ps = con.prepareStatement(
            "INSERT INTO employee (id, name, department, salary) VALUES (?, ?, ?, ?)");
        ps.setInt(1, id);
        ps.setString(2, name);
        ps.setString(3, dept);
        ps.setDouble(4, salary);

        int rows = ps.executeUpdate();
        if (rows > 0)
            System.out.println("Employee Inserted! (Trigger auto-set join date)");
        else
            System.out.println("Insert Failed!");
        ps.close();
    }

    // VIEW EMPLOYEES — Cursor style
    static void viewEmployees(Connection con) throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM employee");

        System.out.println("\nID\tNAME\t\tDEPT\t\tSALARY\t\tJOINED AT");
        System.out.println("----------------------------------------------------------------");

        boolean found = false;
        while (rs.next()) {   // CURSOR fetch row by row
            found = true;
            System.out.println(
                rs.getInt("id")             + "\t" +
                rs.getString("name")        + "\t\t" +
                rs.getString("department")  + "\t\t" +
                rs.getDouble("salary")      + "\t\t" +
                rs.getString("joined_at"));
        }

        if (!found) System.out.println("No records found!");
        rs.close();
        st.close();
    }

    // UPDATE SALARY
    static void updateSalary(Connection con) throws SQLException {
        System.out.print("Enter Employee ID: ");
        int id = sc.nextInt();
        System.out.print("Enter New Salary: ");
        double salary = sc.nextDouble();

        PreparedStatement ps = con.prepareStatement(
            "UPDATE employee SET salary = ? WHERE id = ?");
        ps.setDouble(1, salary);
        ps.setInt(2, id);

        int rows = ps.executeUpdate();
        System.out.println(rows > 0 ? "Salary Updated!" : "ID not found!");
        ps.close();
    }

    // DELETE EMPLOYEE
    static void deleteEmployee(Connection con) throws SQLException {
        System.out.print("Enter Employee ID to delete: ");
        int id = sc.nextInt();

        PreparedStatement ps = con.prepareStatement(
            "DELETE FROM employee WHERE id = ?");
        ps.setInt(1, id);

        int rows = ps.executeUpdate();
        System.out.println(rows > 0 ? "Employee Deleted!" : "ID not found!");
        ps.close();
    }

    // FUNCTION — Calculate Net Salary
    static void calcNetSalary() {
        System.out.print("Enter Gross Salary: ");
        double gross = sc.nextDouble();
        System.out.println("Net Salary: " + calculateNetSalary(gross));
    }

    static double calculateNetSalary(double gross) {
        double tax = 0;
        if (gross > 50000)      tax = gross * 0.20;
        else if (gross > 30000) tax = gross * 0.10;
        else                    tax = gross * 0.05;
        double pf = gross * 0.12;
        return gross - tax - pf;
    }

    // CURSOR FILTER — High Salary Employees
    static void highSalaryList(Connection con) throws SQLException {
        System.out.print("Enter minimum salary to filter: ");
        double minSalary = sc.nextDouble();

        PreparedStatement ps = con.prepareStatement(
            "SELECT * FROM employee WHERE salary > ?");
        ps.setDouble(1, minSalary);
        ResultSet rs = ps.executeQuery();  // CURSOR

        System.out.println("\nID\tNAME\t\tSALARY");
        System.out.println("--------------------------------");

        boolean found = false;
        while (rs.next()) {   // CURSOR iteration
            found = true;
            System.out.println(
                rs.getInt("id")        + "\t" +
                rs.getString("name")   + "\t\t" +
                rs.getDouble("salary"));
        }

        if (!found) System.out.println("No employees found above this salary!");
        rs.close();
        ps.close();
    }
}
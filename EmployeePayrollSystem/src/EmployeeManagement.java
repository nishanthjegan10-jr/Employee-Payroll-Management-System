import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EmployeeManagement extends JFrame {

    JTextField codeField;
    JTextField firstNameField;
    JTextField lastNameField;
    JTextField emailField;
    JTextField phoneField;
    JTextField designationField;

    JTextField searchField;
    JTable table;
    DefaultTableModel model;

    public EmployeeManagement() {

        setTitle("Employee Management");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));

        form.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        codeField = new JTextField();
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        designationField = new JTextField();

        form.add(new JLabel("Employee Code:"));
        form.add(codeField);

        form.add(new JLabel("First Name:"));
        form.add(firstNameField);

        form.add(new JLabel("Last Name:"));
        form.add(lastNameField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        form.add(new JLabel("Designation:"));
        form.add(designationField);

        JButton addButton = new JButton("Add Employee");
        JButton refreshButton = new JButton("Refresh");

        form.add(addButton);
        form.add(refreshButton);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 15, 8, 15)
        );

        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Search Employee:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(form, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Code");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Email");
        model.addColumn("Phone");
        model.addColumn("Designation");

        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.CENTER);

        addButton.addActionListener(e -> addEmployee());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadEmployees();
        });

        searchButton.addActionListener(e -> searchEmployees());

        searchField.addActionListener(e -> searchEmployees());

        loadEmployees();

        setVisible(true);
    }

    private void addEmployee() {

        String sql = """
                INSERT INTO employees
                (employee_code, first_name, last_name,
                 email, phone, designation)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try {

            Connection con = DatabaseConnection.getConnection();

            if (con == null) {
                throw new SQLException("Unable to connect to database.");
            }

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, codeField.getText());
            pst.setString(2, firstNameField.getText());
            pst.setString(3, lastNameField.getText());
            pst.setString(4, emailField.getText());
            pst.setString(5, phoneField.getText());
            pst.setString(6, designationField.getText());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Employee added successfully!"
            );

            con.close();

            clearFields();
            loadEmployees();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    private void loadEmployees() {

        model.setRowCount(0);

        String sql = "SELECT employee_id, employee_code, " +
                "first_name, last_name, email, phone, designation " +
                "FROM employees";

        try {

            Connection con = DatabaseConnection.getConnection();

            if (con == null) {
                throw new SQLException("Unable to connect to database.");
            }

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                model.addRow(new Object[] {
                        rs.getInt("employee_id"),
                        rs.getString("employee_code"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("designation")
                });
            }

            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading employees: " + e.getMessage()
            );
        }
    }

    private void searchEmployees() {

        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadEmployees();
            return;
        }

        model.setRowCount(0);

        String sql = """
                SELECT employee_id, employee_code, first_name,
                       last_name, email, phone, designation
                FROM employees
                WHERE CAST(employee_id AS CHAR) LIKE ?
                   OR employee_code LIKE ?
                   OR first_name LIKE ?
                   OR last_name LIKE ?
                   OR CONCAT(first_name, ' ', last_name) LIKE ?
                """;

        try {

            Connection con = DatabaseConnection.getConnection();

            if (con == null) {
                throw new SQLException("Unable to connect to database.");
            }

            PreparedStatement pst = con.prepareStatement(sql);

            String searchValue = "%" + keyword + "%";

            pst.setString(1, searchValue);
            pst.setString(2, searchValue);
            pst.setString(3, searchValue);
            pst.setString(4, searchValue);
            pst.setString(5, searchValue);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[] {
                        rs.getInt("employee_id"),
                        rs.getString("employee_code"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("designation")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No matching employee found!"
                );
            }

            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error searching employees: " + e.getMessage()
            );
        }
    }

    private void clearFields() {

        codeField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        designationField.setText("");
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Payslip extends JFrame {

    JComboBox<String> employeeBox;
    JTextField employeeSearchField;
    List<String> allEmployees = new ArrayList<>();
    JTextArea payslipArea;

    public Payslip() {

        setTitle("Employee Payslip");
        setSize(750, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Keep search controls and action buttons on separate rows
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));

        employeeBox = new JComboBox<>();
        employeeBox.setPreferredSize(new Dimension(260, 28));
        employeeSearchField = new JTextField(18);

        JButton viewButton = new JButton("VIEW PAYSLIP");
        JButton printButton = new JButton("PRINT");
        JButton downloadButton = new JButton("DOWNLOAD");

        searchPanel.add(new JLabel("Search Employee (ID / Code / Name):"));
        searchPanel.add(employeeSearchField);
        searchPanel.add(new JLabel("Employee:"));
        searchPanel.add(employeeBox);

        buttonPanel.add(viewButton);
        buttonPanel.add(printButton);
        buttonPanel.add(downloadButton);

        topPanel.add(searchPanel);
        topPanel.add(buttonPanel);

        payslipArea = new JTextArea();
        payslipArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        payslipArea.setEditable(false);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(payslipArea), BorderLayout.CENTER);

        loadEmployees();

        viewButton.addActionListener(e -> loadPayslip());

        printButton.addActionListener(e -> printPayslip());

        downloadButton.addActionListener(e -> downloadPayslip());

        setVisible(true);
    }

    private void loadEmployees() {

        try {

            Connection con = DatabaseConnection.getConnection();

            String sql =
                    "SELECT employee_id, employee_code, first_name, last_name " +
                    "FROM employees";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            employeeBox.removeAllItems();
            allEmployees.clear();

            while (rs.next()) {
                String employee =
                        rs.getInt("employee_id")
                        + " - "
                        + rs.getString("employee_code")
                        + " - "
                        + rs.getString("first_name")
                        + " "
                        + rs.getString("last_name");

                allEmployees.add(employee);
                employeeBox.addItem(employee);
            }

            rs.close();
            st.close();
            con.close();
            setupEmployeeSearch();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading employees: " + e.getMessage()
            );
        }
    }

    private void setupEmployeeSearch() {

        employeeSearchField.getDocument().addDocumentListener(
                new DocumentListener() {

                    private void filterEmployees() {
                        String searchText = employeeSearchField.getText()
                                .trim().toLowerCase();

                        employeeBox.removeAllItems();

                        for (String employee : allEmployees) {
                            if (employee.toLowerCase().contains(searchText)) {
                                employeeBox.addItem(employee);
                            }
                        }
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filterEmployees();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filterEmployees();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filterEmployees();
                    }
                }
        );
    }

    private void loadPayslip() {

        if (employeeBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an employee."
            );
            return;
        }

        try {

            int employeeId = Integer.parseInt(
                    employeeBox.getSelectedItem()
                            .toString()
                            .split(" - ")[0]
            );

            Connection con = DatabaseConnection.getConnection();

            String sql =
                    "SELECT e.employee_code, e.first_name, e.last_name, " +
                    "e.designation, p.month, p.year, " +
                    "p.gross_salary, p.total_deduction, p.net_salary " +
                    "FROM employees e " +
                    "JOIN payroll p ON e.employee_id = p.employee_id " +
                    "WHERE e.employee_id = ? " +
                    "ORDER BY p.payroll_id DESC LIMIT 1";

            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, employeeId);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                String payslip =
                        "========================================\n" +
                        "              PAYSLIP\n" +
                        "========================================\n\n" +

                        "Employee Code : "
                        + rs.getString("employee_code") + "\n" +

                        "Employee Name : "
                        + rs.getString("first_name") + " "
                        + rs.getString("last_name") + "\n" +

                        "Designation   : "
                        + rs.getString("designation") + "\n\n" +

                        "----------------------------------------\n" +

                        "Month         : "
                        + rs.getInt("month") + "\n" +

                        "Year          : "
                        + rs.getInt("year") + "\n\n" +

                        "----------------------------------------\n" +

                        "Gross Salary  : ₹"
                        + String.format("%.2f",
                        rs.getDouble("gross_salary")) + "\n" +

                        "Deduction     : ₹"
                        + String.format("%.2f",
                        rs.getDouble("total_deduction")) + "\n\n" +

                        "NET SALARY    : ₹"
                        + String.format("%.2f",
                        rs.getDouble("net_salary")) + "\n\n" +

                        "========================================\n" +
                        "        Payroll Management System\n" +
                        "========================================";

                payslipArea.setText(payslip);

            } else {

                payslipArea.setText(
                        "No payroll record found for this employee."
                );
            }

            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading payslip: " + e.getMessage()
            );
        }
    }

    private void printPayslip() {

        if (payslipArea.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please click VIEW PAYSLIP first."
            );

            return;
        }

        try {

            boolean complete = payslipArea.print();

            if (complete) {

                JOptionPane.showMessageDialog(
                        this,
                        "Payslip sent to printer."
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Printing cancelled."
                );
            }

        } catch (PrinterException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Printing error: " + e.getMessage()
            );
        }
    }

    private void downloadPayslip() {

        if (payslipArea.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please click VIEW PAYSLIP first."
            );

            return;
        }

        JFileChooser chooser = new JFileChooser();

        chooser.setSelectedFile(
                new File("Payslip.txt")
        );

        int result = chooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            try {

                File file = chooser.getSelectedFile();

                FileWriter writer = new FileWriter(file);

                writer.write(payslipArea.getText());

                writer.close();

                JOptionPane.showMessageDialog(
                        this,
                        "Payslip downloaded successfully!"
                );

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Error saving payslip: " + e.getMessage()
                );
            }
        }
    }

    public static void main(String[] args) {

        new Payslip();
    }
}
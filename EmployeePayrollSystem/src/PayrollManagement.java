import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PayrollManagement extends JFrame {

    JComboBox<String> employeeBox;
    JTextField employeeSearchField = new JTextField();
    List<String> allEmployees = new ArrayList<>();

    JTextField basicField = new JTextField();
    JTextField hraField = new JTextField("0");
    JTextField daField = new JTextField("0");
    JTextField allowanceField = new JTextField("0");

    JTextField pfField = new JTextField("0");
    JTextField taxField = new JTextField("0");
    JTextField deductionField = new JTextField("0");

    JLabel grossLabel = new JLabel("₹ 0.00");
    JLabel totalDeductionLabel = new JLabel("₹ 0.00");
    JLabel netLabel = new JLabel("₹ 0.00");

    JTable table;
    DefaultTableModel model;

    public PayrollManagement() {

        setTitle("Payroll Management");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ================= MAIN PANEL =================

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        // ================= EMPLOYEE + SALARY FORM =================

        JPanel formPanel = new JPanel(
                new GridLayout(8, 2, 10, 10)
        );

        formPanel.setBorder(
                BorderFactory.createTitledBorder("Employee Salary Details")
        );

        employeeBox = new JComboBox<>();

        formPanel.add(new JLabel("Search Employee (ID / Code / Name):"));
        formPanel.add(employeeSearchField);

        formPanel.add(new JLabel("Employee:"));
        formPanel.add(employeeBox);

        formPanel.add(new JLabel("Basic Salary:"));
        formPanel.add(basicField);

        formPanel.add(new JLabel("HRA:"));
        formPanel.add(hraField);

        formPanel.add(new JLabel("DA:"));
        formPanel.add(daField);

        formPanel.add(new JLabel("Other Allowance:"));
        formPanel.add(allowanceField);

        formPanel.add(new JLabel("PF:"));
        formPanel.add(pfField);

        formPanel.add(new JLabel("Tax:"));
        formPanel.add(taxField);

        // ================= DEDUCTION =================

        JPanel otherPanel = new JPanel(
                new GridLayout(1, 2, 10, 10)
        );

        otherPanel.add(new JLabel("Other Deduction:"));
        otherPanel.add(deductionField);

        // Combine salary form + other deduction

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(otherPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ================= CALCULATION =================

        JPanel calculationPanel = new JPanel(
                new GridLayout(3, 4, 15, 15)
        );

        calculationPanel.setBorder(
                BorderFactory.createTitledBorder("Salary Calculation")
        );

        calculationPanel.add(new JLabel("Gross Salary:"));
        calculationPanel.add(grossLabel);

        calculationPanel.add(new JLabel("Total Deduction:"));
        calculationPanel.add(totalDeductionLabel);

        calculationPanel.add(new JLabel("Net Salary:"));
        calculationPanel.add(netLabel);

        JButton calculateButton =
                new JButton("CALCULATE");

        JButton saveButton =
                new JButton("SAVE PAYROLL");

        calculationPanel.add(calculateButton);
        calculationPanel.add(saveButton);

        // Empty spaces for layout

        calculationPanel.add(new JLabel(""));
        calculationPanel.add(new JLabel(""));

        mainPanel.add(calculationPanel, BorderLayout.CENTER);

        // ================= PAYROLL TABLE =================

        model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Employee");
        model.addColumn("Month");
        model.addColumn("Year");
        model.addColumn("Gross");
        model.addColumn("Deduction");
        model.addColumn("Net");

        table = new JTable(model);

        JScrollPane scrollPane =
                new JScrollPane(table);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Payroll History"
                )
        );

        scrollPane.setPreferredSize(
                new Dimension(1000, 220)
        );

        mainPanel.add(
                scrollPane,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        // ================= BUTTON ACTIONS =================

        calculateButton.addActionListener(
                e -> calculate()
        );

        saveButton.addActionListener(
                e -> savePayroll()
        );

        // ================= LOAD DATA =================

        loadEmployees();
        setupEmployeeSearch();
        loadPayroll();

        setVisible(true);
    }

    // =====================================================
    // GET NUMBER FROM TEXT FIELD
    // =====================================================

    private double getValue(JTextField field) {

        String text = field.getText().trim();

        if (text.isEmpty()) {
            return 0;
        }

        return Double.parseDouble(text);
    }

    // =====================================================
    // CALCULATE SALARY
    // =====================================================

    private boolean calculate() {

        try {

            double basic =
                    getValue(basicField);

            double hra =
                    getValue(hraField);

            double da =
                    getValue(daField);

            double allowance =
                    getValue(allowanceField);

            double pf =
                    getValue(pfField);

            double tax =
                    getValue(taxField);

            double other =
                    getValue(deductionField);

            // Gross Salary

            double gross =
                    basic
                    + hra
                    + da
                    + allowance;

            // Total deductions

            double totalDeduction =
                    pf
                    + tax
                    + other;

            // Net Salary

            double net =
                    gross
                    - totalDeduction;

            // Display results

            grossLabel.setText(
                    String.format("₹ %.2f", gross)
            );

            totalDeductionLabel.setText(
                    String.format("₹ %.2f", totalDeduction)
            );

            netLabel.setText(
                    String.format("₹ %.2f", net)
            );

            return true;

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }

    // =====================================================
    // SAVE PAYROLL
    // =====================================================

    private void savePayroll() {

        try {

            // Calculate first

            if (!calculate()) {
                return;
            }

            // Check employee

            if (employeeBox.getSelectedItem() == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select an employee."
                );

                return;
            }

            // Get employee ID

            String employeeText =
                    employeeBox.getSelectedItem()
                            .toString();

            int employeeId =
                    Integer.parseInt(
                            employeeText
                                    .split(" - ")[0]
                    );

            // Calculate values again

            double basic =
                    getValue(basicField);

            double hra =
                    getValue(hraField);

            double da =
                    getValue(daField);

            double allowance =
                    getValue(allowanceField);

            double pf =
                    getValue(pfField);

            double tax =
                    getValue(taxField);

            double other =
                    getValue(deductionField);

            double gross =
                    basic
                    + hra
                    + da
                    + allowance;

            double totalDeduction =
                    pf
                    + tax
                    + other;

            double net =
                    gross
                    - totalDeduction;

            LocalDate date =
                    LocalDate.now();

            // ================= SQL =================

            String sql =
                    "INSERT INTO payroll " +
                    "(employee_id, month, year, gross_salary, " +
                    "total_deduction, net_salary, payment_date, " +
                    "payment_status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            Connection con =
                    DatabaseConnection.getConnection();

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setInt(
                    1,
                    employeeId
            );

            pst.setInt(
                    2,
                    date.getMonthValue()
            );

            pst.setInt(
                    3,
                    date.getYear()
            );

            pst.setDouble(
                    4,
                    gross
            );

            pst.setDouble(
                    5,
                    totalDeduction
            );

            pst.setDouble(
                    6,
                    net
            );

            pst.setDate(
                    7,
                    Date.valueOf(date)
            );

            pst.setString(
                    8,
                    "Paid"
            );

            pst.executeUpdate();

            pst.close();
            con.close();

            JOptionPane.showMessageDialog(
                    this,
                    "Payroll saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Refresh table

            loadPayroll();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error saving payroll:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // LOAD EMPLOYEES
    // =====================================================

    private void loadEmployees() {

        try {

            Connection con =
                    DatabaseConnection.getConnection();

            String sql =
                    "SELECT employee_id, employee_code, first_name " +
                    "FROM employees";

            Statement st =
                    con.createStatement();

            ResultSet rs =
                    st.executeQuery(sql);

            employeeBox.removeAllItems();
            allEmployees.clear();

            while (rs.next()) {

                String employee =
                        rs.getInt("employee_id")
                        + " - "
                        + rs.getString("employee_code")
                        + " - "
                        + rs.getString("first_name");

                allEmployees.add(employee);
                employeeBox.addItem(employee);
            }

            rs.close();
            st.close();
            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading employees:\n"
                            + e.getMessage()
            );
        }
    }

    // =====================================================
    // SEARCH EMPLOYEES IN THE DROPDOWN
    // =====================================================

    private void setupEmployeeSearch() {

        employeeSearchField.getDocument().addDocumentListener(
                new DocumentListener() {

                    private void filterEmployees() {

                        String searchText =
                                employeeSearchField.getText().trim().toLowerCase();

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

    // =====================================================
    // LOAD PAYROLL HISTORY
    // =====================================================

    private void loadPayroll() {

        model.setRowCount(0);

        try {

            Connection con =
                    DatabaseConnection.getConnection();

            String sql =
                    "SELECT p.payroll_id, " +
                    "CONCAT(e.employee_code, ' - ', e.first_name) AS employee, " +
                    "p.month, p.year, " +
                    "p.gross_salary, " +
                    "p.total_deduction, " +
                    "p.net_salary " +
                    "FROM payroll p " +
                    "JOIN employees e " +
                    "ON p.employee_id = e.employee_id " +
                    "ORDER BY p.payroll_id DESC";

            Statement st =
                    con.createStatement();

            ResultSet rs =
                    st.executeQuery(sql);

            while (rs.next()) {

                model.addRow(
                        new Object[]{

                                rs.getInt("payroll_id"),

                                rs.getString("employee"),

                                rs.getInt("month"),

                                rs.getInt("year"),

                                rs.getDouble("gross_salary"),

                                rs.getDouble("total_deduction"),

                                rs.getDouble("net_salary")
                        }
                );
            }

            rs.close();
            st.close();
            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading payroll:\n"
                            + e.getMessage()
            );
        }
    }
}
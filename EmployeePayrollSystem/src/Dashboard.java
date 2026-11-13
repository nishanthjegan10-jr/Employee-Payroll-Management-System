import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {

        setTitle("Employee Payroll Management System");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel(
                "EMPLOYEE PAYROLL MANAGEMENT SYSTEM",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 22));

        JButton employeeButton =
                new JButton("Employee Management");

        JButton payrollButton =
                new JButton("Payroll");

        JButton payslipButton =
                new JButton("Payslip");

        JButton logoutButton =
                new JButton("Logout");

        // 4 buttons now
        JPanel panel =
                new JPanel(new GridLayout(4, 1, 20, 20));

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        50, 200, 50, 200
                )
        );

        panel.add(employeeButton);
        panel.add(payrollButton);
        panel.add(payslipButton);
        panel.add(logoutButton);

        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // Employee Management
        employeeButton.addActionListener(e -> {
            new EmployeeManagement();
        });

        // Payroll
        payrollButton.addActionListener(e -> {
            new PayrollManagement();
        });

        // Payslip
        payslipButton.addActionListener(e -> {
            new Payslip();
        });

        // Logout
        logoutButton.addActionListener(e -> {
            dispose();
            new Login();
        });

        setVisible(true);
    }
}
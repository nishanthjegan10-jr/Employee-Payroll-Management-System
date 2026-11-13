# Employee Payroll Management System

A Java-based desktop application developed using **Java Swing, MySQL, and JDBC** to simplify employee record management and payroll processing.

## 📌 Project Overview

The Employee Payroll Management System is designed to automate employee information management and salary calculation. It provides a user-friendly graphical interface to manage employee records, calculate salaries, maintain payroll history, and generate employee payslips.

The application uses MySQL to store employee and payroll information securely and JDBC to establish connectivity between Java and the database.

## ✨ Features

- **Login & Authentication** – Secure login for authorized users.
- **Dashboard** – Centralized navigation to different modules.
- **Employee Management** – Add, view, search, and refresh employee records.
- **Payroll Management** – Select employees, enter salary details, and calculate payroll.
- **Automatic Salary Calculation** – Calculates gross salary, total deductions, and net salary.
- **Payslip Generation** – View, print, and download employee payslips.
- **Search Functionality** – Search employees by ID, employee code, or name.
- **Database Management** – Store and retrieve employee and payroll records using MySQL.

## 🛠️ Technologies Used

- **Programming Language:** Java
- **GUI:** Java Swing
- **Database:** MySQL
- **Database Connectivity:** JDBC
- **IDE:** Visual Studio Code

## 🧮 Salary Calculation

The system calculates employee salaries using the following formulas:

**Gross Salary**

`Gross Salary = Basic Salary + HRA + DA + Allowance`

**Total Deduction**

`Total Deduction = PF + Tax + Other Deduction`

**Net Salary**

`Net Salary = Gross Salary - Total Deduction`

## 🗄️ Database Structure

The application uses three main MySQL tables:

- `users` – Stores login credentials.
- `employees` – Stores employee information.
- `payroll` – Stores salary details and payroll records.

## ⚙️ Installation and Setup

1. Clone this repository:

   ```bash
   git clone YOUR_REPOSITORY_URL
   ```

2. Open the project in Visual Studio Code.

3. Install Java JDK and MySQL.

4. Create the database named `payroll_management`.

5. Import the provided SQL database file, if included.

6. Configure the MySQL database connection details in the Java project.

7. Add the MySQL Connector/J library to the project.

8. Run the main Java application file.

## 🎯 Project Objective

To develop a simple and efficient desktop-based payroll management application that reduces manual work, improves salary calculation accuracy, and simplifies employee and payroll record management.

## 🚀 Future Enhancements

- Advanced payroll reports.
- Employee record editing and deletion.
- Role-based access control.
- Monthly salary reports and analytics.

## 👨‍💻 Developed By

**Team Members:**

- Nishanth J
- Nithish Kumar N
- Nithish R
- PandhalaSelvam R

**Course:** CGB1201 – Java Programming  
**Academic Year:** 2026–2027

---

*Developed as an academic project using Java Swing and MySQL.*

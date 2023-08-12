package javadatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class AdminFrame extends JFrame {

    private Connection connection;

    public AdminFrame() {
        super("Admin Panel");

        // Create UI components
        JPanel adminPanel = new JPanel(new GridBagLayout());
        JLabel titleLabel = new JLabel("Admin Panel", SwingConstants.CENTER);
        JButton viewUserButton = new JButton("View User Table");
        JButton viewAccountButton = new JButton("View Account Table");
        JButton addClientButton = new JButton("Add New Client");
        JButton addAdminButton = new JButton("Add New Admin");

        // Add UI components to panel
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10); // add space between buttons
        adminPanel.add(titleLabel, c);

        c.gridy = 1;
        adminPanel.add(viewUserButton, c);

        c.gridy = 2;
        adminPanel.add(viewAccountButton, c);

        c.gridy = 3;
        adminPanel.add(addClientButton, c);

        c.gridy = 4;
        adminPanel.add(addAdminButton, c);

        getContentPane().add(adminPanel);

        // Register button listeners
        viewUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewUserTable();
            }
        });

        viewAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewAccountTable();
            }
        });

        addClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewClient();
            }
        });

        addAdminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewAdmin();
            }
        });

        // Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/bankdb?zeroDateTimeBehavior=CONVERT_TO_NULL",
                    "root",
                    "pawan0343@"
            );
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to database.");
            dispose();
            return;
        }

        setSize(400, 300); // increase window size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void viewUserTable() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM user");

            // Create a JTable with the result set
            JTable table = new JTable(buildTableModel(resultSet)) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Create a JScrollPane to contain the JTable
            JScrollPane scrollPane = new JScrollPane(table);

            // Display the JScrollPane in a JOptionPane
            JOptionPane.showMessageDialog(this, scrollPane, "User Table", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve user table.");
        }
    }

    private static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Get column names
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        // Get rows
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void viewAccountTable() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM account");

            // Create a JTable with the result set
            JTable table = new JTable(buildTableModel(resultSet)) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Create a JScrollPane to contain the JTable
            JScrollPane scrollPane = new JScrollPane(table);

            // Display the JScrollPane in a JOptionPane
            JOptionPane.showMessageDialog(this, scrollPane, "Account Table", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to retrieve Account table.");
        }
    }

    private void addNewClient() {
        JTextField firstNameField = new JTextField(10);
        JTextField lastNameField = new JTextField(10);
        JTextField userIDField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);

        JPanel addClientPanel = new JPanel();
        addClientPanel.add(new JLabel("First Name: "));
        addClientPanel.add(firstNameField);
        addClientPanel.add(new JLabel("Last Name: "));
        addClientPanel.add(lastNameField);
        addClientPanel.add(new JLabel("User ID: "));
        addClientPanel.add(userIDField);
        addClientPanel.add(new JLabel("Password: "));
        addClientPanel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, addClientPanel, "Add New Client", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String userID = userIDField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            try {
                // Hash the password using SHA-1
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                byte[] hashBytes = md.digest(password.getBytes());
              String hashedPassword = javax.xml.bind.DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

                PreparedStatement statement = connection.prepareStatement("INSERT INTO user(userID, firstName, lastName, userType, password) VALUES (?, ?, ?, ?, ?)");
                statement.setString(1, userID);
                statement.setString(2, firstName);
                statement.setString(3, lastName);
                statement.setString(4, "client");
                statement.setString(5, hashedPassword);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 1) {
                    JOptionPane.showMessageDialog(this, "New client added successfully.", "Add New client", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add new client.", "Add New client", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add new client.", "Add New client", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addNewAdmin() {
        JTextField firstNameField = new JTextField(10);
        JTextField lastNameField = new JTextField(10);
        JTextField userIDField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);

        JPanel addClientPanel = new JPanel();
        addClientPanel.add(new JLabel("First Name: "));
        addClientPanel.add(firstNameField);
        addClientPanel.add(new JLabel("Last Name: "));
        addClientPanel.add(lastNameField);
        addClientPanel.add(new JLabel("User ID: "));
        addClientPanel.add(userIDField);
        addClientPanel.add(new JLabel("Password: "));
        addClientPanel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, addClientPanel, "Add New Admin", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String userID = userIDField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            try {
                // Hash the password using SHA-1
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                byte[] hashBytes = md.digest(password.getBytes());
              String hashedPassword = javax.xml.bind.DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

                PreparedStatement statement = connection.prepareStatement("INSERT INTO user(userID, firstName, lastName, userType, password) VALUES (?, ?, ?, ?, ?)");
                statement.setString(1, userID);
                statement.setString(2, firstName);
                statement.setString(3, lastName);
                statement.setString(4, "admin");
                statement.setString(5, hashedPassword);

                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 1) {
                    JOptionPane.showMessageDialog(this, "New admin added successfully.", "Add New admin", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add new admin.", "Add New admin", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add new admin.", "Add New admin", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}


/*
    Clinet Class
*/
class ClientFrame extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JButton showAccountInfoButton, transferMoneyButton, exitButton;
    private JList<String> accountList;

    private int clientID;
    private Connection conn;

    public ClientFrame(int clientID) {
        super("Client Account Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);

        this.clientID = clientID;

        // create database connection
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "pawan0343@");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // create GUI components
        titleLabel = new JLabel("Welcome, " + getClientName() + "!");
        titleLabel.setBounds(20, 20, 200, 20);
        add(titleLabel);

        showAccountInfoButton = new JButton("Show Account Info");
        showAccountInfoButton.setBounds(20, 50, 150, 30);
        showAccountInfoButton.addActionListener(this);
        add(showAccountInfoButton);

        transferMoneyButton = new JButton("Transfer Money");
        transferMoneyButton.setBounds(20, 90, 150, 30);
        transferMoneyButton.addActionListener(this);
        add(transferMoneyButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(20, 130, 150, 30);
        exitButton.addActionListener(this);
        add(exitButton);

        accountList = new JList<String>();
        accountList.setBounds(200, 50, 250, 300);
        add(accountList);

        setLayout(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == showAccountInfoButton) {
            displayAccountInfo();
        } else if (e.getSource() == transferMoneyButton) {
            transferMoney();
        } else if (e.getSource() == exitButton) {
            dispose();
        }
    }

    private String getClientName() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT firstName, lastName FROM user WHERE userID = ?");
            stmt.setInt(1, clientID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getString("firstName") + " " + rs.getString("lastName");
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void displayAccountInfo() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT account.account_type, account.balance, user.userID, user.firstName, user.lastName FROM account INNER JOIN user ON account.id = user.userID WHERE account.id = ?");
            stmt.setInt(1, clientID);
            ResultSet rs = stmt.executeQuery();

            DefaultListModel<String> model = new DefaultListModel<>();
            while (rs.next()) {
                int userId = rs.getInt("userID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                model.addElement(userId + " " + firstName + " " + lastName + " - " + accountType + ": $" + balance);
            }
            accountList.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void transferMoney() {
        String[] accountTypes = {"S", "C", "FD", "INV"};

        // display input dialog to get transfer information
        JComboBox<String> sourceAccountTypeComboBox = new JComboBox<>(accountTypes);
        JTextField amountTextField = new JTextField();
        JTextField destUserIDTextField = new JTextField();
        JComboBox<String> destAccountTypeComboBox = new JComboBox<>(accountTypes);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Source Account Type:"));
        panel.add(sourceAccountTypeComboBox);
        panel.add(new JLabel("Amount:"));
        panel.add(amountTextField);
        panel.add(new JLabel("Destination User ID:"));
        panel.add(destUserIDTextField);
        panel.add(new JLabel("Destination Account Type:"));
        panel.add(destAccountTypeComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Transfer Money", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // get transfer information from user input
            String sourceAccountType = (String) sourceAccountTypeComboBox.getSelectedItem();
            double amount = Double.parseDouble(amountTextField.getText());
            int destUserID = Integer.parseInt(destUserIDTextField.getText());
            String destAccountType = (String) destAccountTypeComboBox.getSelectedItem();

            // perform transfer
            try {
                // check if source account has enough balance
                PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM account WHERE id = ? AND account_type = ?");
                stmt.setInt(1, clientID);
                stmt.setString(2, sourceAccountType);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                double balance = rs.getDouble("balance");
                if (balance < amount) {
                    JOptionPane.showMessageDialog(null, "Not enough balance in source account.");
                    return;
                }

                // update source account balance
                stmt = conn.prepareStatement("UPDATE account SET balance = balance - ? WHERE id = ? AND account_type = ?");
                stmt.setDouble(1, amount);
                stmt.setInt(2, clientID);
                stmt.setString(3, sourceAccountType);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    JOptionPane.showMessageDialog(null, "Error updating source account balance.");
                    return;
                }

                // update destination account balance
                stmt = conn.prepareStatement("UPDATE account SET balance = balance + ? WHERE id = ? AND account_type = ?");
                stmt.setDouble(1, amount);
                stmt.setInt(2, destUserID);
                stmt.setString(3, destAccountType);
                rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    JOptionPane.showMessageDialog(null, "Error updating destination account balance.");
                    return;
                }

                JOptionPane.showMessageDialog(null, "Transfer successful.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error Finding Account of User.");
            }
        }
    }

}

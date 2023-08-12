package javadatabase;

import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.*;
import java.sql.*;
import java.util.Base64;

public class BankDB extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JButton connectButton, disconnectButton, loginButton, exitButton;
    private JTextField idField, passwordField;
    private JLabel idLabel, passwordLabel, resultLabel;
    private Connection conn;
    private boolean isConnected;

    public BankDB() {
        super("BankDB");

        // Create GUI components
        connectButton = new JButton("Connect");
        connectButton.addActionListener(this);
        disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(this);
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        idField = new JTextField(20);
        passwordField = new JTextField(20);
        idLabel = new JLabel("ID:");
        passwordLabel = new JLabel("Password:");
        resultLabel = new JLabel("");
        resultLabel.setForeground(Color.RED);

        // Set preferred size for text fields
        idField.setPreferredSize(new Dimension(200, 25));
        passwordField.setPreferredSize(new Dimension(200, 25));

        // Create login panel
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        loginPanel.add(idLabel);
        loginPanel.add(idField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(resultLabel);

        // Add components to content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(loginPanel, c);
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);
        buttonPanel.add(exitButton);
        mainPanel.add(buttonPanel, c);
        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Set initial state
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
        loginButton.setEnabled(false);
        isConnected = false;

        // Set frame properties
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton) {
            // Connect to database
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb?zeroDateTimeBehavior=CONVERT_TO_NULL", "root", "pawan0343@");
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
                loginButton.setEnabled(true);
                isConnected = true;
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "MySQL JDBC driver not found", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Could not connect to database", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == disconnectButton) {
            // Disconnect from database
            try {
                conn.close();
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                loginButton.setEnabled(false);
                resultLabel.setText("");
                isConnected = false;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Could not disconnect from database", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == loginButton) {
            // Login
            String id = idField.getText();
            String password = passwordField.getText();
            if (id.isEmpty() || password.isEmpty()) {
                resultLabel.setText("Please enter ID and password");
            } else {
                try {
                    PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE userID = ?");
                    stmt.setString(1, id);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String hashedPassword = rs.getString("Password");
                        MessageDigest md = MessageDigest.getInstance("SHA-1");
                        byte[] hashBytes = md.digest(password.getBytes());
                        String hashedInputPassword = javax.xml.bind.DatatypeConverter.printHexBinary(hashBytes).toLowerCase();
                        if (hashedInputPassword.equals(hashedPassword)) {
                            String userType = rs.getString("userType");
                            int cId = Integer.parseInt(rs.getString("userID"));
                            if (userType.equals("admin")) {
                                try {
                                    AdminFrame admin = new AdminFrame();
                                } catch (Exception e1) {
                                    JOptionPane.showMessageDialog(this, "Error While Loading Account", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                try {
                                    ClientFrame client = new ClientFrame(cId);
                                } catch (Exception e1) {
                                    JOptionPane.showMessageDialog(this, "Error While Loading Account", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid password", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid ID or password", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException | NoSuchAlgorithmException ex) {
                    JOptionPane.showMessageDialog(this, "Error executing query", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == exitButton) {
            // Exit program
            if (isConnected) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Could not disconnect from database", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new BankDB();
    }
}

import java.awt.*;
import java.sql.*;
import javax.swing.*;

/*
 * Class composed of functions necessary to run the login panel
 * 
 * @author Landon Delgado
 */
public class LoginPanel extends JFrame{
    /*
    * Creates the login panel
    * 
    * @return Returns the login panel
    */
    public JPanel createLoginPanel() {
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/images/bobabackground.png"));
        Image backgroundImage = backgroundIcon.getImage();

        // Set up background
        JPanel panel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        };
        setTitle("User Login");
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new   Font("Arial", Font.BOLD, 18));
        JTextField userField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JPasswordField passField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Add form components to the panel
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(userField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        // Add action listener for login button
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (authenticateUser(username, password)) {
                userField.setText("");
                passField.setText("");
                GUI.cardLayout.show(GUI.cardPanel, "Menu");
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add form panel to the main panel
        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    /*
    * Authenticates the user from the database
    * 
    * @param username The username entered into login panel
    * @param password Password entered into login panel
    * @return Boolean based on if the user was authenticated
    */
    private boolean authenticateUser(String username, String password) {
        // Set up connection parameters
        Connection conn = null;
        String database_name = "team_74_db";
        String database_user = "team_74";
        String database_password = "alka";
        String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);

        try {
        conn = DriverManager.getConnection(database_url, database_user, database_password);
        
        // Set up and execute query
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet result = stmt.executeQuery();

        if(result.next()){
            conn.close();
            boolean isManager = result.getString("ismanager").equalsIgnoreCase("t");
            GUI.currUser = new User(username, password, isManager);
            return true;
        }

        conn.close();
        return false;

        } catch (Exception e){
        JOptionPane.showMessageDialog(null, "Error accessing Database: " + e);
        }

        return false;
    }
}
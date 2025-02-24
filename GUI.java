import java.sql.*;
import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
  private CardLayout cardLayout;
  private JPanel cardPanel;

  public GUI()
  {
    //Building the connection
    Connection conn = null;

    String database_name = "team_74_db";
    String database_user = "team_74";
    String database_password = "alka";
    String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);
    try {
      conn = DriverManager.getConnection(database_url, database_user, database_password);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName()+": "+e.getMessage());
      System.exit(0);
    }
    JOptionPane.showMessageDialog(null,"Opened database successfully");

    // TODO: Fill with panel swapping logic

    setTitle("ShareTea");
    setSize(600, 400);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Initialize CardLayout
    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    // Create different panels (frames)
    JPanel mainMenuPanel = createMenu();
    JPanel analyticsPanel = createInventory();
    JPanel inventoryPanel = createAnalytics();

    // Add panels to card layout
    cardPanel.add(mainMenuPanel, "Menu");
    cardPanel.add(analyticsPanel, "Analytics");
    cardPanel.add(inventoryPanel, "Inventory");

    add(cardPanel);
    setVisible(true);

    //closing the connection
    try {
      conn.close();
      JOptionPane.showMessageDialog(null,"Connection Closed.");
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
    }
  }

  private JPanel createMenu(){
    JPanel panel = new JPanel(new BorderLayout());
    
    return panel;
  }

  private JPanel createInventory(){
    JPanel panel = new JPanel(new BorderLayout());

    return panel;
  }

  private JPanel createAnalytics(){
    JPanel panel = new JPanel(new BorderLayout());

    return panel;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new GUI());
  }

}

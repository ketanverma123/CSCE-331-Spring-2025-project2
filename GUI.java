import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class GUI extends JFrame {
  private CardLayout cardLayout;
  private JPanel cardPanel;
  private JTable analyticsTable;
  private DefaultTableModel analyticsTableModel;
  private JLabel timeLabel;
  private User currUser = new User("Null", "Null", false);

  public static class User {
    String username;
    String password;
    boolean isManager;

    public User(String username, String password, boolean isManager) {
      this.username = username;
      this.password = password;
      this.isManager = isManager;
    }
  }
  // Main framework
  public GUI() {
    Connection conn = null;
    String database_name = "team_74_db";
    String database_user = "team_74";
    String database_password = "alka";
    String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);

    try {
      conn = DriverManager.getConnection(database_url, database_user, database_password);
      JOptionPane.showMessageDialog(null, "Opened database successfully (Analytics).");
    } 
    catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }

    try {
      if (conn != null) conn.close();
    } 
    catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Connection NOT Closed.");
    }
    // Setting title
    setTitle("ShareTea Analytics");
    setSize(1200, 750);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    // Panel Creation
    JPanel analyticsPanel = createAnalytics();  // the main analytics panel
    JPanel inventoryPanel = createInventory();  // placeholder if you want to switch
    JPanel mainMenuPanel = createMenu();

    cardPanel.add(analyticsPanel, "Analytics");
    cardPanel.add(inventoryPanel, "Inventory");
    cardPanel.add(mainMenuPanel,  "Menu");

    add(cardPanel);
    setVisible(true);
  }

  private JPanel createMenu() {
    JPanel panel = new JPanel(new BorderLayout());
    JLabel lbl = new JLabel("Main Menu (placeholder)", SwingConstants.CENTER);
    lbl.setFont(new Font("Arial", Font.BOLD, 24));
    panel.add(lbl, BorderLayout.CENTER);
    return panel;
  }

  private JPanel createInventory() {
    JPanel panel = new JPanel(new BorderLayout());
    JLabel lbl = new JLabel("Inventory Panel (placeholder)", SwingConstants.CENTER);
    lbl.setFont(new Font("Arial", Font.BOLD, 24));
    panel.add(lbl, BorderLayout.CENTER);
    return panel;
  }
  
  // Creates analytics panel
  private JPanel createAnalytics() {
    ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/bobabackground.png"));
    Image bgImage = bgIcon.getImage();

    JPanel panel = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
      }
    };

    // Top bar creation
    JPanel topBar = new JPanel(new BorderLayout());
    topBar.setOpaque(false);

    JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    navPanel.setOpaque(false);

    JButton menuButton = createNavButton("./images/home.png", "Main Menu");
    menuButton.addActionListener(e -> cardLayout.show(cardPanel, "Menu"));

    JButton inventoryButton = createNavButton("./images/inventory.png", "Inventory");
    inventoryButton.addActionListener(e -> {
      if (currUser.isManager) {
        cardLayout.show(cardPanel, "Inventory");
      } else {
        JOptionPane.showMessageDialog(this, "Access Denied: Manager only.", "Restricted", JOptionPane.WARNING_MESSAGE);
      }
    });

    JButton analyticsButton = createNavButton("./images/analytics.png", "Analytics");
    analyticsButton.addActionListener(e -> cardLayout.show(cardPanel, "Analytics"));

    //adding buttons
    navPanel.add(menuButton);
    navPanel.add(inventoryButton);
    navPanel.add(analyticsButton);

    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightPanel.setOpaque(false);

    timeLabel = new JLabel();
    timeLabel.setFont(new Font("Arial", Font.BOLD, 24));
    timeLabel.setForeground(Color.WHITE);

    // timer
    Timer timer = new Timer(1000, evt -> updateTime());
    timer.start();
    updateTime(); 

    JButton logoutButton = new JButton("Logout");
    logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
    logoutButton.addActionListener(e -> logoutUser());

    rightPanel.add(timeLabel);
    rightPanel.add(logoutButton);

    topBar.add(navPanel, BorderLayout.WEST);
    topBar.add(rightPanel, BorderLayout.EAST);

    panel.add(topBar, BorderLayout.NORTH);

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
    centerPanel.setOpaque(false);

    JLabel analyticsTitle = new JLabel("Analytics", SwingConstants.CENTER);
    analyticsTitle.setFont(new Font("Arial", Font.BOLD, 40));
    analyticsTitle.setForeground(Color.WHITE);
    analyticsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

    analyticsTableModel = new DefaultTableModel();
    analyticsTable = new JTable(analyticsTableModel) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    analyticsTable.setRowHeight(30);
    analyticsTable.setShowGrid(false);
    analyticsTable.setIntercellSpacing(new Dimension(0, 0));
    JScrollPane scrollPane = new JScrollPane(analyticsTable);
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);

    loadAnalyticsDataFromDatabase();

    analyticsTable.setBackground(new Color(255, 255, 255, 200));
    analyticsTable.setOpaque(false);
    JTableHeader header = analyticsTable.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 20));
    header.setForeground(Color.BLACK);
    header.setBackground(new Color(255, 255, 255));
    header.setOpaque(true);

    centerPanel.add(analyticsTitle);
    panel.add(centerPanel, BorderLayout.CENTER);
    panel.add(scrollPane, BorderLayout.SOUTH);

    return panel;
  }

  //data for queries
  private void loadAnalyticsDataFromDatabase() {
    analyticsTableModel.setRowCount(0); 

    String database_name = "team_74_db";
    String database_user = "team_74";
    String database_password = "alka";
    String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);

    String sql =
      "WITH categorytot AS ( " +
      "  SELECT category, COUNT(*) AS totcnt, SUM(saleprice) AS salecnt " +
      "  FROM Sales " +
      "  GROUP BY category " +
      "), mostpplr AS ( " +
      "  SELECT category, itemname, COUNT(*) AS itemcnt, " +
      "         ROW_NUMBER() OVER (PARTITION BY category ORDER BY COUNT(*) DESC) AS rn " +
      "  FROM Sales " +
      "  GROUP BY category, itemname " +
      ") " +
      "SELECT cs.category, " +
      "       cs.totcnt AS \"Sales Qty\", " +
      "       cs.salecnt AS \"Sales\", " +
      "       ts.itemname AS \"Top Seller\", " +
      "       (ts.itemcnt::numeric / cs.totcnt * 100) AS \"Top Seller %Sales\" " +
      "FROM categorytot cs " +
      "JOIN mostpplr ts ON cs.category = ts.category " +
      "WHERE ts.rn = 1;";

    try (Connection conn = DriverManager.getConnection(database_url, database_user, database_password);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      String[] columns = {"Category","Sales Qty","Sales","Top Seller","Top Seller %Sales"};
      analyticsTableModel.setColumnIdentifiers(columns);

      while (rs.next()) {
        String category  = rs.getString("category");
        int salesQty     = rs.getInt("Sales Qty");
        double sales     = rs.getDouble("Sales");
        String topSeller = rs.getString("Top Seller");
        double pct       = rs.getDouble("Top Seller %Sales");

        analyticsTableModel.addRow(new Object[] {
            category,
            salesQty,
            "$" + String.format("%.2f", sales),
            topSeller,
            String.format("%.2f%%", pct)
        });
      }
      DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
      centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
      for (int i = 0; i < analyticsTable.getColumnCount(); i++) {
        analyticsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
      }

    } catch (SQLException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(this, "Error retrieving analytics data.");
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new GUI());
  }
}

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class GUI extends JFrame {
  private CardLayout cardLayout;
  private JPanel cardPanel;

  private JTable inventoryTable;
  private DefaultTableModel inventoryTableModel;
  
  private JLabel timeLabel;

  private Vector<Item> order = new Vector<>();

  // Item class for all products
  public static class Item {
    int id;
    String name;
    String category;
    double price;

    public Item(int id, String name, String category, double price) {
      this.id = id;
      this.name = name;
      this.category = category;
      this.price = price;
    }

    // For debugging
    @Override
    public String toString() {
      return "Item{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", category='" + category + '\'' +
              ", price=" + price +
              '}';
    }
  }

  // Main handler to switch between panels
  public GUI()
  {
    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    JPanel mainMenuPanel = createMenu();
    JPanel analyticsPanel = createAnalytics();
    JPanel inventoryPanel = createInventory();

    cardPanel.add(inventoryPanel, "Inventory");
    cardPanel.add(analyticsPanel, "Analytics");
    cardPanel.add(mainMenuPanel, "Menu");

    add(cardPanel);
    setVisible(true);
  }

  private JPanel createMenu(){
    JPanel panel = new JPanel(new BorderLayout());

    return panel;
  }

  private JPanel createAnalytics(){
    JPanel panel = new JPanel(new BorderLayout());

    return panel;
  }

  // Creates Inventory Panel
  private JPanel createInventory(){
    ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/images/bobabackground.png"));
    Image backgroundImage = backgroundIcon.getImage();

    // Set up panel with custom background
    JPanel panel = new JPanel(new BorderLayout()) {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
      }
    };
    setTitle("Sharetea Inventory");
    setSize(1200, 750);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Add top bar for buttons
    JPanel topBar = new JPanel(new BorderLayout());
    topBar.setOpaque(false);

    // Add button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.setOpaque(false);
    JButton menuButton = createButton("./images/home.png", "Main Menu");
    JButton inventoryButton = createButton("./images/inventory.png", "Inventory");
    JButton analyticsButton = createButton("./images/analytics.png", "Analytics");

    // Add functionality to buttons
    menuButton.addActionListener(e->cardLayout.show(cardPanel,"Menu"));
    inventoryButton.addActionListener(e -> cardLayout.show(cardPanel, "Inventory"));
    analyticsButton.addActionListener(e -> cardLayout.show(cardPanel, "Analytics"));

    // Add buttons to button panel
    buttonPanel.add(menuButton);
    buttonPanel.add(inventoryButton);
    buttonPanel.add(analyticsButton);

    // Set up clock
    timeLabel = new JLabel();
    timeLabel.setFont(new Font("Arial", Font.BOLD, 24));
    timeLabel.setForeground(Color.WHITE);
    Timer timer = new Timer(1000, e -> updateTime());
    timer.start();
    startClock();
    
    // Add time and buttons to top bar
    topBar.add(buttonPanel, BorderLayout.WEST);
    topBar.add(timeLabel, BorderLayout.EAST);

    // Set up inventory title
    JLabel inventoryTitle = new JLabel("Inventory", SwingConstants.CENTER);
    inventoryTitle.setFont(new Font("Arial", Font.BOLD, 40));
    inventoryTitle.setForeground(Color.WHITE);
    inventoryTitle.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0)); 

    // Set up table
    inventoryTableModel = new DefaultTableModel();
    inventoryTable = new JTable(inventoryTableModel) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    inventoryTable.setShowGrid(false);
    inventoryTable.setIntercellSpacing(new Dimension(0, 0));
    inventoryTable.setRowHeight(30);
    inventoryTable.setBackground(new Color(255, 255, 255, 200));
    inventoryTable.setOpaque(false);
    JScrollPane scrollPane = new JScrollPane(inventoryTable);
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    loadInventoryDataFromDatabase();

    // Add everything to panel
    panel.add(topBar, BorderLayout.NORTH);
    panel.add(inventoryTitle, BorderLayout.CENTER);
    panel.add(scrollPane, BorderLayout.SOUTH);

    return panel;
  }

  // Used in createInventory to fill table with database values
  private void loadInventoryDataFromDatabase() {
    // Set up connection parameters
    Connection conn = null;
    String database_name = "team_74_db";
    String database_user = "team_74";
    String database_password = "alka";
    String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);
    try {
      conn = DriverManager.getConnection(database_url, database_user, database_password);
      
      // Set up and execute query
      Statement stmt = conn.createStatement();
      String query = "WITH WeeklyUsage AS (\n" +
                "    SELECT \n" +
                "        i.category,\n" +
                "        COUNT(s.itemid) AS weekly_usage\n" +
                "    FROM Sales s\n" +
                "    JOIN Inventory i ON s.itemid = i.itemid\n" +
                "    WHERE s.saledate >= CURRENT_DATE - INTERVAL '7 days'\n" +
                "    GROUP BY i.category\n" +
                "),\n" +
                "LowStockItems AS (\n" +
                "    SELECT \n" +
                "        category,\n" +
                "        itemname\n" +
                "    FROM Inventory\n" +
                "    WHERE stock < 10\n" +
                ")\n" +
                "SELECT \n" +
                "    i.category AS \"Category\",\n" +
                "    SUM(i.stock) AS \"Stock\",\n" +
                "    wu.weekly_usage AS \"Previous Week Usage\",\n" +
                "    COALESCE(STRING_AGG(DISTINCT ls.itemname, ', '), 'None') AS \"Low Stock Items\"\n" +
                "FROM Inventory i\n" +
                "LEFT JOIN WeeklyUsage wu ON i.category = wu.category\n" +
                "LEFT JOIN LowStockItems ls ON i.category = ls.category\n" +
                "GROUP BY i.category, wu.weekly_usage\n" +
                "ORDER BY i.category;";
      ResultSet result = stmt.executeQuery(query);
      
      // Obtain data from query
      ResultSetMetaData metaData = result.getMetaData();
      int colCount = metaData.getColumnCount();
      String[] colNames = new String[colCount];
      for(int i = 1; i <= colCount; ++i){
        colNames[i-1] = metaData.getColumnName(i);
      }
      inventoryTableModel.setColumnIdentifiers(colNames);
      while(result.next()){
        Object[] rowData = new Object[colCount];
        for(int i = 1; i <= colCount; ++i){
          rowData[i-1] = result.getObject(i);
        }
        inventoryTableModel.addRow(rowData);
      }
      
      // Set style for table
      DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
      centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
      for (int i = 0; i < inventoryTable.getColumnCount(); i++) {
        inventoryTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
      }
      JTableHeader header = inventoryTable.getTableHeader();
      header.setFont(new Font("Arial", Font.BOLD, 20));
      header.setForeground(Color.BLACK);
      header.setBackground(new Color(255, 255, 255));
      header.setOpaque(true);
      
      conn.close();
    } catch (Exception e){
      JOptionPane.showMessageDialog(null, "Error accessing Database: " + e);
    }
  }

  private void startClock() {
    Timer timer = new Timer(1000, e -> updateTime());
    timer.start();
  }

  private void updateTime() {
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
    String currentTime = sdf.format(new Date());
    timeLabel.setText(currentTime);
  }

  // Function to make button from image and label
  private JButton createButton(String imgPath, String label){
    ImageIcon analyticsIcon = new ImageIcon(imgPath);
    Image img = analyticsIcon.getImage().getScaledInstance(60,60,1);
    ImageIcon scaledIcon = new ImageIcon(img);

    JButton button = new JButton(label,scaledIcon);

    button.setHorizontalTextPosition(SwingConstants.CENTER);
    button.setVerticalTextPosition(SwingConstants.BOTTOM);
    button.setFont(new Font("Arial", Font.BOLD, 10));
    button.setForeground(Color.WHITE);
    
    Dimension buttonSize = new Dimension(90,100);
    button.setPreferredSize(buttonSize);
    button.setMinimumSize(buttonSize);
    button.setMaximumSize(buttonSize);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setContentAreaFilled(false);

    return button;
  }

  // Puts menu item into order vector given an id
  private void addItemToOrder(Integer id){
    
    // Set up connection parameters
    Connection conn = null;
    String database_name = "team_74_db";
    String database_user = "team_74";
    String database_password = "alka";
    String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);

    try {
      conn = DriverManager.getConnection(database_url, database_user, database_password);
      
      // Set up and execute query
      String query = "SELECT name, category, price FROM Menu WHERE id = ?";
      PreparedStatement pstmt = conn.prepareStatement(query);
      pstmt.setInt(1, id);
      ResultSet result = pstmt.executeQuery();
      
      // Get item data from query
      if (result.next()) {
        String name = result.getString("name");
        String category = result.getString("category");
        double price = result.getDouble("price");

        Item item = new Item(id, name, category, price);

        // Add item to order
        order.add(item);
        
        // For debugging
        System.out.println("Item added to order: " + item);
      } else {
        // For debugging
        System.out.println("Item with ID " + id + " not found in Menu.");

        JOptionPane.showMessageDialog(null, "Item with ID " + id + " not found in Menu.");
      }
      
      conn.close();
    } catch (Exception e){
      JOptionPane.showMessageDialog(null, "Error accessing Database: " + e);
    }
  }

  // Empties order vector and updates inventory and sales tables in database
  private void checkout(){
    // Dont proceed with logic if empty order
    if(order.isEmpty()){
      JOptionPane.showMessageDialog(null, "Order Empty");
      return;
    }

    // Set up connection parameters
    Connection conn = null;
    String database_name = "team_74_db";
    String database_user = "team_74";
    String database_password = "alka";
    String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);

    try {
      conn = DriverManager.getConnection(database_url, database_user, database_password);
      conn.setAutoCommit(false);
      
      // Set up query to be filled in
      String insertSaleQuery = "INSERT INTO Sales (itemid, itemname, category, saleprice, saledate, saletime, customerid) " +
                                "VALUES (?, ?, ?, ?, CURRENT_DATE, CURRENT_TIME, ?)";
      String updateInventoryQuery = "UPDATE Inventory SET stock = stock - 1 WHERE itemid = ?";
      PreparedStatement insertSaleStmt = conn.prepareStatement(insertSaleQuery);
      PreparedStatement updateInventoryStmt = conn.prepareStatement(updateInventoryQuery);

      // Create queries
      while (!order.isEmpty()) {
        Item item = order.remove(0);

        insertSaleStmt.setInt(1, item.id);
        insertSaleStmt.setString(2, item.name);
        insertSaleStmt.setString(3, item.category);
        insertSaleStmt.setDouble(4, item.price);
        insertSaleStmt.setInt(5, ThreadLocalRandom.current().nextInt(9000, 10000));
        insertSaleStmt.executeUpdate();

        updateInventoryStmt.setInt(1, item.id);
        updateInventoryStmt.executeUpdate();

        System.out.println("Item checked out: " + item);
      }

      // Execute Queries
      conn.commit();
      System.out.println("Checkout complete!");
      
      conn.close();
    } catch (Exception e){
      JOptionPane.showMessageDialog(null, "Error accessing Database: " + e);
    }
  }

  public static void setGlobalFont(Font font) {
    UIManager.getDefaults().keySet().forEach(key -> {
      if (key.toString().toLowerCase().contains("font")) {
        UIManager.put(key, font);
      }
    });
  }

  public static void main(String[] args) {
    setGlobalFont(new Font("Arial", Font.BOLD, 12));
    SwingUtilities.invokeLater(() -> new GUI());
  }

}

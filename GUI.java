import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class GUI extends JFrame {
  private CardLayout cardLayout;
  private JPanel cardPanel;

  private JTable analyticsTable;
  private DefaultTableModel analyticsTableModel;

  private JTable inventoryTable;
  private DefaultTableModel inventoryTableModel;
  
  private JLabel timeLabel;

  private Vector<Item> order = new Vector<>();

  private DefaultListModel<String> orderListModel = new DefaultListModel<>();
  private JPanel orderPanel;
  private JLabel totalPriceLabel;
  private double totalPrice = 0.0;

  private User currUser = new User("Null","Null",false);

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

  public static class User{
    String username;
    String password;
    boolean isManager = false;

    public User(String username, String password, boolean isManager){
      this.username = username;
      this.password = password;
      this.isManager = isManager;
    }
  }

  // Main handler to switch between panels
  public GUI()
  {
    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    JPanel loginPanel = createLoginPanel();
    JPanel mainMenuPanel = createMenu();
    JPanel analyticsPanel = createAnalytics();
    JPanel inventoryPanel = createInventory();

    cardPanel.add(loginPanel, "Login");
    cardPanel.add(mainMenuPanel, "Menu");
    cardPanel.add(inventoryPanel, "Inventory");
    cardPanel.add(analyticsPanel, "Analytics");

    add(cardPanel);
    setVisible(true);
  }

  private JPanel createMenu() {
    int windowWidth = 1200;
    int windowHeight = 750;

    ImageIcon backgroundIcon = new ImageIcon(".\\images\\bobabackground.png");
    Image scaledImage = backgroundIcon.getImage().getScaledInstance(windowWidth, windowHeight, Image.SCALE_SMOOTH);
    ImageIcon resizedIcon = new ImageIcon(scaledImage);

    JLabel backgroundLabel = new JLabel(resizedIcon);
    backgroundLabel.setBounds(0, 0, windowWidth, windowHeight);


    JLayeredPane layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(new Dimension(windowWidth, windowHeight));

    // Add top bar for buttons
    JPanel topBar = new JPanel(new BorderLayout());
    topBar.setOpaque(false);
    topBar.setOpaque(false);

    // Add button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.setOpaque(false);
    buttonPanel.setOpaque(false);
    JButton menuButton = createButton("./images/home.png", "Main Menu");
    JButton inventoryButton = createButton("./images/inventory.png", "Inventory");
    JButton analyticsButton = createButton("./images/analytics.png", "Analytics");

    // Add functionality to buttons
    menuButton.addActionListener(e->cardLayout.show(cardPanel,"Menu"));
    inventoryButton.addActionListener(e -> {
      if (currUser.isManager) {
        cardLayout.show(cardPanel, "Inventory");
      } else {
        JOptionPane.showMessageDialog(null, "Access Denied: You do not have permission to access Inventory.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
      }
    });
    analyticsButton.addActionListener(e -> {
      if (currUser.isManager) {
        cardLayout.show(cardPanel, "Analytics");
      } else {
        JOptionPane.showMessageDialog(null, "Access Denied: You do not have permission to access Analytics.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
      }
    });

    // Add buttons to button panel
    buttonPanel.add(menuButton);
    buttonPanel.add(inventoryButton);
    buttonPanel.add(analyticsButton);

    // Set up clock
    // Set up clock
    timeLabel = new JLabel();
    timeLabel.setFont(new Font("Arial", Font.BOLD, 24));
    timeLabel.setForeground(Color.WHITE);
    Timer timer = new Timer(1000, e -> updateTime());
    timer.start();
    startClock();

    // Create logout button
    JButton logoutButton = new JButton("Logout");
    logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
    logoutButton.addActionListener(e -> logoutUser());

    // Create panel for time and logout button
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightPanel.setOpaque(false);
    rightPanel.add(timeLabel);
    rightPanel.add(logoutButton);
    
    // Add time and buttons to top bar
    topBar.add(buttonPanel, BorderLayout.WEST);
    topBar.add(rightPanel, BorderLayout.EAST);

    JPanel menuPanel = new JPanel(new GridLayout(3, 5, 10, 10));
    menuPanel.setBounds(100, 100, (int) (windowWidth * 0.6), (int) (windowHeight * 0.7));
    menuPanel.setBackground(new Color(255, 255, 255, 220));
    menuPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

    ImageIcon brewed_tea = new ImageIcon(".\\images\\Brewed_Tea.png");
    Image adjust_size = brewed_tea.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    brewed_tea = new ImageIcon(adjust_size);

    ImageIcon milk_tea = new ImageIcon(".\\images\\Milk_Tea.png");
    adjust_size = milk_tea.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    milk_tea = new ImageIcon(adjust_size);

    ImageIcon fruit_tea = new ImageIcon(".\\images\\FruitTea.png");
    adjust_size = fruit_tea.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    fruit_tea = new ImageIcon(adjust_size);

    ImageIcon fresh_milk = new ImageIcon(".\\images\\Fresh_Milk.png");
    adjust_size = fresh_milk.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    fresh_milk = new ImageIcon(adjust_size);

    ImageIcon ice_blended = new ImageIcon(".\\images\\Ice_Blended.png");
    adjust_size = ice_blended.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    ice_blended = new ImageIcon(adjust_size);

    ImageIcon creama = new ImageIcon(".\\images\\Creama.png");
    adjust_size = creama.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    creama = new ImageIcon(adjust_size);

    ImageIcon tea_mojito = new ImageIcon(".\\images\\Tea_Mojito.png");
    adjust_size = tea_mojito.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    tea_mojito = new ImageIcon(adjust_size);

    ImageIcon ice_cream = new ImageIcon(".\\images\\Ice_Cream.png");
    adjust_size = ice_cream.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    ice_cream = new ImageIcon(adjust_size);

    ImageIcon newItem = new ImageIcon(".\\images\\New.png");
    adjust_size = newItem.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    newItem = new ImageIcon(adjust_size);

    ImageIcon rewards = new ImageIcon(".\\images\\Rewards.png");
    adjust_size = rewards.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    rewards = new ImageIcon(adjust_size);

    ImageIcon top_order = new ImageIcon(".\\images\\Top.png");
    adjust_size = top_order.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    top_order = new ImageIcon(adjust_size);

    ImageIcon second = new ImageIcon(".\\images\\Second.png");
    adjust_size = second.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    second = new ImageIcon(adjust_size);

    ImageIcon third = new ImageIcon(".\\images\\Third.png");
    adjust_size = third.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
    third = new ImageIcon(adjust_size);

    ImageIcon fourth = new ImageIcon(".\\images\\Fourth.png");
    adjust_size = fourth.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    fourth = new ImageIcon(adjust_size);

    ImageIcon gift_cards = new ImageIcon(".\\images\\Gift_Card.png");
    adjust_size = gift_cards.getImage().getScaledInstance(150, 120, Image.SCALE_SMOOTH);
    gift_cards = new ImageIcon(adjust_size);

    ImageIcon[] menu_items = {brewed_tea, milk_tea, fruit_tea, fresh_milk, ice_blended, creama, tea_mojito, ice_cream, newItem, rewards, top_order, second, third, fourth, gift_cards};
    String[] categories = {"Brewed Tea", "Milk Tea", "Fruit Tea", "Fresh Milk", "Ice Blended", "Creama", "Tea Mojito", "Ice Cream", "New_Item", "Rewards", "Top Order", "Second", "Third", "Fourth", "Gift Cards"};
    for(int i = 0; i < 15; i++)
    {
      JButton button = new JButton(menu_items[i]);
      final int index = i;
      button.addActionListener(e -> showPopup(categories[index]));

      menuPanel.add(button);
    }

    orderPanel = new JPanel();
    orderPanel.setBounds((int) (windowWidth * 0.7) + 20, 150, (int) (windowWidth * 0.25) - 40, (int) (windowHeight * 0.6));
    orderPanel.setBackground(Color.WHITE);
    orderPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

    JLabel orderTitle = new JLabel("Order", SwingConstants.CENTER);
    orderTitle.setFont(new Font("Arial", Font.BOLD, 24));
    orderTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    orderPanel.add(orderTitle, BorderLayout.NORTH);

    JList<String> orderList = new JList<>(orderListModel);
    orderList.setFont(new Font("Arial", Font.PLAIN, 10));
    JScrollPane orderScrollPane = new JScrollPane(orderList);
    orderPanel.add(orderScrollPane, BorderLayout.CENTER);

    totalPriceLabel = new JLabel("Total: $0.00", SwingConstants.CENTER);
    totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 18));
    orderPanel.add(totalPriceLabel, BorderLayout.SOUTH);

    JButton checkoutButton = new JButton("Checkout");
    checkoutButton.setFont(new Font("Arial", Font.BOLD, 18));
    checkoutButton.addActionListener(e -> checkout());
    orderPanel.add(checkoutButton, BorderLayout.SOUTH);

    // Add components to layered pane
    layeredPane.add(backgroundLabel, Integer.valueOf(0));
    layeredPane.add(topBar, Integer.valueOf(1));
    layeredPane.add(menuPanel, Integer.valueOf(1));
    layeredPane.add(orderPanel, Integer.valueOf(1));

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(layeredPane);

    return panel;
  }

  private void updateTotalPrice(double price) {
      totalPrice += price;
      totalPriceLabel.setText(String.format("Total: $%.2f", totalPrice));
      orderPanel.revalidate();  // Recalculate layout
      orderPanel.repaint();
  }

  private void showPopup(String category) {
      Connection conn = null;
      String database_name = "team_74_db";
      String database_user = "team_74";
      String database_password = "alka";
      String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);

      try {
        conn = DriverManager.getConnection(database_url, database_user, database_password);
        
        // Set up and execute query
        String query = "SELECT name, id, price FROM Menu WHERE category = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, category);
        ResultSet result = pstmt.executeQuery();

        JDialog popup = new JDialog(this, "Select an Item", true);
        popup.setSize(400, 300);
        popup.setLayout(new GridLayout(0, 1));

        while (result.next()) 
        {
          int itemId = result.getInt("id");
          String itemName = result.getString("name");

          JButton itemButton = new JButton(itemName);
          itemButton.addActionListener(e -> {
              addItemToOrder(itemId);  // Add the item to the order when clicked
              popup.dispose();  // Close popup after selection
          });

          popup.add(itemButton);
        }
        popup.setLocationRelativeTo(this);
        popup.setVisible(true);
        conn.close();
      } catch (Exception e){
        JOptionPane.showMessageDialog(null, "Error accessing Database: " + e);
      }
  }

  // Creates Login Panel
  private JPanel createLoginPanel() {
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
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            cardLayout.show(cardPanel, "Menu");
        } else {
            JOptionPane.showMessageDialog(panel, "Invalid Username or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    });

    // Add form panel to the main panel
    panel.add(formPanel, BorderLayout.CENTER);

    return panel;
  }

  // Authenticates user login from database
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
        currUser = new User(username, password, isManager);
        return true;
      }

      conn.close();
      return false;

    } catch (Exception e){
      JOptionPane.showMessageDialog(null, "Error accessing Database: " + e);
    }

    return false;
  }

  // Logs user out
  private void logoutUser(){
    cardLayout.show(cardPanel,"Login");
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

    JButton menuButton = createButton("./images/home.png", "Main Menu");
    menuButton.addActionListener(e -> cardLayout.show(cardPanel, "Menu"));

    JButton inventoryButton = createButton("./images/inventory.png", "Inventory");
    inventoryButton.addActionListener(e -> {
      if (currUser.isManager) {
        cardLayout.show(cardPanel, "Inventory");
      } else {
        JOptionPane.showMessageDialog(this, "Access Denied: Manager only.", "Restricted", JOptionPane.WARNING_MESSAGE);
      }
    });

    JButton analyticsButton = createButton("./images/analytics.png", "Analytics");
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
    topBar.setOpaque(false);

    // Add button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.setOpaque(false);
    buttonPanel.setOpaque(false);
    JButton menuButton = createButton("./images/home.png", "Main Menu");
    JButton inventoryButton = createButton("./images/inventory.png", "Inventory");
    JButton analyticsButton = createButton("./images/analytics.png", "Analytics");

    // Add functionality to buttons
    menuButton.addActionListener(e->cardLayout.show(cardPanel,"Menu"));
    inventoryButton.addActionListener(e -> {
      if (currUser.isManager) {
        cardLayout.show(cardPanel, "Inventory");
      } else {
        JOptionPane.showMessageDialog(null, "Access Denied: You do not have permission to access Inventory.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
      }
    });
    analyticsButton.addActionListener(e -> {
      if (currUser.isManager) {
        cardLayout.show(cardPanel, "Analytics");
      } else {
        JOptionPane.showMessageDialog(null, "Access Denied: You do not have permission to access Analytics.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
      }
    });

    // Add buttons to button panel
    buttonPanel.add(menuButton);
    buttonPanel.add(inventoryButton);
    buttonPanel.add(analyticsButton);

    // Set up clock
    // Set up clock
    timeLabel = new JLabel();
    timeLabel.setFont(new Font("Arial", Font.BOLD, 24));
    timeLabel.setForeground(Color.WHITE);
    Timer timer = new Timer(1000, e -> updateTime());
    timer.start();
    startClock();

    // Create logout button
    JButton logoutButton = new JButton("Logout");
    logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
    logoutButton.addActionListener(e -> logoutUser());

    // Create panel for time and logout button
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightPanel.setOpaque(false);
    rightPanel.add(timeLabel);
    rightPanel.add(logoutButton);
    
    // Add time and buttons to top bar
    topBar.add(buttonPanel, BorderLayout.WEST);
    topBar.add(rightPanel, BorderLayout.EAST);

    // Set up inventory title
    JLabel inventoryTitle = new JLabel("Inventory", SwingConstants.CENTER);
    inventoryTitle.setFont(new Font("Arial", Font.BOLD, 40));
    inventoryTitle.setForeground(Color.WHITE);
    inventoryTitle.setHorizontalAlignment(SwingConstants.CENTER);
    inventoryTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

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

    // Create bottom button panel
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton addStockButton = new JButton("Add Stock");
    addStockButton.addActionListener(e -> addStock());
    JButton createItemButton = new JButton("Create Item");
    createItemButton.addActionListener(e -> createItem());
    JButton editItemButton = new JButton("Edit Item");
    editItemButton.addActionListener(e -> editItem());
    JButton deleteItemButton = new JButton("Delete Item");
    deleteItemButton.addActionListener(e -> deleteMenuItem());
    JButton addEmployeeButton = new JButton("Add Employee");
    addEmployeeButton.addActionListener(e -> addUser());
    JButton editEmployeeButton = new JButton("Edit Employee");
    editEmployeeButton.addActionListener(e -> editUser());
    JButton removeEmployeeButton = new JButton("Remove Employee");
    removeEmployeeButton.addActionListener(e -> removeUser());
    bottomPanel.add(addStockButton);
    bottomPanel.add(createItemButton);
    bottomPanel.add(editItemButton);
    bottomPanel.add(deleteItemButton);
    bottomPanel.add(addEmployeeButton);
    bottomPanel.add(editEmployeeButton);
    bottomPanel.add(removeEmployeeButton);
    bottomPanel.setOpaque(false);

    // Create Inventory and Buttons Panel
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
    centerPanel.add(inventoryTitle);
    centerPanel.add(bottomPanel);
    centerPanel.setOpaque(false);

    // Add everything to panel
    panel.add(topBar, BorderLayout.NORTH);
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

  // Used in createInventory to fill table with database values
  private void loadInventoryDataFromDatabase() {
    inventoryTableModel.setRowCount(0);
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

  // Allows user to add stock to inventory
  private void addStock() {
    // Prompt the user for input
    String itemName = JOptionPane.showInputDialog(null, "Enter item name:", "Add Stock", JOptionPane.QUESTION_MESSAGE);
    if (itemName == null || itemName.trim().isEmpty()) return;

    String amountStr = JOptionPane.showInputDialog(null, "Enter amount to add:", "Add Stock", JOptionPane.QUESTION_MESSAGE);
    if (amountStr == null || amountStr.trim().isEmpty()) return;

    try {
      double amount = Double.parseDouble(amountStr);
      if (amount <= 0) {
        JOptionPane.showMessageDialog(null, "Amount must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Update stock in the database
      if (updateStockInDatabase(itemName, amount)) {
        // Refresh GUI table after updating database
        loadInventoryDataFromDatabase();
        JOptionPane.showMessageDialog(null, "Stock updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null, "Item not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
      }
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(null, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  // Updates the stock value in the database
  private boolean updateStockInDatabase(String itemName, double amount) {
    String databaseUrl = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_74_db";
    String databaseUser = "team_74";
    String databasePassword = "alka";

    String updateQuery = "UPDATE Inventory SET stock = stock + ? WHERE itemname = ?";

    try (Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
      PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

      stmt.setDouble(1, amount);
      stmt.setString(2, itemName);

      int rowsUpdated = stmt.executeUpdate();
      return rowsUpdated > 0; // If at least one row is updated, return true

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  // Allows user to create new menu item
  private void createItem() {
    // Prompt user for item details
    String idStr = JOptionPane.showInputDialog(null, "Enter item ID:", "Create Item", JOptionPane.QUESTION_MESSAGE);
    if (idStr == null || idStr.trim().isEmpty()) return;

    String itemName = JOptionPane.showInputDialog(null, "Enter item name:", "Create Item", JOptionPane.QUESTION_MESSAGE);
    if (itemName == null || itemName.trim().isEmpty()) return;

    String category = JOptionPane.showInputDialog(null, "Enter category:", "Create Item", JOptionPane.QUESTION_MESSAGE);
    if (category == null || category.trim().isEmpty()) return;

    String priceStr = JOptionPane.showInputDialog(null, "Enter price:", "Create Item", JOptionPane.QUESTION_MESSAGE);
    if (priceStr == null || priceStr.trim().isEmpty()) return;

    try {
      int itemId = Integer.parseInt(idStr);
      double price = Double.parseDouble(priceStr);

      if (price < 0) {
        JOptionPane.showMessageDialog(null, "Price cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Check if item already exists in the database
      if (itemInMenu(itemId, itemName)) {
        JOptionPane.showMessageDialog(null, "Item ID or Name already exists in the menu.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Add item to database
      if (addItemToDatabase(itemId, itemName, category, price)) {
        // Update GUI table after database update
        loadInventoryDataFromDatabase();
        JOptionPane.showMessageDialog(null, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null, "Error adding item to database.", "Error", JOptionPane.ERROR_MESSAGE);
      }

    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(null, "Invalid input. ID must be an integer and price must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  // Checks if an item is already in the menu
  private boolean itemInMenu(int itemId, String itemName) {
    String databaseUrl = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_74_db";
    String databaseUser = "team_74";
    String databasePassword = "alka";

    String query = "SELECT * FROM Menu WHERE id = ? OR name = ?";

    try (Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
      PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setInt(1, itemId);
      stmt.setString(2, itemName);

      ResultSet result = stmt.executeQuery();
      return result.next();

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return true;
    }
  }

  // Adds new menu item to database
  private boolean addItemToDatabase(int itemId, String itemName, String category, double price) {
    String databaseUrl = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_74_db";
    String databaseUser = "team_74";
    String databasePassword = "alka";

    String menuQuery = "INSERT INTO Menu (id, name, category, price) VALUES (?, ?, ?, ?)";
    String inventoryQuery = "INSERT INTO Inventory (stock, itemid, itemname, category) VALUES (0, ?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
      PreparedStatement menuStmt = conn.prepareStatement(menuQuery);
      PreparedStatement inventoryStmt = conn.prepareStatement(inventoryQuery)) {

      // Insert into Menu table
      menuStmt.setInt(1, itemId);
      menuStmt.setString(2, itemName);
      menuStmt.setString(3, category);
      menuStmt.setDouble(4, price);
      menuStmt.executeUpdate();

      // Insert into Inventory table with 0 stock
      inventoryStmt.setInt(1, itemId);
      inventoryStmt.setString(2, itemName);
      inventoryStmt.setString(3, category);
      inventoryStmt.executeUpdate();

      return true;

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  // Allows user to delete item from menu
  private void deleteMenuItem() {
    // Prompt user for item ID
    String idStr = JOptionPane.showInputDialog(null, "Enter item ID to delete:", "Delete Item", JOptionPane.QUESTION_MESSAGE);
    if (idStr == null || idStr.trim().isEmpty()) return;

    String nameStr = JOptionPane.showInputDialog(null, "Enter item name to delete:", "Delete Item", JOptionPane.QUESTION_MESSAGE);
    if (nameStr == null || nameStr.trim().isEmpty()) return;

    try {
      int itemId = Integer.parseInt(idStr);

      // Check if the item exists
      if (!itemInMenu(itemId,nameStr)) {
          JOptionPane.showMessageDialog(null, "Item ID does not exist in the menu.", "Error", JOptionPane.ERROR_MESSAGE);
          return;
      }

      // Confirm deletion
      int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this item?", 
                                                  "Confirm Deletion", JOptionPane.YES_NO_OPTION);
      if (confirm != JOptionPane.YES_OPTION) return;

      // Delete from database
      if (deleteItemFromDatabase(itemId)) {
        loadInventoryDataFromDatabase();
        JOptionPane.showMessageDialog(null, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null, "Error deleting item from database.", "Error", JOptionPane.ERROR_MESSAGE);
      }

    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(null, "Invalid input. ID must be an integer.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  // Deletes an item from the database
  private boolean deleteItemFromDatabase(int itemId) {
    String databaseUrl = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_74_db";
    String databaseUser = "team_74";
    String databasePassword = "alka";

    String deleteInventoryQuery = "DELETE FROM Inventory WHERE itemid = ?";
    String deleteMenuQuery = "DELETE FROM Menu WHERE id = ?";

    try (Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
      PreparedStatement inventoryStmt = conn.prepareStatement(deleteInventoryQuery);
      PreparedStatement menuStmt = conn.prepareStatement(deleteMenuQuery)) {

      // Delete from Inventory table first
      inventoryStmt.setInt(1, itemId);
      inventoryStmt.executeUpdate();

      // Delete from Menu table
      menuStmt.setInt(1, itemId);
      int rowsAffected = menuStmt.executeUpdate();

      return rowsAffected > 0;

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  // Allows user to edit menu item
  private void editItem() {
    // Prompt user for item ID
    String idStr = JOptionPane.showInputDialog(null, "Enter the ID of the item to edit:", "Edit Item", JOptionPane.QUESTION_MESSAGE);
    if (idStr == null || idStr.trim().isEmpty()) return;

    String nameStr = JOptionPane.showInputDialog(null, "Enter item name to edit:", "Edit Item", JOptionPane.QUESTION_MESSAGE);
    if (nameStr == null || nameStr.trim().isEmpty()) return;

    try {
      int oldItemId = Integer.parseInt(idStr);

      // Check if the item exists
      if (!itemInMenu(oldItemId, nameStr)) {
        JOptionPane.showMessageDialog(null, "Item ID not found in the menu.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Prompt user for new values
      String newIdStr = JOptionPane.showInputDialog(null, "Enter new item ID:", "Edit Item", JOptionPane.QUESTION_MESSAGE);
      if (newIdStr == null || newIdStr.trim().isEmpty()) return;

      String newName = JOptionPane.showInputDialog(null, "Enter new item name:", "Edit Item", JOptionPane.QUESTION_MESSAGE);
      if (newName == null || newName.trim().isEmpty()) return;

      String newCategory = JOptionPane.showInputDialog(null, "Enter new category:", "Edit Item", JOptionPane.QUESTION_MESSAGE);
      if (newCategory == null || newCategory.trim().isEmpty()) return;

      String newPriceStr = JOptionPane.showInputDialog(null, "Enter new price:", "Edit Item", JOptionPane.QUESTION_MESSAGE);
      if (newPriceStr == null || newPriceStr.trim().isEmpty()) return;

      // Convert inputs
      int newItemId = Integer.parseInt(newIdStr);
      double newPrice = Double.parseDouble(newPriceStr);

      if (newPrice < 0) {
        JOptionPane.showMessageDialog(null, "Price cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Ensure the new ID or name is not taken (except for the item being edited)
      if (itemInMenu(newItemId, newName) && newItemId != oldItemId) {
        JOptionPane.showMessageDialog(null, "New item ID or name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Update the item in the database
      if (updateItemInDatabase(oldItemId, newItemId, newName, newCategory, newPrice)) {
        loadInventoryDataFromDatabase();
        JOptionPane.showMessageDialog(null, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null, "Error updating item in database.", "Error", JOptionPane.ERROR_MESSAGE);
      }

    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(null, "Invalid input. ID must be an integer and price must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  // Updates menu item in database
  private boolean updateItemInDatabase(int oldItemId, int newItemId, String newName, String newCategory, double newPrice) {
    String databaseUrl = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_74_db";
    String databaseUser = "team_74";
    String databasePassword = "alka";

    String updateMenuQuery = "UPDATE Menu SET id = ?, name = ?, category = ?, price = ? WHERE id = ?";
    String updateInventoryQuery = "UPDATE Inventory SET itemid = ?, itemname = ?, category = ? WHERE itemid = ?";

    try (Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
      PreparedStatement menuStmt = conn.prepareStatement(updateMenuQuery);
      PreparedStatement inventoryStmt = conn.prepareStatement(updateInventoryQuery)) {

      // Update Menu table
      menuStmt.setInt(1, newItemId);
      menuStmt.setString(2, newName);
      menuStmt.setString(3, newCategory);
      menuStmt.setDouble(4, newPrice);
      menuStmt.setInt(5, oldItemId);
      menuStmt.executeUpdate();

      // Update Inventory table
      inventoryStmt.setInt(1, newItemId);
      inventoryStmt.setString(2, newName);
      inventoryStmt.setString(3, newCategory);
      inventoryStmt.setInt(4, oldItemId);
      inventoryStmt.executeUpdate();

      return true;

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  // Allows user to add employee
  private void addUser() {
    // Prompt user for employee details
    String username = JOptionPane.showInputDialog(null, "Enter new employee username:", "Add Employee", JOptionPane.QUESTION_MESSAGE);
    if (username == null || username.trim().isEmpty()) return;

    String password = JOptionPane.showInputDialog(null, "Enter password:", "Add Employee", JOptionPane.QUESTION_MESSAGE);
    if (password == null || password.trim().isEmpty()) return;

    String[] options = {"Yes", "No"};
    int isManagerOption = JOptionPane.showOptionDialog(null, "Is this employee a manager?", "Add Employee",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
    
    boolean isManager = (isManagerOption == 0);

    // Check if the username already exists
    if (isUsernameTaken(username)) {
      JOptionPane.showMessageDialog(null, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Add employee to database
    if (addUserToDatabase(username, password, isManager)) {
      JOptionPane.showMessageDialog(null, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(null, "Error adding employee to database.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  // Makes sure username is not taken
  private boolean isUsernameTaken(String username) {
    String databaseUrl = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_74_db";
    String databaseUser = "team_74";
    String databasePassword = "alka";

    String query = "SELECT * FROM users WHERE username = ?";

    try (Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
      PreparedStatement stmt = conn.prepareStatement(query)) {

      stmt.setString(1, username);
      ResultSet result = stmt.executeQuery();
      return result.next();

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return true;
    }
  }

  // Adds the user into the database
  private boolean addUserToDatabase(String username, String password, boolean isManager) {
    String databaseUrl = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_74_db";
    String databaseUser = "team_74";
    String databasePassword = "alka";

    String insertQuery = "INSERT INTO users (username, password, ismanager) VALUES (?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
      PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

      stmt.setString(1, username);
      stmt.setString(2, password);
      stmt.setBoolean(3, isManager);

      int rowsInserted = stmt.executeUpdate();
      return rowsInserted > 0;

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  // Allows user to edit employee
  private void editUser() {
      // Prompt user for the employee's username to edit
      String oldUsername = JOptionPane.showInputDialog(null, "Enter the username of the employee to edit:", "Edit Employee", JOptionPane.QUESTION_MESSAGE);
      if (oldUsername == null || oldUsername.trim().isEmpty()) return;

      // Check if the username exists
      if (!isUsernameTaken(oldUsername)) {
        JOptionPane.showMessageDialog(null, "Username not found.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Prompt for new values
      String newUsername = JOptionPane.showInputDialog(null, "Enter new username:", "Edit Employee", JOptionPane.QUESTION_MESSAGE);
      if (newUsername == null || newUsername.trim().isEmpty()) return;

      String newPassword = JOptionPane.showInputDialog(null, "Enter new password:", "Edit Employee", JOptionPane.QUESTION_MESSAGE);
      if (newPassword == null || newPassword.trim().isEmpty()) return;

      String[] options = {"Yes", "No"};
      int isManagerOption = JOptionPane.showOptionDialog(null, "Is this employee a manager?", "Edit Employee",
              JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

      // Ensure a selection was made
      if (isManagerOption == JOptionPane.CLOSED_OPTION) return;

      boolean isManager = (isManagerOption == 0);

      // Ensure the new username is not already taken (unless unchanged)
      if (!oldUsername.equals(newUsername) && isUsernameTaken(newUsername)) {
        JOptionPane.showMessageDialog(null, "New username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Update the employee in the database
      if (updateUserInDatabase(oldUsername, newUsername, newPassword, isManager)) {
        JOptionPane.showMessageDialog(null, "Employee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(null, "Error updating employee in database.", "Error", JOptionPane.ERROR_MESSAGE);
      }
  }

  // Updates the employee in the database
  private boolean updateUserInDatabase(String oldUsername, String newUsername, String newPassword, boolean isManager) {
    String databaseUrl = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_74_db";
    String databaseUser = "team_74";
    String databasePassword = "alka";

    String updateQuery = "UPDATE users SET username = ?, password = ?, ismanager = ? WHERE username = ?";

    try (Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
      PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

      stmt.setString(1, newUsername);
      stmt.setString(2, newPassword);
      stmt.setBoolean(3, isManager);
      stmt.setString(4, oldUsername);

      int rowsUpdated = stmt.executeUpdate();
      return rowsUpdated > 0;

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  // Allows the user to remove an employee
  private void removeUser() {
    // Prompt user for the employee's username to delete
    String username = JOptionPane.showInputDialog(null, "Enter the username of the employee to remove:", "Remove Employee", JOptionPane.QUESTION_MESSAGE);
    if (username == null || username.trim().isEmpty()) return;

    // Check if the username exists
    if (!isUsernameTaken(username)) {
      JOptionPane.showMessageDialog(null, "Username not found.", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Confirm deletion
    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove this employee?", 
                                                "Confirm Removal", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) return;

    // Delete the employee from the database
    if (removeUserFromDatabase(username)) {
      JOptionPane.showMessageDialog(null, "Employee removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(null, "Error removing employee from database.", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  // Removes an employee from the database
  private boolean removeUserFromDatabase(String username) {
    String databaseUrl = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_74_db";
    String databaseUser = "team_74";
    String databasePassword = "alka";

    String deleteQuery = "DELETE FROM users WHERE username = ?";

    try (Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
      PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

      stmt.setString(1, username);
      int rowsDeleted = stmt.executeUpdate();
      return rowsDeleted > 0;

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return false;
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
        orderListModel.addElement(name + " - $" + price);
        updateTotalPrice(price);
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
      orderListModel.clear();
      totalPrice = 0.0;
      updateTotalPrice(0.0);
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
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/*
 * Class composed of functions necesary to run the Inventory panel
 * 
 * @author Landon Delgado
 */
public class Inventory extends JFrame{
    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;

    /*
    * Creates the inventory panel
    * 
    * @return Inventory panel
    */
    public JPanel createInventory(){
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/images/bobabackground.png"));
        Image backgroundImage = backgroundIcon.getImage();
        JLabel timeLabel;

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
        setLayout(new BorderLayout());

        // Add top bar for buttons
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setOpaque(false);

        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setOpaque(false);
        JButton menuButton = GUI.createButton("./images/home.png", "Main Menu");
        JButton inventoryButton = GUI.createButton("./images/inventory.png", "Inventory");
        JButton analyticsButton = GUI.createButton("./images/analytics.png", "Analytics");

        // Add functionality to buttons
        menuButton.addActionListener(e->GUI.cardLayout.show(GUI.cardPanel,"Menu"));
        inventoryButton.addActionListener(e -> {
        if (GUI.currUser.isManager) {
            GUI.cardLayout.show(GUI.cardPanel, "Inventory");
        } else {
            JOptionPane.showMessageDialog(null, "Access Denied: You do not have permission to access Inventory.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
        }
        });
        analyticsButton.addActionListener(e -> {
        if (GUI.currUser.isManager) {
            GUI.cardLayout.show(GUI.cardPanel, "Analytics");
        } else {
            JOptionPane.showMessageDialog(null, "Access Denied: You do not have permission to access Analytics.", "Access Restricted", JOptionPane.WARNING_MESSAGE);
        }
        });

        // Add buttons to button panel
        buttonPanel.add(menuButton);
        buttonPanel.add(inventoryButton);
        buttonPanel.add(analyticsButton);

        // Set up clock
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timeLabel.setForeground(Color.WHITE);
        Timer timer = new Timer(1000, e -> GUI.updateTime(timeLabel));
        timer.start();

        // Create logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.addActionListener(e -> GUI.logoutUser());

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

    /*
    * Loads and populates the inventory table from the database
    * 
    * @return none (void)
    */
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

    /*
    * Adds stock to inventory in database and updates local table
    * 
    * @return none (void)
    */
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

    /*
     * Add 'amount' number of stock for 'itemName' in the database
     * 
     * @param itemName Name of the item to be updated
     * @param amount Amount of stock to add
     * @return boolean Whether item was added successfully
     */
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

    /*
     * Displays a prompt to create a new menu item
     * 
     * @return none (void)
     */
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

    /*
     * Checks if an item is already on the menu
     * 
     * @param itemId The id of the item to check
     * @param itemName The name of the item to check
     * @return boolean True if item is already in the menu
     */
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

    /*
     * Inserts a new item into the database
     * 
     * @param itemId The id of the item to add
     * @param itemName The name of the item to add
     * @param category The category of the item to add
     * @param price The price of the item to add
     * @return boolean True if item was successfully added to the database
     */
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

    /*
     * Displays a prompt to delete an item from the menu
     * 
     * @return none (void)
     */
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

    /*
     * Deletes an item from the database
     * 
     * @param itemId The item to be deleted
     * @return boolean True if item was successfully deleted
     */
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

    /*
     * Prompts the user to edit a database item
     * 
     * @return none (void)
     */
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

    /*
     * Updates an item with new values in the database
     * 
     * @param oldItemId The id of the item to be changed
     * @param newItemId The id the item should be changed to
     * @param newName The name the item should be changed to
     * @param newCategory The category the item should be changed to
     * @param newPrice The price the item should be changed to
     * @return boolean True if the update was successful
     */
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

    /*
     * Prompts the user to add a new user
     * 
     * @return none (void)
     */
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

    /*
     * Checks if the given username already exists
     * 
     * @param username The username that should be checked
     * @return boolean True if the username is taken
     */
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

    /*
     * Adds a new user to the database
     * 
     * @param username The username of the user to be added
     * @param password The password of the user to be added
     * @param isManager Whether hte user is a manager or not
     * @return boolean True if the user is successfully added
     */
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

    /*
     * Prompts the user to edit an employees values
     * (*Note must be a manager to access this)
     * 
     * @return none (void)
     */
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

    /*
     * Updates user values in database
     * 
     * @param oldUsername The previous username that should be changed
     * @param newUsername The username the user should be changed to
     * @param newPassword The user's new password
     * @param isManager Whether or not the user is a manager
     * @return boolean True if the update was successful
     */
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

    /*
     * Prompts the user to remove another user
     * 
     * @return none (void)
     */
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

    /*
     * Removes a given employee from the database
     * 
     * @param username The username of the user to be removed
     * @return boolean True if the removal was successful
     */
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
}

import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.*;

public class Menu extends JFrame{
    //Global variables for calculating an individuals order for the GUI
    private Vector<Item> order = new Vector<>();
    private DefaultListModel<String> orderListModel = new DefaultListModel<>();
    private JPanel orderPanel;
    private JLabel totalPriceLabel;
    private double totalPrice = 0.0;

    /*
    * @author Ayush Shah
    * @return menu_panel
    */
    public JPanel createMenu() {
        int windowWidth = 1300;
        int windowHeight = 750;
        
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
        panel.setPreferredSize(new Dimension(1300, 750));
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

        //Create a panel for that categorizes all the possible menu items
        JPanel menuPanel = new JPanel(new GridLayout(3, 5, 10, 10));
        menuPanel.setBounds(100, 100, (int) (windowWidth * 0.6), (int) (windowHeight * 0.7));
        menuPanel.setBackground(new Color(255, 255, 255, 220));
        menuPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        //Customizes buttons for each menu category
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

        //Array for each menu category (One for the GUI Buttons, the other for the SQL Queries and their category name
        ImageIcon[] menu_items = {brewed_tea, milk_tea, fruit_tea, fresh_milk, ice_blended, creama, tea_mojito, ice_cream, newItem, rewards, top_order, second, third, fourth, gift_cards};
        String[] categories = {"Brewed Tea", "Milk Tea", "Fruit Tea", "Fresh Milk", "Ice Blended", "Creama", "Tea Mojito", "Ice Cream", "New_Item", "Rewards", "Top Order", "Second", "Third", "Fourth", "Gift Cards"};
        
        //Loads Buttons and Queries once button is selected
        for(int i = 0; i < 15; i++)
        {
            JButton button = new JButton(menu_items[i]);
            final int index = i;
            button.addActionListener(e -> showPopup(categories[index]));

            menuPanel.add(button);
        }

        //Order Panel keeps track of one's order
        orderPanel = new JPanel();
        orderPanel.setBounds((int) (windowWidth * 0.7) + 20, 150, (int) (windowWidth * 0.25) - 40, (int) (windowHeight * 0.6));
        orderPanel.setBackground(Color.WHITE);
        orderPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel orderTitle = new JLabel("Order", SwingConstants.CENTER);
        orderTitle.setFont(new Font("Arial", Font.BOLD, 24));
        orderTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        orderPanel.add(orderTitle, BorderLayout.NORTH);

        //stores an individuals order
        JList<String> orderList = new JList<>(orderListModel);
        orderList.setFont(new Font("Arial", Font.PLAIN, 10));
        JScrollPane orderScrollPane = new JScrollPane(orderList);
        orderScrollPane.setPreferredSize(new Dimension(300, 500));
        orderPanel.add(orderScrollPane, BorderLayout.CENTER);

        totalPriceLabel = new JLabel("Total: $0.00", SwingConstants.CENTER);
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        orderPanel.add(totalPriceLabel, BorderLayout.SOUTH);

        //Clears order and stores in database
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 18));
        checkoutButton.addActionListener(e -> checkout());
        orderPanel.add(checkoutButton, BorderLayout.SOUTH);

        // Add components to layered pane
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.add(menuPanel);
        centerPanel.add(orderPanel);
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        // layeredPane.add(backgroundLabel, Integer.valueOf(0));
        // layeredPane.add(topBar, Integer.valueOf(1));
        // layeredPane.add(menuPanel, Integer.valueOf(1));
        // layeredPane.add(orderPanel, Integer.valueOf(1));

        return panel;
    }
    
    /*
    * Updates the total of current transaction
    * 
    * @param price price of the item veing added
    * @return void
    */
    private void updateTotalPrice(double price) {
        totalPrice += price;
        totalPriceLabel.setText(String.format("Total: $%.2f", totalPrice));
        orderPanel.revalidate();  //If you add items to your order, update the order panel with its new price
        orderPanel.repaint();
    }

    /* 
    * Shows the popup to select a drink within a certain category
    * 
    * @param category category needed to populate popup
    * @return void 
    */
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

}

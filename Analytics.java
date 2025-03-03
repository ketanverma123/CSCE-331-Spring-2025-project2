import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Analytics extends JFrame{
    private JTable analyticsTable;
    private DefaultTableModel analyticsTableModel;

    /* 
    * Creates the analytics panel
    * 
    * @return the analytics panel 
    */
    public JPanel createAnalytics() {
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/bobabackground.png"));
        Image bgImage = bgIcon.getImage();
        JLabel timeLabel;

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

        JButton menuButton = GUI.createButton("./images/home.png", "Main Menu");
        menuButton.addActionListener(e -> GUI.cardLayout.show(GUI.cardPanel, "Menu"));

        JButton inventoryButton = GUI.createButton("./images/inventory.png", "Inventory");
        inventoryButton.addActionListener(e -> {
        if (GUI.currUser.isManager) {
            GUI.cardLayout.show(GUI.cardPanel, "Inventory");
        } else {
            JOptionPane.showMessageDialog(this, "Access Denied: Manager only.", "Restricted", JOptionPane.WARNING_MESSAGE);
        }
        });

        JButton analyticsButton = GUI.createButton("./images/analytics.png", "Analytics");
        analyticsButton.addActionListener(e -> GUI.cardLayout.show(GUI.cardPanel, "Analytics"));

        //adding buttons
        navPanel.add(menuButton);
        navPanel.add(inventoryButton);
        navPanel.add(analyticsButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        // Set up clock
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timeLabel.setForeground(Color.WHITE);
        Timer timer = new Timer(1000, e -> GUI.updateTime(timeLabel));
        timer.start();

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.addActionListener(e -> GUI.logoutUser());

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

    /* 
    * Loads analytics table data from the database
    * 
    * @return void 
    */
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

}

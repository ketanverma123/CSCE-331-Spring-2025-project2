import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

/*
 * Class composed of functions needed to run analytics panel
 * 
 * @author Ketan Verma
 */
public class Analytics extends JFrame {
    private JTable categoryTable;
    private DefaultTableModel categoryTableModel;
    private JTable timeWindowTable;
    private DefaultTableModel timeWindowTableModel;

    private JTable hourlySalesTable;
    private DefaultTableModel hourlySalesTableModel;

    public Analytics() {
    }

    /* 
    * Creates the analytics panel
    * 
    * @return the analytics panel 
    */
    public JPanel createAnalytics() {
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/bobabackground.png"));
        Image bgImage = bgIcon.getImage();
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Top bar with nav buttons & clock
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        // Left navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setOpaque(false);

        JButton menuButton = GUI.createButton("./images/home.png", "Main Menu");
        menuButton.addActionListener(e -> GUI.cardLayout.show(GUI.cardPanel, "Menu"));

        JButton inventoryButton = GUI.createButton("./images/inventory.png", "Inventory");
        inventoryButton.addActionListener(e -> {
            if (GUI.currUser.isManager) {
                GUI.cardLayout.show(GUI.cardPanel, "Inventory");
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Access Denied: Manager only.",
                    "Restricted",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        });

        JButton analyticsButton = GUI.createButton("./images/analytics.png", "Analytics");
        analyticsButton.addActionListener(e -> GUI.cardLayout.show(GUI.cardPanel, "Analytics"));

        navPanel.add(menuButton);
        navPanel.add(inventoryButton);
        navPanel.add(analyticsButton);

        // Right navigation panel
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JLabel timeLabel = new JLabel();
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
        mainPanel.add(topBar, BorderLayout.NORTH);

        JLabel analyticsTitle = new JLabel("Analytics", SwingConstants.CENTER);
        analyticsTitle.setFont(new Font("Arial", Font.BOLD, 40));
        analyticsTitle.setForeground(Color.WHITE);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        analyticsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(analyticsTitle);
        titlePanel.add(Box.createVerticalStrut(10));

        mainPanel.add(titlePanel, BorderLayout.CENTER);

        JTabbedPane analyticsTabs = new JTabbedPane();
        analyticsTabs.setOpaque(false);

        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setOpaque(false);

        categoryTableModel = new DefaultTableModel();
        categoryTable = new JTable(categoryTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable.setRowHeight(30);
        categoryTable.setShowGrid(false);
        categoryTable.setIntercellSpacing(new Dimension(0, 0));
        categoryTable.setBackground(new Color(255, 255, 255, 200));
        categoryTable.setOpaque(false);

        JTableHeader catHeader = categoryTable.getTableHeader();
        catHeader.setFont(new Font("Arial", Font.BOLD, 20));
        catHeader.setForeground(Color.BLACK);
        catHeader.setBackground(Color.WHITE);
        catHeader.setOpaque(true);

        JScrollPane catScrollPane = new JScrollPane(categoryTable);
        catScrollPane.getViewport().setOpaque(false);

        categoryPanel.add(catScrollPane, BorderLayout.CENTER);
        loadCategorySummaryData();
        analyticsTabs.addTab("Category Summary", categoryPanel);

        JPanel timeWindowPanel = new JPanel(new BorderLayout());
        timeWindowPanel.setOpaque(false);

        JPanel timeWindowControlPanel = new JPanel(new FlowLayout());
        timeWindowControlPanel.setOpaque(false);

        JLabel startLabel = new JLabel("Start Date/Time:");
        JTextField startField = new JTextField("2025-03-01 10:00:00", 18);

        JLabel endLabel = new JLabel("End Date/Time:");
        JTextField endField = new JTextField("2025-03-01 14:00:00", 18);

        JButton loadTimeWindowButton = new JButton("Load");
        loadTimeWindowButton.addActionListener(e -> {
            String start = startField.getText().trim();
            String end   = endField.getText().trim();
            loadItemUsageWithinTimeWindow(start, end);
        });

        timeWindowControlPanel.add(startLabel);
        timeWindowControlPanel.add(startField);
        timeWindowControlPanel.add(endLabel);
        timeWindowControlPanel.add(endField);
        timeWindowControlPanel.add(loadTimeWindowButton);

        timeWindowTableModel = new DefaultTableModel(
            new String[] { "Item Name", "Total Used" }, 0
        );
        timeWindowTable = new JTable(timeWindowTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane timeWindowScroll = new JScrollPane(timeWindowTable);
        timeWindowScroll.getViewport().setOpaque(false);

        timeWindowPanel.add(timeWindowControlPanel, BorderLayout.NORTH);
        timeWindowPanel.add(timeWindowScroll, BorderLayout.CENTER);
        analyticsTabs.addTab("Time Window Usage", timeWindowPanel);

        JPanel hourlySalesPanel = new JPanel(new BorderLayout());
        hourlySalesPanel.setOpaque(false);

        hourlySalesTableModel = new DefaultTableModel(
            new String[] { "Hour of Day", "Sales Count", "Total Revenue" }, 0
        );
        hourlySalesTable = new JTable(hourlySalesTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane hourlySalesScroll = new JScrollPane(hourlySalesTable);
        hourlySalesScroll.getViewport().setOpaque(false);

        hourlySalesPanel.add(hourlySalesScroll, BorderLayout.CENTER);
        analyticsTabs.addTab("Hourly Sales", hourlySalesPanel);

        // hourly sales of current day
        loadSalesPerHourOfCurrentDay();

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(analyticsTabs, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    /* 
    * Loads analytics table data from the database
    * 
    * @return void 
    */
    private void loadCategorySummaryData() {
        categoryTableModel.setRowCount(0);

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

            String[] columns = { "Category", "Sales Qty", "Sales", "Top Seller", "Top Seller %Sales" };
            categoryTableModel.setColumnIdentifiers(columns);

            while (rs.next()) {
                String category  = rs.getString("category");
                int salesQty     = rs.getInt("Sales Qty");
                double sales     = rs.getDouble("Sales");
                String topSeller = rs.getString("Top Seller");
                double pct       = rs.getDouble("Top Seller %Sales");

                categoryTableModel.addRow(new Object[] {
                    category,
                    salesQty,
                    "$" + String.format("%.2f", sales),
                    topSeller,
                    String.format("%.2f%%", pct)
                });
            }

            // Center align columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 0; i < categoryTable.getColumnCount(); i++) {
                categoryTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving analytics data.");
        }
    }

    // Items used in user specified time window
    public void loadItemUsageWithinTimeWindow(String startDateTime, String endDateTime) {
        timeWindowTableModel.setRowCount(0);

        String database_name = "team_74_db";
        String database_user = "team_74";
        String database_password = "alka";
        String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);

        String sql =
            "SELECT itemname, COUNT(*) AS total_used " +
            "FROM Sales " +
            "WHERE (saledate + saletime) BETWEEN '" + startDateTime + "' AND '" + endDateTime + "' " +
            "GROUP BY itemname " +
            "ORDER BY total_used DESC;";

        try (Connection conn = DriverManager.getConnection(database_url, database_user, database_password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String itemName = rs.getString("itemname");
                int totalUsed   = rs.getInt("total_used");

                timeWindowTableModel.addRow(new Object[] {
                    itemName,
                    totalUsed
                });
            }

            // Center align
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 0; i < timeWindowTable.getColumnCount(); i++) {
                timeWindowTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving time window usage data.");
        }
    }

    // hourly sales of current day
    public void loadSalesPerHourOfCurrentDay() {
        hourlySalesTableModel.setRowCount(0);

        String database_name = "team_74_db";
        String database_user = "team_74";
        String database_password = "alka";
        String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);

        // Generate hours from 11AM to 
        String sql =
            "SELECT hrs.hour, " +
            "       COALESCE(sales_data.sales_count, 0) AS sales_count, " +
            "       COALESCE(sales_data.total_revenue, 0) AS total_revenue " +
            "FROM (SELECT generate_series(11, 23) AS hour) hrs " +
            "LEFT JOIN ( " +
            "    SELECT EXTRACT(HOUR FROM saletime)::int AS hour, " +
            "           COUNT(*) AS sales_count, " +
            "           SUM(saleprice) AS total_revenue " +
            "    FROM Sales " +
            "    WHERE saledate = CURRENT_DATE " +
            "    GROUP BY EXTRACT(HOUR FROM saletime) " +
            ") sales_data ON hrs.hour = sales_data.hour " +
            "ORDER BY hrs.hour;";

        try (Connection conn = DriverManager.getConnection(database_url, database_user, database_password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int hour        = rs.getInt("hour");
                int salesCount  = rs.getInt("sales_count");
                double revenue  = rs.getDouble("total_revenue");

                String hourStr = String.format("%02d:00", hour);

                hourlySalesTableModel.addRow(new Object[] {
                    hourStr,
                    salesCount,
                    "$" + String.format("%.2f", revenue)
                });
            }

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            for (int i = 0; i < hourlySalesTable.getColumnCount(); i++) {
                hourlySalesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving hourly sales data.");
        }
    }
}

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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
    JPanel analyticsPanel = createAnalytics();
    JPanel inventoryPanel = createInventory();

    // Add panels to card layout
    cardPanel.add(analyticsPanel, "Analytics");
    cardPanel.add(mainMenuPanel, "Menu");
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

  private JPanel createAnalytics() {
    JPanel panel=new JPanel(new BorderLayout());
    JLabel anlylbl=new JLabel("Analytics",SwingConstants.CENTER);
    anlylbl.setFont(new Font("Arial", Font.BOLD, 24));
    anlylbl.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
    panel.add(anlylbl,BorderLayout.NORTH);
    String[] categories={"Category","Sales Qty","Sales","Top Seller","Top Seller %Sales"};
    DefaultTableModel type=new DefaultTableModel(categories,0);
    JTable table=new JTable(type);
    JScrollPane scroller=new JScrollPane(table);
    panel.add(scroller,BorderLayout.CENTER);
    JPanel navig=new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton menicon=new JButton("Main Menu");
    JButton invicon=new JButton("Inventory");
    navig.add(menicon);
    navig.add(invicon);
    panel.add(navig,BorderLayout.SOUTH);
    String sql = "WITH categorytot AS ( " + "  SELECT category, COUNT(*) AS totcnt, SUM(saleprice) AS salecnt " + "  FROM Sales " + "  GROUP BY category " + "), mostpplr AS ( " +
    "SELECT category, itemname, COUNT(*) AS itemcnt, " + " ROW_NUMBER() OVER (PARTITION BY category ORDER BY COUNT(*) DESC) AS rn " + "  FROM Sales " + "  GROUP BY category, itemname " + ") " + "SELECT cs.category, " +
    "cs.totcnt AS \"Sales Qty\", " + " cs.salecnt AS \"Sales\", " + " ts.itemname AS \"Top Seller\", " + " (ts.itemcnt::numeric / cs.totcnt * 100) AS \"Top Seller %Sales\" " +
    "FROM categorytot cs " + "JOIN mostpplr ts ON cs.category = ts.category " + "WHERE ts.rn = 1;";
    String database_name = "team_74_db";
    String database_user = "team_74";
    String database_password = "alka";
    String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);
    try {
        Connection conn=DriverManager.getConnection(database_url,database_user,database_password);
        Statement stmt=conn.createStatement();
        ResultSet rs=stmt.executeQuery(sql);
        while (rs.next()) 
        {
            String category=rs.getString("category");
            int salestot=rs.getInt("Sales Qty");
            double sales=rs.getDouble("Sales");
            String mostpplr=rs.getString("Top Seller");
            double mostpplrpct=rs.getDouble("Top Seller %Sales");
            type.addRow(new Object[]{category,salestot,"$" + String.format("%.2f", sales),mostpplr,String.format("%.2f%%", mostpplrpct)});
        }
        conn.close();
    } 
    catch (SQLException e) 
    {
        e.printStackTrace();
        JOptionPane.showMessageDialog(panel,"Error ");
    }
    return panel;
}


  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new GUI());
  }

}

import java.sql.*;
import javax.swing.*;

/*
  TODO:
  1) Change credentials for your own team's database
  2) Change SQL command to a relevant query that retrieves a small amount of data
  3) Create a JTextArea object using the queried data
  4) Add the new object to the JPanel p
*/

public class GUI extends JFrame {
    static JFrame f;

    public static void main(String[] args)
    {
      //Building the connection
      Connection conn = null;
      //TODO STEP 1 (see line 7)
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

      //closing the connection
      try {
        conn.close();
        JOptionPane.showMessageDialog(null,"Connection Closed.");
      } catch(Exception e) {
        JOptionPane.showMessageDialog(null,"Connection NOT Closed.");
      }
    }
}

    import java.awt.*;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import javax.swing.*;

    /* Compiles all panels into one to construct the overall GUI
    * 
    * @author Landon Delgado, Ketan Verma, Ayush Shah
    */
    public class GUI extends JFrame {
    public static CardLayout cardLayout;
    public static JPanel cardPanel;

    public static User currUser = new User("Null","Null",false);

    /*
    * Constructor for GUI
    */
    public GUI()
    {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        LoginPanel login = new LoginPanel();
        JPanel loginPanel = login.createLoginPanel();
        Menu menu = new Menu();
        JPanel mainMenuPanel = menu.createMenu();
        Analytics analytics = new Analytics();
        JPanel analyticsPanel = analytics.createAnalytics();
        Inventory inventory = new Inventory();
        JPanel inventoryPanel = inventory.createInventory();

        cardPanel.add(loginPanel, "Login");
        cardPanel.add(mainMenuPanel, "Menu");
        cardPanel.add(inventoryPanel, "Inventory");
        cardPanel.add(analyticsPanel, "Analytics");

        add(cardPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /*
    * Logs out the current user
    * 
    * @return none (void)
    */
    public static void logoutUser(){
        cardLayout.show(cardPanel,"Login");
    }

    /*
     * Function to update the timers
     * 
     * @param label The label which is showing the time
     * @return none (void)
     */
    public static void updateTime(JLabel label) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        String currentTime = sdf.format(new Date());
        label.setText(currentTime);
    }

    /*
     * General function to create top bar buttons which are used across all screens
     * 
     * @param imgPath The path to the image which will be used by the button
     * @param label Label to be added to button
     */
    public static JButton createButton(String imgPath, String label){
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

    /*
    * Updates font for whole GUI
    * 
    * @param font font type that you want to be set
    * @return nothing (void function)
    */
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

/*
* Class to define a user to distinguish cashiers and managers
* 
* @author Landon Delgado
*/
public class User{
    String username;
    String password;
    boolean isManager = false;

    public User(String username, String password, boolean isManager){
        this.username = username;
        this.password = password;
        this.isManager = isManager;
    }
}

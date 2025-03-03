/*
* Item class for products
* 
* @author Landon Delgado
*/
public class Item {
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

    /*
     * Override function to output the Item as a string for debugging purposes
     */
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
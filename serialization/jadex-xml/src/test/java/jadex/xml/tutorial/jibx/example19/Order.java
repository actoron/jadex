
package jadex.xml.tutorial.jibx.example19;

import java.util.ArrayList;

public class Order {
    public Customer customer;
    public double total;
    public ArrayList items;
    
    public static Order orderFactory() {
        System.out.println("Order.orderFactory called");
        return new Order();
    }
    public void preget() {
        System.out.println("Order.preget called");
        total = items!=null ? items.size() * 1.5 : 0;
    }
}

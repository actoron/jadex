
package jadex.xml.tutorial.jibx.example19;

//import org.jibx.runtime.IUnmarshallingContext;

public class Item {
    public Order order;
    public int itemId;
    public int count;
    
//    public void postset(IUnmarshallingContext ctx) {
//        order = (Order)ctx.getStackObject(1);
//        System.out.println("Item.postset called");
//    }
}

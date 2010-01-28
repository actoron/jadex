
package jadex.xml.tutorial.example21;

import java.util.HashMap;

public class Directory {
    public HashMap customerMap;
    
    public void putCustomer(String key, Customer customer)
    {
    	if(customerMap==null)
    		customerMap = new HashMap();
    	customerMap.put(key, customer);
    }
}

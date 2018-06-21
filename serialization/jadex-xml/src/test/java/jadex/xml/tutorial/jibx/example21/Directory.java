
package jadex.xml.tutorial.jibx.example21;

import java.util.HashMap;

public class Directory {
    public HashMap customerMap;
    
//    public void putCustomer(String key, Object customer)
//    {
//    	if(customerMap==null)
//    		customerMap = new HashMap();
//    	customerMap.put(key, customer);
//    }

	public String toString()
	{
		return "Directory(customerMap=" + this.customerMap + ")";
	}
}

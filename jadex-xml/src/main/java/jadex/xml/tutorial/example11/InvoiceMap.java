package jadex.xml.tutorial.example11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *  List of invoice items.
 */
public class InvoiceMap 
{
	//-------- attributes --------
	
	/** The map. */
	protected Map invoicemap;
	
	//-------- methods --------
	
	/**
	 *  Add an invoice item.
	 *  @param item The item.
	 */
	public void putItem(String key, Invoice invoice)
	{
		if(invoicemap==null)
			invoicemap = new HashMap();
		invoicemap.put(key, invoice);
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString() 
	{
		return "InvoiceMap(invoicemap=" + invoicemap + ")";
	}
}

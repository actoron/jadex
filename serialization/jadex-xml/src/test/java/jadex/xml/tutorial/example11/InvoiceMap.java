package jadex.xml.tutorial.example11;

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
	 *  Get the invoicemap.
	 *  @return The invoicemap.
	 */
	public Map getItems()
	{
		return this.invoicemap;
	}
	
	/**
	 *  Get the invoicemap.
	 *  @return The invoicemap.
	 * /
	public Set getEntries()
	{
		return this.invoicemap.entrySet();
	}*/

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString() 
	{
		return "InvoiceMap(invoicemap=" + invoicemap + ")";
	}
}

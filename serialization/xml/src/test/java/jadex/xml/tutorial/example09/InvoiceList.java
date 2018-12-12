package jadex.xml.tutorial.example09;

import java.util.ArrayList;
import java.util.List;

/**
 *  List of invoice items.
 */
public class InvoiceList 
{
	//-------- attributes --------
	
	/** The list. */
	protected List invoicelist;
	
	//-------- methods --------
	
	/**
	 *  Add an invoice item.
	 *  @param item The item.
	 */
	public void addItem(Invoice invoice)
	{
		if(invoicelist==null)
			invoicelist = new ArrayList();
		invoicelist.add(invoice);
	}
	
	/**
	 *  Get the invoicelist.
	 *  @return The invoicelist.
	 */
	public List getItems()
	{
		return invoicelist;
	}
	
	/**
	 *  Set the invoicelist.
	 *  @param invoicelist The invoicelist to set.
	 */
	public void setItems(List invoicelist)
	{
		this.invoicelist = invoicelist;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString() 
	{
		return "InvoiceList(invoicelist=" + invoicelist + ")";
	}
}

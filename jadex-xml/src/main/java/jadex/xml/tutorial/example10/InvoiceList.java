package jadex.xml.tutorial.example10;

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
	public void setItems(Invoice[] invoice)
	{
		if(invoicelist==null)
			invoicelist = new ArrayList();
		for(int i=0; i<invoice.length; i++)
			invoicelist.add(invoice[i]);
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

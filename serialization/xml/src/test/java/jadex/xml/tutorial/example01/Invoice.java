package jadex.xml.tutorial.example01;

/**
 *  Invoice example class.
 */
public class Invoice
{
	//-------- attributes --------
	
	/** The invoice name. */
	public String name;
	
	/** The description. */
	public String description;
	
	/** The description. */
	public double price;
	
	/** The product key. */
	public String key;
	
	/** The quantity. */
	public int quantity;

	//-------- methods --------
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Invoice(description=" + this.description + ", key=" + this.key
			+ ", name=" + this.name + ", price=" + this.price
			+ ", quantity=" + this.quantity + ")";
	}
}

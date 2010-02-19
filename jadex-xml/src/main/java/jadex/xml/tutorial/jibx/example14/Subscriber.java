
package jadex.xml.tutorial.jibx.example14;

public class Subscriber 
{
    public String name;
    public Address mailAddress;
    
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}
	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	/**
	 *  Get the street.
	 *  @return The street.
	 */
	public String getStreet()
	{
		return mailAddress!=null? mailAddress.street: null;
	}
	/**
	 *  Set the street.
	 *  @param street The street to set.
	 */
	public void setStreet(String street)
	{
		if(mailAddress==null)
			mailAddress = new Address();
		mailAddress.street = street;
	}
	/**
	 *  Get the city.
	 *  @return The city.
	 */
	public String getCity()
	{
		return mailAddress!=null? mailAddress.city: null;
	}
	/**
	 *  Set the city.
	 *  @param city The city to set.
	 */
	public void setCity(String city)
	{
		if(mailAddress==null)
			mailAddress = new Address();
		mailAddress.city = city;
	}
	/**
	 *  Get the state.
	 *  @return The state.
	 */
	public String getState()
	{
		return mailAddress!=null? mailAddress.state: null;
	}
	/**
	 *  Set the state.
	 *  @param state The state to set.
	 */
	public void setState(String state)
	{
		if(mailAddress==null)
			mailAddress = new Address();
		mailAddress.state = state;
	}
	/**
	 *  Get the zip.
	 *  @return The zip.
	 */
	public Integer getZip()
	{
		return mailAddress!=null? mailAddress.zip: null;
	}
	/**
	 *  Set the zip.
	 *  @param zip The zip to set.
	 */
	public void setZip(Integer zip)
	{
		if(mailAddress==null)
			mailAddress = new Address();
		mailAddress.zip = zip;
	}
    
    
}

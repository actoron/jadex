package jadex.webservice.examples.rs.hello;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
//@XmlRootElement
public class A
{
	protected String a;
	protected List b;

	/**
	 * 
	 */
	public A()
	{
		this.b = new ArrayList();
	}
	
	/**
	 * 
	 */
	public A(String a)
	{
		this.a = a;
		this.b = new ArrayList();
	}
	/**
	 *  Get the a.
	 *  @return the a.
	 */
	public String getA()
	{
		return a;
	}
	/**
	 *  Set the a.
	 *  @param a The a to set.
	 */
	public void setA(String a)
	{
		this.a = a;
	}
	/**
	 *  Get the b.
	 *  @return the b.
	 */
	public List getB()
	{
		return b;
	}
	/**
	 *  Set the b.
	 *  @param b The b to set.
	 */
	public void setB(List b)
	{
		this.b = b;
	}
	
	
}

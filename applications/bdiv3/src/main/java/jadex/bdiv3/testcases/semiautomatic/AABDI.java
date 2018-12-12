package jadex.bdiv3.testcases.semiautomatic;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.micro.annotation.Agent;

@Agent(type=BDIAgentFactory.TYPE)
public abstract class AABDI
{
//	static
//	{
//		System.out.println("aabdi: "+AABDI.class.hashCode()+" "+AABDI.class.getClassLoader());
//	}
	
	@Belief
	protected int num1;

	/**
	 *  Get the num1.
	 *  @return The num1.
	 */
	public int getNum1()
	{
		return num1;
	}

	/**
	 *  Set the num1.
	 *  @param num1 The num1 to set.
	 */
	public void setNum1(int num1)
	{
		this.num1 = num1;
	}
	
	/**
	 * 
	 */
	public void incNum1()
	{
		this.num1++;
		System.out.println("num1: "+num1);
	}
}

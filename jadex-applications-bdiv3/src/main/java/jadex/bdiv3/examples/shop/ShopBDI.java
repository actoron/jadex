package jadex.bdiv3.examples.shop;

import jadex.bdiv3.annotation.Belief;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

import java.util.List;

/**
 *  Shop BDI agent offers items from a catalog.
 */
@Agent
@Arguments(
{
	@Argument(name="catalog", clazz=List.class), 
	@Argument(name="shopname", clazz=String.class)
})
public class ShopBDI	extends ShopCapa
{
	//-------- attributes --------

	/** The money. */
	@Belief
	protected double	money	= 100;
	
	/** The shop name. */
	@AgentArgument
	protected String shopname;
	
	/** The shop catalog. */
	@AgentArgument
	protected List<ItemInfo> catalog;
	
	//-------- constructors --------
	
	/**
	 *  Create a shop capability
	 */
	public ShopBDI()
	{
		super(null, null);
		super.shopname	= shopname;
		super.catalog	= catalog;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the money.
	 */
	public double	getMoney()
	{
		return money;
	}
	
	/**
	 *  Set the money.
	 */
	public void 	setMoney(double money)
	{
		this.money	= money;
	}
}


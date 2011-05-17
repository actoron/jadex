package jadex.examples.shop;

import jadex.bdi.examples.shop.IShopService;
import jadex.bdi.examples.shop.ItemInfo;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.IArgument;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

/**
 *  Micro agent implementation of the shop.
 */
public class ShopAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The money account. */
	protected double money;
	
	//--------methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public IFuture	agentCreated()
	{
		addService(IShopService.class, new ShopService(getExternalAccess(), (String)getArgument("name")));
		return IFuture.DONE;
	}
	
	/**
	 *  Buy an item.
	 *  @param item The item.
	 */
	public ItemInfo buyItem(String name, double price)
	{
		ItemInfo ret = null;
		
		// Fetch item data.
		ItemInfo[] catalog = getCatalog();
		ItemInfo ii = null;
		for(int i=0; i<catalog.length; i++)
		{
			if(catalog[i].getName().equals(name))
			{
				ii = catalog[i];
				break;
			}
		}
		
		if(ii==null)
		{
			throw new RuntimeException("Item not found: "+name);
		}
		// Check if enough money is given and it is in stock.
		else if(ii.getQuantity()>0 && ii.getPrice()<=price)
		{
			// Sell item by updating catalog and account
//			System.out.println(getComponentName()+" sell item: "+name+" for: "+price);
			ret = new ItemInfo(name, ii.getPrice(), 1);
			ii.setQuantity(ii.getQuantity()-1);
			
			money = money + price;
		}
		else if(ii.getQuantity()==0)
		{
			throw new RuntimeException("Item not in store: "+name);
		}
		else
		{
			throw new RuntimeException("Payment not sufficient: "+price);
		}
		
		return ret;
	}
	
	/**
	 *  Get the item catalog.
	 *  @return  The catalog.
	 */
	public ItemInfo[] getCatalog()
	{
		ItemInfo[] catalog = (ItemInfo[])getArgument("catalog");
		return catalog!=null? catalog: new ItemInfo[0];
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent exposes a shop interface.", 
			new String[]{}, 
			new IArgument[]{
				new Argument("name", "The name of the shop.", "String", "Microshop"),	
				new Argument("catalog", "The catalog of the shop.", "ItemInfo[]", new ItemInfo[]{new ItemInfo("Micro turbo", 99.99, 9)}),	
			}, null, null, null);
	}
}

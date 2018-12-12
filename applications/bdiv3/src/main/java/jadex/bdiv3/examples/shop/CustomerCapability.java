package jadex.bdiv3.examples.shop;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ICapability;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Customer capability.
 */
@Capability
@Properties(@NameValue(name="componentviewer.viewerclass", value="\"jadex.bdi.examples.shop.CustomerViewerPanel\""))
@RequiredServices({
	@RequiredService(name="localshopservices", type=IShopService.class, multiple=true, scope=ServiceScope.PLATFORM),
	@RequiredService(name="remoteshopservices", type=IShopService.class, multiple=true, scope=ServiceScope.GLOBAL),
})
public class CustomerCapability
{
	//-------- attributes --------

	/** The capability. */
	@Agent
	protected ICapability capa;
	
	/** The inventory. */
	@Belief
	protected List<ItemInfo> inventory = new ArrayList<ItemInfo>();
	
	//-------- constructors --------
	
	/**
	 *  Called when the agent is started.
	 */
	public CustomerCapability()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new CustomerFrame(capa);
			}
		});
	}
	
	/**
	 *  Get the money.
	 */
	@Belief
	public native double getMoney();
	
	/**
	 *  Set the money.
	 */
	@Belief
	public native void setMoney(double money);
	
	//-------- goals --------
	
	/**
	 *  Goal to buy an item.
	 */
	@Goal
	public static class BuyItem
	{
		//-------- attributes --------
		
		/** The item name. */
		public String	name;
		
		/** The shop. */
		public IShopService	shop;
		
		/** The price. */
		public double	price; 
		
		//-------- constructors --------

		/**
		 *  Create a buy item goal.
		 */
		public BuyItem(String name, IShopService shop, double price)
		{
			this.name	= name;
			this.shop	= shop;
			this.price	= price;
		}
	}
	
	//-------- plans --------
	
	/**
	 *  Plan for buying an item.
	 */
	@Plan(trigger=@Trigger(goals=BuyItem.class))
	public void	buyItem(BuyItem big)
	{
		// Check if enough money to buy the item
		if(getMoney()<big.price)
			throw new RuntimeException("Not enough money to buy: "+big.name);
		
		// Buy the item at the shop (the shop is a service at another agent)
		System.out.println(capa.getAgent().getId().getName()+" buying item: "+big.name);
		IFuture<ItemInfo>	future	= big.shop.buyItem(big.name, big.price);
		System.out.println(capa.getAgent().getId().getName()+" getting item: "+future);
		ItemInfo item = (ItemInfo)future.get();
		System.out.println(capa.getAgent().getId().getName()+" bought item: "+item);
		
		// Update the customer inventory 
		ItemInfo ii = null;
		for(ItemInfo test: inventory)
		{
			if(test.equals(item))
			{
				ii	= test;
				break;
			}
		}
		if(ii==null)
		{
			ii = new ItemInfo(big.name, big.price, 1);
			inventory.add(ii);
		}
		else
		{
			ii.setQuantity(ii.getQuantity()+1);
			// Hack!!! Should use beliefModified()?
			int	index	= inventory.indexOf(ii);
			inventory.set(index, ii);
		}
		
		// Update the account
		setMoney(getMoney() - big.price);
	}

	/**
	 * @return the capa
	 */
	public ICapability getCapability()
	{
		return capa;
	}
}

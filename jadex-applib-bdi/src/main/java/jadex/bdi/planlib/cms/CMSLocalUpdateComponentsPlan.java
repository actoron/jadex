package jadex.bdi.planlib.cms;

import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IEABeliefSet;
import jadex.bdi.runtime.IEABeliefbase;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentListener;
import jadex.commons.IFuture;

/**
 *  Update the belief set containing the local components.
 */
public class CMSLocalUpdateComponentsPlan extends Plan
{ 
	//-------- attributes --------
	
	/** The listener. */
	protected IComponentListener	listener;
	
	//-------- methods --------
	
	/**
	 *  The body method.
	 */
	public void body()
	{
		final IEABeliefSet components = (IEABeliefSet)getExternalAccess().getBeliefbase().getBeliefSet("components").get(this);
		
//		final IEBeliefSet components = getExternalAccess().getBeliefbase().getBeliefSet("components");
		
		final Object mon = new Object();
		synchronized(mon)
		{
			this.listener	= new IComponentListener()
			{
				public void componentAdded(final IComponentDescription desc)
				{
					try
					{
						components.addFact(desc);
						
						// Decouple threads to avoid deadlocks (e.g. with sync executor)
//						getExternalAccess().invokeLater(new Runnable()
//						{
//							public void run()
//							{
//	//							synchronized(mon)
//								{
//									components.addFact(desc);
//								}
//							}
//						});
					}
					catch(ComponentTerminatedException ate)
					{
					}
				}
						
				public void componentRemoved(final IComponentDescription desc, java.util.Map results)
				{
					try
					{
						components.removeFact(desc);
						
						// Decouple threads to avoid deadlocks (e.g. with sync executor)
//						getExternalAccess().invokeLater(new Runnable()
//						{
//							public void run()
//							{
//	//							synchronized(mon)
//								{
//									components.removeFact(desc);
//								}
//							}
//						});
					}
					catch(ComponentTerminatedException ate)
					{
					}
				}

				public void componentChanged(IComponentDescription desc)
				{
				}
			};
			
			IComponentManagementService	ces	= (IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class).get(this);
			ces.addComponentListener(null, listener);
			
			IFuture fut = ces.getComponentDescriptions();
			IComponentDescription[] descs = (IComponentDescription[])fut.get(this);
			getBeliefbase().getBeliefSet("components").addFacts(descs);
		}
		
		// Hack!!! How to remove listener on component exit? Store listener as belief?
		waitForEver();
	}
	
	public void aborted()
	{
		IComponentManagementService	ces	= (IComponentManagementService)getScope().getServiceContainer().getService(IComponentManagementService.class);
		ces.removeComponentListener(null, listener);
	}
}

package jadex.tools.generic;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComboBox;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;

/**
 *  The abstract base class for service selector panels.
 */
public abstract class AbstractServiceSelectorPanel extends AbstractSelectorPanel<IService>
{
	//-------- attributes --------
	
	/** The platform external access. */
	protected IExternalAccess platform;
	
	/** The service. */
	protected Class<?> servicetype;
	
	//-------- constructors --------
	
	/**
	 *  Create a new selector panel.
	 */
	public AbstractServiceSelectorPanel(IExternalAccess platform, Class<?> servicetype)
	{
		this.platform = platform;
		this.servicetype = servicetype;
	}
	
	//-------- methods --------
	
	/**
	 *  Refresh the combo box.
	 */
	public void refreshCombo()
	{
		// Hack!!! Search locally at (potentially remote) platform, as scope global is set to platform when transferring search request.
		final Class<IService>	type	= (Class<IService>)servicetype;
		final String	scope	= isRemote() ? RequiredServiceInfo.SCOPE_GLOBAL: RequiredServiceInfo.SCOPE_PLATFORM;
		platform.searchServices( new ServiceQuery<>(type, scope))
			.addResultListener(new SwingIntermediateResultListener<IService>(new IIntermediateResultListener<IService>()
		{
			boolean first = true;
			public void intermediateResultAvailable(IService result)
			{
				reset();
				selcb.addItem(result);
			}
			public void finished()
			{
				reset();
			}
			public void resultAvailable(Collection<IService> result)
			{
				reset();
				for(Iterator<IService> it=result.iterator(); it.hasNext(); )
				{
					selcb.addItem(it.next());
				}
			}
			public void exceptionOccurred(Exception exception)
			{
			}
			
			protected void reset()
			{
				if(first)
				{
					first = false;
					JComboBox selcb = getSelectionComboBox();
					for(int i=0; i<selcb.getItemCount(); i++)
					{
						Object oldservice = selcb.getItemAt(i);
						removePanel(oldservice);
					}
					selcb.removeAllItems();
				}
			}
			
		}));
//		}).addResultListener(new SwingDefaultResultListener<Collection<IService>>(this)
//		{
//			public void customResultAvailable(Collection<IService> newservices)
//			{
//				// Find items to remove
//				JComboBox selcb = getSelectionComboBox();
//				for(int i=0; i<selcb.getItemCount(); i++)
//				{
//					Object oldservice = selcb.getItemAt(i);
//					if(!newservices.contains(oldservice))
//					{
//						// remove old cid
//						removePanel(oldservice);
//					}
//				}
//				
//				selcb.removeAllItems();
//				for(Iterator<IService> it=newservices.iterator(); it.hasNext(); )
//				{
//					selcb.addItem(it.next());
//				}
//			}
//		});
	}
		
	/**
	 *  Convert object to string for property saving.
	 */
	public String convertToString(IService element)
	{
		return element.getServiceId().toString();
	}

	/**
	 *  Get the platform.
	 *  @return The platform.
	 */
	public IExternalAccess getPlatformAccess()
	{
		return platform;
	}
	
	
}

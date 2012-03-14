package jadex.tools.generic;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.xml.annotation.XMLClassname;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComboBox;

/**
 *  The abstract base class for service selector panels.
 */
public abstract class AbstractServiceSelectorPanel extends AbstractSelectorPanel
{
	//-------- attributes --------
	
	/** The platform external access. */
	protected IExternalAccess platform;
	
	/** The service. */
	protected Class servicetype;
	
	//-------- constructors --------
	
	/**
	 *  Create a new selector panel.
	 */
	public AbstractServiceSelectorPanel(IExternalAccess platform, Class servicetype)
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
		final Class	type	= servicetype;
		final String	scope	= isRemote() ? RequiredServiceInfo.SCOPE_GLOBAL: RequiredServiceInfo.SCOPE_PLATFORM;
		platform.scheduleStep(new IComponentStep<Collection>()
		{
			@XMLClassname("search-services")
			public IFuture<Collection> execute(IInternalAccess ia)
			{
				final Future	ret	= new Future();
				SServiceProvider.getServices(ia.getServiceContainer(), type, scope)
					.addResultListener(new DelegationResultListener(ret));
				return ret;
			}
		}).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object result)
			{
				Collection newservices = (Collection)result;
				
				// Find items to remove
				JComboBox selcb = getSelectionComboBox();
				for(int i=0; i<selcb.getItemCount(); i++)
				{
					Object oldservice = selcb.getItemAt(i);
					if(!newservices.contains(oldservice))
					{
						// remove old cid
						removePanel(oldservice);
					}
				}
				
				selcb.removeAllItems();
				for(Iterator it=newservices.iterator(); it.hasNext(); )
				{
					selcb.addItem(it.next());
				}
			}
		});
	}
	
	/**
	 *  Create a panel for a component identifier.
	 */
	public IFuture createPanel(final Object element)
	{
		final Future ret = new Future();
		final IService service = (IService)element;
		
		createServicePanel(service).addResultListener(new SwingDelegationResultListener(ret));
		
		return ret;
	}
	
	/**
	 *  Convert object to string for property saving.
	 */
	public String convertToString(Object element)
	{
		return ((IService)element).getServiceIdentifier().toString();
	}
	
	/**
	 *  Create the component panel.
	 */
	public abstract IFuture createServicePanel(IService service);

}

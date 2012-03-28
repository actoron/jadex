package jadex.tools.generic;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.xml.annotation.XMLClassname;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComboBox;

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
		platform.scheduleStep(new IComponentStep<Collection<IService>>()
		{
			@XMLClassname("search-services")
			public IFuture<Collection<IService>> execute(IInternalAccess ia)
			{
				return SServiceProvider.getServices(ia.getServiceContainer(), type, scope);
			}
		}).addResultListener(new SwingDefaultResultListener<Collection<IService>>(this)
		{
			public void customResultAvailable(Collection<IService> newservices)
			{
				// Find items to remove
				JComboBox<IService> selcb = getSelectionComboBox();
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
				for(Iterator<IService> it=newservices.iterator(); it.hasNext(); )
				{
					selcb.addItem(it.next());
				}
			}
		});
	}
		
	/**
	 *  Convert object to string for property saving.
	 */
	public String convertToString(IService element)
	{
		return element.getServiceIdentifier().toString();
	}
}

package jadex.tools.generic;

import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.IService;
import jadex.commons.service.SServiceProvider;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;

/**
 *  Abstract plugin for wrapping service views to plugin view.
 */
public abstract class AbstractServicePlugin extends AbstractGenericPlugin
{
	//-------- methods --------
	
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public abstract Class getServiceType();
	
	/**
	 *  Create the component/service panel.
	 */
	public abstract IFuture createServicePanel(IService service);
	
	/**
	 *  Get the tool icon.
	 */
	public abstract Icon getToolIcon(boolean selected);
	
	/**
	 *  Convert object to string for property saving.
	 */
	public String convertToString(Object element)
	{
		return ((IService)element).getServiceIdentifier().getServiceName();
	}
	
	/**
	 *  Refresh the combo box.
	 */
	public void refreshCombo()
	{
		SServiceProvider.getServices(getJCC().getServiceProvider(), getServiceType(), remotecb.isSelected(), true)
			.addResultListener(new SwingDefaultResultListener(getView()) 
		{
			public void customResultAvailable(Object result) 
			{
				Collection newservices = (Collection)result;
				
				// Find items to remove
				for(int i=0; i<selcb.getItemCount(); i++)
				{
					Object oldservice = selcb.getItemAt(i);
					if(!newservices.contains(oldservice))
					{
						// remove old cid
						IComponentViewerPanel panel = (IComponentViewerPanel)panels.remove(oldservice);
						if(panel!=null)
							removePanel(panel);
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
		
		createServicePanel(service).addResultListener(new SwingDefaultResultListener(centerp)
		{
			public void customResultAvailable(Object result)
			{
//				System.out.println("add: "+result+" "+sel);
				IServiceViewerPanel panel = (IServiceViewerPanel)result;
				panels.put(service, panel);
				centerp.add(panel.getComponent(), service);
				ret.setResult(panel);
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return getServiceType().getName();
	}
}

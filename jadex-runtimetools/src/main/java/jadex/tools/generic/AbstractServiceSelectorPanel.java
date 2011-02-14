package jadex.tools.generic;

import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.service.IService;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JComboBox;

/**
 *  The abstract base class for service selector panels.
 */
public abstract class AbstractServiceSelectorPanel extends AbstractSelectorPanel
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess exta;
	
	/** The service. */
	protected Class servicetype;
	
	//-------- constructors --------
	
	/**
	 *  Create a new selector panel.
	 */
	public AbstractServiceSelectorPanel(IExternalAccess exta, Class servicetype)
	{
		this.exta = exta;
		this.servicetype = servicetype;
	}
	
	//-------- methods --------
	
	/**
	 *  Refresh the combo box.
	 */
	public void refreshCombo()
	{
		SServiceProvider.getServices(exta.getServiceProvider(), servicetype,
			AbstractServiceSelectorPanel.this.isRemote()? RequiredServiceInfo.SCOPE_GLOBAL: RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDefaultResultListener(this) 
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
		
		createServicePanel(service).addResultListener(new SwingDefaultResultListener(this)
		{
			public void customResultAvailable(Object result)
			{
//				System.out.println("add: "+result+" "+sel);
				IServiceViewerPanel panel = (IServiceViewerPanel)result;
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

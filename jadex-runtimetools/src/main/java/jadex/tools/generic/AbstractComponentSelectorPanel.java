package jadex.tools.generic;

import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JComboBox;

/**
 *  Abstract base class for selector panels.
 */
public abstract class AbstractComponentSelectorPanel extends AbstractSelectorPanel
{
	//-------- attributes --------
	
	/** The jcc external access. */
	protected IExternalAccess jccaccess;
	
	/** The platform external access. */
	protected IExternalAccess platformaccess;
	
	/** The model name. */
	protected String modelname;
	
	//-------- constructors --------
	
	/**
	 *  Create a new selector panel.
	 */
	public AbstractComponentSelectorPanel(IExternalAccess jccaccess, IExternalAccess platformaccess, String modelname)
	{
		this.jccaccess = jccaccess;
		this.platformaccess = platformaccess;
		this.modelname = modelname;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the model name.
	 *  @return the model name.
	 */
	public String getModelName()
	{
		return modelname;
	}
	
	/**
	 *  Refresh the combo box.
	 */
	public void refreshCombo()
	{
		// Search starting from remote CMS.
		SServiceProvider.getService(platformaccess.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDefaultResultListener(AbstractComponentSelectorPanel.this) 
		{
			public void customResultAvailable(Object result) 
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				IComponentDescription adesc = new CMSComponentDescription(null, null, null, null, null, getModelName(), null, null);
				cms.searchComponents(adesc, null, isRemote()).addResultListener(new SwingDefaultResultListener(AbstractComponentSelectorPanel.this)
				{
					public void customResultAvailable(Object result)
					{
						IComponentDescription[] descs = (IComponentDescription[])result;
//						System.out.println("descs: "+SUtil.arrayToString(descs)+" "+remotecb.isSelected());
						Set newcids = new HashSet();
						for(int i=0; i<descs.length; i++)
						{
							newcids.add(descs[i].getName());
						}
						
						// Find items to remove
						JComboBox selcb = getSelectionComboBox();
						for(int i=0; i<selcb.getItemCount(); i++)
						{
							IComponentIdentifier oldcid = (IComponentIdentifier)selcb.getItemAt(i);
							if(!newcids.contains(oldcid))
							{
								// remove old cid
								removePanel(oldcid);
							}
						}
						
						selcb.removeAllItems();
						for(int i=0; i<descs.length; i++)
						{
							selcb.addItem(descs[i].getName());
						}
					}
				});
			}
		});
	}
	
	/**
	 *  Create a panel for a component identifier.
	 */
	public IFuture createPanel(final Object element)
	{
		final Future ret = new Future();
		final IComponentIdentifier cid = (IComponentIdentifier)element;
		
		// Get external access using local CMS (speedup in case remote component found by remote platform is actually local).
		SServiceProvider.getService(jccaccess.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess((IComponentIdentifier)cid)
					.addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						IExternalAccess exta = (IExternalAccess)result;
						createComponentPanel(exta).addResultListener(new SwingDelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
//								System.out.println("add: "+result+" "+sel);
								IComponentViewerPanel panel = (IComponentViewerPanel)result;
								ret.setResult(panel);
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Convert object to string for property saving.
	 */
	public String convertToString(Object element)
	{
		return ((IComponentIdentifier)element).getName();
	}
	
	/**
	 *  Create the component panel.
	 */
	public abstract IFuture createComponentPanel(IExternalAccess component);

}

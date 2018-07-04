package jadex.tools.generic;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JComboBox;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CMSComponentDescription;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;

/**
 *  Abstract base class for selector panels.
 */
public abstract class AbstractComponentSelectorPanel extends AbstractSelectorPanel<IComponentIdentifier>
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
		SServiceProvider.searchService(platformaccess, new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new SwingDefaultResultListener<IComponentManagementService>(AbstractComponentSelectorPanel.this) 
		{
			public void customResultAvailable(IComponentManagementService cms) 
			{
				IComponentDescription adesc = new CMSComponentDescription(null, null, false, false, false, false, false, null, getModelName(), null, null, -1, null, null, false);
				cms.searchComponents(adesc, null, isRemote()).addResultListener(new SwingDefaultResultListener<IComponentDescription[]>(AbstractComponentSelectorPanel.this)
				{
					public void customResultAvailable(IComponentDescription[] descs)
					{
//						System.out.println("descs for: "+getModelName()+" "+SUtil.arrayToString(descs)+" "+remotecb.isSelected());
						Set<IComponentIdentifier> newcids = new HashSet<IComponentIdentifier>();
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
	public IFuture<IAbstractViewerPanel> createPanel(final IComponentIdentifier cid)
	{
		final Future<IAbstractViewerPanel> ret = new Future<IAbstractViewerPanel>();
		
		// Get external access using local CMS (speedup in case remote component found by remote platform is actually local).
		SServiceProvider.searchService(jccaccess, new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new SwingExceptionDelegationResultListener<IComponentManagementService, IAbstractViewerPanel>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getExternalAccess((IComponentIdentifier)cid)
					.addResultListener(new SwingExceptionDelegationResultListener<IExternalAccess, IAbstractViewerPanel>(ret)
				{
					public void customResultAvailable(IExternalAccess exta)
					{
						createComponentPanel(exta).addResultListener(new SwingDelegationResultListener<IAbstractViewerPanel>(ret));
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Convert object to string for property saving.
	 */
	public String convertToString(IComponentIdentifier element)
	{
		return element.getName();
	}
	
	/**
	 *  Create the component panel.
	 */
	public abstract IFuture<IAbstractViewerPanel> createComponentPanel(IExternalAccess component);
}

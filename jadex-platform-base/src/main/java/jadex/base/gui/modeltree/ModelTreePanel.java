package jadex.base.gui.modeltree;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IMultiKernelListener;
import jadex.bridge.IMultiKernelNotifierService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PopupBuilder;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.SwingUtilities;

/**
 *  Tree for component models.
 */
public class ModelTreePanel extends FileTreePanel
{
	//-------- attributes --------
	
	/** The actions. */
	protected Map actions;
	
	//-------- constructors --------
	
	/**
	 *  Create a new model tree panel.
	 */
	public ModelTreePanel(IExternalAccess exta, boolean remote)
	{
		super(exta, remote, false);
		actions = new HashMap();
		
		ModelFileFilterMenuItemConstructor mic = new ModelFileFilterMenuItemConstructor(getModel(), exta);
		ModelFileFilter ff = new ModelFileFilter(mic, exta);
		ModelIconCache ic = new ModelIconCache(exta, getTree());
		
		setFileFilter(ff);
		setMenuItemConstructor(mic);
		actions.put(AddPathAction.getName(), remote ? new AddRemotePathAction(this) : new AddPathAction(this));
		actions.put(RemovePathAction.getName(), new RemovePathAction(this));
		setPopupBuilder(new PopupBuilder(new Object[]{actions.get(AddPathAction.getName()), 
			actions.get(AddRemotePathAction.getName()), mic}));
		setMenuItemConstructor(mic);
		setIconCache(ic);
		DefaultNodeHandler dnh = new DefaultNodeHandler(getTree());
		dnh.addAction(new RemovePathAction(this), null);
		addNodeHandler(dnh);
		
		SServiceProvider.getService(exta.getServiceProvider(), IMultiKernelNotifierService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				((IMultiKernelNotifierService) result).addKernelListener(new IMultiKernelListener()
				{
					protected Runnable refresh = new Runnable()
					{
						public void run()
						{
							((ModelFileFilterMenuItemConstructor)getMenuItemConstructor()).getSupportedComponentTypes().addResultListener(new SwingDefaultResultListener()
							{
								public void customResultAvailable(Object result)
								{
									((ITreeNode) getTree().getModel().getRoot()).refresh(true);
								}
							});
						}
					};
					
					public IFuture componentTypesRemoved(String[] types)
					{
						SwingUtilities.invokeLater(refresh);
						return IFuture.DONE;
					}
					
					public IFuture componentTypesAdded(String[] types)
					{
						SwingUtilities.invokeLater(refresh);
						return IFuture.DONE;
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Ignore, no multi-kernel
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Get the action.
	 *  @param name The action name.
	 *  @return The action.
	 */
	public Action getAction(String name)
	{
		return (Action)actions.get(name);
	}
}

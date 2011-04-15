package jadex.base.gui.modeltree;

import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.bridge.IExternalAccess;
import jadex.commons.gui.PopupBuilder;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;

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

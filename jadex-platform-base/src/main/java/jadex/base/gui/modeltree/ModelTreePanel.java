package jadex.base.gui.modeltree;

import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.bridge.IExternalAccess;
import jadex.commons.gui.PopupBuilder;

/**
 * 
 */
public class ModelTreePanel extends FileTreePanel
{
	/**
	 * 
	 */
	public ModelTreePanel(IExternalAccess exta, boolean remote)
	{
		super(exta, remote);
		
		ModelFileFilterMenuItemConstructor mic = new ModelFileFilterMenuItemConstructor(getModel(), exta);
		ModelFileFilter ff = new ModelFileFilter(mic, exta);
		ModelIconCache ic = new ModelIconCache(exta, getTree());
		
		setFileFilter(ff);
		setMenuItemConstructor(mic);
		setPopupBuilder(new PopupBuilder(new Object[]{new AddPathAction(this), new AddRemotePathAction(this), mic}));
		setIconCache(ic);
		addNodeHandler(new DefaultNodeHandler(getTree()));
	}
}

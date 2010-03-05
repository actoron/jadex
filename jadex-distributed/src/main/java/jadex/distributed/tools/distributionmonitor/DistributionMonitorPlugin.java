//Erbt von AbstractJCCPlugin
package jadex.distributed.tools.distributionmonitor;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIDefaults;

import jadex.commons.SGUI;
import jadex.tools.common.plugin.AbstractJCCPlugin;
import jadex.tools.libtool.LibraryPlugin;
import jadex.tools.starter.StarterPlugin;

public class DistributionMonitorPlugin extends AbstractJCCPlugin {

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
//		"conversation",	SGUI.makeIcon(LibraryPlugin.class, "/jadex/tools/common/images/libcenter.png"),
//		"conversation_sel", SGUI.makeIcon(StarterPlugin.class, "/jadex/tools/common/images/libcenter_sel.png"),
//		"help",	SGUI.makeIcon(LibraryPlugin.class, "/jadex/tools/common/images/help.gif"),
		//"icon",	SGUI.makeIcon(DistributionMonitorPlugin.class, "/jadex/distributed/tools/distributionmonitor/images/icon.png")
		"icon",	SGUI.makeIcon(DistributionMonitorPlugin.class, "/jadex/distributed/tools/distributionmonitor/images/icon.png")
		
	});
	
	
	@Override
	public boolean isLazy() {
		return false; // während einer Demo soll alles gut und schnell laufen
	}

	@Override
	public String getHelpID() {
		return "tools.distributionmonitor"; // TODO ist wahrscheinlich nicht so wichtig, aber: wofür wird das benötigt?
	}

	@Override
	public String getName() {
		return "Distribution Monitor";
	}

	@Override
	public Icon getToolIcon(boolean selected) {
		//return selected? icons.getIcon("icon"): icons.getIcon("icon");
		return icons.getIcon("icon");
	}
	
	
	/* What are the parts of a JCC plugin
	 * There are three parts
	 *  - tool bar: created by method createToolBar():JComponent[]
	 *  - menu bar: created by method createMenuBar():JMenu[]
	 *  - view: created by method createView():JComponent
	 * If you only want a view, but no tool bar or menu bar, then just don't write them!
	 * The class AbstractJCCPlugin implements all of them with a return of null, which means that
	 * non of the parts is build and displayed. Besides that, it is ALWAYS a good idea to at least
	 * provide a own createView():JComponent method to display something useful to the user.
	 */
	
	
	@Override
	public JComponent createView() {
		JComponent jcomp = new JComponent() {
		};
		return jcomp;
		//return super.createView();
	}
}
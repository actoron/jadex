/**
 * 
 */
package deco4mas.distributed.jcc.viewer;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.service.IService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.tools.generic.AbstractServicePlugin;

import javax.swing.Icon;

import deco4mas.distributed.jcc.service.ICoordinationManagementService;

/**
 * Coordination Plugin for the JCC.
 * 
 * @author Thomas Preisler
 */
public class CoordinationPlugin extends AbstractServicePlugin {

	static {
		icons.put("coordination", SGUI.makeIcon(CoordinationPlugin.class, "/deco4mas/distributed/jcc/viewer/images/coordination.png"));
		icons.put("coordination_sel", SGUI.makeIcon(CoordinationPlugin.class, "/deco4mas/distributed/jcc/viewer/images/coordination-invert.png"));
	}

	@Override
	public Class<?> getServiceType() {
		return ICoordinationManagementService.class;
	}

	@Override
	public IFuture<IAbstractViewerPanel> createServicePanel(IService service) {
		final Future<IAbstractViewerPanel> ret = new Future<IAbstractViewerPanel>();
		final CoordinationPanel cp = new CoordinationPanel();
		cp.init(getJCC(), service).addResultListener(new ExceptionDelegationResultListener<Void, IAbstractViewerPanel>(ret) {

			@Override
			public void customResultAvailable(Void result) {
				ret.setResult(cp);

			}
		});
		return ret;
	}

	@Override
	public Icon getToolIcon(boolean selected) {
		return selected ? icons.getIcon("coordination_sel") : icons.getIcon("coordination");
	}

}

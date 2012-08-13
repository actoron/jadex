/**
 * 
 */
package deco4mas.distributed.jcc.viewer;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;
import jadex.tools.generic.AbstractServicePlugin;

import javax.swing.Icon;

import deco4mas.distributed.jcc.service.ICoordinationManagementService;

/**
 * @author thomas
 * 
 */
public class CoordinationPlugin extends AbstractServicePlugin {

	@Override
	public Class<?> getServiceType() {
		return ICoordinationManagementService.class;
	}

	@Override
	public IFuture<IAbstractViewerPanel> createServicePanel(IService service) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getToolIcon(boolean selected) {
		// TODO Auto-generated method stub
		return null;
	}

}

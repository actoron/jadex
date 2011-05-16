package jadex.simulation.analysis.common.component.workflow.defaultView;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import jadex.base.gui.componenttree.ComponentProperties;
import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.bpmn.runtime.ExternalAccess;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class BpmnComponentViewerPanel extends AbstractComponentViewerPanel {

	@Override
	public JComponent getComponent() {
		JComponent view = null;
		BpmnComponentView compView = (BpmnComponentView) ((ExternalAccess)component).getInterpreter().getContextVariable("view");
		compView.init();
		view = compView;
		
		if (view == null)
		{
			//default view
			view = new JPanel();
			JComponent generalcomp = new JPanel(new GridBagLayout());
			Insets insets = new Insets(1, 1, 1, 1);
			IFuture cmsFut = SServiceProvider.getService(component.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			IComponentManagementService cms = (IComponentManagementService) cmsFut.get(new ThreadSuspendable(this));

			IFuture descFut = cms.getComponentDescription(component.getComponentIdentifier());
			IComponentDescription desc = (IComponentDescription) descFut.get(new ThreadSuspendable(this));

			ComponentProperties compProp = new ComponentProperties();
			compProp.setDescription(desc);
			compProp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Jadex 'coomponent' Eigenschaften "));
			generalcomp.add(compProp, new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
			view.add(generalcomp);
		}
		return view;
	}
}

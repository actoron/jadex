package jadex.simulation.analysis.common.component.workflow.defaultView;

import java.awt.GridBagLayout;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ExternalAccess;
import jadex.bridge.IExternalAccess;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class BpmnComponentView extends JTabbedPane
{
	ExternalAccess component;

	public BpmnComponentView(IExternalAccess component)
	{
//		this.component = (ExternalAccess) component;
		init();
	}
	
	private void init()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JPanel modelcomp = new JPanel(new GridBagLayout());


				
				addTab("Component", null, modelcomp);
				setSelectedComponent(modelcomp);
				modelcomp.validate();
				modelcomp.updateUI();
			}
		});

	}
	
}

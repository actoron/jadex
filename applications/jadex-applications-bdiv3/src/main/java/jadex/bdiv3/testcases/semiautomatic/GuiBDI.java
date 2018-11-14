package jadex.bdiv3.testcases.semiautomatic;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.bridge.service.annotation.GuiClass;
import jadex.micro.annotation.Agent;

@Agent
@GuiClass(GuiPanel.class)
public class GuiBDI
{
}

class GuiPanel extends AbstractComponentViewerPanel
{
	JTabbedPane panel = new JTabbedPane();

	public JComponent getComponent()
	{
		return panel;
    }
}
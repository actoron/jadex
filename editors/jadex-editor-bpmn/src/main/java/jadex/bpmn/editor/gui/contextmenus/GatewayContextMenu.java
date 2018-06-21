package jadex.bpmn.editor.gui.contextmenus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import jadex.bpmn.editor.gui.BpmnToolbar;
import jadex.bpmn.editor.gui.BpmnToolbar.IconGenerationTask;
import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.commons.Tuple3;

public class GatewayContextMenu extends JPopupMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GatewayContextMenu(final VActivity gateway, final ModelContainer modelcontainer)
	{
		ImageProvider imgprov = modelcontainer.getSettings().getImageProvider();
		List<IconGenerationTask> tasks =  BpmnToolbar.getTaskList(null, 0);
		List<Tuple3<String, String, Icon>> itemlist = new ArrayList<Tuple3<String,String,Icon>>();

		for (IconGenerationTask task : tasks)
		{
			if (task.mode.startsWith("Gateway") &&
				!task.mode.equals(gateway.getMActivity().getActivityType()))
			{
				ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, Color.BLACK, task.color);
				Tuple3<String, String, Icon> item = new Tuple3<String, String, Icon>(task.mode, task.tooltip, icon);
				itemlist.add(item);
			}
		}
		
		Action action = new AbstractAction()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				String mode = ((JMenuItem) e.getSource()).getName();
				((MActivity) gateway.getBpmnElement()).setActivityType(mode);
				modelcontainer.getGraph().refreshCellView(gateway);
			}
		};
		
		for (Tuple3<String, String, Icon> item : itemlist)
		{
			JMenuItem mitem = new JMenuItem(action);
			mitem.setText(item.getSecondEntity());
			mitem.setIcon(item.getThirdEntity());
			mitem.setName(item.getFirstEntity());
			add(mitem);
		}
	}

}

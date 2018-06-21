package jadex.bpmn.editor.gui.contextmenus;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import jadex.bpmn.editor.gui.BpmnToolbar;
import jadex.bpmn.editor.gui.BpmnToolbar.IconGenerationTask;
import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.commons.Tuple3;

public class EventContextMenu extends JPopupMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EventContextMenu(final VActivity event, final ModelContainer modelcontainer)
	{
		ImageProvider imgprov = modelcontainer.getSettings().getImageProvider();
		List<IconGenerationTask> tasks =  BpmnToolbar.getTaskList(null, 0);
		
		List<Tuple3<String, String, Icon>> startlist = null;
		List<Tuple3<String, String, Icon>> handlerlist = null;
		List<Tuple3<String, String, Icon>> interlist = null;
		List<Tuple3<String, String, Icon>> endlist = null;
		
		if (event.getMActivity().isEventHandler())
		{
			handlerlist = new ArrayList<Tuple3<String,String,Icon>>();
		}
		else
		{
			if (event.getMActivity().getIncomingSequenceEdges() == null || event.getMActivity().getIncomingSequenceEdges().size() == 0)
			{	
				startlist = new ArrayList<Tuple3<String,String,Icon>>();
			}
			interlist = new ArrayList<Tuple3<String,String,Icon>>();
			if (event.getMActivity().getOutgoingSequenceEdges() == null || event.getMActivity().getOutgoingSequenceEdges().size() == 0)
			{
				endlist = new ArrayList<Tuple3<String,String,Icon>>();
			}
		}

		for (IconGenerationTask task : tasks)
		{
			if (startlist != null &&
				task.mode.startsWith("EventStart") &&
				!task.mode.equals(event.getMActivity().getActivityType()))
			{
				ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, Color.BLACK, task.color);
				Tuple3<String, String, Icon> item = new Tuple3<String, String, Icon>(task.mode, task.tooltip, icon);
				startlist.add(item);
			}
			else if (interlist != null &&
					task.mode.startsWith("EventIntermediate") &&
					!task.mode.equals(event.getMActivity().getActivityType()) &&
					!task.mode.contains(ModelContainer.BOUNDARY_EVENT))
			{
				ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, Color.BLACK, task.color);
				Tuple3<String, String, Icon> item = new Tuple3<String, String, Icon>(task.mode, task.tooltip, icon);
				interlist.add(item);
			}
			else if (handlerlist != null &&
					task.mode.startsWith("EventIntermediate") &&
					!task.mode.equals(event.getMActivity().getActivityType()) &&
					task.mode.contains(ModelContainer.BOUNDARY_EVENT))
			{
				ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, Color.BLACK, task.color);
				Tuple3<String, String, Icon> item = new Tuple3<String, String, Icon>(task.mode, task.tooltip, icon);
				handlerlist.add(item);
			}
			else if (endlist != null && 
					task.mode.startsWith("EventEnd") &&
					!task.mode.equals(event.getMActivity().getActivityType()))
			{
				ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, Color.BLACK, task.color);
				Tuple3<String, String, Icon> item = new Tuple3<String, String, Icon>(task.mode, task.tooltip, icon);
				endlist.add(item);
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
				String type = ModelContainer.ACTIVITY_MODES_TO_TYPES.get(mode);
				if (type == null)
				{
					type = mode;
				}
				
				((MActivity) event.getBpmnElement()).setActivityType(type);
				((MActivity) event.getBpmnElement()).setThrowing(mode.endsWith(ModelContainer.THROWING_EVENT));
//				modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(event, modelcontainer));
				modelcontainer.setPropertyPanel(modelcontainer.getSettings().getPropertyPanelFactory().createPanel(modelcontainer, event));
				modelcontainer.getGraph().refreshCellView(event);
			}
		};
		
		List<Container> submenus = new ArrayList<Container>();
		Container menu = null;
		if (startlist != null)
		{
			if (event.getMActivity().getActivityType() != null && event.getMActivity().getActivityType().startsWith("EventStart"))
			{
				menu = this;
			}
			else
			{
				menu = new JMenu("Start Events");
				submenus.add(menu);
			}
			for (Tuple3<String, String, Icon> item : startlist)
			{
				JMenuItem mitem = new JMenuItem(action);
				mitem.setText(item.getSecondEntity());
				mitem.setIcon(item.getThirdEntity());
				mitem.setName(item.getFirstEntity());
				menu.add(mitem);
			}
		}
		
		if (interlist != null)
		{
			if (event.getMActivity().getActivityType() != null && event.getMActivity().getActivityType().startsWith("EventIntermediate"))
			{
				menu = this;
			}
			else
			{
				menu = new JMenu("Intermediate Events");
				submenus.add(menu);
			}
			for (Tuple3<String, String, Icon> item : interlist)
			{
				JMenuItem mitem = new JMenuItem(action);
				mitem.setText(item.getSecondEntity());
				mitem.setIcon(item.getThirdEntity());
				mitem.setName(item.getFirstEntity());
				menu.add(mitem);
			}
		}
		
		if (handlerlist != null)
		{
			if (event.getMActivity().isEventHandler())
			{
				menu = this;
			}
			else
			{
				menu = new JMenu("Boundary Events");
				submenus.add(menu);
			}
			for (Tuple3<String, String, Icon> item : handlerlist)
			{
				JMenuItem mitem = new JMenuItem(action);
				mitem.setText(item.getSecondEntity());
				mitem.setIcon(item.getThirdEntity());
				mitem.setName(item.getFirstEntity());
				menu.add(mitem);
			}
		}
		
		if (endlist != null)
		{
			if (event.getMActivity().getActivityType() != null && event.getMActivity().getActivityType().startsWith("EventEnd"))
			{
				menu = this;
			}
			else
			{
				menu = new JMenu("End Events");
				submenus.add(menu);
			}
			for (Tuple3<String, String, Icon> item : endlist)
			{
				JMenuItem mitem = new JMenuItem(action);
				mitem.setText(item.getSecondEntity());
				mitem.setIcon(item.getThirdEntity());
				mitem.setName(item.getFirstEntity());
				menu.add(mitem);
			}
		}
		
		for (Container submenu : submenus)
		{
			add(submenu);
		}
	}

}

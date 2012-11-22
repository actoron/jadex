package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.BpmnToolbar.IconGenerationTask;
import jadex.bpmn.editor.gui.controllers.SCreationController;
import jadex.commons.Tuple3;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.mxgraph.model.mxICell;

public class EdgeDragContextMenu extends JPopupMenu
{
	protected static Tuple3<String, String, Icon>[] DEFAULT_ITEMS;
	protected static Tuple3<String, String, Icon>[] INTERMEDIATE_EVENT_ITEMS;
	protected static Tuple3<String, String, Icon>[] END_EVENT_ITEMS;
	
	/** Source cell */
	protected Object source;
	
	/** Generated target cell if any. */
	protected mxICell target;
	
	/**
	 * Creates a new drag context menu.
	 * 
	 * @param src The source cell
	 */
	public EdgeDragContextMenu(final ModelContainer modelcontainer, Object src, final Point loc, final ActionListener actionlistener)
	{
		super("Create Target");
		
		if (DEFAULT_ITEMS == null)
		{
			ImageProvider imgprov = modelcontainer.getImageProvider();
			List<Tuple3<String, String, Icon>> defitems = new ArrayList<Tuple3<String, String, Icon>>();
			List<Tuple3<String, String, Icon>> ieitems = new ArrayList<Tuple3<String, String, Icon>>();
			List<Tuple3<String, String, Icon>> eeitems = new ArrayList<Tuple3<String, String, Icon>>();
			
			List<IconGenerationTask> tasks =  BpmnToolbar.getTaskList(null, 0);
			for (IconGenerationTask task : tasks)
			{
				if (task.mode.startsWith("EventStart") ||
					ModelContainer.EDIT_MODE_POOL.equals(task.mode) ||
					ModelContainer.EDIT_MODE_LANE.equals(task.mode) ||
					ModelContainer.EDIT_MODE_SELECTION.equals(task.mode))
				{
				}
				else if (task.mode.startsWith("EventIntermediate"))
				{
					ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, task.color);
					Tuple3<String, String, Icon> item = new Tuple3<String, String, Icon>(task.mode, task.tooltip, icon);
					ieitems.add(item);
				}
				else if (task.mode.startsWith("EventEnd"))
				{
					ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, task.color);
					Tuple3<String, String, Icon> item = new Tuple3<String, String, Icon>(task.mode, task.tooltip, icon);
					eeitems.add(item);
				}
				else
				{
					System.out.println(task.mode);
					ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, task.color);
					System.out.println(icon);
					Tuple3<String, String, Icon> item = new Tuple3<String, String, Icon>(task.mode, task.tooltip, icon);
					defitems.add(item);
				}
			}
			DEFAULT_ITEMS = (Tuple3<String, String, Icon>[]) defitems.toArray(new Tuple3[defitems.size()]);
			INTERMEDIATE_EVENT_ITEMS = (Tuple3<String, String, Icon>[]) ieitems.toArray(new Tuple3[ieitems.size()]);
			END_EVENT_ITEMS = (Tuple3<String, String, Icon>[]) eeitems.toArray(new Tuple3[eeitems.size()]);
		}
		
		this.source = src;
		this.target = null;
		
		Action action = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				target = SCreationController.createActivity(modelcontainer,
												   ((JMenuItem) e.getSource()).getName(),
												   ((mxICell) source).getParent(),
												   loc);
				actionlistener.actionPerformed(e);
			}
		};
		
		for (Tuple3<String, String, Icon> item : DEFAULT_ITEMS)
		{
			JMenuItem mitem = new JMenuItem(action);
			mitem.setText(item.getSecondEntity());
			mitem.setIcon(item.getThirdEntity());
			mitem.setName(item.getFirstEntity());
			add(mitem);
		}
		
		JMenu iemenu = new JMenu("Intermediate Events");
		for (Tuple3<String, String, Icon> item : INTERMEDIATE_EVENT_ITEMS)
		{
			JMenuItem mitem = new JMenuItem(action);
			mitem.setText(item.getSecondEntity());
			mitem.setIcon(item.getThirdEntity());
			mitem.setName(item.getFirstEntity());
			iemenu.add(mitem);
		}
		add(iemenu);
		
		JMenu eemenu = new JMenu("End Events");
		for (Tuple3<String, String, Icon> item : END_EVENT_ITEMS)
		{
			JMenuItem mitem = new JMenuItem(action);
			mitem.setText(item.getSecondEntity());
			mitem.setIcon(item.getThirdEntity());
			mitem.setName(item.getFirstEntity());
			eemenu.add(mitem);
		}
		add(eemenu);
		
//		JMenuItem item = new JMenuItem(action);
//		item.setText("Task");
//		item.setName(ModelContainer.EDIT_MODE_TASK);
//		add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("XOR-Gateway");
//		item.setName(ModelContainer.EDIT_MODE_GW_XOR);
//		add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("AND-Gateway");
//		item.setName(ModelContainer.EDIT_MODE_GW_AND);
//		add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("OR-Gateway");
//		item.setName(ModelContainer.EDIT_MODE_GW_OR);
//		add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Internal Sub-Process");
//		item.setName(ModelContainer.EDIT_MODE_SUBPROCESS);
//		add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("External Sub-Process");
//		item.setName(ModelContainer.EDIT_MODE_EXTERNAL_SUBPROCESS);
//		add(item);
//		
//		JMenu evtmenu = new JMenu("Events");
//		add(evtmenu);
//		
//		item = new JMenuItem(action);
//		item.setText("Empty Intermediate Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_EMPTY);
//		evtmenu.add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Empty End Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_END_EMPTY);
//		evtmenu.add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Catching Message Intermediate Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MESSAGE);
//		evtmenu.add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Throwing Message Intermediate Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MESSAGE_THROWING);
//		evtmenu.add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Message End Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_END_MESSAGE_THROWING);
//		evtmenu.add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Timer Intermediate Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_TIMER);
//		evtmenu.add(item);
//
//		item = new JMenuItem(action);
//		item.setText("Rule Intermediate Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_RULE);
//		evtmenu.add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Catching Signal Intermediate Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_SIGNAL);
//		evtmenu.add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Throwing Signal Intermediate Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_SIGNAL_THROWING);
//		evtmenu.add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Signal End Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_END_SIGNAL_THROWING);
//		evtmenu.add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Catching Multiple Intermediate Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MULTIPLE);
//		evtmenu.add(item);
//		
//		item = new JMenuItem(action);
//		item.setText("Throwing Multiple Intermediate Event");
//		item.setName(ModelContainer.EDIT_MODE_EVENT_INTERMEDIATE_MULTIPLE_THROWING);
//		evtmenu.add(item);
		
		addPopupMenuListener(new PopupMenuListener()
		{
			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
			}
			
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
			{
			}
			
			public void popupMenuCanceled(PopupMenuEvent e)
			{
				actionlistener.actionPerformed(null);
			}
		});
	}

	/**
	 *  Gets the target.
	 *
	 *  @return The target.
	 */
	public mxICell getTarget()
	{
		return target;
	}
	
	
}

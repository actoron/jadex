package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.BpmnToolbar.IconGenerationTask;
import jadex.bpmn.editor.gui.controllers.SCreationController;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.commons.Tuple3;

import java.awt.Color;
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
			ImageProvider imgprov = ImageProvider.getInstance();
			List<Tuple3<String, String, Icon>> defitems = new ArrayList<Tuple3<String, String, Icon>>();
			List<Tuple3<String, String, Icon>> ieitems = new ArrayList<Tuple3<String, String, Icon>>();
			List<Tuple3<String, String, Icon>> eeitems = new ArrayList<Tuple3<String, String, Icon>>();
			
			List<IconGenerationTask> tasks =  BpmnToolbar.getTaskList(null, 0);
			for (IconGenerationTask task : tasks)
			{
				if (task.mode.startsWith("EventStart") ||
					ModelContainer.EDIT_MODE_POOL.equals(task.mode) ||
					ModelContainer.EDIT_MODE_LANE.equals(task.mode) ||
					ModelContainer.EDIT_MODE_ADD_CONTROL_POINT.equals(task.mode) ||
					ModelContainer.EDIT_MODE_SELECTION.equals(task.mode) ||
					task.mode.endsWith(ModelContainer.BOUNDARY_EVENT) ||
					task.mode.equals(ModelContainer.EDIT_MODE_EVENT_BOUNDARY_MESSAGE))
				{
				}
				else if (task.mode.startsWith("EventIntermediate"))
				{
					ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, Color.BLACK, task.color);
					Tuple3<String, String, Icon> item = new Tuple3<String, String, Icon>(task.mode, task.tooltip, icon);
					ieitems.add(item);
				}
				else if (task.mode.startsWith("EventEnd"))
				{
					ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, Color.BLACK, task.color);
					Tuple3<String, String, Icon> item = new Tuple3<String, String, Icon>(task.mode, task.tooltip, icon);
					eeitems.add(item);
				}
				else
				{
					ImageIcon icon = imgprov.generateFlatButtonIcon(16, task.baseshape, task.frametype, task.sym, Color.BLACK, task.color);
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
				mxICell parent = ((mxICell) source).getParent();
				
				if (source instanceof VActivity && ((MActivity) ((VActivity) source).getBpmnElement()).isEventHandler())
				{
					parent = parent.getParent();
				}
				
				target = SCreationController.createActivity(modelcontainer,
												   ((JMenuItem) e.getSource()).getName(),
												   parent,
												   loc,
												   false);
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

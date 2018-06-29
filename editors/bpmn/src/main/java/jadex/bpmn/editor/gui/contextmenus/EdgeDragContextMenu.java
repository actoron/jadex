package jadex.bpmn.editor.gui.contextmenus;

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

import jadex.bpmn.editor.gui.BpmnToolbar;
import jadex.bpmn.editor.gui.BpmnToolbar.IconGenerationTask;
import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.gui.controllers.SCreationController;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.commons.Tuple3;

public class EdgeDragContextMenu extends JPopupMenu
{
	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 *  Default creation items in the main part of the menu.
	 */
	protected Tuple3<String, String, Icon>[] defaultitems;
	
	/**
	 *  Intermediate events in sub-menu.
	 */
	protected Tuple3<String, String, Icon>[] intermediateeventitems;
	
	/**
	 *  End events in sub-menu.
	 */
	protected Tuple3<String, String, Icon>[] endeventitems;
	
	/** Source cell */
	protected Object source;
	
	/** Generated target cell if any. */
	protected mxICell target;
	
	/**
	 * Creates a new drag context menu.
	 * 
	 * @param src The source cell
	 */
	@SuppressWarnings("unchecked")
	public EdgeDragContextMenu(final ModelContainer modelcontainer, Object src, final Point loc, final ActionListener actionlistener)
	{
		super("Create Target");
		
		if(modelcontainer.getSettings()==null)
			return;
		
		if (defaultitems == null)
		{
			ImageProvider imgprov = modelcontainer.getSettings().getImageProvider();
			List<Tuple3<String, String, Icon>> defitems = new ArrayList<Tuple3<String, String, Icon>>();
			List<Tuple3<String, String, Icon>> ieitems = new ArrayList<Tuple3<String, String, Icon>>();
			List<Tuple3<String, String, Icon>> eeitems = new ArrayList<Tuple3<String, String, Icon>>();
			
			List<IconGenerationTask> tasks =  BpmnToolbar.getTaskList(null, 0);
			
//			MActivity mac = null;
//			if (src instanceof VActivity)
//			{
//				mac = (MActivity) ((VActivity) src).getBpmnElement();
//			}
			
			for (IconGenerationTask task : tasks)
			{
				if (task.mode.startsWith("EventStart") ||
					ModelContainer.EDIT_MODE_POOL.equals(task.mode) ||
					ModelContainer.EDIT_MODE_LANE.equals(task.mode) ||
					ModelContainer.EDIT_MODE_ADD_CONTROL_POINT.equals(task.mode) ||
					ModelContainer.EDIT_MODE_SELECTION.equals(task.mode) ||
					ModelContainer.EDIT_MODE_MESSAGING_EDGE.equals(task.mode) ||
					task.mode.endsWith(ModelContainer.BOUNDARY_EVENT) ||
					task.mode.equals(ModelContainer.EDIT_MODE_EVENT_BOUNDARY_MESSAGE))
				{
				}
//				else if (ModelContainer.EDIT_MODE_MESSAGING_EDGE.equals(task.mode) &&
//						 (mac == null || mac.getActivityType() == null ||
//						 !(mac.getActivityType().contains("Message") && mac.isThrowing())))
//				{
//				}
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
			defaultitems = (Tuple3<String, String, Icon>[]) defitems.toArray(new Tuple3[defitems.size()]);
			intermediateeventitems = (Tuple3<String, String, Icon>[]) ieitems.toArray(new Tuple3[ieitems.size()]);
			endeventitems = (Tuple3<String, String, Icon>[]) eeitems.toArray(new Tuple3[eeitems.size()]);
		}
		
		this.source = src;
		this.target = null;
		
		Action action = new AbstractAction()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
			{
				String mode = ((JMenuItem) e.getSource()).getName();
				mxICell parent = ((mxICell) source).getParent();
				if (source instanceof VActivity && ((MActivity) ((VActivity) source).getBpmnElement()).isEventHandler())
				{
					parent = parent.getParent();
				}
				
				target = SCreationController.createActivity(modelcontainer,
												   mode,
												   parent,
												   loc,
												   false);
				actionlistener.actionPerformed(e);
			}
		};
		
		for (Tuple3<String, String, Icon> item : defaultitems)
		{
			JMenuItem mitem = new JMenuItem(action);
			mitem.setText(item.getSecondEntity());
			mitem.setIcon(item.getThirdEntity());
			mitem.setName(item.getFirstEntity());
			add(mitem);
		}
		
		JMenu iemenu = new JMenu("Intermediate Events");
		for (Tuple3<String, String, Icon> item : intermediateeventitems)
		{
			JMenuItem mitem = new JMenuItem(action);
			mitem.setText(item.getSecondEntity());
			mitem.setIcon(item.getThirdEntity());
			mitem.setName(item.getFirstEntity());
			iemenu.add(mitem);
		}
		add(iemenu);
		
		JMenu eemenu = new JMenu("End Events");
		for (Tuple3<String, String, Icon> item : endeventitems)
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

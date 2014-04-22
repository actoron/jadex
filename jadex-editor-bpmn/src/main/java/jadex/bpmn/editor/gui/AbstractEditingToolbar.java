package jadex.bpmn.editor.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.OverlayLayout;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.EmptyBorder;

/**
 *  Abstract class for editing tools.
 *
 */
public abstract class AbstractEditingToolbar extends JPanel
{
	/** The tool button group. */
	protected ButtonGroup toolgroup;
	
	/** The tool bars. */
	protected List<JToolBar> toolbars;;
	
	/**
	 *  Creates a new tool bar for editing tools.
	 */
	public AbstractEditingToolbar()
	{
		super();
		BoxLayout mgr = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(mgr);
		toolgroup = new ButtonGroup();
		this.toolbars = new ArrayList<JToolBar>();
	}
	
	/**
	 *  Adds a tool to the tool bar.
	 *  
	 *  @param row Toolbar row.
	 *  @param mode The edit mode.
	 *  @param icons The icons in the order off, on, highlight.
	 *  @param tooltip The button tool tip.
	 */
	protected void addTool(int row, String mode, Icon[] icons, String tooltip)
	{
		addTool(row, mode, icons[0], icons[1], icons[2], tooltip);
	}
	
	/**
	 *  Removes all components.
	 */
	public void removeAll()
	{
		//super.removeAll();
		//this.toolbars = new ArrayList<JToolBar>();
		
		for (JToolBar toolbar : toolbars)
		{
			((JPanel) ((JScrollPane) toolbar.getComponent(0)).getViewport().getComponent(0)).removeAll();
		}
		
		this.toolgroup = new ButtonGroup();
	}
	
	/**
	 *  Adds a tool to the tool bar.
	 *  
	 *  @param row Toolbar row.
	 *  @param mode The edit mode.
	 *  @param officon The icon of the button in off mode.
	 *  @param onicon The icon of the button in on mode.
	 *  @param highicon The icon of the button in highlighted mode.
	 *  @param tooltip The button tool tip.
	 */
	protected void addTool(int row, String mode, Icon officon, Icon onicon, Icon highicon, String tooltip)
	{
		JToggleButton tool = new JToggleButton(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				String mode = e.getActionCommand();
				setEditMode(mode);
			}
		});
		
		tool.getModel().setActionCommand(mode);
		tool.setContentAreaFilled(false);
		
		tool.setIcon(officon);
		tool.setRolloverIcon(highicon);
		tool.setPressedIcon(onicon);
		tool.setSelectedIcon(onicon);
		
		tool.setBorder(new EmptyBorder(0, 1, 0, 1));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText(tooltip);
		
		toolgroup.add(tool);
		JToolBar tb = getToolBar(row);
		JScrollPane sp = (JScrollPane) tb.getComponent(0);
		JPanel tbpanel = (JPanel) sp.getViewport().getComponent(0);
		tbpanel.add(tool);
		
		
		//tb.add(tool);
		
		//tb.addSeparator(new Dimension(2, 0));
	}
	
	/**
	 *  Gets the edit mode.
	 *
	 *  @return The edit mode.
	 */
	public String getEditMode()
	{
		if (toolgroup.getSelection() == null)
		{
			return ModelContainer.EDIT_MODE_STEALTH_SELECTION;
		}
		
		return toolgroup.getSelection().getActionCommand();
	}
	
	/**
	 *  Sets the edit mode.
	 *
	 *  @param editmode The edit mode.
	 */
	public void setEditMode(String editmode)
	{
		Enumeration<AbstractButton> buttons = toolgroup.getElements();
		while (buttons.hasMoreElements())
		{
			AbstractButton button = buttons.nextElement();
			if (editmode.equals(button.getActionCommand()))
			{
				toolgroup.setSelected(button.getModel(), true);
				return;
			}
		}
		toolgroup.clearSelection();
	}
	
	/**
	 *  Adds a separator.
	 *  
	 *  @param row The toolbar row.
	 */
	protected void addSeparator(int row)
	{
		JToolBar tb = getToolBar(row);
		JScrollPane sp = (JScrollPane) tb.getComponent(0);
		JPanel tbpanel = (JPanel) sp.getViewport().getComponent(0);
		tbpanel.add(new JToolBar.Separator());
	}
	
	/**
	 *  Gets the toolbar in a row.
	 *  
	 *  @param row The row.
	 *  @return The toolbar.
	 */
	protected JToolBar getToolBar(int row)
	{
		while (row >= toolbars.size())
		{
			JToolBar newbar = new JToolBar(JToolBar.HORIZONTAL);
			newbar.setLayout(new OverlayLayout(newbar));
			newbar.setFloatable(true);
			newbar.setAlignmentX(Component.LEFT_ALIGNMENT);
			newbar.setMargin(new Insets(0, 0, 0, 0));
			
			JPanel tbpanel = new JPanel();
			tbpanel.setLayout(new BoxLayout(tbpanel, BoxLayout.X_AXIS));
			
			JScrollPane sp = new JScrollPane(tbpanel);
			// Scroll pane bug fix. Thanks Java.
			sp.setLayout(new ScrollPaneLayout()
			{
				public Dimension preferredLayoutSize(Container parent)
				{
					Dimension ret =  super.preferredLayoutSize(parent);
			        JScrollPane sp = (JScrollPane) parent;
			        if (sp.getViewport().getExtentSize().width <
			        	sp.getViewport().getView().getPreferredSize().width)
			        {
			            ret.height += sp.getHorizontalScrollBar().getPreferredSize().height;
			        }
			        return ret;
				}
			});
			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			newbar.add(sp);
			toolbars.add(newbar);
			
			add(newbar);
		}
		
		return toolbars.get(row);
	}
}

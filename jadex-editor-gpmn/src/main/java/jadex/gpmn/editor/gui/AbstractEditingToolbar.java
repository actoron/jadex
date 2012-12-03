package jadex.gpmn.editor.gui;

import java.awt.Component;
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
import javax.swing.border.EmptyBorder;

/**
 *  Abstract class for editing tools.
 *
 */
public abstract class AbstractEditingToolbar extends JPanel
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** The tool button group. */
	protected ButtonGroup toolgroup;
	
	/** The tool bars. */
	protected List<JToolBar> toolbars;;
	
	/**
	 *  Creates a new tool bar for editing tools.
	 *  
	 *  @param modelcontainer The model container.
	 */
	public AbstractEditingToolbar(ModelContainer modelcontainer)
	{
		super();
		BoxLayout mgr = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(mgr);
		this.modelcontainer = modelcontainer;
		modelcontainer.setEditingToolbar(this);
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
		super.removeAll();
		this.toolbars = new ArrayList<JToolBar>();
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
				modelcontainer.setEditMode(mode);
			}
		});
		
		tool.getModel().setActionCommand(mode);
		tool.setContentAreaFilled(false);
		
		tool.setIcon(officon);
		tool.setRolloverIcon(highicon);
		tool.setPressedIcon(onicon);
		tool.setSelectedIcon(onicon);
		
		tool.setBorder(new EmptyBorder(0, 0, 0, 0));
		tool.setMargin(new Insets(0, 0, 0, 0));
		tool.setToolTipText(tooltip);
		
		toolgroup.add(tool);
		JToolBar tb = getToolBar(row);
		tb.add(tool);
		
		tb.addSeparator(new Dimension(2, 0));
	}
	
	/**
	 *  Gets the edit mode.
	 *
	 *  @return The edit mode.
	 */
	public String getEditMode()
	{
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
				break;
			}
		}
	}
	
	/**
	 *  Adds a separator.
	 *  
	 *  @param row The toolbar row.
	 */
	protected void addSeparator(int row)
	{
		getToolBar(row).addSeparator();
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
			JToolBar newbar = new JToolBar();
			newbar.setFloatable(false);
			newbar.setAlignmentX(Component.LEFT_ALIGNMENT);
			newbar.setMargin(new Insets(2, 2, 2, 2));
			add(new JScrollPane(newbar));
			toolbars.add(newbar);
		}
		
		return toolbars.get(row);
	}
}

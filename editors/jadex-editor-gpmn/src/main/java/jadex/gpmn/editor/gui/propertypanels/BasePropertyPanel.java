package jadex.gpmn.editor.gui.propertypanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import jadex.gpmn.editor.gui.GpmnGraph;
import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;

/**
 *  Class for property panels.
 *
 */
public class BasePropertyPanel extends JPanel
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/**
	 *  Creates a new property panel.
	 *  @param container The model container.
	 */
	public BasePropertyPanel(ModelContainer container)
	{
		this.modelcontainer = container;
	}
	
	/**
	 *  Returns the graph.
	 *  
	 *  @return The graph.
	 */
	public GpmnGraph getGraph()
	{
		return modelcontainer.getGraph();
	}
	
	/**
	 *  Returns the GPMN model.
	 *  
	 *  @return The model.
	 */
	public IGpmnModel getModel()
	{
		return modelcontainer.getGpmnModel();
	}
	
	/** Default text field border used for text areas. */
	protected static final Border DEFAULT_TEXT_BORDER = new JTextField().getBorder();
	
	/**
	 *  Helper method for adding a 2-component line for an input.
	 *  @param first First component, often the label.
	 *  @param second Second component, often the field.
	 *  @param y The vertical position.
	 */
	protected void configureAndAddInputLine(JLabel label, JComponent field, int y)
	{
		configureAndAddInputLine(this, label, field, y);
	}
	
	/**
	 *  Helper method for adding a 2-component line for an input.
	 *  @param column The target column.
	 *  @param first First component, often the label.
	 *  @param second Second component, often the field.
	 *  @param y The vertical position.
	 */
	protected void configureAndAddInputLine(JPanel column, JComponent first, JComponent second, int y)
	{
		configureAndAddInputLine(column, first, second, y, true);
	}
	
	/**
	 *  Helper method for adding a 2-component line for an input.
	 *  @param column The target column.
	 *  @param first First component, often the label.
	 *  @param second Second component, often the field.
	 *  @param y The vertical position.
	 *  @param insets Flag whether to use insets.
	 */
	protected void configureAndAddInputLine(JPanel column, JComponent first, JComponent second, int y, boolean insets)
	{
		first = first != null? first : new JPanel();
		second = second != null? second: new JPanel();
		
		if (second instanceof JTextArea)
		{
			JTextArea area = (JTextArea) second;
			area.setLineWrap(true);
			area.setWrapStyleWord(true);
			area.setBorder(DEFAULT_TEXT_BORDER);
			
			//FIXME: Hack for Icedtea...
			area.setMinimumSize(new Dimension(0, 20));
		}
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		if (insets)
		{
			gbc.insets = new Insets(2, 5, 2, 5);
		}
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		column.add(first, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = y;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		if (insets)
		{
			gbc.insets = new Insets(2, 0, 2, 5);
		}
		gbc.fill = GridBagConstraints.HORIZONTAL;
		if (second instanceof JCheckBox || second instanceof JComboBox)
		{
			gbc.weightx = 0.0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.WEST;
		}
		column.add(second, gbc);
	}
	
	/**
	 *  Helper method for adding a vertical filler space.
	 *  @param y The vertical position.
	 */
	protected void addVerticalFiller(int y)
	{
		addVerticalFiller(this, y);
	}
	
	/**
	 *  Helper method for adding a vertical filler space.
	 *  @param column The target column.
	 *  @param y The vertical position.
	 */
	protected void addVerticalFiller(JPanel column, int y)
	{
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.fill = GridBagConstraints.BOTH;
		column.add(new JPanel(), gbc);
	}
	
	/**
	 *  Helper method for generating a column.
	 *  @param num The column number.
	 *  @return The new column.
	 */
	protected JPanel createColumn(int num)
	{
		JPanel column = new JPanel();
		column.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = num;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		//gbc.insets = new Insets(0, 0, 0, 5);
		gbc.fill = GridBagConstraints.BOTH;
		add(column, gbc);
		
		return column;
	}
	
	/**
	 *  Helper method for creating a text area/button combination.
	 *  
	 *  @return The combined panel.
	 */
	protected JPanel createTextButtonPanel()
	{
		JPanel ret = new JPanel(new GridBagLayout());
		
		JTextArea area = new JTextArea();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setBorder(DEFAULT_TEXT_BORDER);
		
		//FIXME: Hack for Icedtea...
		area.setMinimumSize(new Dimension(0, 20));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		ret.add(area, gbc);
		
		JButton button = new JButton();
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.NONE;
		ret.add(button, gbc);
		
		return ret;
	}
}

package jadex.bpmn.editor.gui.propertypanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import jadex.bpmn.editor.gui.BpmnGraph;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VInParameter;
import jadex.bpmn.editor.model.visual.VOutParameter;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MParameter;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.collection.IndexMap;

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
	public BasePropertyPanel(String title, ModelContainer container)
	{
		super(new GridBagLayout());
		this.modelcontainer = container;
		
		if (title != null)
		{
			setBorder(new TitledBorder(title));
		}
	}

	/**
	 *  Returns the graph.
	 *  
	 *  @return The graph.
	 */
	public BpmnGraph getGraph()
	{
		return modelcontainer.getGraph();
	}
	
	/**
	 *  Returns the GPMN model.
	 *  
	 *  @return The model.
	 */
	public MBpmnModel getModel()
	{
		return modelcontainer.getBpmnModel();
	}
	
	/**
	 *  Get the modelcontainer.
	 *  @return The modelcontainer.
	 */
	public ModelContainer getModelContainer()
	{
		return modelcontainer;
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
	 */
	protected void configureAndAddInputLine(JPanel column, JComponent first, JComponent second, int y, Map<String, Object> properties)
	{
		configureAndAddInputLine(column, first, second, y, true, properties);
	}
	
	/**
	 *  Helper method for adding a 2-component line for an input.
	 *  @param column The target column.
	 *  @param first First component, often the label.
	 *  @param second Second component, often the field.
	 *  @param y The vertical position.
	 *  @param insets Flag whether to use insets.
	 *  @param properties Additional properties.
	 */
	protected void configureAndAddInputLine(JPanel column, JComponent first, JComponent second, int y, boolean insets)
	{
		configureAndAddInputLine(column, first, second, y, insets, null);
	}
	
	/**
	 *  Helper method for adding a 2-component line for an input.
	 *  @param column The target column.
	 *  @param first First component, often the label.
	 *  @param second Second component, often the field.
	 *  @param y The vertical position.
	 *  @param insets Flag whether to use insets.
	 *  @param properties Additional properties.
	 */
	protected void configureAndAddInputLine(JPanel column, JComponent first, JComponent second, int y, boolean insets, Map<String, Object> properties)
	{
		properties = properties == null? new HashMap<String, Object>(): properties;
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
		if (first != second)
		{
			column.add(first, gbc);
		}
		
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
		if (properties.containsKey("second_fill"))
		{
			gbc.fill = (Integer) properties.get("second_fill");
			gbc.weightx = gbc.fill == GridBagConstraints.HORIZONTAL || gbc.fill == GridBagConstraints.BOTH? 1.0 : 0.0;
			gbc.weighty = gbc.fill == GridBagConstraints.VERTICAL || gbc.fill == GridBagConstraints.BOTH? 1.0 : 0.0;
		}
		if (first == second)
		{
			gbc.gridx = 0;
			gbc.gridwidth = 2;
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
	
	/**
	 *  Terminate operations.
	 */
	public void terminate()
	{
	}
	
	/**
	 *  Convenience method to stop editing a JTable.
	 *  
	 *  @param table The table.
	 */
	public static final void stopEditing(JTable table)
	{
		if (table.isEditing())
		{
			table.getCellEditor().stopCellEditing();
		}
	}
	
	/**
	 *  Helper method for finding a free name.
	 */
	protected static final String createFreeName(String name, IFilter<String> contains)
	{
		if(contains.filter(name))
		{
			String basename = name;
			int counter = 1;
			while (contains.filter(name))
			{
				name = basename + counter++;
			}
		}
		return name;
	}
	
	/**
	 *  Turns empty strings to null.
	 */
	public static final String nullifyString(Object value)
	{
		return value != null && ((String) value).length() == 0? null : ((String) value);
	}
	
	/**
	 *  Returns the activity and the selected parameter from Parameter visuals or activity visuals.
	 *  
	 *  @param velement The visual element.
	 *  @return Activity and selected parameter (may be null).
	 */
	public static final Tuple2<VActivity, MParameter> getActivityAndSelectedParameter(Object velement)
	{
		VActivity act = null;
		MParameter selectedparameter = null;
		if (velement instanceof VInParameter)
		{
			act = (VActivity) ((VInParameter) velement).getParent();
			selectedparameter = ((VInParameter) velement).getParameter();
		}
		else if (velement instanceof VOutParameter)
		{
			act = (VActivity) ((VOutParameter) velement).getParent();
			selectedparameter = ((VOutParameter) velement).getParameter();
		}
		else
		{
			act = (VActivity) velement;
		}
		
		return new Tuple2<VActivity, MParameter>(act, selectedparameter);
	}
	
	/**
	 *  Index Map containment filter.
	 */
	public static final class IndexMapContains implements IFilter<String>
	{
		/** The map. */
		protected IndexMap map;
		
		/**
		 *  Creates a new filter.
		 *  @param map The map.
		 */
		public IndexMapContains(IndexMap map)
		{
			this.map = map;
		}
		
		/**
		 *  Test if an object passes the filter.
		 *  @return True, if passes the filter.
		 */
		public boolean filter(String obj)
		{
			return map != null? map.containsKey(obj): false;
		}
	}
	
	/**
	 *  Map containment filter.
	 */
	public static final class MapContains implements IFilter<String>
	{
		/** The map. */
		protected Map<String, ?> map;
		
		/**
		 *  Creates a new filter.
		 *  @param map The map.
		 */
		public MapContains(Map<String, ?> map)
		{
			this.map = map;
		}
		
		/**
		 *  Test if an object passes the filter.
		 *  @return True, if passes the filter.
		 */
		public boolean filter(String obj)
		{
			return map != null? map.containsKey(obj): false;
		}
	}
	
	/**
	 *  Collection containment filter.
	 */
	public static final class CollectionContains implements IFilter<String>
	{
		/** The collection. */
		protected Collection<String> coll;
		
		/**
		 *  Creates a new filter.
		 *  @param coll The collection.
		 */
		public CollectionContains(Collection<String> coll)
		{
			this.coll = coll;
		}
		
		/**
		 *  Test if an object passes the filter.
		 *  @return True, if passes the filter.
		 */
		public boolean filter(String obj)
		{
			return coll != null? coll.contains(obj): false;
		}
	}
}

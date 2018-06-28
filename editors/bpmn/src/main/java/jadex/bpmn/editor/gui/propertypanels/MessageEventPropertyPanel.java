package jadex.bpmn.editor.gui.propertypanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.table.AbstractTableModel;

import jadex.bpmn.editor.gui.ImageProvider;
import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MProperty;
import jadex.commons.collection.BiHashMap;
import jadex.commons.collection.IndexMap;

/**
 *  Property for message events.
 */
public class MessageEventPropertyPanel extends BasePropertyPanel
{
	/** Names for the parameter table columns. */
	protected static final String[] PARAMETER_COLUMN_NAMES = new String[] {"Name", "Value" };
	
	//FIXME: Copied from EventIntermediateMessageActivityHandler, probably
	//		  has to be moved to model.
	/** The type property message type identifies the meta type (e.g. fipa). */
	protected static final String	PROPERTY_MESSAGETYPE = "messagetype";
	
	/** The property message is the message to be sent. */
	protected static final String	PROPERTY_MESSAGE = "message";
	
	/** The property message is the message to be sent. */
	protected static final String	PROPERTY_CODECIDS = "codecids";
	
	/** Message name mapping. */
	protected static final BiHashMap<String, String> MESSAGE_NAME_MAPPING = new BiHashMap<String, String>();
	static
	{
		MESSAGE_NAME_MAPPING.put("FIPA", "fipa");
	}
	
	/** Message type mapping. */
	protected static final Map<String, Set<String>> MESSAGE_TYPE_MAPPING = new HashMap<String, Set<String>>();
	static
	{
		//FIXME: Hack! Hard-coded to avoid bridge dependency.
		Set<String> fipatype = new HashSet<String>();
		fipatype.add("encoding");
		fipatype.add("in_reply_to");
		fipatype.add("language");
		fipatype.add("ontology");
		fipatype.add("protocol");
		fipatype.add("reply_by");
		fipatype.add("reply_with");
		fipatype.add("receivers");
		fipatype.add("reply_to");
		fipatype.add("performative");
		fipatype.add("content");
		fipatype.add("sender");
		fipatype.add("conversation_id");

		fipatype.add("x_message_id");
		fipatype.add("x_timestamp");
		fipatype.add("x_rid");
		fipatype.add("x_receiver");
		fipatype.add("x_nonfunctional");
		
		MESSAGE_TYPE_MAPPING.put("fipa", fipatype);
	}
	
	/** The parameter cache */
	protected IndexMap<String, MProperty> parametercache;
	
	/** The visual event */
	protected VActivity vevent;
	
	/** The parameter table. */
	protected JTable paramtable;
	
	/**
	 *  Create a new panel.
	 *  @param container The model container.
	 *  @param vmsgevent The vactivity.
	 */
	public MessageEventPropertyPanel(ModelContainer container, VActivity vmsgevent)
	{
		super("Message Event", container);
		this.vevent = vmsgevent;
		
		parametercache = new IndexMap<String, MProperty>();
		
		for (MProperty prop : getMEventProperties().values())
		{
			if (!PROPERTY_MESSAGETYPE.equals(prop.getName()) &&
				!PROPERTY_MESSAGE.equals(prop.getName()) &&
				!PROPERTY_CODECIDS.equals(prop.getName()))
			{
				parametercache.put(prop.getName(), prop);
			}
		}
		
		int y = 0;
//		int colnum = 0;
//		JPanel column = createColumn(colnum++);
		JPanel column = new JPanel(new GridBagLayout());
		
		JScrollPane msp = new JScrollPane(column);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		add(msp, gbc);
		
		final JButton defaultparameterbutton = new JButton();
		defaultparameterbutton.setEnabled(MESSAGE_TYPE_MAPPING.containsKey(getMessageTypeString()));
		
		JLabel label = new JLabel("Message Type");
		JComboBox cbox = new JComboBox(MESSAGE_TYPE_MAPPING.keySet().toArray(new String[0]));
		cbox.setEditable(true);
		cbox.setSelectedItem(getVisibleMessageTypeString());
		cbox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
//				String oldname = getMEvent().getPropertyValue(PROPERTY_MESSAGETYPE).getValue();
				String newname = (String) ((JComboBox) e.getSource()).getSelectedItem();
				
				
				setMessageTypeString(newname);
				defaultparameterbutton.setEnabled(MESSAGE_TYPE_MAPPING.containsKey(getMessageTypeString()));
			}
		});
		configureAndAddInputLine(column, label, cbox, y++);
		
		if (getMEvent().isThrowing())
		{
			label = new JLabel("Message");
			JTextArea textarea = new JTextArea();
			textarea.setWrapStyleWord(true);
			textarea.setLineWrap(true);
			String strval = getMEvent().getPropertyValueString(PROPERTY_MESSAGE);
			textarea.setText(strval != null? strval : "");
			textarea.getDocument().addDocumentListener(new DocumentAdapter()
			{
				public void update(DocumentEvent e)
				{
					String msgval = getText(e.getDocument());
					msgval = msgval.isEmpty() ? null : msgval;
					getMEvent().addProperty("message", msgval);
				}
			});
			JScrollPane sp =  new JScrollPane(textarea);
			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			configureAndAddInputLine(column, label, sp, y++);
			textarea.setRows(3);
			sp.setMinimumSize(textarea.getPreferredSize());
		}
		
		JPanel tablepanel = new JPanel(new GridBagLayout());
		
		paramtable = new JTable(new ParameterTableModel());
		JScrollPane tablescrollpane = new JScrollPane(paramtable);
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		tablepanel.add(tablescrollpane, gc);
		
		Action addaction = new AbstractAction("Add Parameter")
		{
			public void actionPerformed(ActionEvent e)
			{
				stopEditing(paramtable);
				
				int row = paramtable.getRowCount();
				String name = BasePropertyPanel.createFreeName("name", new MapContains(getMEventProperties().getAsMap()));
				MProperty prop = new MProperty(null, name, null);
				getMEvent().addProperty(prop);
				parametercache.put(name, prop);
				modelcontainer.setDirty(true);
				((ParameterTableModel) paramtable.getModel()).fireTableRowsInserted(row, row);
			}
		};
		
		Action removeaction = new AbstractAction("Remove Parameters")
		{
			public void actionPerformed(ActionEvent e)
			{
				stopEditing(paramtable);
				
				int[] ind = paramtable.getSelectedRows();
				Arrays.sort(ind);
				
				IndexMap<String, MProperty> props = getMEventProperties();
				for (int i = ind.length - 1; i >= 0; --i)
				{
					MProperty prop = parametercache.remove(ind[i]);
					props.removeKey(prop.getName());
					
					((ParameterTableModel) paramtable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
				}
				modelcontainer.setDirty(true);
			}
		};
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(modelcontainer.getSettings().getImageProvider(), addaction, removeaction);
		
		Action setdefaultparametersaction = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				while (!parametercache.isEmpty())
				{
					MProperty prop = parametercache.remove(0);
					getMEvent().removeProperty(prop);
				}
				
				Set<String> paramnames = MESSAGE_TYPE_MAPPING.get(getMessageTypeString());
				for (String paramname : paramnames)
				{
					String name = BasePropertyPanel.createFreeName(paramname, new MapContains(getMEventProperties().getAsMap()));
					MProperty prop = new MProperty(null, name, null);
					getMEvent().addProperty(prop);
					parametercache.put(name, prop);
				}
				
				modelcontainer.setDirty(true);
				((ParameterTableModel) paramtable.getModel()).fireTableStructureChanged();
			}
		};
		Icon[] icons = modelcontainer.getSettings().getImageProvider().generateGenericFlatImageIconSet(buttonpanel.getIconSize(), ImageProvider.EMPTY_FRAME_TYPE, "page", buttonpanel.getIconColor());
		defaultparameterbutton.setAction(setdefaultparametersaction);
		defaultparameterbutton.setIcon(icons[0]);
		defaultparameterbutton.setPressedIcon(icons[1]);
		defaultparameterbutton.setRolloverIcon(icons[2]);
		defaultparameterbutton.setContentAreaFilled(false);
		defaultparameterbutton.setBorder(new EmptyBorder(0, 0, 0, 0));
		defaultparameterbutton.setMargin(new Insets(0, 0, 0, 0));
		defaultparameterbutton.setToolTipText("Enter default parameters appropriate for the message type.");
		((GridLayout) buttonpanel.getLayout()).setRows(3);
		buttonpanel.add(defaultparameterbutton);
		
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		gc.anchor = GridBagConstraints.PAGE_START;
		tablepanel.add(buttonpanel, gc);
		
		tablepanel.setBorder(new TitledBorder("Message Parameters"));
		
		configureAndAddInputLine(column, tablepanel, tablepanel, y++);
		
		addVerticalFiller(column, y);
		
	}
	
	/**
	 *  Gets the semantic message event properties.
	 * 
	 *  @return The event properties.
	 */
	protected IndexMap<String, MProperty> getMEventProperties()
	{
		return getMEvent().getProperties() != null? getMEvent().getProperties() : new IndexMap<String, MProperty>();
	}
	
	/**
	 *  Gets the semantic message event.
	 * 
	 *  @return The event.
	 */
	protected MActivity getMEvent()
	{
		return (MActivity) vevent.getBpmnElement();
	}
	
	/**
	 *  Gets the message type string.
	 *  @return The message type string.
	 */
	protected String getVisibleMessageTypeString()
	{
		String msgtypename = getMessageTypeString();
		msgtypename = MESSAGE_NAME_MAPPING.containsValue(msgtypename) ? MESSAGE_NAME_MAPPING.rget(msgtypename) : msgtypename;
		return msgtypename;
	}
	
	/**
	 *  Gets the message type string.
	 *  @return The message type string.
	 */
	protected String getMessageTypeString()
	{
		String msgtypename = getMEvent().getPropertyValueString(PROPERTY_MESSAGETYPE);
		msgtypename = msgtypename == null? "fipa" : msgtypename;
		
		return msgtypename;
	}
	
	/**
	 *  Sets the message type string.
	 *  @param msgtypestring The message type string.
	 */
	protected void setMessageTypeString(String msgtypestring)
	{
//		msgtypestring = MESSAGE_NAME_MAPPING.containsKey(msgtypestring) ? MESSAGE_NAME_MAPPING.get(msgtypestring) : msgtypestring;
//		msgtypestring = "fipa".equals(msgtypestring)? null : msgtypestring;
//		getMEvent().setPropertyValue("messagetype", msgtypestring);
	}
	
	/**
	 *  Terminates.
	 */
	public void terminate()
	{
		if (paramtable.isEditing())
		{
			paramtable.getCellEditor().stopCellEditing();
		}
	}
	
	protected class ParameterTableModel extends AbstractTableModel
	{
		/**
		 * 
		 */
		public String getColumnName(int column)
		{
			return PARAMETER_COLUMN_NAMES[column];
		}
		
		/**
		 * 
		 */
		public int getRowCount()
		{
			return parametercache.size();
		}
		
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return true;
		}
		
		/**
		 * 
		 */
		public int getColumnCount()
		{
			return PARAMETER_COLUMN_NAMES.length;
		}

		/**
		 * 
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			MProperty prop = parametercache.get(rowIndex);
			
			switch(columnIndex)
			{
				case 1:
				{
					return prop.getInitialValueString() != null? prop.getInitialValueString() : "";
				}
				case 0:
				default:
				{
					return prop.getName();
				}
			}
		}
		
		/**
		 * 
		 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			MProperty prop = parametercache.get(rowIndex);
			switch(columnIndex)
			{
				case 1:
				{
					String val = (String) aValue;
					val = val != null && val.length() > 0 ? val : null;
					prop.setInitialValue(val);
					modelcontainer.setDirty(true);
					break;
				}
				case 0:
				default:
				{
					String newname = (String) aValue;
					if (!newname.equals(prop.getName()))
					{
						newname = createFreeName(newname, new MapContains(getMEventProperties().getAsMap()));
						parametercache.remove(rowIndex);
						getMEvent().removeProperty(prop.getName());
						
						prop.setName(newname);
						getMEvent().addProperty(prop);
						parametercache.add(rowIndex, newname, prop);
						modelcontainer.setDirty(true);
					}
					break;
				}
			}
		}
		
	}
}

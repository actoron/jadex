package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bridge.fipa.FIPAMessageType;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.collection.BiHashMap;
import jadex.commons.collection.IndexMap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.table.AbstractTableModel;

public class MessageEventPropertyPanel extends BasePropertyPanel
{
	//FIXME: Copied from EventIntermediateMessageActivityHandler, probably
	//		  has to be moved to model.
	/** The type property message type identifies the meta type (e.g. fipa). */
	public static final String	PROPERTY_MESSAGETYPE = "messagetype";
	
	/** The property message is the message to be sent. */
	public static final String	PROPERTY_MESSAGE = "message";
	
	/** The property message is the message to be sent. */
	public static final String	PROPERTY_CODECIDS = "codecids";
	
	/** Message name mapping. */
	public static final BiHashMap<String, String> MESSAGE_NAME_MAPPING = new BiHashMap<String, String>();
	static
	{
		MESSAGE_NAME_MAPPING.put("FIPA", "fipa");
	}
	
	/** Message type mapping. */
	public static final Map<String, MessageType> MESSAGE_TYPE_MAPPING = new HashMap<String, MessageType>();
	{
		MessageType fmt = new FIPAMessageType();
		MESSAGE_TYPE_MAPPING.put("fipa", fmt);
	}
	
	/** The parameter cache */
	protected IndexMap<String, UnparsedExpression> parametercache;
	
	/** The visual event */
	protected VActivity vevent;
	
	public MessageEventPropertyPanel(ModelContainer container, VActivity vmsgevent)
	{
		super("Message Event", container);
		this.vevent = vmsgevent;
		
		parametercache = new IndexMap<String, UnparsedExpression>();
		getMEvent().getProperties().entrySet();
		
		
		int y = 0;
		int colnum = 0;
		JPanel column = createColumn(colnum++);
		
		JLabel label = new JLabel("Message Type");
		JComboBox cbox = new JComboBox(MESSAGE_TYPE_MAPPING.keySet().toArray(new String[0]));
		cbox.setEditable(true);
		cbox.setSelectedItem(getMessageTypeString());
		cbox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String oldname = getMEvent().getPropertyValue(PROPERTY_MESSAGETYPE).getValue();
				String newname = (String) ((JComboBox) e.getSource()).getSelectedItem();
				String msg = getMEvent().getPropertyValue(PROPERTY_MESSAGE).getValue();
				getMEvent().removeProperty(PROPERTY_MESSAGE);
				
				
				setMessageTypeString(newname);
			}
		});
		configureAndAddInputLine(column, label, cbox, y++);
		
		label = new JLabel("Message");
		JTextArea textarea = new JTextArea();
		textarea.setWrapStyleWord(true);
		textarea.setLineWrap(true);
		String strval = getMEvent().getPropertyValue(PROPERTY_MESSAGE).getValue();
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
		
		
		
		addVerticalFiller(column, y);
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
	protected String getMessageTypeString()
	{
		String msgtypename = getMEvent().getPropertyValue(PROPERTY_MESSAGETYPE).getValue();
		msgtypename = msgtypename == null? "fipa" : msgtypename;
		msgtypename = MESSAGE_NAME_MAPPING.containsValue(msgtypename) ? MESSAGE_NAME_MAPPING.rget(msgtypename) : msgtypename;
		return msgtypename;
	}
	
	/**
	 *  Sets the message type string.
	 *  @param msgtypestring The message type string.
	 */
	protected void setMessageTypeString(String msgtypestring)
	{
		msgtypestring = MESSAGE_NAME_MAPPING.containsKey(msgtypestring) ? MESSAGE_NAME_MAPPING.get(msgtypestring) : msgtypestring;
		msgtypestring = "fipa".equals(msgtypestring)? null : msgtypestring;
		//getMEvent().setPropertyValue("messagetype", msgtypestring);
	}
	
	protected class ParameterTableModel extends AbstractTableModel
	{
		public int getRowCount()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getColumnCount()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}

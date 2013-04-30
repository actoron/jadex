package jadex.bpmn.editor.gui.propertypanels;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.commons.collection.BiHashMap;
import jadex.commons.gui.autocombo.AutoCompleteCombo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;

public class MessageEventPropertyPanel extends BasePropertyPanel
{
	public static final BiHashMap<String, String> MESSAGE_TYPE_MAPPING = new BiHashMap<String, String>();
	static
	{
		MESSAGE_TYPE_MAPPING.put("FIPA", "fipa");
	}
	
	/** The visual event */
	protected VActivity vevent;
	
	public MessageEventPropertyPanel(ModelContainer container, VActivity vmsgevent)
	{
		super("Message Event", container);
		this.vevent = vmsgevent;
		
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
				setMessageTypeString((String) ((JComboBox) e.getSource()).getSelectedItem());
			}
		});
		configureAndAddInputLine(column, label, cbox, y++);
		
		label = new JLabel("Message");
		JTextArea textarea = new JTextArea();
		String strval = (String) getMEvent().getPropertyValue("message");
		textarea.setText(strval != null? strval : "");
		textarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				String msgval = getText(e.getDocument());
				msgval = msgval.isEmpty() ? null : msgval;
				getMEvent().setPropertyValue("message", msgval);
			}
		});
		configureAndAddInputLine(column, label, textarea, y++);
		
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
		String msgtypename = (String) getMEvent().getPropertyValue("messagetype");
		msgtypename = msgtypename == null? "fipa" : msgtypename;
		msgtypename = MESSAGE_TYPE_MAPPING.containsValue(msgtypename) ? MESSAGE_TYPE_MAPPING.rget(msgtypename) : msgtypename;
		return msgtypename;
	}
	
	/**
	 *  Sets the message type string.
	 *  @param msgtypestring The message type string.
	 */
	protected void setMessageTypeString(String msgtypestring)
	{
		msgtypestring = MESSAGE_TYPE_MAPPING.containsKey(msgtypestring) ? MESSAGE_TYPE_MAPPING.get(msgtypestring) : msgtypestring;
		msgtypestring = "fipa".equals(msgtypestring)? null : msgtypestring;
		getMEvent().setPropertyValue("messagetype", msgtypestring);
	}
}

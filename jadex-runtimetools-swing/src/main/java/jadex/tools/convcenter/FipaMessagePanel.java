package jadex.tools.convcenter;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import jadex.base.gui.CMSUpdateHandler;
import jadex.base.gui.ComponentSelectorDialog;
import jadex.base.gui.componenttree.ComponentIconCache;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;

/**
 *  A panel for displaying and editing a fipa message.
 */
public class FipaMessagePanel extends JPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		// Tab icons.
		"edit", SGUI.makeIcon(FipaMessagePanel.class,	"/jadex/tools/common/images/dots_small.png"),
		"delete", SGUI.makeIcon(FipaMessagePanel.class,	"/jadex/tools/common/images/delete_small.png")
	});

	/** Default textfield size (columns). */
	protected static final int	DEFCOLS	= 20;
	
	//-------- attributes --------

	/** The displayed message. */
	protected Map	message;

	/** Flag indicating if editing is allowed. */
	protected boolean	editable;
	
	protected JComboBox	performative;
	protected JTextField	tfsender;
	protected JButton	setsender;
	protected JButton	clearsender;
	protected JTextField	tfreceivers;
	protected JButton	editreceivers;
	protected JButton	clearreceivers;
	protected JTextField	tfreplyto;
	protected JButton	setreplyto;
	protected JButton	clearreplyto;
	protected JTextField	encoding;
	protected JTextField	language;
	protected JTextField	ontology;
	protected JComboBox	protocol;
	protected JTextField	convid;
	protected JTextField	inreplyto;
	protected JTextField	replywith;
	protected JFormattedTextField	replyby;
	protected JTextArea	content;

	protected IComponentIdentifier	sender;
	protected IComponentIdentifier	replyto;
	protected IComponentIdentifier[]	receivers;
	
	//-------- constructors --------

	/**
	 *  Create the panel with an initial message.
	 *  @param message	The message.
	 *  @param agent	The agent.
	 *  @param cmshandler	The shared CMS update handler.
	 *  @param comptree	The comptree (if any) will be repainted when new receivers are set in the panel.
	 */
	public FipaMessagePanel(Map message, IExternalAccess access, IExternalAccess jccaccess, CMSUpdateHandler cmshandler,
		ComponentIconCache iconcache,  final Component comptree)
	{
		super(new GridBagLayout());
		this.editable	= true;

		performative = new JComboBox((String[])SUtil.joinArrays(new String[]{""}, SFipa.PERFORMATIVES.toArray()));
//		performative.setEditable(true);	// Todo: support arbitrary performatives?

		tfsender = new JTextField(DEFCOLS);
		tfsender.setEditable(false);

		setsender	= new JButton(icons.getIcon("edit"));
		setsender.setMargin(new Insets(0,0,0,0));
		setsender.setToolTipText("Set sender agent identifier");

		clearsender	= new JButton(icons.getIcon("delete"));
		clearsender.setMargin(new Insets(0,0,0,0));
		clearsender.setToolTipText("Remove sender agent identifier");

		tfreceivers = new JTextField(DEFCOLS);
		tfreceivers.setEditable(false);

		editreceivers	= new JButton(icons.getIcon("edit"));
		editreceivers.setMargin(new Insets(0,0,0,0));
		editreceivers.setToolTipText("Edit receiver agent identifiers");

		clearreceivers	= new JButton(icons.getIcon("delete"));
		clearreceivers.setMargin(new Insets(0,0,0,0));
		clearreceivers.setToolTipText("Clear receiver agent identifiers");

		tfreplyto = new JTextField(DEFCOLS);
		tfreplyto.setEditable(false);

		setreplyto	= new JButton(icons.getIcon("edit"));
		setreplyto.setMargin(new Insets(0,0,0,0));
		setreplyto.setToolTipText("Set reply-to agent identifier");

		clearreplyto	= new JButton(icons.getIcon("delete"));
		clearreplyto.setMargin(new Insets(0,0,0,0));
		clearreplyto.setToolTipText("Remove reply-to agent identifier");

		language = new JTextField(DEFCOLS);
		encoding = new JTextField(DEFCOLS);
		ontology = new JTextField(DEFCOLS);
		protocol = new JComboBox(SFipa.PROTOCOLS.toArray());
		protocol.setEditable(true);
		convid = new JTextField(DEFCOLS);
		inreplyto = new JTextField(DEFCOLS);
		replywith = new JTextField(DEFCOLS);
		replyby = new JFormattedTextField(DateFormat.getDateInstance());
		content = new JTextArea(3,DEFCOLS);

		JLabel	label;
		Dimension	labeldim	= new JLabel("Conversation-id ").getPreferredSize();
		
		// We use this gbc's as template for all groups (box), labels (left), components (right) and buttons (but).
		GridBagConstraints	boxcons	= new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
		GridBagConstraints	leftcons	= new GridBagConstraints(0, 0, 1, 1, 0, 0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0);
		GridBagConstraints	rightcons	= new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, 1, 1, 0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0);
		GridBagConstraints	butcons1	= new GridBagConstraints(2, 0, 1, 1, 0, 0,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1,1,1,1), 0, 0);
		GridBagConstraints	butcons2	= new GridBagConstraints(3, 0, GridBagConstraints.REMAINDER, 1, 0, 0,
			GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1,1,1,1), 0, 0);
		
		// The row/box counters are used in the gbc's and incrementd to place the labels/components under each other.
		int box	= 0;
		int	row;

		
		// Conversation participants.
		boxcons.gridy	= box++;
		JPanel	participants	= new JPanel(new GridBagLayout());
		participants.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Message Properties "));
		this.add(participants, boxcons);
		row	= 0;
		
		// Performative
		leftcons.gridy	= rightcons.gridy	= row++;
		label	= new JLabel("Performative");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		participants.add(label, leftcons);
		participants.add(performative, rightcons);

		leftcons.gridy	= rightcons.gridy	= butcons1.gridy	= butcons2.gridy	= row++;
		label	= new JLabel("Sender");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		participants.add(label, leftcons);
		rightcons.gridwidth	= 1;
		participants.add(tfsender, rightcons);
		rightcons.gridwidth	= GridBagConstraints.REMAINDER;
		participants.add(setsender, butcons1);
		participants.add(clearsender, butcons2);

		leftcons.gridy	= rightcons.gridy	= butcons1.gridy	= butcons2.gridy	= row++;
		label	= new JLabel("Receivers");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		participants.add(label, leftcons);
		rightcons.gridwidth	= 1;
		participants.add(tfreceivers, rightcons);
		rightcons.gridwidth	= GridBagConstraints.REMAINDER;
		participants.add(editreceivers, butcons1);
		participants.add(clearreceivers, butcons2);

		leftcons.gridy	= rightcons.gridy	= butcons1.gridy	= butcons2.gridy	= row++;
		label	= new JLabel("Reply_to");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		participants.add(label, leftcons);
		rightcons.gridwidth	= 1;
		participants.add(tfreplyto, rightcons);
		rightcons.gridwidth	= GridBagConstraints.REMAINDER;
		participants.add(setreplyto, butcons1);
		participants.add(clearreplyto, butcons2);

		
		// Conversation control.
		boxcons.gridy	= box++;
		JPanel	convcontrol	= new JPanel(new GridBagLayout());
		convcontrol.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Conversation Control "));
		this.add(convcontrol, boxcons);
		row	= 0;

		leftcons.gridy	= rightcons.gridy	= row++;
		label	= new JLabel("Protocol");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		convcontrol.add(label, leftcons);
		convcontrol.add(protocol, rightcons);

		leftcons.gridy	= rightcons.gridy	= row++;
		label	= new JLabel("Conversation_id");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		convcontrol.add(label, leftcons);
		convcontrol.add(convid, rightcons);

		leftcons.gridy	= rightcons.gridy	= row++;
		label	= new JLabel("Reply_with");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		convcontrol.add(label, leftcons);
		convcontrol.add(replywith, rightcons);

		leftcons.gridy	= rightcons.gridy	= row++;
		label	= new JLabel("In_reply_to");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		convcontrol.add(label, leftcons);
		convcontrol.add(inreplyto, rightcons);

		leftcons.gridy	= rightcons.gridy	= row++;
		label	= new JLabel("Reply_by");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		convcontrol.add(label, leftcons);
		convcontrol.add(replyby, rightcons);


		// Content description.
		boxcons.gridy	= box++;
		JPanel	contentdesc	= new JPanel(new GridBagLayout());
		contentdesc.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Content Description "));
		this.add(contentdesc, boxcons);
		row	= 0;

		leftcons.gridy	= rightcons.gridy	= row++;
		label	= new JLabel("Language");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		contentdesc.add(label, leftcons);
		contentdesc.add(language, rightcons);

		leftcons.gridy	= rightcons.gridy	= row++;
		label	= new JLabel("Encoding");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		contentdesc.add(label, leftcons);
		contentdesc.add(encoding, rightcons);

		leftcons.gridy	= rightcons.gridy	= row++;
		label	= new JLabel("Ontology");
		label.setMinimumSize(labeldim);
		label.setPreferredSize(labeldim);
		contentdesc.add(label, leftcons);
		contentdesc.add(ontology, rightcons);

		
		// Content.
		JPanel	cpane	= new JPanel(new GridBagLayout());
		cpane.add(new JScrollPane(content), new GridBagConstraints(0, 0, GridBagConstraints.REMAINDER, 1, 1, 1,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(1,1,1,1), 0, 0));
		cpane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Content "));
		this.add(cpane, new GridBagConstraints(0, box++, GridBagConstraints.REMAINDER, 1, 1, 1,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));

		
		setMessage(message);


		// Actions for agent selection.
		final ComponentSelectorDialog	agentselector	= new ComponentSelectorDialog(this, access, jccaccess, cmshandler, null, iconcache);
		setsender.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IComponentIdentifier aid = agentselector.selectAgent(sender);
				if(aid!=null)
				{
					sender	= aid;
					tfsender.setText(aid.toString());
					tfsender.setCaretPosition(0);
				}
			}
		});
		clearsender.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sender	= null;
				tfsender.setText("");
			}
		});
		setreplyto.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IComponentIdentifier	aid	= agentselector.selectAgent(replyto);
				if(aid!=null)
				{
					replyto	= aid;
					tfreplyto.setText(aid.toString());
					tfreplyto.setCaretPosition(0);
				}
			}
		});
		clearreplyto.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				replyto	= null;
				tfreplyto.setText("");
			}
		});
		editreceivers.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IComponentIdentifier[]	aids	= agentselector.selectAgents(receivers);
				if(aids!=null)
				{
					if(aids.length>0)
					{
						receivers	= aids;
						tfreceivers.setText(SUtil.arrayToString(receivers));
						tfreceivers.setCaretPosition(0);
					}
					else
					{
						receivers	= null;
						tfreceivers.setText("");
					}
				}
				if(comptree!=null)
					comptree.repaint();
			}
		});
		clearreceivers.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				receivers	= null;
				tfreceivers.setText("");
				FipaMessagePanel.this.message.remove(SFipa.RECEIVERS);
				if(comptree!=null)
					comptree.repaint();
			}
		});
	}

	//-------- methods --------

	/**
	 *  Set the message to be displayed.
	 */
	public void	setMessage(Map message)
	{
		this.message = message;

		// Extract parameter values.
		
		performative.setSelectedItem(getParameter(SFipa.PERFORMATIVE));
		tfsender.setText(getParameter(SFipa.SENDER));
		tfreplyto.setText(getParameter(SFipa.REPLY_TO));
		tfreceivers.setText(getParameter(SFipa.RECEIVERS));
		content.setText(getParameter(SFipa.CONTENT));
		language.setText(getParameter(SFipa.LANGUAGE));
		encoding.setText(getParameter(SFipa.ENCODING));
		ontology.setText(getParameter(SFipa.ONTOLOGY));
		protocol.setSelectedItem(getParameter(SFipa.PROTOCOL));
		convid.setText(getParameter(SFipa.CONVERSATION_ID));
		inreplyto.setText(getParameter(SFipa.IN_REPLY_TO));
		replywith.setText(getParameter(SFipa.REPLY_WITH));
		replyby.setText(getParameter(SFipa.REPLY_BY));

		// Beautify appearance of text fields.
		tfsender.setCaretPosition(0);
		tfreplyto.setCaretPosition(0);
		content.setCaretPosition(0);
		language.setCaretPosition(0);
		encoding.setCaretPosition(0);
		ontology.setCaretPosition(0);
		convid.setCaretPosition(0);
		inreplyto.setCaretPosition(0);
		replywith.setCaretPosition(0);
		replyby.setCaretPosition(0);
		
		// Extract sender / replyto
		sender	= (IComponentIdentifier)message.get(SFipa.SENDER);	
		replyto	= (IComponentIdentifier)message.get(SFipa.REPLY_TO);					
		Object	recs	= message.get(SFipa.RECEIVERS);
		if(recs instanceof IComponentIdentifier)
			receivers	= new IComponentIdentifier[]{(IComponentIdentifier)recs};
		else if(recs instanceof IComponentIdentifier[])
			receivers  = (IComponentIdentifier[])recs;
		else if(recs instanceof Collection)
			receivers  = (IComponentIdentifier[])((Collection)recs).toArray(new IComponentIdentifier[((Collection)recs).size()]);
	}
  
	/**
	 *  Get the displayed message.
	 *  Should be called to ensure that uptodate values are
	 *  contained in the message parameters.
	 */
	public Map getMessage()
	{
		if(!editable)
			return this.message;
		else
			this.message = new HashMap(this.message);
		
		// Set parameter values.
		setParameter(SFipa.PERFORMATIVE, (String)performative.getSelectedItem());
		setParameter(SFipa.CONTENT, content.getText());
		setParameter(SFipa.LANGUAGE, language.getText());
		setParameter(SFipa.ENCODING, encoding.getText());
		setParameter(SFipa.ONTOLOGY, ontology.getText());
		setParameter(SFipa.PROTOCOL, (String)protocol.getSelectedItem());
		setParameter(SFipa.CONVERSATION_ID, convid.getText());
		setParameter(SFipa.IN_REPLY_TO, inreplyto.getText());
		setParameter(SFipa.REPLY_WITH, replywith.getText());

		// Set sender / replyto
		setParameter(SFipa.SENDER, sender);
		setParameter(SFipa.REPLY_TO, replyto);

		// Add receivers.
		setParameterSet(SFipa.RECEIVERS, receivers);

		// Parse reply-by field.
		Object	replybyval	= null;
		if(replyby.getText()!=null && !replyby.getText().equals(""))
		{
			try
			{
				replyby.commitEdit();
				replybyval	= replyby.getValue();
			}
			catch(ParseException e)
			{
				throw new RuntimeException("Error parsing reply-by date: "+e);
			}
		}
		else
		{
			replybyval	= null;
		}
		setParameter(SFipa.REPLY_BY, replybyval);

		return this.message;
	}

	/**
	 *  Allow editing of the message.
	 */
	public void	setEditable(boolean editable)
	{
		if(editable!=this.editable)
		{
			// When setting to not-editable, make snapshot of message.
			if(!editable)
			{
				this.message = getMessage();
			}
			
			this.editable	= editable;
			performative.setEnabled(editable);
			setsender.setEnabled(editable);
			clearsender.setEnabled(editable);
			editreceivers.setEnabled(editable);
			clearreceivers.setEnabled(editable);
			setreplyto.setEnabled(editable);
			clearreplyto.setEnabled(editable);
			content.setEditable(editable);
			language.setEditable(editable);
			encoding.setEditable(editable);
			ontology.setEditable(editable);
			protocol.setEnabled(editable);
			convid.setEditable(editable);
			replywith.setEditable(editable);
			inreplyto.setEditable(editable);
			replyby.setEditable(editable);
		}
	}

	//-------- helper methods --------

	/**
	 *  Get a message parameter value as string.
	 */
	protected String getParameter(String name)
	{
		Object	val	= message.get(name);
		return val!=null ? SUtil.arrayToString(val): "";	// arrayToString also works for non arrays.
	}

	/**
	 *  Set a message parameter from string.
	 */
	protected void setParameter(String name, final Object value)
	{
		// Replace empty string with null.
		final Object oval = value==null || value.equals("") ? null : value;		
		message.put(name, oval);
	}

	/**
	 *  Set a message parameter set.
	 */
	protected void	setParameterSet(String name, final Object[] values)
	{
		message.put(name, values);
	}

	/**
	 *  Get the currently set receivers.
	 */
	public IComponentIdentifier[] getReceivers()
	{
		return receivers;
	}
}

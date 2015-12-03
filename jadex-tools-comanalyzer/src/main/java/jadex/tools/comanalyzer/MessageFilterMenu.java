package jadex.tools.comanalyzer;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import jadex.commons.SUtil;


/**
 * The menu for message filter. Selected messages are passed to the menu to
 * provide filter options suitable for the given messages.
 */
public class MessageFilterMenu extends TitlePopupMenu
{

	/** The default parameters */
	protected final static String[] default_parameters = new String[]{Message.CONVERSATION_ID, Message.PROTOCOL, Message.PERFORMATIVE};

	/** The extended parameters */
	protected final static String[] extended_parameters = new String[]{Message.XID, Message.SENDER, Message.RECEIVER, Message.IN_REPLY_TO,
			Message.REPLY_TO, Message.REPLY_WITH, Message.REPLY_BY, Message.CONTENT, Message.ONTOLOGY, Message.ENCODING, Message.LANGUAGE};

	//			Message.CONTENT_CLASS, Message.CONTENT_START, Message.ACTION_CLASS};

	/** The ComanalyzerPlugin */
	protected ComanalyzerPlugin plugin;

	/** The passed messages */
	protected Message[] messages;

	/** Weather to replace an existing filter or to add a new */
	protected boolean replacefilter = true;

	/**
	 * Creates the message filter menu with a single message.
	 * 
	 * @param plugin The plugin.
	 * @param message The message
	 */
	public MessageFilterMenu(ComanalyzerPlugin plugin, final Message message)
	{
		this(plugin, new Message[]{message});
	}

	/**
	 * Creates the message filter menu with an array of messages.
	 * 
	 * @param plugin The plugin.
	 * @param messages The array of messages
	 */
	public MessageFilterMenu(ComanalyzerPlugin plugin, final Message[] messages)
	{
		super("Message Filter");
		this.plugin = plugin;
		this.messages = messages;
		createMenu();
	}

	/**
	 * Creates the menu items.
	 */
	protected void createMenu()
	{

		JMenuItem menu0 = new JMenuItem("Remove message filter");
		menu0.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plugin.removeMessageFilter();
				plugin.applyMessageFilter();
			}
		});
		add(menu0);
		addSeparator();

		JMenuItem menu1 = new JMenuItem("Show only messages between participants");
		menu1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				MessageFilter filter = new MessageFilter();
				for(int i = 0; i < messages.length; i++)
				{
					filter.addValue(Message.SENDER, messages[i].getParameter(Message.SENDER));
					filter.addValue(Message.SENDER, messages[i].getParameter(Message.RECEIVER));
					filter.addValue(Message.RECEIVER, messages[i].getParameter(Message.RECEIVER));
					filter.addValue(Message.RECEIVER, messages[i].getParameter(Message.SENDER));
				}
				plugin.setMessageFilter(new MessageFilter[]{filter});
				plugin.applyMessageFilter();
			}
		});
		add(menu1);
		JMenuItem menu2 = new JMenuItem("Show only selected messages");
		menu2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				MessageFilter filter = new MessageFilter();
				for(int i = 0; i < messages.length; i++)
				{
					filter.addValue(Message.SEQ_NO, messages[i].getParameter(Message.SEQ_NO));
				}
				plugin.setMessageFilter(new MessageFilter[]{filter});
				plugin.applyMessageFilter();
			}
		});
		if(messages.length > 1)
		{
			add(menu2);
		}
		addSeparator();

		JCheckBoxMenuItem addfilter = new JCheckBoxMenuItem("Replace filter");
		addfilter.setSelected(true);
		addfilter.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JCheckBoxMenuItem checkbox = (JCheckBoxMenuItem)e.getSource();
				replacefilter = checkbox.isSelected();
				setVisible(true);

			}
		});
		add(addfilter);

		addParameterMenuItems(default_parameters);

		addSeparator();

		JMenuItem extend = new JMenuItem("Extend...");
		// set italic
		extend.setFont(getFont().deriveFont(getFont().getStyle() + Font.ITALIC));
		extend.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{

				JMenuItem item = (JMenuItem)e.getSource();
				item.setVisible(false);
				addParameterMenuItems(extended_parameters);
				// prevent popup from closing
				setVisible(true);
			}
		});
		add(extend);
	}

	/**
	 * Adds the menu items for the given parameter names by extracting the
	 * values from the messages. Only items are enabled, where the values for
	 * all messages are equal
	 * 
	 * @param parameters The parameter names.
	 */
	private void addParameterMenuItems(String[] parameters)
	{
		for(int  i=0; i<parameters.length; i++)
		{
			KeyMenuItem item;

			Set valueset = new HashSet();
			for(int j=0; j< messages.length; j++)
			{
				valueset.add(messages[j].getParameter(parameters[i]));
			}
			Object[] values = (Object[])valueset.toArray(new Object[valueset.size()]);
			if(valueset.size() == 1)
			{
				item = new KeyMenuItem(parameters[i], values[0]);
			}
			else
			{
				item = new KeyMenuItem(parameters[i]);
			}
			add(item);
		}
	}

	// -------- inner class --------

	/**
	 * A menu item for parameter values
	 */
	private class KeyMenuItem extends JMenuItem
	{
		/**
		 * Creates a disabled menu item for a parameter key. (There are more
		 * than one values for the key)
		 * 
		 * @param key The parameter key.
		 */
		public KeyMenuItem(final String key)
		{
			super(key + ": more than one value");
			setEnabled(false);
		}

		/**
		 * Creates a menu item for a parameter key and a value. The action for
		 * applying the filter based on the given parameter is contained.
		 * 
		 * @param key The parameter key.
		 * @param value The parameter value.
		 */
		public KeyMenuItem(final String key, final Object value)
		{
			super(key + ": " + value);

			String name = key + ": " + value;

			setAction(new AbstractAction(name)
			{

				public void actionPerformed(ActionEvent e)
				{
					MessageFilter[] fil;

					MessageFilter filter = new MessageFilter();
					filter.addValue(key, value);

					if(replacefilter)
					{
						fil = new MessageFilter[]{filter};
					}
					else
					{
						List list = SUtil.arrayToList(plugin.getMessageFilter());
						list.add(filter);
						fil = (MessageFilter[])list.toArray(new MessageFilter[list.size()]);
					}

					plugin.setMessageFilter(fil);
					plugin.applyMessageFilter();
				}

			});

		}

	}

}
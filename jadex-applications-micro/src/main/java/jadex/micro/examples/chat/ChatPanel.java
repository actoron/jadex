package jadex.micro.examples.chat;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TerminationAdapter;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.SGUI;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *  Panel for displaying the chat.
 */
public class ChatPanel extends JPanel
{
	//-------- constants --------
	
	/** The linefeed separator. */
	public static final String lf = (String)System.getProperty("line.separator");
	
	/** The time format. */
	public static final DateFormat	df	= new SimpleDateFormat("HH:mm:ss");
	
	/** The initial state for previously known users when sending a new message. */
	public static final String	STATE_UNKNOWN	= "unknown";
	
	/** The user state for found but not yet responding user. */
	public static final String	STATE_OPEN	= "open";
	
	/** The user state for a responding user. */
	public static final String	STATE_CONNECTED	= "connected";
	
	/** The user state for a not responding user. */
	public static final String	STATE_BROKEN	= "broken";
	
	/** The user state for a no longer available user. */
	public static final String	STATE_DEAD	= "dead";
	
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		STATE_UNKNOWN,		SGUI.makeIcon(ChatPanel.class, "images/user_blue.png"),
		STATE_OPEN,			SGUI.makeIcon(ChatPanel.class, "images/user_yellow.png"),
		STATE_CONNECTED,	SGUI.makeIcon(ChatPanel.class, "images/user_green.png"),
		STATE_BROKEN,		SGUI.makeIcon(ChatPanel.class, "images/user_red.png"),
		STATE_DEAD,			SGUI.makeIcon(ChatPanel.class, "images/user_gray.png")
	});
	
	//-------- attributes --------
	
	/** The agent. */
	protected IExternalAccess agent;
	
	/** The clock service. */
	protected IClockService	clock;
	
	/** The text area. */
	protected JTextArea chatarea;
	
	/** The known chat users (cid->user state). */
	protected Map<IComponentIdentifier, String>	users;
	
	/** The user table. */
	protected JTable	table;
	
	//-------- constructors --------
	
	/**
	 *  Create a new chat panel.
	 */
	public ChatPanel(final IExternalAccess agent, IClockService clock)
	{
		this.agent	= agent;
		this.clock	= clock;
		this.users	= new LinkedHashMap<IComponentIdentifier, String>();
		
		chatarea = new JTextArea(10, 30)
		{
			public void append(String text)
			{
				super.append(text);
				this.setCaretPosition(getText().length());
			}
		};
		chatarea.setEditable(false);
		JScrollPane main = new JScrollPane(chatarea);
		
		table	= new JTable(new UserTableModel());
		JScrollPane userpan = new JScrollPane(table);
		table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
			{
				super.getTableCellRendererComponent(table, value, selected, focus, row, column);
				IComponentIdentifier	cid	= (IComponentIdentifier)value;
				this.setText(cid.getLocalName());
				this.setToolTipText(cid.getName());
				Icon	icon	= icons.getIcon(users.get(cid));
				this.setIcon(icon);
				return this;
			}
		});
		
		JPanel south = new JPanel(new BorderLayout());
		final JTextField tf = new JTextField();
		final JButton send = new JButton("Send");
		south.add(tf, BorderLayout.CENTER);
		south.add(send, BorderLayout.EAST);
		tf.setEnabled(false);
		send.setEnabled(false);

		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				tf.setEnabled(false);
				send.setEnabled(false);

				// Set user states to unknown before sending.
				IComponentIdentifier[]	cids	= users.keySet().toArray(new IComponentIdentifier[users.size()]);
				for(int i=0; i<cids.length; i++)
				{
					// Change user states to unknown, except for those who are already determined as dead (will be resurrected if found in search anyways).
					if(!STATE_DEAD.equals(users.get(cids[i])))
					{
						users.put(cids[i], STATE_UNKNOWN);
					}
				}
				((DefaultTableModel)table.getModel()).fireTableDataChanged();
				table.getParent().invalidate();
				table.getParent().doLayout();
				table.repaint();
				
				tell(tf.getText()).addResultListener(new SwingDefaultResultListener<Void>()
				{
					public void customResultAvailable(Void result)
					{
						// Set states of unavailable users to dead
						IComponentIdentifier[]	cids	= users.keySet().toArray(new IComponentIdentifier[users.size()]);
						for(int i=0; i<cids.length; i++)
						{
							if(STATE_UNKNOWN.equals(users.get(cids[i])))
							{
								users.put(cids[i], STATE_DEAD);
							}
						}
						((DefaultTableModel)table.getModel()).fireTableDataChanged();
						table.getParent().invalidate();
						table.getParent().doLayout();
						table.repaint();
						
						tf.setEnabled(true);
						send.setEnabled(true);
						tf.setText("");
						tf.requestFocus();
					}
					public void customExceptionOccurred(Exception exception)
					{
						super.customExceptionOccurred(exception);
						customResultAvailable(null);	// re-enable widgets.
					}
				});
			}
		};
		tf.addActionListener(al);
		send.addActionListener(al);

		JSplitPanel	split	= new JSplitPanel(JSplitPanel.HORIZONTAL_SPLIT, userpan, main);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(0.3);
		this.setLayout(new BorderLayout());
		this.add(split, BorderLayout.CENTER);
		this.add(south, BorderLayout.SOUTH);
		
		// Get list of initial users.
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IIntermediateFuture<IChatService> ifut = ia.getServiceContainer().getRequiredServices("chatservices");
				ifut.addResultListener(new IntermediateDefaultResultListener<IChatService>()
				{
					public void intermediateResultAvailable(final IChatService chat)
					{
						// Assume connected at first.
						setUserState(((IService)chat).getServiceIdentifier().getProviderId(), STATE_CONNECTED);
					}
					
					public void finished()
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								// Ready to chat.
								tf.setEnabled(true);
								send.setEnabled(true);
								tf.setText("");
								tf.requestFocus();
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
						finished();
					}
				});
				
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Create a gui frame.
	 */
	public static ChatPanel createGui(final IExternalAccess agent, IClockService clock)
	{
		final JFrame f = new JFrame(agent.getComponentIdentifier().getName());
		ChatPanel cp = new ChatPanel(agent, clock);
		f.add(cp);
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@XMLClassname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.addComponentListener(new TerminationAdapter()
				{
					public void componentTerminated()
					{
						f.setVisible(false);
					}
				});
				return IFuture.DONE;
			}
		});
		
		return cp;
	}
	
	/**
	 *  Add a message to the text area.
	 */
	public void addMessage(String name, String text)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("[").append(df.format(new Date(clock.getTime()))).append(", ").append(name).append("]: ").append(text).append(lf);
		chatarea.append(buf.toString());
	}
	
	/**
	 *  Tell something.
	 *  @param name The name.
	 *  @param text The text.
	 */
	public IFuture<Void>	tell(final String text)
	{
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Future<Void>	ret	= new Future<Void>();
				
				// Keep track of search and called chats (only accessed from component thread).
				final Set<IFuture<?>>	futures	= new HashSet<IFuture<?>>();
				
				final IIntermediateFuture<IChatService> ifut = ia.getServiceContainer().getRequiredServices("chatservices");
				futures.add(ifut);
				
				ifut.addResultListener(new IntermediateDefaultResultListener<IChatService>()
				{
					public void intermediateResultAvailable(final IChatService chat)
					{
						// Send chat message and wait for future.
						final IFuture<Void>	cfut	= chat.message(text);
						setUserState(((IService)chat).getServiceIdentifier().getProviderId(), STATE_OPEN);
						futures.add(cfut);
						cfut.addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								setUserState(((IService)chat).getServiceIdentifier().getProviderId(), STATE_CONNECTED);
								done(cfut);
							}
							public void exceptionOccurred(Exception exception)
							{
								setUserState(((IService)chat).getServiceIdentifier().getProviderId(), STATE_BROKEN);
								done(cfut);
							}
						});
					}
					
					public void finished()
					{
						done(ifut);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						done(ifut);
					}
					
					public void	done(IFuture<?> fut)
					{
						futures.remove(fut);
						if(futures.isEmpty())
						{
							ret.setResult(null);
						}						
					}
				});
				
				return ret;
			}
		});
	}
	
	/**
	 *  Add a user or change its state.
	 */
	public void	setUserState(final IComponentIdentifier user, final String state)
	{
		// Called on component thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				users.put(user, state);
				((DefaultTableModel)table.getModel()).fireTableDataChanged();
				table.getParent().invalidate();
				table.getParent().doLayout();
				table.repaint();
			}
		});
	}
	
	//-------- helper classes --------
	
	/**
	 *  Table model for list of users.
	 */
	public class UserTableModel	extends DefaultTableModel
	{
		protected String[]	columns	= new String[]{"User"};
		
		public int getColumnCount()
		{
			return columns.length;
		}
		public String getColumnName(int i)
		{
			return columns[i];
		}
		public Class<?> getColumnClass(int i)
		{
			return String.class;
		}
		public int getRowCount()
		{
			return users.size();
		}
		public Object getValueAt(int row, int column)
		{
			IComponentIdentifier[]	cids	= users.keySet().toArray(new IComponentIdentifier[users.size()]);
//			return column==0 ? cids[row] : users.get(cids[row]);
			return cids[row];
		}
		
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
		public void setValueAt(Object val, int row, int column)
		{
		}
		
		public void addTableModelListener(TableModelListener l)
		{
		}
		public void removeTableModelListener(TableModelListener l)
		{
		}
	}
}

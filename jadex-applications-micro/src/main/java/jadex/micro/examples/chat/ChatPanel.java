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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
	public static final String	STATE_READY	= "ready";
	
	/** The user state for found but not yet responding user. */
	public static final String	STATE_RECEIVING	= "receiving";
	
	/** The user state for a responding user. */
	public static final String	STATE_IDLE	= IChatService.STATE_IDLE;
	
	/** The user state for a user typing a message. */
	public static final String	STATE_TYPING	= IChatService.STATE_TYPING;
	
	/** The user state for a not responding user. */
	public static final String	STATE_BROKEN	= "not responding";
	
	/** The user state for a no longer available user. */
	public static final String	STATE_DEAD	= "dead";
	
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		STATE_READY,		SGUI.makeIcon(ChatPanel.class, "images/user_blue.png"),
		STATE_RECEIVING,			SGUI.makeIcon(ChatPanel.class, "images/user_yellow.png"),
		STATE_IDLE,	SGUI.makeIcon(ChatPanel.class, "images/user_green.png"),
		STATE_TYPING,	SGUI.makeIcon(ChatPanel.class, "images/user_typing.png"),
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
	
	/** The status field. */
	protected JLabel	status;
	
	/** The known chat users (cid->user state). */
	protected Map<IComponentIdentifier, String>	users;
	
	/** The user table. */
	protected JTable	table;
	
	/** The request counter for coordinating gui updates. */
	protected int	reqcnt;
	
	/** The typing state. */
	protected boolean	typing;

	
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
				this.setText(cid.getName());
				this.setToolTipText("State: "+users.get(cid));
				Icon	icon	= icons.getIcon(users.get(cid));
				this.setIcon(icon);
				return this;
			}
		});
		status	= new JLabel();
//		final JButton	refresh	= new JButton("Refresh");
		JPanel	listpan	= new JPanel(new BorderLayout());
		listpan.add(status, BorderLayout.NORTH);
		listpan.add(userpan, BorderLayout.CENTER);
//		listpan.add(refresh, BorderLayout.SOUTH);

		JPanel south = new JPanel(new BorderLayout());
		final JTextField tf = new JTextField();
		final JButton send = new JButton("Send");
		south.add(tf, BorderLayout.CENTER);
		south.add(send, BorderLayout.EAST);
		tf.getDocument().addDocumentListener(new DocumentListener()
		{
			public void removeUpdate(DocumentEvent e)
			{
				update();
			}
			
			public void insertUpdate(DocumentEvent e)
			{
				update();
			}
			
			public void changedUpdate(DocumentEvent e)
			{
				update();
			}
			
			public void update()
			{
				boolean	newtyping	= tf.getText().length()!=0;
				if(newtyping!=typing)
				{
					typing	= newtyping;
					postStatus(typing ? STATE_TYPING : STATE_IDLE);
				}
			}			
		});

		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final int	request	= startRequest("Sending message...");
				String	msg	= tf.getText();
				tf.setText("");
				typing	= false;
				tell(msg, request).addResultListener(new SwingDefaultResultListener<Void>()
				{
					public void customResultAvailable(Void result)
					{
						endRequest(request, "Sending completed.");
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

		JSplitPanel	split	= new JSplitPanel(JSplitPanel.HORIZONTAL_SPLIT, listpan, main);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(0.3);
		this.setLayout(new BorderLayout());
		this.add(split, BorderLayout.CENTER);
		this.add(south, BorderLayout.SOUTH);
		
//		ActionListener	refreshlis	= new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				users.clear();
//			}
//		};
//		refresh.addActionListener(refreshlis);

		// Post availability, also gets list of initial users.
		postStatus(STATE_IDLE);
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
	public void addMessage(final IComponentIdentifier sender, final String text)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				setUserState(sender, STATE_IDLE, -1);
				StringBuffer buf = new StringBuffer();
				buf.append("[").append(df.format(new Date(clock.getTime()))).append(", ")
					.append(sender.getName()).append("]: ").append(text).append(lf);
				chatarea.append(buf.toString());
			}
		});
	}
	
	/**
	 *  Tell something.
	 *  @param text The text.
	 */
	public IFuture<Void>	tell(final String text, final int request)
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
						setUserState(((IService)chat).getServiceIdentifier().getProviderId(), STATE_RECEIVING, request);
						futures.add(cfut);
						cfut.addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								setUserState(((IService)chat).getServiceIdentifier().getProviderId(), STATE_IDLE, request);
								done(cfut);
							}
							public void exceptionOccurred(Exception exception)
							{
								setUserState(((IService)chat).getServiceIdentifier().getProviderId(), STATE_BROKEN, request);
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
	 *  Post the local state to available chatters
	 */
	public IFuture<Void>	postStatus(final String status)
	{
		final int	request	= startRequest("Refreshing...");
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IIntermediateFuture<IChatService> ifut = ia.getServiceContainer().getRequiredServices("chatservices");
				ifut.addResultListener(new IntermediateDefaultResultListener<IChatService>()
				{
					public void intermediateResultAvailable(final IChatService chat)
					{
						// Assume connected at first for found users.
						setUserState(((IService)chat).getServiceIdentifier().getProviderId(), STATE_IDLE, request);
						
						chat.status(status);
					}
					
					public void finished()
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								endRequest(request, "Refresh completed.");
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
	 *  Add a user or change its state.
	 */
	public void	setUserState(final IComponentIdentifier user, final String state, final int request)
	{
		// Called on component thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if(request==reqcnt || request==-1)
				{
					users.put(user, state);
					((DefaultTableModel)table.getModel()).fireTableDataChanged();
					table.getParent().invalidate();
					table.getParent().doLayout();
					table.repaint();
				}
			}
		});
	}
	
	/**
	 *  Start an asynchronous request.
	 */
	protected int	startRequest(String message)
	{
//		tf.setEnabled(false);
//		send.setEnabled(false);
		status.setText(message);

		// Set user states to unknown before sending.
		IComponentIdentifier[]	cids	= users.keySet().toArray(new IComponentIdentifier[users.size()]);
		for(int i=0; i<cids.length; i++)
		{
			// Change user states to unknown, except for those who are already determined as dead (will be resurrected if found in search anyways).
			if(!STATE_DEAD.equals(users.get(cids[i])))
			{
				users.put(cids[i], STATE_READY);
			}
		}
		((DefaultTableModel)table.getModel()).fireTableDataChanged();
		table.getParent().invalidate();
		table.getParent().doLayout();
		table.repaint();
		
		return ++reqcnt;	// Keep track of parallel sendings and update gui only for last.		
	}
	
	/**
	 *  Called on request end
	 */
	protected void	endRequest(int request, String message)
	{
		// Set states of unavailable users to dead
		if(request==reqcnt)
		{
			status.setText(message);
			
			IComponentIdentifier[]	cids	= users.keySet().toArray(new IComponentIdentifier[users.size()]);
			for(int i=0; i<cids.length; i++)
			{
				if(STATE_READY.equals(users.get(cids[i])))
				{
					users.put(cids[i], STATE_DEAD);
				}
			}
			((DefaultTableModel)table.getModel()).fireTableDataChanged();
			table.getParent().invalidate();
			table.getParent().doLayout();
			table.repaint();
		}
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

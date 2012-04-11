package jadex.micro.examples.chat;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.service.IService;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.remote.ServiceOutputConnection;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
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
	
	//-------- attributes --------
	
	/** The agent. */
	protected IExternalAccess agent;
	
	/** The clock service. */
	protected IClockService	clock;
	
	/** The text area. */
	protected JTextArea chatarea;
	
	/** The known chat users (cid->user state). */
	protected Map<IComponentIdentifier, ChatUser>	users;
	
	/** The user table. */
	protected JTable	table;
	
	/** The typing state. */
	protected boolean	typing;

	/** The request counter for coordinating gui updates. */
	protected int	reqcnt;
	
	/** The dead users determined during a request. */
	protected Set<IComponentIdentifier>	deadusers;
	
	/** The downloads. */
	protected Map<Integer, FileInfo> downloads;
	
	/** The uploads. */
	protected Map<Integer, FileInfo> uploads;

	/** The download table. */
	protected JTable dtable;
	
	/** The upload table. */
	protected JTable utable;
	
	//-------- constructors --------
	
	/**
	 *  Create a new chat panel.
	 */
	public ChatPanel(final IExternalAccess agent, IClockService clock)
	{
		this.agent	= agent;
		this.clock	= clock;
		this.users	= new LinkedHashMap<IComponentIdentifier, ChatUser>();
		this.downloads = new LinkedHashMap<Integer, FileInfo>();
		this.uploads = new LinkedHashMap<Integer, FileInfo>();
		
		DefaultTableCellRenderer userrend = new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
			{
				super.getTableCellRendererComponent(table, value, selected, focus, row, column);
				IComponentIdentifier	cid	= (IComponentIdentifier)value;
				this.setText(cid.getName());
				this.setToolTipText("State: "+users.get(cid));
				Icon	icon	= users.get(cid).getIcon();
				this.setIcon(icon);
				return this;
			}
		};
		
		DefaultTableCellRenderer cidrend = new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
			{
				super.getTableCellRendererComponent(table, value, selected, focus, row, column);
				IComponentIdentifier	cid	= (IComponentIdentifier)value;
				this.setText(cid.getName());
				this.setToolTipText(SUtil.arrayToString(cid.getAddresses()));
				return this;
			}
		};
		
		DefaultTableCellRenderer filecellrend = new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
			{
				super.getTableCellRendererComponent(table, value, selected, focus, row, column);
				File f = (File)value;
				this.setText(f.getName());
				this.setToolTipText(f.getAbsolutePath());
				return this;
			}
		};
		DefaultTableCellRenderer progressrend = new DefaultTableCellRenderer()
		{
			JProgressBar bar = new JProgressBar(0,100);
			{
				bar.setStringPainted(true);
			}
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
			{
				bar.setValue((int)(((Double)value).doubleValue()*100));
				return bar;
			}
		};
		
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
		table.getColumnModel().getColumn(0).setCellRenderer(userrend);

		final JFileChooser chooser = new JFileChooser(".");
		MouseListener lis = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e) 
			{
				trigger(e);
			}

			public void mouseReleased(MouseEvent e) 
			{
				trigger(e);
			}
			
			protected void trigger(MouseEvent e)
			{
				if(e.isPopupTrigger()) 
				{
					int row = table.rowAtPoint(e.getPoint());
					IComponentIdentifier cid = (IComponentIdentifier)((UserTableModel)table.getModel()).getValueAt(row, 0);
					createMenu(cid).show(e.getComponent(), e.getX(), e.getY());
				}
			}
			
			protected JPopupMenu createMenu(final IComponentIdentifier cid)
			{
				final JPopupMenu menu = new JPopupMenu();
				JMenuItem mi = new JMenuItem("Send file ...");
				mi.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(JFileChooser.APPROVE_OPTION==chooser.showOpenDialog(ChatPanel.this))
						{
							sendFile(chooser.getSelectedFile(), cid).addResultListener(new IResultListener<Void>()
							{
								public void resultAvailable(Void result)
								{
									System.out.println("transferred file");
								}
								public void exceptionOccurred(Exception exception)
								{
									System.out.println("ex: "+exception.getMessage());
								}
							});
						}
					}
				});
				menu.add(mi);
				return menu;
			}
		};
		table.addMouseListener(lis);
		table.getTableHeader().addMouseListener(lis);
		
		JPanel	listpan	= new JPanel(new BorderLayout());
		listpan.add(userpan, BorderLayout.CENTER);

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
					postStatus(typing ? IChatService.STATE_TYPING : IChatService.STATE_IDLE);
				}
			}			
		});

		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String	msg	= tf.getText();
				tf.setText("");
				typing	= false;
				final int	request	= startRequest();
				tell(msg, request).addResultListener(new SwingDefaultResultListener<Void>(ChatPanel.this)
				{
					public void customResultAvailable(Void result)
					{
						endRequest(request);
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						super.customExceptionOccurred(exception);
						endRequest(request);
					}
				});
			}
		};
		tf.addActionListener(al);
		send.addActionListener(al);

		JSplitPanel	split	= new JSplitPanel(JSplitPanel.HORIZONTAL_SPLIT, listpan, main);
		split.setOneTouchExpandable(true);
		split.setDividerLocation(0.3);
		
		JPanel msgpane = new JPanel(new BorderLayout());
		msgpane.add(split, BorderLayout.CENTER);
		msgpane.add(south, BorderLayout.SOUTH);
		
//		JSplitPanel udpane = new JSplitPanel(JSplitPanel.VERTICAL_SPLIT);
		dtable = new JTable(new FileTableModel(true));
		utable = new JTable(new FileTableModel(false));
//		dtable.setPreferredSize(new Dimension(400, 100));
//		utable.setPreferredSize(new Dimension(400, 100));
		JScrollPane dtpan = new JScrollPane(dtable);
		JScrollPane utpan = new JScrollPane(utable);
//		dtpan.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Downloads"));
//		utpan.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Uploads"));
//		udpane.add(dtpan);
//		udpane.add(utpan);
//		udpane.add(dtable);
//		udpane.add(utable);
//		udpane.setOneTouchExpandable(true);
//		udpane.setDividerLocation(0.5);
		
		dtable.getColumnModel().getColumn(0).setCellRenderer(filecellrend);
		dtable.getColumnModel().getColumn(1).setCellRenderer(cidrend);
		dtable.getColumnModel().getColumn(4).setCellRenderer(progressrend);
		utable.getColumnModel().getColumn(0).setCellRenderer(filecellrend);
		utable.getColumnModel().getColumn(1).setCellRenderer(cidrend);
		utable.getColumnModel().getColumn(4).setCellRenderer(progressrend);
		
		JTabbedPane tpane = new JTabbedPane();
		tpane.add("Messaging", msgpane);
		tpane.add("Downloads", dtpan);
		tpane.add("Uploads", utpan);
		
		this.setLayout(new BorderLayout());
		this.add(tpane, BorderLayout.CENTER);
		
		// Post availability, also gets list of initial users.
		postStatus(IChatService.STATE_IDLE);
	}
	
	//-------- methods called from gui --------
	
	/**
	 *  Send a message.
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
				final Set<IFuture<?>> futures	= new HashSet<IFuture<?>>();
				
				final IIntermediateFuture<IChatService> ifut = ia.getServiceContainer().getRequiredServices("chatservices");
				futures.add(ifut);
				
				ifut.addResultListener(new IntermediateDefaultResultListener<IChatService>()
				{
					public void intermediateResultAvailable(final IChatService chat)
					{
						// Send chat message and wait for future.
						final IFuture<Void>	cfut	= chat.message(text);
						setReceiving(chat, request, true);
						futures.add(cfut);
						cfut.addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								setReceiving(chat, request, false);
								done(cfut);
							}
							public void exceptionOccurred(Exception exception)
							{
								setReceiving(chat, -2, true);
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
		final int	request	= startRequest();
		return agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IIntermediateFuture<IChatService> ifut = ia.getServiceContainer().getRequiredServices("chatservices");
				ifut.addResultListener(new IntermediateDefaultResultListener<IChatService>()
				{
					public void intermediateResultAvailable(final IChatService chat)
					{
						setReceiving(chat, -1, false);	// Adds user if not already known.
						
						chat.status(status);
					}
					
					public void finished()
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								endRequest(request);
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
	
	//-------- helper methods --------

	/**
	 *  Start an asynchronous request.
	 *  Used to collect dead users.
	 */
	protected int	startRequest()
	{
		assert SwingUtilities.isEventDispatchThread();
		// Remember known users to determine dead ones.
		deadusers	= new HashSet<IComponentIdentifier>(users.keySet());
		return ++reqcnt;	// Keep track of parallel sendings and update gui only for last.		
	}
	
	/**
	 *  Called on request end
	 *  Used to collect dead users.
	 */
	protected void	endRequest(int request)
	{
		assert SwingUtilities.isEventDispatchThread();
		// Set states of unavailable users to dead
		if(request==reqcnt)
		{
			for(IComponentIdentifier cid: deadusers)
			{
				if(users.containsKey(cid))
					users.get(cid).setState(IChatService.STATE_DEAD);
			}
			((DefaultTableModel)table.getModel()).fireTableDataChanged();
			table.getParent().invalidate();
			table.getParent().doLayout();
			table.repaint();
		}
	}
	
	/**
	 *  Add a user or change its state.
	 */
	public void	setReceiving(final IChatService chat, final int receiving, final boolean b)
	{
		// Called on component thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				IComponentIdentifier	cid	= ((IService)chat).getServiceIdentifier().getProviderId();
				deadusers.remove(cid);
				ChatUser	cu	= users.get(cid);
				if(cu==null)
				{
					cu	= new ChatUser(chat);
					users.put(cid, cu);
				}
			
				cu.setReceiving(receiving, b);
				((DefaultTableModel)table.getModel()).fireTableDataChanged();
				table.getParent().invalidate();
				table.getParent().doLayout();
				table.repaint();
			}
		});
	}

	//-------- methods called from service --------
	
	/**
	 *  Create a gui frame.
	 */
	public static IFuture<ChatPanel>	createGui(final IExternalAccess agent, final IClockService clock)
	{
		final Future<ChatPanel>	ret	= new Future<ChatPanel>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
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
				ret.setResult(cp);
			}
		});
		
		return ret;
	}

	/**
	 *  Close the gui.
	 */
	public void dispose()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				SGUI.getWindowParent(ChatPanel.this).dispose();
			}
		});
	}
	
	/**
	 *  Add a message to the text area.
	 */
	public void addMessage(final IComponentIdentifier cid, final String text)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				StringBuffer buf = new StringBuffer();
				buf.append("[").append(df.format(new Date(clock.getTime()))).append(", ")
					.append(cid.getName()).append("]: ").append(text).append(lf);
				chatarea.append(buf.toString());
				
				deadusers.remove(cid);
				ChatUser	cu	= users.get(cid);
				if(cu==null)
				{
					cu	= new ChatUser(cid);
					users.put(cid, cu);
					((DefaultTableModel)table.getModel()).fireTableDataChanged();
					table.getParent().invalidate();
					table.getParent().doLayout();
					table.repaint();
				}
			}
		});
	}
	
	/**
	 *  Add a user or change its state.
	 */
	public void	setUserState(final IComponentIdentifier cid, final String newstate)
	{
		// Called on component thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				deadusers.remove(cid);
				ChatUser	cu	= users.get(cid);
				if(cu==null)
				{
					cu	= new ChatUser(cid);
					users.put(cid, cu);
				}
				cu.setState(newstate);
				((DefaultTableModel)table.getModel()).fireTableDataChanged();
				table.getParent().invalidate();
				table.getParent().doLayout();
				table.repaint();
			}
		});
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> sendFile(final File file, final IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IFuture<IChatService> fut = ia.getServiceContainer().getService(IChatService.class, cid);
				fut.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IChatService, Void>(ret)
				{
					public void customResultAvailable(IChatService cs)
					{
						final ServiceOutputConnection ocon = new ServiceOutputConnection();
						final IInputConnection icon = ocon.getInputConnection();
						final long size = file.length();
						
						ITerminableIntermediateFuture<Long> fut = cs.sendFile(file.getName(), size, icon);
						final boolean[] started = new boolean[1];
						fut.addResultListener(new IIntermediateResultListener<Long>()
						{
							public void intermediateResultAvailable(Long result)
							{
								if(!started[0])
								{
									started[0] = true;
									send(file, ocon, cid);//.addResultListener(new DelegationResultListener<Void>(ret));
								}
							}
							
							public void finished()
							{
//								System.out.println("sending finished");
								ret.setResult(null);
							}
							
							public void resultAvailable(Collection<Long> result)
							{
//								System.out.println("result");
								ret.setResult(null);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								System.out.println("exception: "+exception);
								ret.setException(exception);
							}
						});
					}
				}));
				
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> send(final File file, final IOutputConnection ocon, final IComponentIdentifier receiver)
	{
		final Future<Void> ret = new Future<Void>();
		try
		{
			final long[] filesize = new long[1];
			final FileInfo fi = new FileInfo(file, receiver, file.length(), 0, FileInfo.WAITING);
			final FileInputStream fis = new FileInputStream(file);
			
			IComponentStep<Void> step = new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					try
					{
						
						final IComponentStep<Void> self = this;
						int size = Math.min(200000, fis.available());
						filesize[0] += size;
						byte[] buf = new byte[size];
						int read = 0;
						while(read!=buf.length)
						{
							read += fis.read(buf, read, buf.length-read);
						}
						ocon.write(buf);
//						System.out.println("wrote: "+size);
						
						fi.setState(FileInfo.TRANSFERRING);
						fi.setDone(filesize[0]);
						updateUpload(fi);
						if(fis.available()>0)
						{
							ia.waitForDelay(1000, self);
	//						agent.scheduleStep(self);
	//						ocon.waitForReady().addResultListener(new IResultListener<Void>()
	//						{
	//							public void resultAvailable(Void result)
	//							{
	//								agent.scheduleStep(self);
	////												agent.waitFor(10, self);
	//							}
	//							public void exceptionOccurred(Exception exception)
	//							{
	//								exception.printStackTrace();
	//								ocon.close();
	//							}
	//						});
						}
						else
						{
							ocon.close();
							fi.setState(FileInfo.COMPLETED);
							updateUpload(fi);
							ret.setResult(null);
						}
					}
					catch(Exception e)
					{
						fi.setState(FileInfo.ERROR);
						updateUpload(fi);
						e.printStackTrace();
						ret.setException(e);
					}
					
					return IFuture.DONE;
				}
			};
			agent.scheduleStep(step);
		}
		catch(Exception e)
		{
			ret.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture<File> acceptFile(final String filename, final long size, final IComponentIdentifier sender)
	{
		final Future<File> ret = new Future<File>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				PropertiesPanel pp = new PropertiesPanel();
				final JTextField fn = pp.createTextField("Filename: ", "./"+filename);
				pp.createTextField("Size [bytes]: ", ""+size);
				pp.createTextField("Sender: ", ""+(sender==null? sender: sender.getName()));
				
				if(0==JOptionPane.showOptionDialog(null, pp, "Incoming File Transfer", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Accept", "Cancel"}, "Accept"))
				{
					ret.setResult(new File(fn.getText()));
				}
				else
				{
					ret.setException(new RuntimeException("Denied"));
				}
			}
		});
	
		return ret;
	}
	
	/**
	 * 
	 */
	public void updateDownload(final FileInfo fi)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				((FileTableModel)dtable.getModel()).updateFile(fi);
			}
		});
	}
	
	/**
	 * 
	 */
	public void updateUpload(final FileInfo fi)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				((FileTableModel)utable.getModel()).updateFile(fi);
			}
		});
	}
	
	//-------- helper classes --------
	
	/**
	 *  Table model for list of users.
	 */
	public class UserTableModel	extends DefaultTableModel
	{
		protected String[]	columns	= new String[]{"Users"};
		
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
	
	/**
	 *  Table model for up/downloads.
	 */
	public class FileTableModel	extends DefaultTableModel
	{
		protected boolean down;
		protected String[]	columns;

		public FileTableModel(boolean down)
		{
			this.down = down;
			columns = new String[]{"Name", down? "Sender": "Receiver", "Size", "Done", "%", "State"};
		}

		protected JTable getTable()
		{
			return down? dtable: utable;
		}
		protected Map<Integer, FileInfo> getDataMap()
		{
			return down? downloads: uploads;
		}
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
			return getDataMap().size();
		}
		public Object getValueAt(int row, int column)
		{
			Object ret;
			FileInfo[] files = getDataMap().values().toArray(new FileInfo[getDataMap().size()]);
			if(column==0)
			{
				ret = files[row].getFile();
			}
			else if(column==1)
			{
				ret = files[row].getSender();
			}
			else if(column==2)
			{
				ret = files[row].getSize();
			}
			else if(column==3)
			{
				ret = files[row].getDone();
			}
			else if(column==4)
			{
				ret = new Double(((double)files[row].getDone())/files[row].getSize());
			}
			else if(column==5)
			{
				ret = files[row].getState();
			}
			else
			{
				throw new RuntimeException("Unknown column");
			}
			return ret;
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
		
		public void updateFile(FileInfo fi)
		{
			FileInfo oldfi = getDataMap().put(fi.getId(), fi);
//			if(oldfi!=null)
//			{
//				int row = 0;
//				for(Iterator<Integer> it=downloads.keySet().iterator(); it.hasNext(); row++)
//				{
//					Integer nextid = it.next();
//					if(nextid==oldfi.getId())
//						break;
//				}
//				System.out.println("update old: "+row);
//				fireTableRowsUpdated(row, row);
////				fireTableDataChanged();
//			}
//			else
//			{
//				System.out.println("update new: "+(downloads.size()-1));
//				fireTableRowsInserted(downloads.size()-1, downloads.size()-1);
//			}
			
			fireTableDataChanged();
			getTable().getParent().invalidate();
			getTable().getParent().doLayout();
			getTable().repaint();
		}
	}
}

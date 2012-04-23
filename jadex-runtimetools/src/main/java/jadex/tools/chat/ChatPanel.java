package jadex.tools.chat;

import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateDefaultResultListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *  Panel for displaying the chat.
 */
public class ChatPanel extends AbstractServiceViewerPanel<IChatGuiService>
{
	//-------- constants --------
	
	/** The linefeed separator. */
	public static final String lf = (String)System.getProperty("line.separator");
	
	/** The time format. */
	public static final DateFormat	df	= new SimpleDateFormat("HH:mm:ss");
	
	/** The notification sound for a newly online user. */
	public static final String	NOTIFICATION_NEW_USER	= "newuser";
	
	/** The notification sound for a new message. */
	public static final String	NOTIFICATION_NEW_MSG	= "newmsg";
	
	/** The notification sound for an incoming file request. */
	public static final String	NOTIFICATION_NEW_FILE	= "newfile";
	
	/** The notification sound for a successfully completed file. */
	public static final String	NOTIFICATION_FILE_COMPLETE	= "filecomplete";
	
	/** The notification sound for an aborted or failed file transfer. */
	public static final String	NOTIFICATION_FILE_ABORT	= "fileabort";
	
	/** The default notification sounds. */
	protected static Map<String, String>	NOTIFICATION_SOUNDS;
	
	static
	{
		NOTIFICATION_SOUNDS	= new HashMap<String, String>();
		NOTIFICATION_SOUNDS.put(NOTIFICATION_NEW_USER, "sounds/gong.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_NEW_MSG, "sounds/ping.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_NEW_FILE, "sounds/cuckoo_clock.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_FILE_COMPLETE, "sounds/music_box.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_FILE_ABORT, "sounds/blurps.wav");
	}
	
	//-------- attributes --------
	
	/** The chat panel. */
	protected JPanel	panel;
	
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
	
	/** The download table. */
	protected JTable dtable;
	
	/** The upload table. */
	protected JTable utable;
	
	/** Registration at the service. */
	ISubscriptionIntermediateFuture<ChatEvent>	subscription;
	
	//-------- constructors --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param service	The service.
	 */
	public IFuture<Void> init(IControlCenter jcc, IService service)
	{
		final Future<Void>	ret	= new Future<Void>();
		super.init(jcc, service).addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
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
				
				DefaultTableCellRenderer byterend = new DefaultTableCellRenderer()
				{
					public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
					{
						super.getTableCellRendererComponent(table, value, selected, focus, row, column);
						long	bytes	= ((Number)value).longValue();
						this.setText(SUtil.bytesToString(bytes));
						this.setToolTipText(bytes+" bytes");
						return this;
					}
				};
				
				DefaultTableCellRenderer filecellrend = new DefaultTableCellRenderer()
				{
					public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
					{
						super.getTableCellRendererComponent(table, value, selected, focus, row, column);
						File f = new File((String)value);
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
				
				DefaultTableCellRenderer speedrend = new DefaultTableCellRenderer()
				{
					public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
					{
						super.getTableCellRendererComponent(table, value, selected, focus, row, column);
						TransferInfo	info	= (TransferInfo) value;
						if(TransferInfo.STATE_TRANSFERRING.equals(info.getState()))
						{
							double	bytes	= info.getSpeed();
							this.setText(SUtil.bytesToString((long)bytes)+"/sec.");
							this.setToolTipText(bytes+" bytes/sec.");
						}
						else
						{
							this.setText("");
							this.setToolTipText("");
						}
						return this;
					}
				};

				users	= new LinkedHashMap<IComponentIdentifier, ChatUser>();

				
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
				table.setTransferHandler(new TransferHandler()
				{
					public boolean	canImport(TransferHandler.TransferSupport support)
					{
						// Todo: check if chat component is local.
						
						boolean	ret	= support.isDrop() && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
							&& (support.getSourceDropActions()&TransferHandler.COPY)!=0;
						
						if(ret)
							support.setDropAction(TransferHandler.COPY);

						return ret;
					}

					public boolean importData(TransferHandler.TransferSupport support)
					{
						boolean	success;
						try
						{
							JTable.DropLocation	droploc	= (JTable.DropLocation)support.getDropLocation();
							IComponentIdentifier	cid	= (IComponentIdentifier)table.getModel().getValueAt(droploc.getRow(), 0);
							
							List<File>	files	= (List<File>)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
//							System.out.println("importData: "+files);
							for(File file: files)
							{
								getService().sendFile(file.getAbsolutePath(), cid).addResultListener(new SwingDefaultResultListener<Void>(panel)
								{
									public void customResultAvailable(Void result)
									{
										// Transfer initiated -> ignore.
									}
								});
							}
							success	= true;
						}
						catch(Exception e)
						{
							System.err.println("Drop error: "+e);
							success	= false;
						}
					    return success;
					}  
				});

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
							table.setRowSelectionInterval(row, row);
							IComponentIdentifier cid = (IComponentIdentifier)((UserTableModel)table.getModel()).getValueAt(row, 0);
							createMenu(cid).show(e.getComponent(), e.getX(), e.getY());
						}
					}
					
					protected JPopupMenu createMenu(final IComponentIdentifier cid)
					{
						// Todo: check if is local, otherwise use remote file chooser.
						
						final JPopupMenu menu = new JPopupMenu();
						JMenuItem mi = new JMenuItem("Send file ...");
						mi.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								if(JFileChooser.APPROVE_OPTION==chooser.showOpenDialog(panel))
								{
									File file = chooser.getSelectedFile();
									getService().sendFile(file.getAbsolutePath(), cid).addResultListener(new SwingDefaultResultListener<Void>(panel)
									{
										public void customResultAvailable(Void result)
										{
											// Transfer initiated -> ignore.
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
						tell(msg, request).addResultListener(new SwingDefaultResultListener<Void>(panel)
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
				
//				JSplitPanel udpane = new JSplitPanel(JSplitPanel.VERTICAL_SPLIT);
				dtable = new JTable(new FileTableModel(true));
				utable = new JTable(new FileTableModel(false));
//				dtable.setPreferredSize(new Dimension(400, 100));
//				utable.setPreferredSize(new Dimension(400, 100));
				JScrollPane dtpan = new JScrollPane(dtable);
				JScrollPane utpan = new JScrollPane(utable);
//				dtpan.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Downloads"));
//				utpan.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Uploads"));
//				udpane.add(dtpan);
//				udpane.add(utpan);
//				udpane.add(dtable);
//				udpane.add(utable);
//				udpane.setOneTouchExpandable(true);
//				udpane.setDividerLocation(0.5);
				
				dtable.getColumnModel().getColumn(0).setCellRenderer(filecellrend);
				dtable.getColumnModel().getColumn(1).setCellRenderer(cidrend);
				dtable.getColumnModel().getColumn(2).setCellRenderer(byterend);
				dtable.getColumnModel().getColumn(3).setCellRenderer(byterend);
				dtable.getColumnModel().getColumn(4).setCellRenderer(progressrend);
				dtable.getColumnModel().getColumn(6).setCellRenderer(speedrend);
				
				utable.getColumnModel().getColumn(0).setCellRenderer(filecellrend);
				utable.getColumnModel().getColumn(1).setCellRenderer(cidrend);
				utable.getColumnModel().getColumn(2).setCellRenderer(byterend);
				utable.getColumnModel().getColumn(3).setCellRenderer(byterend);
				utable.getColumnModel().getColumn(4).setCellRenderer(progressrend);
				utable.getColumnModel().getColumn(6).setCellRenderer(speedrend);
				
				FileTransferMouseAdapter dlis = new FileTransferMouseAdapter(dtable);
				FileTransferMouseAdapter ulis = new FileTransferMouseAdapter(utable);
				dtable.addMouseListener(dlis);
				dtable.getTableHeader().addMouseListener(dlis);
				utable.addMouseListener(ulis);
				utable.getTableHeader().addMouseListener(ulis);
				
				JTabbedPane tpane = new JTabbedPane();
				tpane.add("Messaging", msgpane);
				tpane.add("Downloads", dtpan);
				tpane.add("Uploads", utpan);
				
				panel	= new JPanel(new BorderLayout());
				panel.add(tpane, BorderLayout.CENTER);
				
				subscription	= getService().subscribeToEvents();
				subscription.addResultListener(new SwingIntermediateDefaultResultListener<ChatEvent>(panel)
				{
					public void customIntermediateResultAvailable(ChatEvent ce)
					{
						if(ChatEvent.TYPE_MESSAGE.equals(ce.getType()))
						{
							addMessage(ce.getComponentIdentifier(), (String)ce.getValue());
						}
						else if(ChatEvent.TYPE_STATECHANGE.equals(ce.getType()))
						{
							setUserState(ce.getComponentIdentifier(), (String)ce.getValue());
						}
						else if(ChatEvent.TYPE_FILE.equals(ce.getType()))
						{
							final TransferInfo	ti	= (TransferInfo) ce.getValue();
							updateTransfer(ti);
							
							if(ti.isDownload() && TransferInfo.STATE_WAITING.equals(ti.getState()))
							{
								notifyChatEvent(NOTIFICATION_NEW_FILE, ti.getOther(), ti, false);
								
								if(panel.isShowing())
								{
									acceptFile(new File(ti.getFile()).getName(), ti.getSize(), ti.getOther())
										.addResultListener(new IResultListener<File>()
									{
										public void resultAvailable(File result)
										{
											getService().acceptFile(ti.getId(), result.getAbsolutePath());
										}
										
										public void exceptionOccurred(Exception exception)
										{
											getService().rejectFile(ti.getId());
										}
									});
								}
							}
						}
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						if(!isShutdown())
						{
							super.customExceptionOccurred(exception);
						}
					}
				});
				
				ret.setResult(null);
				
				getService().findUsers().addResultListener(new SwingIntermediateDefaultResultListener<IChatService>(panel)
				{
					public void customIntermediateResultAvailable(IChatService chat)
					{
						setReceiving(chat, -1, false);
					}
				});
				
				getService().getFileTransfers().addResultListener(new SwingIntermediateDefaultResultListener<TransferInfo>(panel)
				{
					public void customIntermediateResultAvailable(TransferInfo ti)
					{
						updateTransfer(ti);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the plugin view.
	 */
	public JComponent getComponent()
	{
		return panel;
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		Future<Void>	ret	= new Future<Void>();
		super.shutdown().addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				subscription.terminate();				
			}
		});
		return ret;
	}
	
	//-------- methods called from gui --------
	
	/**
	 *  Send a message.
	 *  @param text The text.
	 */
	public IFuture<Void>	tell(String text, final int request)
	{
		final Future<Void>	ret	= new Future<Void>();
		getService().message(text).addResultListener(new IntermediateDefaultResultListener<IChatService>()
		{
			public void intermediateResultAvailable(final IChatService chat)
			{
				setReceiving(chat, request, false);
			}
			
			public void finished()
			{
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Post the local state to available chatters
	 */
	public IFuture<Void>	postStatus(String status)
	{
		final Future<Void>	ret	= new Future<Void>();
		getService().status(status).addResultListener(new IntermediateDefaultResultListener<IChatService>()
		{
			public void intermediateResultAvailable(final IChatService chat)
			{
				setReceiving(chat, -1, false);
			}
			
			public void finished()
			{
				ret.setResult(null);
			}
		});
		
		return ret;
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
				if(deadusers!=null)
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
	 *  Add a message to the text area.
	 */
	public void addMessage(final IComponentIdentifier cid, final String text)
	{
		SServiceProvider.getService(getJCC().getJCCAccess().getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DefaultResultListener<IClockService>()
		{
			public void resultAvailable(final IClockService clock)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						StringBuffer buf = new StringBuffer();
						buf.append("[").append(df.format(new Date(clock.getTime()))).append(", ")
							.append(cid.getName()).append("]: ").append(text).append(lf);
						chatarea.append(buf.toString());
						
						notifyChatEvent(NOTIFICATION_NEW_MSG, cid, text, false);
						
						if(deadusers!=null)
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
				boolean	isnew	= false;
				if(deadusers!=null)
					deadusers.remove(cid);
				ChatUser	cu	= users.get(cid);
				if(cu==null)
				{
					cu	= new ChatUser(cid);
					users.put(cid, cu);
					isnew	= true;
				}
				else if(IChatService.STATE_DEAD.equals(cu.getState()))
				{
					isnew	= true;
				}
				cu.setState(newstate);
				((DefaultTableModel)table.getModel()).fireTableDataChanged();
				table.getParent().invalidate();
				table.getParent().doLayout();
				table.repaint();
				
				if(isnew)
				{
					notifyChatEvent(NOTIFICATION_NEW_USER, cid, null, false);
				}
			}
		});
	}
	
	
	/**
	 *  Open dialog and check if user wants to receive the file.
	 */
	public IFuture<File> acceptFile(final String filename, final long size, final IComponentIdentifier sender)
	{
		final Future<File> ret = new Future<File>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				PropertiesPanel pp = new PropertiesPanel();
				JPanel fnp = new JPanel(new GridBagLayout());
				final JTextField tfpath = new JTextField(".", 15);
				JButton bupath = new JButton("...");
				bupath.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						JFileChooser ch = new JFileChooser(tfpath.getText());
						ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						if(JFileChooser.APPROVE_OPTION==ch.showOpenDialog(panel))
						{
							tfpath.setText(ch.getSelectedFile().getAbsolutePath());
						}
					}
				});
				bupath.setMargin(new Insets(0,0,0,0));
//				JTextField tfname = new JTextField(filename, 15);
				fnp.add(tfpath, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.WEST, 
					GridBagConstraints.BOTH, new Insets(0,0,0,2),0,0));
				fnp.add(bupath, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, 
					GridBagConstraints.NONE, new Insets(0,2,0,0),0,0));
//				fnp.add(tfname, new GridBagConstraints(2,0,1,1,1,1,GridBagConstraints.WEST, 
//					GridBagConstraints.BOTH, new Insets(0,2,0,2),0,0));
				pp.addComponent("File path: ", fnp);
				final JTextField tfname = pp.createTextField("File name: ", filename, true);
				pp.createTextField("Size: ", SUtil.bytesToString(size));
				pp.createTextField("Sender: ", ""+(sender==null? sender: sender.getName()));
				
				int res	= JOptionPane.showOptionDialog(panel, pp, "Incoming File Transfer", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Accept", "Reject", "Cancel"}, "Accept");
				if(0==res)
				{
					ret.setResult(new File(tfpath.getText()+File.separatorChar+tfname.getText()));
				}
				else if(1==res)
				{
					ret.setException(new RuntimeException(TransferInfo.STATE_REJECTED));
				}
				else
				{
					// No result for future -> nop
				}
			}
		});
	
		return ret;
	}
	

	/**
	 *  Show a status message and optionally play a notification sound.
	 */
	protected void	notifyChatEvent(String type, IComponentIdentifier source, Object value, boolean quiet)
	{
		// Ignore own messages and own online/offline state changes
		if(!((IService)getService()).getServiceIdentifier().getProviderId().equals(source))
		{
			String	text	= null;
			if(NOTIFICATION_NEW_MSG.equals(type))
			{
				text	= "New chat message from "+source+": "+value;
			}
			else if(NOTIFICATION_NEW_USER.equals(type))
			{
				text	= "New chat user online: "+source;
			}
			else if(NOTIFICATION_NEW_FILE.equals(type))
			{
				text	= "New file upload request from "+source+": "+new File(((TransferInfo)value).getFile()).getName();
			}
			else if(NOTIFICATION_FILE_COMPLETE.equals(type))
			{
				text	= ((TransferInfo)value).isDownload()
					? "Completed downloading '"+new File(((TransferInfo)value).getFile()).getName()+"' from "+source
					: "Completed uploading '"+new File(((TransferInfo)value).getFile()).getName()+"' to "+source;
			}
			else if(NOTIFICATION_FILE_ABORT.equals(type))
			{
				text	= ((TransferInfo)value).isDownload()
					? "Problem while downloading '"+new File(((TransferInfo)value).getFile()).getName()+"' from "+source
					: "Problem while uploading '"+new File(((TransferInfo)value).getFile()).getName()+"' to "+source;
			}
			
			if(text!=null)
			{
				// Add status component, if panel is not showing.
				if(!panel.isShowing())
				{
					JComponent	scomp	= getJCC().getStatusComponent("chat-status-comp");
					if(scomp==null)
					{
						scomp	= new JButton(ChatPlugin.getIcon());
						((JButton)scomp).setMargin(new Insets(0, 0, 0, 0));
						((JButton)scomp).addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								getJCC().showPlugin(ChatPlugin.PLUGIN_NAME);
								getJCC().removeStatusComponent("chat-status-comp");
							}
						});
						getJCC().addStatusComponent("chat-status-comp", scomp);
					}
					String	tip	= scomp.getToolTipText();
					if(tip==null || !tip.contains(text))
					{
						tip	= (tip==null || tip.length()==0) ? "<html>"+text+"</html>"
								: tip.substring(0, tip.length()-7)+"<br/>"+text+"</html>";
						scomp.setToolTipText(tip);
					}
				}
				
				getJCC().setStatusText(text);
			}
			
			if(!quiet)
			{
				try
				{
					Clip	clip	= AudioSystem.getClip();
					InputStream	is	= getClass().getResourceAsStream(NOTIFICATION_SOUNDS.get(type));
					AudioInputStream	ais	= AudioSystem.getAudioInputStream(is);
					clip.open(ais);
					clip.start();
				}
				catch(Exception e)
				{
					System.err.println("Couldn't play notification sound: "+e);
				}
			}
		}
	}
	
	/**
	 *  Update the fileinfo in the upload/download area.
	 */
	public void updateTransfer(final TransferInfo fi)
	{
		if(TransferInfo.STATE_COMPLETED.equals(fi.getState()))
		{
			notifyChatEvent(NOTIFICATION_FILE_COMPLETE, fi.getOther(), fi, false);
		}
		else if(fi.isFinished())
		{
			notifyChatEvent(NOTIFICATION_FILE_ABORT, fi.getOther(), fi, false);
		}

		((FileTableModel)(fi.isDownload()?dtable:utable).getModel()).updateFile(fi);
	}
	
	//-------- helper classes --------
	
	/**
	 *  Mouse listener that allows to manipulate transfers.
	 */
	public class FileTransferMouseAdapter extends MouseAdapter
	{
		protected JTable table;
		
		public FileTransferMouseAdapter(JTable table)
		{
			this.table = table;
		}
		
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
				if(row!=-1)
				{
					TransferInfo fi = ((FileTableModel)table.getModel()).getValueAt(row);
					createMenu(row, fi).show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}
		
		protected JPopupMenu createMenu(final int row, final TransferInfo fi)
		{
			final JPopupMenu menu = new JPopupMenu();
			if(!fi.isFinished())
			{
				if(fi.isDownload() && TransferInfo.STATE_WAITING.equals(fi.getState()))
				{
					JMenuItem mi = new JMenuItem("Accept/reject transfer...");
					mi.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							acceptFile(new File(fi.getFile()).getName(), fi.getSize(), fi.getOther())
								.addResultListener(new IResultListener<File>()
							{
								public void resultAvailable(File result)
								{
									getService().acceptFile(fi.getId(), result.getAbsolutePath());
								}
								
								public void exceptionOccurred(Exception exception)
								{
									getService().rejectFile(fi.getId());
								}
							});
						}
					});
					menu.add(mi);
				}
				
				JMenuItem mi = new JMenuItem("Cancel transfer");
				mi.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						getService().cancelTransfer(fi.getId());
					}
				});
				menu.add(mi);
				
				if(table.getRowCount()>1)
				{
					mi = new JMenuItem("Cancel all transfers");
					mi.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							final TransferInfo[]	infos	= ((FileTableModel)table.getModel()).getDataMap().values().toArray(new TransferInfo[0]);
							
							for(int i=0; i<infos.length; i++)
							{
								if(!infos[i].isFinished())
								{
									getService().cancelTransfer(infos[i].getId());
								}
							}
						}
					});
					menu.add(mi);
				}

			}
			
			if(fi.isFinished())
			{
				// Todo: check if remote
				
				JMenuItem mi = new JMenuItem("Open");
				mi.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							Desktop.getDesktop().open(new File(fi.getFile()).getCanonicalFile());
						}
						catch(IOException ex)
						{
							// Doesn't work for PDf on windows :-(  http://bugs.sun.com/view_bug.do?bug_id=6764271
							SGUI.showError(table, "Error Opening File", "File '"+new File(fi.getFile()).getName()+"' could not be opened.", ex);
						}
					}
				});
				menu.add(mi);

				mi = new JMenuItem("Open folder");
				mi.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							Desktop.getDesktop().open(new File(fi.getFile()).getParentFile().getCanonicalFile());
						}
						catch(IOException ex)
						{
							SGUI.showError(table, "Error Opening Folder", "Folder '"+new File(fi.getFile()).getParentFile().getName()+"'could not be opened.", ex);
						}
					}
				});
				menu.add(mi);

				mi = new JMenuItem("Remove");
				mi.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						((FileTableModel)table.getModel()).removeRow(row);
					}
				});
				menu.add(mi);

				if(table.getRowCount()>1)
				{
					mi = new JMenuItem("Remove all finished");
					mi.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							final TransferInfo[]	infos	= ((FileTableModel)table.getModel()).getDataMap().values().toArray(new TransferInfo[0]);
							for(int i=infos.length-1; i>=0; i--)
							{
								if(infos[i].isFinished())
								{
									((FileTableModel)table.getModel()).removeRow(i);
								}
							}
						}
					});
					menu.add(mi);
				}
			}
			
			return menu;
		}
	};
	
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
	public class FileTableModel	extends AbstractTableModel
	{
		protected boolean down;
		protected String[]	columns;
		protected Map<String, TransferInfo>	data;

		public FileTableModel(boolean down)
		{
			this.down = down;
			this.columns = new String[]{"Name", down? "Sender": "Receiver", "Size", "Done", "%", "State", "Speed", "Remaining Time"};
			this.data	= new LinkedHashMap<String, TransferInfo>();
		}

		protected JTable getTable()
		{
			// Hack!!! Table required for repaint
			return down? dtable: utable;
		}
		protected Map<String, TransferInfo> getDataMap()
		{
			return data;
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
			TransferInfo[] files = getDataMap().values().toArray(new TransferInfo[getDataMap().size()]);
			if(column==0)
			{
				ret = files[row].getFile();
			}
			else if(column==1)
			{
				ret = files[row].getOther();
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
			else if(column==6)
			{
				ret = files[row];
			}
			else if(column==7)
			{
				if(TransferInfo.STATE_TRANSFERRING.equals(files[row].getState()))
				{
					long	time	= (long)((files[row].getSize()-files[row].getDone())/files[row].getSpeed());
					long	hrs	= time / 3600;
					long	min	= time % 3600 / 60;
					long	sec	= time % 60;
					ret	= hrs + ":" + (min<10 ? "0"+min : min) + ":" + (sec<10 ? "0"+sec : sec);
				}
				else
				{
					ret	= "";
				}
			}
			else
			{
				throw new RuntimeException("Unknown column");
			}
			return ret;
		}
		
		public TransferInfo getValueAt(int row)
		{
			TransferInfo[] files = getDataMap().values().toArray(new TransferInfo[getDataMap().size()]);
			return files[row];
		}
		public void removeRow(int row) 
		{
			TransferInfo[] files = getDataMap().values().toArray(new TransferInfo[getDataMap().size()]);
			getDataMap().remove(files[row].getId());
	        fireTableRowsDeleted(row, row);
	        refresh();
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
		
		public void updateFile(TransferInfo fi)
		{
			/*FileInfo oldfi =*/ getDataMap().put(fi.getId(), fi);
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
			refresh();
		}
		
		boolean dorefresh;
		
		public void refresh()
		{
			if(!dorefresh)
			{
				dorefresh	= true;
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						dorefresh	= false;
						fireTableDataChanged();
						getTable().getParent().invalidate();
						getTable().getParent().doLayout();
						getTable().repaint();
					}
				});
			}
		}
	}
}

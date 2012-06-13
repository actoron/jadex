package jadex.tools.chat.gui;

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
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.tools.chat.ChatUser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *  Panel for displaying the chat.
 */
public class ChatPanel extends AbstractServiceViewerPanel<IChatGuiService>
{
	//-------- constants --------
	
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"play", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/common/images/arrowright.png")
	});
	
	/** The linefeed separator. */
	public static final String lf = (String)System.getProperty("line.separator");
	
	/** The time format. */
	public static final DateFormat	df	= new SimpleDateFormat("HH:mm:ss");
	
	/** The notification sound for a newly online user. */
	public static final String	NOTIFICATION_NEW_USER	= "new user";
	
	/** The notification sound for a new message. */
	public static final String	NOTIFICATION_NEW_MSG	= "new msg";
	
	/** The notification sound for an incoming file request. */
	public static final String	NOTIFICATION_NEW_FILE	= "new file";
	
	/** The notification sound for a successfully completed file. */
	public static final String	NOTIFICATION_FILE_COMPLETE	= "file complete";
	
	/** The notification sound for an aborted or failed file transfer. */
	public static final String	NOTIFICATION_FILE_ABORT	= "file abort";
	
	/** The default notification sounds. */
	protected static Map<String, String>	NOTIFICATION_SOUNDS;
	
	static
	{
		NOTIFICATION_SOUNDS	= new HashMap<String, String>();
		NOTIFICATION_SOUNDS.put(NOTIFICATION_NEW_USER, "../sounds/pling.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_NEW_MSG, "../sounds/ping.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_NEW_FILE, "../sounds/cuckoo_clock.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_FILE_COMPLETE, "../sounds/music_box.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_FILE_ABORT, "../sounds/blurps.wav");
	}
	
	//-------- attributes --------
	
	/** The chat panel. */
	protected JPanel	panel;
	
	/** The text area. */
//	protected JTextArea chatarea;
	protected JTextPane chatarea;
	
	/** The known chat users (cid->user state). */
	protected Map<IComponentIdentifier, ChatUser>	users;
	
	/** The user table. */
	protected JTable	usertable;
	
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
	
	/** The tabbed pane. */
	protected JTabbedPane	tpane;
	
	/** Registration at the service. */
	protected ISubscriptionIntermediateFuture<ChatEvent> subscription;
	
	/** The timer. */
	protected Timer timer;
	
	/** The split panel on left hand side. */
	protected JSplitPanel listpan;
	
	/** The main split panel between left and right. */
	protected JSplitPanel	horsplit;

	/** The sound flag. */
	protected boolean sound;
	
	/** The autorefresh flag. */
	protected boolean autorefresh;
	
	/** The custom notification sounds. */
	protected Map<String, String> notificationsounds;

	//-------- constructors --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param service	The service.
	 */
	public IFuture<Void> init(IControlCenter jcc, IService service)
	{
		this.sound = true;
		this.autorefresh = true;
		this.notificationsounds = new HashMap<String, String>();
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
						ChatUser cu = (ChatUser)value;
						this.setText(cu.getNick()+" ["+cu.getComponentIdentifier()+"]");
						this.setToolTipText("State: "+cu);
						Icon	icon	= cu.getIcon();
						this.setIcon(icon);
						return this;
					}
				};
				
				DefaultTableCellRenderer usericonrend = new DefaultTableCellRenderer()
				{
					{
						this.setHorizontalAlignment(JLabel.CENTER);
					}
					
					public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
					{
						super.getTableCellRendererComponent(table, value, selected, focus, row, column);
						ChatUser cu = (ChatUser)value;
//						System.out.println("cu: "+cu.getNick()+" "+cu.getImage());
						this.setText("");
						byte[] imgdata	= cu.getImage();
						if(imgdata!=null)
						{
							this.setIcon(new ImageIcon(imgdata));
						}
						else
						{
							this.setIcon(null);
						}
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

				chatarea = new JTextPane()
				{
//					public void append(String text)
//					{
//						super.append(text);
//						this.setCaretPosition(getText().length());
//					}
				};
				chatarea.setEditable(false);
				JScrollPane main = new JScrollPane(chatarea);

				final JLabel lto = new JLabel("To: all");
				
				usertable	= new JTable(new UserTableModel());
//				usertable.setTableHeader(new ResizeableTableHeader(usertable.getColumnModel()));
				usertable.setRowHeight(40);
				usertable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
				{
					public void valueChanged(ListSelectionEvent e)
					{
//						System.out.println(SUtil.arrayToString(table.getSelectedRows()));
						int[] sels = usertable.getSelectedRows();
						StringBuffer buf = new StringBuffer("To: ");
						for(int i=0; i<sels.length; i++)
						{
							ChatUser cu = (ChatUser)usertable.getModel().getValueAt(sels[i], 0);
							buf.append(cu.getNick());
							if(i+1<sels.length)
								buf.append(", ");
						}
						if(sels.length==0)
						{
							buf.append("all");
						}
						lto.setText(buf.toString());
					}
				});
				
				JScrollPane userpan = new JScrollPane(usertable);
				usertable.getColumnModel().getColumn(0).setCellRenderer(usericonrend);
				usertable.getColumnModel().getColumn(1).setCellRenderer(userrend);
				usertable.setTransferHandler(new TransferHandler()
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
							ChatUser cu	= (ChatUser)usertable.getModel().getValueAt(droploc.getRow(), 0);
							
							List<?>	files	= (List<?>)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
//							System.out.println("importData: "+files);
							for(Object file: files)
							{
								getService().sendFile(((File)file).getAbsolutePath(), cu.getComponentIdentifier());
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
							int row = usertable.rowAtPoint(e.getPoint());
							usertable.setRowSelectionInterval(row, row);
							ChatUser cu = (ChatUser)((UserTableModel)usertable.getModel()).getValueAt(row, 0);
							createMenu(cu.getComponentIdentifier()).show(e.getComponent(), e.getX(), e.getY());
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
									getService().sendFile(file.getAbsolutePath(), cid);
								}
							}
						});
						menu.add(mi);
						return menu;
					}
				};
				usertable.addMouseListener(lis);
				usertable.getTableHeader().addMouseListener(lis);
//				usertable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
				usertable.getColumnModel().getColumn(0).setMinWidth(32);
				usertable.getColumnModel().getColumn(0).setPreferredWidth(64);
				usertable.getColumnModel().getColumn(0).setMaxWidth(64);
				
				Insets	binsets	= new Insets(1, 3, 1, 3);
				PropertiesPanel pp = new PropertiesPanel("Settings");
				final JTextField tfnick = new JTextField();
				JButton bunick = new JButton("Set");
				bunick.setMargin(binsets);
				JPanel ppan = new JPanel(new BorderLayout());
				ppan.add(tfnick, BorderLayout.CENTER);
				ppan.add(bunick, BorderLayout.EAST);
				pp.addComponent("Nickname: ", ppan);
//				final JTextField tfnick = pp.createTextField("Nickname: ", "unknown", true);
//				bunick.setPreferredSize(b.getPreferredSize());
//				bunick.setMinimumSize(b.getMinimumSize());
				bunick.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						getService().setNickName(tfnick.getText());
					}
				});
				tfnick.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						getService().setNickName(tfnick.getText());
					}
				});
				
				getService().getNickName().addResultListener(new SwingResultListener<String>()
				{
					public void customResultAvailable(String result)
					{
						tfnick.setText(result);
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						// Ignore...
					}
				});
				
				
				final JTextField tfava = new JTextField();
				tfava.setEditable(false);
				JButton buava = new JButton("...");
				buava.setMinimumSize(bunick.getMinimumSize());
				buava.setMaximumSize(bunick.getMaximumSize());
				buava.setPreferredSize(bunick.getPreferredSize());
				JPanel apan = new JPanel(new BorderLayout());
				apan.add(tfava, BorderLayout.CENTER);
				apan.add(buava, BorderLayout.EAST);
				pp.addComponent("Image: ", apan);
				final JFileChooser fcava = new JFileChooser(".");
				fcava.setFileFilter(new FileFilter()
				{
					public String getDescription()
					{
						return "*.jpg, *.png";
					}
					
					public boolean accept(File f)
					{
						return f.isDirectory() || f.getName().endsWith(".jpg") || f.getName().endsWith(".png");
					}
				});
				buava.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						fcava.showOpenDialog(panel);
						File sel = fcava.getSelectedFile();
						if(sel!=null && sel.exists())
						{
							try
							{
								FileInputStream fis = new FileInputStream(sel);
								byte[] data = new byte[fis.available()];
								
								int read = 0;
								while(read<data.length)
								{
									read+=fis.read(data, read, data.length-read);
								}

								getService().setImage(data);
								
								tfava.setText(SUtil.convertPathToRelative(sel.getAbsolutePath()));
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
							}
						}
					}
				});
				
				final JComboBox jcom = new JComboBox(new String[]{NOTIFICATION_FILE_ABORT, 
					NOTIFICATION_FILE_COMPLETE, NOTIFICATION_NEW_FILE, NOTIFICATION_NEW_MSG, NOTIFICATION_NEW_USER});
				final JTextField jtxt = new JTextField();
				jtxt.setEditable(false);
				jtxt.setText(getNotificationSound((String)jcom.getSelectedItem()));
				final JButton jbut = new JButton("...");
				jbut.setMinimumSize(bunick.getMinimumSize());
				jbut.setMaximumSize(bunick.getMaximumSize());
				jbut.setPreferredSize(bunick.getPreferredSize());
				final JButton playbut = new JButton(icons.getIcon("play"));
				playbut.setMinimumSize(bunick.getMinimumSize());
				playbut.setMaximumSize(bunick.getMaximumSize());
				playbut.setPreferredSize(bunick.getPreferredSize());
				final JFileChooser jfil = new JFileChooser(".");
				jfil.setFileFilter(new FileFilter()
				{
					public String getDescription()
					{
						return "*.wav";
					}
					
					public boolean accept(File f)
					{
						return f.isDirectory() || f.getName().endsWith(".wav");
					}
				});
				jcom.addItemListener(new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						String sel = (String)jcom.getSelectedItem();
						if(sel!=null)
						{
							jtxt.setText(getNotificationSound(sel));
						}
						else
						{
							jtxt.setText("");
						}
					}
				});
				jbut.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							jfil.showOpenDialog(panel);
							File sel = jfil.getSelectedFile();
							if(sel!=null)
							{
								String txt = SUtil.convertPathToRelative(sel.getAbsolutePath());
								notificationsounds.put((String)jcom.getSelectedItem(), txt);
								jtxt.setText(txt);
							}
						}
						catch(Exception ex)
						{
							ex.printStackTrace();
						}
					}
				});
				playbut.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						playSound((String)jcom.getSelectedItem());
					}
				});
				JPanel pan = new JPanel(new GridBagLayout());
				GridBagConstraints	gbc	= new GridBagConstraints();
				gbc.fill	= GridBagConstraints.BOTH;
				gbc.weightx	= 1;
				pan.add(jcom, gbc);
				gbc.weightx	= 0;
				gbc.gridwidth	= GridBagConstraints.REMAINDER;
				pan.add(playbut, gbc);
				gbc.weightx	= 1;
				gbc.gridwidth	= GridBagConstraints.RELATIVE;
				pan.add(jtxt, gbc);
				gbc.weightx	= 0;
				gbc.gridwidth	= GridBagConstraints.REMAINDER;
				pan.add(jbut, gbc);
				pp.addComponent("Sound files: ", pan);
				
				final JCheckBox cb = pp.createCheckBox("Sound enabled: ", sound, true, 0);
				cb.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						sound = cb.isSelected();
					}
				});
				
				final JCheckBox cbar = pp.createCheckBox("Users auto refresh: ", autorefresh, true, 0);
				cbar.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						autorefresh = cbar.isSelected();
					}
				});
				
				JButton rsb = pp.createButton("Reset sounds: ", "Reset");
				rsb.setMargin(binsets);
				rsb.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						notificationsounds.clear();
						jtxt.setText(getNotificationSound((String)jcom.getSelectedItem()));
					}
				});
				
				JButton b = pp.createButton("Reset receivers: ", "Reset");
				b.setMargin(binsets);
				b.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						usertable.getSelectionModel().clearSelection();
						lto.setText("To: all");
					}
				});
				
				
				listpan	= new JSplitPanel(JSplitPane.VERTICAL_SPLIT, userpan, pp);
				listpan.setDividerLocation(0.5);
				listpan.setOneTouchExpandable(true);
				listpan.setResizeWeight(1);
//				listpan.add(userpan, BorderLayout.CENTER);
//				listpan.add(pp, BorderLayout.SOUTH);

				JPanel south = new JPanel(new BorderLayout());
				final JTextField tf = new JTextField();
				final JButton send = new JButton("Send");
				south.add(lto, BorderLayout.WEST);
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
							postStatus(typing ? IChatService.STATE_TYPING : IChatService.STATE_IDLE, null);
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
						tell(msg, request).addResultListener(new SwingResultListener<Void>()
						{
							public void customResultAvailable(Void result)
							{
								endRequest(request);
							}
							
							public void customExceptionOccurred(Exception exception)
							{
								endRequest(request);
							}
						});
					}
				};
				tf.addActionListener(al);
				send.addActionListener(al);

				horsplit	= new JSplitPanel(JSplitPanel.HORIZONTAL_SPLIT, listpan, main);
				horsplit.setOneTouchExpandable(true);
				horsplit.setDividerLocation(0.3);
				
				JPanel msgpane = new JPanel(new BorderLayout());
				msgpane.add(horsplit, BorderLayout.CENTER);
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
				
				tpane = new JTabbedPane();
				tpane.add("Messaging", msgpane);
				tpane.add("Downloads", dtpan);
				tpane.add("Uploads", utpan);
				
				panel	= new JPanel(new BorderLayout());
				panel.add(tpane, BorderLayout.CENTER);
				
				subscription	= getService().subscribeToEvents();
				subscription.addResultListener(new SwingIntermediateDefaultResultListener<ChatEvent>()
				{
					public void customIntermediateResultAvailable(ChatEvent ce)
					{
						if(ChatEvent.TYPE_MESSAGE.equals(ce.getType()))
						{
							addMessage(ce.getComponentIdentifier(), (String)ce.getValue(), ce.getNick(), ce.isPrivateMessage());
						}
						else if(ChatEvent.TYPE_STATECHANGE.equals(ce.getType()))
						{
//							System.out.println("state change: "+ce.getComponentIdentifier()+" "+ce.getNick()+" "+ce.getImage());
							setUserState(ce.getComponentIdentifier(), (String)ce.getValue(), ce.getNick(), ce.getImage());
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
						ret.setExceptionIfUndone(exception);
					}
				});
				
				ret.setResultIfUndone(null);
				
				getService().findUsers().addResultListener(new SwingIntermediateDefaultResultListener<IChatService>()
				{
					public void customIntermediateResultAvailable(IChatService chat)
					{
						final IComponentIdentifier cid = ((IService)chat).getServiceIdentifier().getProviderId();
						updateChatUser(cid, chat);
					}
					public void customExceptionOccurred(Exception exception)
					{
					}
				});
				
				getService().getFileTransfers().addResultListener(new SwingIntermediateDefaultResultListener<TransferInfo>()
				{
					public void customIntermediateResultAvailable(TransferInfo ti)
					{
						updateTransfer(ti);
					}
					public void customExceptionOccurred(Exception exception)
					{
					}
				});
			}
		});
		
		timer = new Timer(10000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(autorefresh)
				{
//					System.out.println("refresh");
					
					getService().findUsers().addResultListener(new SwingIntermediateDefaultResultListener<IChatService>()
					{
						public void customIntermediateResultAvailable(final IChatService chat)
						{
							final IComponentIdentifier cid = ((IService)chat).getServiceIdentifier().getProviderId();
							updateChatUser(cid, chat);
						}
						public void customExceptionOccurred(Exception exception)
						{
						}
					});
				}
			}
		});
		timer.start();
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected void updateChatUser(IComponentIdentifier cid, IChatService cs)
	{
		ChatUser	cu	= users.get(cid);
		if(cu==null)
		{
			createChatUser(cid, cs);
		}
		else
		{
			updateExistingChatUser(cu);
		}
	}
	
	/**
	 * 
	 */
	protected void createChatUser(final IComponentIdentifier cid, final IChatService chat)
	{
		chat.getNickName().addResultListener(new SwingResultListener<String>()
		{
			public void customResultAvailable(final String nick)
			{
				chat.getImage().addResultListener(new SwingResultListener<byte[]>()
				{
					public void customResultAvailable(byte[] img)
					{
						setUserState(cid, null, nick, img);
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						setUserState(cid, null, nick, null);
					}
				});
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				setUserState(cid, null, null, null);
			}
		});
	}
	
	/**
	 * 
	 */
	protected void updateExistingChatUser(final ChatUser cu)
	{
		if(cu.isNickUnknown())
		{
			getChatService(cu).addResultListener(new IResultListener<IChatService>()
			{
				public void resultAvailable(IChatService cs)
				{
					cs.getNickName().addResultListener(new SwingResultListener<String>()
					{
						public void customResultAvailable(final String nick)
						{
							setUserState(cu.getComponentIdentifier(), null, nick, null);
						}
						
						public void customExceptionOccurred(Exception exception)
						{
						}
					});
				}

				public void exceptionOccurred(Exception exception)
				{
				}
			});
		}
		
		if(cu.isImageUnknown())
		{
			getChatService(cu).addResultListener(new IResultListener<IChatService>()
			{
				public void resultAvailable(IChatService cs)
				{
					cs.getImage().addResultListener(new SwingResultListener<byte[]>()
					{
						public void customResultAvailable(final byte[] img)
						{
							setUserState(cu.getComponentIdentifier(), null, null, img);
						}
						
						public void customExceptionOccurred(Exception exception)
						{
						}
					});
				}

				public void exceptionOccurred(Exception exception)
				{
				}
			});
		}
	}
	
	/**
	 * 
	 */
	protected IFuture<IChatService> getChatService(final ChatUser cu)
	{
		if(cu.getChat()!=null)
		{
			return new Future(cu.getChat());
		}
		else
		{
			final Future<IChatService> ret = new Future<IChatService>();
			IFuture<IChatService> fut = SServiceProvider.getService(jcc.getJCCAccess().getServiceProvider(), cu.getComponentIdentifier(), IChatService.class);
			fut.addResultListener(new DelegationResultListener<IChatService>(ret)
			{
				public void customResultAvailable(IChatService cs)
				{
					cu.setChatService(cs);
					super.customResultAvailable(cs);
				}
			});
			return ret;
		}
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
		timer.stop();
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
		int[] sels = usertable.getSelectedRows();
		IComponentIdentifier[] recs = new IComponentIdentifier[sels.length];
		for(int i=0; i<sels.length; i++)
		{
			recs[i] = ((ChatUser)usertable.getModel().getValueAt(sels[i], 0)).getComponentIdentifier();
		}
		getService().message(text, recs, true).addResultListener(new IntermediateDefaultResultListener<IChatService>()
		{
			public void intermediateResultAvailable(final IChatService chat)
			{
				setReceiving(chat, request, false);
			}
			
			public void finished()
			{
				ret.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Post the local state to available chatters
	 */
	public IFuture<Void>	postStatus(String status, byte[] image)
	{
		final Future<Void>	ret	= new Future<Void>();
		getService().status(status, image).addResultListener(new IntermediateDefaultResultListener<IChatService>()
		{
			public void intermediateResultAvailable(final IChatService chat)
			{
				setReceiving(chat, -1, false);
			}
			
			public void finished()
			{
				ret.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// ignore.
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
			((DefaultTableModel)usertable.getModel()).fireTableDataChanged();
			usertable.getParent().invalidate();
			usertable.getParent().doLayout();
			usertable.repaint();
		}
	}
	
	/**
	 *  Add a user or change its state.
	 */
	public void	setReceiving(final IChatService chat, final int receiving, final boolean b)
	{
		IComponentIdentifier	cid	= ((IService)chat).getServiceIdentifier().getProviderId();
		setUserState(cid, null, null, null, receiving, b);
	}

	//-------- methods called from service --------
	
	/**
	 *  Add a message to the text area.
	 */
	public void addMessage(final IComponentIdentifier cid, final String text, final String nick, final boolean privatemessage)
	{
		SServiceProvider.getService(getJCC().getJCCAccess().getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new SwingResultListener<IClockService>()
		{
			public void customResultAvailable(final IClockService clock)
			{
				StringBuffer buf = new StringBuffer();
				buf.append("[").append(df.format(new Date(clock.getTime()))).append(", ")
					.append(nick).append("]: ").append(text).append(lf);
//							.append(cid.getName()).append("]: ").append(text).append(lf);
				append(privatemessage? Color.RED: Color.BLACK, buf.toString(), chatarea);
//						chatarea.append(Color.BLACK, buf.toString());
				
				notifyChatEvent(NOTIFICATION_NEW_MSG, cid, text, false);
				
				setUserState(cid, null, null, null);
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				// Ignore...
			}
		});
	}
	
	/**
	 *  Add a user or change its state.
	 */
	public void	setUserState(final IComponentIdentifier cid, final String newstate, final String nickname, final byte[] image)
	{
		this.setUserState(cid, newstate, nickname, image, -1, false);
	}
	
	/**
	 *  Add a user or change its state.
	 */
	public void	setUserState(final IComponentIdentifier cid, final String newstate, final String nickname, 
		final byte[] image, final int id, final boolean rec)
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
				if(newstate!=null)
					cu.setState(newstate);
				if(nickname!=null)
					cu.setNick(nickname);
				if(image!=null)
					cu.setImage(image);
				if(id!=-1)
					cu.setReceiving(id, rec);
				((DefaultTableModel)usertable.getModel()).fireTableDataChanged();
				usertable.getParent().invalidate();
				usertable.getParent().doLayout();
				usertable.repaint();
				
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
		Future<File> ret = new Future<File>();

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
						final JButton but	= new JButton(ChatPlugin.getStatusIcon(false));
						scomp	= but;
						final Timer	timer	= new Timer(500, new ActionListener()
						{
							boolean	star;
							public void actionPerformed(ActionEvent e)
							{
								star	= !star;
								but.setIcon(ChatPlugin.getStatusIcon(star));
							}
						});
						timer.setRepeats(true);
						timer.start();
						but.setMargin(new Insets(0, 0, 0, 0));
						but.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								getJCC().showPlugin(ChatPlugin.PLUGIN_NAME);
								getJCC().removeStatusComponent("chat-status-comp");
								timer.stop();
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

			// Add icon to tab, if changed but not currently shown 
			final int	tab	= value instanceof TransferInfo ? ((TransferInfo) value).isDownload() ? 1 : 2
				: NOTIFICATION_NEW_MSG.equals(type) ? 0 : -1;	// ignore new users.
			if(tab!=-1 && !tpane.getComponentAt(tab).isShowing())
			{
				tpane.setIconAt(tab, ChatPlugin.getTabIcon());
				tpane.getComponentAt(tab).addComponentListener(new ComponentListener()
				{
					public void componentShown(ComponentEvent e)
					{
						tpane.setIconAt(tab, null);
						tpane.getComponentAt(tab).removeComponentListener(this);
					}
					
					public void componentResized(ComponentEvent e) {}
					
					public void componentMoved(ComponentEvent e) {}
					
					public void componentHidden(ComponentEvent e) {}
				});
			}
			
			if(!quiet && sound)
			{
				playSound(type);
			}
		}
	}

	/**
	 *  Play the notification sound for the selected event.
	 *  @param type	The notification event.
	 */
	protected void playSound(String type)
	{
		try
		{
			Clip	clip	= AudioSystem.getClip();
//					InputStream	is	= getClass().getResourceAsStream(NOTIFICATION_SOUNDS.get(type));
			String filename = getNotificationSound(type);
			URL	url	= this.getClass().getResource(filename);
			if(url==null)
			{
				File f = new File(filename);
				if(f.exists())
					url = f.toURI().toURL();
			}
			// Cannot use stream due to jar starter bug.
			AudioInputStream	ais	= AudioSystem.getAudioInputStream(url); // (is);
			clip.open(ais);
			clip.start();
		}
		catch(Exception e)
		{
			System.err.println("Couldn't play notification sound '"+type+"': "+e);
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
	
	/**
	 * 
	 * @param c
	 * @param s
	 */
	public static void append(Color c, String s, JTextPane p) 
	{ 
		p.setEditable(true);
		
		// better implementation--uses
	                      // StyleContext
	    StyleContext sc = StyleContext.getDefaultStyleContext();
	    AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
	        StyleConstants.Foreground, c);

	    int len = p.getDocument().getLength(); // same value as
	                       // getText().length();
	    p.setCaretPosition(len); // place caret at the end (with no selection)
	    p.setCharacterAttributes(aset, false);
	    p.replaceSelection(s); // there is no selection, so inserts at caret
	
	    p.setEditable(false);
	}
	
//	/**
//	 * 
//	 * @param c
//	 * @param s
//	 */
//	public void append(Color c, String s, JTextPane p) 
//	{ // naive implementation
//	    // bad: instiantiates a new AttributeSet object on each call
//	    SimpleAttributeSet aset = new SimpleAttributeSet();
//	    StyleConstants.setForeground(aset, c);
//
//	    int len = p.getText().length();
//	    p.setCaretPosition(len); // place caret at the end (with no selection)
//	    p.setCharacterAttributes(aset, false);
//	    p.replaceSelection(s); // there is no selection, so inserts at caret
//	}

	
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
		protected String[]	columns	= new String[]{"Avatar", "Users"};
		
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
			ChatUser cu = users.get(cids[row]);
			return cu;//cu.getNick()+" ["+cids[row].getName()+"]";
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
	
	/**
	 *  Get the notification sound.
	 */
	public String getNotificationSound(String type)
	{
		String ret = notificationsounds.get(type);
		if(ret==null)
			ret = NOTIFICATION_SOUNDS.get(type);
		return ret;
	}
	
	/**
	 *  Update from given properties.
	 */
	public IFuture<Void> setProperties(Properties props)
	{
		String ar = props.getStringProperty("autorefresh");
		if(ar!=null)
			autorefresh = Boolean.parseBoolean(ar);
		String snd = props.getStringProperty("sound");
		if(snd!=null)
			sound = Boolean.parseBoolean(snd);
		double lp = props.getDoubleProperty("listpan");
		if(lp!=0)
			listpan.setDividerLocation(lp);
		double hs = props.getDoubleProperty("horsplit");
		if(hs!=0)
			horsplit.setDividerLocation(hs);
		
		snd = props.getStringProperty(NOTIFICATION_FILE_ABORT);
		if(snd!=null)
			notificationsounds.put(NOTIFICATION_FILE_ABORT, snd);
		snd = props.getStringProperty(NOTIFICATION_FILE_COMPLETE);
		if(snd!=null)
			notificationsounds.put(NOTIFICATION_FILE_COMPLETE, snd);
		snd = props.getStringProperty(NOTIFICATION_NEW_FILE);
		if(snd!=null)
			notificationsounds.put(NOTIFICATION_NEW_FILE, snd);
		snd = props.getStringProperty(NOTIFICATION_NEW_MSG);
		if(snd!=null)
			notificationsounds.put(NOTIFICATION_NEW_MSG, snd);
		snd = props.getStringProperty(NOTIFICATION_NEW_USER);
		if(snd!=null)
			notificationsounds.put(NOTIFICATION_NEW_USER, snd);
		
		return IFuture.DONE;
	}
	
	/**
	 *  Write current state into properties.
	 */
	public IFuture<Properties> getProperties()
	{
		Properties	props	= new Properties();
		props.addProperty(new Property("autorefresh", ""+autorefresh));
		props.addProperty(new Property("sound", ""+sound));
		double lp =listpan.getProportionalDividerLocation();
		if(lp>0)
			props.addProperty(new Property("listpan", ""+lp));
		double hs = horsplit.getProportionalDividerLocation();
		if(hs>0)
			props.addProperty(new Property("horsplit", ""+hs));
		for(String key: notificationsounds.keySet())
		{
			props.addProperty(new Property(key, notificationsounds.get(key)));
		}
		
		return new Future<Properties>(props);
	}
}

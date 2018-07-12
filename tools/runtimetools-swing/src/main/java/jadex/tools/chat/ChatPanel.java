package jadex.tools.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import jadex.base.SRemoteGui;
import jadex.base.gui.RemoteFileChooser;
import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.commons.gui.future.SwingIntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.transformation.traverser.ImageProcessor;

/**
 *  Panel for displaying the chat.
 */
public class ChatPanel extends AbstractServiceViewerPanel<IChatGuiService>
{
	//-------- constants --------
	
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"play", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/common/images/arrowright.png"),
		":-)", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/smile.png"),
		":D", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/happy.png"),
		":-(", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/unhappy.png"),
//		":'(", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/cry.png"),
		";-)", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/wink.png"),
		":-p", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/tongue.png"),
		":o", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/surprised.png"),
		">-)", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/evilgrin.png"),
		">)", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/grin.png"),
		":>", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/waii.png"),
//		":@", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/angry.png")
//		"+o(", SGUI.makeIcon(ChatPanel.class, "/jadex/tools/chat/images/smi/green.png")

	});
	
	protected static final List<String> smileys = SUtil.createArrayList(new String[]{
		":-)", ":D", ":-(", ";-)", ":-p", ":o", ">-)", ">)", ":>"});

	
	/** The linefeed separator. */
	public static final String lf = (String)System.getProperty("line.separator");
	
	/** The time format. */
	public final DateFormat	df	= new SimpleDateFormat("HH:mm:ss");
	
	/** The notification sound for a newly online user. */
	public static final String	NOTIFICATION_NEW_USER	= "new user";
	
	/** The notification sound for a new message. */
	public static final String	NOTIFICATION_NEW_MSG	= "new msg";
	
	/** The notification sound for a failed message. */
	public static final String	NOTIFICATION_MSG_FAILED	= "msg failed";
	
	/** The notification sound for an incoming file request. */
	public static final String	NOTIFICATION_NEW_FILE	= "new file";
	
	/** The notification sound for a successfully completed file. */
	public static final String	NOTIFICATION_FILE_COMPLETE	= "file complete";
	
	/** The notification sound for an aborted or failed file transfer. */
	public static final String	NOTIFICATION_FILE_ABORT	= "file abort";
	
	/** The default notification sounds. */
	protected static final Map<String, String>	NOTIFICATION_SOUNDS;
	
	static
	{
		NOTIFICATION_SOUNDS	= new HashMap<String, String>();
		NOTIFICATION_SOUNDS.put(NOTIFICATION_NEW_USER, "sounds/pling.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_NEW_MSG, "sounds/ping.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_NEW_FILE, "sounds/cuckoo_clock.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_FILE_COMPLETE, "sounds/music_box.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_MSG_FAILED, "sounds/blurps.wav");
		NOTIFICATION_SOUNDS.put(NOTIFICATION_FILE_ABORT, "sounds/blurps.wav");
	}
	
	//-------- attributes --------
	
	/** The chat panel. */
	protected JPanel	panel;
	
	/** The text area. */
//	protected JTextArea chatarea;
	protected JTextPane chatarea;
		
	/** The user table. */
	protected JTable	usertable;
	protected UserTableModel usermodel;
	
	/** The typing state. */
	protected boolean	typing;

	/** The away state. */
	protected boolean	away;

	/** The download table. */
	protected JTable dtable;
	
	/** The upload table. */
	protected JTable utable;
	
	/** The tabbed pane. */
	protected JTabbedPane	tpane;
	
	/** Registration at the service. */
	protected ISubscriptionIntermediateFuture<ChatEvent> subscription;
	
	/** The refresh timer. */
	protected Timer refreshtimer;
	
	/** The away timer. */
	protected Timer awaytimer;
	
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
	
	/** The file chooser (created on demand, if local). */
	protected JFileChooser	filechooser;

	/** The remote file chooser (created on demand, if remote). */
	protected RemoteFileChooser	rfilechooser;
	
	/** Map for panels of open accept dialogs to close when transfer has been accepted/rejected/timeouted in background. */
	protected Map<TransferInfo, JComponent>	dialogs;
	
	/** The message counter to differentiate sent messages. */
	protected int	reqcnt;
	
	/** The timer for the flashing chat icon. */
	protected Timer	icontimer;
	
	/** Flag to indicate that a sound is playing. */
	protected boolean	playing;

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
		this.dialogs	= new HashMap<TransferInfo, JComponent>();
		usermodel = new UserTableModel();

		final Future<Void>	ret	= new Future<Void>();
		
		super.init(jcc, service).addResultListener(new DelegationResultListener<Void>(ret)
		{
			JFileChooser jfil;

			public void customResultAvailable(Void result)
			{
				DefaultTableCellRenderer userrend = new DefaultTableCellRenderer()
				{
					public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
					{
						super.getTableCellRendererComponent(table, value, selected, focus, row, column);
						ChatUser cu = (ChatUser)value;
						this.setText(cu.getNick()+" ["+cu.getComponentIdentifier()+"]");
						if(cu.getComponentIdentifier()==null)
						{
							System.out.println("CU.CID is null");
						}
						if(getService()==null)
						{
							System.out.println("service is null");
						}
						if(((IService)getService()).getId()==null)
						{
							System.out.println("service.SID is null");
						}
							
						if(!cu.getComponentIdentifier().equals(((IService)getService()).getId().getProviderId()))
						{
							this.setToolTipText("Select to send private message.\nRight-click to send file.");
						}
						Icon	icon	= cu.getIcon();
						this.setIcon(icon);
						return this;
					}
				};
				
//				DefaultTableCellRenderer usericonrend = new DefaultTableCellRenderer()
//				{
//					{
//						this.setHorizontalAlignment(JLabel.CENTER);
//					}
//					
//					public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
//					{
//						super.getTableCellRendererComponent(table, value, selected, focus, row, column);
//						ChatUser cu = (ChatUser)value;
////						System.out.println("cu: "+cu.getNick()+" "+cu.getImage());
//						this.setText("");
//						byte[] imgdata	= cu.getImage();
//						if(imgdata!=null)
//						{
//							this.setIcon(new ImageIcon(imgdata));
//						}
//						else
//						{
//							this.setIcon(null);
//						}
//						return this;
//					}
//				};
				
				DefaultTableCellRenderer cidrend = new DefaultTableCellRenderer()
				{
					public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
					{
						super.getTableCellRendererComponent(table, value, selected, focus, row, column);
						IComponentIdentifier	cid	= (IComponentIdentifier)value;
						this.setText(cid.getName());
						// todo?
//						this.setToolTipText(SUtil.arrayToString(cid.getAddresses()));
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
				
				DefaultTableCellRenderer stringrend = new DefaultTableCellRenderer()
				{
					public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
					{
						super.getTableCellRendererComponent(table, value, selected, focus, row, column);
						if(value!=null)
							this.setToolTipText(value.toString());
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

//				users	= new LinkedHashMap<IComponentIdentifier, ChatUser>();

				chatarea = new JTextPane()
				{
//					public void append(String text)
//					{
//						super.append(text);
//						this.setCaretPosition(getText().length());
//					}
				};
//				StyledDocument doc = chatarea.getStyledDocument();
//				((AbstractDocument)doc).setDocumentFilter(new DocumentFilter()
//				{
//					public void insertString(FilterBypass fb, int offset,
//							String string, AttributeSet attr)
//							throws BadLocationException
//					{
//						super.insertString(fb, offset, string.replaceAll("\n", "\n\r"), attr);
//					}
//					public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a)
//					        throws BadLocationException
//					   {
//					        super.replace(fb, offs, length, str.replaceAll("\n", "\n\r"), a); // works
//					    }
//				});


//				doc.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\r\n");
//				Style base = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
//				Style style = doc.addStyle("smileys", base);
//				SimpleAttributeSet as = new SimpleAttributeSet();
//				for(String sm: smileys)
//				{
//					StyleConstants.setIcon(as, icons.get(sm));
//				}
				
//				StyledDocument doc = (StyledDocument) p.getDocument();
//                String text = doc.getText(0, p.getDocument().getLength());
//                int index = text.indexOf(":)");
//                int start = 0;
//                while (index > -1) {
//                    Element el = doc.getCharacterElement(index);
//                    if (StyleConstants.getIcon(el.getAttributes()) == null) {
//                        doc.remove(index, 2);
//                        SimpleAttributeSet attrs = new SimpleAttributeSet();
//                        StyleConstants.setIcon(attrs, getImage());
//                        doc.insertString(index, ":)", attrs);
//                    }
//                    start = index + 2;
//                    index = text.indexOf(":)", start);
//                }
				
				chatarea.setEditable(false);
				JScrollPane main = new JScrollPane(chatarea);

				final JLabel lto = new JLabel("To: all");
				
				usertable	= new JTable(usermodel);
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
				usertable.getColumnModel().getColumn(0).setCellRenderer(userrend);
//				usertable.getColumnModel().getColumn(1).setCellRenderer(userrend);
				usertable.setTransferHandler(new TransferHandler()
				{
					public boolean	canImport(TransferHandler.TransferSupport support)
					{
						boolean	ret = false;
						
						if(support.isDrop() && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
							&& (support.getSourceDropActions()&TransferHandler.COPY)!=0 && isLocal())
						{
							JTable.DropLocation	droploc	= (JTable.DropLocation)support.getDropLocation();
							ChatUser cu	= (ChatUser)usertable.getModel().getValueAt(droploc.getRow(), 0);
							if(!cu.getComponentIdentifier().equals(((IService)getService()).getId().getProviderId()))
							{
								try
								{
									boolean	nodirs	= true;
									
									// Cannot check due to java bug on windows, grrr
									// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6759788
//									List<?>	files	= (List<?>)support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
//	//								System.out.println("importData: "+files);
//									for(int i=0; nodirs && i<files.size(); i++)
//									{
//										nodirs	= !((File)files.get(i)).isDirectory();
//									}
									
									if(nodirs)
									{
										ret	= true;
										support.setDropAction(TransferHandler.COPY);
									}
								}
								catch(Exception e)
								{
									System.err.println("Drop error: "+e);
								}
							}
						}

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
							if(!cu.getComponentIdentifier().equals(((IService)getService()).getId().getProviderId()))
							{
								createMenu(cu.getComponentIdentifier()).show(e.getComponent(), e.getX(), e.getY());
							}
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
								if(isLocal())
								{
									if(filechooser==null)
									{
										filechooser = new JFileChooser(".");
										filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
									}
									
									if(JFileChooser.APPROVE_OPTION==filechooser.showOpenDialog(panel))
									{
										File file = filechooser.getSelectedFile();
										if(file!=null)
										{
											getService().sendFile(file.getAbsolutePath(), cid);
										}
									}									
								}
								else
								{
									getServiceAccess().addResultListener(new SwingResultListener<IExternalAccess>(new IResultListener<IExternalAccess>()
									{
										public void resultAvailable(IExternalAccess ea)
										{
											if(rfilechooser==null)
											{
												rfilechooser	= new RemoteFileChooser(ea);
											}
											
											// Hack!!! remote file chooser has hack that assumes files without '.' are directories and vice versa
											// -> accept both (assumed) files and directories and hope that the user only selects actual files. 
											rfilechooser.chooseFile(null, ".", panel, JFileChooser.FILES_AND_DIRECTORIES, null)
												.addResultListener(new SwingResultListener<FileData>(new IResultListener<FileData>()
											{							
												public void resultAvailable(FileData file)
												{
													if(file!=null)
													{
														getService().sendFile(file.getPath(), cid);
													}
												}
												
												public void exceptionOccurred(Exception exception)
												{
													// ignore...
												}
											}));							
										}
										
										public void exceptionOccurred(Exception exception)
										{
											// ignore...
										}
									}));
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
//				usertable.getColumnModel().getColumn(0).setMinWidth(32);
//				usertable.getColumnModel().getColumn(0).setPreferredWidth(64);
//				usertable.getColumnModel().getColumn(0).setMaxWidth(64);
				
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
				
				getService().getNickName().addResultListener(new SwingResultListener<String>(new IResultListener<String>()
				{
					public void resultAvailable(String result)
					{
						tfnick.setText(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						// Ignore...
					}
				}));
				
				
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
				buava.addActionListener(new ActionListener()
				{
					JFileChooser fcava;
					
					public void actionPerformed(ActionEvent e)
					{
						// Create file chooser lazily to allow jenkins build to succeed
						// (new JFileChooser() throws exception in headless windows)
						if(fcava==null)
						{
							fcava	=  new JFileChooser(".");
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
						}
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
								fis.close();

								double twh = 40.0;
								BufferedImage src = ImageIO.read(new ByteArrayInputStream(data));
								int sw = src.getWidth();
								int sh = src.getHeight();
								int tw = sw;
								int th = sh;
								double fac = Math.max(sw, sh)/twh;
								if(fac>1)
								{
									tw /= fac;
									th /= fac; 
									
//									System.out.println("scaled: "+sw+" "+sh+" "+tw+" "+th);
									
									Image target = SGUI.scaleImage(src, tw, th, Image.SCALE_FAST);
									data = ImageProcessor.imageToStandardBytes(target, "image/png");
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

				final JComboBox jcom = new JComboBox(new String[]{NOTIFICATION_NEW_MSG, NOTIFICATION_NEW_USER,
					NOTIFICATION_MSG_FAILED, NOTIFICATION_NEW_FILE, NOTIFICATION_FILE_ABORT, NOTIFICATION_FILE_COMPLETE});
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
				jcom.addItemListener(new ItemListener()
				{
					public void itemStateChanged(ItemEvent e)
					{
						// Create file chooser lazily to allow jenkins build to succeed
						// (new JFileChooser() throws exception in headless windows)
						if(jfil==null)
						{
							 jfil	= new JFileChooser(".");
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
						}
						
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
						// Create file chooser lazily to allow jenkins build to succeed
						// (new JFileChooser() throws exception in headless windows)
						if(jfil==null)
						{
							 jfil	= new JFileChooser(".");
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
						}
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
						playSound((String)jcom.getSelectedItem(), true);
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
						updateRefreshTimer();
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
				
				
				listpan	= new JSplitPanel(JSplitPane.VERTICAL_SPLIT, userpan, new JScrollPane(pp));
				listpan.setDividerLocation(0.5);
				listpan.setOneTouchExpandable(true);
				listpan.setResizeWeight(1);
//				listpan.add(userpan, BorderLayout.CENTER);
//				listpan.add(pp, BorderLayout.SOUTH);

				JPanel south = new JPanel(new BorderLayout(2,0));
				final JTextField tf = new JTextField();
				final JButton send = new JButton("Send");
				final JButton smi = new JButton(icons.getIcon(":-)"));
				smi.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						final String[] smiley = new String[1];
						final JDialog dia = new JDialog((JFrame)null, "Smiley Selection", false);
						
						JPanel pan = new JPanel(new FlowLayout());
						for(final String key: smileys)
						{
							JButton but = new JButton(icons.getIcon(key));
							but.setMargin(new Insets(0,0,0,0));
							but.addActionListener(new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									smiley[0] = key;
									dia.dispose();
									if(smiley[0]!=null)
									{
										tf.setText(tf.getText()+smiley[0]);
									}
								}
							});
							pan.add(but);
						}
						
						dia.getContentPane().add(pan, BorderLayout.CENTER);
//						dia.setLocation(SGUI.calculateMiddlePosition(SGUI.getWindowParent(panel), dia));
						dia.setUndecorated(true);
//						Border bl = BorderFactory.createLineBorder(Color.black);
//						((JComponent)dia).setBorder(bl);
						dia.addWindowFocusListener(new WindowFocusListener()
						{
							public void windowLostFocus(WindowEvent e) {dia.dispose();}
							public void windowGainedFocus(WindowEvent e) {/*NOP*/}
						});
						dia.pack();
						dia.setLocationRelativeTo(smi);
						Point loc = dia.getLocation();
						dia.setLocation(new Point((int)loc.getX(), (int)(loc.getY()-30)));
						dia.setVisible(true);
//						if(smiley[0]!=null)
//						{
//							tf.setText(tf.getText()+smiley[0]);
//						}
					}
				});
				smi.setMargin(new Insets(0,0,0,0));
				JPanel bp = new JPanel(new BorderLayout(2,0));
				bp.add(smi, BorderLayout.WEST);
				bp.add(send, BorderLayout.EAST);
				south.add(lto, BorderLayout.WEST);
				south.add(tf, BorderLayout.CENTER);
				south.add(bp, BorderLayout.EAST);
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
						setAway(false);
						boolean	newtyping	= tf.getText().length()!=0;
						if(newtyping!=typing)
						{
							typing	= newtyping;
							postStatus();
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
						tell(msg);
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
				final JScrollPane dtpan = new JScrollPane(dtable);
				final JScrollPane utpan = new JScrollPane(utable);
//				dtpan.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Downloads"));
//				utpan.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Uploads"));
//				udpane.add(dtpan);
//				udpane.add(utpan);
//				udpane.add(dtable);
//				udpane.add(utable);
//				udpane.setOneTouchExpandable(true);
//				udpane.setDividerLocation(0.5);

				// User refresh only when table is visible
				usertable.addHierarchyListener(new HierarchyListener()
				{
					public void hierarchyChanged(HierarchyEvent e)
					{
						if((e.getChangeFlags()&HierarchyEvent.SHOWING_CHANGED)!=0)
						{
							updateRefreshTimer();
						}
					}
				});
				
				// Repaint tables if shown to update timeout column.
				dtpan.addHierarchyListener(new HierarchyListener()
				{
					Timer	timer;
					public void hierarchyChanged(HierarchyEvent e)
					{
						if(dtpan.isShowing() && timer==null)
						{
//							System.out.println("start dtpan timer");
							timer	= new Timer(1000, new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									dtable.repaint();
								}
							});
							timer.start();
						}
						else if(!dtpan.isShowing() && timer!=null)
						{
//							System.out.println("stop dtpan timer");
							timer.stop();
							timer	= null;
						}
					}
				});
				utpan.addHierarchyListener(new HierarchyListener()
				{
					Timer	timer;
					public void hierarchyChanged(HierarchyEvent e)
					{
						if(utpan.isShowing() && timer==null)
						{
//							System.out.println("start utpan timer");
							timer	= new Timer(1000, new ActionListener()
							{
								public void actionPerformed(ActionEvent e)
								{
									utable.repaint();
								}
							});
							timer.start();
						}
						else if(!utpan.isShowing() && timer!=null)
						{
//							System.out.println("stop utpan timer");
							timer.stop();
							timer	= null;
						}
					}
				});
				
				
				dtable.getColumnModel().getColumn(0).setCellRenderer(stringrend);
				dtable.getColumnModel().getColumn(1).setCellRenderer(stringrend);
				dtable.getColumnModel().getColumn(2).setCellRenderer(cidrend);
				dtable.getColumnModel().getColumn(3).setCellRenderer(byterend);
				dtable.getColumnModel().getColumn(4).setCellRenderer(byterend);
				dtable.getColumnModel().getColumn(5).setCellRenderer(progressrend);
				dtable.getColumnModel().getColumn(7).setCellRenderer(speedrend);
				
				utable.getColumnModel().getColumn(0).setCellRenderer(stringrend);
				utable.getColumnModel().getColumn(1).setCellRenderer(stringrend);
				utable.getColumnModel().getColumn(2).setCellRenderer(cidrend);
				utable.getColumnModel().getColumn(3).setCellRenderer(byterend);
				utable.getColumnModel().getColumn(4).setCellRenderer(byterend);
				utable.getColumnModel().getColumn(5).setCellRenderer(progressrend);
				utable.getColumnModel().getColumn(7).setCellRenderer(speedrend);
				
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
							addMessage(ce.getComponentIdentifier(), (String)ce.getValue(), ce.getNick(), ce.isPrivateMessage(), false);
						}
						else if(ChatEvent.TYPE_STATECHANGE.equals(ce.getType()))
						{
							setUserState(ce.getComponentIdentifier(),
								!IChatService.STATE_DEAD.equals(ce.getValue()) ? Boolean.TRUE : Boolean.FALSE,
								IChatService.STATE_TYPING.equals(ce.getValue()) ? Boolean.TRUE : Boolean.FALSE,
								IChatService.STATE_AWAY.equals(ce.getValue()) ? Boolean.TRUE : Boolean.FALSE,
								ce.getNick(), ce.getImage());
						}
						else if(ChatEvent.TYPE_FILE.equals(ce.getType()))
						{
							final TransferInfo	ti	= (TransferInfo) ce.getValue();
							updateTransfer(ti);
							
							if(ti.isDownload() && TransferInfo.STATE_WAITING.equals(ti.getState()))
							{
								notifyChatEvent(NOTIFICATION_NEW_FILE, ti.getOther(), ti, false);
								
//								if(panel.isShowing())
								{
									acceptFile(ti).addResultListener(new IResultListener<String>()
									{
										public void resultAvailable(String filepath)
										{
											getService().acceptFile(ti.getId(), filepath);
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
						final IComponentIdentifier cid = ((IService)chat).getId().getProviderId();
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
				
				updateRefreshTimer();
			}
		});
		
		return ret;
	}
	
	/**
	 *  Start or stop the refresh timer, if necessary.
	 */
	protected void	updateRefreshTimer()
	{
		setAway(!usertable.isShowing());
		
		if(usertable.isShowing() && autorefresh && refreshtimer==null)
		{
//			System.out.println("enabling refresh timer");
			refreshtimer = new Timer(10000, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if(autorefresh)
					{
						for(IComponentIdentifier cu: usermodel.getUserIDs())
						{
							// Reset user -> will be removed if not updated within some time.
							setUserState(cu, null, null, null, null, null);
						}
						
						getService().findUsers().addResultListener(new SwingIntermediateDefaultResultListener<IChatService>()
						{
							public void customIntermediateResultAvailable(final IChatService chat)
							{
								final IComponentIdentifier cid = ((IService)chat).getId().getProviderId();
								updateChatUser(cid, chat);
							}
							public void customExceptionOccurred(Exception exception)
							{
							}
						});
					}
				}
			});
			refreshtimer.setInitialDelay(0);	// start first search immediately.
			refreshtimer.start();
		}
		else if((!autorefresh || !usertable.isShowing()) && refreshtimer!=null)
		{
//			System.out.println("disabling refresh timer");
			refreshtimer.stop();
			refreshtimer	= null;
		}
	}
	
	/**
	 *  Change the status to away and post a change, if necessary.
	 */
	protected void setAway(boolean away)
	{
		if(away!=this.away)
		{
			this.away	= away;
			postStatus();
		}

		if(awaytimer!=null)
		{
			awaytimer.stop();
		}
		
		if(!away)
		{
			if(awaytimer==null)
			{
				awaytimer	= new Timer(300000, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						setAway(true);
					}
				});
				awaytimer.setRepeats(false);
			}
			
			awaytimer.start();
		}
	}
	
	/**
	 * 
	 */
	protected void updateChatUser(final IComponentIdentifier cid, IChatService cs)
	{
		setUserState(cid, Boolean.TRUE, null, null, null, null);
		ChatUser	cu	= usermodel.getUser(cid);
		
		if(cu==null || cu.isNickUnknown())
		{
			cs.getNickName().addResultListener(new SwingResultListener<String>(new IResultListener<String>()
			{
				public void resultAvailable(final String nick)
				{
					setUserState(cid, Boolean.TRUE, null, null, nick, null);
				}
				
				public void exceptionOccurred(Exception exception)
				{
				}
			}));
		}
		
		if(cu==null || cu.isAvatarUnknown())
		{
			cs.getImage().addResultListener(new SwingResultListener<byte[]>(new IResultListener<byte[]>()
			{
				public void resultAvailable(final byte[] img)
				{
					setUserState(cid, Boolean.TRUE, null, null, null, img);
				}
				
				public void exceptionOccurred(Exception exception)
				{
				}
			}));
		}
		
		cs.getStatus().addResultListener(new SwingResultListener<String>(new IResultListener<String>()
		{
			public void resultAvailable(final String status)
			{
				setUserState(cid, Boolean.TRUE,
					IChatService.STATE_TYPING.equals(status) ? Boolean.TRUE : Boolean.FALSE,
					IChatService.STATE_AWAY.equals(status) ? Boolean.TRUE : Boolean.FALSE,
					null, null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		}));
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
		if(refreshtimer!=null)
		{
			refreshtimer.stop();
		}
		if(awaytimer!=null)
		{
			awaytimer.stop();
		}
		if(icontimer!=null)
		{
			icontimer.stop();
			icontimer	= null;
		}
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
	public IFuture<Void>	tell(final String text)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Remember known/target users to determine send failures.
		final Set<ChatUser>	sendusers	= new HashSet<ChatUser>();
		final int id	= ++reqcnt;
		
		int[] sels = usertable.getSelectedRows();
		IComponentIdentifier[] recs = new IComponentIdentifier[sels.length];
		if(sels.length>0)
		{
			for(int i=0; i<sels.length; i++)
			{
				ChatUser	cu	= (ChatUser)usertable.getModel().getValueAt(sels[i], 0);
				cu.addMessage(id);
				sendusers.add(cu);
				recs[i] = cu.getComponentIdentifier();
			}
		}
		else
		{
			for(ChatUser cu: usermodel.getUsers())
			{
				cu.addMessage(id);
				sendusers.add(cu);				
			}
		}
		
		usertable.repaint();

		getService().message(text, recs, true).addResultListener(new SwingIntermediateDefaultResultListener<IChatService>()
		{
			public void customIntermediateResultAvailable(final IChatService chat)
			{
				ChatUser	cu	= usermodel.getUser(((IService)chat).getId().getProviderId());
				if(cu!=null)
				{
					sendusers.remove(cu);
					cu.removeMessage(id);
					usertable.repaint();
				}
			}
			
			public void customFinished()
			{
				ret.setResult(null);
				printFailures();
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				ret.setException(exception);
				printFailures();
			}
			
			protected void	printFailures()
			{
				if(!sendusers.isEmpty())
				{
					StringBuffer	nick	= new StringBuffer();
					nick.append("failed to deliver message to");
					for(ChatUser cu: sendusers)
					{
						nick.append(" ");
						nick.append(cu.getNick());
						nick.append(",");
						cu.removeMessage(id);
					}
					usertable.repaint();
					addMessage(((IService)getService()).getId().getProviderId(),
						text, nick.substring(0, nick.length()-1), false, true); // Strip last comma.
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Post the local state to available chatters
	 */
	public IFuture<Void>	postStatus()
	{
		final Future<Void>	ret	= new Future<Void>();
		String	status	= typing ? IChatService.STATE_TYPING
			: away ? IChatService.STATE_AWAY : IChatService.STATE_IDLE;
		
		// Empty cid array for backwards compatibility.
		getService().status(status, null, new IComponentIdentifier[0]).addResultListener(new IntermediateDefaultResultListener<IChatService>()
		{
			public void intermediateResultAvailable(final IChatService chat)
			{
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
	
	//-------- methods called on updates from service --------
	
	/**
	 *  Add a message to the text area.
	 */
	public void addMessage(final IComponentIdentifier cid, final String text, final String nick, final boolean privatemessage, final boolean sendfailure)
	{
		getJCC().getJCCAccess().searchService( new ServiceQuery<>( IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new SwingResultListener<IClockService>(new IResultListener<IClockService>()
		{
			public void resultAvailable(final IClockService clock)
			{
				StringBuffer buf = new StringBuffer();
				buf.append("[").append(df.format(new Date(clock.getTime()))).append(", ")
					.append(nick).append("]: ").append(text).append(lf);
//							.append(cid.getName()).append("]: ").append(text).append(lf);
				append(sendfailure ? Color.GRAY : privatemessage? Color.RED: Color.BLACK, buf.toString(), chatarea);
//						chatarea.append(Color.BLACK, buf.toString());
				
				notifyChatEvent(sendfailure ? NOTIFICATION_MSG_FAILED : NOTIFICATION_NEW_MSG, cid, text, false);
				
				setUserState(cid, Boolean.TRUE, null, null, null, null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Ignore...
			}
		}));
	}
	
	/**
	 *  Add a user or change its state.
	 */
	public void	setUserState(final IComponentIdentifier cid, final Boolean online, final Boolean typing,  final Boolean away, final String nickname, final byte[] image)
	{
		if(cid==null)
		{
			throw new NullPointerException();
		}
//		System.out.println("setUserState "+cid+", "+online);
		
		// Called on component thread.
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				boolean	isnew	= false;
				boolean	isdead	= false;
				ChatUser	cu	= usermodel.getUser(cid);
				if(cu==null && online!=null && online.booleanValue())
				{
//					System.out.println("create User "+cid+", "+online);
					cu	= new ChatUser(cid);
					usermodel.addUser(cid, cu);
					isnew	= true;
				}
				
				if(cu!=null)
				{
					isdead	= cu.setOnline(online);
					
					if(isdead)
					{
						usermodel.removeUser(cid);
					}
					else
					{
						if(away!=null)
						{
							cu.setAway(away.booleanValue());
						}
						if(typing!=null)
						{
							cu.setTyping(typing.booleanValue());
						}
						if(nickname!=null)
						{
							cu.setNick(nickname);
						}
						if(image!=null)
						{
							cu.setAvatar(new ImageIcon(image));
						}
						
						if(isnew)
						{
							notifyChatEvent(NOTIFICATION_NEW_USER, cid, null, false);
						}
					}
					
					((DefaultTableModel)usertable.getModel()).fireTableDataChanged();
					usertable.getParent().invalidate();
					usertable.getParent().doLayout();
					usertable.repaint();
				}
			}
		});
	}
	
	
	/**
	 *  Open dialog and check if user wants to receive the file.
	 *  @return The path name to store the file, if accepted.
	 */
	public IFuture<String> acceptFile(final TransferInfo ti)
	{
		final Future<String> ret = new Future<String>();
		
		// Future for getting the file path and checking if the file exists.
		final Future<Tuple2<String, Boolean>>	initial	= new Future<Tuple2<String,Boolean>>();
		
		if(isLocal())
		{
			File file	= new File(".", ti.getFileName());
			initial.setResult(new Tuple2<String, Boolean>(file.getAbsolutePath(), file.exists() ? Boolean.TRUE : Boolean.FALSE));
		}
		else
		{
			getServiceAccess().addResultListener(new SwingExceptionDelegationResultListener<IExternalAccess, Tuple2<String, Boolean>>(initial)
			{
				public void customResultAvailable(IExternalAccess ea)
				{
					SRemoteGui.getFileData(ea, ti.getFileName())
						.addResultListener(new SwingExceptionDelegationResultListener<FileData, Tuple2<String, Boolean>>(initial)
					{
						public void customResultAvailable(FileData file)
						{
							initial.setResult(new Tuple2<String, Boolean>(file.getPath(), file.isExists() ? Boolean.TRUE : Boolean.FALSE));
						}
					});					
				}
			});
		}
		
		final boolean[]	exists	= new boolean[1];
		
		final PropertiesPanel pp = new PropertiesPanel();
		JPanel fnp = new JPanel(new GridBagLayout());
		final JTextField tfpath = new JTextField(ti.getFileName(), 15);
		JButton bupath = new JButton("...");
		bupath.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(isLocal())
				{
					if(filechooser==null)
					{
						filechooser = new JFileChooser();
						filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					}
					filechooser.setSelectedFile(new File(tfpath.getText()));
					
					if(JFileChooser.APPROVE_OPTION==filechooser.showDialog(panel, "Save file"))
					{
						File file	= filechooser.getSelectedFile();
						if(file!=null)
						{
							tfpath.setText(file.getAbsolutePath());
							exists[0]	= file.exists();
						}
					}
				}
				else
				{
					getServiceAccess().addResultListener(new SwingResultListener<IExternalAccess>(new IResultListener<IExternalAccess>()
					{
						public void resultAvailable(IExternalAccess ea)
						{
							if(rfilechooser==null)
							{
								rfilechooser	= new RemoteFileChooser(ea);
							}
							
							// Hack!!! remote file chooser has hack that assumes files without '.' are directories and vice versa
							// -> accept both (assumed) files and directories and hope that the user only selects actual files. 
							rfilechooser.chooseFile("Save file", tfpath.getText(), panel, JFileChooser.FILES_AND_DIRECTORIES, null)
								.addResultListener(new SwingResultListener<FileData>(new IResultListener<FileData>()
							{							
								public void resultAvailable(FileData file)
								{
									if(file!=null)
									{
										tfpath.setText(file.getPath());
										exists[0]	= file.isExists();
									}
								}
								
								public void exceptionOccurred(Exception exception)
								{
									// ignore...
								}
							}));							
						}
						
						public void exceptionOccurred(Exception exception)
						{
							// ignore...
						}
					}));
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
		pp.createTextField("Size: ", SUtil.bytesToString(ti.getSize()));
		pp.createTextField("Sender: ", ""+(ti.getOther()==null? ti.getOther(): ti.getOther().getName()));
		pp.createTextField("Time left: ", "", false);
		
		initial.addResultListener(new ExceptionDelegationResultListener<Tuple2<String, Boolean>, String>(ret)
		{
			public void customResultAvailable(Tuple2<String, Boolean> result)
			{
				tfpath.setText(result.getFirstEntity());
				exists[0]	= result.getSecondEntity().booleanValue();
				
				dialogs.put(ti, pp);
				Timer	timer	= new Timer(1000, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						pp.getTextField("Time left: ").setText(
							Long.toString(Math.max(0, (ti.getTimeout()-System.currentTimeMillis())/1000)));
					}
				});
				timer.start();
				int res	= JOptionPane.showOptionDialog(panel, pp, "Incoming File Transfer", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Accept", "Reject", "Cancel"}, "Accept");
				timer.stop();
				dialogs.remove(ti);
				if(JOptionPane.YES_OPTION==res)
				{
					if(exists[0] && JOptionPane.NO_OPTION==
						JOptionPane.showConfirmDialog(panel, "File already exists. Are you sure you want to overwrite it?"))
					{
						acceptFile(ti).addResultListener(new SwingDelegationResultListener<String>(ret));
					}
					else
					{
						ret.setResult(tfpath.getText());
					}
				}
				else if(JOptionPane.NO_OPTION==res)
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
		if(!((IService)getService()).getId().getProviderId().equals(source)
			|| NOTIFICATION_MSG_FAILED.equals(type))
		{
			String	text	= null;
			if(NOTIFICATION_NEW_MSG.equals(type))
			{
				text	= "New chat message from "+source+": "+value;
			}
			else if(NOTIFICATION_MSG_FAILED.equals(type))
			{
				text	= "Chat message failed: "+value;
			}
			else if(NOTIFICATION_NEW_USER.equals(type))
			{
				text	= "New chat user online: "+source;
			}
			else if(NOTIFICATION_NEW_FILE.equals(type))
			{
				text	= "New file upload request from "+source+": "+((TransferInfo)value).getFileName();
			}
			else if(NOTIFICATION_FILE_COMPLETE.equals(type))
			{
				text	= ((TransferInfo)value).isDownload()
					? "Completed downloading '"+((TransferInfo)value).getFileName()+"' from "+source
					: "Completed uploading '"+((TransferInfo)value).getFileName()+"' to "+source;
			}
			else if(NOTIFICATION_FILE_ABORT.equals(type))
			{
				text	= ((TransferInfo)value).isDownload()
					? "Problem while downloading '"+((TransferInfo)value).getFileName()+"' from "+source
					: "Problem while uploading '"+((TransferInfo)value).getFileName()+"' to "+source;
			}
			
			if(text!=null)
			{
				// Add status component for improtant events, if panel is not showing.
				if(!tpane.isShowing() && (NOTIFICATION_NEW_MSG.equals(type)
						|| NOTIFICATION_NEW_FILE.equals(type)))
				{
					JComponent	scomp	= getJCC().getStatusComponent("chat-status-comp");
					if(scomp==null)
					{
						final JButton but	= new JButton(ChatPlugin.getStatusIcon(false));
						scomp	= but;
						assert icontimer==null;
						icontimer	= new Timer(500, new ActionListener()
						{
							boolean	star;
							public void actionPerformed(ActionEvent e)
							{
								star	= !star;
								but.setIcon(ChatPlugin.getStatusIcon(star));
							}
						});
						icontimer.setRepeats(true);
						icontimer.start();
						but.setMargin(new Insets(0, 0, 0, 0));
						but.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								getJCC().showPlugin(ChatPlugin.PLUGIN_NAME);
							}
						});
						getJCC().addStatusComponent("chat-status-comp", scomp);
						tpane.addHierarchyListener(new HierarchyListener()
						{
							public void hierarchyChanged(HierarchyEvent e)
							{
								if(tpane.isShowing())
								{
									getJCC().removeStatusComponent("chat-status-comp");
									icontimer.stop();
									icontimer	= null;
									tpane.removeHierarchyListener(this);
								}
							}
						});
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
				tpane.getComponentAt(tab).addHierarchyListener(new HierarchyListener()
				{
					public void hierarchyChanged(HierarchyEvent e)
					{
						if(tpane.getComponentAt(tab).isShowing())
						{
							tpane.setIconAt(tab, null);
							tpane.getComponentAt(tab).removeHierarchyListener(this);
						}
					}
				});
			}
			
			if(!quiet && sound)
			{
				playSound(type, false);
			}
		}
	}

	/**
	 *  Play the notification sound for the selected event.
	 *  @param type	The notification event.
	 */
	protected void playSound(final String type, final boolean verbose)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		// Hack!!! Play only one sound at a time (otherwise toaster vm crashes :-( )
		if(!playing)
		{
			playing	= true;
			
			// Hack to avoid freeze on OpenJDK
			new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						String filename = getNotificationSound(type);
						URL	url	= this.getClass().getResource(filename);
						if(url==null)
						{
							File f = new File(filename);
							if(f.exists())
								url = f.toURI().toURL();
						}
						// Cannot use stream due to jar starter bug.
						final AudioInputStream	ais	= AudioSystem.getAudioInputStream(url); // (is);
						AudioFormat	format	= ais.getFormat();
						DataLine.Info	info	= new DataLine.Info(Clip.class, format);
						Clip	clip	= (Clip)AudioSystem.getLine(info);
						// OpenJDK hangs below :-(
						clip.open(ais);
						clip.addLineListener(new LineListener()
						{
							public void update(LineEvent event)
							{
								if(event.getType()==LineEvent.Type.STOP)
								{
									// Close the clip after it finished playing.
									event.getLine().close();
									try
									{
										ais.close();
									}
									catch(Exception e)
									{
									}
									SwingUtilities.invokeLater(new Runnable()
									{
										public void run()
										{
											playing	= false;
										}
									});
								}
							}
						});
						clip.start();
	//					while(clip.isRunning())
	//					{
	//						Thread.yield();
	//					}
					}
					catch(Throwable e)	// AssertionError in org.classpath.icedtea.pulseaudio.Stream.disconnect(Stream.java:557) grrr...
					{
						if(verbose)
						{
							System.err.println("Couldn't play notification sound '"+type+"': "+e);
						}
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								playing	= false;
							}
						});
					}
				}
			}).start();
		}
	}
	
	/**
	 *  Update the fileinfo in the upload/download area.
	 */
	public void updateTransfer(final TransferInfo fi)
	{
		// Close open dialogs, if any.
		if(dialogs.containsKey(fi))
		{
			SGUI.getWindowParent(dialogs.get(fi)).dispose();
		}
		
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
	 *  Append text.
	 */
	public static void append(Color c, String s, JTextPane p) 
	{ 
		String found = null;
		for(String key: smileys)
		{
			if(s.startsWith(key))
			{
				found = key;
				break;
			}
			
		}
		if(found!=null)
		{
			doAppend(c, found, p);
			if(s.length()>found.length())
			{
				String next = s.substring(found.length(), s.length());
				append(c, next, p);
			}
		}
//		else if(s.startsWith("\r\n"))
//		{
//			doAppend(c, "\n\r", p);
//			if(s.length()>2)
//			{
//				String next = s.substring(2, s.length());
//				append(c, next, p);
//			}
//		}
		else
		{
			String next = s.substring(0, 1);
			doAppend(c, next, p);
			if(s.length()>1)
			{
				next = s.substring(1, s.length());
				append(c, next, p);
			}
		}
	}
	
	/**
	 * 
	 */
	public static void doAppend(Color c, String s, JTextPane p) 
	{ 
		p.setEditable(true);
		
		SimpleAttributeSet aset = new SimpleAttributeSet();
	    if(smileys.contains(s))
	    {
	    	StyleConstants.setIcon(aset, icons.getIcon(s));
	    }
	    else
	    {
	    	StyleConstants.setForeground(aset, c);
	    }
	    
		// better implementation--uses
	    // StyleContext
//	    StyleContext sc = StyleContext.getDefaultStyleContext();
//	    SimpleAttributeSet aset = new SimpleAttributeSet(sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c));
//	    if(smileys.contains(s))
//	    	StyleConstants.setIcon(aset, icons.getIcon(s));
		
	    StyledDocument doc = p.getStyledDocument();
	    aset.addAttribute("dummy", Integer.valueOf(doc.getLength())); // Java bug: https://forums.oracle.com/forums/thread.jspa?threadID=1355974
	    
//	    int len = p.getDocument().getLength(); // same value as// getText().length();
	    p.setCaretPosition(doc.getLength()); // place caret at the end (with no selection)
	    p.setCharacterAttributes(aset, false);
	    p.replaceSelection(s); // there is no selection, so inserts at caret
//	    try
//	    {
//	    	doc.insertString(doc.getLength(), s, aset);
//	    }
//	    catch(Exception e)
//	    {
//	    	e.printStackTrace();
//	    }
	
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
							acceptFile(fi)
								.addResultListener(new IResultListener<String>()
							{
								public void resultAvailable(String filepath)
								{
									getService().acceptFile(fi.getId(), filepath);
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
				// Open (folder) only allowed in local GUI.
				if(isLocal())
				{
					JMenuItem	mi = new JMenuItem("Open");
					mi.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							try
							{
								Desktop.getDesktop().open(new File(fi.getFilePath()).getCanonicalFile());
							}
							catch(IOException ex)
							{
								// Doesn't work for PDf on windows :-(  http://bugs.sun.com/view_bug.do?bug_id=6764271
								SGUI.showError(table, "Error Opening File", "File '"+new File(fi.getFilePath()).getName()+"' could not be opened.", ex);
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
								Desktop.getDesktop().open(new File(fi.getFilePath()).getParentFile().getCanonicalFile());
							}
							catch(IOException ex)
							{
								SGUI.showError(table, "Error Opening Folder", "Folder '"+new File(fi.getFilePath()).getParentFile().getName()+"'could not be opened.", ex);
							}
						}
					});
					menu.add(mi);
				}

				JMenuItem	mi = new JMenuItem("Remove");
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
		/** The known chat users (cid->user state). */
		protected Map<IComponentIdentifier, ChatUser>	users = new LinkedHashMap<IComponentIdentifier, ChatUser>();

		/** The column names. */
		protected String[]	columns	= new String[]{"Users"};
		
		/** The selected users. */
		protected List<ChatUser> sels;
		
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
			return users==null? 0: users.size();
		}
		
		public Object getValueAt(int row, int column)
		{
			IComponentIdentifier[]	cids	= users.keySet().toArray(new IComponentIdentifier[users.size()]);
			ChatUser cu = users.get(cids[row]);
			return cu;
		}
		
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
		
		public ChatUser getUser(IComponentIdentifier cid)
		{
			return users.get(cid);
		}
		
		public Collection<ChatUser> getUsers()
		{
			return users.values();
		}
		
		public Collection<IComponentIdentifier> getUserIDs()
		{
			return users.keySet();
		}
		
		public void addUser(IComponentIdentifier cid, ChatUser user)
		{
			saveUserSelection();
			
			users.put(cid, user);
			fireTableRowsInserted(users.size()-1, users.size()-1);
		
			restoreUserSelection();
		}
		
		public void removeUser(IComponentIdentifier cid)
		{
			saveUserSelection();
			
			Iterator<IComponentIdentifier> it = users.keySet().iterator();
			int row = -1;
			for(int i=0; it.hasNext(); i++)
			{
				IComponentIdentifier key = it.next();
				if(key.equals(cid))
				{
					row = i;
				}
			}
			if(row!=-1)
			{
				users.remove(cid);
				fireTableRowsDeleted(row, row);
			}
			
			restoreUserSelection();
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
				
		/**
		 *  Save the current user selection 
		 */
		protected void saveUserSelection()
		{
			int[] rows = usertable.getSelectedRows();
			ChatUser[] users = usermodel.getUsers().toArray(new ChatUser[0]);
			
			sels = new ArrayList<ChatUser>();
			for(int i=0; i<rows.length; i++)
			{
				sels.add(users[rows[i]]);
			}
		}
		
		/**
		 *  Restore the current user selection.
		 */
		protected void restoreUserSelection()
		{
			usertable.clearSelection();
			
			if(sels!=null && !sels.isEmpty())
			{
				List<ChatUser> users = new ArrayList<ChatUser>();
				users.addAll(usermodel.getUsers());

				for(ChatUser cu: sels)
				{
					int idx = users.indexOf(cu);
					if(idx>=0)
					{
						usertable.addRowSelectionInterval(idx, idx);
					}
				}
			}
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
			this.columns = new String[]{"Name", "Path", down? "Sender": "Receiver", "Size", "Done", "%", "State", "Speed", "Remaining Time"};
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
				ret = files[row].getFileName();
			}
			else if(column==1)
			{
				ret = files[row].getFilePath()!=null ? files[row].getFilePath() : "n/a";
			}
			else if(column==2)
			{
				ret = files[row].getOther();
			}
			else if(column==3)
			{
				ret = files[row].getSize();
			}
			else if(column==4)
			{
				ret = files[row].getDone();
			}
			else if(column==5)
			{
				ret = files[row].getSize()==0 ? 1 : Double.valueOf(((double)files[row].getDone())/files[row].getSize());
			}
			else if(column==6)
			{
				ret = files[row].getState();
			}
			else if(column==7)
			{
				ret = files[row];
			}
			else if(column==8)
			{
				if(TransferInfo.STATE_TRANSFERRING.equals(files[row].getState()))
				{
					long	time	= (long)((files[row].getSize()-files[row].getDone())/files[row].getSpeed());
					long	hrs	= time / 3600;
					long	min	= time % 3600 / 60;
					long	sec	= time % 60;
					ret	= hrs + ":" + (min<10 ? "0"+min : min) + ":" + (sec<10 ? "0"+sec : sec);
				}
				else if(files[row].getTimeout()>0)
				{
					long	time	= (files[row].getTimeout()-System.currentTimeMillis())/1000;
					ret	= time>0 ? Long.toString(time) : "0";
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
		{
			autorefresh = Boolean.parseBoolean(ar);
			updateRefreshTimer();
		}
		String snd = props.getStringProperty("sound");
		if(snd!=null)
		{
			sound = Boolean.parseBoolean(snd);
		}
		double lp = props.getDoubleProperty("listpan");
		if(lp!=0)
		{
			listpan.setDividerLocation(lp);
		}
		double hs = props.getDoubleProperty("horsplit");
		if(hs!=0)
		{
			horsplit.setDividerLocation(hs);
		}
		
		snd = props.getStringProperty(NOTIFICATION_FILE_ABORT);
		if(snd!=null)
		{
			notificationsounds.put(NOTIFICATION_FILE_ABORT, snd);
		}
		snd = props.getStringProperty(NOTIFICATION_FILE_COMPLETE);
		if(snd!=null)
		{
			notificationsounds.put(NOTIFICATION_FILE_COMPLETE, snd);
		}
		snd = props.getStringProperty(NOTIFICATION_NEW_FILE);
		if(snd!=null)
		{
			notificationsounds.put(NOTIFICATION_NEW_FILE, snd);
		}
		snd = props.getStringProperty(NOTIFICATION_NEW_MSG);
		if(snd!=null)
		{
			notificationsounds.put(NOTIFICATION_NEW_MSG, snd);
		}
		snd = props.getStringProperty(NOTIFICATION_NEW_USER);
		if(snd!=null)
		{
			notificationsounds.put(NOTIFICATION_NEW_USER, snd);
		}
		
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
	
//	/**
//	 * 
//	 */
//	protected static ImageIcon getImage() 
//	{
//        BufferedImage bi = new BufferedImage(15, 15, BufferedImage.TYPE_INT_ARGB);
//        Graphics g = bi.getGraphics();
//        g.setColor(Color.red);
//        g.drawOval(0, 0, 14, 14);
//        g.drawLine(4, 9, 9, 9);
//        g.drawOval(4, 4, 1, 1);
//        g.drawOval(10, 4, 1, 1);
//        return new ImageIcon(bi);
//    }

}

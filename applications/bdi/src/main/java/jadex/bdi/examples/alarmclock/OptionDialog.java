package jadex.bdi.examples.alarmclock;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.annotations.Classname;

/**
 *  Configure the alarmclock options.
 */
public class OptionDialog extends JDialog
{
	//-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"Browse", SGUI.makeIcon(OptionDialog.class,	"/jadex/bdi/examples/alarmclock/images/dots_small.png")
	});

	//-------- attributes --------

	/** The agent. */
	protected IExternalAccess agent;

	//-------- constructors --------

	/**
	 *  Create a new test center panel.
	 */
	public OptionDialog(final JFrame parent, final IExternalAccess agent)
	{
		super(parent, true);
		this.agent = agent;
		setTitle("Options");
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("create")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
				final Settings orig_sets = (Settings)bia.getBeliefbase().getBelief("settings").getFact();
				final Settings sets = (Settings)orig_sets.clone();
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						final JRadioButton ampm = new JRadioButton("AM/PM");
						ampm.setSelected(sets.isAMPM());
						final JRadioButton hrs = new JRadioButton("24 hours");
						hrs.setSelected(!sets.isAMPM());
						ButtonGroup bg = new ButtonGroup();
						bg.add(ampm);
						bg.add(hrs);
						
						JButton save = new JButton("Save");
						JButton load = new JButton("Load");
						save.setMargin(new Insets(0,0,0,0));
						load.setMargin(new Insets(0,0,0,0));
						final JTextField deffile = new JTextField(sets.getFilename());
						final JCheckBox autosave = new JCheckBox();
						autosave.setSelected(sets.isAutosave());
						JButton browse = new JButton(icons.getIcon("Browse"));
						browse.setMargin(new Insets(0,0,0,0));
						final JButton ok = new JButton("OK");
						final JButton apply = new JButton("Apply");
						final JButton cancel = new JButton("Cancel");
						Dimension md = cancel.getMinimumSize();
						Dimension pd = cancel.getPreferredSize();
						ok.setMinimumSize(md);
						ok.setPreferredSize(pd);
						apply.setMinimumSize(md);
						apply.setPreferredSize(md);
		
						final JFileChooser filechooser = new JFileChooser(".");
						filechooser.setAcceptAllFileFilterUsed(true);
						final javax.swing.filechooser.FileFilter load_filter = new javax.swing.filechooser.FileFilter()
						{
							public String getDescription()
							{
								return "XMLs (*.xml)";
							}
		
							public boolean accept(File f)
							{
								String name = f.getName();
								return f.isDirectory() || (name.endsWith(".xml"));
							}
						};
						filechooser.addChoosableFileFilter(load_filter);
						final JSpinner fontsize = new JSpinner();
						fontsize.setValue(Integer.valueOf(sets.getFontsize()));
		
						JPanel tf = new JPanel(new GridBagLayout());
						tf.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(), "Layout"));
						tf.add(new JLabel("Time format:"), new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHEAST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
						tf.add(ampm, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
						tf.add(hrs, new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
						tf.add(new JLabel("Font size:"), new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
						tf.add(fontsize, new GridBagConstraints(1,1,1,1,1,0,GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		
						JPanel sett = new JPanel(new GridBagLayout());
						sett.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(), "Load/Save Settings"));
						sett.add(new JLabel("Settings file:"), new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
						sett.add(deffile, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.NORTHWEST,
							GridBagConstraints.HORIZONTAL, new Insets(4,2,2,4),0,0));
						sett.add(browse, new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
						sett.add(save, new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
						sett.add(load, new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.NORTHWEST,
								GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		
						sett.add(new JLabel("Autosave:"), new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
						sett.add(autosave, new GridBagConstraints(1,1,GridBagConstraints.REMAINDER,1,1,0,GridBagConstraints.NORTHWEST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		
						JPanel buts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
						buts.add(ok);
						buts.add(apply);
						buts.add(cancel);
						sett.add(buts, new GridBagConstraints(0,3,GridBagConstraints.REMAINDER,1,1,1,GridBagConstraints.NORTHEAST,
							GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
		
						ampm.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								sets.setAMPM(true);
							}
						});
						fontsize.addChangeListener(new ChangeListener()
						{
							public void stateChanged(ChangeEvent e)
							{
								sets.setFontsize(((Integer)fontsize.getValue()).intValue());
							}
						});
						hrs.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								sets.setAMPM(false);
							}
						});
						browse.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								if(filechooser.showDialog(SGUI.getWindowParent(OptionDialog.this)
									, "Load")==JFileChooser.APPROVE_OPTION)
								{
									File file = filechooser.getSelectedFile();
									//System.out.println("File is: "+file);
									deffile.setText(""+file);
									sets.setFilename(""+file);
								}
							}
						});
						deffile.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								sets.setFilename(deffile.getText());
							}
						});
						save.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								File f = new File(deffile.getText());
								//if(f.exists())
								filechooser.setSelectedFile(f);
								if(filechooser.showDialog(SGUI.getWindowParent(OptionDialog.this)
									, "Save")==JFileChooser.APPROVE_OPTION)
								{
									try
									{
										File file = filechooser.getSelectedFile();
										sets.setFilename(file.getAbsolutePath());
										sets.save();
										deffile.setText(file.getAbsolutePath());
									}
									catch(Exception ex)
									{
										JOptionPane.showMessageDialog(OptionDialog.this, "Cannot save settings. The file: \n"
											+filechooser.getSelectedFile().getAbsolutePath()+"\n could not be written", "Settings error",
											JOptionPane.ERROR_MESSAGE);
										//System.out.println("Could not save settings: "+ex);
									}
								}
							}
						});
						load.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								File f = new File(deffile.getText());
								if(f.exists())
									filechooser.setSelectedFile(f);
								if(filechooser.showDialog(SGUI.getWindowParent(OptionDialog.this)
									, "Load")==JFileChooser.APPROVE_OPTION)
								{
									try
									{
										File file = filechooser.getSelectedFile();
										final Settings ns = Settings.loadSettings(file.getAbsolutePath());
										agent.scheduleStep(new IComponentStep<Void>()
										{
											@Classname("alarms")
											public IFuture<Void> execute(IInternalAccess ia)
											{
												IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
												bia.getBeliefbase().getBelief("settings").setFact(ns);
												bia.getBeliefbase().getBeliefSet("alarms").removeFacts();
												bia.getBeliefbase().getBeliefSet("alarms").addFacts(ns.getAlarms());
		
												return IFuture.DONE;
											}
										});
		//								agent.getBeliefbase().setBeliefFact("settings", ns);
		//								agent.getBeliefbase().removeBeliefSetFacts("alarms");
		//								agent.getBeliefbase().addBeliefSetFacts("alarms", ns.getAlarms());
										// Refresh gui
										autosave.setSelected(ns.isAutosave());
										ampm.setSelected(ns.isAMPM());
										hrs.setSelected(!ns.isAMPM());
										deffile.setText(file.getAbsolutePath());
									}
									catch(Exception ex)
									{
										JOptionPane.showMessageDialog(OptionDialog.this, "Cannot load settings. The file: \n"
											+filechooser.getSelectedFile().getAbsolutePath()+"\n could not be read", "Settings error",
											JOptionPane.ERROR_MESSAGE);
										//System.out.println("Could not load settings: "+ex);
									}
									
		//							agent.getComponentFeature(IRequiredServicesFeature.class).getService(IClockService.class).addResultListener(new SwingDefaultResultListener()
		//							{
		//								public void customResultAvailable(Object source, Object result)
		//								{
		//									
		//								}
		//							});
								}
							}
						});
						autosave.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								sets.setAutosave(autosave.isSelected());
							}
						});
						ok.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								OptionDialog.this.dispose();
								copySettings(sets, orig_sets);
							}
						});
						apply.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								copySettings(sets, orig_sets);
							}
						});
						cancel.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								OptionDialog.this.dispose();
							}
						});
		
						JPanel pan = new JPanel(new BorderLayout());
						pan.add(tf, "North");
						pan.add(sett, "Center");
		
						getContentPane().add("Center", pan);
						
						// Must be done here because 
						pack();
						setLocation(SGUI.calculateMiddlePosition(OptionDialog.this));
						setVisible(true);
						
						}
					});
				return IFuture.DONE;
			}
		});
	}

	/**
	 *  Copy the settings back to the used settings.
	 */
	protected void copySettings(Settings from, Settings to)
	{
		// todo: would be nicer with a property change from the settings
		boolean rep = to.isAMPM()!=from.isAMPM() || to.getFontsize()!=from.getFontsize();
		to.setAMPM(from.isAMPM());
		to.setAutosave(from.isAutosave());
		to.setFilename(from.getFilename());
		to.setFontsize(from.getFontsize());
		if(rep)
			((ClockFrame)getParent()).refresh(false);
	}

//	/**
//	 *  Main for testing.
//	 *  @param args The arguments.
//	 */
//	public static void main(String[] args)
//	{
//		Dialog dia = new OptionDialog(new ClockFrame(null), null);
//		dia.pack();
//		dia.setVisible(true);
//	}
}

package jadex.tools.testcenter;

import jadex.bdi.planlib.test.Testcase;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bridge.IAgentFactory;
import jadex.bridge.ILibraryService;
import jadex.bridge.Properties;
import jadex.bridge.Property;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.tools.common.BrowserPane;
import jadex.tools.common.EditableList;
import jadex.tools.common.ElementPanel;
import jadex.tools.common.ScrollablePanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import nuggets.Nuggets;

/**
 *  The test center panel for running tests and viewing the results.
 */
public class TestCenterPanel extends JSplitPane
{
	//-------- constants --------
	
	/** The file extension for test suites. */
	public static final String FILEEXTENSION_TESTS = ".tests";

	//-------- attributes --------

	/** The table of tests. */
	protected EditableList teststable;

	/** The test center plugin. */
	protected TestCenterPlugin plugin;

	/** The current test suite (if any). */
	protected TestSuite	testsuite;

	/** The start/abort button. */
	protected JButton startabort;
	
	/** The clear report button. */
	protected JButton	clearreport;

	/** The progress bar. */
	protected JProgressBar progress;
	
	/** The state label. */
	protected JLabel	statelabel;
		
	/** The details view. */
	protected JTextPane	details;

	/** Timeout textfield. */
	protected JTextField tfto;
	
	/** Concurrency combo box. */
	protected JComboBox tfpar;
	
	/** Allow duplicate entries in test suite. */
	protected JCheckBox allowduplicates;
	
	/** The last generated report. */
	protected String report;
	
	/** The testcase concurrency. */
	protected int	concurrency;
	
	//-------- constructors --------

	/**
	 *  Create a new test center panel.
	 */
	public TestCenterPanel(final TestCenterPlugin plugin)
	{
		this.plugin = plugin;
		this.concurrency	= 1;
		this.setResizeWeight(0.5);
	
		final JFileChooser addchooser = new JFileChooser(".");
		addchooser.setAcceptAllFileFilterUsed(true);
		final IAgentFactory agentfactory = plugin.getJCC().getAgent().getPlatform().getAgentFactory();
		final javax.swing.filechooser.FileFilter load_filter = new javax.swing.filechooser.FileFilter()
		{
			public String getDescription()
			{
				return "ADFs (*.agent.xml)";
			}

			public boolean accept(File f)
			{
				String name = f.getName();
//				return f.isDirectory() || SXML.isAgentFilename(name);
				return f.isDirectory() || agentfactory.isStartable(name);
			}
		};
		addchooser.addChoosableFileFilter(load_filter);
		addchooser.setMultiSelectionEnabled(true);

		final JFileChooser loadsavechooser = new JFileChooser(".");
		final javax.swing.filechooser.FileFilter save_filter = new javax.swing.filechooser.FileFilter()
		{
			public String getDescription()
			{
				return "Testcases (*.tests)";
			}

			public boolean accept(File f)
			{
				return f.isDirectory() || f.getName().endsWith(FILEEXTENSION_TESTS);
			}
		};
		loadsavechooser.addChoosableFileFilter(save_filter);
		
		final JFileChooser saverepchooser = new JFileChooser(".");
		saverepchooser.setSelectedFile(new File("test_report.html"));
		saverepchooser.setAcceptAllFileFilterUsed(true);
		final javax.swing.filechooser.FileFilter savereport_filter = new javax.swing.filechooser.FileFilter()
		{
			public String getDescription()
			{
				return "HTMLs (*.html)";
			}

			public boolean accept(File f)
			{
				String name = f.getName();
				return f.isDirectory() || name.toLowerCase().endsWith("html") || name.toLowerCase().endsWith("htm");
			}
		};
		saverepchooser.addChoosableFileFilter(savereport_filter);
		saverepchooser.setMultiSelectionEnabled(true);

		JPanel testcases = new ScrollablePanel(null, false, true);
		testcases.setLayout(new GridBagLayout());
		testcases.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Test suite settings "));
		this.teststable = new EditableList("Test cases", true);
		
		JScrollPane	scroll	= new JScrollPane(teststable);
		teststable.setPreferredScrollableViewportSize(new Dimension(400, 200)); // todo: hack
		tfto = new JTextField(""+plugin.getJCC().getAgent().getBeliefbase().getBelief("testcase_timeout").getFact(), 6);
		tfto.setMinimumSize(tfto.getPreferredSize());
		tfto.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				extractTimeoutValue(tfto.getText());
			}
		});
		tfto.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent fe)
			{
				extractTimeoutValue(tfto.getText());
			}
		});
		this.tfpar = new JComboBox(new String[]{"1", "5", "10", "all"});
		tfpar.setPreferredSize(new Dimension(tfto.getPreferredSize().width, tfpar.getPreferredSize().height));
		tfpar.setEditable(true);
		tfpar.setMinimumSize(tfpar.getPreferredSize());
		tfpar.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				extractConcurrencyValue((String)tfpar.getModel().getSelectedItem());
			}
		});
		tfpar.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent fe)
			{
				extractConcurrencyValue((String)tfpar.getModel().getSelectedItem());
			}
		});
		tfpar.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				extractConcurrencyValue((String)tfpar.getModel().getSelectedItem());
			}
		});
		allowduplicates = new JCheckBox("Allow including the same test more than once");
		allowduplicates.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				teststable.setAllowDuplicates(allowduplicates.isSelected());
			}
		});
		JButton add = new JButton("Add");
		add.setToolTipText("Add testcases to the test suite");
		JButton load = new JButton("Load");
		load.setToolTipText("Load a test suite");
		JButton save = new JButton("Save");
		save.setToolTipText("Save a test suite");
		JButton clear = new JButton("Clear");
		clear.setToolTipText("Clear the test suite");
		testcases.add(scroll, new GridBagConstraints(0,0,7,1,1,1,GridBagConstraints.NORTHWEST,
			GridBagConstraints.BOTH, new Insets(4,2,2,4),0,0));
		
		testcases.add(allowduplicates, new GridBagConstraints(0,1,GridBagConstraints.REMAINDER,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.NONE, new Insets(0,0,0,0),0,0));

		testcases.add(new JLabel("Testcase timeout [ms]:"), new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.NONE, new Insets(0,4,2,0),0,0));
		testcases.add(tfto, new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.HORIZONTAL, new Insets(2,2,2,0),0,0));
		testcases.add(new JLabel("Testcase concurrency:"), new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.NONE, new Insets(2,4,2,0),0,0));
		testcases.add(tfpar, new GridBagConstraints(1,3,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.HORIZONTAL, new Insets(2,2,2,0),0,0));

		testcases.add(new JLabel(), new GridBagConstraints(2,1,1,2,1,0,GridBagConstraints.WEST,
			GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2),0,0));

		
		testcases.add(add, new GridBagConstraints(3,2,1,2,0,0,GridBagConstraints.SOUTH,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		testcases.add(load, new GridBagConstraints(4,2,1,2,0,0,GridBagConstraints.SOUTH,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		testcases.add(save, new GridBagConstraints(5,2,1,2,0,0,GridBagConstraints.SOUTH,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		testcases.add(clear, new GridBagConstraints(6,2,1,2,0,0,GridBagConstraints.SOUTH,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		
		JPanel testperformer = new JPanel(new GridBagLayout());
		testperformer.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Test suite execution "));
		this.progress = new JProgressBar(JProgressBar.HORIZONTAL);

		this.startabort = new JButton("Start");
		JButton savereport = new JButton("Save");
		clearreport = new JButton("Clear");
		startabort.setToolTipText("Start the execution of the test suite.");
		savereport.setToolTipText("Save the current test suite report.");
		clearreport.setToolTipText("Clear the current test suite report.");
		this.statelabel	= new JLabel("State: Idle");
		testperformer.add(statelabel, new GridBagConstraints(0,0,3,1,1,0,GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		testperformer.add(progress, new GridBagConstraints(0,1,3,1,1,0,GridBagConstraints.NORTHWEST,
			GridBagConstraints.HORIZONTAL, new Insets(4,2,2,4),0,0));
		testperformer.add(startabort, new GridBagConstraints(0,2,1,1,1,0,GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		testperformer.add(savereport, new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		testperformer.add(clearreport, new GridBagConstraints(2,2,1,1,0,0,GridBagConstraints.EAST,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));

		// Calculate button sizes.
		SGUI.adjustComponentSizes(new JButton[]{add, load, save, clear, startabort, savereport, new JButton("Abort")});
		progress.setPreferredSize(new Dimension(progress.getPreferredSize().width, add.getPreferredSize().height));

		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(addchooser.showDialog(SGUI.getWindowParent(TestCenterPanel.this)
					, "Load")==JFileChooser.APPROVE_OPTION)
				{
					File[] files = addchooser.getSelectedFiles();
					for(int i=0; i<files.length; i++)
						teststable.addEntry(""+files[i]);
				}
			}
		});

		save.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(loadsavechooser.showDialog(SGUI.getWindowParent(TestCenterPanel.this)
					, "Save")==JFileChooser.APPROVE_OPTION)
				{
					File file = loadsavechooser.getSelectedFile();
					if(file!=null)
					{
						try
						{
							if(!file.getName().endsWith(FILEEXTENSION_TESTS))
							{
								file	= new File(file.getParentFile(), file.getName()+FILEEXTENSION_TESTS);
								loadsavechooser.setSelectedFile(file);
							}
							Nuggets nug = new Nuggets();
							FileOutputStream fos = new FileOutputStream(file);
							ClassLoader cl = ((ILibraryService)plugin.getJCC().getAgent().getPlatform().getService(ILibraryService.class)).getClassLoader();
							nug.write(teststable.getEntries(), fos, cl);
							fos.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		});

		load.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(loadsavechooser.showDialog(SGUI.getWindowParent(TestCenterPanel.this)
					, "Load")==JFileChooser.APPROVE_OPTION)
				{
					File file = loadsavechooser.getSelectedFile();
					if(file!=null)
					{
						try
						{
							FileInputStream fis = new FileInputStream(file);
							Nuggets nug = new Nuggets();
							
							ClassLoader cl = ((ILibraryService)plugin.getJCC().getAgent().getPlatform().getService(ILibraryService.class)).getClassLoader();
							String[] names = (String[])nug.readObject(fis, cl);
							teststable.setEntries(names);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		});

		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				teststable.setEntries(new String[0]);
			}
		});

		startabort.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(testsuite==null || !testsuite.isRunning())
				{
					testsuite	= new TestSuite(teststable.getEntries());
					testsuite.start();
				}
				else
				{
					testsuite.abort();
				}
			}
		});
		
		savereport.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ev)
			{
				if(saverepchooser.showDialog(SGUI.getWindowParent(TestCenterPanel.this),
					"Save Report")==JFileChooser.APPROVE_OPTION)
				{
					File file = saverepchooser.getSelectedFile();
					if(file!=null)
					{
						try
						{
							FileWriter fw = new FileWriter(file);
							fw.write(report);
							fw.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		});
		
		clearreport.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(testsuite!=null && !testsuite.isRunning())
				{
					testsuite	= null;
					updateProgress();
					updateDetails();
				}
			}
		});
		
		JPanel	top	= new JPanel(new BorderLayout());
		JScrollPane	scrollx	= new JScrollPane(testcases);
		scrollx.setBorder(null);
		top.add(BorderLayout.CENTER, scrollx);
		top.add(BorderLayout.SOUTH, testperformer);
		
		JPanel bottom = new JPanel(new BorderLayout());
		bottom.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Details "));
		this.details = new BrowserPane();
		details.setMinimumSize(new Dimension(400,100));
		details.setPreferredSize(new Dimension(400,100));
		new ElementPanel("","").setStylesheet(details);	// Hack!!!
		JScrollPane	scroll2	= new JScrollPane(details);
		bottom.add(BorderLayout.CENTER, scroll2);

		setOrientation(JSplitPane.VERTICAL_SPLIT);
		setOneTouchExpandable(true);
		setDividerLocation(300);
		setTopComponent(top);
		setBottomComponent(bottom);
	}

	/**
	 * Load the properties.
	 */
	public void setProperties(Properties props)
	{
		// Load settings into fresh state.
		reset();
		
		teststable.setAllowDuplicates(props.getBooleanProperty("allowduplicates"));
		allowduplicates.setSelected(props.getBooleanProperty("allowduplicates"));
		
		Property[]	entries	= props.getProperties("entry");
		for(int i=0; i<entries.length; i++)
			teststable.addEntry(entries[i].getValue());
		
		String timeout	= ""+plugin.getJCC().getAgent().getBeliefbase().getBelief("testcase_timeout").getFact();
		if(props.getProperty("timeout")!=null)
		{
			timeout	= props.getStringProperty("timeout");
			plugin.getJCC().getAgent().getBeliefbase().getBelief("testcase_timeout").setFact(new Integer(timeout));
		}
		tfto.setText(timeout);

		if(props.getProperty("concurrency")!=null)
		{
			concurrency  = props.getIntProperty("concurrency");
			if(concurrency==-1)
				tfpar.getModel().setSelectedItem("all");
			else
				tfpar.getModel().setSelectedItem(""+concurrency);
		}
	}

	/**
	 * Save the properties.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		String[]	entries	= teststable.getEntries();
		for(int i=0; i<entries.length; i++)
		{
			String	entry	= SUtil.convertPathToRelative(entries[i]);
			props.addProperty(new Property("entry", entry));
		}

		props.addProperty(new Property("timeout", tfto.getText()));
		props.addProperty(new Property("concurrency", ""+concurrency));
		props.addProperty(new Property("allowduplicates", ""+allowduplicates.isSelected()));
		
		return props;
	}
	
	/**
	 *  Update the test suite progress.
	 */
	protected void	updateProgress()
	{
		if(testsuite!=null)
		{
			Testcase[]	testcases	= testsuite.getTestcases();
			int performed	= 0;
			int failed	= 0;
			for(int i=0; i<testcases.length; i++)
			{
				if(testcases[i]!=null && testcases[i].isPerformed())
				{
					performed++;
					if(!testcases[i].isSucceeded())
					{
						failed++;
					}
				}
			}
			
			// Update progress bar.
			progress.setMinimum(0);
			progress.setMaximum(testcases.length);
			progress.setStringPainted(true);
			progress.setValue(performed);
			if(failed==0)
			{
				progress.setForeground(Color.green);
			}
			else
			{
				progress.setForeground(Color.red);			
			}
			long alldur = System.currentTimeMillis() - testsuite.getStartTime();
			progress.setString("Performed: " + (performed) + "/" + testcases.length
					+ " in " + SUtil.getDurationHMS(alldur)
					+ "     Failed: "+failed+"/"+testcases.length);
		}
		else
		{
			progress.setStringPainted(false);
			progress.setMinimum(0);
			progress.setMaximum(100);
			progress.setValue(0);
			progress.setForeground(new JProgressBar().getForeground());
		}
		progress.repaint();
		
		
		// Set state label.
		if(testsuite==null)
		{
			statelabel.setText("State: Idle");
		}
		else if(testsuite.isRunning())
		{
			statelabel.setText("State: Running");
		}
		else if(testsuite.isAborted())
		{
			statelabel.setText("State: Aborted");
		}
		else
		{
			statelabel.setText("State: Finished");
		}
		
		// Set start/abort button.
		if(testsuite!=null && testsuite.isRunning())
		{
			startabort.setText("Abort");
			startabort.setToolTipText("Abort the execution of the test suite.");
		}
		else
		{
			startabort.setText("Start");
			startabort.setToolTipText("Start the execution of the test suite.");
		}
		
		// Set state of clear button.
//		clearreport.setEnabled(testsuite!=null && !testsuite.isRunning());
	}

	/**
	 *  Generate a report text for a run.
	 *  @param suite The test suite.
	 *  @return The report.
	 */
	protected String generateReport(TestSuite suite)
	{
		String[]	names	= suite.getTestcaseNames();
		Testcase[]	testcases	= suite.getTestcases();
		int performed	= 0;
		int failed	= 0;
		for(int i=0; i<testcases.length; i++)
		{
			if(testcases[i]!=null && testcases[i].isPerformed())
			{
				performed++;
				if(!testcases[i].isSucceeded())
				{
					failed++;
				}
			}
		}
		
		// Heading shows number of test cases performed / to be performed.
		final StringBuffer	text	= new StringBuffer();
		text.append("<a name=\"top\"></a>");
		if(suite.isRunning())
		{
			text.append("<h3>Performed ");
			text.append(performed);
			text.append(" of ");
			text.append(testcases.length);
			text.append(" Test Cases</h3>\n");
		}
		else if(suite.isAborted())
		{
			text.append("<h3>Aborted after ");
			text.append(performed);
			text.append(" of ");
			text.append(testcases.length);
			text.append(" Test Cases</h3>\n");
		}
		else
		{
			text.append("<h3>Performed ");
			text.append(testcases.length);
			text.append(" Test Cases</h3>\n");
		}
		
		// Table shows state of test cases.
		text.append("<table>\n");
		for(int i=0; i<names.length; i++)
		{
			text.append("<tr>\n");
			
			// Number column.
			text.append("<td width=\"25\" align=\"right\"><strong>");
			text.append(i+1);
			text.append("</strong></td>\n");
			
			// Empty columns consuming extra space. Hack!!!
			text.append("<td>&nbsp;</td>\n");

			// Name column (with link for already performed testcases).
			if(testcases[i]!=null && testcases[i].isPerformed())
			{
				text.append("<td><a href=\"#");
				text.append(names[i]);
				text.append(i);
				text.append("\">");
				text.append(names[i]);
				text.append("</a></td>\n");
			}
			else
			{
				text.append("<td>");
				text.append(names[i]);
				text.append("</td>\n");
			}

			// Column for success state.
			if(testcases[i]!=null)
			{
				if(testcases[i].isPerformed() && testcases[i].isSucceeded())
				{
					text.append("<td align=\"left\" style=\"color: #00FF00\">");
					text.append("<strong>O</strong>");
				}
				else if(testcases[i].isPerformed() && !testcases[i].isSucceeded())
				{
					text.append("<td align=\"left\" style=\"color: #FF0000\">");
					text.append("<strong>X</strong>");
				}
				else // test in progress
				{
					text.append("<td align=\"left\" style=\"color: #444444\">");
					text.append("<strong>?</strong>");					
				}
				text.append("</td>\n");
			}
			
			// When suite is aborted show '?' for skipped tests.
			else if(suite.isAborted())
			{
				text.append("<td align=\"left\" style=\"color: #444444\">");
				text.append("<strong>?</strong>");					
				text.append("</td>\n");
			}
			
			// When suite is still running show empty column for not yet executed tests.
			else
			{
				text.append("<td align=\"left\">&nbsp;</td>\n");				
			}
			
			// Duration column.
			if(testcases[i]!=null && testcases[i].isPerformed())
			{
				text.append("<td>");
				text.append(SUtil.getDurationHMS(testcases[i].getDuration()));
				text.append("</td>\n");
			}
			else
			{
				text.append("<td>&nbsp;</td>\n");
			}

			// Empty columns consuming extra space. Hack!!!
			text.append("<td>&nbsp;</td>\n");
			text.append("<td>&nbsp;</td>\n");
			text.append("<td>&nbsp;</td>\n");
			text.append("<td>&nbsp;</td>\n");
			text.append("<td>&nbsp;</td>\n");
			text.append("<td>&nbsp;</td>\n");
			text.append("<td>&nbsp;</td>\n");
			text.append("<td>&nbsp;</td>\n");
			
			text.append("</tr>\n");
		}
		text.append("</table>\n");

		// Details of test cases.
		for(int i=0; i<testcases.length; i++)
		{
			if(testcases[i]!=null && testcases[i].isPerformed())
			{
				text.append("<p>\n<a name=\"");
				text.append(names[i]);
				text.append(i);
				text.append("\"></a>\n");
				text.append(testcases[i].getHTMLFragment(i+1));
				text.append("<a href=\"#top\">Back to top.</a>");
			}
		}

		return text.toString();
	}
	
	/**
	 *  Update the detail panel with the given testcases.
	 */
	protected void updateDetails()
	{
		this.report = testsuite!=null ? generateReport(testsuite) : "";
		try
		{
//			SwingUtilities.invokeAndWait(new Runnable()
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					final Point pos = ((JViewport)details.getParent())
							.getViewPosition();
//					System.out.println("Pos: " + pos);
					details.setText(report);
					details.repaint();
//					details.setCaretPosition(0);
					((JViewport)details.getParent()).setViewPosition(pos);

					// Hack!!! Reset position after freeing the event thread,
					//  something seems to destroy the position, grrr.
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							((JViewport)details.getParent()).setViewPosition(pos);
						}
					});

//					JScrollPane	scroll	= (JScrollPane)details.getParent().getParent();
//					int	h	= scroll.getHorizontalScrollBar().getValue();
//					int	v	= scroll.getVerticalScrollBar().getValue();
//					System.out.println("Pos: h="+h+", v="+v);
//					details.setText(text.toString());
//					details.setCaretPosition(0);
//					scroll.getHorizontalScrollBar().setValue(h);
//					scroll.getVerticalScrollBar().setValue(v);
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Get the list of tests.
	 *  @return The list.
	 */
	public EditableList getTestList()
	{
		return teststable;
	}

	/**
	 *  Reset the panel to an initial state.
	 */
	public void reset()
	{
		if(testsuite!=null && testsuite.isRunning())
		{
			testsuite.abort();
		}
			
		testsuite	= null;
		updateProgress();
		updateDetails();
		
		teststable.setEntries(new String[0]);
		teststable.setAllowDuplicates(false);
		allowduplicates.setSelected(false);
		
		// tfto.setText(???); // Can't reset as value from belief is overwritten.
	}

	/**
	 *  Test if duplicates are allowed.
	 *  @return True if allowed.
	 */
	public boolean allowDuplicates()
	{
		return teststable.isAllowDuplicates();
	}
	
	/**
	 *  Extract the timeout value taken from the textfield. 
	 */
	protected void extractTimeoutValue(String text)
	{
		try
		{
			Integer to = new Integer(text);
			plugin.getJCC().getAgent().getBeliefbase().getBelief("testcase_timeout").setFact(to);
		}
		catch(final Exception e)
		{
			//e.printStackTrace();
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					String msg = SUtil.wrapText("No integer timeout: "+e.getMessage());
					JOptionPane.showMessageDialog(SGUI.getWindowParent(TestCenterPanel.this),
						msg, "Settings problem", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
	}

	/**
	 *  Extract the concurrency value taken from the combo box. 
	 */
	protected void extractConcurrencyValue(String text)
	{
		
		if(text.equals("all"))
		{
			concurrency	= -1;
		}
		else
		{
			try
			{
				concurrency	= Integer.parseInt(text);
			}
			catch(final Exception e)
			{
				//e.printStackTrace();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						String msg = SUtil.wrapText("No integer concurrency: "+e.getMessage());
						JOptionPane.showMessageDialog(SGUI.getWindowParent(TestCenterPanel.this),
							msg, "Settings problem", JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
			
			if(concurrency<=0)
			{
				concurrency	= 1;
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						String msg = SUtil.wrapText("Concurrency must be greater zero.");
						JOptionPane.showMessageDialog(SGUI.getWindowParent(TestCenterPanel.this),
							msg, "Settings problem", JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}
		}
	}

	/**
	 *
	 */
	public static void main(String[] args)
	{
		TestCenterPanel p = new TestCenterPanel(null);
		JFrame f = new JFrame();
		f.add("Center", p);
		f.pack();
		f.setVisible(true);

		p.teststable.setEntries(new String[]{"a","b","c"});
	}

	//-------- helper classes --------
	
	/**
	 *  Object for controlling test suite execution.
	 */
	public class TestSuite	implements IGoalListener
	{
		//-------- attributes --------
		
		/** The names of the testcases. */
		protected String[]	names;
		
		/** The results of the testcases. */
		protected Testcase[]	testcases;
		
		/** A set of active goals (to be dropped on abort). */
		protected Set	goals;
		
		/** Flag indicating that the test suite has been aborted. */
		protected boolean	aborted;
		
		/** The timepoint when the test execution was started. */
		protected long	starttime;
		
		//-------- constructors --------
		
		/**
		 *  Create a new test suite for the given test cases.
		 */
		public TestSuite(String[] names)
		{
			this.names	= names;
			this.testcases	= new Testcase[names.length];
			this.goals	= new HashSet();
		}
		
		//-------- methods --------
		
		/**
		 *  Check if the test suite is running.
		 */
		public boolean	isRunning()
		{
			return !goals.isEmpty();
		}
		
		/**
		 *  Check if the test suite has been aborted.
		 */
		public boolean	isAborted()
		{
			return aborted;
		}

		/**
		 *  Get the testcase names array.
		 */
		public String[]	getTestcaseNames()
		{
			return names;
		}

		/**
		 *  Get the testcase array.
		 *  Same size as names array, but some entries may be null
		 *  if not yet performed.
		 */
		public Testcase[]	getTestcases()
		{
			return testcases;
		}

		/**
		 *  Get the start time.
		 */
		public long	getStartTime()
		{
			return starttime;
		}

		/**
		 *  Start the execution of the test suite.
		 */
		public void	start()
		{
			this.starttime	= System.currentTimeMillis();
			startNextTestcases();
			updateProgress();
			updateDetails();
		}

		/**
		 *  Abort the execution of the test suite.
		 */
		public void	abort()
		{
			this.aborted	= true;
			
			for(Iterator it=goals.iterator(); it.hasNext(); )
			{
				IGoal	goal	= (IGoal)it.next(); 
				goal.removeGoalListener(TestSuite.this);
				goal.drop();
			}
			
			goals.clear();
			updateProgress();
			updateDetails();			
		}
		
		//-------- helper methods --------
		
		/**
		 *  Start the next testcases (if any).
		 */
		protected void	startNextTestcases()
		{
			for(int i=0; i<testcases.length && (concurrency==-1 || goals.size()<concurrency); i++)
			{
				if(testcases[i]==null)
				{
					// Create testcase and dispatch goal.
					testcases[i]	= new Testcase(names[i]);
					IGoal pt = plugin.getJCC().getAgent().createGoal("perform_test");
					pt.getParameter("testcase").setValue(testcases[i]);
//					pt.getParameter("classloader").setValue(plugin.getModelExplorer().getClassLoader());
					pt.addGoalListener(this);
					goals.add(pt);
					plugin.getJCC().getAgent().dispatchTopLevelGoal(pt);
					plugin.getJCC().setStatusText("Performing test "+names[i]);
				}
			}			
		}

		/**
		 *  Called when a test goal has finished.
		 */
		public void goalFinished(final AgentEvent ae)
		{			
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					// Handling of finished goal.
					IGoal	goal	= (IGoal)ae.getSource();
					
					// Ignore if goal is leftover from aborted execution.
					if(goals.remove(goal))
					{
						goal.removeGoalListener(TestSuite.this);
						if(!goal.isSucceeded() && !aborted)
						{
							String text = SUtil.wrapText("Testcase error: "+goal.getException().getMessage());
							JOptionPane.showMessageDialog(SGUI.getWindowParent(TestCenterPanel.this),
								text, "Testcase problem", JOptionPane.INFORMATION_MESSAGE);
						}
						
//						System.out.println("Goal finished: "+goal);
						startNextTestcases();
						updateProgress();
						updateDetails();
					}
				}
			});
		}
		
		/**
		 *  Called when a test goal was added.
		 *  Currently ignored.
		 */
		public void goalAdded(AgentEvent ae)
		{
		}
	}
}

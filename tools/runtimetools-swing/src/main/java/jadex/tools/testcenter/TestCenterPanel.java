package jadex.tools.testcenter;

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
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import jadex.base.SRemoteGui;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IRemoteResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.BrowserPane;
import jadex.commons.gui.EditableList;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ScrollablePanel;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.xml.PropertiesXMLHelper;

/**
 *  The test center panel for running tests and viewing the results.
 */

// todo: fix allowduplicates to work of the 'tests' tuple list and not on the filename strings

public class TestCenterPanel extends JSplitPanel
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
	
	/** The testcase timeout. */
	protected long	timeout;
	
	/** The list of tests. */
	protected List<Tuple2<String, IResourceIdentifier>> tests;
	
	/** File chooser for loading saving testsuites. */
	protected JFileChooser loadsavechooser;
	
	//-------- constructors --------

	/**
	 *  Create a new test center panel.
	 */
	public TestCenterPanel(final TestCenterPlugin plugin)
	{
		this.plugin = plugin;
		this.concurrency	= 1;
		this.tests = new ArrayList<Tuple2<String, IResourceIdentifier>>();
		this.setResizeWeight(0.5);
	
		JPanel testcases = new ScrollablePanel(null, false, true);
		testcases.setLayout(new GridBagLayout());
		testcases.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Test suite settings "));
		
		this.teststable = new EditableList("Test cases", true);
		teststable.getModel().addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
//				System.out.println("table: "+e);
				if(e.getType()==TableModelEvent.DELETE)
				{
					int row = e.getFirstRow();
					if(row!=-1)
						tests.remove(row);
				}
			}
		});
		
		JScrollPane	scroll	= new JScrollPane(teststable);
		teststable.setPreferredScrollableViewportSize(new Dimension(400, 200)); // todo: hack
		tfto = new JTextField("", 6);
		tfto.setMinimumSize(tfto.getPreferredSize());
		tfto.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				setTimeout(tfto.getText());
			}
		});
		tfto.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent fe)
			{
				setTimeout(tfto.getText());
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

		testcases.add(load, new GridBagConstraints(3,2,1,2,0,0,GridBagConstraints.SOUTH,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		testcases.add(save, new GridBagConstraints(4,2,1,2,0,0,GridBagConstraints.SOUTH,
			GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
		testcases.add(clear, new GridBagConstraints(5,2,1,2,0,0,GridBagConstraints.SOUTH,
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
		SGUI.adjustComponentSizes(new JButton[]{load, save, clear, startabort, savereport, new JButton("Abort")});
		progress.setPreferredSize(new Dimension(progress.getPreferredSize().width, load.getPreferredSize().height));

		save.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(loadsavechooser==null)
				{
					loadsavechooser = new JFileChooser(".");
					loadsavechooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter()
					{
						public String getDescription()
						{
							return "Testcases (*.tests)";
						}
					
						public boolean accept(File f)
						{
							return f.isDirectory() || f.getName().endsWith(FILEEXTENSION_TESTS);
						}
					});
				}
				
				if(loadsavechooser.showDialog(SGUI.getWindowParent(TestCenterPanel.this)
					, "Save")==JFileChooser.APPROVE_OPTION)
				{
//					SServiceProvider.getService(plugin.getJCC().getJCCAccess().getServiceProvider(),
//						ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener(TestCenterPanel.this)
//					{
//						public void customResultAvailable(Object result)
					plugin.getJCC().getClassLoader(null).addResultListener(new SwingDefaultResultListener<ClassLoader>(TestCenterPanel.this)
					{
						public void customResultAvailable(final ClassLoader cl)
						{
							File file = loadsavechooser.getSelectedFile();
							if(file!=null)
							{
								if(!file.getName().endsWith(FILEEXTENSION_TESTS))
								{
									file = new File(file.getParentFile(), file.getName()+FILEEXTENSION_TESTS);
									loadsavechooser.setSelectedFile(file);
								}
								final File	f	= file;
								getProperties().addResultListener(new SwingDefaultResultListener<Properties>()
								{
									public void customResultAvailable(Properties result)
									{
										try
										{
											FileWriter fos = new FileWriter(f);
											fos.write(PropertiesXMLHelper.write(result, getClass().getClassLoader()));
											fos.close();
										}
										catch(Exception e)
										{
											customExceptionOccurred(e);
										}
									}
								});
							}
						}
					});
				}
			}
		});

		load.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(loadsavechooser==null)
				{
					loadsavechooser = new JFileChooser(".");
					loadsavechooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter()
					{
						public String getDescription()
						{
							return "Testcases (*.tests)";
						}
					
						public boolean accept(File f)
						{
							return f.isDirectory() || f.getName().endsWith(FILEEXTENSION_TESTS);
						}
					});
				}
				
				if(loadsavechooser.showDialog(SGUI.getWindowParent(TestCenterPanel.this)
					, "Load")==JFileChooser.APPROVE_OPTION)
				{
//					SServiceProvider.getServiceUpwards(plugin.getJCC().getJCCAccess().getServiceProvider(),
//						ILibraryService.class).addResultListener(new SwingDefaultResultListener(TestCenterPanel.this)
//					{
//						public void customResultAvailable(Object result) 
						
					plugin.getJCC().getClassLoader(null).addResultListener(new SwingDefaultResultListener<ClassLoader>(TestCenterPanel.this)
					{
						public void customResultAvailable(final ClassLoader cl)
						{
							File file = loadsavechooser.getSelectedFile();
							if(file!=null)
							{
								FileInputStream fis = null;
								try
								{
									fis = new FileInputStream(file);
									setProperties((Properties)PropertiesXMLHelper.read(fis, getClass().getClassLoader()));
								}
								catch(Exception e)
								{
								}
								finally
								{
									if(fis!=null)
									{
										try
										{
											fis.close();
										}
										catch(Exception e)
										{
										}
									}
								}
							}
						};
					});
				}
			}
		});

		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				clearTests();
			}
		});

		startabort.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				if(testsuite==null || !testsuite.isRunning())
				{
//					testsuite	= new TestSuite(teststable.getEntries());
					testsuite	= new TestSuite((Tuple2<String, IResourceIdentifier>[])tests.toArray(new Tuple2[0]));
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
			JFileChooser saverepchooser;
			public void actionPerformed(ActionEvent ev)
			{
				if(saverepchooser==null)
				{
					saverepchooser = new JFileChooser(".");
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
				}
				
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
		JScrollPane	scroll2	= new JScrollPane(details);
		bottom.add(BorderLayout.CENTER, scroll2);

		setOrientation(JSplitPane.VERTICAL_SPLIT);
		setOneTouchExpandable(true);
		setDividerLocation(0.7);
		setResizeWeight(0.7);
		setTopComponent(top);
		setBottomComponent(bottom);
		
		reset();
	}

	/**
	 * Load the properties.
	 */
	public IFuture<Void>	setProperties(Properties props)
	{
		final Future<Void>	ret	= new Future<Void>();
		// Load settings into fresh state.
		reset();
		
		teststable.setAllowDuplicates(props.getBooleanProperty("allowduplicates"));
		allowduplicates.setSelected(props.getBooleanProperty("allowduplicates"));

		String timeout;
		if(props.getProperty("timeout")!=null)
		{
			timeout	= props.getStringProperty("timeout");
		}
		else
		{
			timeout	= "20000";
		}
		setTimeout(timeout);
		tfto.setText(timeout);

		if(props.getProperty("concurrency")!=null)
		{
			concurrency  = props.getIntProperty("concurrency");
			if(concurrency==-1)
				tfpar.getModel().setSelectedItem("all");
			else
				tfpar.getModel().setSelectedItem(""+concurrency);
		}

		Properties[]	entries	= props.getSubproperties("entry");
		final CollectionResultListener<Tuple2<String, IResourceIdentifier>>	crl	=
			new CollectionResultListener<Tuple2<String, IResourceIdentifier>>(entries.length, false,
			new SwingExceptionDelegationResultListener<Collection<Tuple2<String,IResourceIdentifier>>, Void>(ret)
		{
			public void customResultAvailable(Collection<Tuple2<String, IResourceIdentifier>> result)
			{
				for(Tuple2<String, IResourceIdentifier> tup: result)
				{
					addTest(tup.getFirstEntity(), tup.getSecondEntity());
				}
				ret.setResult(null);
			}
		});
		for(int i=0; i<entries.length; i++)
		{
			final String	name	= entries[i].getStringProperty("model");
			String	ridurl	= entries[i].getStringProperty("ridurl");
			String	globalrid	= entries[i].getStringProperty("globalrid");
			SRemoteGui.createResourceIdentifier(plugin.getJCC().getPlatformAccess(), ridurl, globalrid)
				.addResultListener(new ExceptionDelegationResultListener<IResourceIdentifier, Void>(ret)
			{
				public void customResultAvailable(IResourceIdentifier result)
				{
					crl.resultAvailable(new Tuple2<String, IResourceIdentifier>(name, result));
				}
			});
		}
		
		return ret;
	}

	/**
	 *  Get the testcase names.
	 */
	protected String[] getTestNames()
	{
		final String[]	ret	= new String[tests.size()];
		for(int i=0; i<tests.size(); i++)
		{
			ret[i] = tests.get(i).getFirstEntity();
		}
		return ret;
	}
	
	/**
	 * Save the properties.
	 */
	public IFuture<Properties>	getProperties()
	{
		final Future<Properties> ret	= new Future<Properties>();

		CollectionResultListener<Tuple2<String, String>>	crl	= new CollectionResultListener<Tuple2<String,String>>(tests.size(), false,
			new SwingExceptionDelegationResultListener<Collection<Tuple2<String, String>>, Properties>(ret)
		{
			public void customResultAvailable(Collection<Tuple2<String, String>> result)
			{
				Properties	props	= new Properties();
				Iterator<Tuple2<String, String>>	it	= result.iterator();
				for(int i=0; i<tests.size(); i++)
				{
					Tuple2<String, String>	local	= it.next();
					Properties	entry	= new Properties();
					entry.addProperty(new Property("model", local.getFirstEntity()));
					entry.addProperty(new Property("ridurl", local.getSecondEntity()));

					// todo: save also repo info of gid
					IResourceIdentifier rid = tests.get(i).getSecondEntity();
					String id = rid!=null && rid.getGlobalIdentifier()!=null && rid.getGlobalIdentifier().getResourceId()!=null
						&& !ResourceIdentifier.isHashGid(rid) ? rid.getGlobalIdentifier().getResourceId(): null;
					entry.addProperty(new Property("globalrid", id));
					props.addSubproperties("entry", entry);
				}
				props.addProperty(new Property("timeout", tfto.getText()));
				props.addProperty(new Property("concurrency", ""+concurrency));
				props.addProperty(new Property("allowduplicates", ""+allowduplicates.isSelected()));
				ret.setResult(props);
			}
		});
		for(int i=0; i<tests.size(); i++)
		{
			SRemoteGui.localizeModel(plugin.getJCC().getPlatformAccess(), tests.get(i).getFirstEntity(), tests.get(i).getSecondEntity())
				.addResultListener(crl);
		}
		
		return ret;
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
		Tuple2<String, IResourceIdentifier>[]	names	= suite.getTestcaseNames();
		Testcase[]	testcases	= suite.getTestcases();
		int performed	= 0;
//		int failed	= 0;
		for(int i=0; i<testcases.length; i++)
		{
			if(testcases[i]!=null && testcases[i].isPerformed())
			{
				performed++;
//				if(!testcases[i].isSucceeded())
//				{
//					failed++;
//				}
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
			text.append("&nbsp;</strong></td>\n");
			
			// Name column (with link for already performed testcases).
			if(testcases[i]!=null && testcases[i].isPerformed())
			{
				text.append("<td><a href=\"#");
				text.append(names[i].getFirstEntity());
				text.append(i);
				text.append("\">");
				text.append(names[i].getFirstEntity());
				text.append("</a></td>\n");
			}
			else
			{
				text.append("<td>");
				text.append(names[i].getFirstEntity());
				text.append("</td>\n");
			}
			
			// Column for success state.
			if(testcases[i]!=null)
			{
				if(testcases[i].isPerformed() && testcases[i].isSucceeded())
				{
					text.append("<td align=\"left\" style=\"color: #00FF00\">");
					text.append("<strong>O&nbsp;</strong>");
				}
				else if(testcases[i].isPerformed() && !testcases[i].isSucceeded())
				{
					text.append("<td align=\"left\" style=\"color: #FF0000\">");
					text.append("<strong>X&nbsp;</strong>");
				}
				else // test in progress
				{
					text.append("<td align=\"left\" style=\"color: #444444\">");
					text.append("<strong>?&nbsp;</strong>");					
				}
				text.append("</td>\n");
			}
			
			// When suite is aborted show '?' for skipped tests.
			else if(suite.isAborted())
			{
				text.append("<td align=\"left\" style=\"color: #444444\">");
				text.append("<strong>?&nbsp;</strong>");					
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

			text.append("</tr>\n");
		}
		text.append("</table>\n");

		// Details of test cases.
		for(int i=0; i<testcases.length; i++)
		{
			if(testcases[i]!=null && testcases[i].isPerformed())
			{
				text.append("<p>\n<a name=\"");
				text.append(names[i].getFirstEntity());
				text.append(i);
				text.append("\"></a>\n");
				text.append(testcases[i].getHTMLFragment(i+1, names[i].getFirstEntity()));
				text.append("<a href=\"#top\">Back to top.</a> &nbsp;\n");
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
	
//	/**
//	 *  Get the list of tests.
//	 *  @return The list.
//	 */
//	public EditableList getTestList()
//	{
//		return teststable;
//	}
	
	/**
	 *  Add a test.
	 */
	public void addTest(String model, final IResourceIdentifier rid)
	{
		SRemoteGui.localizeModel(plugin.getJCC().getPlatformAccess(), model, rid)
			.addResultListener(new SwingDefaultResultListener<Tuple2<String,String>>()
		{
			public void customResultAvailable(Tuple2<String, String> result)
			{				
				tests.add(new Tuple2<String, IResourceIdentifier>(result.getFirstEntity(), rid));
				teststable.addEntry(result.getFirstEntity());
			}
		});
	}
	
	/**
	 *  Remove a test.
	 */
	public void removeTest(String model, IResourceIdentifier rid)
	{
		tests.remove(new Tuple2<String, IResourceIdentifier>(model, rid));
		teststable.removeEntry(model);
	}
	
	/**
	 * 
	 */
	public void clearTests()
	{
		tests.clear();
		teststable.setEntries(new String[0]);
	}
	
	/**
	 * 
	 */
	public void setTests(List<Tuple2<String, IResourceIdentifier>> tests)
	{
		this.tests = tests;
		teststable.setEntries(getTestNames());
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
		
		clearTests();
//		teststable.setEntries(new String[0]);
		teststable.setAllowDuplicates(false);
		allowduplicates.setSelected(false);
		
		tfto.setText("20000");
		setTimeout("20000");
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
	protected void setTimeout(String text)
	{
		try
		{
			this.timeout	= Long.parseLong(text);
		}
		catch(Exception e)
		{
			showTimoutValueWarning(e);
		}
	}
	
	/**
	 *  Show a warning message that a wrong timeout value was entered.
	 */
	protected void	showTimoutValueWarning(final Exception e)
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

//		p.teststable.setEntries(new String[]{"a","b","c"});
	}

	//-------- helper classes --------
	
	/**
	 *  Object for controlling test suite execution.
	 */
	public class TestSuite
	{
		//-------- attributes --------
		
		/** The names of the testcases. */
		protected Tuple2<String, IResourceIdentifier>[]	names;
		
		/** The results of the testcases. */
		protected Testcase[]	results;
		
		/** A set of running testcases to be destroyed on abort (name->cid). */
		protected Map<Tuple2<String, IResourceIdentifier>, IComponentIdentifier>	testcases;
		
		/** Flag indicating that the test suite is running. */
		protected boolean	running;
		
		/** Flag indicating that the test suite has been aborted. */
		protected boolean	aborted;
		
		/** The timepoint when the test execution was started. */
		protected long	starttime;
		
		//-------- constructors --------
		
		/**
		 *  Create a new test suite for the given test cases.
		 */
		public TestSuite(Tuple2<String, IResourceIdentifier>[] names)
		{
			this.names	= names;
			this.results	= new Testcase[names.length];
			this.testcases	= new HashMap<Tuple2<String, IResourceIdentifier>, IComponentIdentifier>();
			this.running	= false;
		}
		
		//-------- methods --------
		
		/**
		 *  Check if the test suite is running.
		 */
		public boolean	isRunning()
		{
			return running;
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
		public Tuple2<String, IResourceIdentifier>[]	getTestcaseNames()
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
			return results;
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
			this.aborted	= false;
			startNextTestcases();
		}

		/**
		 *  Abort the execution of the test suite.
		 */
		public void	abort()
		{
			assert SwingUtilities.isEventDispatchThread();
//			System.out.println("abort: "+testcases.keySet());
			this.aborted	= true;
			
			CounterResultListener<Void>	crl	= new CounterResultListener<Void>(testcases.size(), new SwingDefaultResultListener<Void>(TestCenterPanel.this)
			{
				public void customResultAvailable(Void result)
				{
//					System.out.println("aborted: "+testcases.keySet());
//					testcases.clear();
					updateProgress();
					updateDetails();
				}
				public void customExceptionOccurred(Exception exception)
				{
//					System.out.println("aborted: "+exception);
					super.customExceptionOccurred(exception);
					customResultAvailable(null);
				}
			});
			for(Iterator<IComponentIdentifier> it=testcases.values().iterator(); it.hasNext(); )
			{
				IComponentIdentifier	testcase	= it.next(); 
//				System.out.println("aborting: "+testcase);
				if(testcase!=null)
				{
					abortTestcase(testcase).addResultListener(crl);
				}
				else
				{
					crl.resultAvailable(null);
				}
			}
		}
		
		//-------- helper methods --------
		
		/**
		 *  Start the next testcases (if any).
		 */
		protected void	startNextTestcases()
		{
			assert SwingUtilities.isEventDispatchThread();
			
			// Start next open testcase as long as more testcases allowed.
			for(int i=0; !aborted && i<results.length && (concurrency==-1 || testcases.size()<concurrency); i++)
			{
				if(!testcases.containsKey(names[i]) && results[i]==null)
				{
					final Tuple2<String, IResourceIdentifier>	name	= names[i];
					testcases.put(name, null);
					
					final IResultListener	res	= new TestResultListener(name);
					
					plugin.getJCC().setStatusText("Performing test "+name);
					final Future	ret	= new Future();
				
					Map	args	= new HashMap();
					args.put("timeout", timeout);
					// Todo: Use remote component for parent if any
					CreationInfo ci = new CreationInfo(args, plugin.getJCC().getPlatformAccess().getId());
					ci.setResourceIdentifier(name.getSecondEntity());
					ci.setFilename(name.getFirstEntity());
					plugin.getJCC().getPlatformAccess().createComponent(null, ci, res)
						.addResultListener(new SwingDelegationResultListener(ret));
					
					// Todo: timeout -> force destroy of component
					ret.addResultListener(new SwingDefaultResultListener(TestCenterPanel.this)
					{
						public void customResultAvailable(Object result)
						{
							// Add testcase cid if not aborted in mean time.
							if(testcases.containsKey(name))
							{
								testcases.put(name, (IComponentIdentifier)result);
							}
							else
							{
								abortTestcase((IComponentIdentifier)result);
							}
							startNextTestcases();
							updateProgress();
							updateDetails();
						}
					});
				}
			}
			
			running	= !testcases.isEmpty();
//			System.out.println("running: "+running);
		}

		/**
		 *  Abort a testcase.
		 */
		protected IFuture<Void>	abortTestcase(final IComponentIdentifier testcase)
		{
			final Future<Void>	ret	= new Future<Void>();
			
			plugin.getJCC().getJCCAccess().searchService(
				new ServiceQuery<>(IComponentManagementService.class))
				.addResultListener(new SwingExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService cms)
				{
//					System.out.println("destroying: "+testcase);
					cms.destroyComponent(testcase)
						.addResultListener(new SwingExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
					{
						public void customResultAvailable(Map<String, Object> result)
						{
//							System.out.println("aborted: "+testcase);
							ret.setResult(null);
						}
					});
				}
			});
			
			return ret;
		}
		
		//-------- helper class --------
	
		/**
		 *  Callback result listener for (local or remote) test results.
		 */
		public class TestResultListener		implements IRemoteResultListener<Collection<Tuple2<String, Object>>>
		{
			//-------- attributes --------
			
			/** The testcase name. */
			protected Tuple2<String, IResourceIdentifier>	name;
			
			//-------- constructors --------
			
			/**
			 *  Create a test result listener
			 */
			public TestResultListener(Tuple2<String, IResourceIdentifier> name)
			{
				this.name	= name;
			}
			
			//-------- IResultListener interface --------
			
			/**
			 *  Exception during test execution.
			 */
			public void exceptionOccurred(Exception exception)
			{
				Testcase	res	= new Testcase(1, new TestReport[]{new TestReport("creation", "Test center report", exception)});
				testFinished(res);
			}
			
			/**
			 *  Result of test execution.
			 */
			public void resultAvailable(Collection<Tuple2<String, Object>> result)
			{
				Map<String, Object> resmap = null;
				Testcase res = null;
				if(result!=null)
				{
					resmap = new HashMap<String, Object>();
					for(Iterator<Tuple2<String, Object>> it=result.iterator(); it.hasNext(); )
					{
						Tuple2<String, Object> tup = it.next();
						resmap.put(tup.getFirstEntity(), tup.getSecondEntity());
					}
					res = (Testcase)resmap.get("testresults");
				}
//				Testcase	res	= (Testcase)((Map)result).get("testresults");
				if(res==null)
				{
					res	= new Testcase(1, new TestReport[]{new TestReport("#1", "Test execution",
						false, "Component did not produce a result.")});
				}
				testFinished(res);
			}
			
			/**
			 *  Cleanup after test is finished.
			 */
			protected void	testFinished(final Testcase result)
			{
				System.out.println("finished: "+result);
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
//						System.out.println("finished2: "+result);
						if(testcases.containsKey(name))
						{
							for(int i=0; i<names.length; i++)
							{
								if(name.equals(names[i]))
								{
									results[i]	= result;
								}
							}
							testcases.remove(name);
							startNextTestcases();
							updateProgress();
							updateDetails();
						}
					}
				});
			}
		}
	}
}

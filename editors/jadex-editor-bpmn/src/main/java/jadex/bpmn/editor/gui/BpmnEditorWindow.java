package jadex.bpmn.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.view.mxStylesheet;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.controllers.DeletionController;
import jadex.bpmn.editor.gui.controllers.SCreationController;
import jadex.bpmn.editor.gui.propertypanels.PropertyPanelFactory;
import jadex.bpmn.editor.model.legacy.BpmnXMLReader;
import jadex.bpmn.editor.model.visual.BpmnVisualModelGenerator;
import jadex.bpmn.editor.model.visual.BpmnVisualModelReader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.io.SBpmnModelReader;
import jadex.bridge.ResourceIdentifier;
import jadex.commons.ResourceInfo;
import jadex.commons.collection.OrderedProperties;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingResultListener;

/**
 * 
 */
public class BpmnEditorWindow extends JFrame
{
	public static final String JADEX_PANEL_CONFIG = "jadex/bpmn/editor/gui/propertypanels/jadexpanels.properties";
	
	/** The tool bar. */
	protected BpmnToolbar bpmntoolbar;
	
	/** The tab pane showing the models. */
	protected JTabbedPane tabpane;
	
	/** The global settings. */
	protected Settings settings;
	
	/** The status area. */
	protected StatusArea statusarea;
	
	/**
	 * 
	 */
	public BpmnEditorWindow()
	{
		super(BpmnEditor.APP_NAME);
		
		statusarea = new StatusArea();
		BpmnEditor.initialize();
		
		BackgroundProgressBar bgprogressbar = new BackgroundProgressBar();
		
		settings = Settings.load();
		
		OrderedProperties panelprops = new OrderedProperties();
		try
		{
			panelprops.load(getClass().getClassLoader().getResourceAsStream(BpmnEditorWindow.JADEX_PANEL_CONFIG));
		}
		catch (IOException e2)
		{
			e2.printStackTrace();
		}
		settings.setPropertyPanelFactory(new PropertyPanelFactory(panelprops));
		
		LookAndFeelInfo lfinfo = BpmnEditor.LOOK_AND_FEELS.get(settings.getLfName());
		if (lfinfo != null)
		{
			try
			{
				UIManager.setLookAndFeel(lfinfo.getClassName());
			}
			catch (Exception e1)
			{
			}
		}
		else
		{
			settings.setLfName(UIManager.getLookAndFeel().getName());
		}
		
		settings.setProgressBar(bgprogressbar);
		
		if(settings.getGlobalInterfaces()==null || settings.getGlobalInterfaces().size()==0)// || true)
		{
			Logger.getLogger(BpmnEditor.APP_NAME).log(Level.INFO, "Scanning classes start...");
			final long start = System.currentTimeMillis();
			settings.scanForClasses().addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					long needed = System.currentTimeMillis()-start;
					Logger.getLogger(BpmnEditor.APP_NAME).log(Level.INFO, "... scanning classes end, needed: "+needed/1000+" secs");
					for (ModelContainer container : getModelContainers())
					{
						container.generateClassLoader();
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
				}
			}));
		}
		
		getContentPane().setLayout(new BorderLayout());
		
		bpmntoolbar = new BpmnToolbar(settings);
		//bpmntoolbar.getInfoPanel().setLayout(new BoxLayout(bpmntoolbar.getInfoPanel(), BoxLayout.LINE_AXIS));
		getContentPane().add(bpmntoolbar, BorderLayout.PAGE_START);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		tabpane = new JTabbedPane();
		getContentPane().add(tabpane, BorderLayout.CENTER);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				terminate();
			}
		});
		
		/* Menu */
		JMenuBar menubar = new BpmnMenuBar(BpmnEditorWindow.this);
		setJMenuBar(menubar);
		
		JToolBar statusbar = new JToolBar();
		statusbar.setLayout(new BoxLayout(statusbar, BoxLayout.LINE_AXIS));
		statusbar.setFloatable(false);
		add(statusbar, BorderLayout.PAGE_END);
		
		JButton refreshbutton = new JButton(new AbstractAction()
		{
			
			public void actionPerformed(ActionEvent e)
			{
				getSettings().scanForClasses().addResultListener(new SwingResultListener<Void>(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						for (ModelContainer container : getModelContainers())
						{
							container.generateClassLoader();
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				}));
			}
		});
		Icon[] icons = settings.getImageProvider().generateGenericFlatImageIconSet(16, ImageProvider.EMPTY_FRAME_TYPE, "refresh", Color.BLACK);
		refreshbutton.setIcon(icons[0]);
		refreshbutton.setPressedIcon(icons[1]);
		refreshbutton.setRolloverIcon(icons[2]);
		refreshbutton.setContentAreaFilled(false);
		refreshbutton.setBorder(new EmptyBorder(0, 0, 0, 0));
		refreshbutton.setMargin(new Insets(0, 0, 0, 0));
		refreshbutton.setToolTipText(BpmnEditor.getString("Refresh Classes"));
		statusbar.add(refreshbutton);
		
		statusbar.add(bgprogressbar);
//		statusbar.add(Box.createHorizontalGlue());
		ZoomSlider zs = new ZoomSlider(this);
		zs.setMaximumSize(zs.getPreferredSize());
		statusbar.add(zs);
		statusbar.doLayout();
		
		
		pack();
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int) (sd.width * GuiConstants.GRAPH_PROPERTY_RATIO),
				(int) (sd.height * GuiConstants.GRAPH_PROPERTY_RATIO));
		setLocationRelativeTo(null);
		
		// Buggy Swing Bugness
		addWindowListener(new WindowAdapter()
		{
			public void windowOpened(WindowEvent e)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						File[] openedfiles = settings.getOpenedFiles();
						if (openedfiles != null && openedfiles.length > 0)
						{
							for (File file : openedfiles)
							{
								try
								{
//									long ts = System.currentTimeMillis();
//									SBpmnModelReader.readModel(file, null);
//									System.out.println(file.toString() + " " + (System.currentTimeMillis() - ts));
//									ts = System.currentTimeMillis();
									loadModel(file);
//									System.out.println(file.toString() + " " + (System.currentTimeMillis() - ts));
								}
								catch(Exception e)
								{
								}
							}
						}
						
						if (tabpane.getTabCount() < 1)
						{
							initializeNewModel(newModelTab(null));
						}
						
						getSelectedModelContainer().getGraphComponent().requestFocusInWindow();
					}
				});
				removeWindowListener(this);
			}
		});
		
		setVisible(true);
		getContentPane().doLayout();
//		statuspane.setDividerLocation(1.0);
//		statuspane.repaint();
	}
	
	/**
	 *  Gets model containers.
	 *  
	 *  @return The model containers.
	 */
	public List<ModelContainer> getModelContainers()
	{
		List<ModelContainer> ret = new ArrayList<ModelContainer>();
		for (int i = 0; i < tabpane.getTabCount(); ++i)
		{
			BpmnEditorPanel panel = (BpmnEditorPanel) tabpane.getComponentAt(i);
			ret.add(panel.getModelContainer());
		}
		return ret;
	}
	
	/**
	 *  Gets the current model container.
	 *  
	 *  @return The selected model container (may be null if none is selected).
	 */
	public ModelContainer getSelectedModelContainer()
	{
		ModelContainer ret = null;
		
		BpmnEditorPanel panel = (BpmnEditorPanel) tabpane.getSelectedComponent();
		if (panel != null)
		{
			ret = panel.getModelContainer();
		}
		
		return ret;
	}

	/**
	 *  Gets the settings.
	 *
	 *  @return The settings.
	 */
	public Settings getSettings()
	{
		return settings;
	}
	
	/**
	 *  Gets the status area.
	 *
	 *  @return The status area.
	 */
	public StatusArea getStatusArea()
	{
		return statusarea;
	}

	/**
	 *  Adds a listener to the tab pane.
	 *  
	 *  @param listener The listener.
	 */
	public void addTabListener(ChangeListener listener)
	{
		tabpane.addChangeListener(listener);
	}
	
	/**
	 *  Removes a listener to the tab pane.
	 *  
	 *  @param listener The listener.
	 */
	public void removeTabListener(ChangeListener listener)
	{
		tabpane.removeChangeListener(listener);
	}
	
	/**
	 *  Creates new model tab.
	 */
	public ModelContainer newModelTab(ModelContainer modelcontainer)
	{
		boolean createpool = false;
		if (modelcontainer == null)
		{
			modelcontainer = new ModelContainer(settings);
			createpool = true;
		}
		modelcontainer.setEditingToolbar(bpmntoolbar);
		
		if (modelcontainer.getGraph() == null)
		{
			mxStylesheet sheet = null;
			for (int i = 0; i < BpmnEditor.STYLE_SHEETS.size(); ++i)
			{
				sheet = BpmnEditor.STYLE_SHEETS.get(i).getSecondEntity();
				if (BpmnEditor.STYLE_SHEETS.get(i).getFirstEntity().equals(settings.getSelectedSheet()))
				{
					break;
				}
			}
			
			modelcontainer.setGraph(new BpmnGraph(modelcontainer, sheet));
		}
		modelcontainer.getGraph().getView().setScale(GuiConstants.DEFAULT_ZOOM);
		
		if (modelcontainer.getBpmnModel() == null)
		{
			modelcontainer.setBpmnModel(new MBpmnModel());
		}
		
		String tabname = "Unnamed Model";
		if (modelcontainer.getFile() != null)
		{
			tabname = modelcontainer.getFile().getName();
		}
		
		modelcontainer.setDirty(false);
		
		int index = tabpane.getTabCount();
		final BpmnEditorPanel panel = new BpmnEditorPanel(modelcontainer);
		tabpane.addTab(tabname, null, panel, modelcontainer.getFile() != null? modelcontainer.getFile().getAbsolutePath() : "");
		final JPanel tabtitlecomponent = new JPanel()
		{
			/** The title label. */
			private JLabel tabtitle;
			
			/** The close button. */
			private JButton closebutton;
			
			{
				setOpaque(false);
				setBorder(new EmptyBorder(0, 0, 0, 0));
				
				tabtitle = new JLabel();
				tabtitle.setOpaque(false);
				
				closebutton = new JButton(new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (panel.getModelContainer().checkUnsaved(BpmnEditorWindow.this))
						{
							tabpane.remove(panel);
						}
					}
				});
				
				Icon[] icons = settings.getImageProvider().
						generateGenericFlatImageIconSet(16,
														ImageProvider.THIN_FRAME_TYPE,
														"invEVT_X",
														Color.DARK_GRAY,
														0.0f);
				
				closebutton.setMargin(new Insets(0, 0, 0, 0));
				closebutton.setBorder(new EmptyBorder(3, 2, 2, 2));
				closebutton.setIcon(icons[0]);
				closebutton.setPressedIcon(icons[1]);
				closebutton.setRolloverIcon(icons[2]);
				closebutton.setContentAreaFilled(false);
				
				setLayout(new GridBagLayout());
				
				GridBagConstraints g = new GridBagConstraints();
				g.fill = GridBagConstraints.HORIZONTAL;
				g.weightx = 1.0;
				g.ipadx = 10;
				add(tabtitle, g);
				
				g = new GridBagConstraints();
				g.fill = GridBagConstraints.NONE;
				g.gridx = 1;
				add(closebutton, g);
			}
			
			public void paint(Graphics g)
			{
				String title = "New Model";
				File file = panel.getModelContainer().getFile();
				if (file != null)
				{
					title = file.getName();
				}
				if (panel.getModelContainer().isDirty())
				{
					title += "*";
				}
				
				if (tabtitle != null)
				{
					tabtitle.setText(title);
				}
				
				super.paint(g);
			}
		};
		tabpane.setTabComponentAt(index, tabtitlecomponent);
		
		modelcontainer.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				tabtitlecomponent.repaint();
			}
		});
		
		tabpane.setSelectedIndex(index);
		
//		panel.getModelContainer().setPropertyPanel(SPropertyPanelFactory.createPanel(null, panel.getModelContainer()));
		panel.getModelContainer().setPropertyPanel(settings.getPropertyPanelFactory().createPanel(panel.getModelContainer(), null));
		panel.setDividerLocation(GuiConstants.GRAPH_PROPERTY_RATIO);
		
		// More Swing Bugness
		final Runnable fixsplitpane = new Runnable()
		{
			public void run()
			{
				final JSplitPane viewpane = (JSplitPane) tabpane.getSelectedComponent();
				if (viewpane != null)
				{
					viewpane.repaint();
					viewpane.revalidate();
					viewpane.setDividerLocation(GuiConstants.GRAPH_PROPERTY_RATIO);
					viewpane.doLayout();
					panel.doLayout();
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							viewpane.revalidate();
							viewpane.repaint();
						}
					});
				}
			}
		};
		SwingUtilities.invokeLater(fixsplitpane);
		
		ActionListener fixlistener =  new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fixsplitpane.run();
			}
		};
		
		for (int i = 1; i < 5; ++i)
		{
			javax.swing.Timer timer = new javax.swing.Timer(i * 250, fixlistener);
			timer.setRepeats(false);
			timer.start();
		}
		
		if (createpool)
		{
			SCreationController.createPool(modelcontainer, new Point(50, 50));
		}
		
		modelcontainer.generateClassLoader();
		
		return modelcontainer;
	}
	
	/**
	 *  Initializes a model (move to newModelTab()?)
	 *  
	 *  @param modelcontainer The model container.
	 */
	public void initializeNewModel(ModelContainer modelcontainer)
	{
//		modelcontainer.getGraph().getSelectionModel().addListener(mxEvent.CHANGE, new SelectionController(modelcontainer));
		modelcontainer.getGraphComponent().refresh();
		modelcontainer.setPropertyPanel(settings.getPropertyPanelFactory().createPanel(modelcontainer, null));
//		modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(null, modelcontainer));
		new DeletionController(modelcontainer);
	}
	
	public void loadModel(File file) throws Exception
	{
		if (!file.getName().endsWith(".bpmn2") &&
				!file.getName().endsWith(".bpmn"))
		{
			File tmpfile = new File(file.getAbsolutePath() + ".bpmn2");
			if (tmpfile.exists())
			{
				file = tmpfile;
			}
			else
			{
				file = new File(file.getAbsolutePath() + ".bpmn");
			}
		}
		
		ModelContainer modelcontainer = new ModelContainer(settings);
		mxStylesheet sheet = null;
		for (int i = 0; i < BpmnEditor.STYLE_SHEETS.size(); ++i)
		{
			sheet = BpmnEditor.STYLE_SHEETS.get(i).getSecondEntity();
			if (BpmnEditor.STYLE_SHEETS.get(i).getFirstEntity().equals(settings.getSelectedSheet()))
			{
				break;
			}
		}
		
		BpmnGraph graph = new BpmnGraph(modelcontainer, sheet);
		
		MBpmnModel mmodel = null;
		if (file.getName().endsWith("bpmn2"))
		{
			BpmnVisualModelReader vreader = new BpmnVisualModelReader(graph);
			graph.deactivate();
			graph.setEventsEnabled(false);
			graph.getModel().beginUpdate();
			mmodel = SBpmnModelReader.readModel(new FileInputStream(file), file.getPath(), vreader);
			graph.getModel().endUpdate();
			graph.setEventsEnabled(true);
			graph.activate();
		}
		else
		{
			ResourceInfo rinfo = new ResourceInfo(file.getAbsolutePath(), new FileInputStream(file), file.lastModified());
			mmodel = BpmnXMLReader.read(rinfo, BpmnMenuBar.class.getClassLoader(), new ResourceIdentifier(), null);
			(new BpmnVisualModelGenerator(mmodel)).generateModel(graph);
		}
		
		modelcontainer.setGraph(graph);
		modelcontainer.setBpmnModel(mmodel);
		
		modelcontainer.setFile(file);
		getSettings().setLastFile(file);
		
		
		newModelTab(modelcontainer);
		initializeNewModel(modelcontainer);
	}
	
	public void terminate()
	{
		boolean quit = true;
		List<File> openfiles = new ArrayList<File>();
		for (int i = 0; i < tabpane.getTabCount(); ++i)
		{
			BpmnEditorPanel panel = (BpmnEditorPanel) tabpane.getComponentAt(i);
			
			ModelContainer modelcontainer = panel.getModelContainer();
			if (modelcontainer != null && !modelcontainer.checkUnsaved(this))
			{
				quit = false;
				break;
			}
			
			if (modelcontainer.getFile() != null)
			{
				openfiles.add(modelcontainer.getFile());
			}
		}
		if (quit)
		{
			if (settings.isSaveSettingsOnExit())
			{
				try
				{
					settings.setOpenedFiles(openfiles.toArray(new File[openfiles.size()]));
					settings.save();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
//			trys
//			{
//				ImageProvider.getInstance().saveCache(BpmnEditor.HOME_DIR + File.separator + "imagecache.dat");
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
			
			dispose();
			System.exit(0);
		}
	}
	
	/** 
	 *  Get the tab pane.
	 *  
	 *  @return The tab pane
	 */
	public JTabbedPane getTabPane()
	{
		return tabpane;
	}
}

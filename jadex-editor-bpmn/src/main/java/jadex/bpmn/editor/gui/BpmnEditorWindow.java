package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.controllers.DeletionController;
import jadex.bpmn.editor.gui.controllers.SCreationController;
import jadex.bpmn.editor.gui.controllers.SelectionController;
import jadex.bpmn.editor.gui.propertypanels.SPropertyPanelFactory;
import jadex.bpmn.editor.model.legacy.BpmnXMLReader;
import jadex.bpmn.editor.model.visual.BpmnVisualModelGenerator;
import jadex.bpmn.editor.model.visual.BpmnVisualModelReader;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.io.SBpmnModelReader;
import jadex.bridge.ClassInfo;
import jadex.bridge.ResourceIdentifier;
import jadex.commons.ResourceInfo;
import jadex.commons.SReflect;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
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
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxStylesheet;

/**
 * 
 */
public class BpmnEditorWindow extends JFrame
{
	/** The tool bar. */
	protected BpmnToolbar bpmntoolbar;
	
	/** The tab pane showing the models. */
	protected JTabbedPane tabpane;
	
	/** The global cache. */
	protected GlobalCache globalcache;
	
	/** The global settings. */
	protected Settings settings;
	
	/**
	 * 
	 */
	public BpmnEditorWindow()
	{
		super(BpmnEditor.APP_NAME);
		
		settings = Settings.load();
		
		globalcache = new GlobalCache();
	
		Comparator<ClassInfo> comp = new Comparator<ClassInfo>()
		{
			public int compare(ClassInfo o1, ClassInfo o2)
			{
				String str1 = SReflect.getUnqualifiedTypeName(o1.toString());
				String str2 = SReflect.getUnqualifiedTypeName(o2.toString());
				return str1.compareTo(str2);
			}
		};
		
		if(settings.getGlobalInterfaces()==null || settings.getGlobalInterfaces().size()==0)// || true)
		{
			System.out.println("Scanning classes start...");
			long start = System.currentTimeMillis();
			List<ClassInfo>[] tmp = GlobalCache.scanForClasses(settings.getHomeClassLoader());
			globalcache.getGlobalTaskClasses().addAll(tmp[0]);
			globalcache.getGlobalInterfaces().addAll(tmp[1]);
			Collections.sort(globalcache.getGlobalTaskClasses(), comp);
			Collections.sort(globalcache.getGlobalInterfaces(), comp);
			settings.setGlobalTaskClasses(tmp[0]);
			settings.setGlobalInterfaces(tmp[1]);
			long needed = System.currentTimeMillis()-start;
			System.out.println("... scanning classes end, needed: "+needed/1000+" secs");
		}
		else
		{
			globalcache.getGlobalTaskClasses().clear();
			globalcache.getGlobalTaskClasses().addAll(settings.getGlobalTaskClasses());
			globalcache.getGlobalInterfaces().clear();
			globalcache.getGlobalInterfaces().addAll(settings.getGlobalInterfaces());
			Collections.sort(globalcache.getGlobalTaskClasses(), comp);
			Collections.sort(globalcache.getGlobalInterfaces(), comp);
		}
		
		getContentPane().setLayout(new BorderLayout());
		
//		final JSplitPanel statuspane = new JSplitPanel(JSplitPane.VERTICAL_SPLIT)
//		{
//			/* Bug fix goodness for Swing. */
//			@SuppressWarnings("deprecation")
//			public void reshape(int x, int y, int w, int h)
//			{
//				final double divloc = getProportionalDividerLocation();
//				super.reshape(x, y, w, h);
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						setDividerLocation(divloc);
//					}
//				});
//			}
//		};
//		statuspane.setOneTouchExpandable(true);
//		statuspane.setBottomComponent(new StatusArea());
		BpmnEditor.initialize();
//		getContentPane().add(statuspane, BorderLayout.CENTER);
		
//		statuspane.addComponentListener(new ComponentAdapter()
//		{
//			public void componentResized(ComponentEvent e)
//			{
//				JSplitPanel panel = (JSplitPanel) e.getSource();
//				System.out.println(panel.getProportionalDividerLocation());
//				panel.setDividerLocation(panel.getProportionalDividerLocation());
//			}
//		});
		
		bpmntoolbar = new BpmnToolbar(settings.getToolbarIconSize());
		//bpmntoolbar.getInfoPanel().setLayout(new BoxLayout(bpmntoolbar.getInfoPanel(), BoxLayout.LINE_AXIS));
		getContentPane().add(bpmntoolbar, BorderLayout.PAGE_START);
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		tabpane = new JTabbedPane();
//		{
//			public String getTitleAt(int index)
//			{
//				String ret = "New Model";
//				BpmnEditorPanel panel = (BpmnEditorPanel) getTabComponentAt(index);
//				if (panel != null)
//				{
//					File file = panel.getModelContainer().getFile();
//					if (file != null)
//					{
//						ret = file.getName();
//					}
//					if (panel.getModelContainer().isDirty())
//					{
//						ret += "*";
//					}
//				}
//				return ret;
//			}
//		};
//		statuspane.setTopComponent(tabpane);
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
		statusbar.setLayout(new GridBagLayout());
		statusbar.setFloatable(false);
		add(statusbar, BorderLayout.PAGE_END);
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1.0;
		statusbar.add(new JPanel(), g);
		g = new GridBagConstraints();
		g.gridx = 1;
		g.fill = GridBagConstraints.NONE;
		statusbar.add(new ZoomSlider(this), g);
		
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
//						statuspane.repaint();
//						statuspane.revalidate();
//						statuspane.setDividerLocation(statuspane.getHeight());
//						statuspane.setDividerLocation(1.0);
//						statuspane.repaint();
//						statuspane.revalidate();
						
						File[] openedfiles = settings.getOpenedFiles();
						if (openedfiles != null && openedfiles.length > 0)
						{
							for (File file : openedfiles)
							{
								try
								{
									loadModel(file);
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
	 *  Gets the global cache.
	 *
	 *  @return The global cache.
	 */
	public GlobalCache getGlobalCache()
	{
		return globalcache;
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
			modelcontainer = new ModelContainer(globalcache, settings);
			createpool = true;
		}
		modelcontainer.setEditingToolbar(bpmntoolbar);
		
		if (modelcontainer.getGraph() == null)
		{
			mxStylesheet sheet = null;
			for (int i = 0; i < BpmnEditor.STYLE_SHEETS.length; ++i)
			{
				sheet = BpmnEditor.STYLE_SHEETS[i].getSecondEntity();
				if (BpmnEditor.STYLE_SHEETS[i].getFirstEntity().equals(settings.getSelectedSheet()))
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
		
		tabpane.addTab(tabname, panel);
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
				
				Icon[] icons = ImageProvider.getInstance().
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
		
		panel.getModelContainer().setPropertyPanel(SPropertyPanelFactory.createPanel(null, panel.getModelContainer()));
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
		
		return modelcontainer;
	}
	
	/**
	 *  Initializes a model (move to newModelTab()?)
	 *  
	 *  @param modelcontainer The model container.
	 */
	public void initializeNewModel(ModelContainer modelcontainer)
	{
		modelcontainer.getGraph().getSelectionModel().addListener(mxEvent.CHANGE, new SelectionController(modelcontainer));
		modelcontainer.getGraphComponent().refresh();
		modelcontainer.setPropertyPanel(SPropertyPanelFactory.createPanel(null, modelcontainer));
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
		
		ModelContainer modelcontainer = new ModelContainer(globalcache, settings);
		mxStylesheet sheet = null;
		for (int i = 0; i < BpmnEditor.STYLE_SHEETS.length; ++i)
		{
			sheet = BpmnEditor.STYLE_SHEETS[i].getSecondEntity();
			if (BpmnEditor.STYLE_SHEETS[i].getFirstEntity().equals(settings.getSelectedSheet()))
			{
				break;
			}
		}
		
		BpmnGraph graph = new BpmnGraph(modelcontainer, sheet);
		
		MBpmnModel mmodel = null;
		if (file.getName().endsWith("bpmn2"))
		{
			BpmnVisualModelReader vreader = new BpmnVisualModelReader(graph);
			mmodel = SBpmnModelReader.readModel(file, vreader);
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
}

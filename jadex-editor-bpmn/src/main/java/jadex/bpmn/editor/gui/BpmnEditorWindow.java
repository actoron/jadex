package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.controllers.DeletionController;
import jadex.bpmn.editor.gui.controllers.SelectionController;
import jadex.bpmn.editor.gui.propertypanels.SPropertyPanelFactory;
import jadex.bpmn.model.MBpmnModel;
import jadex.commons.gui.JSplitPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.util.mxEvent;

public class BpmnEditorWindow extends JFrame
{
	/** The tool bar. */
	protected BpmnToolbar bpmntoolbar;
	
	/** The tab pane showing the models. */
	protected JTabbedPane tabpane;
	
	/** The global settings. */
	protected Settings settings;
	
	public BpmnEditorWindow()
	{
		super(BpmnEditor.APP_NAME);
		
		settings = Settings.load();
		
		getContentPane().setLayout(new BorderLayout());
		
		bpmntoolbar = new BpmnToolbar(GuiConstants.DEFAULT_ICON_SIZE);
		getContentPane().add(bpmntoolbar, BorderLayout.PAGE_START);
		
		final JSplitPanel statuspane = new JSplitPanel(JSplitPane.VERTICAL_SPLIT);
		statuspane.setOneTouchExpandable(true);
		statuspane.setBottomComponent(new StatusArea());
		getContentPane().add(statuspane, BorderLayout.CENTER);
		
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
		statuspane.setTopComponent(tabpane);
		
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
						statuspane.repaint();
						statuspane.setDividerLocation(statuspane.getHeight());
						statuspane.setDividerLocation(1.0);
						statuspane.repaint();
						initializeNewModel(newModelTab(null));
					}
				});
				removeWindowListener(this);
			}
		});
		
		setVisible(true);
		getContentPane().doLayout();
		statuspane.setDividerLocation(1.0);
		statuspane.repaint();
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
		if (modelcontainer == null)
		{
			modelcontainer = new ModelContainer();
		}
		modelcontainer.setEditingToolbar(bpmntoolbar);
		
		if (modelcontainer.getGraph() == null)
		{
			modelcontainer.setGraph(new BpmnGraph(modelcontainer, BpmnEditor.STYLE_SHEETS[0].getSecondEntity()));
		}
		
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
		
		// More Swing Bugness
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JSplitPane viewpane = (JSplitPane) tabpane.getSelectedComponent();
				if (viewpane != null)
				{
					viewpane.repaint();
					viewpane.setDividerLocation(GuiConstants.GRAPH_PROPERTY_RATIO);
					viewpane.repaint();
					panel.getModelContainer().setPropertyPanel(SPropertyPanelFactory.createPanel(null, panel.getModelContainer()));
				}
			}
		});
		
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
	
	public void terminate()
	{
		boolean quit = true;
		for (int i = 0; i < tabpane.getTabCount(); ++i)
		{
			BpmnEditorPanel panel = (BpmnEditorPanel) tabpane.getComponentAt(i);
			
			ModelContainer modelcontainer = panel.getModelContainer();
			if (modelcontainer != null && !modelcontainer.checkUnsaved(this))
			{
				quit = false;
				break;
			}
		}
		if (quit)
		{
			try
			{
				settings.save();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			dispose();
			System.exit(0);
		}
	}
}

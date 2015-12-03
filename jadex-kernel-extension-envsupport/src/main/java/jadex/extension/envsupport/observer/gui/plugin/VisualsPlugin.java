package jadex.extension.envsupport.observer.gui.plugin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import jadex.commons.SUtil;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.observer.gui.ObserverCenter;
import jadex.extension.envsupport.observer.perspective.IPerspective;
import jadex.extension.envsupport.observer.perspective.Perspective2D;

public class VisualsPlugin implements IObserverCenterPlugin
{
	/** Plugin name
	 */
	private static final String NAME = "Visuals";
	
	/** The main panel
	 */
	private JSplitPane mainPane;
	
	/** The perspectives
	 */
	private JTable perspectivelist;
	
	/** The dataviews
	 */
	private JList dataviewlist;
	
	/** The zoom spinner
	 */
	private JSpinner zoomSpinner;
	
	/** Check box for inverting the x-axis */
	private JCheckBox invertXBox;
	
	/** Check box for inverting the y-axis */
	private JCheckBox invertYBox;
	
	/** The observer center
	 */
	private ObserverCenter observerCenter_;
	
	/** The perspective selection controller
	 */
	private ListSelectionListener perspectiveController_;
	
	/** The dataview selection controller
	 */
	private ListSelectionListener dataviewController_;
	
	/**
	 * 
	 */
	public VisualsPlugin()
	{
		mainPane = new JSplitPane();
		mainPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainPane.setOneTouchExpandable(true);
		mainPane.setDividerLocation(160);
		mainPane.setResizeWeight(0.5);
		mainPane.setMinimumSize(new Dimension(50, 400));
		
		JSplitPane persViewPane = new JSplitPane();
		persViewPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		persViewPane.setOneTouchExpandable(true);
		persViewPane.setDividerLocation(80);
		persViewPane.setResizeWeight(0.5);
		mainPane.setTopComponent(persViewPane);
		
		JPanel perspectivePanel = new JPanel(new GridBagLayout());
		perspectivePanel.setBorder(new TitledBorder("Perspective"));
		persViewPane.setTopComponent(perspectivePanel);
		
		DefaultTableModel perspectiveModel = new DefaultTableModel(new String[]{"Perspective", "OpenGL"}, 0)
		{
			public Class<?> getColumnClass(int columnIndex)
			{
				if (columnIndex == 1)
					return Boolean.class;
				return super.getColumnClass(columnIndex);
			}
			
			public boolean isCellEditable(int row, int column)
			{
				return (column == 1);
			}
		};
		perspectiveModel.addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				if (e.getColumn() == 1)
				{
					int row = perspectivelist.getSelectedRow();
					String selection = null;
					if (row != -1)
						selection = (String) ((DefaultTableModel) perspectivelist.getModel()).getValueAt(row, 0);
					if (selection != null)
					{
						Boolean opengl = (Boolean) ((DefaultTableModel) perspectivelist.getModel()).getValueAt(row, 1);
						observerCenter_.setOpenGLMode(selection, opengl.booleanValue());
					}
				}
			}
		});
		perspectivelist = new JTable(perspectiveModel);
		perspectivelist.setSelectionModel(new DefaultListSelectionModel());
		JScrollPane perspectiveScrollPane = new JScrollPane(perspectivelist);
		perspectiveController_ = new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				/*int col = perspectivelist.getSelectedColumn();
				if (col == 1)
					return;*/
				int row = perspectivelist.getSelectedRow();
				String selection = null;
				if (row != -1)
					selection = (String) ((DefaultTableModel) perspectivelist.getModel()).getValueAt(row, 0);
				if (selection != null)
				{
					observerCenter_.setSelectedPerspective(selection);
				}
			}
		};
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		perspectivePanel.add(perspectiveScrollPane, c);
		
		JPanel dataviewPanel = new JPanel(new GridBagLayout());
		dataviewPanel.setBorder(new TitledBorder("Dataview"));
		persViewPane.setBottomComponent(dataviewPanel);
		
		dataviewlist = new JList(new DefaultComboBoxModel());
		JScrollPane dataviewScrollPane = new JScrollPane(dataviewlist);
		dataviewController_ = new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				String selection = (String) dataviewlist.getSelectedValue();
				observerCenter_.setSelectedDataView(selection);
			}
		};
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		dataviewPanel.add(dataviewScrollPane, c);
		
		JPanel controlPanel = new JPanel(new GridBagLayout());
		controlPanel.setBorder(new TitledBorder("Controls"));
		mainPane.setBottomComponent(controlPanel);
		
		JPanel movePanel = new JPanel(new GridBagLayout());
		//String[] moveButtonNames = {"Up", "Right", "Down", "Left"};
		String baseImgLoc = "/jadex/extension/envsupport/observer/images/";
		String[] moveImgNames = {"arrow_up.png", "arrow_right.png", "arrow_down.png", "arrow_left.png", "x_small.png"};
		int[] moveButtonPos = {1, 2, 1, 0};
		for (int i = 0; i < 5; ++i)
		{
			JButton b = new JButton();
			c = new GridBagConstraints();
			
			if (i < 4)
			{
				c.gridx = moveButtonPos[i];
				c.gridy = moveButtonPos[(i + 3) % 4];
				final Vector2Double direction = new Vector2Double((i == 1)||(i == 3)? 0.1:0.0, (i == 0)||(i== 2)? 0.1:0.0);
				if ((i & 2) != 0)
					direction.negate();

				b.setAction(new AbstractAction()
					{
						public void actionPerformed(ActionEvent e)
						{
							IPerspective p = observerCenter_.getSelectedPerspective();
							if (p instanceof Perspective2D)
							{
								Perspective2D pers = (Perspective2D) p;
								pers.shiftPosition(direction.copy());
							}
						}
					});
			}
			else if (i == 4)
			{
				c.gridx = 1;
				c.gridy = 1;
				b.setAction(new AbstractAction()
				{
					public void actionPerformed(ActionEvent e)
					{
						IPerspective p = observerCenter_.getSelectedPerspective();
						if (p instanceof Perspective2D)
						{
							p.resetZoomAndPosition();
						}
					}
				});
			}
			
			try
			{
				b.setIcon(new ImageIcon(ImageIO.read(SUtil.getResource(baseImgLoc + moveImgNames[i], getClass().getClassLoader()))));
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			
			b.setBorderPainted(false);
			
			b.setContentAreaFilled(false);
			
			c.weighty = 0.0;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.CENTER;
			c.fill = GridBagConstraints.NONE;
			movePanel.add(b, c);
		}
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.ipady = 10;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		controlPanel.add(movePanel, c);
		
		JPanel zoomPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		controlPanel.add(zoomPanel, c);
		
		JLabel zoomDesc = new JLabel("Zoom");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.ipadx = 30;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		zoomPanel.add(zoomDesc, c);
		
		zoomSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 20.0, 0.0));
		zoomSpinner.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				IPerspective p = observerCenter_.getSelectedPerspective();
				if (p instanceof Perspective2D)
				{
					Perspective2D pers = (Perspective2D) p;
					double newVal = ((Double)zoomSpinner.getValue()).doubleValue();
					pers.setZoom(newVal);
				}
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		zoomPanel.add(zoomSpinner, c);
		
		JPanel invertPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		controlPanel.add(invertPanel, c);
		
		invertXBox = new JCheckBox(new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					IPerspective p = observerCenter_.getSelectedPerspective();
					if (p instanceof Perspective2D)
					{
						Perspective2D pers = (Perspective2D) p;
						pers.setInvertXAxis(invertXBox.isSelected());
					}
				}
			});
		invertXBox.setText("X-Axis Inversion");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		invertPanel.add(invertXBox, c);
		
		invertYBox = new JCheckBox(new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					IPerspective p = observerCenter_.getSelectedPerspective();
					if (p instanceof Perspective2D)
					{
						Perspective2D pers = (Perspective2D) p;
						pers.setInvertYAxis(invertYBox.isSelected());
					}
				}
			});
		invertYBox.setText("Y-Axis Inversion");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		invertPanel.add(invertYBox, c);
		
		JPanel dummy = new JPanel();
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		controlPanel.add(dummy, c);
	}
	
	/** Starts the plugin
	 *  
	 *  @param the observer center
	 */
	public void start(ObserverCenter main)
	{
		observerCenter_ = main;
		
		Map perspectives = observerCenter_.getPerspectives();
		synchronized(perspectives)
		{
			Set themeNames = perspectives.entrySet();
			for (Iterator it = themeNames.iterator(); it.hasNext(); )
			{
				Map.Entry entry = (Map.Entry) it.next();
				String name = (String) entry.getKey();
				Boolean opengl = Boolean.valueOf(((IPerspective) entry.getValue()).getOpenGl());
				((DefaultTableModel) perspectivelist.getModel()).addRow(new Object[]{name, opengl});
			}
			String perspname = observerCenter_.getSelectedPerspective().getName();
			int row = 0;
			while (!perspectivelist.getValueAt(row, 0).equals(perspname))
				++row;
			perspectivelist.getSelectionModel().setSelectionInterval(row, row);
		}
		
		perspectivelist.getSelectionModel().addListSelectionListener(perspectiveController_);
		
		Map dataviews = observerCenter_.getDataViews();
		synchronized(dataviews)
		{
			Set dataviewnames = dataviews.keySet();
			for (Iterator it = dataviewnames.iterator(); it.hasNext(); )
			{
				String name = (String) it.next();
				((DefaultComboBoxModel) dataviewlist.getModel()).addElement(name);
			}
		}
		
		dataviewlist.addListSelectionListener(dataviewController_);
		
		refresh();
		
		
	}
	
	/** Stops the plugin
	 *  
	 */
	public void shutdown()
	{
		perspectivelist.getSelectionModel().removeListSelectionListener(perspectiveController_);
		while (((DefaultTableModel) perspectivelist.getModel()).getRowCount() > 0)
			((DefaultTableModel) perspectivelist.getModel()).removeRow(0);
		dataviewlist.removeListSelectionListener(dataviewController_);
		((DefaultComboBoxModel) dataviewlist.getModel()).removeAllElements();
	}
	
	/** Returns the name of the plugin
	 *  
	 *  @return name of the plugin
	 */
	public String getName()
	{
		return NAME;
	}
	
	/** Returns the path to the icon for the plugin in the toolbar.
	 * 
	 *  @return path to the icon
	 */
	public String getIconPath()
	{
		return getClass().getPackage().getName().replaceAll("gui.plugin","").concat("images.").replaceAll("\\.", "/").concat("visuals_icon.png");
	}
	
	/** Returns the viewable component of the plugin
	 *  
	 *  @return viewable component of the plugin
	 */
	public Component getView()
	{
		return mainPane;
	}
	
	/** Refreshes the display
	 */
	public void refresh()
	{
		String selection = observerCenter_.getSelectedPerspective().getName();
		//TODO: FIXME
		/*Map perspectives = observerCenter_.getPerspectives();
		perspectivelist.getSelectionModel().removeListSelectionListener(perspectiveController_);
		((DefaultComboBoxModel) perspectivelist.getModel()).removeAllElements();
		synchronized(perspectives)
		{
			Set themeNames = perspectives.keySet();
			for (Iterator it = themeNames.iterator(); it.hasNext(); )
			{
				String name = (String) it.next();
				((DefaultComboBoxModel) perspectivelist.getModel()).addElement(name);
			}
		}*/
		
		/*int row = ((DefaultTableModel) perspectivelist.getModel());
		perspectivelist.getSelectionModel().// setSelectedValue(selection, true);*/
		//perspectivelist.getSelectionModel().addListSelectionListener(perspectiveController_);
		
		selection = observerCenter_.getSelectedDataViewName();
		Map dataviews = observerCenter_.getDataViews();
		dataviewlist.removeListSelectionListener(dataviewController_);
		((DefaultComboBoxModel) dataviewlist.getModel()).removeAllElements();
		synchronized(dataviews)
		{
			Set dataviewnames = dataviews.keySet();
			for (Iterator it = dataviewnames.iterator(); it.hasNext(); )
			{
				String name = (String) it.next();
				((DefaultComboBoxModel) dataviewlist.getModel()).addElement(name);
			}
		}
		dataviewlist.setSelectedValue(selection, true);
		dataviewlist.addListSelectionListener(dataviewController_);
		
		IPerspective p = observerCenter_.getSelectedPerspective();
		if (p instanceof Perspective2D)
		{
			Perspective2D pers = (Perspective2D) p;
			zoomSpinner.setValue(Double.valueOf(pers.getZoom()));
			((SpinnerNumberModel) zoomSpinner.getModel()).setMaximum(Double.valueOf(pers.getZoomLimit()));
			((SpinnerNumberModel) zoomSpinner.getModel()).setStepSize(Double.valueOf(pers.getZoomStepping()));
			
			invertXBox.setSelected(pers.getInvertXAxis());
			invertYBox.setSelected(pers.getInvertYAxis());
		}
	}
	
	/**
	 *  Should plugin be visible.
	 */
	public boolean isVisible()
	{
		return true;
	}
	
	/**
	 *  Should plugin be started on load.
	 */
	public boolean isStartOnLoad()
	{
		return false;
	}
}

package jadex.adapter.base.envsupport.observer.gui.plugin;

import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.adapter.base.envsupport.observer.perspective.IPerspective;
import jadex.adapter.base.envsupport.observer.perspective.Perspective2D;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
	private JList perspectivelist;
	
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
	
	public VisualsPlugin()
	{
		mainPane = new JSplitPane();
		mainPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainPane.setOneTouchExpandable(true);
		mainPane.setDividerLocation(160);
		mainPane.setResizeWeight(0.5);
		mainPane.setMinimumSize(new Dimension(200, 200));
		
		JSplitPane persViewPane = new JSplitPane();
		persViewPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		persViewPane.setOneTouchExpandable(true);
		persViewPane.setDividerLocation(80);
		persViewPane.setResizeWeight(0.5);
		mainPane.setTopComponent(persViewPane);
		
		JPanel perspectivePanel = new JPanel(new GridBagLayout());
		perspectivePanel.setBorder(new TitledBorder("Perspective"));
		persViewPane.setTopComponent(perspectivePanel);
		
		perspectivelist = new JList(new DefaultComboBoxModel());
		JScrollPane perspectiveScrollPane = new JScrollPane(perspectivelist);
		perspectiveController_ = new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				String selection = (String) perspectivelist.getSelectedValue();
				observerCenter_.setSelectedPerspective(selection);
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
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		dataviewPanel.add(dataviewScrollPane, c);
		
		JPanel controlPanel = new JPanel(new GridBagLayout());
		controlPanel.setBorder(new TitledBorder("Controls"));
		mainPane.setBottomComponent(controlPanel);
		
		JPanel movePanel = new JPanel(new BorderLayout());
		String[] moveButtonNames = {"Up", "Right", "Down", "Left"};
		String[] moveButtonPos = {BorderLayout.NORTH, BorderLayout.EAST, BorderLayout.SOUTH, BorderLayout.WEST};
		for (int i = 0; i < 4; ++i)
		{
			final Vector2Double direction = new Vector2Double((i == 1)||(i == 3)? 0.1:0.0, (i == 0)||(i== 2)? 0.1:0.0);
			if ((i & 2) == 0)
				direction.negate();
			
			JButton b = new JButton(new AbstractAction()
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
			b.setText(moveButtonNames[i]);
			movePanel.add(b, moveButtonPos[i]);
		}
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.ipady = 10;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		controlPanel.add(movePanel, c);
		
		JPanel zoomPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		controlPanel.add(zoomPanel, c);
		
		JLabel zoomDesc = new JLabel("Zoom");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.ipadx = 30;
		c.anchor = GridBagConstraints.NORTHWEST;
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
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		zoomPanel.add(zoomSpinner, c);
		
		JPanel invertPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.NORTHWEST;
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
		c.ipadx = 30;
		c.anchor = GridBagConstraints.NORTHWEST;
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
		c.ipadx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		invertPanel.add(invertYBox, c);
		
		JPanel dummy = new JPanel();
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
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
			Set themeNames = perspectives.keySet();
			for (Iterator it = themeNames.iterator(); it.hasNext(); )
			{
				String name = (String) it.next();
				((DefaultComboBoxModel) perspectivelist.getModel()).addElement(name);
			}
		}
		
		perspectivelist.addListSelectionListener(perspectiveController_);
		
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
		perspectivelist.removeListSelectionListener(perspectiveController_);
		((DefaultComboBoxModel) perspectivelist.getModel()).removeAllElements();
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
		Map perspectives = observerCenter_.getPerspectives();
		perspectivelist.removeListSelectionListener(perspectiveController_);
		((DefaultComboBoxModel) perspectivelist.getModel()).removeAllElements();
		synchronized(perspectives)
		{
			Set themeNames = perspectives.keySet();
			for (Iterator it = themeNames.iterator(); it.hasNext(); )
			{
				String name = (String) it.next();
				((DefaultComboBoxModel) perspectivelist.getModel()).addElement(name);
			}
		}
		perspectivelist.setSelectedValue(selection, true);
		perspectivelist.addListSelectionListener(perspectiveController_);
		
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
			zoomSpinner.setValue(new Double(pers.getZoom()));
			((SpinnerNumberModel) zoomSpinner.getModel()).setMaximum(new Double(pers.getZoomLimit()));
			((SpinnerNumberModel) zoomSpinner.getModel()).setStepSize(new Double(pers.getZoomStepping()));
			
			invertXBox.setSelected(pers.getInvertXAxis());
			invertYBox.setSelected(pers.getInvertYAxis());
		}
		
		
	}
}

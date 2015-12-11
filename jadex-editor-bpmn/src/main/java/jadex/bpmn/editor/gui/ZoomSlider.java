package jadex.bpmn.editor.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import jadex.bpmn.editor.gui.controllers.MouseController;

/**
 *  Box for zoom setting.
 */
public class ZoomSlider extends JPanel
{
	/** The bpmn component. */
	protected BpmnGraphComponent bpmncomp;
	
	/** The editor window. */
	protected BpmnEditorWindow editorwindow;
	
	/** Current model container */
	protected ModelContainer currentcontainer;
	
	/** Change listener. */
	protected ChangeListener changelistener;
	
	/** The slider. */
	protected JSlider slider;
	
	/**
	 *  Creates the box
	 *  @param window The editor window.
	 */
	public ZoomSlider(BpmnGraphComponent bpmncomp, ModelContainer modelcontainer)
	{
		setLayout(new GridBagLayout());
		this.bpmncomp = bpmncomp;
		this.currentcontainer = modelcontainer;
		
		slider = new JSlider(GuiConstants.MIN_ZOOM_LEVEL, GuiConstants.MAX_ZOOM_LEVEL);
		
		changelistener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				int val = slider.getValue();
				MouseController.setScale(currentcontainer, currentcontainer.getGraph().getView().getScale(), val * 0.01, null);
			}
		};
		
		final mxIEventListener zoomlistener = new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				if(currentcontainer != null)
				{
					setZoomText(currentcontainer.getGraph().getView().getScale());
				}
			}
		};
		currentcontainer.getGraph().getView().addListener(mxEvent.SCALE, zoomlistener);
		
		slider.addChangeListener(changelistener);
		
		final JLabel label = new JLabel();
		slider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				label.setText(String.valueOf(slider.getValue()) + "%");
			}
		});
		
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.NONE;
		add(label, g);
		g = new GridBagConstraints();
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1.0;
		g.gridx = 1;
		add(slider, g);
	}
	
	/**
	 *  Creates the box
	 *  @param window The editor window.
	 */
	public ZoomSlider(BpmnEditorWindow window)
	{
		setLayout(new GridBagLayout());
		
		slider = new JSlider(GuiConstants.MIN_ZOOM_LEVEL, GuiConstants.MAX_ZOOM_LEVEL);
		
		this.editorwindow = window;
		
		changelistener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				int val = slider.getValue();
				MouseController.setScale(currentcontainer, currentcontainer.getGraph().getView().getScale(), val * 0.01, null);
			}
		};
		
		final mxIEventListener zoomlistener = new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				if(currentcontainer != null)
				{
					setZoomText(currentcontainer.getGraph().getView().getScale());
				}
			}
		};
		
		window.addTabListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if(currentcontainer != null)
				{
					currentcontainer.getGraph().getView().removeListener(zoomlistener);
				}
				
				currentcontainer = editorwindow.getSelectedModelContainer();
				
				if (currentcontainer != null)
				{
					currentcontainer.getGraph().getView().addListener(mxEvent.SCALE, zoomlistener);
					setZoomText(currentcontainer.getGraph().getView().getScale());
				}
			}
		});
		
		slider.addChangeListener(changelistener);
		
		final JLabel label = new JLabel();
		slider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				label.setText(String.valueOf(slider.getValue()) + "%");
			}
		});
		
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.NONE;
		add(label, g);
		g = new GridBagConstraints();
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1.0;
		g.gridx = 1;
		add(slider, g);
	}
	
	/**
	 *  Sets the zoom text.
	 *  @param scale The scale.
	 */
	protected void setZoomText(double scale)
	{
		slider.removeChangeListener(changelistener);
		slider.setValue((int) Math.round(scale * 100.0));
		slider.addChangeListener(changelistener);
	}
}

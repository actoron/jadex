package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.gui.controllers.MouseController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

/**
 *  Box for zoom setting.
 *
 */
public class ZoomBox extends JComboBox
{
	/** The editor window. */
	protected BpmnEditorWindow editorwindow;
	
	/** Current model container */
	protected ModelContainer currentcontainer;
	
	/** Action listener. */
	protected ActionListener actionlistener;
	
	/**
	 *  Creates the box
	 *  @param window The editor window.
	 */
	public ZoomBox(BpmnEditorWindow window)
	{
		super(GuiConstants.STANDARD_ZOOM_LEVELS);
		this.editorwindow = window;
		setEditable(true);
		
		actionlistener =  new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String valstr = (String) getSelectedItem();
				valstr = valstr.replaceAll("[^0123456789]", "").trim();
				try
				{
					int val = Math.max(20, Math.min(400, Integer.parseInt(valstr)));
					MouseController.setScale(currentcontainer, currentcontainer.getGraph().getView().getScale(), val * 0.01, null);
				}
				catch (Exception e1)
				{
				}
				System.out.println("A");
			}
		};
		
		final mxIEventListener zoomlistener = new mxIEventListener()
		{
			public void invoke(Object sender, mxEventObject evt)
			{
				if (currentcontainer != null)
				{
					setZoomText(currentcontainer.getGraph().getView().getScale());
				}
			}
		};
		
		window.addTabListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (currentcontainer != null)
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
		
		addActionListener(actionlistener);
	}
	
	/**
	 *  Sets the zoom text.
	 *  
	 *  @param scale The scale.
	 */
	protected void setZoomText(double scale)
	{
		removeActionListener(actionlistener);
		setSelectedItem(String.valueOf(Math.round(scale * 100.0)) + "%");
		addActionListener(actionlistener);
	}
}

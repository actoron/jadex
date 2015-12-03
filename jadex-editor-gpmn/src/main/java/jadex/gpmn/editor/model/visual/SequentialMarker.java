package jadex.gpmn.editor.model.visual;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

import jadex.gpmn.editor.gui.GuiConstants;
import jadex.gpmn.editor.model.gpmn.ModelConstants;

/**
 * 
 * Visual marker for sequential plans.
 *
 */
public class SequentialMarker extends mxCell
{
	/** The object providing the mode */
	protected IPlanModeProvider provider;
	
	/**
	 *  Creates a marker.
	 */
	public SequentialMarker(IPlanModeProvider provider)
	{
		super(null, new mxGeometry(), GuiConstants.PLAN_MODE_STYLE);
		this.provider = provider;
		setValue("1..n");
		
		setGeometry();
		setConnectable(false);
		setVertex(true);
	}
	
	public boolean isVisible()
	{
		return ModelConstants.ACTIVATION_MODE_SEQUENTIAL.equals(provider.getPlanMode());
	}
	
	public void setGeometry()
	{
		mxGeometry geo = new mxGeometry(0, 0, GuiConstants.PLAN_MODE_MARKER_WIDTH, GuiConstants.PLAN_MODE_MARKER_HEIGHT);
		geo.setRelative(true);
		mxGeometry pgeo = provider.getGeometry();
		geo.setOffset(new mxPoint((pgeo.getWidth() - GuiConstants.PLAN_MODE_MARKER_WIDTH) / 2,
					  pgeo.getHeight() - GuiConstants.PLAN_MODE_MARKER_HEIGHT));
		setGeometry(geo);
	}
}

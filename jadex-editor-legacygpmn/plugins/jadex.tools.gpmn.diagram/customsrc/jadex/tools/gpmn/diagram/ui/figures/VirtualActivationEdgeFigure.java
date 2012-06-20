package jadex.tools.gpmn.diagram.ui.figures;


import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;

public class VirtualActivationEdgeFigure extends PolylineConnectionEx
{
	public static final double OVAL_RADIUS = 6.0;
	
	private static final PrecisionRectangle OVAL_BOUNDS = new PrecisionRectangle();
	
	private static final PrecisionRectangle OVAL_BOUNDS_EX = new PrecisionRectangle();
	
	static
	{
		OVAL_BOUNDS.setX(-6.0);
		OVAL_BOUNDS.setY(-6.0);
		OVAL_BOUNDS.setWidth(12.0);
		OVAL_BOUNDS.setHeight(12.0);
		
		OVAL_BOUNDS_EX.setX(-8.0);
		OVAL_BOUNDS_EX.setY(-8.0);
		OVAL_BOUNDS_EX.setWidth(16.0);
		OVAL_BOUNDS_EX.setHeight(16.0);
	}
	
	private static final float PLUS_WIDTH_FACTOR = 1.5f;
	
	private static final float PLUS_BOUNDS_DISTANCE = 2.0f;
	
	public VirtualActivationEdgeFigure()
	{
		PolylineDecoration tDec = new PolylineDecoration();
		tDec.setLineWidth(1);
		tDec.setForegroundColor(ColorConstants.black);
		setTargetDecoration(tDec);
		
		Figure expander = new Figure()
		{
			@Override
			public Rectangle getBounds()
			{
				PrecisionPoint c = getPreciseBendPointCenter();
				return OVAL_BOUNDS_EX.getCopy().translate(c);
			}
			
			@Override
			protected void paintFigure(Graphics graphics)
			{
				PrecisionPoint c = getPreciseBendPointCenter();
				GpmnShapePainter.paintOpaqueOval(graphics, OVAL_BOUNDS.getCopy().translate(c));
				GpmnShapePainter.paintPlus(graphics, OVAL_BOUNDS.getCopy().translate(c), PLUS_BOUNDS_DISTANCE, lineWidth * PLUS_WIDTH_FACTOR);
			}
		};
		
		add(expander);
	}
	
	public PrecisionPoint getPreciseBendPointCenter()
	{
		PointList pl = getPolygonPoints();
		PrecisionPoint start = new PrecisionPoint(pl.getPoint(pl.size() / 2 - 1));
		PrecisionPoint end = new PrecisionPoint(pl.getPoint(pl.size() / 2));
		PrecisionPoint center = new PrecisionPoint((start.x + end.x) / 2.0, (start.y + end.y) / 2.0);
		return center;
	}
}

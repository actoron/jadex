package jadex.tools.gpmn.diagram.ui;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.geometry.LineSeg;
import org.eclipse.gmf.runtime.draw2d.ui.geometry.PointListUtilities;
import org.eclipse.gmf.runtime.draw2d.ui.geometry.PrecisionPointList;

public class SlidableRoundedRectangleAnchor extends AbstractConnectionAnchor
{
	public SlidableRoundedRectangleAnchor(ShadowedRoundedRectangleFigure owner)
	{
		super(owner);
	}
	
	
	public Point getLocation(Point p)
	{
		return getClosestRelOutlinePoint(p);
	}
	
	public Point getClosestRelOutlinePoint(Point foreignReference)
	{
		ShadowedRoundedRectangleFigure figure = (ShadowedRoundedRectangleFigure) getOwner();
		Rectangle b = figure.getOutlineBounds().getCopy();
		figure.translateToAbsolute(b);
		
		PrecisionPoint ownReference = new PrecisionPoint(b.getCenter());
		
		int cornerAngle = figure.getCornerAngle();
		
		LineSeg seg = new LineSeg(new PrecisionPoint(ownReference), new PrecisionPoint(foreignReference));
		PrecisionPointList pl = new PrecisionPointList();
		
		PrecisionRectangle rect = new PrecisionRectangle(new Rectangle(b.getTopLeft(), new Point(b.getTopLeft().x + cornerAngle, b.getTopLeft().y + cornerAngle)));
		addPoints(pl, seg.getLineIntersectionsWithEllipse(rect));
		rect = new PrecisionRectangle(new Rectangle(new Point(b.getTopLeft().x, b.getTopLeft().y + b.height), new Point(b.getTopLeft().x + cornerAngle, b.getTopLeft().y + b.height - cornerAngle)));
		addPoints(pl, seg.getLineIntersectionsWithEllipse(rect));
		rect = new PrecisionRectangle(new Rectangle(new Point(b.getTopLeft().x + b.width, b.getTopLeft().y), new Point(b.getTopLeft().x + b.width - cornerAngle, b.getTopLeft().y + cornerAngle)));
		addPoints(pl, seg.getLineIntersectionsWithEllipse(rect));
		rect = new PrecisionRectangle(new Rectangle(new Point(b.getTopLeft().x + b.width, b.getTopLeft().y + b.height), new Point(b.getTopLeft().x + b.width - cornerAngle, b.getTopLeft().y + b.height - cornerAngle)));
		addPoints(pl, seg.getLineIntersectionsWithEllipse(rect));
		rect = new PrecisionRectangle(new Rectangle(new Point(b.getTopLeft().x, b.getTopLeft().y + cornerAngle/2), new Point(b.getBottomRight().x, b.getBottomRight().y - cornerAngle/2)));
		addPoints(pl, seg.getLineIntersectionsWithLineSegs(rectToPoints(rect)));
		rect = new PrecisionRectangle(new Rectangle(new Point(b.getTopLeft().x + cornerAngle/2, b.getTopLeft().y), new Point(b.getBottomRight().x - cornerAngle/2, b.getBottomRight().y)));
		addPoints(pl, seg.getLineIntersectionsWithLineSegs(rectToPoints(rect)));
		
		
		Point loc = PointListUtilities.pickClosestPoint(pl, foreignReference);
		return loc;
	}
	
	/**
	 * Workaround replacement for broken PointList.addAll() method.
	 */
	private void addPoints(PointList container, PointList additions)
	{
		for (int i = 0; i < additions.size(); ++i)
			container.addPoint(additions.getPoint(i));
	}
	
	private PrecisionPointList rectToPoints(PrecisionRectangle rect)
	{
		PrecisionPointList ptList = new PrecisionPointList();
		ptList.addPoint(rect.getTopLeft());
		ptList.addPoint(rect.getTopRight());
		ptList.addPoint(rect.getBottomRight());
		ptList.addPoint(rect.getBottomLeft());
		ptList.addPoint(rect.getTopLeft());
		
		return ptList;
	}
}

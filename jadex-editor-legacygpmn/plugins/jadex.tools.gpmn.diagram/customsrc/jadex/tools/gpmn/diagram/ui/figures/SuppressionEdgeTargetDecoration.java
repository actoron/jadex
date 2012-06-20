package jadex.tools.gpmn.diagram.ui.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.gmf.runtime.draw2d.ui.geometry.PrecisionPointList;
import org.eclipse.swt.graphics.Color;

public class SuppressionEdgeTargetDecoration extends PolygonDecoration
{
	public SuppressionEdgeTargetDecoration()
	{
		super();
		//setFill(true);
		setLineWidth(1);
		setForegroundColor(ColorConstants.black);
		setFill(true);
		PrecisionPointList pl = new PrecisionPointList();
		/*pl.addPoint(getMapMode().DPtoLP(0), getMapMode().DPtoLP(1));
		pl.addPoint(getMapMode().DPtoLP(0), getMapMode().DPtoLP(-1));
		pl.addPoint(getMapMode().DPtoLP(-1), getMapMode().DPtoLP(-1));
		pl.addPoint(getMapMode().DPtoLP(-1), getMapMode().DPtoLP(0));*/
		/*pl.addPrecisionPoint(0, 1);
		pl.addPrecisionPoint(-0.5, -0.5);
		pl.addPrecisionPoint(-1, 1);
		pl.addPrecisionPoint(0, -1);
		pl.addPrecisionPoint(-0.5, -0.5);
		pl.addPrecisionPoint(-1, -1);*/
		pl.addPrecisionPoint(0.0, 0.0);
		pl.addPrecisionPoint(-1.0, 1.0);
		pl.addPrecisionPoint(-1.0, 2.0);
		pl.addPrecisionPoint(-1.0, -2.0);
		pl.addPrecisionPoint(-1.0, -1.0);
		
		setTemplate(pl);
		//setPoints(pl);
		setScale(7, 3);
		
	}
	
	/*public void setScale(double x, double y)
	{
		if (transform == null)
			this.transform = new Transform();
		System.out.println("Scale " + transform);
        transform.setScale(x, y);
        //super.setScale(x, y);
	}
	
	public void setRotation(double angle)
	{
        transform.setRotation(angle);
        //super.setRotation(angle);
	}
	
	private Point location = new Point();
	
	public void setLocation(Point p)
	{
        transform.setTranslation(p.x, p.y);
        location = p.getCopy();
        super.setLocation(p);
	}
	private Point reference = new Point();
	public void setReferencePoint(Point ref)
	{
		reference = ref;
        Point pt = Point.SINGLETON;
        pt.setLocation(ref);
        pt.negate().translate(location);
        setRotation(Math.atan2(pt.y, pt.x));
	}
	
	@Override
	protected void paintFigure(Graphics g)
	{
		g.pushState();
		g.setBackgroundColor(ColorConstants.black);
		g.setLineWidth(1);
		
		g.setClip(getBounds());
		g.fillOval(getBounds());
		
		g.popState();
	}
	
	@Override
	public Rectangle getBounds()
	{
		PrecisionRectangle b = new PrecisionRectangle();
		b.preciseX = 0.0;
		b.preciseY = 3.5;
		b.preciseWidth = 7;
		b.preciseHeight = 7;
		b.translate(location);
		//transform.
		return b;
	}*/

	
	/*@Override
	protected void fillShape(Graphics g)
	{
		g.pushState();
		g.setLineWidth(getLineWidth());
		g.setForegroundColor(ColorConstants.white);
		Rectangle b = new Rectangle(getBounds());
		b.x = b.x + 1;
		b.y = b.y + 1;
		b.width = b.width - 1;
		b.height = b.height -1;
		//GpmnShapePainter.paintOpaqueOval(g, getBounds());
		g.fillOval(b);
		g.setForegroundColor(getForegroundColor());
		g.drawOval(b);
		g.popState();
		throw new RuntimeException();
		//super.fillShape(g);
	}*/
	
	//@Override
	//protected void outlineShape(Graphics g)
//	{
		/*g.pushState();
		PrecisionPointList pl = new PrecisionPointList();
		pl.addPoint(transform.getTransformed(new PrecisionPoint(-0.5, 2.0)));
		pl.addPoint(transform.getTransformed(new PrecisionPoint(-0.5, -2.0)));
		
		System.out.println(getPoints().size());
		//pl.translate(getPoints().getPoint(0));
		Rectangle b = new Rectangle(getBounds());
		b.union(pl.getPoint(0));
		b.union(pl.getPoint(1));
		g.setForegroundColor(getForegroundColor());
		g.setLineWidth(getLineWidth());
		g.setClip(b);
		//g.drawLine(getPoints().getPoint(0), getPoints().getPoint(1));
		g.drawLine(pl.getPoint(0), pl.getPoint(1));
		g.popState();*/
		
		// TODO Auto-generated method stub
		//super.outlineShape(g);
	//}
	
	/*@Override
	public Rectangle getBounds()
	{
		return null;*/
		/*Rectangle b = new Rectangle(super.getBounds());
		for (int i = 0; i < getChildren().size(); ++i)
		{
			Rectangle cb = new Rectangle(((IFigure) getChildren().get(i)).getBounds());
			//translateToAbsolute(b);
			cb.translate(super.getBounds().getLocation());
			System.out.println("C: " + cb);
			b = b.union(cb);
		}
		System.out.println("M: " + b);
		return b;*/
	//}
	
}

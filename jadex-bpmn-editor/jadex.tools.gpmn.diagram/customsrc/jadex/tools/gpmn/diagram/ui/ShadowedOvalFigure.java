package jadex.tools.gpmn.diagram.ui;


import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;

public class ShadowedOvalFigure extends AbstractGpmnFigure
{

	/**
	 * Default Constructor
	 */
	public ShadowedOvalFigure()
	{
		this(DEFAULT_MIN_SIZE);
	}
	
	/**
	 * Constructor with corner angle specified
	 */
	public ShadowedOvalFigure(Dimension minSize)
	{
		super(minSize);
		
		setBorder(new OvalSchemeBorder()
		{
			//@Override
			//public void paint(IFigure fig, Graphics graphics, Insets insets)
			//{
			//	super.paint(fig, graphics, insets);
			//}
		});
	}

	@Override
	protected void fillShape(Graphics graphics)
	{
		graphics.fillOval(super.getInnerPaintBounds());
	}

	@Override
	protected void outlineShape(Graphics graphics)
	{
		graphics.drawOval(super.getOutlineBounds());
	}

}

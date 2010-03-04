
package jadex.tools.gpmn.diagram.ui;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * This scheme border draws a border under an oval shape.
 */
public class OvalSchemeBorder extends RoundedSchemeBorder
{

	public OvalSchemeBorder()
	{
		super(0);
	}

	@Override
	protected void fillShadow(IFigure fig, Graphics graphics, Insets insets)
	{
		Rectangle rect = fig.getBounds().getCopy().translate(2, 2).resize(-4, -4);

		graphics.fillOval(rect/*.getCopy()*/.crop(insets));
	}
}

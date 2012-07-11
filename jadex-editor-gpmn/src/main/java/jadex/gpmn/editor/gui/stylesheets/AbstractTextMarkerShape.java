package jadex.gpmn.editor.gui.stylesheets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxActorShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;

/**
 * 
 *  Abstract implementation for a text-based node marker.
 *
 */
public abstract class AbstractTextMarkerShape extends mxActorShape
{
	
	/**
	 *  Paints the shape.
	 */
	public void paintShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		super.paintShape(canvas, state);
		
		Rectangle rect = state.getRectangle();
		int x = rect.x;
		int y = rect.y;
		int w = rect.width;
		int h = rect.height;
		
		Color fontcolor = Color.BLACK;
		try
		{
			fontcolor = mxUtils.parseColor((String) state.getStyle().get(mxConstants.STYLE_FONTCOLOR));
		}
		catch (Exception e)
		{
		}
		
		Font font = mxUtils.getFont(state.getStyle(), canvas.getScale());
		String indicator = getText(state);
		Graphics2D g = canvas.getGraphics();
		if (indicator == null)
		{
			System.out.println();
		}
		Rectangle2D textbounds = font.createGlyphVector(g.getFontRenderContext(), indicator).getVisualBounds();
		double shiftx = textbounds.getWidth() * 0.5;
		double shifty = textbounds.getHeight() * 0.5;
		
		double w2 = w * 0.5;
		double h2 = h * 0.5;
		
		
		Color oldcolor = g.getColor();
		g.setColor(fontcolor);
		Font oldfont = g.getFont();
		g.setFont(font);
		Shape s = font.createGlyphVector(g.getFontRenderContext(), indicator).getOutline((float) (x + w2 - shiftx), (float) (y + h2 + shifty));
		//g.drawString(indicator, (float) (x + w2 - shiftx), (float) (y + h2 + shifty));
		g.fill(s);
		//canvas.getGraphics().draw(textshape);
		g.setColor(oldcolor);
		g.setFont(oldfont);
		
		//ret.moveTo(w2 - shiftx, h2 - shifty);
	}
	
	/**
	 *  Returns the text for the marker.
	 *  
	 *  @param state The cell state.
	 *  @return Text of the marker.
	 */
	protected abstract String getText(mxCellState state);
}

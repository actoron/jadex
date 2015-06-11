package jadex.extension.envsupport.observer.graphics.opengl;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;
import jadex.extension.envsupport.observer.graphics.drawable.Text;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SimpleValueFetcher;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import com.sun.opengl.util.j2d.TextRenderer;

public class TextGLRenderer implements IGLRenderer
{
	/** Dummy FontRenderContext since we don't use FRCs */
	private final static FontRenderContext DUMMY_FRC = new FontRenderContext(null, true, true);
	
	/**
	 * Prepares the object for rendering to a Java2D viewport
	 * 
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public void prepareAndExecuteDraw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJOGL vp)
	{
		IParsedExpression drawcondition = primitive.getDrawCondition();
		boolean draw = drawcondition==null;
		if(!draw)
		{
			SimpleValueFetcher fetcher = new SimpleValueFetcher(vp.getPerspective().getObserverCenter().getSpace().getFetcher());
			fetcher.setValue("$object", obj);
			fetcher.setValue("$perspective", vp.getPerspective());
//			fetcher.setValue("$space", vp.getPerspective().getObserverCenter().getSpace());
			draw = ((Boolean)drawcondition.getValue(fetcher)).booleanValue();
		}
		
		if (draw)
			draw(dc, primitive, obj, vp);
	}
	
	/**
	 * Draws the primitive.
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public void draw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJOGL vp)
	{
		IVector2 position = ((IVector2)dc.getBoundValue(obj, primitive.getPosition(), vp)).copy();
		IVector2 dcPos = Vector2Double.ZERO;
		if (primitive.isRelativePosition())
			dcPos = (IVector2)dc.getBoundValue(obj, dc.getPosition(), vp);//SObjectInspector.getVector2(obj, dc.getPosition());
		IVector2 dcScale = (IVector2)dc.getBoundValue(obj, dc.getSize(), vp);
		if((position == null) || (dcPos == null) || (dcScale == null))
		{
			return;
		}
		
		IVector2 canvasSize = vp.getCanvasSize();
		float fontscale = Text.getBasicFontScale(canvasSize, vp.getAreaSize(), vp.getSize());
		if (primitive.isRelativeSize())
		{
			position = position.copy().multiply(dcScale);
			// Do not scale fintsize wrt. drawable combiner size.
//			fontscale *= dcScale.getMean().getAsFloat();
		}
		
		Text textP = (Text) primitive;
		Font font = textP.getBaseFont().deriveFont(textP.getBaseFont().getSize() * fontscale);;
			
		TextRenderer tr = vp.getTextRenderer(font);
		
		Color color = (Color)dc.getBoundValue(obj, primitive.getColor(), vp);
		tr.setColor(color);
		
		IVector2 pos = Text.getBasePosition(vp, dcPos, position, canvasSize, vp.getInvertX(), vp.getInvertY());
		
		double xPos = pos.getXAsDouble();
		double yPos = pos.getYAsDouble();
		
		String text = Text.getReplacedText(dc, obj, textP.getText(), vp);
		String[] lines = text.split("(\n\r?)|(\r)");
		
		if (textP.getVAlign() == Primitive.ALIGN_MIDDLE || textP.getVAlign() == Primitive.ALIGN_BOTTOM)
		{
			double hadj = 0;
			for (int i = 0; i < lines.length; ++i)
			{
				TextLayout tl = new TextLayout(lines[i], font, DUMMY_FRC);
				hadj += tl.getAscent() + tl.getDescent() + tl.getLeading();
			}
			if (textP.getVAlign() == Primitive.ALIGN_MIDDLE)
			{
				hadj *= 0.5;
			}
			yPos += hadj;
		}
		
		for (int i = 0; i < lines.length; ++i)
		{
//			System.out.println("hier2");
			TextLayout tl = new TextLayout(lines[i], font, DUMMY_FRC);
//			System.out.println("hier2.1");
			
//			if (i != 0)
//			{
//				System.out.println("hier2.2");
//				yPos -= tl.getAscent();
//			}
			
			yPos -= tl.getLeading() + tl.getAscent();

//			System.out.println("hier2.3");
			tr.beginRendering(canvasSize.getXAsInteger(), canvasSize.getYAsInteger());
//			System.out.println("vorher: "+SUtil.arrayToString(lines[i])+", "+(xPos + textP.getAlignment(tl))+", "+yPos);
			tr.draw(lines[i], (int) (xPos + textP.getTextHAlignment(tl)), (int) yPos);
//			System.out.println("nachher");
			tr.endRendering();
			
//			System.out.println("hier2.6");
//			yPos -= (tl.getDescent() + tl.getLeading());
			yPos -= (tl.getDescent());
//			System.out.println("hier2.0");
		}
	}
}

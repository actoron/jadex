package jadex.extension.envsupport.observer.graphics.drawable;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.AbstractViewport;
import jadex.extension.envsupport.observer.graphics.IViewport;
import jadex.javaparser.IParsedExpression;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextLayout;

/**
 * Drawable component for displaying text.
 */
public final class Text extends Primitive
{
	/** Viewport size (in pixels) on which the base font size is relative to */
	public final static float BASE_VIEWPORT_SIZE = 300.0f;
	
	/** Font used for the text */
	private Font baseFont;
	
	/** Lines of text */
	private String text;
	
	/** The condition deciding if the drawable should be drawn. */
	//private IParsedExpression drawcondition;
	
	public Text()
	{
		this(null, null, null, null, 0, 0, 0, null);
	}
	
	public Text(Object position, Font baseFont, Color color, String text, int halign, int valign, int absFlags, IParsedExpression drawcondition)
	{
		super(Primitive.PRIMITIVE_TYPE_TEXT, position, Vector3Double.ZERO.copy(), new Vector2Double(1.0), absFlags, color, drawcondition);
		setHAlign(halign);
		setVAlign(valign);
		if (baseFont == null)
			baseFont = new Font(null);
		this.baseFont = baseFont;
		if (text == null)
			text = "";
		this.text = text;
	}
	
	public Font getBaseFont()
	{
		return baseFont;
	}
	
	public String getText()
	{
		return text;
	}
	
	public double getTextHAlignment(TextLayout tl)
	{
		double xAlign = 0.0;
		switch(getHAlign())
		{
			case ALIGN_RIGHT:
				xAlign -= tl.getAdvance();
				break;
			case ALIGN_CENTER:
				xAlign -= tl.getAdvance() / 2.0f;
			case ALIGN_LEFT:
			default:
		}
		return xAlign;
	}
	
	public final static IVector2 getBasePosition(AbstractViewport vp, IVector2 dcPos, IVector2 position, IVector2 canvasSize, boolean invX, boolean invY)
	{
		IVector2 pos = vp.getPosition().copy().negate().add(vp.getObjectShift()).add(dcPos).add(position).divide(vp.getPaddedSize()).multiply(canvasSize);
		if (invX)
			pos.negateX().add(new Vector2Double(canvasSize.getXAsDouble(), 0));
		if (invY)
			pos.negateY().add(new Vector2Double(0, canvasSize.getYAsDouble()));
		return pos;
	}
	
	public final static float getBasicFontScale(IVector2 canvasSize, IVector2 areaSize, IVector2 size)
	{
		return ((Math.min(canvasSize.getXAsFloat(), canvasSize.getYAsFloat()) / BASE_VIEWPORT_SIZE) * areaSize.copy().divide(size).getMean().getAsFloat());
	}
	
	public final static String getReplacedText(DrawableCombiner dc, Object obj, String text, IViewport vp)
	{
		String[] tokens = text.split("\\$");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tokens.length; ++i)
		{
//			System.out.println("hier3");
			if ((i & 1) == 0)
			{
				sb.append(tokens[i]);
			}
			else
			{
				if("".equals(tokens[i]))
				{
					sb.append("$");
				}
				else
				{
					sb.append(String.valueOf(dc.getBoundValue(obj, tokens[i], vp)));
//					sb.append(String.valueOf(SObjectInspector.getProperty(obj, tokens[i])));
				}
			}
		}
		
		return sb.toString();
	}
}

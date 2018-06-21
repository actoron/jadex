package jadex.extension.envsupport.observer.graphics.drawable;

import java.awt.Color;

import jadex.javaparser.IParsedExpression;

public class Primitive extends AbstractVisual2D
{
	public static final int ABSOLUTE_POSITION = 1;
	public static final int ABSOLUTE_SIZE = 2;
	public static final int ABSOLUTE_ROTATION = 4;
	
	public static final int PRIMITIVE_TYPE_ELLIPSE			 = 0;
	public static final int PRIMITIVE_TYPE_RECTANGLE		 = 1;
	public static final int PRIMITIVE_TYPE_REGULARPOLYGON	 = 2;
	public static final int PRIMITIVE_TYPE_TEXT				 = 3;
	public static final int PRIMITIVE_TYPE_TEXTUREDRECTANGLE = 4;
	public static final int PRIMITIVE_TYPE_TRIANGLE			 = 5;
	
	/** Left Alignment */
	public final static int ALIGN_LEFT		= 0;
	
	/** Center Alignment */
	public final static int ALIGN_CENTER 	= 1;
	
	/** Right Alignment */
	public final static int ALIGN_RIGHT 	= 2;
	
	/** Top Alignment */
	public final static int ALIGN_TOP		= 0;
	
	/** Middle Alignment */
	public final static int ALIGN_MIDDLE 	= 1;
	
	/** Bottom Alignment */
	public final static int ALIGN_BOTTOM 	= 2;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCPos;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCSize;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCRot;
	
	/** Color or Color binding of the primitive. */
	protected Object	color_;
	
	/** The condition deciding if the drawable should be drawn. */
	protected IParsedExpression drawcondition;
	
	/** Cached render information */
	protected Object[] renderinfos;
	
	/** Primitive type */
	protected int type;
	
	/** Horizontal alignment */
	protected int halign;
	
	/** Vertical alignment */
	protected int valign;
	
	public Primitive()
	{
		super();
		type = PRIMITIVE_TYPE_ELLIPSE;
		enableDCPos = true;
		enableDCSize = true;
		enableDCRot = true;
		setColor(Color.WHITE);
		flushRenderInfo();
	}
	
	/**
	 * Initializes the drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param absFlags flags for setting position, size and rotation as absolutes
	 * @param c the drawable's color or color binding
	 */
	public Primitive(int type, Object position, Object rotation, Object size, int absFlags, Object c, IParsedExpression drawcondition)
	{
		super(position, rotation, size);
		this.type = type;
		enableDCPos = (absFlags & ABSOLUTE_POSITION) == 0;
		enableDCSize = (absFlags & ABSOLUTE_SIZE) == 0;
		enableDCRot = (absFlags & ABSOLUTE_ROTATION) == 0;
		this.drawcondition = drawcondition;
		
		if (c == null)
			c = Color.WHITE;
		setColor(c);
		flushRenderInfo();
	}
	
	
	
	/**
	 *  Get the primitive type.
	 *  @return The type.
	 */
	public int getType()
	{
		return type;
	}

	/**
	 *  Set the primitive type.
	 *  @param type The type to set.
	 */
	public void setType(int type)
	{
		this.type = type;
	}

	/** 
	 * Enables using absolute positioning.
	 * 
	 * @param enable true, to use the drawable's value as an absolute.
	 */
	public void enableAbsolutePosition(boolean enable)
	{
		enableDCPos = !enable;
	}
	
	/** 
	 * Enables using absolute scaling.
	 * 
	 * @param enable true, to use the drawable's value as an absolute.
	 */
	public void enableAbsoluteSize(boolean enable)
	{
		enableDCSize = !enable;
	}
	
	/** 
	 * Enables using absolute rotation.
	 * 
	 * @param enable true, to use the drawable's value as an absolute.
	 */
	public void enableAbsoluteRotation(boolean enable)
	{
		enableDCRot = !enable;
	}
	
	/** 
	 * Tests if primitive is using relative positioning.
	 * 
	 * @return true, if the drawable's value is an absolute.
	 */
	public boolean isRelativePosition()
	{
		return enableDCPos;
	}
	
	/** 
	 * Tests if primitive is using relative scaling.
	 * 
	 * @return true, if the drawable's value is an absolute.
	 */
	public boolean isRelativeSize()
	{
		return enableDCSize;
	}
	
	/** 
	 * Tests if primitive is using relative rotation.
	 * 
	 * @return true, if the drawable's value is an absolute.
	 */
	public boolean isRelativeRotation()
	{
		return enableDCRot;
	}
	
	/**
	 * Sets the draw condition.
	 * 
	 * @param drawcondition the draw condition
	 */
	public void setDrawCondition(IParsedExpression drawcondition)
	{
		this.drawcondition = drawcondition;
	}
	
	/**
	 * Gets the draw condition.
	 * 
	 * @return the draw condition
	 */
	public IParsedExpression getDrawCondition()
	{
		return drawcondition;
	}
	
	/**
	 * Gets the color or color binding of the drawable
	 * 
	 * @return color or color binding of the drawable
	 */
	public Object getColor()
	{
		return color_;
	}

	/**
	 * Sets a new color or binding for the drawable
	 * 
	 * @param c new color or binding
	 */
	public void setColor(Object c)
	{
		color_ = c;
	}
	
	/**
	 *  Gets the vertical alignment.
	 *  
	 *  @return The vertical alignment (top, middle, bottom).
	 */
	public int getVAlign()
	{
		return valign;
	}
	
	/**
	 *  Sets the vertical alignment.
	 *  
	 *  @param valign The vertical alignment (top, middle, bottom).
	 */
	public void setVAlign(int valign)
	{
		this.valign = valign;
	}
	
	/**
	 *  Gets the horizontal alignment.
	 *  
	 *  @return The horizontal alignment (left, center, right).
	 */
	public int getHAlign()
	{
		return halign;
	}
	
	/**
	 *  Sets the horizontal alignment.
	 *  
	 *  @param halign The horizontal alignment (left, center, right).
	 */
	public void setHAlign(int halign)
	{
		this.halign = halign;
	}
	
	/**
	 * Returns cached render information.
	 * 
	 * @param infoId id of the information
	 * @return render info
	 */
	public Object getRenderInfo(int infoId)
	{
		return renderinfos[infoId];
	}
	
	/**
	 * Sets cached render information.
	 * 
	 * @param infoId id of the information
	 * @param info the render info
	 */
	public void setRenderInfo(int infoId, Object info)
	{
		if (renderinfos.length <= infoId)
		{
			Object[] newInfoArray = new Object[infoId + 1];
			System.arraycopy(renderinfos, 0, newInfoArray, 0, renderinfos.length);
			renderinfos= newInfoArray;
		}
		
		renderinfos[infoId] = info;
	}
	
	/**
	 *  Flushes the render information.
	 */
	public void flushRenderInfo()
	{
		renderinfos = new Object[0];
	}
}

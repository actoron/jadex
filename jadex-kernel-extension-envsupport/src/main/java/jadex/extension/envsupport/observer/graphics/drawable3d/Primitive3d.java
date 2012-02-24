package jadex.extension.envsupport.observer.graphics.drawable3d;

import java.awt.Color;

import jadex.javaparser.IParsedExpression;

public class Primitive3d extends AbstractVisual3d
{
	public static final int ABSOLUTE_POSITION = 1;
	public static final int ABSOLUTE_SIZE = 2;
	public static final int ABSOLUTE_ROTATION = 4;
	
	public static final int PRIMITIVE_TYPE_SPHERE			 = 0;
	public static final int PRIMITIVE_TYPE_BOX		 	 	 = 1;
	public static final int PRIMITIVE_TYPE_CYLINDER			 = 2;
	public static final int PRIMITIVE_TYPE_ARROW			= 3;
	public static final int PRIMITIVE_TYPE_DOME		 		= 4;
	public static final int PRIMITIVE_TYPE_TORUS			 = 5;
	

	
	public static final int PRIMITIVE_TYPE_OBJECT3D	 		= 6;
	
	public static final int PRIMITIVE_TYPE_TEXT3D			 = 7;
	
	public static final int PRIMITIVE_TYPE_SKY	 		= 8;
	
	public static final int PRIMITIVE_TYPE_TERRAIN	 		= 9;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCPos;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCSize;
	
	/** Enable DrawableCombiner position */
	protected boolean enableDCRot;
	
	/** Color or Color binding of the primitive. */
	protected Object	color_;
	
	/** Path to the Texture */
	protected String	texturePath_;
	
	/** The condition deciding if the drawable should be drawn. */
	protected IParsedExpression drawcondition;
	
	/** Cached render information */
	protected Object[] renderinfos;
	
	/** Primitive type */
	protected int type;
	
	public Primitive3d()
	{
		super();
		type = PRIMITIVE_TYPE_SPHERE;
		enableDCPos = false;
		enableDCSize = false;
		enableDCRot = false;
		setColor(Color.MAGENTA);
		texturePath_ = "";
	}
	
	
	/**
	 * Initializes the drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param c the drawable's color or color binding
	 */
	public Primitive3d(int type, Object position, Object rotation, Object size, Object c)
	{
		super(position, rotation, size);
		this.type = type;
		
		if (c == null)
			c = Color.DARK_GRAY;
		setColor(c);
		setTexturePath("");
	}
	
	
	/**
	 * Initializes the drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param c the drawable's color or color binding
	 */
	public Primitive3d(int type, Object position, Object rotation, Object size, Object c, IParsedExpression drawcondition)
	{
		super(position, rotation, size);
		this.type = type;
		
		if (c == null)
			c = Color.WHITE;
		setColor(c);
		setTexturePath("");
		this.drawcondition = drawcondition;
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
	public Primitive3d(int type, Object position, Object rotation, Object size, int absFlags, Object c, String texturePath, IParsedExpression drawcondition)
	{
		super(position, rotation, size);
		this.type = type;
		enableDCPos = (absFlags & ABSOLUTE_POSITION) == 0;
		enableDCSize = (absFlags & ABSOLUTE_SIZE) == 0;
		enableDCRot = (absFlags & ABSOLUTE_ROTATION) == 0;
		this.drawcondition = drawcondition;
		
		if (c == null)
			c = Color.DARK_GRAY;
		setColor(c);
		setTexturePath(texturePath);
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
	 *  Set the texture path.
	 *  @return The texture path.
	 */
	public void setTexturePath(String texturepath)
	{
		texturePath_ = texturepath;
	}
	/**
	 *  Returns the texture path.
	 *  @return The texture path.
	 */
	public String getTexturePath()
	{
		return texturePath_;
	}
}

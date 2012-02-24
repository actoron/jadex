package jadex.extension.envsupport.observer.graphics.drawable3d;

import jadex.javaparser.IParsedExpression;

/**
 * 
 */
public class Text3d extends Primitive3d
{
	/** Model path. */
	protected String			_text;

	/**
	 * Creates default Text3d.
	 * 
	 * @param modelPath resource path of the model
	 */
	public Text3d(String text)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_TEXT3D;
		_text = text;
	}

	/**
	 * Creates a new Polygon drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param absFlags flags for setting position, size and rotation as absolutes
	 * @param c modulation color or binding
	 * @param modelPath resource path of the texture
	 */
	public Text3d(Object position, Object rotation, Object size, int absFlags, Object c, String texturePath, String text, IParsedExpression drawcondition)
	{
		super(Primitive3d.PRIMITIVE_TYPE_TEXT3D, position, rotation, size, absFlags, c, texturePath, drawcondition);
		_text = text;
	}
	
	/**
	 *  Set the primitive type (Disabled).
	 *  @param type The type to set.
	 */
	public void setType(int type)
	{
		throw new RuntimeException("Set type not supported: " + getClass().getCanonicalName());
	}
	
	/**
	 *  Returns the model path.
	 *  @return The model path.
	 */
	public String getText()
	{
		return _text;
	}
}


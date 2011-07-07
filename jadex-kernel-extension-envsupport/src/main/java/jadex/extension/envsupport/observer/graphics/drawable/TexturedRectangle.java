package jadex.extension.envsupport.observer.graphics.drawable;

import jadex.javaparser.IParsedExpression;

/**
 * 
 */
public class TexturedRectangle extends Primitive
{
	/** Texture path. */
	protected String			texturePath_;

	/** Texture ID for OpenGL operations. */
	//private int					texture_;

	/** Image for Java2D operations. */
	//private BufferedImage		image_;
	
	/** Composite for modulating in Java2D */
	//private Composite modComposite_;
	
	/** Current color value */
	//private Color currentColor_;

	/**
	 * Creates default TexturedRectangle.
	 * 
	 * @param texturePath resource path of the texture
	 */
	public TexturedRectangle(String texturePath)
	{
		super();
		type = Primitive.PRIMITIVE_TYPE_TEXTUREDRECTANGLE;
		texturePath_ = texturePath;
		//texture_ = 0;
		//image_ = null;
	}

	/**
	 * Creates a new TexturedRectangle drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param absFlags flags for setting position, size and rotation as absolutes
	 * @param c modulation color or binding
	 * @param texturePath resource path of the texture
	 */
	public TexturedRectangle(Object position, Object rotation, Object size, int absFlags, Object c, String texturePath, IParsedExpression drawcondition)
	{
		super(Primitive.PRIMITIVE_TYPE_TEXTUREDRECTANGLE, position, rotation, size, absFlags, c, drawcondition);
		texturePath_ = texturePath;
		//texture_ = 0;
		//image_ = null;
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
	 *  Returns the texture path.
	 *  @return The texture path.
	 */
	public String getTexturePath()
	{
		return texturePath_;
	}
}

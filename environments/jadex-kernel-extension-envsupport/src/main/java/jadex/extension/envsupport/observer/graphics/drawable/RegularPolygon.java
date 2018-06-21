package jadex.extension.envsupport.observer.graphics.drawable;

import jadex.javaparser.IParsedExpression;


public class RegularPolygon extends Primitive
{
	/** Vertex count. */
	private int			vertices_;

	/**
	 * Generates a size 1.0 triangle.
	 */
	public RegularPolygon()
	{
		super();
		type = PRIMITIVE_TYPE_REGULARPOLYGON;
		vertices_ = 3;
	}

	/**
	 * Generates a new RegularPolygon.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param absFlags flags for setting position, size and rotation as absolutes
	 * @param c the drawable's color or binding
	 * @param vertices number of vertices (corners)
	 */
	public RegularPolygon(Object position, Object rotation, Object size, int absFlags, Object c, int vertices, IParsedExpression drawcondition)
	{
		super(Primitive.PRIMITIVE_TYPE_REGULARPOLYGON, position, rotation, size, absFlags, c, drawcondition);
		vertices_ = vertices;
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
	 *  Returns the vertex count.
	 *  
	 *  @return The vertex count.
	 */
	public int getVertexCount()
	{
		return vertices_;
	}
}

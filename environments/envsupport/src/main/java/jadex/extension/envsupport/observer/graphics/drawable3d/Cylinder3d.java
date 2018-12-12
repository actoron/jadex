package jadex.extension.envsupport.observer.graphics.drawable3d;

import java.util.ArrayList;

import jadex.extension.envsupport.observer.graphics.drawable3d.special.SpatialControl;
import jadex.javaparser.IParsedExpression;


/**
 * @author 7willuwe
 */
public class Cylinder3d extends Primitive3d
{
	protected double	_radius;

	protected double	_height;


	/**
	 * @param radius
	 * @param height
	 */
	public Cylinder3d(double radius, double height)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_CYLINDER;
		_radius = radius;
		_height = height;
	}


	/**
	 * Generates a new Cylinder3d
	 * 
	 * @param position
	 * @param rotation
	 * @param size
	 * @param absFlags
	 * @param c
	 * @param texturePath
	 * @param radius
	 * @param height
	 * @param drawcondition
	 */
	public Cylinder3d(Object position, Object rotation, Object size, int absFlags, Object c, String materialPath, String texturePath, double radius,
			double height, IParsedExpression drawcondition, String shadowtype, ArrayList<SpatialControl> controler)
	{
		super(Primitive3d.PRIMITIVE_TYPE_CYLINDER, position, rotation, size, absFlags, c, materialPath, texturePath, drawcondition, shadowtype, controler);
		_radius = radius;
		_height = height;
	}


	/**
	 * @param radius
	 */
	public void setRadius(float radius)
	{
		_radius = radius;

	}


	/**
	 * @param height
	 */
	public void setHeight(float height)
	{
		_height = height;

	}

	/**
	 */
	public double getRadius()
	{
		return _radius;
	}


	/**
	 */
	public double getHeight()
	{
		return _height;
	}

}

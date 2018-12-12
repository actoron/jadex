package jadex.extension.envsupport.observer.graphics.drawable3d;

import jadex.javaparser.IParsedExpression;


/**
 * @author 7willuwe
 *
 */
public class PointLight3d extends Primitive3d
{
	protected double	radius;


	/**
	 * @param radius
	 * @param height
	 */
	public PointLight3d(double radius)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_CYLINDER;
		this.radius = radius;
	}




	/**
	 * Generates a new PointLight3d
	 * 
	 * @param position
	 * @param rotation
	 * @param color
	 * @param radius
	 * @param drawcondition
	 */
	public PointLight3d(Object position, Object color, double radius,  IParsedExpression drawcondition)
	{
		super(Primitive3d.PRIMITIVE_TYPE_POINTLIGHT, position, color, drawcondition);
		this.radius = radius;
//		System.out.println("point light position " + position + color + radius);
	}




	/**
	 * @return the radius
	 */
	public double getRadius() {
		return radius;
	}




	/**
	 * @param radius the radius to set
	 */
	public void setRadius(double radius) {
		this.radius = radius;
	}

}

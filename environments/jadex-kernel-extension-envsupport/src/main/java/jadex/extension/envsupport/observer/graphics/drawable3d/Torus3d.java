package jadex.extension.envsupport.observer.graphics.drawable3d;

import java.util.ArrayList;

import jadex.extension.envsupport.observer.graphics.drawable3d.special.SpatialControl;
import jadex.javaparser.IParsedExpression;


public class Torus3d extends Primitive3d
{
	protected double	_innerRadius;

	protected double	_outerRadius;

	protected int		_circleSamples;

	protected int		_radialSamples;

	/**
	 * Creates default Tube.
	 * 
	 * @param outerRadius
	 * @param innerRadius
	 * @param height
	 */
	public Torus3d(double radius, double innerRadius, double outerRadius,
			int circleSamples, int radialSamples)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_TORUS;
		_innerRadius = innerRadius;
		_outerRadius = outerRadius;
		_circleSamples = circleSamples;
		_radialSamples = radialSamples;
	}

	/**
	 * Creates a new Cylinder3d drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param absFlags flags for setting position, size and rotation as
	 *        absolutes
	 * @param c modulation color or binding
	 * @param radius
	 * @param samples
	 * @param planes
	 */


	public Torus3d(Object position, Object rotation, Object size, int absFlags,
			Object c, String materialpath, String texturePath, double innerRadius,
			double outerRadius, int circleSamples, int radialSamples,
			IParsedExpression drawcondition, String shadowtype , ArrayList<SpatialControl> controler)
	{
		super(Primitive3d.PRIMITIVE_TYPE_TORUS, position, rotation, size,
				absFlags, c, materialpath, texturePath, drawcondition, shadowtype, controler);
		_innerRadius = innerRadius;
		_outerRadius = outerRadius;
		_circleSamples = circleSamples;
		_radialSamples = radialSamples;
	}

	/**
	 * @return the _innerRadius
	 */
	public double getInnerRadius()
	{
		return _innerRadius;
	}

	/**
	 * @param _innerRadius the _innerRadius to set
	 */
	public void setInnerRadius(double innerRadius)
	{
		_innerRadius = innerRadius;
	}

	/**
	 * @return the _outerRadius
	 */
	public double getOuterRadius()
	{
		return _outerRadius;
	}

	/**
	 * @param _outerRadius the _outerRadius to set
	 */
	public void setOuterRadius(double outerRadius)
	{
		_outerRadius = outerRadius;
	}

	/**
	 * @return the _circleSamples
	 */
	public int getCircleSamples()
	{
		return _circleSamples;
	}

	/**
	 * @param _circleSamples the _circleSamples to set
	 */
	public void setCircleSamples(int circleSamples)
	{
		_circleSamples = circleSamples;
	}

	/**
	 * @return the _radialSamples
	 */
	public int getRadialSamples()
	{
		return _radialSamples;
	}

	/**
	 * @param _radialSamples the _radialSamples to set
	 */
	public void setRadialSamples(int radialSamples)
	{
		_radialSamples = radialSamples;
	}


}

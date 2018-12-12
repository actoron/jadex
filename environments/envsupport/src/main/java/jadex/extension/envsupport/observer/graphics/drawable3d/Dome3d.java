package jadex.extension.envsupport.observer.graphics.drawable3d;

import java.util.ArrayList;

import jadex.extension.envsupport.observer.graphics.drawable3d.special.SpatialControl;
import jadex.javaparser.IParsedExpression;


public class Dome3d extends Primitive3d
{
	protected double	_radius;

	protected int		_samples;

	protected int		_planes;


	/**
	 * 
	 * Generates a new Dome3d
	 * 
	 * @param radius
	 * @param samples
	 * @param planes
	 */
	public Dome3d(double radius, int samples, int planes)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_DOME;
		_radius = radius;
		_samples = samples;
		_planes = planes;
	}


	/**
	 * Generates a new Dome3d
	 * 
	 * @param position
	 * @param rotation
	 * @param size
	 * @param absFlags
	 * @param c
	 * @param texturePath
	 * @param radius
	 * @param samples
	 * @param planes
	 * @param drawcondition
	 */
	public Dome3d(Object position, Object rotation, Object size, int absFlags,
			Object c, String materialpath, String texturePath, double radius, int samples,
			int planes, IParsedExpression drawcondition, String shadowtype, ArrayList<SpatialControl> controler)
	{
		super(Primitive3d.PRIMITIVE_TYPE_DOME, position, rotation, size,
				absFlags, c, materialpath, texturePath, drawcondition, shadowtype, controler);
		_radius = radius;
		_samples = samples;
		_planes = planes;
	}


	/**
	 * @param radius
	 */
	public void setRadius(float radius)
	{
		_radius = radius;

	}

	/**
	 */
	public double getRadius()
	{
		return _radius;
	}

	/**
	 */
	public int getPlanes()
	{
		return _planes;
	}

	/**
	 * @param planes
	 */
	public void setPlanes(int planes)
	{
		_planes = planes;
	}

	/**
	 */
	public int getSamples()
	{
		return _samples;
	}

	/**
	 * @param samples
	 */
	public void setSamples(int samples)
	{
		_samples = samples;
	}


}

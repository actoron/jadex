package jadex.extension.envsupport.observer.graphics.drawable3d;

import jadex.javaparser.IParsedExpression;

/**
 * 
 */
public class Object3d extends Primitive3d
{
	/** Model path. */
	protected String			_modelPath;

	/**
	 * Creates default Polygon.
	 * 
	 * @param modelPath resource path of the model
	 */
	public Object3d(String modelPath)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_OBJECT3D;
		_modelPath = modelPath;
	}
	/**
	 * 
	 * Creates a new Polygon drawable.
	 * 
	 * @param position
	 * @param rotation
	 * @param size
	 * @param absFlags
	 * @param c
	 * @param modelPath
	 * @param texturePath
	 * @param drawcondition
	 */
	public Object3d(Object position, Object rotation, Object size, int absFlags, Object c, String modelPath, String texturePath, IParsedExpression drawcondition)
	{
		super(Primitive3d.PRIMITIVE_TYPE_OBJECT3D, position, rotation, size, absFlags, c, texturePath, drawcondition);
		_modelPath = modelPath;
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
	 * @return the _modelPath
	 */
	public String getModelPath()
	{
		return _modelPath;
	}

	/**
	 * @param _modelPath the _modelPath to set
	 */
	public void setModelPath(String modelPath)
	{
		this._modelPath = modelPath;
	}
	

}


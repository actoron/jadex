package jadex.extension.envsupport.observer.graphics.drawable3d;

import jadex.javaparser.IParsedExpression;

/**
 * 
 */
public class Object3d extends Primitive3d
{
	/** Model path. */
	protected String			_modelPath;
	
	/** Light Materials - to deal with possible render Errors  */
	protected boolean _hasLightMaterials;

	/**
	 * Creates default Polygon.
	 * 
	 * @param modelPath resource path of the model
	 */
	public Object3d(String modelPath, boolean hasLightMaterials)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_OBJECT3D;
		_modelPath = modelPath;
		_hasLightMaterials = hasLightMaterials;
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
	public Object3d(Object position, Object rotation, Object size, int absFlags, Object c, String modelPath, String texturePath, boolean hasLightMaterials, IParsedExpression drawcondition)
	{
		super(Primitive3d.PRIMITIVE_TYPE_OBJECT3D, position, rotation, size, absFlags, c, texturePath, drawcondition);
		_modelPath = modelPath;
		_hasLightMaterials = hasLightMaterials;
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
	/**
	 * @return the _hasLightMaterials
	 */
	public boolean isHasLightMaterials() {
		return _hasLightMaterials;
	}
	/**
	 * @param _hasLightMaterials the _hasLightMaterials to set
	 */
	public void setHasLightMaterials(boolean hasLightMaterials) {
		this._hasLightMaterials = hasLightMaterials;
	}
	

}


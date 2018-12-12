package jadex.extension.envsupport.observer.graphics.drawable3d;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import jadex.extension.envsupport.observer.graphics.drawable3d.special.Animation;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.Materialfile;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.SpatialControl;
import jadex.javaparser.IParsedExpression;


/**
 * 
 */
public class Object3d extends Primitive3d
{
	/** Model path. */
	protected String					_modelPath;

	/** Light Materials - to deal with possible render Errors */
	protected boolean					_hasLightMaterials;

	protected ArrayList<Animation>		_animations;

	protected ArrayList<Materialfile>	materials;

	protected TreeSet<String>			_channels;

	protected boolean					_rigDebug;

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
	public Object3d(Object position, Object rotation, Object size, int absFlags, Object c, String modelPath, String materialpath, String texturePath,
			boolean hasLightMaterials, boolean rigDebug, IParsedExpression drawcondition, String shadowtype, List<Animation> animations,
			List<Materialfile> materials, ArrayList<SpatialControl> controler)
	{
		super(Primitive3d.PRIMITIVE_TYPE_OBJECT3D, position, rotation, size, absFlags, c, materialpath, texturePath, drawcondition, shadowtype, controler);
		_modelPath = modelPath;
		_hasLightMaterials = hasLightMaterials;
		_rigDebug = rigDebug;
		_animations = (ArrayList<Animation>)animations;
		_channels = new TreeSet<String>();
		this.materials = (ArrayList<Materialfile>)materials;
		if(_animations != null)
		{
			for(Animation a : _animations)
			{
				_channels.add(a.getChannel());
			}
		}

		System.out.println("_channels:" + _channels.toString() + " Size: " + _channels.size());
	}


	/**
	 * Set the primitive type (Disabled).
	 * 
	 * @param type The type to set.
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
	public boolean isHasLightMaterials()
	{
		return _hasLightMaterials;
	}

	/**
	 * @param _hasLightMaterials the _hasLightMaterials to set
	 */
	public void setHasLightMaterials(boolean hasLightMaterials)
	{
		this._hasLightMaterials = hasLightMaterials;
	}

	/**
	 * @return the _animations
	 */
	public ArrayList<Animation> getAnimations()
	{
		return _animations;
	}

	/**
	 * @param _animations the _animations to set
	 */
	public void setAnimations(ArrayList<Animation> _animations)
	{
		this._animations = _animations;
	}

	/**
	 * @return the _channels
	 */
	public TreeSet<String> getChannels()
	{
		return _channels;
	}

	/**
	 * @param _channels the _channels to set
	 */
	public void setChannels(TreeSet<String> channels)
	{
		this._channels = channels;
	}

	/**
	 * @return the _rigDebug
	 */
	public boolean isRigDebug()
	{
		return this._rigDebug;
	}

	/**
	 * @param _rigDebug the _rigDebug to set
	 */
	public void setRigDebug(boolean rigDebug)
	{
		this._rigDebug = rigDebug;
	}

	public ArrayList<Materialfile> getMaterials()
	{
		return materials;
	}

	public void setMaterials(ArrayList<Materialfile> materials)
	{
		this.materials = materials;
	}


}

package jadex.extension.envsupport.observer.graphics.drawable3d;

/**
 *  Helper class for terrain
 */
public class TerrainTexture
{
	String _name;
	String _texture;
	float _scale;
	
	public TerrainTexture(String name, String texture, float scale)
	{
		_name = name;
		_texture = texture;
		_scale = scale;
	}
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * @return the _texture
	 */
	public String getTexture()
	{
		return _texture;
	}

	/**
	 * @param _texture the _texture to set
	 */
	public void setTexture(String _texture)
	{
		this._texture = _texture;
	}

	/**
	 * @return the _scale
	 */
	public float getScale()
	{
		return _scale;
	}

	/**
	 * @param _scale the _scale to set
	 */
	public void setScale(float _scale)
	{
		this._scale = _scale;
	}

	
}

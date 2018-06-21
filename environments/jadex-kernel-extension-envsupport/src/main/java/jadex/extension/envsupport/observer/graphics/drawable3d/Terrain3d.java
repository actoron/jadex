package jadex.extension.envsupport.observer.graphics.drawable3d;

import java.util.ArrayList;


/**
 * 
 */
public class Terrain3d extends Primitive3d
{
	protected boolean _isRnd;
	
	protected String			_terrapath;

	/** Alphamap for Texture */
	protected String			_alphamap;

	protected String			_heightmap;
	
	protected int _patchsize;
	
	protected int _picsize;
	
	protected int _tiles;
	protected int _iterations;
	protected int _minradius;
	protected int _maxradius;
	protected int _seed;
	
	protected String _shadowtype;
	

	
	protected ArrayList<TerrainTexture> _terrainTexture = new ArrayList<TerrainTexture>(); 


	/**
	 * Creates default Polygon.
	 * 
	 * @param modelPath resource path of the model
	 */
	public Terrain3d(String terrapath, String alphamap, String heightmap,
			String texture01, String texture02, String texture03,
			String texture04, String texture05, Integer tex01val, Integer tex02val,
			Integer tex03val, Integer tex04val, Integer tex05val, int patchsize, int picsize, String shadowtype)
	{
		super();
		type = Primitive3d.PRIMITIVE_TYPE_TERRAIN;

		_terrapath = terrapath;
		_alphamap = alphamap;
		
		_heightmap = heightmap;
		_terrainTexture.add(new TerrainTexture("Tex1", texture01!=null? texture01: "", tex01val!=null? tex01val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex2", texture02!=null? texture02: "", tex02val!=null? tex02val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex3", texture03!=null? texture03: "", tex03val!=null? tex03val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex4", texture04!=null? texture04: "", tex04val!=null? tex04val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex5", texture05!=null? texture05: "", tex05val!=null? tex05val.floatValue(): 1f));
		_picsize = picsize;
		_patchsize = patchsize;
		_shadowtype = shadowtype;

	}

	/**
	 * Creates a new Polygon drawable.
	 * 
	 * @param position position or position-binding
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param shadowtype 
	 * @param absFlags flags for setting position, size and rotation as
	 *        absolutes
	 * @param c modulation color or binding
	 * @param modelPath resource path of the texture
	 */
	public Terrain3d(Object position, Object rotation, Object size,
			String terrapath, String alphamap, String heightmap,
			String texture01, String texture02, String texture03,
			String texture04, String texture05, Integer tex01val, Integer tex02val,
			Integer tex03val, Integer tex04val, Integer tex05val, int patchsize, int picsize, String shadowtype)
	{
		super(Primitive3d.PRIMITIVE_TYPE_TERRAIN, position, rotation, size, null);
		
		_isRnd = false;
		_terrapath = terrapath;
		_alphamap = alphamap;
		_heightmap = heightmap;
		_terrainTexture.add(new TerrainTexture("Tex1", texture01!=null? texture01: "", tex01val!=null? tex01val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex2", texture02!=null? texture02: "", tex02val!=null? tex02val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex3", texture03!=null? texture03: "", tex03val!=null? tex03val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex4", texture04!=null? texture04: "", tex04val!=null? tex04val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex5", texture05!=null? texture05: "", tex05val!=null? tex05val.floatValue(): 1f));
		_picsize = picsize;
		_patchsize = patchsize;
		_shadowtype = shadowtype;
	}

	public Terrain3d(Object position, Object rotation, Object size,
			Integer tiles, Integer iterations, Integer minradius,
			Integer maxradius, Integer seed, String terrapath, String alphamap,
			String texture01, String texture02, String texture03,
			String texture04, String texture05, Integer tex01val,
			Integer tex02val, Integer tex03val, Integer tex04val,
			Integer tex05val, String shadowtype)
	{
		super(Primitive3d.PRIMITIVE_TYPE_TERRAIN, position, rotation, size, null);
		_isRnd = true;
		_terrapath = terrapath;
		_alphamap = alphamap;
		_terrainTexture.add(new TerrainTexture("Tex1", texture01!=null? texture01: "", tex01val!=null? tex01val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex2", texture02!=null? texture02: "", tex02val!=null? tex02val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex3", texture03!=null? texture03: "", tex03val!=null? tex03val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex4", texture04!=null? texture04: "", tex04val!=null? tex04val.floatValue(): 1f));
		_terrainTexture.add(new TerrainTexture("Tex5", texture05!=null? texture05: "", tex05val!=null? tex05val.floatValue(): 1f));
		
		_tiles = tiles;
		_iterations = iterations;
		_minradius = minradius;
		_maxradius = maxradius;
		_seed = seed;
		_shadowtype = shadowtype;
	}

	/**
	 * Set the primitive type (Disabled).
	 * 
	 * @param type The type to set.
	 */
	public void setType(int type)
	{
		throw new RuntimeException("Set type not supported: "
				+ getClass().getCanonicalName());
	}


	/**
	 * @return the alphamap
	 */
	public String getAlphamap()
	{
		return _alphamap;
	}

	/**
	 * @param alphamap the alphamap to set
	 */
	public void setAlphamap(String alphamap)
	{
		this._alphamap = alphamap;
	}

	/**
	 * @return the heightmap
	 */
	public String getHeightmap()
	{
		return _heightmap;
	}

	/**
	 * @param heightmap the heightmap to set
	 */
	public void setHeightmap(String heightmap)
	{
		this._heightmap = heightmap;
	}


	/**
	 * @return the _terrapath
	 */
	public String getTerrapath()
	{
		return _terrapath;
	}

	/**
	 * @param _terrapath the _terrapath to set
	 */
	public void setTerrapath(String _terrapath)
	{
		this._terrapath = _terrapath;
	}

	/**
	 * @return the _terrainTexture
	 */
	public ArrayList<TerrainTexture> getTextures()
	{
		return _terrainTexture;
	}

	/**
	 * @param _terrainTexture the _terrainTexture to set
	 */
	public void setTextures(ArrayList<TerrainTexture> _terrainTexture)
	{
		this._terrainTexture = _terrainTexture;
	}

	/**
	 * @return the patchsize
	 */
	public int getPatchsize()
	{
		return _patchsize;
	}

	/**
	 * @param patchsize the patchsize to set
	 */
	public void setPatchsize(int patchsize)
	{
		this._patchsize = patchsize;
	}

	/**
	 * @return the picsize
	 */
	public int getPicsize()
	{
		return _picsize;
	}

	/**
	 * @param picsize the picsize to set
	 */
	public void setPicsize(int picsize)
	{
		this._picsize = picsize;
	}

	/**
	 * @return the isRnd
	 */
	public boolean isRnd()
	{
		return _isRnd;
	}

	/**
	 * @param isRnd the isRnd to set
	 */
	public void setRnd(boolean isRnd)
	{
		this._isRnd = isRnd;
	}

	/**
	 * @return the tiles
	 */
	public int getTiles()
	{
		return _tiles;
	}

	/**
	 * @param tiles the tiles to set
	 */
	public void setTiles(int tiles)
	{
		this._tiles = tiles;
	}

	/**
	 * @return the iterations
	 */
	public int getIterations()
	{
		return _iterations;
	}

	/**
	 * @param iterations the iterations to set
	 */
	public void setIterations(int iterations)
	{
		this._iterations = iterations;
	}

	/**
	 * @return the minradius
	 */
	public int getMinradius()
	{
		return _minradius;
	}

	/**
	 * @param minradius the minradius to set
	 */
	public void setMinradius(int minradius)
	{
		this._minradius = minradius;
	}

	/**
	 * @return the maxradius
	 */
	public int getMaxradius()
	{
		return _maxradius;
	}

	/**
	 * @param maxradius the maxradius to set
	 */
	public void setMaxradius(int maxradius)
	{
		this._maxradius = maxradius;
	}

	/**
	 * @return the seed
	 */
	public int getSeed()
	{
		return _seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(int seed)
	{
		this._seed = seed;
	}


}

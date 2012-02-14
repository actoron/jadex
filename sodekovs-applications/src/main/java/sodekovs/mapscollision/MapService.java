package sodekovs.mapscollision;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Int;
import jadex.extension.envsupport.math.Vector2Int;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

public class MapService
{

	private ArrayList<IVector2> _safeLocations = new ArrayList<IVector2>();
	private boolean[][] _map;
	private static MapService _myself;
	private static Object LOCK = new Object();

	private ArrayList<IVector2> _validPositions = new ArrayList<IVector2>();

	private MapService()
	{
		System.out.println("Loading map in MapService...");

		// File urlImage = new
		// File("/Users/holger/Documents/workspace-evaa/sandbox/src/map.png");
		File workspacefolder = new File("../");
		File urlImage = null;
		for (File f : workspacefolder.listFiles())
		{
			if (f.isDirectory())
			{
				File picture = new File(f.getAbsolutePath() + "./src/main/java/sodekovs/mapscollision/Untitled2.png");
				if (picture.exists())
				{
					urlImage = picture;
					break;
				}
			}
		}

		BufferedImage image;
		try
		{
			image = ImageIO.read(urlImage);

			int height = image.getHeight();
			int width = image.getWidth();

			_map = new boolean[height][width];

			for (int h = 0; h < height; ++h)
			{
				for (int w = 0; w < width; ++w)
				{
					int c = image.getRGB(w, h);
					int red = (c & 0x00ff0000) >> 16;
					int green = (c & 0x0000ff00) >> 8;
					int blue = c & 0x000000ff;
					// and the Java Color is ...
					Color color = new Color(red, green, blue);
					if (color.equals(Color.black))
					{
						_map[h][w] = false;
					}
					else
					{
						_map[h][w] = true;
						// Als gültige Position speichern
						_validPositions.add(new Vector2Int(w, h));

						if (color.equals(Color.RED))
						{
							// _spawnMap.get(Spawn.Type.Hospital).add(new
							// Vector2Int(w, h));
						}
						else if (color.equals(Color.GREEN))
						{
							// _spawnMap.get(Spawn.Type.Zombie).add(new
							// Vector2Int(w, h));
						}
						else if (color.equals(Color.BLUE))
						{
							// _spawnMap.get(Spawn.Type.Soldier).add(new
							// Vector2Int(w, h));
						}
						else if (color.equals(new Color(255, 0, 255)))
						{
							// _spawnMap.get(Spawn.Type.Scout).add(new
							// Vector2Int(w, h));
						}
						else if (color.equals(new Color(255, 255, 0)))
						{
							_safeLocations.add(new Vector2Int(w, h));
						}
					}
				}
			}

			System.out.println("Karte erfolgreich geladen. Hospitals:"
			// + getSpawnsForType(Spawn.Type.Hospital).size() +
			// " Zombie spawns:"
			// + getSpawnsForType(Spawn.Type.Zombie).size() + " Soldier Spawns:"
			// + getSpawnsForType(Spawn.Type.Soldier).size() + " Scout Spawns:"
			// + getSpawnsForType(Spawn.Type.Scout).size() + " Safe Locations:"
					+ _safeLocations.size());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Fehler beim Laden der Karte");
		}

	}

	public static MapService getInstance()
	{
		synchronized (LOCK)
		{
			if (_myself == null)
			{
				_myself = new MapService();
			}
			return _myself;
		}
	}

	/**
	 * Returns the Map.Width
	 * 
	 * @return
	 */
	public int getWidth()
	{
		return _map[0].length;
	}

	/**
	 * Returns the Map.Height
	 * 
	 * @return
	 */
	public int getHeight()
	{
		return _map.length;
	}

	/**
	 * Checks if the given Location is a passable Field
	 * 
	 * @param location
	 * @return true if passable, else false
	 */
	public boolean isPassable(IVector2 location)
	{
		int x = location.getXAsInteger();
		int y = location.getYAsInteger();

		// Make x and y positive and below picture.width/height
		if (x >= _map[0].length || x < 0)
		{
			x = (x + getHeight()) % getHeight();
		}
		if (y >= _map.length || y < 0)
		{
			y = (y + getWidth()) % getWidth();
		}

		return _map[y][x];
	}

	public boolean isPosInMap(IVector2 pos)
	{
		int x = pos.getXAsInteger();
		int y = pos.getYAsInteger();

		if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight())
		{
			return true;
		}
		return false;
	}

	// public Collection<IVector2> getSpawnsForType(Spawn.Type type)
	// {
	// // return new ArrayList<IVector2>(_spawnMap.get(type));
	// }

	public ArrayList<IVector2> getSafeLocations()
	{
		return new ArrayList<IVector2>(_safeLocations);
	}

	/**
	 * Returns the next closest valid Spawning position
	 * 
	 * @param fromhere
	 * @return Valid Spawn Position
	 */
	public IVector2 getValidPosition(IVector2 fromhere)
	{
		IVector2 result = null;
		for (IVector2 vec : _validPositions)
		{
			if (result == null || getDistance(fromhere, vec) < getDistance(fromhere, result))
			{
				result = vec.copy();
			}
		}
		return result;
	}

	/**
	 * Returns all valid Positions
	 * 
	 * @return
	 */
	public List<IVector2> getValidPositions()
	{
		return Collections.unmodifiableList(_validPositions);
	}

	/**
	 * Returns the Distance between the two Coordinates
	 */
	private int getDistance(IVector2 from, IVector2 to)
	{
		return from.getDistance(to).getAsInteger();
	}

	/**
	 * Returns a copy of the passable coordinates
	 * 
	 * @return
	 */
	public boolean[][] getMap()
	{
		boolean[][] result = new boolean[_map.length][_map[0].length];

		for (int i = 0; i < _map.length; i++)
		{
			result[i] = _map[i].clone();
		}
		return result;
	}

	/**
	 * Returns an integer-array described by {@link MapConstant}
	 * 
	 * @return
	 */
	public int[][] getMapInt()
	{
		int[][] result = new int[getHeight()][getWidth()];
		for (int x = 0; x < getWidth(); x++)
			for (int y = 0; y < getHeight(); y++)
			{
				result[y][x] = getMap()[y][x] ? MapConstant.WALKABLE : MapConstant.NOTWALKABLE;
			}
		return result;
	}

	/**
	 * Returns a random spot within the area from 30 to range away the given
	 * range. The minimum range is 30.
	 * 
	 * @param myPos
	 *            position of your spot
	 * @param fromRange
	 *            minimal distance to random spot
	 * @param toRange
	 *            min range is 30 where a spot can be chosen
	 * @return chosen spot
	 */
	public static IVector2 getNearbyRandomPassablePos(IVector2 myPos, int fromRange, int toRange)
	{
		toRange = Math.max(fromRange, toRange);
		MapService service = MapService.getInstance();
		IVector2 pos = new Vector2Int();
		IVector2 change = new Vector2Int(0, 0);
		do
		{
			change.randomX(new Vector1Int(fromRange), new Vector1Int(toRange));
			change.randomY(new Vector1Int(fromRange), new Vector1Int(toRange));
			pos = (Math.random() > 0.5 ? myPos.add(change) : myPos.subtract(change));

			if (pos.getXAsInteger() < 0)
			{
				pos = pos.add(new Vector2Int(service.getWidth(), 0));
			}
			if (pos.getXAsInteger() >= service.getWidth())
			{
				pos = pos.subtract(new Vector2Int(service.getWidth(), 0));
			}

			if (pos.getYAsInteger() < 0)
			{
				pos = pos.add(new Vector2Int(0, service.getHeight()));
			}
			if (pos.getYAsInteger() >= service.getHeight())
			{
				pos = pos.subtract(new Vector2Int(0, service.getHeight()));
			}
		}
		while (!service.isPassable(pos) || !service.isPosInMap(pos));

		return myPos;
	}
}

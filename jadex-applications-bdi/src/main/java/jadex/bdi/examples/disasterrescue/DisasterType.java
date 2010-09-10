package jadex.bdi.examples.disasterrescue;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector2Double;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *  Helper class for random disaster generation.
 */
public class DisasterType
{
	//-------- constants --------
	
	/** The disaster types. */
	public static final DisasterType[]	DISASTER_TYPES	= new DisasterType[]
	{
		new DisasterType("Car Crash", 1, 0.05,  new int[]{15, 25}, 0.2, 0.05, 0), 
		new DisasterType("Explosion", 0.3, 0.15,  new int[]{25, 40}, 0.4, 1, 0.1), 
		new DisasterType("Chemical Leakage", 0.3, 0.5,  new int[]{25, 40}, 0.4, 0, 0.5), 
		new DisasterType("Earthquake", 0.05, 1,  new int[]{100, 150}, 0.2, 0.05, 0.05) 
	};
	
	/** The random number generator. */
	protected static final Random	random	= new Random(23);
	
	//-------- attributes --------
	
	/** The type name. */
	protected String	name;

	/** The occurrence probability. */
	protected double	occurrence;

	/** The severity probability. */
	protected double	severe;

	/** The size range [min, max]. */
	protected int[]	size;

	/** The average victims number, relative to size (0 = off). */
	protected double	victims;
	
	/** The average fire number, relative to size (0 = off). */
	protected double	fire;
	
	/** The average chemical number, relative to size (0 = off). */
	protected double	chemicals;
	
	//-------- constructors --------
	
	/**
	 *  Create a new disaster type.
	 */
	public DisasterType(String name, double occurrence, double severe, int[] size, double victims, double fire, double chemicals)
	{
		this.name	= name;
		this.occurrence	= occurrence;
		this.severe	= severe;
		this.size	= size;
		this.victims	= victims;
		this.fire	= fire;
		this.chemicals	= chemicals;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 */
	public String getName()
	{
		return name;
	}
		
	/**
	 *  Get the occurrence probability.
	 */
	public double getOccurrence()
	{
		return occurrence;
	}
	
	/**
	 *  Get the severity probability.
	 */
	public double getSevere()
	{
		return severe;
	}

	/**
	 *  Get the name.
	 */
	public int[] getSize()
	{
		return size;
	}

	/**
	 *  Get the name.
	 */
	public double getVictims()
	{
		return victims;
	}

	/**
	 *  Get the name.
	 */
	public double getFire()
	{
		return fire;
	}

	/**
	 *  Get the name.
	 */
	public double getChemicals()
	{
		return chemicals;
	}
	
	//-------- static methods --------
	
	/**
	 *  Generate properties for a random disaster.
	 */
	public static Map	generateDisaster()
	{
		// Select disaster based on occurrence probability.
		int	index	= -1;
		while(index==-1)
		{
			index	= random.nextInt(DISASTER_TYPES.length);
			if(random.nextDouble()>DISASTER_TYPES[index].getOccurrence())
				index	= -1;
		}
		
		Map	ret	= new HashMap();
		ret.put("type", DISASTER_TYPES[index].getName());
		ret.put("severe", new Boolean(random.nextDouble()<DISASTER_TYPES[index].getSevere()));
		int[]	range	= DISASTER_TYPES[index].getSize();
		int	size	= range[0]+random.nextInt(range[1]-range[0]);
		ret.put("size", new Integer(size));
		
		// Use random +/- 25% for victims/fire/chemicals value
		ret.put("victims", new Integer(DISASTER_TYPES[index].getVictims()>0 ? (int)((0.75+random.nextDouble()/2)*DISASTER_TYPES[index].getVictims()*size): 0));
		ret.put("fire", new Integer(DISASTER_TYPES[index].getFire()>0 ? (int)((0.75+random.nextDouble()/2)*DISASTER_TYPES[index].getFire()*size): 0));
		ret.put("chemicals", new Integer(DISASTER_TYPES[index].getChemicals()>0 ? (int)((0.75+random.nextDouble()/2)*DISASTER_TYPES[index].getChemicals()*size): 0));
		return ret;
	}
	
	/**
	 *  Get the position of a fire at the given disaster.
	 */
	public static IVector2	getFireLocation(ISpaceObject disaster)
	{
		IVector2	center	= (IVector2)disaster.getProperty(Space2D.PROPERTY_POSITION);
		int	size	= ((Number)disaster.getProperty("size")).intValue();
		double	angle	= random.nextDouble()*Math.PI*2;
		double	x	= Math.cos(angle)*size/2 * 0.005;	// 0.005 = scale of drawsize in application.xml
		double	y	= Math.sin(angle)*size/2 * 0.005;	// 0.005 = scale of drawsize in application.xml
		double	range	= random.nextDouble();
		
		// Place fires at inner area of circle
		x	= x*0.2 + x*0.4*range;
		y	= x*0.2 + y*0.4*range;
		
		// Clip position at space borders
		x	+= center.getXAsDouble();
		y	+= center.getYAsDouble();
		x	= Math.max(Math.min(x, 1), 0);
		y	= Math.max(Math.min(y, 1), 0);
		
		return new Vector2Double(x, y);
	}
	
	/**
	 *  Get the position of chemicals at the given disaster.
	 */
	public static IVector2	getChemicalsLocation(ISpaceObject disaster)
	{
		return getFireLocation(disaster);
	}
}

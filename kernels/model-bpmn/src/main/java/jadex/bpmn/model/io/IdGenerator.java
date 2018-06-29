package jadex.bpmn.model.io;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import jadex.commons.Base64;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;

/**
 *  A configurable Id generator.
 *
 */
public class IdGenerator
{
	/** The default ID size. */
	protected static final int DEFAULT_ID_SIZE = 12; // 96 bits, 16 bytes in Base64, 
													 // approx. 1 trillionth chance of collision after generating
													 // almost 400 million IDs.
	
	/** Random number generator. */
	protected Random random;
	
	/** The selected id size. */
	protected int idsize;
	
	protected IResultCommand<String, byte[]> encoder;
	
	/**
	 *  Set of generated IDs to guarantee collision-free IDs.
	 */
	protected Set<String> generatedIds;
	
	/**
	 *  Creates a new ID generator.
	 */
	public IdGenerator()
	{
		this(DEFAULT_ID_SIZE, false);
	}
	
	/**
	 *  Creates a new ID generator.
	 *  
	 *  @param idsize Size of ID entropy in bytes, actual ID is slightly larger due to Base64 encoding.
	 */
	public IdGenerator(int idsize)
	{
		this(idsize, false);
	}
	
	/**
	 *  Creates a new ID generator.
	 *  
	 *  @param collisionfree If true, this will guarantee collision-free IDs. However, requires maintaining
	 *  					 a set of used IDs, increasing memory consumption. This option is unnecessary for
	 *  					 most use cases due to the extremely low probability of collisions at reasonable
	 *  					 ID sizes.
	 */
	public IdGenerator(boolean collisionfree)
	{
		this(DEFAULT_ID_SIZE, collisionfree);
	}
	
	/**
	 *  Creates a new ID generator.
	 *  
	 *  @param idsize Size of ID entropy in bytes, actual ID is slightly larger due to Base64 encoding.
	 *  @param collisionfree If true, this will guarantee collision-free IDs. However, requires maintaining
	 *  					 a set of used IDs, increasing memory consumption. This option is unnecessary for
	 *  					 most use cases due to the extremely low probability of collisions at reasonable
	 *  					 ID sizes.
	 */
	public IdGenerator(int idsize, boolean collisionfree)
	{
		random = new Random();
		this.idsize = idsize;
		if (collisionfree)
		{
			generatedIds = new HashSet<String>();
		}
		
		base64Mode();
	}
	
	/**
	 *  Generates an ID.
	 *  
	 *  @return The ID.
	 */
	public String generateId()
	{
		byte[] rawid = new byte[idsize];
		random.nextBytes(rawid);
		String id = encoder.execute(rawid);
		if (generatedIds != null)
		{
			while (generatedIds.contains(id))
			{
				random.nextBytes(rawid);
				id = encoder.execute(rawid);
			}
			generatedIds.add(id);
		}
		
		
		return id;
	}
	
	/**
	 *  Adds a used ID, only useful for collision-free mode.
	 *  
	 *  @param id The id.
	 */
	public void addUsedId(String id)
	{
		if (generatedIds != null)
		{
			generatedIds.add(id);
		}
	}
	
	/**
	 *  Removes a used ID, only useful in collision-free mode.
	 *  
	 *  @param id The ID.
	 */
	public void removeUsedId(String id)
	{
		if (generatedIds != null)
		{
			generatedIds.remove(id);
		}
	}
	
	/**
	 *  Switches the ID generator to BASE64 mode.
	 */
	public void base64Mode()
	{
		synchronized (this)
		{
			encoder = new IResultCommand<String, byte[]>()
			{
				public String execute(byte[] args)
				{
					String id = new String(Base64.toCharArray(args));
					return id;
				}
			};
		}
	}
	
	/**
	 *  Switches the ID generator to BASE16 mode.
	 */
	public void base16Mode()
	{
		synchronized (this)
		{
			encoder = new IResultCommand<String, byte[]>()
			{
				public String execute(byte[] args)
				{
					String id = SUtil.hex(args);
					return id;
				}
			};
		}
	}
	
	public static void main(String[] args)
	{
		IdGenerator idgen = new IdGenerator();
		for (int i = 0; i < 100; ++i)
		{
			System.out.println(idgen.generateId());
		}
		idgen.base16Mode();
		for (int i = 0; i < 100; ++i)
		{
			System.out.println(idgen.generateId());
		}
	}
}

package jadex.binary;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Level;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding Logging Level objects.
 *
 */
public class LoggingLevelCodec extends AbstractCodec
{
	Level[] DEFAULT_LEVELS = new Level[] {Level.OFF,
										  Level.SEVERE,
										  Level.WARNING,
										  Level.INFO,
										  Level.CONFIG,
										  Level.FINE,
										  Level.FINER,
										  Level.FINEST,
										  Level.ALL};
	
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(Level.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
		Level ret = null;
		
		// Check if default level
		if (context.readBoolean())
			ret = DEFAULT_LEVELS[context.readByte() & 0xFF];
		else
		{
			// Subclassed Level object
			String name = context.readString();
			int val = (int) context.readSignedVarInt();
			
			try
			{
				// Let's hope the Level subclass has this constructor...
				Constructor c = clazz.getDeclaredConstructor(new Class[] { String.class, int.class });
				c.setAccessible(true);
				ret = (Level) c.newInstance(new Object[] {name, val} );
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			
		}
		return ret;
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		Level level = (Level) object;
		int id = 0;
		
		// Check for applicable default Level
		while (id < DEFAULT_LEVELS.length && !DEFAULT_LEVELS[id].equals(level))
			++id;
		
		if (id < DEFAULT_LEVELS.length)
		{
			ec.writeBoolean(true);
			ec.write(new byte[] {(byte) id});
		}
		else
		{
			// Subclassed Level object
			ec.writeBoolean(false);
			String name = level.getName();
			int value = level.intValue();
			ec.writeSignedVarInt(value);
			ec.writeString(name);
		}
		
		return object;
	}
}

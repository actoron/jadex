package jadex.xml.writer;

import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.commons.SReflect;

/**
 * Factory to create XML Writers.
 */
public abstract class XMLWriterFactory
{
	// -------- attributes --------

	/** The instance of this factory */
	private static XMLWriterFactory INSTANCE;

	// -------- constructors --------
	/**
	 * Constructor.
	 */
	protected XMLWriterFactory()
	{
	}

	// -------- methods --------
	/**
	 * Returns the instance of this factory.
	 * 
	 * @return the factory instance
	 */
	public static XMLWriterFactory getInstance()
	{
		if(INSTANCE == null)
		{
			if(SReflect.isAndroid() && !SReflect.isAndroidTesting())
			{
				Class< ? > clz;
				try
				{
					clz = SReflect.classForName("jadex.xml.writer.XMLWriterFactoryAndroid", null);
					if(clz != null)
					{
						INSTANCE = (XMLWriterFactory)clz.newInstance();
					}
				}
				catch(ClassNotFoundException e)
				{
					Logger.getLogger("jadex").log(Level.WARNING, "XMLWriter not available.");
				}
				catch(InstantiationException e)
				{
					e.printStackTrace();
				}
				catch(IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				INSTANCE = new XMLWriterFactoryDesktop();
			}
		}
		return INSTANCE;
	}

	/**
	 *  Create a new reader (with genids=true and indent=true).
	 */
	public abstract AWriter createWriter();

	/**
	 * Create an XMLWRiter
	 * @param genIds flag for generating ids.
	 * @return the writer
	 */
	public abstract AWriter createWriter(boolean genIds);
	

	/**
	 * Creates a new default XML Reader.
	 * 
	 * @param genids
	 * @param indents
	 * @return reader
	 */
	public abstract AWriter createWriter(boolean genids, boolean indents);
	
	/**
	 * Creates a new default XML Reader.
	 * 
	 * @param genids
	 * @param indents
	 * @return reader
	 */
	public abstract AWriter createWriter(boolean genids, boolean indents, boolean newline);

}

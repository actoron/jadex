package jadex.xml.reader;

import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.commons.SReflect;
import jadex.xml.stax.XMLReporter;


/**
 * Factory to create XML Readers.
 */
public abstract class XMLReaderFactory
{

	// -------- attributes --------

	/** The instance of this factory */
	private static XMLReaderFactory	INSTANCE;

	// -------- constructors --------

	/** Constructor */
	protected XMLReaderFactory()
	{
	}

	// -------- methods --------
	/**
	 * Returns the instance of this factory.
	 * 
	 * @return the factory instance
	 */
	public static XMLReaderFactory getInstance()
	{
		if(INSTANCE == null)
		{
			if(SReflect.isAndroid() && !SReflect.isAndroidTesting())
			{
				Class< ? > clz;
				try
				{
					clz = SReflect.classForName("jadex.xml.reader.XMLReaderFactoryAndroid", null);
					if(clz != null)
					{
						INSTANCE = (XMLReaderFactory)clz.newInstance();
					}
				}
				catch(ClassNotFoundException e)
				{
					Logger.getLogger("jadex").log(Level.WARNING, "XMLReader not available.");
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
				INSTANCE = new XMLReaderFactoryDesktop();
			}
		}
		return INSTANCE;
	}

	/**
	 * Creates a new default XML Reader.
	 * 
	 * @return reader
	 */
	public abstract AReader createReader();

	/**
	 * Creates a new XML Reader
	 */
	public abstract AReader createReader(boolean bulklink);

	/**
	 * Creates a new XML Reader
	 */
	public abstract AReader createReader(boolean bulklink, boolean validate, XMLReporter reporter);

	/**
	 * Creates a new XML Reader
	 */
	public abstract AReader createReader(boolean bulklink, boolean validate, boolean coalescing, XMLReporter reporter);
}

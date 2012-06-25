package jadex.xml.writer;

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
		if (INSTANCE == null)
		{
			if (SReflect.isAndroid())
			{
				// INSTANCE = new XMLWriterFactoryAndroid();
			} else
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

}

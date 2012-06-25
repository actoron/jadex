package jadex.xml.writer;

import jadex.commons.SReflect;
import jadex.xml.stax.XMLReporter;

public abstract class XMLWriterFactory
{

	private static XMLWriterFactory INSTANCE;
	
	protected XMLWriterFactory(){}

	public static XMLWriterFactory getInstance()
	{
		if (INSTANCE == null) {
			if (SReflect.isAndroid())
			{
				//INSTANCE = new XMLWriterFactoryAndroid();
			} else
			{
				INSTANCE = new XMLWriterFactoryDesktop();
			}
		}
		return INSTANCE;
	}
	

	public abstract AWriter createWriter();
	
	public abstract AWriter createWriter(boolean genIds);
	
}

package jadex.xml.writer;

public class XMLWriterFactoryDesktop extends XMLWriterFactory
{

	@Override
	public AWriter createWriter()
	{
		return new Writer();
	}
	
	public AWriter createWriter(boolean genids) {
		return new Writer(genids);
	}
	
	public AWriter createWriter(boolean genids, boolean indents) {
		return new Writer(genids, indents);
	}
	
}

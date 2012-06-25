package jadex.xml.writer;

import java.io.OutputStream;

import jadex.xml.writer.AWriter;
import jadex.xml.writer.IObjectWriterHandler;

public class PullParserWriter extends AWriter
{
	
	private boolean genIds;

	public PullParserWriter()
	{
	}

	public PullParserWriter(boolean genIds)
	{
		this.genIds = genIds;
	}

	@Override
	public void write(IObjectWriterHandler handler, Object object, String encoding, OutputStream out, ClassLoader classloader, Object context) throws Exception
	{
		
	}

}

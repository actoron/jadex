package jadex.wfms.parametertypes;

import jadex.commons.Base64;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * Class representing a generic binary document used in workflows.
 */
public class Document
{
	/** File name of the document */
	private String fileName;
	
	/** String with encoded content */
	private String contString;
	
	public Document()
	{
	}
	
	public Document(File file) throws IOException
	{
		FileChannel inChannel = (new RandomAccessFile(file, "r")).getChannel();
		long size = inChannel.size();
		MappedByteBuffer inBuffer = inChannel.map(MapMode.READ_ONLY, 0, size);
		byte[] contentBuffer = new byte[(int) size];
		inBuffer.get(contentBuffer);
		inChannel.close();
		
		fileName = file.getName();
		encodeContent(contentBuffer);
	}
	
	public Document(String fileName, byte[] content)
	{
		this.fileName = fileName;
		encodeContent(content);
	}
	
	public byte[] retrieveContent()
	{
		try
		{
			return Base64.decode(contString.getBytes("US-ASCII"));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	public String getContString()
	{
		return contString;
	}
	
	public void setContString(String contString)
	{
		this.contString = contString;
	}
	
	private void encodeContent(byte[] content)
	{
		try
		{
			contString = new String(Base64.encode(content), "US-ASCII");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}
}

package jadex.base.service.deployment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import jadex.commons.Base64;

/**
 *  A file data is for transferring binary file content as byte[]
 *  via a base64 coded string.
 */
public class FileData
{
	/** The file name. */
	protected String filename;
	
	/** The file data. */
	protected byte[] data;
	
	/**
	 *  Create a new file data.
	 */
	public FileData()
	{
	}
	
	/**
	 *  Create new file data with content of a file.
	 */
	public FileData(File file)
	{
		this.filename = file.getName();
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			data = new byte[(int)file.length()];
			fis.read(data);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			throw new RuntimeException(e);
		}
		try
		{
			if(fis!=null)
				fis.close();
		}
		catch(Exception e)
		{
		}
	}

	/**
	 *  Write content to a file.
	 */
	public void writeFile(File file)
	{
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(file);
			fos.write(data);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			throw new RuntimeException(e);
		}
		try
		{
			if(fos!=null)
				fos.close();
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 *  Get the data as a transferable string.
	 *  @return the data string.
	 */
	public String getDataString()
	{
		String	ret	= null;
		if(data!=null)
		{
			ret	= new String(Base64.encode(data));
		}
		return ret;
	}

	/**
	 *  Set the data.
	 *  @param data The data to set.
	 */
	public void setDataString(String sdata)
	{
		this.data = Base64.decode(sdata.getBytes());
	}

	/**
	 *  Get the filename.
	 *  @return the filename.
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 *  Set the filename.
	 *  @param filename The filename to set.
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	
}

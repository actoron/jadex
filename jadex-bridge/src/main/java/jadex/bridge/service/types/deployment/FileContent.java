package jadex.bridge.service.types.deployment;

import jadex.commons.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 *  A file data is for transferring binary file content as byte[]
 *  via a base64 coded string.
 */
public class FileContent
{		
	/** The file name. */
	protected String filename;
	
	/** The file data. */
	protected byte[] data;
	
	
//	/** The fragment number (-1 for none). */
//	protected int fragment;
	
//	/** The number of fragments. */
//	protected int fragments;
	
//	/** The fragment size. */
//	protected int fragmentsize;
	
//	/** The file id. */
//	protected int fileid;
	
	/** The file size. */
	protected int size;
	
	/**
	 *  Create a new file data.
	 */
	public FileContent()
	{
	}
	
	/**
	 *  Create a new file data.
	 */
//	public FileContent(String filename, byte[] data, int fragment, int fragements, int fileid, int size, int fragmentsize)
	public FileContent(String filename, byte[] data, int size)
	{
		this.filename = filename;
		this.data = data;
//		this.fragment = fragment;
//		this.fragments = fragements;
//		this.fileid = fileid;
		this.size = size;
//		this.fragmentsize = fragmentsize;
	}
	
	/**
	 *  Create new file data with content of a file.
	 */
	public FileContent(File file)
	{
//		this.fragments = 1;
		this.filename = file.getName();
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			data = new byte[(int)file.length()];
			while(fis.read(data)!=-1);
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
	 *  Create fragmented file content objects.
	 */
	public static FileContent createFragment(FileInputStream fis, String filename, int fragmentsize, int size)
	{
		FileContent ret = null;
		
		try
		{
			byte[] data = new byte[fragmentsize];
			for(int res = fis.read(data); res!=-1 && res!=data.length; );
			ret = new FileContent(filename, data, size);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
//	/**
//	 *  Create fragmented file content objects.
//	 */
//	public static FileContent[] createFragments(File file, int fileid)
//	{
//		return createFragments(file, fileid, FRAGMENT_SIZE);
//	}
//	
//	/**
//	 *  Create fragmented file content objects.
//	 */
//	public static FileContent[] createFragments(File file, int fileid, int maxsize)
//	{
//		FileContent[] ret = null;
//		int len = (int)file.length();
//		int num = (int)(len/maxsize);
//		int last = (int)(len%maxsize);
//		int fragments = num + (last>0? 1: 0);
//		
//		try
//		{
//			FileInputStream fis = new FileInputStream(file);
//			ret = new FileContent[fragments];
//			
//			for(int i=0; i<num; i++)
//			{
//				byte[] data = new byte[maxsize];
//				for(int res = fis.read(data); res!=-1 && res!=data.length; );
//				ret[i] = new FileContent(file.getName(), data, i, fragments, fileid, len, maxsize);
//			}
//			
//			if(last>0)
//			{
//				byte[] data = new byte[last];
//				for(int res = fis.read(data); res!=-1 && res!=data.length; );
//				ret[fragments-1] = new FileContent(file.getName(), data, fragments-1, fragments, fileid, len, maxsize);
//			}
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Create fragmented file content objects.
//	 */
//	public static FileContent mergeFragments(FileContent[] fragments)
//	{
//		byte[] data = new byte[fragments[0].getSize()];
//		for(int i=0; i<fragments.length; i++)
//		{
//			System.arraycopy(fragments[i].data, 0, data, fragments[i].getFragment()*fragments[i].getFragmentSize(), fragments[i].data.length);
//		}
//		return new FileContent(fragments[0].getFilename(), data, 0, 1, 0, 0, 0);
//	}
	
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
			ret	= new String(Base64.encode(data), Charset.forName("UTF-8"));
		}
		return ret;
	}
	
	/**
	 *  Get the data.
	 *  @return the data.
	 */
	public byte[] getData()
	{
		return data;
	}

	/**
	 *  Set the data.
	 *  @param data The data to set.
	 */
	public void setDataString(String sdata)
	{
		this.data = Base64.decode(sdata.getBytes(Charset.forName("UTF-8")));
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

//	/**
//	 *  Get the fragment.
//	 *  @return the fragment.
//	 */
//	public int getFragment()
//	{
//		return fragment;
//	}

//	/**
//	 *  Set the fragment.
//	 *  @param fragment The fragment to set.
//	 */
//	public void setFragment(int fragment)
//	{
//		this.fragment = fragment;
//	}
//
//	/**
//	 *  Get the fragments.
//	 *  @return the fragments.
//	 */
//	public int getFragments()
//	{
//		return fragments;
//	}
//
//	/**
//	 *  Set the fragments.
//	 *  @param fragments The fragments to set.
//	 */
//	public void setFragments(int fragments)
//	{
//		this.fragments = fragments;
//	}

//	/**
//	 *  Get the fileid.
//	 *  @return the fileid.
//	 */
//	public int getFileId()
//	{
//		return fileid;
//	}
//
//	/**
//	 *  Set the fileid.
//	 *  @param fileid The fileid to set.
//	 */
//	public void setFileId(int fileid)
//	{
//		this.fileid = fileid;
//	}

	/**
	 *  Get the size.
	 *  @return the size.
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 *  Set the size.
	 *  @param size The size to set.
	 */
	public void setSize(int size)
	{
		this.size = size;
	}

//	/**
//	 *  Get the fragmentsize.
//	 *  @return the fragmentsize.
//	 */
//	public int getFragmentSize()
//	{
//		return fragmentsize;
//	}
//
//	/**
//	 *  Set the fragmentsize.
//	 *  @param fragmentsize The fragmentsize to set.
//	 */
//	public void setFragmentSize(int fragmentsize)
//	{
//		this.fragmentsize = fragmentsize;
//	}
}

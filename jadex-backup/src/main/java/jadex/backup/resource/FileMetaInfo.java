package jadex.backup.resource;

import jadex.bridge.IComponentIdentifier;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *  Meta information about a file. This meta information is not
 *  physically backed by a file but is written to the metadata repo.
 *  
 *  Using a filedata the meta info is linked to a physical file. 
 */
public class FileMetaInfo
{
	//-------- attributes --------
	
	/** The file location relative to the resource root (using '/' as separator char). */
	protected FileData	data;
	
	/** True, if the file or directory exists. */
	protected boolean	exists;
	
	/** The cached vector times as map (transferred as string "platform1@time1.platform2@time2..."). */
	protected Map<String, Long>	vtimes;
	
	/** The cached hash code if any (transferred in base 64 at start of vtime string "hash.platform1@time1..."). */
	protected String hash;
	
	/** The file size. */
	protected long size;
	
	//-------- constructors --------
	
	/**
	 *  Create a file meta info.
	 */
	public FileMetaInfo()
	{
		// bean constructor.
	}
	
	/**
	 *  Create a file meta info.
	 */
	public FileMetaInfo(FileData data, String vtime)
	{
		this.data	= data;
		this.exists	= parseExists(vtime);
		this.size	= parseSize(vtime);
		this.hash	= parseHash(vtime);
		this.vtimes	= parseVTime(vtime);
	}
	
	//-------- bean accessors --------
	
	/**
	 *  Get the file name. 
	 */
	public String getName()
	{
		String path = data.getPath();
		String ret = path;
		int idx = path.lastIndexOf("/");
		if(idx>0 || (idx==0 && path.length()>1))
		{
			ret = path.substring(idx+1, path.length());
		}
		return ret;
	}
	
	/**
	 *  Get the file path.
	 *  @return The file path.
	 */
	public String getPath()
	{
		return data.getPath();
	}
	
	//-------- methods --------
	
	/**
	 *  Get a part of the vector time.
	 *  @param node	The platform.
	 *  @return The time (negative for an outdated time stamp).
	 */
	public long	getVTime(String node)
	{
		return vtimes.containsKey(node) ? vtimes.get(node).longValue() : 0;
	}
	
	/**
	 *  Get the data.
	 *  @return The data.
	 */
	public FileData getData()
	{
		return data;
	}

	/**
	 *  Set the data.
	 *  @param data The data to set.
	 */
	public void setData(FileData data)
	{
		this.data = data;
	}

	/**
	 *  Get the exists.
	 *  @return The exists.
	 */
	public boolean isExisting()
	{
		return exists;
	}

	/**
	 *  Set the exists.
	 *  @param exists The exists to set.
	 */
	public void setExisting(boolean exists)
	{
		this.exists = exists;
	}

	/**
	 *  Get the vector time.
	 */
	public String	getVTime()
	{
		return vtimesToString(vtimes, hash, exists, size);
	}
	
	/**
	 *  Set the vector time.
	 */
	public void	setVTime(String vtime)
	{
		this.vtimes	= parseVTime(vtime);
	}
	
//	/**
//	 *  Get the vtimes.
//	 *  @return The vtimes.
//	 */
//	public Map<String, Long> getVtimes()
//	{
//		return vtimes;
//	}
//
//	/**
//	 *  Set the vtimes.
//	 *  @param vtimes The vtimes to set.
//	 */
//	public void setVtimes(Map<String, Long> vtimes)
//	{
//		this.vtimes = vtimes;
//	}

	/**
	 *  Get the hash.
	 *  @return The hash.
	 */
	public String getHash()
	{
		return hash;
	}

	/**
	 *  Set the hash.
	 *  @param hash The hash to set.
	 */
	public void setHash(String hash)
	{
		this.hash = hash;
	}
	
	/**
	 *  Get the size.
	 *  @return The size.
	 */
	public long getSize()
	{
		return size;
	}

	/**
	 *  Set the size.
	 *  @param size The size to set.
	 */
	public void setSize(long size)
	{
		this.size = size;
	}

	/**
	 *  Update a part of the vector time and 
	 *  invalidate other stored times if hash values differ.
	 *  @param node	The platform.
	 *  @param time	The time.
	 *  @param hash	The new hash.
	 */
	public void	bumpVTime(String node, long time, String hash, boolean exists, long size)
	{
		// Do not save unknown time as vector time.
		if(time==0)
			return;
		
		boolean	change	= this.hash==null || !this.hash.equals(hash) || exists!=this.exists;

		// on change -> invalidate other times and update hash.
		if(change)
		{
			for(String key: vtimes.keySet())
			{
				if(!node.equals(key))
				{
					// Use abs() for handling valid and invalid times
					setVTime(key, -Math.abs(vtimes.get(key).longValue()));
				}
			}
			
			this.hash	= hash;
			this.exists	= exists;
			this.size = size;
		}
		
		setVTime(node, time);
	}
	
	/**
	 *  Update a part of the vector time.
	 *  @param node	The platform.
	 *  @param time	The time.
	 */
	public void	setVTime(String node, long time)
	{
		if(time==0)
			throw new IllegalArgumentException("Time must not null: "+node);
		
//		if(time==0)
//			System.out.println("herere");
//		else
			vtimes.put(node, Long.valueOf(time));
	}
	
	
	/**
	 *  Find out if this file or directory has changed with respect to a target.
	 *  Note that this method might return true in either direction in case of a conflict
	 *  (i.e. both local and target are newer with respect to each other).
	 *  
	 *  @param target	The target file info.
	 */
	public boolean isNewerThan(FileMetaInfo fmi)
	{
		if(!data.getPath().equals(fmi.getData().getPath()))
		{
			throw new IllegalArgumentException("Location differs: "+fmi.getData().getPath());
		}
		
		// Local file has not changed wrt remote when
		// 1: hash values are equal (currently only tested for directories)
		// 2: a locally valid time stamp is found for which a greater or equal time stamp exists remotely as valid or invalid
		
		boolean	changed	= (getHash()==null && vtimes.size()>0)
			|| (getHash()!=null && !getHash().equals(fmi.getHash()));
		if(changed)
		{
			for(Iterator<String> it=vtimes.keySet().iterator(); changed && it.hasNext(); )
			{
				String node	= it.next();
				// No match (changed stays true) when:
				// locally invalid or abs value is larger.
				long	local	= getVTime(node);
				long	remote	= fmi.getVTime(node);
				changed	= local<=0 || local>Math.abs(remote);
			}
		}
		
		return changed;
	}

	/**
	 *  Update the vector times of this file info
	 *  with vector times of another file info,
	 *  if the absolute values are larger or the same but valid instead of invalid.
	 *  @param fi	The remote file info, from which which times should be taken.
	 *  @param valid	Set new valid times as valid (e.g. for update or copy) or all new times as invalid (for override).
	 */
	public void	updateVTimes(FileMetaInfo fi, boolean valid)
	{
		if(!data.getPath().equals(fi.getPath()))
		{
			throw new IllegalArgumentException("Location differs: "+fi.getPath());
		}
		
		Set<String>	nodes	= new HashSet<String>(vtimes.keySet());
		nodes.addAll(fi.vtimes.keySet());
		
		for(String node: nodes)
		{
			if(Math.abs(getVTime(node))<Math.abs(fi.getVTime(node))
				|| Math.abs(getVTime(node))==fi.getVTime(node))
			{
				setVTime(node, valid ? fi.getVTime(node) : -Math.abs(fi.getVTime(node)));
			}
		}		
	}
	
//	/**
//	 * 
//	 */
//	protected boolean hasChanged(String localid)
//	{
//		// changed when deleted (or recreated)
//		boolean ret = data.exists!=exists;
//		
//		if(!ret)
//		{
//			boolean mod = data.getLastModified()==getVTime(localid);
//			boolean sz = data.getSize()==getSize();
//			if(mod && sz && deep)
//			{
//				// how to check hash?!
//			}
//		}
//		
//		return ret;
//	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the existing state.
	 */
	protected static boolean	parseExists(String vtime)
	{
		boolean ret = false;
		if(vtime!=null)
		{
			StringTokenizer	stok = new StringTokenizer(vtime, ".");
			while(stok.hasMoreTokens())
			{
				String tok = stok.nextToken();
				if(tok.startsWith("D"))
				{
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Get the size.
	 */
	protected static long parseSize(String vtime)
	{
		long ret = 0;
		if(vtime!=null)
		{
			StringTokenizer	stok = new StringTokenizer(vtime, ".");
			while(stok.hasMoreTokens())
			{
				String tok = stok.nextToken();
				if(tok.startsWith("S"))
				{
					ret = Long.parseLong(tok.substring(1));
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Get the hash if any.
	 */
	protected static String	parseHash(String vtime)
	{
		String	ret	= null;
		if(vtime!=null)
		{
			StringTokenizer	stok = new StringTokenizer(vtime, ".");
			while(stok.hasMoreTokens())
			{
				String tok = stok.nextToken();
				if(tok.startsWith("H"))
				{
					ret = tok.substring(1);
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 *  Get the vector time as map.
	 */
	protected static Map<String, Long>	parseVTime(String vtime)
	{
		Map<String, Long>	vtimes	= new LinkedHashMap<String, Long>();

		if(vtime!=null)
		{
			StringTokenizer	stok	= new StringTokenizer(vtime, ".");
			while(stok.hasMoreTokens())
			{
				String tok	= stok.nextToken();
				if(tok.startsWith("T"))
				{
					int idx = tok.indexOf("@");
					String name = tok.substring(1, idx);
					String time = tok.substring(idx+1, tok.length());
					vtimes.put(name, Long.valueOf(time));
				}
			}
		}
		return vtimes;
	}
	
	/**
	 *  Get the vector time as string.
	 */
	protected static String	vtimesToString(Map<String, Long> vtimes, String hash, boolean exists, long size)
	{
		StringBuffer	buf	= new StringBuffer();
		
		if(!exists)
		{
			buf.append("D.");
		}
		
		if(buf.length()>0)
		{
			buf.insert(0, '.');
		}
		buf.append("S").append(size);
		
		if(hash!=null)
		{
			if(buf.length()>0)
			{
				buf.append('.');
			}
			buf.append("H").append(hash);
		}
		
		for(String key: vtimes.keySet())
		{
			if(buf.length()>0)
			{
				buf.append(".");
			}
			buf.append("T");
			buf.append(key);
			buf.append("@");
			buf.append(vtimes.get(key));
		}

//		System.out.println(buf.toString());
		
		return buf.toString();
	}
}

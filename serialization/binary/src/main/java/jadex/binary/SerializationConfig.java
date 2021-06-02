package jadex.binary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *  Class defining known values and other serialization knowledge.
 *
 */
public class SerializationConfig
{
	/** The String pool v3. */
	protected List<String> dec3stringpool = new ArrayList<String>();
	
	/** The class name pool v3. */
	protected List<String> dec3classnamepool = new ArrayList<String>();
	
	/** The package fragment pool v3. */
	protected List<String> dec3fragpool = new ArrayList<String>();
	
	/** The String pool v2. */
	protected List<String> dec2stringpool = new ArrayList<String>();
	
	/** The class name pool v2. */
	protected List<String> dec2classnamepool = new ArrayList<String>();
	
	/** The package fragment pool v2. */
	protected List<String> dec2fragpool = new ArrayList<String>();
	
	/** The encoding string pool. */
	protected Map<String, Integer> encstringpool = new HashMap<String, Integer>();
	
	/** The encoding class name pool. */
	protected Map<String, Integer> encclassnamepool = new HashMap<String, Integer>();
	
	/** The encoding fragment pool. */
	protected Map<String, Integer> encfragpool = new HashMap<String, Integer>();
	
	/**
	 *  Initializes the config.
	 *  @param predefinedclassnames Class names to predefine as known.
	 */
	public SerializationConfig(String[] predefinedstrings)
	{
		this(predefinedstrings, null);
	}
	
	/**
	 *  Initializes the config.
	 *  @param predefinedclassnames Class names to predefine as known.
	 *  @deprecated Class fragments merged with string pooling in v4.
	 */
	@Deprecated
	public SerializationConfig(String[] predefinedstrings, String[] predefinedclassnames)
	{
		predefinedstrings = predefinedstrings != null ? predefinedstrings : new String[0];
		predefinedclassnames = predefinedclassnames != null ? predefinedclassnames : new String[0];
		
		predefineClassnames2(predefinedclassnames);
		predefineClassnames3(predefinedclassnames);
		
		for (int i = 0; i < predefinedstrings.length; ++i)
		{
			int sid = dec3stringpool.size();
			dec3stringpool.add(predefinedstrings[i]);
			encstringpool.put(predefinedstrings[i], sid);
			dec2stringpool.add(predefinedstrings[i]);
		}
	}
	
	/**
	 *  Returns pool for encoding.
	 *  @return Encoding pool.
	 */
	public Map<String, Integer> createEncodingStringPool()
	{
		return new HashMap<String, Integer>(encstringpool);
	}
	
	/**
	 *  Returns pool for encoding.
	 *  @return Encoding pool.
	 */
	public Map<String, Integer> createEncodingFragPool()
	{
		return new HashMap<String, Integer>(encfragpool);
	}
	
	/**
	 *  Returns pool for encoding.
	 *  @return Encoding pool.
	 */
	public Map<String, Integer> createEncodingClassnamePool()
	{
		return new HashMap<String, Integer>(encclassnamepool);
	}
	
	/**
	 *  Returns pool for decoding v3 serialization streams.
	 *  @return The pool.
	 */
	public List<String> createDecodingStringPool3()
	{
		return new ArrayList<String>(dec3stringpool);
	}
	
	/**
	 *  Returns pool for decoding v3 serialization streams.
	 *  @return The pool.
	 */
	public List<String> createDecodingClassnamePool3()
	{
		return new ArrayList<String>(dec3classnamepool);
	}
	
	/**
	 *  Returns pool for decoding v3 serialization streams.
	 *  @return The pool.
	 */
	public List<String> createDecodingFragPool3()
	{
		return new ArrayList<String>(dec3fragpool);
	}
	
	/**
	 *  Returns pool for decoding v2 serialization streams.
	 *  @return The pool.
	 */
	public List<String> createDecodingStringPool2()
	{
		return new ArrayList<String>(dec2stringpool);
	}
	
	/**
	 *  Returns pool for decoding v2 serialization streams.
	 *  @return The pool.
	 */
	public List<String> createDecodingClassnamePool2()
	{
		return new ArrayList<String>(dec2classnamepool);
	}
	
	/**
	 *  Returns pool for decoding v2 serialization streams.
	 *  @return The pool.
	 */
	public List<String> createDecodingFragPool2()
	{
		return new ArrayList<String>(dec2fragpool);
	}
	
	/**
	 *  Predefine class names, version 3.
	 *  
	 *  @param classnames Class names to predefine.
	 */
	protected void predefineClassnames3(String[] classnames)
	{
		Set<String> knownfrags = new HashSet<String>();
		for (int i = 0; i < classnames.length; ++i)
		{
			int classid = dec3classnamepool.size();
			dec3classnamepool.add(classnames[i]);
			encclassnamepool.put(classnames[i], classid);
			
			int lppos = classnames[i].lastIndexOf('.');
			if (lppos >= 0)
			{
				String pkgname = classnames[i].substring(0, lppos);
				String classname = classnames[i].substring(lppos + 1);
				
				StringTokenizer tok = new StringTokenizer(pkgname, ".");
				while (tok.hasMoreElements())
				{
					String frag = tok.nextToken();
					if (!knownfrags.contains(frag))
					{
						int fragid = dec3fragpool.size();
						dec3fragpool.add(frag);
						encfragpool.put(frag, fragid);
						knownfrags.add(frag);
					}
				}
				if (!knownfrags.contains(classname))
				{
					int fragid = dec3fragpool.size();
					dec3fragpool.add(classname);
					encfragpool.put(classname, fragid);
					knownfrags.add(classname);
				}
			}
		}
	}
	
	/**
	 *  Predefine class names, version 2.
	 *  
	 *  @param classnames Class names to predefine.
	 */
	protected void predefineClassnames2(String[] classnames)
	{
		Set<String> knownfrags = new HashSet<String>();
		Set<String> knownstrings = new HashSet<String>();
		for (int i = 0; i < classnames.length; ++i)
		{
			dec2classnamepool.add(classnames[i]);
			
			int lppos = classnames[i].lastIndexOf('.');
			if (lppos >= 0)
			{
				String pkgname = classnames[i].substring(0, lppos);
				String classname = classnames[i].substring(lppos + 1);
				
				StringTokenizer tok = new StringTokenizer(pkgname, ".");
				while (tok.hasMoreElements())
				{
					String frag = tok.nextToken();
					if (!knownfrags.contains(frag))
					{
						dec2fragpool.add(frag);
						if (!knownstrings.contains(frag))
						{
							dec2stringpool.add(frag);
							knownstrings.add(frag);
						}
						knownfrags.add(frag);
					}
				}
				if (!knownstrings.contains(classname))
				{
					dec2stringpool.add(classname);
					knownstrings.add(classname);
				}
			}
		}
	}
}

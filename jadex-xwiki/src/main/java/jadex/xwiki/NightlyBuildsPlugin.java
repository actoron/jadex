package jadex.xwiki;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.plugin.XWikiPluginInterface;

/**
 *  XWiki plugin for browsing files of jadex nightly builds.
 */
public class NightlyBuildsPlugin implements XWikiPluginInterface
{
	//-------- constructors --------
	
	/**
	 *  Create a plugin instance.
	 */
	public NightlyBuildsPlugin(String name, String classname, XWikiContext context)
	{
//		System.out.println("NightlyBuildsPlugin: "+name+", "+classname+", "+context);
	}
	
	//-------- plugin methods --------
	
	/**
	 *  Get builds sorted by date (newest first).
	 *  @return NightlyBuild objects for each project.
	 */
	protected NightlyBuild[]	getAllBuilds(XWikiContext context, File dir)
	{
		Map	builds	= new HashMap();

		File[]	dirs	= dir.listFiles();
		for(int i=0; i<dirs.length; i++)
		{
			if(dirs[i].isDirectory() && !dirs[i].getName().toLowerCase().equals("web-inf"))
			{
				String[]	files	= dirs[i].list();

				for(int j=0; j<files.length; j++)
				{
					File	file	= new File(dirs[i], files[j]);
					String	name	= extractName(files[j]);
					NightlyBuild	build	= new NightlyBuild(name, files[j], new Date(file.lastModified()), (file.length()*10/1024/1024)/10.0,
						"/"+dir.getName()+"/"+dirs[i].getName()+"/"+files[j]);
					if(builds.containsKey(build.getName()))
					{
						((NightlyBuild)builds.get(build.getName())).addBuild(build);
					}
					else
					{
						builds.put(build.getName(), build);
					}
				}
			}
		}
		NightlyBuild[]	ret	= (NightlyBuild[])builds.values().toArray(new NightlyBuild[builds.size()]);
		Arrays.sort(ret, new NightlyBuild.BuildNameComparator());
		return ret;
	}

	//-------- XWikiPluginInterface management --------
	
	public XWikiAttachment downloadAttachment(XWikiAttachment attachment, XWikiContext context)
	{
//		System.out.println("downloadAttachment: "+attachment+","+context);
		return attachment;
	}
	public void flushCache()
	{
//		System.out.println("flushCache");
	}
	public void flushCache(XWikiContext context)
	{
//		System.out.println("flushCache: "+context);
	}
	public String getClassName()
	{
//		System.out.println("getClassName");
		return getClass().getName();
	}
	public String getName()
	{
//		System.out.println("getName");
		return "nightlybuilds";
	}
	public Api getPluginApi(XWikiPluginInterface plugin, XWikiContext context)
	{
//		System.out.println("getPluginApi"+plugin+","+context);
		return new NightlyBuildsPluginApi((NightlyBuildsPlugin)plugin, context);
	}
	public void init(XWikiContext context) throws XWikiException
	{
//		System.out.println("init: "+context);
	}
	public void setClassName(String name)
	{
//		System.out.println("setClassName: "+name);
	}
	public void setName(String name)
	{
//		System.out.println("setName: "+name);
	}
	public void virtualInit(XWikiContext context)
	{
//		System.out.println("virtualInit: "+context);
	}

	//-------- XWikiPluginInterface page rendering --------
	
	/**
	 *  Called once for each document(?).
	 */
	public void beginParsing(XWikiContext context)
	{
//		System.out.println("beginParsing: "+context.getDoc().getFullName());
	}
	
	/**
	 *  Called once for each document(?).
	 */
	public String endParsing(String content, XWikiContext context)
	{
//		System.out.println("endParsing: "+context.getDoc().getFullName());
		return content;
	}

	public void beginRendering(XWikiContext context)
	{
//		System.out.println("beginRendering: "+context.getDoc().getFullName());
	}
	public void endRendering(XWikiContext context)
	{
//		System.out.println("endRendering: "+context.getDoc().getFullName());
	}

	public String commonTagsHandler(String line, XWikiContext context)
	{
//		System.out.println("commonTagsHandler: "+line+","+context);
		return line;
	}
	public String endRenderingHandler(String line, XWikiContext context)
	{
//		System.out.println("endRenderingHandler: "+line+","+context);
		return line;
	}
	public String insidePREHandler(String line, XWikiContext context)
	{
//		System.out.println("insidePREHandler: "+line+","+context);
		return line;
	}
	public String outsidePREHandler(String line, XWikiContext context)
	{
//		System.out.println("outsidePREHandler: "+line+","+context);
		return line;
	}
	public String startRenderingHandler(String line, XWikiContext context)
	{
//		System.out.println("startRenderingHandler: "+line+","+context);
		return line;
	}

	/**
	 *  Extract the project name (e.g. jadex-xml) out of a
	 *  file name (e.g. jadex-xml-2.0-rc2-dist.zip).
	 */
	protected String	extractName(String filename)
	{
		StringBuffer	ret	= new StringBuffer();
		StringTokenizer	stok	= new StringTokenizer(filename, ".-", true);
		while(stok.hasMoreTokens())
		{
			String	tok	= stok.nextToken();
			if(tok.equals("."))
			{
				break;
			}
			else if(tok.equals("-"))
			{
				if(stok.hasMoreTokens())
				{
					String	tok2	= stok.nextToken();
					boolean	digits	= true;
					for(int i=0; digits && i<tok2.length(); i++)
						digits	= Character.isDigit(tok2.charAt(i));
					
					if(digits)
					{
						break;
					}
					else
					{
						ret.append(tok);
						ret.append(tok2);
					}
				}
			}
			else
			{
				ret.append(tok);
			}
		}
		return ret.toString();
	}
	
	/**
	 *  Main for testing.
	 */
	public static void	main(String[] args)
	{
		NightlyBuildsPlugin	plug	= new NightlyBuildsPlugin(null, null, null);
		File	dir	= new File("C:/Files/Programs/apache-tomcat-6.0.26/webapps/jadex-nightly-builds");
//		File	dir	= new File("C:/Programme/Apache Software Foundation/Tomcat 6.0/webapps/jadex-nightlybuilds");
		NightlyBuild[] builds	= plug.getAllBuilds(null, dir);
		for(int i=0; i<builds.length; i++)
		{
			System.out.println(builds[i]);
		}
	}
}
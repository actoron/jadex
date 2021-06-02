package org.activecomponents.webservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jadex.commons.SUtil;


/**
 *  Download the jadex.js.
 *  
 *  Tries different alternatives:
 *  
 *  - local file for live-reloading
 *  - directly from classpath via name
 *  - from a contained platform-webservice-websocket jar in WEB-INF/libs
 */
public class JadexjsDownloadServlet extends HttpServlet
{
	/** The jadex.js name. */
	public static final String JADEXJSNAME = "jadex.js";
	
	/** The cached jadex rs file **/
	protected byte[] jadexjs;

	/**
	 *  Do get method.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename="+JADEXJSNAME);
		ServletContext ctx = getServletContext();

		byte[] bytes = jadexjs;

		if(bytes == null) 
		{
			// first check if local version exists for live-reloading
			File local = new File(JADEXJSNAME);
			if(!local.exists()) 
				local = new File("build/js/"+JADEXJSNAME);

			if(local.exists()) 
			{
				// do not cache
				System.out.println("Serving non-cached jadex.js from " + local.getAbsolutePath());
				bytes = SUtil.readFile(local);
			} 
			else 
			{
				System.out.print("Loading "+JADEXJSNAME+" from classpath ...");
				bytes = loadJadexJs(ctx);
				jadexjs = bytes;
			}
		}
		
		if(bytes != null) 
		{
			OutputStream os = response.getOutputStream();
			SUtil.copyStream(new ByteArrayInputStream(bytes), os);
			os.close();
		}
	}

	/**
	 *  Load jadex.js on server side.
	 * @param ctx
	 * @return
	 * @throws IOException
	 */
	protected byte[] loadJadexJs(ServletContext ctx)  throws IOException 
	{
		byte[] result = null;

		// try loading from classpath
		InputStream is = ctx.getClassLoader().getResourceAsStream(JADEXJSNAME);
		if(is == null) 
		{
//			is = ctx.getClassLoader().getResourceAsStream("jadex.js");
			is = JadexjsDownloadServlet.class.getClassLoader().getResourceAsStream(JADEXJSNAME);
		}

		if(is != null) 
		{
			result = SUtil.readStream(is);
		} 
		else 
		{
			// try loading from lib dir
			Set<String> files = ctx.getResourcePaths("/WEB-INF/lib");

			for(String file: files)
			{
				// Hack, name sensitive and fragile :-(
				if(file.indexOf("platform-webservice-websocket")!=-1)
				{
	//				is = ctx.getResourceAsStream("/WEB-INF/lib/actoron-webservice-websocket-1.0.jar");
					is = ctx.getResourceAsStream(file);
					ZipInputStream zis = new ZipInputStream(is);
					ZipEntry entry;

					while((entry = zis.getNextEntry())!=null)
					{
	//		            System.out.println("entry: " + entry.getName() + ", " + entry.getSize());

						if(entry.getName().indexOf(JADEXJSNAME)!=-1)
						{
							result = SUtil.readStream(zis);
							break;
						}
					}
				}
			}
		}

		if(result == null)
			System.err.println("Could not load "+JADEXJSNAME+" from jar");
		else
			System.out.println(" loaded " + result.length + " bytes.");

		return result;
	}
}

package org.activecomponents.webservice;

import jadex.commons.SUtil;

import java.io.*;
import java.net.URL;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *  Download the jadex.js file und /jadex.js.
 */
public class JadexjsDownloadServlet extends HttpServlet
{
	/** The cached jadex rs file **/
	byte[] jadexJs;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename=jadex.js");
		ServletContext ctx = getServletContext();

		byte[] outputBytes = jadexJs;

		if (outputBytes == null) {
			// first check if local version exists for live-reloading:
			File localJadexJs = new File("jadex.js");
			if (!localJadexJs.exists()) {
				localJadexJs = new File("build/js/jadex.js");
			}

			if (localJadexJs.exists()) {
				// do not cache
				System.out.println("Serving non-cached jadex.js from " + localJadexJs.getAbsolutePath());
				outputBytes = SUtil.readFile(localJadexJs);
			} else {
				System.out.print("Loading jadex.js from classpath ...");
				outputBytes = loadJadexJs(ctx);
				
				jadexJs = outputBytes;
			}
		}
		if (outputBytes != null) {
			OutputStream os = response.getOutputStream();
			SUtil.copyStream(new ByteArrayInputStream(outputBytes), os);
			os.close();
		}
	}

	private byte[] loadJadexJs(ServletContext ctx)  throws IOException {
		byte[] result = null;

		// try loading from classpath
		InputStream is = ctx.getClassLoader().getResourceAsStream("jadex.js");
		if (is == null) {
//			is = ctx.getClassLoader().getResourceAsStream("jadex.js");
			is = JadexjsDownloadServlet.class.getClassLoader().getResourceAsStream("jadex.js");
		}

		if (is != null) {
			result = SUtil.readStream(is);
		} else {
			// try loading from lib dir
			Set<String> files = ctx.getResourcePaths("/WEB-INF/lib");

			for(String file: files)
			{
				if(file.indexOf("actoron-webservice-websocket")!=-1)
				{
	//				is = ctx.getResourceAsStream("/WEB-INF/lib/actoron-webservice-websocket-1.0.jar");
					is = ctx.getResourceAsStream(file);
					ZipInputStream zis = new ZipInputStream(is);
					ZipEntry entry;

					while((entry = zis.getNextEntry())!=null)
					{
	//		            System.out.println("entry: " + entry.getName() + ", " + entry.getSize());

						if(entry.getName().indexOf("jadex.js")!=-1)
						{
							result = SUtil.readStream(zis);
							break;
						}
					}
				}
			}
		}

		if(result == null)
			System.err.println("Could not load jadex.js from jar");
		else
			System.out.println(" loaded " + result.length + " bytes.");

		return result;
	}
}

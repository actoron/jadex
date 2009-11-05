package com.daimler.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceLocator
{
	
	public static URI getURIForResource(String resource, String basis)
	{
		//AEM_FileUtils.getFilenameForCurrentPlatform
		URL url = null;
		URI uri = null;
		try
		{
			if (basis != null)
			{
			    if (!basis.endsWith("/")) basis += "/";
				url = ClassLoader.getSystemResource(basis + resource);
				uri = url.toURI();
				return uri;
			}
		}
		catch (Exception err)
		{
			//System.err.println(err.getMessage());
		}
		try
		{
			url = ClassLoader.getSystemResource(resource);
			uri = url.toURI();
			return uri;
		}
		catch (Exception err)
		{
			//System.err.println(err.getMessage());
		}
		try
		{
			if (basis != null)
			{
				uri = new URI(basis + resource);
				if (uri.getScheme() == null) uri = null;
			}
		}
		catch (Exception err)
		{
			//System.err.println(err.getMessage());
		}
		if (uri != null) return uri;
		try
		{
			uri = new URI(resource);
			if (uri.getScheme() == null) uri = null;
		}
		catch (URISyntaxException err)
		{
			//System.err.println(err.getMessage());
		}
		return uri;
	}
	
	public static URL getURLforLocation(String location) {
	    URL url = null;
        File f = null;
        try
        {
            f = new File(location);
            if (!f.exists())
            {
                //try to read it as a local resource
                try
                {
                    URL testurl = ClassLoader.getSystemResource(location);
                    if (testurl != null) f = new File(testurl.toURI());
                }
                catch (URISyntaxException err)
                {
                }
                if (!f.exists() || f.isDirectory())
                {
                    url = new URL(location);
                }
                else
                {
                    url = f.toURL();
                }
            }
            else
            {
                url = f.toURL();
            }
        }
        catch (MalformedURLException err)
        {
            return null;
        }
        return url;
	}
}

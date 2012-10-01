package jadex.wfms.common;

import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SClassLoaderTools
{
	public static final IFuture searchLibraryService(ILibraryService libService, final FileFilter filter)
	{
		final Future ret = new Future();
		
		//FIXME: Correct?
		libService.getAllURLs().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				List urls = (List) result;
				Set fileSet = new HashSet();
				for (Iterator it = urls.iterator(); it.hasNext(); )
				{
					URL url = (URL) it.next();
					File dir = new File(url.getFile());
					if (dir.isDirectory())
						fileSet.addAll(searchDirectory(dir, filter));
					else if (dir.getAbsolutePath().endsWith(".jar"))
						fileSet.addAll(searchJar(dir, filter));
				}
				ret.setResult(fileSet);
			}
		});
		
		return ret;
	}
	
	public static final Set searchDirectory(File dir, FileFilter filter)
	{
		return searchDirectory(dir, false, filter);
	}
	
	public static final Set searchDirectory(File dir, boolean prependDir, FileFilter filter)
	{
		return searchDirectory(dir, dir, prependDir, filter);
	}
	
	protected static final Set searchDirectory(File mainDir, File dir, boolean prependDir, FileFilter filter)
	{
		HashSet ret = new HashSet();
		File[] content = dir.listFiles();
		
		if (content == null)
			return ret;
		
		for (int i = 0; i < content.length; ++i)
		{
			if (content[i].isDirectory())
			{
				Set subSet = searchDirectory(mainDir, content[i], true, filter);
				for (Iterator it = subSet.iterator(); it.hasNext(); )
				{
					if (prependDir)
						ret.add(dir.getName().concat("/").concat((String) it.next()));
					else
						ret.add(it.next());
				}
			}
			else if (filter.accept(new File (content[i].getAbsolutePath().substring(mainDir.getAbsolutePath().length() + 1))))
			{
				if (prependDir)
					ret.add(dir.getName().concat("/").concat(content[i].getName()));
				else
					ret.add(content[i].getName());
			}
		}
		
		return ret;
	}
	
	public static final Set searchJar(File jar, FileFilter filter)
	{
		HashSet ret = new HashSet();
		try
		{
			JarFile jarFile = new JarFile(jar);
			for (Enumeration entries = jarFile.entries(); entries.hasMoreElements(); )
			{
				JarEntry entry = (JarEntry) entries.nextElement();
				if (filter.accept(new File(entry.getName())))
					ret.add(entry.getName());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	/*private Set searchDirectory(File dir, boolean prependDir)
	{
		HashSet ret = new HashSet();
		File[] content = dir.listFiles();
		for (int i = 0; i < content.length; ++i)
		{
			if (content[i].isDirectory())
			{
				Set subSet = searchDirectory(content[i], true);
				for (Iterator it = subSet.iterator(); it.hasNext(); )
				{
					if (prependDir)
						ret.add(dir.getName().concat("/").concat((String) it.next()));
					else
						ret.add(it.next());
				}
			}
			else if ((content[i].getName().endsWith(".bpmn")) || (content[i].getName().endsWith(".gpmn")))
			{
				if (prependDir)
					ret.add(dir.getName().concat("/").concat(content[i].getName()));
				else
					ret.add(content[i].getName());
			}
		}
		
		return ret;
	}
	
	private Set searchJar(File jar)
	{
		HashSet ret = new HashSet();
		try
		{
			JarFile jarFile = new JarFile(jar);
			for (Enumeration entries = jarFile.entries(); entries.hasMoreElements(); )
			{
				JarEntry entry = (JarEntry) entries.nextElement();
				if (entry.getName().endsWith(".bpmn") || entry.getName().endsWith(".gpmn"))
					ret.add(entry.getName());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return ret;
	}*/
}

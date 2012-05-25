package jadex.base.gui;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 *  
 */
public class ClassChooserPanel	extends JPanel
{
	//-------- attributes --------
	
	/** The current class loader. */
	protected URL[] urls;
	
	/** The file filter. */
	protected IFilter filefilter;
	
	/** The class filter. */
	protected IFilter classfilter;
	
	/** The current class loader. */
	protected ClassLoader	classloader;
	
	/** The box. */
	protected JComboBox box;
	
	//-------- constructors --------
	
	/**
	 *  Create a class path panel.
	 */
	public ClassChooserPanel(IFilter filefilter, IFilter classfilter, URL[] urls, ClassLoader classloader)
	{
		super(new BorderLayout());
		this.filefilter = filefilter;
		this.classfilter = classfilter;
		this.filefilter = filefilter;
		this.urls = urls;
		this.classloader = classloader;
		
		this.box = new JComboBox();
		this.add(box, BorderLayout.CENTER);
		
		Class<?>[] plugins = scanForClasses();
		for(int i=0; i<plugins.length; i++)
		{
			box.addItem(plugins[i]);
		}
	}

	/**
	 *  Get the selected item.
	 */
	public Object getSelectedElement()
	{
		return box.getSelectedItem();
	}
	
	/**
	 * 
	 * @return
	 */
	public Class<?>[] scanForClasses()
	{
		List<Class<?>>	ret	= new ArrayList<Class<?>>();
		String[] facs = scanForFiles(filefilter);
		try
		{
			for(int i=0; i<facs.length; i++)
			{
				String	clname	= facs[i].substring(0, facs[i].length()-6).replace('/', '.');
//				System.out.println("Found candidate: "+clname);
				Class<?>	fac	= SReflect.findClass0(clname, null, getClassLoader());
				
				if(fac!=null && classfilter.filter(fac))
				{
					ret.add(fac);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret.toArray(new Class[ret.size()]);
	}

	/**
	 * 
	 * @param extensions
	 * @return
	 */
	public String[] scanForFiles(IFilter filter)
	{
		List<String>	ret	= new ArrayList<String>();
		try
		{
			for(int i=0; i<urls.length; i++)
			{
//				System.out.println("Scanning: "+entry);
				File f = new File(urls[i].toURI());
				if(f.getName().endsWith(".jar"))
				{
					JarFile	jar	= new JarFile(f);
					for(Enumeration<JarEntry> e=jar.entries(); e.hasMoreElements(); )
					{
						JarEntry	je	= e.nextElement();
						if(filter.filter(f))	
						{
							ret.add(je.getName());
						}
					}
					jar.close();
				}
				else if(f.isDirectory())
				{
					scanDir(f, filter, ret, new ArrayList<String>());
//					throw new UnsupportedOperationException("Currently only jar files supported: "+f);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ret.toArray(new String[ret.size()]);
	}
	
	/**
	 * 
	 */
	protected void scanDir(File file, IFilter filter, List<String> results, List<String> donedirs)
	{
		File[] files = file.listFiles(new FileFilter()
		{
			public boolean accept(File f)
			{
				return !f.isDirectory();
			}
		});
		for(File fi: files)
		{
			if(fi.getName().endsWith(".class") && filter.filter(fi))
			{
				String fn = SUtil.convertPathToPackage(fi.getAbsolutePath(), urls);
//				System.out.println("fn: "+fi.getName());
				results.add(fn+"."+fi.getName());
			}
		}
		
		if(file.isDirectory())
		{
			donedirs.add(file.getAbsolutePath());
			File[] sudirs = file.listFiles(new FileFilter()
			{
				public boolean accept(File f)
				{
					return f.isDirectory();
				}
			});
			
			for(File dir: sudirs)
			{
				if(!donedirs.contains(dir.getAbsolutePath()))
				{
					scanDir(dir, filter, results, donedirs);
				}
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ClassLoader getClassLoader()
	{
//		if(classloader==null)
//		{
//			classloader	= new URLClassLoader(urls, null);
//		}
		return classloader;	
	}
	
}

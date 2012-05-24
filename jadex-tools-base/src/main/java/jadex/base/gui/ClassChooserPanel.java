package jadex.base.gui;

import jadex.commons.SReflect;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
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
	
	/** The name part. */
	protected String namepart;
	
	/** The class type. */
	protected Class<?> type;
	
	/** The current class loader. */
	protected ClassLoader	classloader;
	
	/** The box. */
	protected JComboBox box;
	
	//-------- constructors --------
	
	/**
	 *  Create a class path panel.
	 */
	public ClassChooserPanel(String suffix, Class<?> type, URL[] urls)
	{
		super(new BorderLayout());
		this.namepart = suffix;
		this.type = type;
		this.urls = urls;
		
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
		String[]	facs	= scanForFiles(new String[]{namepart});
		try
		{
			for(int i=0; i<facs.length; i++)
			{
				String	clname	= facs[i].substring(0, facs[i].length()-6).replace('/', '.');
				System.out.println("Found candidate: "+clname);
				Class<?>	fac	= SReflect.findClass0(clname, null, getClassLoader());
				if(fac!=null && !fac.isInterface() && SReflect.isSupertype(type, fac))
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
	public String[] scanForFiles(String[] extensions)
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
						for(int j=0; j<extensions.length; j++)
						{
							if(je.getName().endsWith(extensions[j]))
							{
								ret.add(je.getName());
							}
						}
					}
					jar.close();
				}
				else if(f.isDirectory())
				{
					scanDir(f, extensions, ret, new ArrayList<String>());
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
	protected void scanDir(File file, String[] extensions, List<String> results, List<String> donedirs)
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
			for(int j=0; j<extensions.length; j++)
			{
//				if(fi.getName().indexOf("Plugin")!=-1)
//					System.out.println("file: "+fi.getName());
				if(fi.getName().endsWith(".class") && fi.getName().indexOf(extensions[j])!=-1)
				{
					results.add(fi.getAbsolutePath());
				}
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
					scanDir(dir, extensions, results, donedirs);
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
		if(classloader==null)
		{
			classloader	= new URLClassLoader(urls, null);
		}
		return classloader;	
	}
	
//	/**
//	 *  Get the settings as properties.
//	 */
//	public Properties	getProperties()
//	{
//		Properties	props	= new Properties();
//		for(int i=0; i<model.getRowCount(); i++)
//		{
//			File	entry	= (File)model.getValueAt(i, 0);
//			props.addProperty(new Property("ENTRY", entry.getAbsolutePath()));
//		}
//		return props;
//	}

	
//	/**
//	 *  Set the settings as properties.
//	 */
//	public void	setProperties(Properties props)
//	{
//		for(int i=model.getRowCount()-1; i>=0; i--)
//		{
//			model.removeRow(i);
//		}
//		Property[]	entries	= props.getProperties("ENTRY");
//		for(int i=0; i<entries.length; i++)
//		{
//			model.addRow(new Object[]{new File(entries[i].getValue())});
//		}
//	}
}

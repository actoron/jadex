package jadex.base.gui.config;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.gui.PropertiesPanel;

/**
 *  Show/edit the class path or the used maven artifacts
 */
public class ClasspathPanel	extends JPanel
{
	//-------- attributes --------
	
	/** The table model. */
	protected DefaultTableModel	model;
	
	/** The current class loader. */
	protected ClassLoader	classloader;
	
	//-------- constructors --------
	
	/**
	 *  Create a class path panel.
	 */
	public ClasspathPanel()
	{
		super(new BorderLayout());
		
		this.model	= new DefaultTableModel(new String[]{"Entries"}, 0)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		final JTable	classpath = new JTable(model);
		classpath.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
			{
				
				JComponent	ret	= (JComponent)super.getTableCellRendererComponent(table, ((File)value).getName(), selected, focus, row, column);
				ret.setToolTipText(((File)value).getAbsolutePath());
				return ret;
			}
		});
		
		PropertiesPanel	props	= new PropertiesPanel("Classpath");
		props.addFullLineComponent("classpath", new JScrollPane(classpath), 1);
		JButton[]	buts	= props.createButtons("buts", new String[]{"Add...", "Remove..."}, 0);
		
		this.add(props, BorderLayout.CENTER);
		
		final JFileChooser	fc	= new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		fc.setFileFilter(new FileFilter()
		{
			public boolean accept(File file)
			{
				return file.isDirectory() || file.getName().endsWith(".jar");
			}

			public String getDescription()
			{
				return "Classpath entry (.jar file or bin or classes directory)";
			}
		});

		buts[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int	ok	= fc.showOpenDialog(ClasspathPanel.this);
				if(ok==JFileChooser.APPROVE_OPTION)
				{
					File[]	files 	= fc.getSelectedFiles();
					for(int i=0; i<files.length; i++)
					{
						boolean	found	= false;
						for(int j=0; !found && j<model.getRowCount(); j++)
						{
							found	= files[i].equals(model.getValueAt(j, 0));
						}
						if(!found)
						{
							model.addRow(new Object[]{files[i]});
						}
					}
					classloader	= null;
				}
			}
		});
		
		buts[1].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int[]	rows	= classpath.getSelectedRows();
				if(rows.length>0)
				{
					for(int i=rows.length-1; i>=0; i--)
					{
						model.removeRow(rows[i]);
					}
					classloader	= null;
				}
			}
		});

	}

	/**
	 * 
	 */
	public Class<?>[] scanForFactories()
	{
		List<Class<?>>	ret	= new ArrayList<Class<?>>();
		String[]	facs	= scanForFiles(new String[]{"Factory.class"});
		try
		{
			Class<?>	type	= SReflect.findClass("jadex.bridge.service.types.factory.IComponentFactory", null, getClassLoader());
			
			for(int i=0; i<facs.length; i++)
			{
				String	clname	= facs[i].substring(0, facs[i].length()-6).replace('/', '.');
//				System.out.println("Found candidate: "+clname);
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
	 */
	public ClassLoader getClassLoader()
	{
		if(classloader==null)
		{
			List<URL>	urls	= new ArrayList<URL>();
			for(int i=0; i<model.getRowCount(); i++)
			{
				urls.add(SUtil.toURL(model.getValueAt(i, 0)));
			}
			classloader	= new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
		}
		return classloader;	
	}

	/**
	 * 
	 */
	public String[] scanForFiles(String[] extensions)
	{
		List<String>	ret	= new ArrayList<String>();
		try
		{
			for(int i=0; i<model.getRowCount(); i++)
			{
				File	entry	= (File)model.getValueAt(i, 0);
//				System.out.println("Scanning: "+entry);
				if(entry.getName().endsWith(".jar"))
				{
					JarFile	jar	= new JarFile(entry);
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
				else
				{
					throw new UnsupportedOperationException("Currently only jar files supported: "+entry);
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
	 *  Get the settings as properties.
	 */
	public Properties	getProperties()
	{
		Properties	props	= new Properties();
		for(int i=0; i<model.getRowCount(); i++)
		{
			File	entry	= (File)model.getValueAt(i, 0);
			props.addProperty(new Property("ENTRY", entry.getAbsolutePath()));
		}
		return props;
	}

	
	/**
	 *  Set the settings as properties.
	 */
	public void	setProperties(Properties props)
	{
		for(int i=model.getRowCount()-1; i>=0; i--)
		{
			model.removeRow(i);
		}
		Property[]	entries	= props.getProperties("ENTRY");
		for(int i=0; i<entries.length; i++)
		{
			model.addRow(new Object[]{new File(entries[i].getValue())});
		}
	}
}

package jadex.commons.gui;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarEntry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.table.AbstractTableModel;

/**
 *  Panel that allows for searching artifacts from maven repositories.
 */
public class ClassSearchPanel extends JPanel
{
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"jar", SGUI.makeIcon(ClassSearchPanel.class, "/jadex/base/gui/images/jar.png"),
		"folder", SGUI.makeIcon(ClassSearchPanel.class, "/jadex/base/gui/images/folder4.png")
	});
	
	//-------- attributes --------
	
	/** The results table. */
	protected JTable results;
	protected ClassTableModel ctm;
	
	/** The status. */
	protected JLabel status;
			
	/** The thread pool. */
	protected IThreadPool tp;
	
	/** The classloader. */
	protected ClassLoader cl;
	
	/** The current search query text. */
	protected String curquery;
	
	/** The checkbox for interfaces. */
	protected JCheckBox cbif;
	
	//-------- constructors --------
	
	/**
	 *  Create a new search panel.
	 */
	public ClassSearchPanel(ClassLoader cl, IThreadPool tp)
	{
		this.tp = tp;
		
		final JTextField tfsearch = new JTextField();
		tfsearch.addKeyListener(new KeyListener()
		{
			protected boolean dirty = false;
			protected Timer t;
			
			{
				t = new Timer(500, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(dirty)
						{
							dirty = false;
						}
						else
						{
							t.stop();
							performSearch(tfsearch.getText());
						}
					}
				});
			}
			
			public void keyTyped(KeyEvent e)
			{
				if(!t.isRunning())
				{
					t.start();
				}
				else
				{
					dirty = true;
				}
			}
			
			public void keyReleased(KeyEvent e)
			{
			}
			
			public void keyPressed(KeyEvent e)
			{
			}
		});
		
		status = new JLabel("idle");
		
		ctm = new ClassTableModel();
		results = new JTable(ctm);
		
		setLayout(new GridBagLayout());
		
		int y=0;
		
		add(new JLabel("Enter type name prefix:"), new GridBagConstraints(0,y++,
			2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));
		
		add(tfsearch, new GridBagConstraints(0,y++,2,1,1,0,
			GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		add(new JLabel("Search Results:"), new GridBagConstraints(0,y++,
			2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));
		
		add(new JScrollPane(results), new GridBagConstraints(0,y++,
			2,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		add(status, new GridBagConstraints(0,y++,
			2,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		performSearch(null);
		
//		tp.execute(new Runnable()
//		{
//			public void run()
//			{
//				while(true)
//				{
//					try
//					{
//						Thread.sleep(1000);
//						getSelectedArtifactInfo();
//					}
//					catch(Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//			}
//		});
	}
	
	//-------- methods --------
	
	/**
	 *  Get the thread pool.
	 *  @return The thread pool.
	 */
	public IThreadPool getThreadPool()
	{
		return tp;
	}

	/**
	 *  Set the current query string.
	 *  Used to abort old searches.
	 *  @param curquery The current query.
	 */
	protected synchronized void setCurrentQuery(String curquery)
	{
		this.curquery = curquery;
	}
	
	/**
	 *  Test if the search is still the current one.
	 *  @param curquery The current search text.
	 */
	protected synchronized boolean isCurrentQuery(String curquery)
	{
		return SUtil.equals(this.curquery, curquery);
	}
	
	/**
	 *  Perform a search using a search expression.
	 */
	public void performSearch(final String exp)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		if(exp==null || exp.length()==0)
		{
			status.setText("idle '"+exp+"'");
			return;
		}
		
		setCurrentQuery(exp);
//		System.out.println("perform search: "+exp);
		
		ctm.clear();
		
		if(exp!=null && exp.length()>0)
			status.setText("searching '"+exp+"'");
						
		getThreadPool().execute(new Runnable()
		{
			public void run()
			{
				IFilter<Object> filefilter = new IFilter<Object>()
				{
					public boolean filter(Object obj)
					{
						String	fn	= "";
						if(obj instanceof File)
						{
							File	f	= (File)obj;
							fn	= f.getName();
						}
						else if(obj instanceof JarEntry)
						{
							JarEntry	je	= (JarEntry)obj;
							fn	= je.getName();
						}
						return fn.indexOf("$")==-1  && fn.indexOf(exp)!=-1;
					}
				};
				IFilter<Class<?>> classfilter = new IFilter<Class<?>>()
				{
					public boolean filter(Class<?> clazz)
					{
//						System.out.println("found: "+clazz);
								
//							Class<?> cl = (Class<?>)obj;
						boolean ret = SReflect.getInnerClassName(clazz).startsWith(exp);
//							boolean ret = SReflect.isSupertype(IControlCenterPlugin.class, cl) && !(cl.isInterface() || Modifier.isAbstract(cl.getModifiers()));
						
						return ret;
					}
				};
				
				SReflect.asyncScanForClasses(cl, filefilter, classfilter)
					.addResultListener(new SwingIntermediateResultListener<Class<?>>(new IIntermediateResultListener<Class<?>>()
				{
					public void intermediateResultAvailable(Class<?> result)
					{
						ctm.addEntry(result);
					}
					
					public void finished()
					{
						status.setText("searching '"+exp+"' ("+ctm.size()+") finished");
					}
					
					public void resultAvailable(Collection<Class<?>> result)
					{
						for(Class<?>clazz: result)
						{
							ctm.addEntry(clazz);
						}
						finished();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						status.setText("idle '"+exp+"'");
					}
				}));
			}
		});
	}
	
	/**
	 *  Set a status text.
	 *  @param text The text.
	 */
	protected void setStatus(final String text)
	{
		if(SwingUtilities.isEventDispatchThread())
		{
			status.setText(text);
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					status.setText(text);
				}
			});
		}
	}
	
	/**
	 *  Get the selected class.
	 */
	public Class<?> getSelectedClass()
	{
		Class<?> ret = null;
		int sel = results.getSelectedRow();
		if(sel>0)
		{
			ret = ctm.getClass(sel);
		}
		return ret;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				// todo: use thread pool
				Class<?> cl = showDialog(null, null, null);
				System.out.println("clazz: "+cl);
//				JFrame f = new JFrame();
//				f.add(new RepositorySearchPanel(createPlexus(), new ThreadPool()));
//				f.pack();
//				f.setLocation(SGUI.calculateMiddlePosition(f));
//				f.setVisible(true);
			}
		});
	}
	
	/**
	 *  Show a repository and artifact dialog.
	 */
	public static Class<?> showDialog(ClassLoader cl, IThreadPool tp, Component parent)
	{		
		assert SwingUtilities.isEventDispatchThread();

		Class<?> ret = null;
		ClassSearchPanel pan = new ClassSearchPanel(cl, tp!=null? tp: new ThreadPool());		

		final JDialog dia = new JDialog((JFrame)null, "Type Selection", true);
		
		JButton bok = new JButton("OK");
		JButton bcancel = new JButton("Cancel");
		bok.setMinimumSize(bcancel.getMinimumSize());
		bok.setPreferredSize(bcancel.getPreferredSize());
		JPanel ps = new JPanel(new GridBagLayout());
		ps.add(bok, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2), 0, 0));
		ps.add(bcancel, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));

		dia.getContentPane().add(pan, BorderLayout.CENTER);
		dia.getContentPane().add(ps, BorderLayout.SOUTH);
		final boolean[] ok = new boolean[1];
		bok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ok[0] = true;
				dia.dispose();
			}
		});
		bcancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dia.dispose();
			}
		});
		dia.pack();
		dia.setLocation(SGUI.calculateMiddlePosition(parent!=null? SGUI.getWindowParent(parent): null, dia));
		dia.setVisible(true);
		if(ok[0])
		{
			ret = pan.getSelectedClass();
		}
		
//		int res	= JOptionPane.showOptionDialog(null, pan, "Repository Location", JOptionPane.YES_NO_CANCEL_OPTION,
//			JOptionPane.PLAIN_MESSAGE, null, new Object[]{"OK", "Cancel"}, "OK");
//		if(JOptionPane.YES_OPTION==res)
//		{
//			ret = pan.getSelectedArtifactInfo();
//		}
		return ret;
	}
	
	/**
	 * 
	 */
	class ClassTableModel extends AbstractTableModel
	{
		protected List<Class<?>> entries;
		
		/**
		 *  Get the column count.
		 */
		public int getColumnCount()
		{
			return 2;
		}

		/**
		 *  Add a new entry.
		 */
		public void addEntry(Class<?> clazz)
		{
			if(entries==null)
				entries = new ArrayList<Class<?>>();
			entries.add(clazz);
			fireTableRowsInserted(entries.size()-1, entries.size()-1);
		}
		
		/**
		 * @return all component subscriptions length
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount()
		{
			return entries!=null? entries.size(): 0;
		}

		/**
		 * @param rowIndex
		 * @param columnIndex
		 * @return the values of this table
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			Object ret = null;
			if(entries!= null && rowIndex>-1 && rowIndex<entries.size())
			{
				if(columnIndex==0)
				{
					ret = "";
				}
				else if(columnIndex==1)
				{
					ret = entries.get(rowIndex).toString();
				}
			}

			return ret;
		}

		/**
		 * @param columnIndex
		 * @return the name of a columnt
		 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
		 */
		public String getColumnName(int columnIndex)
		{
			switch(columnIndex)
			{
				case 0:
					return "";
				case 1:
					return "Name";
			}
			return null;
		}

		/**
		 * @param columnIndex
		 * @return the class of a column
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		public Class<?> getColumnClass(int columnIndex)
		{
			switch(columnIndex)
			{
				case 0:
					return String.class;
				case 1:
					return String.class;
			}
			return null;
		}

		/**
		 *  Get the class.
		 */
		public Class<?> getClass(int i)
		{
			return entries!=null? entries.get(i): null;
		}
		
		/**
		 *  Remove all entries.
		 */
		public void clear()
		{
			if(entries!=null)
				entries.clear();
		}
		
		/**
		 *  Get the size.
		 */
		public int size()
		{
			return entries!=null? entries.size(): 0;
		}
	}

}

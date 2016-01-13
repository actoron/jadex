package jadex.commons.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.TerminableFuture;
import jadex.commons.gui.future.SwingIntermediateResultListener;

/**
 *  Panel that allows for searching artifacts from maven repositories.
 */
public class ClassSearchPanel extends JPanel
{
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"class", SGUI.makeIcon(ClassSearchPanel.class, "/jadex/commons/gui/images/class.png"),
		"abstractclass", SGUI.makeIcon(ClassSearchPanel.class, "/jadex/commons/gui/images/abstractclass.png"),
		"interface", SGUI.makeIcon(ClassSearchPanel.class, "/jadex/commons/gui/images/interface.png")
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
	protected JCheckBox cbac;
	protected JCheckBox cbc;
	protected JCheckBox cbic;
	
	//-------- constructors --------
	
	/**
	 *  Create a new search panel.
	 */
	public ClassSearchPanel(ClassLoader cl, IThreadPool tp)
	{
		this(cl, tp, true, true, true, false);
	}
	
	/**
	 *  Create a new search panel.
	 */
	public ClassSearchPanel(ClassLoader cl, IThreadPool tp, 
		boolean interfaces, boolean absclasses, boolean classes, boolean innerclasses)
	{
		this.tp = tp==null? new ThreadPool(): tp;
		
		final JTextField tfsearch = new JTextField();
		tfsearch.addKeyListener(new KeyListener()
		{
			protected boolean dirty = false;
			protected Timer t;
			
			{
				// Swing timer
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
		results.setDefaultRenderer(ClassInfo.class, new ClassCellRenderer());
		results.setTableHeader(null);
		
		cbif = new JCheckBox("Interfaces", interfaces);
		cbac = new JCheckBox("Abstract Classes", absclasses);
		cbc = new JCheckBox("Classes", classes);
		cbic = new JCheckBox("Inner Classes", innerclasses);
		
		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				performSearch(tfsearch.getText());
			}
		};
		
		cbif.addActionListener(al);
		cbac.addActionListener(al);
		cbc.addActionListener(al);
		cbic.addActionListener(al);
		
		JPanel cbp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cbp.add(cbif);
		cbp.add(cbac);
		cbp.add(cbc);
		cbp.add(cbic);
		
		setLayout(new GridBagLayout());
		
		int y=0;
		
		add(new JLabel("Enter type name prefix:"), new GridBagConstraints(0,y++,
			2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));
		
		add(tfsearch, new GridBagConstraints(0,y++,2,1,1,0,
			GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		add(cbp, new GridBagConstraints(0,y++,
			2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2),0,0));
		
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
	
	protected TerminableFuture<Void> lastsearch;
	/**
	 *  Perform a search using a search expression.
	 */
	public void performSearch(final String exp)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		final ISubscriptionIntermediateFuture<Class<?>>[] fut = new ISubscriptionIntermediateFuture[1];
		
		if(lastsearch!=null)
			lastsearch.terminate();
		
		final TerminableFuture<Void> ret = new TerminableFuture<Void>(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
//				System.out.println("terminating: "+exp);
				if(fut[0]!=null)
					fut[0].terminate();
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		
		lastsearch = ret;
		
		if(exp==null || exp.length()==0)
		{
			status.setText("idle");
			lastsearch.setResultIfUndone(null);
			return;
		}
		
		final Pattern pat = SUtil.createRegexFromGlob(exp+"*");
		
		setCurrentQuery(exp);
//		System.out.println("perform search: "+exp);
		
//		ctm.clear();
		
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
						
						if(!cbic.isSelected() && fn.indexOf("$")!=-1)
						{
							return false;
						}
						
						StringTokenizer stok = new StringTokenizer(exp, "*?");
						boolean ret = true;
						while(ret && stok.hasMoreElements())
						{
							String tst = stok.nextToken();
							ret = fn.indexOf(tst)!=-1;
						}
						return ret;
					}
				};
				IFilter<Class<?>> classfilter = new IFilter<Class<?>>()
				{
					public boolean filter(Class<?> clazz)
					{
//						System.out.println("found: "+clazz);
						boolean in = clazz.isInterface();
						boolean abs = Modifier.isAbstract(clazz.getModifiers()); 
						boolean ret = (in && cbif.isSelected())
							|| (!in && abs && cbac.isSelected())
							|| (!in && !abs && cbc.isSelected());
						
						if(ret)
						{
							String clname = SReflect.getInnerClassName(clazz);
							
							if(exp.indexOf("*")==-1 && exp.indexOf("?")==-1)
							{
								ret = clname.startsWith(exp);
							}
							else
							{
								Matcher m = pat.matcher(clname);
								ret = m.matches(); 
							}
						}
						
						return ret;
					}
				};
				
				fut[0] = SReflect.asyncScanForClasses(cl, filefilter, classfilter, -1, true);
				
				fut[0].addResultListener(new SwingIntermediateResultListener<Class<?>>(new IIntermediateResultListener<Class<?>>()
				{
					List<Class<?>> res = new ArrayList<Class<?>>();
					
					public void intermediateResultAvailable(Class<?> result)
					{
						if(!ret.isDone())
							res.add(result);
//						ctm.addEntry(result);
					}
					
					public void finished()
					{
						if(!ret.isDone())
						{
							ctm.clear();
							for(Class<?> cl: res)
								ctm.addEntry(cl);
							if(ctm.size()>0)
							{
				                results.changeSelection(0, 0, false, false);
								results.requestFocus();
							}
							
							status.setText("searching '"+exp+"' ("+ctm.size()+") finished");
							
							ret.setResultIfUndone(null);
						}
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
						
						lastsearch.setExceptionIfUndone(exception);
					}
				}));
			}
		});
		
		return;
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
			ClassInfo ci = ctm.getClass(sel);
			ret = SReflect.findClass0(ci.getPkg()+"."+ci.getName(), null, cl);
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
		
		pan.results.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
			.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
		pan.results.getActionMap().put("Enter", new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				ok[0] = true;
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
//		protected List<Class<?>> entries;
		protected List<ClassInfo> entries;
		
		/**
		 *  Get the column count.
		 */
		public int getColumnCount()
		{
			return 1;
		}

		/**
		 *  Add a new entry.
		 */
		public void addEntry(Class<?> clazz)
		{
			if(entries==null)
				entries = new ArrayList<ClassInfo>();
			
			ClassInfo ci = new ClassInfo(clazz);
			
			int done = -1;
			int cnt = entries.size();
			if(cnt>0)
			{
				for(int i=0; i<cnt && done==-1; i++)
				{
					ClassInfo tmp = entries.get(i);
					if(tmp.getName().compareTo(ci.getName())>=0)
					{
						entries.add(i, ci);
						done = i;
					}
				}
			}
			
			if(done==-1)
			{
				entries.add(ci);
				done = entries.size()-1;
			}
			
			fireTableRowsInserted(done, done);
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
					ret = entries.get(rowIndex);
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
			return "Name";
		}

		/**
		 * @param columnIndex
		 * @return the class of a column
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		public Class<?> getColumnClass(int columnIndex)
		{
			return ClassInfo.class;
		}

		/**
		 *  Get the class.
		 */
		public ClassInfo getClass(int i)
		{
			return entries!=null? entries.get(i): null;
		}
		
		/**
		 *  Remove all entries.
		 */
		public void clear()
		{
			if(entries!=null && !entries.isEmpty())
			{
				int size = entries.size();
				entries.clear();
				fireTableRowsDeleted(0, size-1);
			}
		}
		
		/**
		 *  Get the size.
		 */
		public int size()
		{
			return entries!=null? entries.size(): 0;
		}
	}

	/**
	 * 
	 */
	static class ClassCellRenderer extends DefaultTableCellRenderer
	{
		/**
		 * 
		 */
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
		{
			ClassInfo clazz = (ClassInfo)value;
			int sel = table.getSelectedRow();
			
			String clname = SReflect.getUnqualifiedTypeName(clazz.getName());
			
			String val;
			if(sel==row)
			{
				val = clname+" - "+clazz.getPkg();
			}
			else
			{
				val = clname;
			}
			
			if(clazz.getType().equals(ClassInfo.Type.INTERFACE))
			{
				setIcon(icons.getIcon("interface"));
			}
			else if(clazz.getType().equals(ClassInfo.Type.ABSTRACTCLASS))
			{
				setIcon(icons.getIcon("abstractclass"));
			}
			else
			{
				setIcon(icons.getIcon("class"));
			}
			
			return super.getTableCellRendererComponent(table, val, isSelected, hasFocus, row, column);
		}
	}
	
	/**
	 * 
	 */
	static class ClassInfo
	{
		public static enum Type{INTERFACE, ABSTRACTCLASS, CLASS};

		protected String name;
		
		protected String pkg;
		
		protected Type type;

		/**
		 *  Create a new class info.
		 */
		public ClassInfo(Class<?> clazz)
		{
			this.name = SReflect.getUnqualifiedClassName(clazz);
			this.pkg = clazz.getPackage().getName();
			if(clazz.isInterface())
			{
				type = Type.INTERFACE;
			}
			else if(Modifier.isAbstract(clazz.getModifiers()))
			{
				type = Type.ABSTRACTCLASS;
			}
			else
			{
				type = Type.CLASS;
			}
		}
		
		/**
		 *  Create a new class info.
		 */
		public ClassInfo(String name, Type type)
		{
			this.name = name;
			this.type = type;
		}

		/**
		 *  Get the name.
		 *  @return The name.
		 */
		public String getName()
		{
			return name;
		}

		/**
		 *  Set the name.
		 *  @param name The name to set.
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 *  Get the type.
		 *  @return The type.
		 */
		public Type getType()
		{
			return type;
		}

		/**
		 *  Set the type.
		 *  @param type The type to set.
		 */
		public void setType(Type type)
		{
			this.type = type;
		}

		/**
		 *  Get the pkg.
		 *  @return The pkg.
		 */
		public String getPkg()
		{
			return pkg;
		}

		/**
		 *  Set the pkg.
		 *  @param pkg The pkg to set.
		 */
		public void setPkg(String pkg)
		{
			this.pkg = pkg;
		}
		
	}
}

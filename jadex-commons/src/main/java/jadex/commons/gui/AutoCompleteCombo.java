package jadex.commons.gui;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;


/**
 * Autocomplete combobox with filtering and text inserting of new text
 * 
 * @author Exterminator13
 */
public class AutoCompleteCombo extends JComboBox
{
	private Model model = new Model();

	private final JTextComponent textcomp;

	private boolean updatepopup;

	protected ClassLoader cl;
	
	protected IThreadPool tp;

	protected String lastpattern;

	protected ISubscriptionIntermediateFuture<Class<?>> current;
	
	/**
	 * 
	 */
	public AutoCompleteCombo(ThreadPool tp)
	{
		this.tp = tp==null? new ThreadPool(): tp;
		this.textcomp = (JTextComponent)getEditor().getEditorComponent();
		setEditable(true);

		setPattern(null);
		updatepopup = false;

		textcomp.setDocument(new AutoCompleteDocument());
		setModel(model);
		setSelectedItem(null);

		new Timer(20, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(updatepopup && isDisplayable())
				{
					setPopupVisible(false);
					if(model.getSize() > 0)
					{
						setPopupVisible(true);
					}
					updatepopup = false;
				}
			}
		}).start();
	}

	/**
	 * 
	 */
	private class AutoCompleteDocument extends PlainDocument
	{
		boolean	arrowkey = false;

		public AutoCompleteDocument()
		{
			textcomp.addKeyListener(new KeyAdapter()
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
								updateModel();
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
				
				public void keyPressed(KeyEvent e)
				{
					int key = e.getKeyCode();
					if(key == KeyEvent.VK_ENTER)
					{
						// there is no such element in the model for now
						String text = textcomp.getText();
						if(!model.contains(text))
						{
							addToTop(text);
						}
					}
					else if(key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN)
					{
						arrowkey = true;
					}
				}
			});
		}

		/**
		 * 
		 */
		protected void updateModel() //throws BadLocationException
		{
			try
			{
				String textToMatch = getText(0, getLength());
				setPattern(textToMatch);
			}
			catch(Exception e)
			{
			}
		}

		/**
		 * 
		 */
		public void remove(int offs, int len) throws BadLocationException
		{
			if(current!=null && !current.isDone())
				return;

			super.remove(offs, len);
			if(arrowkey)
			{
				arrowkey = false;
			}
//			else
//			{
//				updateModel();
//			}
			clearSelection();
		}

		/**
		 * 
		 */
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
		{
			if(current!=null && !current.isDone())
				return;

			// insert the string into the document
			super.insertString(offs, str, a);

			// if (enterKeyPressed) {
			// logger.debug("[insertString] enter key was pressed");
			// enterKeyPressed = false;
			// return;
			// }

			String text = getText(0, getLength());
			if(arrowkey)
			{
				model.setSelectedItem(text);
				arrowkey = false;
			}
//			else if(!text.equals(getSelectedItem()))
//			{
//				updateModel();
//			}

			clearSelection();
		}

	}

	/**
	 * 
	 * @param text
	 */
	public void setText(String text)
	{
		if(model.contains(text))
		{
			setSelectedItem(text);
		}
		else
		{
			addToTop(text);
			setSelectedIndex(0);
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getText()
	{
		return getEditor().getItem().toString();
	}

	/**
	 * 
	 */
	private ISubscriptionIntermediateFuture<Class<?>> setPattern(String pattern)
	{
		ISubscriptionIntermediateFuture<Class<?>> ret;
		if(pattern != null && pattern.trim().isEmpty())
			pattern = null;

		if(lastpattern == null && pattern == null || pattern != null && pattern.equals(lastpattern))
		{
			SubscriptionIntermediateFuture<Class<?>> fut = new SubscriptionIntermediateFuture<Class<?>>();
			fut.setResult(null);
			ret = fut;
		}
		else
		{
			lastpattern = pattern;

			if(current!=null)
				current.terminate();
		
			ret =  model.setPattern(pattern);
			current = ret;
		}
		return ret;
	}

	/**
	 * 
	 */
	private void clearSelection()
	{
		int i = getText().length();
		textcomp.setSelectionStart(i);
		textcomp.setSelectionEnd(i);
	}

	// @Override
	// public void setSelectedItem(Object anObject) {
	// super.setSelectedItem(anObject);
	// clearSelection();
	// }

	/**
	 * 
	 * @param aString
	 */
	public void addToTop(String aString)
	{
		model.addToTop(aString);
	}

	/**
	 * 
	 */
	protected class Model extends AbstractListModel implements ComboBoxModel
	{
		protected String selected;

		protected int limit	= 20;

		protected List<String> list = new ArrayList<String>(limit);
		
		/**
		 * 
		 */
		public ISubscriptionIntermediateFuture<Class<?>> setPattern(String pattern)
		{
			int size1 = getSize();

			ISubscriptionIntermediateFuture<Class<?>> ret = doSetPattern(pattern);

			int size2 = getSize();

			if(size1<size2)
			{
				fireIntervalAdded(this, size1, size2 - 1);
				fireContentsChanged(this, 0, size1 - 1);
			}
			else if(size1>size2)
			{
				fireIntervalRemoved(this, size2, size1 - 1);
				fireContentsChanged(this, 0, size2 - 1);
			}
			
			return ret;
		}

		/**
		 * 
		 */
		public void addToTop(String val)
		{
			if(val == null || list.contains(val))
				return;

			if(list.size() == 0)
			{
				list.add(val);
			}
			else
			{
				list.add(0, val);
			}
			
			while(list.size()>limit)
			{
				int index = list.size() - 1;
				list.remove(index);
			}

			setPattern(null);
			model.setSelectedItem(val);
		}

		/**
		 * 
		 */
		public Object getSelectedItem()
		{
			return selected;
		}

		/**
		 * 
		 */
		public void setSelectedItem(Object anObject)
		{
			if((selected != null && !selected.equals(anObject))
				|| selected == null && anObject != null)
			{
				selected = (String)anObject;
				fireContentsChanged(this, -1, -1);
			}
		}

		/**
		 * 
		 */
		public int getSize()
		{
			return list.size();
		}

		/**
		 * 
		 */
		public Object getElementAt(int index)
		{
			if(list==null || list.size()==0)
				System.out.println("aaaa");
			return list.get(index);
		}
		
		/**
		 * 
		 */
		protected ISubscriptionIntermediateFuture<Class<?>> doSetPattern(final String pattern)
		{
			assert SwingUtilities.isEventDispatchThread();
			
			ISubscriptionIntermediateFuture<Class<?>> ret;
			
			if(pattern==null || pattern.isEmpty())
			{
				if(list.size()>0)
					AutoCompleteCombo.this.setSelectedItem(model.getElementAt(0));
				
				ret = new SubscriptionIntermediateFuture<Class<?>>();
				((SubscriptionIntermediateFuture<Class<?>>)ret).setResult(null);
			}
			else
			{				
				list.clear();

				ret = performSearch(pattern, true, false, 
					true, false, limit);
				final ISubscriptionIntermediateFuture<Class<?>> fret = ret;
				
				ret.addResultListener(new IIntermediateResultListener<Class<?>>()
				{
					public void intermediateResultAvailable(Class<?> result)
					{
						if(current==fret)
						{
							list.add(result.getName());
						}
					}
					
					public void finished()
					{
						AutoCompleteCombo.this.setSelectedItem(pattern);
						updatepopup = true;
					}
					
					public void resultAvailable(Collection<Class<?>> result)
					{
						if(pattern.equals(AutoCompleteCombo.this.getSelectedItem()))
						{
							for(Class<?> clazz: result)
							{
								intermediateResultAvailable(clazz);
							}
							finished();
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				});
			}
			
			return ret;
		}

		/**
		 * 
		 */
		boolean contains(String s)
		{
			if(s == null || s.trim().isEmpty())
				return true;
			s = s.toLowerCase();
			for(String item : list)
			{
				if(item.toLowerCase().equals(s))
				{
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 *  Perform a search using a search expression.
	 */
	public ISubscriptionIntermediateFuture<Class<?>> performSearch(final String exp, 
		final boolean inter, final boolean abscla, final boolean cla, final boolean incla, final int max)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		final ISubscriptionIntermediateFuture<Class<?>>[] fut = new ISubscriptionIntermediateFuture[1];
		
		final SubscriptionIntermediateFuture<Class<?>> ret = new SubscriptionIntermediateFuture<Class<?>>(new ITerminationCommand()
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
		
		if(exp==null || exp.length()==0)
		{
//			status.setText("idle");
			ret.setResultIfUndone(null);
			return ret;
		}
		
		final Pattern pat = SUtil.createRegexFromGlob(exp+"*");
		
//		setCurrentQuery(exp);
//		System.out.println("perform search: "+exp);
		
//		ctm.clear();
		
//		if(exp!=null && exp.length()>0)
//		status.setText("searching '"+exp+"'");
						
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
						
						if(!incla && fn.indexOf("$")!=-1)
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
						boolean ret = (in && inter)
							|| (!in && abs && abscla)
							|| (!in && !abs && cla);
						
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
				
				fut[0] = SReflect.asyncScanForClasses(cl, filefilter, classfilter, max);
				
				fut[0].addResultListener(new SwingIntermediateResultListener<Class<?>>(new IIntermediateResultListener<Class<?>>()
				{
					List<Class<?>> res = new ArrayList<Class<?>>();
					
					public void intermediateResultAvailable(Class<?> result)
					{
//						if(!ret.isDone())
//							res.add(result);
//						ctm.addEntry(result);
//						System.out.println("found: "+result);
						ret.addIntermediateResultIfUndone(result);
					}
					
					public void finished()
					{
//						if(!ret.isDone())
//						{
//							ctm.clear();
//							for(Class<?> cl: res)
//								ctm.addEntry(cl);
//							if(ctm.size()>0)
//							{
//				                results.changeSelection(0, 0, false, false);
//								results.requestFocus();
//							}
//							
//							status.setText("searching '"+exp+"' ("+ctm.size()+") finished");
//							
							ret.setFinishedIfUndone();
//						}
					}
					
					public void resultAvailable(Collection<Class<?>> result)
					{
						for(Class<?>clazz: result)
						{
							intermediateResultAvailable(clazz);
						}
						finished();
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						status.setText("idle '"+exp+"'");
						
//						lastsearch.setExceptionIfUndone(exception);
						ret.setExceptionIfUndone(exception);
					}
				}));
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IThreadPool getThreadPool()
	{
		return tp;
	}
	
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new GridLayout(3, 1));
//				final JLabel label = new JLabel("label ");
//				frame.add(label);
				final AutoCompleteCombo combo = new AutoCompleteCombo(null);
				// combo.getEditor().getEditorComponent().addKeyListener(new
				// KeyAdapter() {
				//
				// @Override
				// public void keyReleased(KeyEvent e) {
				// if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				// String text = combo.getEditor().getItem().toString();
				// if(text.isEmpty())
				// return;
				// combo.addToTop(text);
				// }
				// }
				// });
				frame.add(combo);
//				JComboBox combo2 = new JComboBox(new String[]{"Item 1",
//						"Item 2", "Item 3", "Item 4"});
//				combo2.setEditable(true);
//				frame.add(combo2);
				frame.pack();
				frame.setSize(500, frame.getHeight());
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
package jadex.commons.gui.autocombo;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.plaf.metal.MetalComboBoxEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import jadex.commons.SUtil;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;


/**
 * Autocomplete combobox with filtering and text inserting of new text
 */
public class AutoCompleteCombo<T> extends JComboBox
{
	/** The cloassloader. */
	protected ClassLoader cl;
	
	/** The thread pool. */
	protected IThreadPool tp;

	/** The last pattern. */
	protected String lastpattern;

	/** The current call. */
	protected ISubscriptionIntermediateFuture<T> current;
	
	/** The flag that the model is modified. */
	protected boolean updating;
	
	/**
	 *  Create a new combo box.
	 */
	public AutoCompleteCombo(ThreadPool tp, ClassLoader cl)
	{
		this.tp = tp==null? new ThreadPool(): tp;
		this.cl = cl==null? AutoCompleteCombo.class.getClassLoader(): cl;
		setEditable(true);
		
		addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				if("JComboBox.isTableCellEditor".equals(evt.getPropertyName()) && Boolean.TRUE.equals(evt.getNewValue()))
				{
					putClientProperty("JComboBox.isTableCellEditor", Boolean.FALSE);
				}
			}
		});
		
		setEditor(new MetalComboBoxEditor()//BasicComboBoxEditor() // Hack due to bug in BasicComboBoxEditor
		{
			Object val;
			public void setItem(Object obj)
			{
				if(SUtil.equals(val, obj) || obj==null)
					return;
				
				String text = getAutoModel().convertToString((T)obj);
			    if(text!=null && !text.equals(editor.getText())) 
			    {
			    	val = obj;
			    	if(text.length()>0)
			    		editor.setText(text);
			    }
			}
			
			public Object getItem()
			{
				return getAutoModel().convertFromString(editor.getText());
			}
		});
	}
	
//	public void setSelectedItem(Object anObject) 
//	{
//		System.out.println("sel: "+anObject);
//		super.setSelectedItem(anObject);
//	}
	
	/**
	 * 
	 */
	public JTextComponent getEditorComponent()
	{
		JTextComponent comp = (JTextComponent)getEditor().getEditorComponent();
		return comp;
	}
	
	/**
	 * 
	 */
	public void setEditor(ComboBoxEditor anEditor)
	{
		super.setEditor(anEditor);
		JTextComponent comp = (JTextComponent)getEditor().getEditorComponent();
		if(comp.getDocument() instanceof AutoCompleteCombo.AutoCompleteDocument)
		{
			AutoCompleteCombo.AutoCompleteDocument doc = (AutoCompleteCombo.AutoCompleteDocument)comp.getDocument();
			doc.dispose();
		}
		comp.setDocument(new AutoCompleteDocument());
	}
//	
//	/**
//	 * 
//	 */
//	public void setRenderer(ListCellRenderer<? super T> aRenderer)
//	{
//		super.setRenderer(aRenderer);
//		JTextComponent comp = (JTextComponent)getEditor().getEditorComponent();
//		comp.setDocument(new AutoCompleteDocument());
//	}
	
	public void actionPerformed(ActionEvent e) 
	{	
//		System.out.println("box updating: "+isUpdating()+" "+e);
//		if(!isUpdating())
//		{
//			super.actionPerformed(e);
//		}
	}
	
	/**
	 *  Get the updating.
	 *  @return The updating.
	 */
	public boolean isUpdating()
	{
		return updating;
	}

	/**
	 * 
	 */
	public AbstractAutoComboModel<T> getAutoModel()
	{
		return (AbstractAutoComboModel<T>)getModel();
	}

//	/**
//	 * 
//	 * @param text
//	 */
//	public void setText(String text)
//	{
//		if(getAutoModel().contains(text))
//		{
//			setSelectedItem(text);
//		}
//		else
//		{
//			getAutoModel().addToTop(text);
//			setSelectedIndex(0);
//		}
//	}

	/**
	 *  Get the text.
	 */
	public String getText()
	{
		return getEditor().getItem()!=null? getEditor().getItem().toString(): "";
	}

	/**
	 * 
	 */
	private ISubscriptionIntermediateFuture<T> setPattern(String pattern)
	{
		ISubscriptionIntermediateFuture<T> ret;
		if(pattern != null && pattern.trim().isEmpty())
			pattern = null;

//		if(lastpattern == null && pattern == null || pattern != null && pattern.equals(lastpattern))
//		{
//			SubscriptionIntermediateFuture<T> fut = new SubscriptionIntermediateFuture<T>();
//			fut.setResult(null);
//			ret = fut;
//		}
//		else
		{
			lastpattern = pattern;

			if(current!=null)
				current.terminate();
		
			ret =  getAutoModel().setPattern(pattern);
			current = ret;
		}
		return ret;
	}
	
	/**
	 *  Get the current search.
	 *  @return The current search.
	 */
	public ISubscriptionIntermediateFuture<T> getCurrentSearch()
	{
		return current;
	}

	/**
	 * 
	 */
	private void clearSelection()
	{
		// Save caret position since selections moves the caret.
		int pos = getEditorComponent().getCaretPosition();
		int i = getText().length();
		getEditorComponent().setSelectionStart(i);
		getEditorComponent().setSelectionEnd(i);
		getEditorComponent().setCaretPosition(pos);
	}

	/**
	 *  Get the thread pool.
	 *  @return The thread pool.
	 */
	protected IThreadPool getThreadPool()
	{
		return tp;
	}
	
	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return cl;
	}
	
	/**
	 *  Update the popup.
	 */
	public void updatePopup()
	{
		try
		{
			hidePopup();
			showPopup();
			clearSelection();
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 * 
	 */
	protected class AutoCompleteDocument extends PlainDocument
	{
		protected boolean arrowkey = false;
		protected Timer t;
		
		/**
		 *  Create a new AutoCompleteDocument.
		 */
		public AutoCompleteDocument()
		{
			getEditorComponent().addKeyListener(new KeyAdapter()
			{
				protected boolean dirty = false;
				
				{
					// Swing timer
					t = new Timer(1000, new ActionListener()
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
					int key = e.getKeyCode();
//					System.out.println("typed: "+key);
				
//					getAutoModel().setSelectedItemQuiet(null);
					
					if(!t.isRunning() && key == KeyEvent.VK_ENTER)
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
//					getAutoModel().setSelectedItemQuiet(null);
					
					int key = e.getKeyCode();
//					System.out.println("pressed: "+key);
					if(key == KeyEvent.VK_ENTER)
					{
						// there is no such element in the model for now
						String text = getEditorComponent().getText();
						if(!getAutoModel().contains(text))
						{
							getAutoModel().addToTop(text);
						}
						else if(getSelectedItem()==null)
						{
//							System.out.println("setting manually");
							T obj = getAutoModel().getModelValue(text);
							if(obj!=null)
								getAutoModel().setSelectedItem(obj);
						}
						
						if(t.isRunning())
						{
							t.stop();
						}
						
//						System.out.println(getSelectedItem());
					}
					else if(key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN)
					{
						arrowkey = true;
						
//						T newItem = (T)getEditor().getItem();
//						System.out.println(newItem);
//				        setSelectedItem(newItem);
//				        getEditorComponent().setText(getAutoModel().convertToString(newItem));
				        
						if(t.isRunning())
						{
							t.stop();
						}
					}
				}
			});
		}

		/**
		 * 
		 */
		public void dispose()
		{
			if(t!=null)
				t.stop();
		}
		
		/**
		 * 
		 */
		protected void updateModel() //throws BadLocationException
		{
			updating = true;
			try
			{
				String text = getText(0, getLength());
				
				setPattern(text).addResultListener(new IResultListener<Collection<T>>()
				{
					public void resultAvailable(Collection<T> result) 
					{
						clearSelection();
						updatePopup();
						JTextComponent comp = (JTextComponent)getEditor().getEditorComponent();
						comp.setCaretPosition(getLength());
						
						updating = false;
					}
					
					public void exceptionOccurred(Exception exception)
					{
						updating = false;
					}
				});
			}
			catch(Exception e)
			{
				updating = false;
			}
		}

		/**
		 * 
		 */
		public void remove(int offs, int len) throws BadLocationException
		{
			if(current!=null && !current.isDone())
				return;

			String beftext = getText(0, getLength());
			
			super.remove(offs, len);
			
			String text = getText(0, getLength());
			if(arrowkey)
			{
				arrowkey = false;
			}
			else if(!text.equals(beftext))
			{
				updateModel();
			}
//			clearSelection();
		}

		/**
		 * 
		 */
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
		{
			if(current!=null && !current.isDone())
				return;

//			System.out.println("insert: "+str);
			
			String beftext = getText(0, getLength());
			
			// insert the string into the document
			super.insertString(offs, str, a);

			String text = getText(0, getLength());
			if(arrowkey)
			{
//				getAutoModel().setSelectedItem(text);
				arrowkey = false;
			}
			else if(!text.equals(beftext))
			{
				updateModel();
			}

//			clearSelection();
		}
		
		/**
		 * 
		 */
		public void replace(int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException
		{
			if(text == null || text.length()==0)
//			if(length == 0 && (text == null || text.length() == 0))
//			if(length == 0 || text == null || text.length() == 0)
			{
				return;
			}

//			System.out.println("replace: "+text);
			
			writeLock();
			try
			{
				boolean ark = arrowkey;
				if(length > 0)
				{
					remove(offset, length);
				}
				arrowkey = ark;
				if(text != null && text.length() > 0)
				{
					insertString(offset, text, attrs);
				}
			}
			finally
			{
				writeUnlock();
			}
		}
	}

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new GridLayout(2, 1));
				
//				final AutoCompleteCombo combo = new AutoCompleteCombo(null);
//				final ClassComboModel model = new ClassComboModel(combo, 20);
//				combo.setModel(model);
////				System.out.println(combo.getEditor().getClass());
////				MetalComboBoxEditor
//				combo.setEditor(new MetalComboBoxEditor()//BasicComboBoxEditor()
//				{
//					public void setItem(Object obj)
//					{
//						super.setItem(obj instanceof Class? model.convertToString((Class<?>)obj): obj);
//					}
//				});
//				combo.setRenderer(new BasicComboBoxRenderer()
//				{
//					public Component getListCellRendererComponent(JList list, Object value,
//						int index, boolean isSelected, boolean cellHasFocus)
//					{
//						Class<?> cl = (Class)value;
//						String txt = SReflect.getInnerClassName(cl)+" - "+cl.getPackage().getName();
//						return super.getListCellRendererComponent(list, txt, index, isSelected, cellHasFocus);
//					}
//				});
//				frame.add(combo);
				
				List<String> vals = SUtil.createArrayList(new String[]{"a", "aa", "aaa", "aab", "b", "bb", "abc"});
				final AutoCompleteCombo combo2 = new AutoCompleteCombo(null, null);
				final StringComboModel model2 = new StringComboModel(combo2, 20, vals);
				combo2.setModel(model2);
				frame.add(combo2);
				
				frame.pack();
				frame.setSize(500, frame.getHeight());
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
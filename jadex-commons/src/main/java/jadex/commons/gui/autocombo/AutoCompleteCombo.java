package jadex.commons.gui.autocombo;

import jadex.commons.SReflect;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.metal.MetalComboBoxEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;


/**
 * Autocomplete combobox with filtering and text inserting of new text
 * 
 * @author Exterminator13
 */
public class AutoCompleteCombo<T> extends JComboBox<T>
{
	private boolean updatepopup;

	protected ClassLoader cl;
	
	protected IThreadPool tp;

	protected String lastpattern;

	protected ISubscriptionIntermediateFuture<T> current;
	
	/**
	 * 
	 */
	public AutoCompleteCombo(ThreadPool tp)
	{
//		this.model = model;
		this.tp = tp==null? new ThreadPool(): tp;
		setEditable(true);

		setPattern(null);
		updatepopup = false;

		
//		setModel(model);
		setSelectedItem(null);

		new Timer(200, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(updatepopup && isDisplayable())
				{
					setPopupVisible(false);
					if(getAutoModel()!=null && getAutoModel().getSize() > 0)
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
		return getEditor().getItem().toString();
	}

	/**
	 * 
	 */
	private ISubscriptionIntermediateFuture<T> setPattern(String pattern)
	{
		ISubscriptionIntermediateFuture<T> ret;
		if(pattern != null && pattern.trim().isEmpty())
			pattern = null;

		if(lastpattern == null && pattern == null || pattern != null && pattern.equals(lastpattern))
		{
			SubscriptionIntermediateFuture<T> fut = new SubscriptionIntermediateFuture<T>();
			fut.setResult(null);
			ret = fut;
		}
		else
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
		int i = getText().length();
		getEditorComponent().setSelectionStart(i);
		getEditorComponent().setSelectionEnd(i);
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
		updatepopup = true;
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
						String text = getEditorComponent().getText();
						if(!getAutoModel().contains(text))
						{
							getAutoModel().addToTop(text);
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

//			System.out.println("insert: "+str);
			
			// insert the string into the document
			super.insertString(offs, str, a);

			String text = getText(0, getLength());
//			if(arrowkey)
//			{
//				getAutoModel().setSelectedItem(text);
//				arrowkey = false;
//			}
//			else if(!text.equals(getSelectedItem()))
//			{
//				updateModel();
//			}

			clearSelection();
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
				frame.setLayout(new GridLayout(1, 1));
				
				final AutoCompleteCombo combo = new AutoCompleteCombo(null);
				final ClassComboModel model = new ClassComboModel(combo, 20);
				combo.setModel(model);
//				System.out.println(combo.getEditor().getClass());
//				MetalComboBoxEditor
				combo.setEditor(new MetalComboBoxEditor()//BasicComboBoxEditor()
				{
					public void setItem(Object obj)
					{
						super.setItem(obj instanceof Class? model.convertToString((Class<?>)obj): obj);
					}
				});
				combo.setRenderer(new BasicComboBoxRenderer()
				{
					public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus)
					{
						Class<?> cl = (Class)value;
						String txt = SReflect.getInnerClassName(cl)+" - "+cl.getPackage().getName();
						return super.getListCellRendererComponent(list, txt, index, isSelected, cellHasFocus);
					}
				});

				frame.add(combo);
				frame.pack();
				frame.setSize(500, frame.getHeight());
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

}
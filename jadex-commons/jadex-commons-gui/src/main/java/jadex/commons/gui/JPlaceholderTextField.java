package jadex.commons.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import javax.swing.text.Highlighter;

/**
 *  Class offering a "placeholder" text similar to HTML input.
 *
 */
public class JPlaceholderTextField extends JTextField
{
	/** Flag if the placeholder is active */
	protected boolean activeplaceholder = true;
	
	/** The placeholder text. */
	protected String  placeholder = "";
	
	/** Color of the placeholder. */
	protected Color placeholdercolor;
	
	/** Regular foreground color. */
	protected Color foregroundcolor;
	
	/** Glow border for broken UIs like GTK. */
	protected RecolorLineBorder border;
	
	/** Animation timer. */
	protected Timer animtimer;
	
	/** Original color during animation. */
	protected Color origcolor;
	
	/** Color for invalid field. */
	protected Color invalidcolor;
	
	/** Document listener to intercept edits when in placeholder mode. */
	protected DocumentListener doclistener;
	
	/** The default highlighter to exchange with null when placeholder is active. */
	protected Highlighter highlighter;
	
//	protected TextUI brokenui = null;
//	
//	protected ComponentUI fakeui = null;
	
	/**
	 *  Creates the field.
	 */
	public JPlaceholderTextField()
	{
		highlighter = getHighlighter();
		placeholdercolor = Color.LIGHT_GRAY;
		invalidcolor = Color.RED;
		foregroundcolor = super.getForeground();
//		FocusListener flis = new FocusListener()
//		{
//			public void focusLost(FocusEvent e)
//			{
//				if ("".equals(JPlaceholderTextField.super.getText()))
//				{
//					activatePlaceholder();
//				}
//			}
//			
//			public void focusGained(FocusEvent e)
//			{
//				if (activeplaceholder)
//				{
//					deactivatePlaceholder();
//				}
//			}
//		};
//		addFocusListener(flis);
		
		addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				setCaretPosition(0);
			}
		});
		
		addCaretListener(new CaretListener()
		{
			public void caretUpdate(CaretEvent e)
			{
				if (activeplaceholder)
				{
					removeCaretListener(this);
					setCaretPosition(0);
					addCaretListener(this);
				}
			}
		});
		
		doclistener = new DocumentListener()
		{
			public void removeUpdate(final DocumentEvent e)
			{
				doclistener.changedUpdate(e);
			}
			
			public void insertUpdate(DocumentEvent e)
			{
				changedUpdate(e);
			}
			
			public void changedUpdate(final DocumentEvent e)
			{
				if ("".equals(JPlaceholderTextField.super.getText()))
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							
							if (EventType.REMOVE.equals(e.getType()))
								JPlaceholderTextField.super.setText("");
							
							activatePlaceholder();
						}
					});
				}
				else if (activeplaceholder)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							deactivatePlaceholder();
						}
					});
				}
			}
		};
		getDocument().addDocumentListener(doclistener);
		
		SGUI.addCopyPasteMenu(this);
		
		if (SGUI.BROKEN_UI_IDS.contains(UIManager.getLookAndFeel().getID()))
		{
			int s = 3;
//			border = new GlowBorder(s, s, s, s);
			border = new RecolorLineBorder(getBackground(), s);
			setBorder(border);
//			setBackground(Color.WHITE);
		}
		
//		System.out.println(getUI().getClass());
//		if (UIManager.getLookAndFeel().getID().equals("GTK"))
//		{
//			try
//			{
//				brokenui = (TextUI) ui;
//				Class<?> uicl = brokenui.getClass();
//				Class<?> sccl = Class.forName("javax.swing.plaf.synth.SynthContext");
//				Class<?> slfcl = Class.forName("javax.swing.plaf.synth.SynthLookAndFeel");
//				
//				final Method dispose = sccl.getDeclaredMethod("dispose", new Class[0]);
//				dispose.setAccessible(true);
//				final Method paint = uicl.getDeclaredMethod("paint", new Class[] { sccl, Graphics.class });
//				paint.setAccessible(true);
//				final Method paintbackground = uicl.getDeclaredMethod("paintBackground", new Class[] { sccl, Graphics.class, JComponent.class });
//				paintbackground.setAccessible(true);
//				final Method update = slfcl.getDeclaredMethod("update", new Class[] { sccl, Graphics.class });
//				update.setAccessible(true);
//				final Method getcontext = uicl.getMethod("getContext", new Class[] { JComponent.class });
//				
//				final Field style = uicl.getDeclaredField("style");
//				style.setAccessible(true);
//				
//				fakeui = new ComponentUI()
//				{
//					@Override
//					public void update(Graphics g, JComponent c)
//					{
//						try
//						{
//							Object context = getcontext.invoke(brokenui, c);
//							update.invoke(null, new Object[] { context, g });
//							paintbackground.invoke(brokenui, new Object[] { context, g, c });
//							style.get(brokenui)
//							paint.invoke(brokenui, new Object[] { context, g });
//							dispose.invoke(context, new Object[0]);
//						}
//						catch (Exception e)
//						{
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				};
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
	}
	/**
	 *  Gets the placeholder.
	 *
	 *  @return The placeholder.
	 */
	public String getPlaceholder()
	{
		return placeholder;
	}

	/**
	 *  Sets the placeholder.
	 *
	 *  @param placeholder The placeholder.
	 */
	public void setPlaceholder(String placeholder)
	{
		this.placeholder = placeholder;
		if (activeplaceholder)
			activatePlaceholder();
	}
	
	/**
	 *  Gets the placeholder color.
	 *  
	 *  @return The placeholder color.
	 */
	public Color getPlaceholderColor()
	{
		return placeholdercolor;
	}
	
	/**
	 *  Gets the foreground color that is not
	 *  the placeholder color (since getForeground()
	 *  must be overwritten).
	 */
	public Color getNonPlaceholderForeground()
	{
		return foregroundcolor;
	}
	
	/**
	 * 
	 */
	public void setForeground(Color fg)
	{
		this.foregroundcolor = fg;
		if (!activeplaceholder)
			super.setForeground(fg);
	}
	
	/**
	 *  Highlights an invalid field.
	 */
	public void showInvalid()
	{
		if (animtimer != null)
		{
			animtimer.stop();
			
			if (border != null)
				border.setColor(origcolor);
			else
				setBackground(origcolor);
		}
		
		AbstractAction anim = new AbstractAction()
		{
			/** Number of animation steps. */
			protected static final int STEPS = 30;
			
			/** Animation step. */
			protected int step = 0;
			
			/** Color components during transition. */
			protected double[] cc;
			
			/** Color difference. */
			protected double[] diff;
			
			/** Run */
			public void actionPerformed(ActionEvent e)
			{
				if (step == 0)
				{
					origcolor = getBackground();
					cc = SGUI.colorToRgba(invalidcolor);
					diff = SGUI.colorToRgba(origcolor);
//					System.out.println("O " + Arrays.toString(origcomps));
					
					for (int i = 0; i < diff.length; ++i)
					{
						diff[i] -= cc[i];
						diff[i] /= (STEPS - 1.0);
					}
				}
				
				Color c = SGUI.rgbaToColor(cc);
				if (border != null)
					border.setColor(c);
				else
					setBackground(c);
				
				repaint();
				
				for (int i = 0; i < cc.length; ++i)
					cc[i] += diff[i];
				++step;
				
				if (step >= STEPS)
				{
					((Timer) e.getSource()).stop();
					if (border != null)
						border.setColor(origcolor);
					else
						setBackground(origcolor);
				}
			}
		};
		animtimer = new Timer(17, anim);
		animtimer.start();
	}
	
	/**
	 *  Sets color of invalid field.
	 *  
	 *  @param invalidcolor The color.
	 */
	public void setInvalidColor(Color invalidcolor)
	{
		this.invalidcolor = invalidcolor;
	}
	
//	/**
//	 *  Override
//	 */
//	public void setBackground(Color bg)
//	{
//		if (border != null)
//		{
//			border = new LineBorder(bg, border.getThickness());
//			setBorder(border);
////			border.setInnerColor(bg);
////			border.setOuterColor(bg);
////			border.setOuterColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 127));
////			border.setOuterColorTransparent();
//		}
//		
//		super.setBackground(bg);
//	}
	
	/**
	 * 
	 */
	public String getText()
	{
		if (activeplaceholder)
			return "";
		return super.getText();
	}
	
	/**
	 *  Method for manually setting the text to a value, use instead of setText().
	 *  
	 *  @param t The text.
	 */
	public void setNonPlaceholderText(String t)
	{
		deactivatePlaceholder();
		super.setText(t);
	}
	
	/**
	 *  Sets the placeholder color.
	 *  
	 *  @param placeholdercolor The placeholder color.
	 */
	public void setPlaceholderColor(Color placeholdercolor)
	{
		this.placeholdercolor = placeholdercolor;
		if (activeplaceholder)
			super.setForeground(placeholdercolor);
	}
	
//	public TextUI getUI()
//	{
//		if (brokenui != null)
//			return brokenui;
//		return super.getUI();
//	}
//	
//	public void paint(Graphics g)
//	{
//		if (fakeui != null)
//		{
//			ui = fakeui;
//			super.paint(g);
//			ui = brokenui;
//		}
//		else
//		{
//			super.paint(g);
//		}
//	}
	
	/**
	 * 
	 */
//	public boolean isOpaque()
//	{
//		if (fakeui != null)
//			return false;
//		return super.isOpaque();
//	}
	
//	public String getName()
//	{
//		if (fakeui != null)
//			return "Tree.cellEditor";
//		// TODO Auto-generated method stub
//		return super.getName();
//	}
	
	/**
	 *  Activates the placeholder.
	 */
	protected void activatePlaceholder()
	{
		super.setForeground(placeholdercolor);
		activeplaceholder = true;
		getDocument().removeDocumentListener(doclistener);
		super.setText(placeholder);
		getDocument().addDocumentListener(doclistener);
		setHighlighter(null);
	}
	
	/**
	 *  Deactivates the placeholder.
	 */
	protected void deactivatePlaceholder()
	{
		super.setForeground(foregroundcolor);
		activeplaceholder = false;
		getDocument().removeDocumentListener(doclistener);
		String txt = super.getText();
		txt = txt.replace(placeholder, "");
		super.setText(txt);
		getDocument().addDocumentListener(doclistener);
		setHighlighter(highlighter);
	}
	
	/** Line border with changeable color */
	protected static class RecolorLineBorder extends LineBorder
	{
		/**
		 *  Create.
		 */
		public RecolorLineBorder(Color color, int thickness)
		{
			super(color, thickness);
		}
		
		/**
		 *  Sets the color.
		 *  
		 *  @param color The color.
		 */
		public void setColor(Color color)
		{
			lineColor = color;
		}
	}
}

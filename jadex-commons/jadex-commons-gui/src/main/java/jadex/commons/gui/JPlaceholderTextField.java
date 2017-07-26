package jadex.commons.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;

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
	
//	protected TextUI brokenui = null;
//	
//	protected ComponentUI fakeui = null;
	
	/**
	 *  Creates the field.
	 */
	public JPlaceholderTextField()
	{
		placeholdercolor = Color.LIGHT_GRAY;
		foregroundcolor = super.getForeground();
		FocusListener flis = new FocusListener()
		{
			public void focusLost(FocusEvent e)
			{
				if ("".equals(JPlaceholderTextField.super.getText()))
				{
					activatePlaceholder();
				}
			}
			
			public void focusGained(FocusEvent e)
			{
				if (activeplaceholder)
				{
					deactivatePlaceholder();
				}
			}
		};
		addFocusListener(flis);
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
	 * 
	 */
	public String getText()
	{
		if (activeplaceholder)
			return "";
		return super.getText();
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
		super.setText(placeholder);
	}
	
	/**
	 *  Deactivates the placeholder.
	 */
	protected void deactivatePlaceholder()
	{
		super.setForeground(foregroundcolor);
		activeplaceholder = false;
		super.setText("");
	}
}

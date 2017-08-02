package jadex.commons.gui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 *  A pop-up menu for text components to support cur, copy, paste, delete.
 *
 */
public class JCopyPasteContextMenu extends JPopupMenu
{
	/** The text component. */
	protected JTextComponent textcomp;
	
	/**
	 *  Creates the menu. 
	 * @param textcomponent The text component.
	 */
	public JCopyPasteContextMenu(JTextComponent textcomponent)
	{
		this.textcomp = textcomponent;
		setLightWeightPopupEnabled(false);
		
		if (textcomp.isEnabled() && textcomp.isEditable())
		{
			JMenuItem cut = new JMenuItem(new AbstractAction("Cut")
			{
				public void actionPerformed(ActionEvent e)
				{
					textcomp.cut();
				}
			});
			cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
			add(cut);
		}
		
		JMenuItem copy = new JMenuItem(new AbstractAction("Copy")
		{
			public void actionPerformed(ActionEvent e)
			{
				textcomp.copy();
			}
		});
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		add(copy);
		
		if (textcomp.isEnabled() && textcomp.isEditable())
		{
			JMenuItem paste = new JMenuItem(new AbstractAction("Paste")
			{
				public void actionPerformed(ActionEvent e)
				{
					textcomp.paste();
				}
			});
			paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
			add(paste);
		
		
			JMenuItem del = new JMenuItem(new AbstractAction("Delete")
			{
				public void actionPerformed(ActionEvent e)
				{
					if (textcomp.isEditable() && textcomp.isEnabled())
					{
						int start = textcomp.getSelectionStart();
						int end = textcomp.getSelectionEnd();
						
						if (end - start > 0)
						{
							try
							{
								textcomp.getDocument().remove(start, end - start);
							}
							catch (BadLocationException e1)
							{
							}
						}
					}
				}
			});
			add(del);
		}
	}
}

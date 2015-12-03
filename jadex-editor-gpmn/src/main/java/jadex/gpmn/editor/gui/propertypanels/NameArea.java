package jadex.gpmn.editor.gui.propertypanels;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;

import jadex.gpmn.editor.gui.DocumentAdapter;
import jadex.gpmn.editor.gui.ModelContainer;
import jadex.gpmn.editor.model.visual.VElement;

/**
 *  Class representing a text area for name changes on elements.
 *
 */
public class NameArea extends JTextArea
{
	/** The model container. */
	protected ModelContainer modelcontainer;
	
	/** The element. */
	protected VElement velement;
	
	/**
	 *  Creates a new text area.
	 *  @param container The model container.
	 *  @param visualelement The element.
	 */
	public NameArea(ModelContainer container, VElement visualelement)
	{
		this.modelcontainer = container;
		this.velement = visualelement;
		
		setText(velement.getElement().getName());
		
		addKeyListener(new KeyAdapter()
		{
			@SuppressWarnings("deprecation")
			public void keyPressed(KeyEvent e) 
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if (!((e.getModifiers() & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK))
					{
						e.consume();
						setText(velement.getElement().getName());
					}
					else
					{
						int mods = e.getModifiers();
						mods = mods & (~KeyEvent.CTRL_MASK);
						e.setModifiers(mods);
					}
				}
			}
		});
		
		addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent e)
			{
				setText(velement.getElement().getName());
			}
		});
		
		getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				String newname = getText();
				modelcontainer.getGraph().getModel().beginUpdate();
				modelcontainer.getGraph().getModel().setValue(velement, newname);
				modelcontainer.getGraph().getModel().endUpdate();
				modelcontainer.setDirty(true);
				if (newname.equals(velement.getElement().getName()))
				{
					setBackground(Color.WHITE);
				}
				else
				{
					setBackground(Color.PINK);
				}
			}
		});
	}
}

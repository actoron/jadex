package jadex.commons.gui.autocombo;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

/**
 * 
 */
public class AutoComboTableCellEditor extends DefaultCellEditor
{
	/**
	 * 
	 */
	public AutoComboTableCellEditor(JComboBox jbox)
	{
		super(jbox);
		
		final JComboBox box = (JComboBox)editorComponent;
		box.removeActionListener(delegate);
		
		delegate = new EditorDelegate()
		{
			public void setValue(Object value)
			{
				box.setSelectedItem(value);
			}

			public Object getCellEditorValue()
			{
				return box.getSelectedItem();
			}

			public boolean shouldSelectCell(EventObject anEvent)
			{
				if(anEvent instanceof MouseEvent)
				{
					MouseEvent e = (MouseEvent)anEvent;
					return e.getID() != MouseEvent.MOUSE_DRAGGED;
				}
				return true;
			}

			public boolean stopCellEditing()
			{
				if(box.isEditable())
				{
					// Commit edited value.
					box.actionPerformed(new ActionEvent(AutoComboTableCellEditor.this, 0, ""));
				}
				return super.stopCellEditing();
			}
			
			public void actionPerformed(ActionEvent e) 
			{
//				System.out.println(acc.isUpdating()+" "+e);
//				if(!acc.isUpdating())
//				{
//					super.actionPerformed(e);
//				}
			}
		};
		
		box.addActionListener(delegate);
		
		box.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
		{
			public void keyTyped(KeyEvent e)
			{
			}
			
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					stopCellEditing();
				}
			}
		});
	}
}
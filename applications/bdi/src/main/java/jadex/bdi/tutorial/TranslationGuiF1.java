package jadex.bdi.tutorial;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import jadex.commons.gui.SGUI;

/**
 *  The gui showing translation actions.
 */
public class TranslationGuiF1 extends JFrame
{
	//-------- attributes --------

	/** The list of all requests served. */
	protected DefaultTableModel tadata;

	//-------- constructors --------

	/**
	 *  Create a new gui.
	 */
	public TranslationGuiF1()
	{
		// create the gui.
		tadata	= new DefaultTableModel(new String[]{"Action", "Language", "Content", "Translation"}, 0);
		JTable tatable = new JTable(tadata);
		JScrollPane sp = new JScrollPane(tatable);
		this.getContentPane().add("Center", sp);
		this.pack();
		this.setLocation(SGUI.calculateMiddlePosition(this));
		this.setVisible(true);
	}

	//-------- methods --------

	/**
	 *  Add some content as new row in the table.
	 *  @param content The content.
	 */
	public void addRow(final String[] content)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				tadata.addRow(content);
			}
		});
	}
}

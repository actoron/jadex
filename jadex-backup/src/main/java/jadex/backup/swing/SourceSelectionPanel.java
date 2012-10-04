package jadex.backup.swing;

import jadex.base.gui.filetree.DefaultFileFilterMenuItemConstructor;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.bridge.IExternalAccess;
import jadex.commons.gui.PopupBuilder;
import jadex.commons.gui.SGUI;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * 
 */
public class SourceSelectionPanel extends JPanel
{
	/** The selection file tree panel. */
	protected FileTreePanel sourcet;

	/**
	 * 
	 */
	public SourceSelectionPanel(IExternalAccess exta)
	{
		setLayout(new BorderLayout());
		
		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Selected Sources "));
		
		sourcet = new FileTreePanel(exta);
		
		final DefaultFileFilterMenuItemConstructor mic = new DefaultFileFilterMenuItemConstructor(sourcet.getModel(), true);
		AddPathAction addp = new AddPathAction(sourcet);
		sourcet.setPopupBuilder(new PopupBuilder(new Object[]{addp, mic}));
		sourcet.setMenuItemConstructor(mic);
		
		add(sourcet, BorderLayout.CENTER);
	}
}

/**
 *  The refresh all action.
 */
class AddPathAction extends AbstractAction
{
	//-------- constants --------

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"add_path", SGUI.makeIcon(SourceSelectionPanel.class, "/jadex/backup/swing/images/add_folder_24.png"),
	});
	
	//-------- attributes --------

	/** The file tree panel. */
	protected FileTreePanel ftp;
	
	/** The selector tree panel. */
	protected FileTreePanel selsourcet;
	
	//-------- constructors --------

	/**
	 *  Create a new action.
	 */
	public AddPathAction(FileTreePanel ftp)
	{
		super("Add source", icons.getIcon("add_path"));
		this.ftp = ftp;
	}
	
	//-------- methods --------

	/**
	 *  Called when the action is performed.
	 */
	public void actionPerformed(ActionEvent e)
	{
		assert SwingUtilities.isEventDispatchThread();

		if(selsourcet==null)
		{
			selsourcet = new FileTreePanel(ftp.getExternalAccess());
			File[] roots = File.listRoots();
			for(File root: roots)
			{
				selsourcet.addTopLevelNode(root);
			}
		}
		
		final JDialog dia = new JDialog((JFrame)null, "Source Folder Selection", true);
		
		JButton bok = new JButton("OK");
		JButton bcancel = new JButton("Cancel");
		bok.setMinimumSize(bcancel.getMinimumSize());
		bok.setPreferredSize(bcancel.getPreferredSize());
		JPanel ps = new JPanel(new GridBagLayout());
		ps.add(bok, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2), 0, 0));
		ps.add(bcancel, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));

		dia.getContentPane().add(selsourcet, BorderLayout.CENTER);
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
		dia.pack();
		dia.setLocation(SGUI.calculateMiddlePosition(SGUI.getWindowParent(ftp), dia));
		dia.setVisible(true);
		if(ok[0])
		{
			String[] sels = selsourcet.getSelectionPaths();
			if(sels!=null)
			{
				for(String sel: sels)
				{
					ftp.addTopLevelNode(new File(sel));
				}
			}
		}
	}
	
	/**
	 *  Get the action name.
	 */
	public static String getName()
	{
		return "Add a new path";
	}
}


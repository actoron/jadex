package jadex.tools.common;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


/**
 *  Request dialog using a tree representation.
 *  Sets icon to OptionPane.questionIcon.
 */
public class JTreeDialog extends JDialog
{
	//-------- attributes --------

	/** The options (node, option). */
	protected Map options;

	/** The default value. */
	protected String def;

	/** The message. */
	protected String message;

	/** The result. */
	protected String result;

	//-------- constructor --------

	/**
	 *  Create a new dialog.
	 */
	public JTreeDialog(Frame owner, String title, boolean modal,
		String message, String[] options, String def)
	{
		super(owner, title, modal);
		this.message = message;
		this.options = new HashMap();
		this.def = def;

		Icon icon = UIManager.getIcon("OptionPane.questionIcon");
		JLabel iconlab = new JLabel("", icon, JLabel.CENTER);

		final JTree tree = createTree(options);
		tree.setRootVisible(false);

		// Select default node.
		if(def!=null)
		{
			StringTokenizer stok = new StringTokenizer(def, "/\\");
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)tree.getModel().getRoot();
			int cnt = stok.countTokens();
			for(int i=0; i<cnt-1 && parent!=null; i++)
			{
				String uo = stok.nextToken();
				parent = getNodeForUserObject(parent, uo);
			}
			if(parent!=null)
			{
				tree.expandPath(new TreePath(((DefaultTreeModel)
					tree.getModel()).getPathToRoot(parent)));
				DefaultMutableTreeNode node = getNodeForUserObject(
					parent, stok.nextToken());
				tree.getSelectionModel().addSelectionPath(
					new TreePath(((DefaultTreeModel)
					tree.getModel()).getPathToRoot(node)));
			}
		}

		JLabel msgtf = new JLabel(message);

		JButton ok = new JButton("Ok");
		JButton cancel = new JButton("Cancel");
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//System.out.println("Oki");
				TreePath sel = tree.getSelectionModel().getSelectionPath();
				if(sel!=null)
				{
					result	= (String)JTreeDialog.this.options
						.get(sel.getLastPathComponent());
				}
				dispose();
			}
		});
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//System.out.println("Cancelled");
				dispose();
			}
		});

		JPanel border = new JPanel(new GridBagLayout());
		border.add(tree, new GridBagConstraints(0, 0, 1, 1, 1, 1,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(10, 10, 10, 10), 0, 0));
		border.setBackground(tree.getBackground());
		JScrollPane center = new JScrollPane(border);
		JPanel south = new JPanel(new GridBagLayout());
		south.add(ok, new GridBagConstraints(0, 0, 1, 1, 0, 0,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(0, 0, 0, 10), 0, 0));
		south.add(cancel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(0, 0, 0, 0), 0, 0));

		ok.setMinimumSize(cancel.getMinimumSize());
		ok.setPreferredSize(cancel.getPreferredSize());
		center.setPreferredSize(tree.getMinimumSize());
		center.setPreferredSize(tree.getPreferredSize());

		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane().add(iconlab, new GridBagConstraints(0, 0, 1, 2, 0, 0,
			GridBagConstraints.NORTH, GridBagConstraints.NONE,
			new Insets(10, 10, 10, 15), 0, 0));
		this.getContentPane().add(msgtf, new GridBagConstraints(1, 0, 1, 1, 0, 0,
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(10, 0, 5, 10), 0, 0));
		this.getContentPane().add(center, new GridBagConstraints(1, 1, 1, 1, 1, 1,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 10, 10), 0, 0));
		this.getContentPane().add(south, new GridBagConstraints(0, 2, 2, 1, 0, 0,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(0, 10, 10, 10), 0, 0));

		this.setSize(400, 300);
		//this.setResizable(false);
		this.setLocationRelativeTo(owner);
	}

	//-------- methods --------

	/**
	 *  Get the result.
	 *  @return The selected string.
	 */
	public String getResult()
	{
		return this.result;
	}

	//-------- helper methods --------

	/**
	 *  Create the tree.
	 */
	protected JTree createTree(String[] options)
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		String delims = "/\\";//File.separator; //does not, jar file entries can have any seperator
		for(int i=0; i<options.length; i++)
		{
			StringTokenizer stok = new StringTokenizer(options[i], delims);
			DefaultMutableTreeNode parent = root;
			while(stok.hasMoreTokens())
			{
				String txt = stok.nextToken();
				DefaultMutableTreeNode node = getNodeForUserObject(parent, txt);
				if(node==null)
				{
					node = new DefaultMutableTreeNode(txt);
					parent.add(node);
				}
				parent = node;
			}
			this.options.put(parent, options[i]);
		}

		return new JTree(root);
	}

	/**
	 *  Get the children of a node that has the same user object.
	 *  @param node The node.
	 *  @param uo The user object.
	 *  @return The child node with same equal user object.
	 */
	protected DefaultMutableTreeNode getNodeForUserObject(
		DefaultMutableTreeNode node, Object uo)
	{
		Enumeration e = node.children();
		DefaultMutableTreeNode ret = null;

		while(ret==null && e.hasMoreElements())
		{
			DefaultMutableTreeNode tester = (DefaultMutableTreeNode)e.nextElement();
			if(uo.equals(tester.getUserObject()))
			{
				ret = tester;
			}
		}

		return ret;
	}

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		String[] opts = new String[]
		{
			"a1/1.xml",
			"a1/b1/2.xml",
			"a1/b2/c1/3.xml",
			"a2/b1/4.xml",
			"a3/b1/c1/5.xml",
		};

		JFrame f = new JFrame();
		JTreeDialog td = new JTreeDialog(f, "Titel", false,
			"Select an agent", opts, "a2/b1/4.xml");
		f.setLocation(400, 400);
		f.setVisible(true);

		td.setVisible(true);

		/*JOptionPane.showInputDialog(
			f, "Select an agent model to load:",
			"Select Agent Model",
			JOptionPane.QUESTION_MESSAGE,
			null, opts, opts[3]);*/
		//System.out.println(UIManager.getIcon("OptionPane.questionIcon"));
	}
}

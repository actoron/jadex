package jadex.commons.gui.jtreetable;



/**
 *  A test usage of the tree table component.
 */
public class JTreeTableTest
{
	/**
	 *  Main method for testing.
	 * /
	public static void	main(String[] args)
	{
		final DefaultTreeTableNode	root	= new DefaultTreeTableNode("Test");
		final DefaultTreeTableNode	node	= new DefaultTreeTableNode(new String[]{"Testchild0", "testvalue0"});
		node.add(new DefaultTreeTableNode(new String[]{"Testchild1", "testvalue1"}));
		node.add(new DefaultTreeTableNode(new String[]{"Testchild2", "testvalue2"}));
		root.add(node);
		root.add(new DefaultTreeTableNode(new String[]{"Testchild3", "testvalue3"}));


		final DefaultTreeTableModel	model	= new DefaultTreeTableModel(root,
			new String[]{"weissnich", "weissauchnich"});

		JTreeTable	tt = new JTreeTable(model);
		//tt.setShowGrid(true);
        JScrollPane sp = new JScrollPane(tt);
        sp.getViewport().setBackground(Color.white);
		JFrame	frame	= new JFrame("JTreeTableTest");
		frame.getContentPane().add(sp);
		frame.setSize(400, 300);
		frame.show();
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				System.exit(0);
			}
		});

		// Test adding nodes.
		for(int i=0; i<5; i++)
		{
			final int i2	= i;
			try{
				Thread.currentThread().sleep(1000);
			}catch(InterruptedException e){}

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					node.add(new DefaultTreeTableNode(new String[]{"Testchild"+i2, "testvalue"+i2}));
					root.add(new DefaultTreeTableNode(new String[]{"Testchild"+i2, "testvalue"+i2}));
					node.setValues(new String[]{"test"+i2, "value"+i2});
				}
			});
		}

		// Test removing nodes.
		for(int i=0; i<5; i++)
		{
			final int i2	= i;
			try{
				Thread.currentThread().sleep(1000);
			}catch(InterruptedException e){}

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					node.remove(node.getChild("Testchild"+i2));
					root.remove(root.getChild("Testchild"+i2));
					node.setValues(new String[]{"test"+i2, "value"+i2});
				}
			});
		}
	}*/
}


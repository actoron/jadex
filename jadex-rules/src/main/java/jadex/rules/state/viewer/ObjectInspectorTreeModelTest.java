package jadex.rules.state.viewer;

import jadex.commons.TreeExpansionHandler;
import jadex.rules.state.io.xml.Reader;
import jadex.rules.state.javaimpl.OAVState;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.Timer;

public class ObjectInspectorTreeModelTest extends OAVTreeModel
{
	
	/** The root node to inspect from */
	private ObjectInspectorNode root;
	
	/** Construct a test model */
	public ObjectInspectorTreeModelTest()
	{
		
		super(new OAVState(null));
		
		this.root = null;
		this.inspectors = new ArrayList();
		
	}

	/** 
	 * Set root node to inspect from 
	 * @param root tree root object
	 */
	public void setRoot(ObjectInspectorNode root)
	{
		ObjectInspectorNode oldRoot = this.root;
		this.root = root;
		fireTreeStructureChanged( new Object[]{oldRoot});
	}
	
	/**
	 * some simple tests
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{

//		ObjectInspectorTreeModelExample example = new ObjectInspectorTreeModelExample(Thread.currentThread());
//		JFrame f = ObjectInspectorTreeModel.createObjectInspectorFrame("Testframe", example);
//		f.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent e) {
//				System.exit(0);
//			}
//		});
//
//		f.show(true);
		
		
		final ObjectInspectorTreeModelTest model = new ObjectInspectorTreeModelTest();
		model.createAndTestStructureChange(model);
		
		
	}
	
	public void createAndTestStructureChange(ObjectInspectorTreeModelTest model) throws Exception
	{
		

		ObjectInspectorTreeModelExample example = new ObjectInspectorTreeModelExample(new String[]{"String","array", "as", "Object", "parameter"});
		
		// inspect the root node
		ObjectInspectorNode rootNode = new ObjectInspectorNode(null, example.getClass(), null, example);
		model.setRoot(rootNode);

		JTree tree = new JTree(model);
		new TreeExpansionHandler(tree);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(tree), BorderLayout.CENTER);
		
		JFrame frame = new JFrame("Threaded - Testframe");
		frame.getContentPane().add(panel,BorderLayout.CENTER);
		frame.setSize(600, 400);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		frame.show();
		
		Thread.sleep(1000);
		
		while(true)
		{
			for (int i = 0; i < 100; i++)
			{
				example.updateNodes(i);
				System.out.println("set Integer="+i);
				Thread.sleep(2000);
			}
		}
		
		//example.objectArrayAttribute = null;
		
		
//		model.setRoot(rootNode);
		
//		ObjectInspectorNode newRootNode = new ObjectInspectorNode(null, Thread.currentThread().getClass(), "Thread-Tree", Thread.currentThread());
//		model.setRoot(newRootNode);
		
	}
	
}



class ObjectInspectorTreeModelExample {

	

	public static final String STRING_ATTR = "A String Attribute";
	
	// ---- attributes -----
	
//	public boolean booleanAttribute = true;
//	public Boolean BooleanAttribute = new Boolean(false);
	
//	public int intAttribute = 1;
//	public int[] intArrayAttribute = new int[]{1,2,3};
//	
//	public Integer IntegerAttribute = new Integer(1);
//	public Integer[] IntegerArrayAttribute = new Integer[]{new Integer(6),new Integer(7),new Integer(8)};
	
//	public String stringAttribute = "A String Attribute";
	
//	public String[] stringArrayAttribute = new String[]{"one", "two", "three"};
	
	public Object objectAttribute = null;
	
//	public String stringTest1 = STRING_ATTR;
//	public String stringTest2 = STRING_ATTR;
	
//	public Object[] objectArrayAttribute = new Object[]{
//			new String("string object in array")
//			,new Boolean(true)
//			,new Reader()
//			,new Object()
//			,null
//			,null
//	};
	
	public Object[] secondObjectArray;
	
//	public TestObject1 test1;
//	public TestObject2 test2;
	public TestObject3 test3;
	
	
	
	// ---- constructor ----
	
	public ObjectInspectorTreeModelExample(Object objectAttr)
	{
		this.objectAttribute = objectAttr;
		this.test3 = new TestObject3();
//		this.test2 = new TestObject2(test3);
//		this.test1 = new TestObject1(test2);
//		objectArrayAttribute[5] = test3;
		secondObjectArray = new Object[]{test3, test3, test3};

	}
	
	// ---- methods ----

	
	public void updateNodes(int x)
	{
//		this.intAttribute = x;
//		this.IntegerAttribute = new Integer(x);
//		this.IntegerArrayAttribute[0] = IntegerAttribute;
//		this.stringArrayAttribute[0] = "As String: " + IntegerAttribute.toString();
		this.test3.someStupidIntToChange = x;
//		if (x > 0)
//			this.objectArrayAttribute[4] = null;
//		if (x > 20)
//			this.objectArrayAttribute[4] = Thread.currentThread();
//		if (x > 40)
//			this.objectArrayAttribute[4] = test3;
		
		if (x < 2)
			this.secondObjectArray[0] = "Buhuuuu >5";
		else if (x < 5)
			this.secondObjectArray[0] = Thread.currentThread();
		else if (x < 10)
			this.secondObjectArray[0] = new Reader();
		else if (x < 40)
		{
			
			this.secondObjectArray = new Object[]{"der is neu ne :-)", test3};
		}
		

	}
	
}

class TestObject1
{
	TestObject2 test2;
	public TestObject1(TestObject2 test2)
	{
		super();
		this.test2 = test2;
	}
}

class TestObject2
{
	TestObject3 test3;
	public TestObject2(TestObject3 test3)
	{
		super();
		this.test3 = test3;
	}
}

class TestObject3
{
	int someStupidIntToChange = 5;
}

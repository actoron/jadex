package jadex.rules.tools.stateviewer;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import jadex.commons.gui.TreeExpansionHandler;
import jadex.rules.state.javaimpl.OAVStateFactory;

public class ObjectInspectorTreeModelTest extends OAVTreeModel
{
	
	/** The root node to inspect from */
	private ObjectInspectorNode root;
	
	/** Construct a test model */
	public ObjectInspectorTreeModelTest()
	{
		
		super(OAVStateFactory.createOAVState(null));
		
		this.root = new ObjectInspectorNode(super.root,String.class,"default-name","default");
		
		this.inspectors = new ArrayList();
		
	}

	/** 
	 * Set root node to inspect from 
	 * @param root tree root object
	 */
	public void setObjectRootNode(ObjectInspectorNode root)
	{
		ObjectInspectorNode oldRoot = this.root;
		this.root = root;
		this.root.parent = super.root;
		
		super.root.children = new ArrayList();
		super.root.children.add(this.root);
		
		fireTreeStructureChanged( new Object[]{super.root, oldRoot});
	}
	
	/**
	 * some simple tests
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{

		final ObjectInspectorTreeModelTest model = new ObjectInspectorTreeModelTest();
		model.createAndTestStructureChange(model);
		
		
	}
	
	public void createAndTestStructureChange(ObjectInspectorTreeModelTest model) throws Exception
	{

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
		
		frame.setVisible(true);
		
		ObjectInspectorTreeModelExample example = new ObjectInspectorTreeModelExample(new String[]{"String","array", "as", "Object", "parameter"});
		// inspect the root node
		ObjectInspectorNode rootNode = new ObjectInspectorNode(null, example.getClass(), null, example);
		model.setObjectRootNode(rootNode);
		
		Thread.sleep(1000);
		
		while(true)
		{
			for (int i = 1; i < 100; i++)
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
//	public Integer IntegerAttribute = Integer.valueOf(1);
//	public Integer[] IntegerArrayAttribute = Integer.valueOf[]{Integer.valueOf(6),Integer.valueOf(7),Integer.valueOf(8)};
	
//	public String stringAttribute = "A String Attribute";
	
//	public String[] stringArrayAttribute = new String[]{"one", "two", "three"};
	
//	public Object objectAttribute = null;
	
//	public String stringTest1 = STRING_ATTR;
//	public String stringTest2 = STRING_ATTR;
	
	public Object[] objectArrayAttribute = new Object[]{
			"string object in array"
			,Boolean.TRUE
//			,new Reader()
			,new Object()
			,null
			,null
	};
	
	public Object[] secondObjectArray;
	
//	public TestObject1 test1;
//	public TestObject2 test2;
	public TestObject3 test3;
//	public TestObject3 test3_1;
	
	public TestArrayList arrayList = new TestArrayList();
	
	
	// ---- constructor ----
	
	public ObjectInspectorTreeModelExample(Object objectAttr)
	{
//		this.objectAttribute = objectAttr;
		this.test3 = new TestObject3(1);
//		this.test3_1 = this.test3;
//		this.test2 = new TestObject2(test3);
//		this.test1 = new TestObject1(test2);
		objectArrayAttribute[5] = test3;
		secondObjectArray = new Object[]{test3, test3, test3};

		arrayList.add(new TestObject3(1));
		arrayList.add(new TestObject3(2));
		arrayList.add(new TestObject3(5));
		
	}
	
	// ---- methods ----

	
	public void updateNodes(int x)
	{
//		this.intAttribute = x;
//		this.IntegerAttribute = Integer.valueOf(x);
//		this.IntegerArrayAttribute[0] = IntegerAttribute;
//		this.stringArrayAttribute[0] = "As String: " + IntegerAttribute.toString();
		this.test3.someStupidIntToChange = x;
		if (x > 0)
			this.objectArrayAttribute[4] = null;
		if (x > 20)
			this.objectArrayAttribute[4] = Thread.currentThread();
		if (x > 40)
			this.objectArrayAttribute[4] = test3;
		
		if (x < 2)
			this.secondObjectArray[0] = "Buhuuuu >5";
		else if (x < 5)
			this.secondObjectArray[0] = Thread.currentThread();
//		else if (x < 10)
//			this.secondObjectArray[0] = new Reader();
		else if (x < 40)
		{
			
			this.secondObjectArray = new Object[]{"der is neu ne :-)", test3};
		}
		
		List l = arrayList.myList;
		for (int i = 0; i < l.size(); i++)
		{
			TestObject3 obj = (TestObject3) l.get(i);
			l.remove(i);
			obj.someStupidIntToChange = obj.someStupidIntToChange+x;
			l.add(i, obj);
		}
		
		if (x == 20)
		{
			List newList = new ArrayList();
			newList.add(new TestObject3(10));
			newList.add(new TestObject3(300));
			arrayList.myList = newList;
//			arrayList.transientList = newList;
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
	int someStupidIntToChange;
	
	public TestObject3(int stupidInt)
	{
		this.someStupidIntToChange = stupidInt;
	}
	
	public String toString()
	{
		return "Test3("+this.someStupidIntToChange+")";
	}
}

class TestArrayList
{
	public List myList = new ArrayList();
//	public transient List transientList = new ArrayList();
	
	public void add(Object obj)
	{
		myList.add(obj);
//		transientList.add(obj);
	}
	
	public void remove(Object obj)
	{
		myList.remove(obj);
//		transientList.remove(obj);
	}
	
	public String toString()
	{
//		String ret = "[";
//		for (Iterator iterator = myList.iterator(); iterator.hasNext();)
//		{
//			Object obj = (Object) iterator.next();
//			ret += obj.toString();
//			if (iterator.hasNext())
//				ret += ", ";
//			else
//				ret += "]";
//			
//		}
//		return "TestArrayList" + ret;
		return "myList:"+myList ;//+ "\ntransientList:"+transientList;
	}
}

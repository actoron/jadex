package jadex.xml.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.collection.MultiCollection;
import jadex.xml.IContext;
import jadex.xml.StackElement;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.stax.ILocation;
import jadex.xml.stax.QName;
import jadex.xml.stax.XMLReporter;

/**
 *  Context for reader that stores all relevant information of the read process.
 */
public class AReadContext implements IContext
{
	//-------- attributes --------
	
	/** The type info path manager. */
	protected TypeInfoPathManager pathmanager;
	
	/** The default object handler. */
	protected IObjectReaderHandler defaulthandler;
	
	/** The parser. */
	protected IXMLReader parser;
	
	/** The parser. */
	protected XMLReporter reporter;
	
	/** The classloader. */
	protected ClassLoader classloader;
	
	/** The root object. */
	protected Object rootobject;
	
	/** The stack. */
	protected List<StackElement> stack;
	
	/** The current comment. */
	protected String comment;
	
	/** The read objects per id. */
	protected Map<String, Object> readobjects;
	
	/** The readignore counter (0=do not ignore). */
	protected int readignore;
	
	/** The call context. */
	protected Object callcontext;
	
	/** The post processors. */
	protected MultiCollection<Integer, IPostProcessorCall> postprocessors;
	
	/** The map or array information. */
	protected Map<Object, Integer> arrayinfos;
	
	/** The map of objects to link in bulk mode (object -> map of tags -> objects per tag). */
	protected MultiCollection<Object, LinkData> children;
	
	//-------- constructors --------

	/**
	 * @param parser
	 */
	public AReadContext(TypeInfoPathManager pathmanager, IObjectReaderHandler handler, IXMLReader parser, XMLReporter reporter, Object callcontext, ClassLoader classloader)
	{
		this(pathmanager, handler, parser, reporter, callcontext, classloader, null, new ArrayList<StackElement>(), 
			null, null, new HashMap<String, Object>(), 0, new MultiCollection<Integer, IPostProcessorCall>());
	}
	
	/**
	 *  Create a new read context.
	 * @param parser
	 * @param root
	 * @param stack
	 * @param topse
	 * @param comment
	 * @param readobjects
	 */
	public AReadContext(TypeInfoPathManager pathmanager, IObjectReaderHandler handler, IXMLReader parser, XMLReporter reporter, Object callcontext, ClassLoader classloader,
						Object root, List<StackElement> stack, StackElement topse, String comment, Map<String, Object> readobjects, int readignore, MultiCollection<Integer, IPostProcessorCall> postprocessors)
	{
		this.pathmanager = pathmanager;
		this.defaulthandler = handler;
		this.parser = parser;
		this.reporter = reporter;
		this.callcontext = callcontext;
		this.classloader = classloader;
//		this.rootobject = root;
		this.stack = stack;
//		this.topse = topse;
		this.comment = comment;
		this.readobjects = readobjects;
		this.readignore = readignore;
		this.postprocessors = postprocessors;
	}

	//-------- methods --------
	
	/**
	 *  Get the parser.
	 *  @return The parser.
	 */
	public IXMLReader getParser()
	{
		return parser;
	}

//	/**
//	 *  Set the parser.
//	 *  @param parser The parser to set.
//	 */
//	public void setParser(XMLStreamReader parser)
//	{
//		this.parser = parser;
//	}

	/**
	 *  Get the pathManager.
	 *  @return the pathManager.
	 */
	public TypeInfoPathManager getPathManager()
	{
		return pathmanager;
	}
	
	/**
	 *  Get the defaulthandler.
	 *  @return the defaulthandler.
	 */
	public IObjectReaderHandler getDefaultHandler()
	{
		return defaulthandler;
	}

	/**
	 *  Get the reporter.
	 *  @return The reporter.
	 */
	public XMLReporter getReporter()
	{
		return reporter;
	}

	/**
	 *  Get the root object.
	 *  @return The root object.
	 */
	public Object getRootObject()
	{
		return rootobject!=null ? rootobject
			: stack!=null && !stack.isEmpty()? ((StackElement) stack.get(0)).getObject()
			: null;
	}

	/**
	 *  Get the current object.
	 *  @return The current object.
	 * /
	public Object getCurrentObject()
	{
		return	getTopStackElement().getObject();
	}*/

//	/**
//	 *  Set the root object.
//	 *  @param root The rootobject to set.
//	 */
//	public void setRootObject(Object root)
//	{
//		this.rootobject = root;
//	}

//	/**
//	 *  Get the stack.
//	 *  @return The stack.
//	 */
//	public List getStack()
//	{
//		return stack;
//	}
//
//	/**
//	 *  Set the stack.
//	 *  @param stack The stack to set.
//	 */
//	public void setStack(List stack)
//	{
//		this.stack = stack;
//	}
	
	/**
	 *  Get stack element.
	 */
	public StackElement getStackElement(int pos)
	{
		return (StackElement)stack.get(pos);
	}

	/**
	 *  Get the top stack element.
	 *  @return The top stack element (if any).
	 */
	public StackElement getTopStackElement()
	{
		StackElement	ret;
		if(stack.isEmpty())
		{
			ret	= null;
		}
		else
		{
			ret	= (StackElement)stack.get(stack.size()-1);
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public void addStackElement(StackElement elem)
	{
		stack.add(elem);
	}
	
	/**
	 * 
	 */
	public void setStackElement(StackElement elem, int pos)
	{
		stack.set(pos, elem);
	}
	
	/**
	 * 
	 */
	public void removeStackElement()
	{	
		StackElement elem = (StackElement)stack.remove(stack.size()-1);
		if(stack.size()==0)
			rootobject = elem.getObject();
	}

	/**
	 *  
	 */
	public int getStackSize()
	{
		return stack.size();
	}
	
	/**
	 *  Get the comment.
	 *  @return The comment.
	 */
	public String getComment()
	{
		return comment;
	}

	/**
	 *  Set the comment.
	 *  @param comment The comment to set.
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
	}

	/**
	 *  Get the readobjects.
	 *  @return The readobjects.
	 */
	public Map<String, Object> getReadObjects()
	{
		return readobjects;
	}

	/**
	 *  Set the readobjects.
	 *  @param readobjects The readobjects to set.
	 */
	public void setReadObjects(Map<String, Object> readobjects)
	{
		this.readobjects = readobjects;
	}

	/**
	 *  Get the readignore.
	 *  @return The readignore.
	 */
	public int getReadIgnore()
	{
		return readignore;
	}

	/**
	 *  Set the readignore.
	 *  @param readignore The readignore to set.
	 */
	public void setReadIgnore(int readignore)
	{
		this.readignore = readignore;
	}

	/**
	 *  Get the callcontext.
	 *  @return The callcontext.
	 */
	public Object getUserContext()
	{
		return callcontext;
	}

	/**
	 *  Set the callcontext.
	 *  @param callcontext The callcontext to set.
	 */
	public void setCallContext(Object callcontext)
	{
		this.callcontext = callcontext;
	}

	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return classloader;
	}

	/**
	 *  Set the classloader.
	 *  @param classloader The classloader to set.
	 */
	public void setClassLoader(ClassLoader classloader)
	{
		this.classloader = classloader;
	}

	/**
	 *  Get the postprocessors.
	 *  @return The postprocessors.
	 */
	public MultiCollection<Integer, IPostProcessorCall> getPostProcessors()
	{
		return postprocessors;
	}

	/**
	 *  Set the postprocessors.
	 *  @param postprocessors The postprocessors to set.
	 */
	public void setPostProcessors(MultiCollection<Integer, IPostProcessorCall> postprocessors)
	{
		this.postprocessors = postprocessors;
	}	
	
	/**
	 *  Get the current array counter.
	 */
	public int getArrayCount(Object parent)
	{
		int ret = 0;
		
		if(arrayinfos==null)
			arrayinfos = new HashMap<Object, Integer>();
		
		if(arrayinfos.containsKey(parent))
			ret = ((Integer)arrayinfos.get(parent)).intValue();
		
		arrayinfos.put(parent, Integer.valueOf(ret+1));
	
		return ret;
	}
	
	/**
	 *  Get children.
	 */
	public List<LinkData> getChildren(Object key)
	{
		return (List<LinkData>)(children==null? null: children.get(key));
	}
	
	/**
	 *  Add a child.
	 */
	public void addChild(Object key, LinkData value)
	{
		if(children==null)
			children = new MultiCollection<Object, LinkData>();
		children.add(key, value);
	}
	
	/**
	 *  Remove a child.
	 */
	public List<LinkData> removeChildren(Object key)
	{
		return children==null? null: (List<LinkData>)children.remove(key);
	}
	
	/**
	 *  Get the xml path for a stack.
	 *  @param stack The stack.
	 *  @return The string representig the xml stack (e.g. tag1/tag2/tag3)
	 */
	public QName[] getXMLPath(QName tag)
	{
		QName[] ret = new QName[stack.size()+1];
		for(int i=0; i<stack.size(); i++)
		{
			ret[i] = ((StackElement)stack.get(i)).getTag();
		}
		ret[ret.length-1] = tag;
		return ret;		
	}
	
	/**
	 *  Get the xml path for a stack.
	 *  @param stack The stack.
	 *  @return The string representig the xml stack (e.g. tag1/tag2/tag3)
	 */
	protected QName[] getXMLPath()
	{
		QName[] ret = new QName[stack.size()];
		for(int i=0; i<stack.size(); i++)
		{
			ret[i] = ((StackElement)stack.get(i)).getTag();
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public StackElement[] getStack()
	{
		return (StackElement[])stack.toArray(new StackElement[stack.size()]);
	}

	public ILocation getLocation() {
		return parser.getLocation();
	}
}

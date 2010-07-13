package jadex.xml.reader;

import jadex.commons.collection.MultiCollection;
import jadex.xml.IContext;
import jadex.xml.StackElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

/**
 *  Context for reader that stores all relevant information of the read process.
 */
public class ReadContext implements IContext
{
	//-------- attributes --------
	
	/** The parser. */
	protected XMLStreamReader parser;
	
	/** The classloader. */
	protected ClassLoader classloader;
	
	/** The root object. */
	protected Object rootobject;
	
	/** The stack. */
	protected List stack;
	
	/** The current comment. */
	protected String comment;
	
	/** The read objects per id. */
	protected Map readobjects;
	
	/** The readignore counter (0=do not ignore). */
	protected int readignore;
	
	/** The call context. */
	protected Object callcontext;
	
	/** The post processors. */
	protected MultiCollection postprocessors;
	
	//-------- constructors --------
	
	/**
	 * @param parser
	 */
	public ReadContext(XMLStreamReader parser, Object callcontext, ClassLoader classloader)
	{
		this(parser, callcontext, classloader, null, new ArrayList(), 
			null, null, new HashMap(), 0, new MultiCollection());
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
	public ReadContext(XMLStreamReader parser, Object callcontext, ClassLoader classloader, Object root, List stack,
		StackElement topse, String comment, Map readobjects, int readignore, MultiCollection postprocessors)
	{
		this.parser = parser;
		this.callcontext = callcontext;
		this.classloader = classloader;
		this.rootobject = root;
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
	public XMLStreamReader getParser()
	{
		return parser;
	}

	/**
	 *  Set the parser.
	 *  @param parser The parser to set.
	 */
	public void setParser(XMLStreamReader parser)
	{
		this.parser = parser;
	}

	/**
	 *  Get the root object.
	 *  @return The root object.
	 */
	public Object getRootObject()
	{
		return rootobject;
	}

	/**
	 *  Get the current object.
	 *  @return The current object.
	 * /
	public Object getCurrentObject()
	{
		return	getTopStackElement().getObject();
	}*/

	/**
	 *  Set the root object.
	 *  @param root The rootobject to set.
	 */
	public void setRootObject(Object root)
	{
		this.rootobject = root;
	}

	/**
	 *  Get the stack.
	 *  @return The stack.
	 */
	public List getStack()
	{
		return stack;
	}

	/**
	 *  Set the stack.
	 *  @param stack The stack to set.
	 */
	public void setStack(List stack)
	{
		this.stack = stack;
	}

	/**
	 *  Get the top stack element.
	 *  @return The top stack element.
	 */
	public StackElement getTopStackElement()
	{
		return (StackElement)stack.get(stack.size()-1);
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
	public Map getReadObjects()
	{
		return readobjects;
	}

	/**
	 *  Set the readobjects.
	 *  @param readobjects The readobjects to set.
	 */
	public void setReadObjects(Map readobjects)
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
	public MultiCollection getPostProcessors()
	{
		return postprocessors;
	}

	/**
	 *  Set the postprocessors.
	 *  @param postprocessors The postprocessors to set.
	 */
	public void setPostProcessors(MultiCollection postprocessors)
	{
		this.postprocessors = postprocessors;
	}	
	
	
	
	
}

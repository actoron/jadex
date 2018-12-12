package jadex.bridge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.collection.MultiCollection;
import jadex.commons.collection.SCollection;

/**
 *  Helper class for building error reports.
 */
public abstract class AbstractErrorReportBuilder
{
	//-------- attributes --------
	
	/** The unqualified (model/file) name. */
	protected String	name;
	
	/** The file name with path (optional). */
	protected String	filename;
	
	/** The element categories. */
	protected String[] categories;
	
	/** The parse errors (tuple(elements) -> {error messages}). */
	protected MultiCollection<Tuple, String>	entries;
	
	/** The external documents for links in html error reports (id -> html text). */
	protected Map<String, String>	externals;
		
	//-------- constructors --------
	
	/**
	 *  Build the error based on the given entries (if any).
	 *  Entries represent error messages mapped by the path to the
	 *  xml element (as a tuple of stack elements).
	 *  @param name	The unqualified (model/file) name.
	 *  @param filename	The file name with path (optional).
	 *  @param categories	The element categories.
	 *  @param entries	The parse errors (tuple(stack elements) -> {error messages}).
	 *  @param externals	The external documents for links in html error reports, if any (id -> html text).
	 */
	public AbstractErrorReportBuilder(String name, String filename, String[] categories, MultiCollection<Tuple, String> entries, Map<String, String> externals)
	{
		this.name	= name;
		this.filename	= filename;
		this.categories	= categories;
		this.entries	= entries;
		this.externals	= externals;
	}
	
	//-------- methods --------
	
	/**
	 *  Build the error based on the given entries (if any).
	 *  Entries represent error messages mapped by the path to the
	 *  xml element (as a tuple of stack elements).
	 *  @return The error report.
	 */
	public IErrorReport	buildErrorReport()
	{
		IErrorReport report = entries==null || entries.size()==0 ? null
			: new ErrorReport(generateErrorText(), generateErrorHTML(), externals);
		return report;
	}
	
	//-------- template methods --------
	
	/**
	 *  Get the object of a path element
	 *  @param obj	An item (entry) of a tuple in the multi collection.
	 *  @return	The object corresponding to the entry.
	 */
	public abstract Object	getPathElementObject(Object element);
	
	/**
	 *  Test if an object belongs to a category.
	 *  @param obj	An item (entry) of a tuple in the multi collection.
	 *  @param category	the category name.
	 *  @return	True, when the object belongs to the category.
	 */
	public abstract boolean	isInCategory(Object obj, String category);
	
	/**
	 *  Get the name of an object.
	 *  @param obj	An object having an error.
	 *  @return	A human readable name of the object. 
	 */
	public abstract String	getObjectName(Object obj);

	//-------- helper methods --------
	
	/**
	 *  Get all invalid elements.
	 */
	protected Tuple[]	getElements()
	{
		if(entries==null)
			return new Tuple[0];
		else
			return (Tuple[])entries.getKeys(Tuple.class);
	}

	/**
	 *  Get the messages for a given element.
	 */
	protected String[]	getMessages(Tuple path)
	{
		if(entries==null)
		{
			return SUtil.EMPTY_STRING_ARRAY;
		}
		else
		{
			Collection<String>	ret	= entries.getCollection(path);
			return (String[])ret.toArray(new String[ret.size()]);
		}
	}

	/**
	 *  Generate a string representation of the report.
	 */
	protected String generateErrorText()
	{
		StringBuffer buf = new StringBuffer();

		buf.append("Report for ");
		buf.append(name);
		buf.append("\n");
		if(filename!=null)
		{
			buf.append("File: ");
			buf.append(filename);
			buf.append("\n");
		}
		
		buf.append("\n");

		Tuple[]	elements	= getElements();
		for(int i=0; i<elements.length; i++)
		{
			Object	obj	= getObject(elements[i]);
			if(obj!=null)
			{
				String	name	= getObjectName(obj);
				name	= name.replace("\n", " ");
				buf.append(name);
				buf.append(":\n");
			}
			else
			{
				buf.append("Errors:\n");				
			}
			String[]	messages	= 	getMessages(elements[i]);
			for(int j=0; j<messages.length; j++)
			{
				buf.append("\t");
				buf.append(messages[j]);
				buf.append("\n");
			}
		}
		
		return SUtil.stripTags(buf.toString());
	}

	/**
	 *  Generate an html representation of the report.
	 */
	protected String generateErrorHTML()
	{
		StringBuffer	buf	= new StringBuffer();

		buf.append("<h3>Report for ");
		buf.append(name);
		buf.append("</h3>\n");
		if(filename!=null)
		{
			buf.append("File: ");
			buf.append(filename);
			buf.append("\n");
		}

		Set<Object>[]	catels	= new Set[categories.length];
		Set<Object> excludes	= new HashSet<Object>();
		for(int i=0; i<categories.length; i++)
		{
			catels[i]	= getOwnedElementErrors(categories[i]);
			excludes.addAll(catels[i]);
		}
		Set<Object>	others	= getOtherErrors(excludes);

		
		// Summaries.
		buf.append("<h4>Summary</h4>\n<ul>\n");
		for(int i=0; i<categories.length; i++)
		{
			generateOverview(buf, categories[i], catels[i]);
		}
		generateOverview(buf, "Other element", others);
		buf.append("</ul>\n");


		// Details.
		for(int i=0; i<categories.length; i++)
		{
			generateDetails(buf, categories[i], catels[i]);
		}
		generateDetails(buf, "Other element", others);
		
		return buf.toString();
	}

	/**
	 *  Get elements of the given owner type, which have errors or contain elements with errors.
	 */
	protected Set<Object>	getOwnedElementErrors(String category)
	{
		Set<Object>	errors	= SCollection.createLinkedHashSet();
		Tuple[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
			boolean	added	= false;
			for(int j=0; !added && j<elements[i].getEntities().length; j++)
			{
				Object	se	= elements[i].getEntity(j);
				Object	obj	= getPathElementObject(se);
				if(obj!=null)
				{
					added	= errors.contains(obj);
					if(!added && isInCategory(obj, category))
					{
						errors.add(obj);
						added	= true;
					}
				}
			}
		}
		return errors;
	}

	/**
	 *  Get other errors, not in the given tags.
	 */
	protected Set<Object>	getOtherErrors(Set<Object> excludes)
	{
		Set<Object>	errors	= SCollection.createLinkedHashSet();
		Tuple[]	elements	= getElements();			
		for(int i=0; i<elements.length; i++)
		{
			boolean	excluded	= false;
			for(int j=0; !excluded && j<elements[i].getEntities().length; j++)
			{
				Object	se	= elements[i].getEntity(j);
				Object	obj	= getPathElementObject(se);
				if(obj!=null)
				{
					excluded	= excludes.contains(obj);
				}
			}
			if(!excluded)
			{
				Object	obj	= getObject(elements[i]);
				if(obj!=null && !errors.contains(obj))
				{
					errors.add(obj);
				}
				else
				{
					errors.add(elements[i]);
				}
			}
		}
		return errors;
	}

	protected Object getObject(Tuple element)
	{
		Object	ret	= null;
		for(int j=element.getEntities().length-1; ret==null && j>=0; j--)
		{
			Object	se	= element.getEntity(j);
			Object	obj	= getPathElementObject(se);
			if(obj!=null)
			{
				ret	= obj;
			}
		}
		return ret;
	}

	/**
	 *  Get all elements which have errors and are contained in the given element.
	 */
	protected Tuple[]	getElementErrors(Object ancestor)
	{
		List<Tuple>	errors;
		if(entries.containsKey(ancestor))
		{
			errors	=  (List)Collections.singletonList(ancestor);
		}
		else
		{
			errors	= SCollection.createArrayList();
			Tuple[]	elements	= getElements();			
			for(int i=0; i<elements.length; i++)
			{
				boolean	added	= errors.contains(elements[i]);
				for(int j=0; !added && j<elements[i].getEntities().length; j++)
				{
					Object	se	= elements[i].getEntity(j);
					Object	obj	= getPathElementObject(se);
					if(ancestor.equals(obj))
					{
						errors.add(elements[i]);
						added	= true;
					}
				}
			}
		}
		return (Tuple[])errors.toArray(new Tuple[errors.size()]);
	}
	
	/**
	 *  Generate overview HTML code for the given elements.
	 */
	protected void	generateOverview(StringBuffer buf, String type, Set<Object> elements)
	{
		if(!elements.isEmpty())
		{
			buf.append("<li>");
			buf.append(type);
			buf.append(" errors\n<ul>\n");
			for(Iterator<Object> it=elements.iterator(); it.hasNext(); )
			{
				Object	obj	= it.next();
				String name = getObjectName(obj);
				buf.append("<li><a href=\"#");
				buf.append(SUtil.makeConform(name));
				buf.append("\">");
				buf.append(SUtil.makeConform(name));
				buf.append("</a> has errors.</li>\n");
			}
			buf.append("</ul>\n</li>\n");
		}
	}

	/**
	 *  Generate detail HTML code for the given elements.
	 */
	protected void	generateDetails(StringBuffer buf, String type, Set<Object> elements)
	{
		if(!elements.isEmpty())
		{
			buf.append("<h4>");
			buf.append(type);
			buf.append(" details</h4>\n<ul>\n");
			for(Iterator<Object> it=elements.iterator(); it.hasNext(); )
			{
				Object	obj	= it.next();
				String name = getObjectName(obj);
				buf.append("<li><a name=\"");
				buf.append(SUtil.makeConform(name));
				buf.append("\">");
				buf.append(SUtil.makeConform(name));
				// Add name of configuration (hack???)
//				if(elements[i] instanceof IMConfigElement)
//				{
//					MElement	owner	= (MElement)elements[i];
//					while(owner!=null && !(owner instanceof IMConfiguration))
//						owner	= owner.getOwner();
//					if(owner!=null)
//					buf.append(" in ");
//					buf.append(SUtil.makeConform(""+owner));
//				}
				buf.append("</a> errors:\n");

				Tuple[]	errors	= getElementErrors(obj);
				buf.append("<dl>\n");
				for(int j=0; j<errors.length; j++)
				{
					Object	obj2	= getObject(errors[j]);
					if(!obj.equals(obj2))
					{
						buf.append("<dt>");
						buf.append(getObjectName(obj2));
						buf.append("</dt>\n");
					}
//					SourceLocation	loc	= errors[j].getSourceLocation();
//					if(loc!=null)
//					{
//						buf.append(" (");
//						buf.append(loc.getFilename());
//						buf.append(": line ");
//						buf.append(loc.getLineNumber());
//						buf.append(", column ");
//						buf.append(loc.getColumnNumber());
//						buf.append(")");
//					}
					
					String[]	msgs	= getMessages(errors[j]);
					buf.append("<dd>");
					for(int k=0; k<msgs.length; k++)
					{
						buf.append(msgs[k]);
						buf.append("\n");
						if(msgs.length>1 && k!=msgs.length-1)
							buf.append("<br>");
					}
					buf.append("</dd>\n");
				}
				buf.append("</dl>\n</li>\n");
				
			}
			buf.append("</ul>\n");
		}
	}
}

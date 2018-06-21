package jadex.commons.transformation.traverser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Processor for handling iterators.
 */
public class IteratorProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return SReflect.isSupertype(Iterator.class, clazz);
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 * @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		Iterator it = (Iterator)object;
		List copy = new ArrayList();
		Iterator ret = new UncheckedIterator(copy);
		TraversedObjectsContext.put(context, object, ret);

		for(; it.hasNext(); )
		{
			Object val = it.next();
			Class valclazz = val!=null? val.getClass(): null;
			Object newval = traverser.doTraverse(val, valclazz, conversionprocessors, processors, mode, targetcl, context);
			if (newval != Traverser.IGNORE_RESULT)
				copy.add(newval);
		}
		
		return ret;
	}
	
//	/**
//	 * 
//	 */
//	public static void main(String[] args)
//	{
//		ArrayList list = new ArrayList();
////		Vector list = new Vector();
//		
//		Iterator it= new UncheckedIterator(list);
////		Enumeration it = list.elements();
//		
//		list.add("a");
//		list.add("b");
//		list.add("c");
//		
//		for(; it.hasNext();)
//		{
//			System.out.println("elem: "+it.next());
//		}
//	}
	
}

class UncheckedIterator<E> implements Iterator<E>
{
	protected List<E> source;
	
	protected int pos;
	
	/**
	 *  Create a new Iterator.
	 */
	public UncheckedIterator(List<E> source)
	{
		this.source = source;
	}
	
	/**
	 *  Test if has next element.
	 */
    public boolean hasNext()
    {
    	return pos<source.size();
    }

    /**
     * 
     */
    public E next()
    {
    	return source.get(pos++);
    }

    /**
     * 
     */
    public void remove()
    {
    	source.remove(--pos);
    }
}


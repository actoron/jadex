package jadex.commons.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;

/**
 *  Static methods for collection creation and observation.
 */
public class SCollection
{
	//-------- static part --------

	public static final boolean DEBUG = false;

	private static Object monitor;

	private static List arraylists;
	private static List fasthashmaps;
	private static List hashmaps;
	private static List hashsets;
	private static List hashtables;
	private static List indexmaps;
	private static List linkedhashsets;
	private static List linkedlists;
	private static List lrus;
	private static List multicollections;
	private static List nestedmaps;
	private static List vectors;
	private static List weakhashmaps;
	private static List weaklists;
	private static List weaksets;
	private static List others;

	static
	{
		if(DEBUG)
		{
			monitor	= new Object();
			
			arraylists = new WeakList();
			fasthashmaps = new WeakList();
			hashmaps = new WeakList();
			hashsets = new WeakList();
			hashtables = new WeakList();
			indexmaps = new WeakList();
			linkedhashsets = new WeakList();
			linkedlists = new WeakList();
			lrus = new WeakList();
			multicollections = new WeakList();
			nestedmaps = new WeakList();
			vectors = new WeakList();
			weakhashmaps = new WeakList();
			weaklists = new WeakList();
			weaksets = new WeakList();
			others = new WeakList();
			Thread debug_printer = new Thread(new DebugPrinter());
			debug_printer.start();
		}
	}

	/**
	 *  Return a fresh index map.
	 */
	public static IndexMap createIndexMap()
	{
		IndexMap ret = new IndexMap();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh hashtable.
	 */
	public static Hashtable createHashtable()
	{
		Hashtable ret = new Hashtable();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh hash map.
	 */
	public static <T,E> HashMap<T,E> createHashMap()
	{
		HashMap<T,E> ret = new HashMap<T,E>();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh linked hash map.
	 */
	public static <T,E> HashMap<T,E> createLinkedHashMap()
	{
		// note for mobile version: change LinkedHashMap to HashMap
		LinkedHashMap<T,E> ret = new LinkedHashMap<T,E>();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

		/**
	 *  Return a fresh linked hash map.
	 */
	public static <T,E> LRU<T,E> createLRU(int max)
	{
		LRU<T,E> ret = new LRU<T,E>(max);
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh hash map.
	 */
	public static FastHashMap createFastHashMap()
	{
		FastHashMap ret = new FastHashMap();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh multi collection.
	 */
	public static MultiCollection createMultiCollection()
	{
		MultiCollection ret = new MultiCollection();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh weak hash map.
	 */
	public static WeakHashMap createWeakHashMap()
	{
		WeakHashMap ret = new WeakHashMap();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh weak list.
	 */
	public static WeakList createWeakList()
	{
		WeakList ret = new WeakList();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh weak set.
	 */
	public static WeakSet createWeakSet()
	{
		WeakSet ret = new WeakSet();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh array list.
	 */
	public static <T> ArrayList<T> createArrayList()
	{
		ArrayList<T> ret = new ArrayList<T>();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh linked list.
	 */
	public static LinkedList createLinkedList()
	{
		LinkedList ret = new LinkedList();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a fresh vector.
	 */
	public static Vector createVector()
	{
		Vector ret = new Vector();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a hash set.
	 */
	public static <T> HashSet<T> createHashSet()
	{
		HashSet<T> ret = new HashSet<T>();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a linked hash set.
	 */
	public static <T> LinkedHashSet<T> createLinkedHashSet()
	{
		// note for mobile version: completly remove this method
		LinkedHashSet<T> ret = new LinkedHashSet<T>();
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a linked hash set.
	 */
	public static NestedMap createNestedMap(Map map)
	{
		NestedMap ret = new NestedMap(map);
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Return a linked hash set.
	 */
	public static NestedMap createNestedMap(Map[] map)
	{
		NestedMap ret = new NestedMap(map);
		if(DEBUG)
			addCollection(ret);
		return ret;
	}

	/**
	 *  Wrap a list for concurrency checking.
	 */
	public static List	concurrencyCheckingList(List list)
	{
		return new ConcurrencyCheckingList(list);
	}
	
	/**
	 *  Add a map to the observer list.
	 */
	protected static void addCollection(Object o)
	{
		assert DEBUG;
		synchronized(monitor)
		{
			if(o.getClass().equals(ArrayList.class))
			{
				if(arraylists!=null)	// WeakList to store arraylists requires an array list
					arraylists.add(o);
			}
			else if(o.getClass().equals(FastHashMap.class))
			{
				fasthashmaps.add(o);
			}
			else if(o.getClass().equals(HashMap.class))
			{
				hashmaps.add(o);
			}
			else if(o.getClass().equals(HashSet.class))
			{
				hashsets.add(o);
			}
			else if(o.getClass().equals(Hashtable.class))
			{
				hashtables.add(o);
			}
			else if(o.getClass().equals(IndexMap.class))
			{
				indexmaps.add(o);
			}
			else if(o.getClass().equals(LinkedHashSet.class))
			{
				linkedhashsets.add(o);
			}
			else if(o.getClass().equals(LinkedList.class))
			{
				linkedlists.add(o);
			}
			else if(o.getClass().equals(LRU.class))
			{
				lrus.add(o);
			}
			else if(o.getClass().equals(MultiCollection.class))
			{
				multicollections.add(o);
			}
			else if(o.getClass().equals(NestedMap.class))
			{
				nestedmaps.add(o);
			}
			else if(o.getClass().equals(Vector.class))
			{
				vectors.add(o);
			}
			else if(o.getClass().equals(WeakHashMap.class))
			{
				weakhashmaps.add(o);
			}
			else if(o.getClass().equals(WeakList.class))
			{
				weaklists.add(o);
			}
			else if(o.getClass().equals(WeakSet.class))
			{
				weaksets.add(o);
			}
			else
			{
				System.out.println("Unknown collection type: "+o.getClass());
				others.add(o);
			}
		}
	}

	/**
	 *  Create an info printer for the collections.
	 */
	public static class DebugPrinter implements Runnable
	{
		/**
		 *  Wait some time and print out info.
		 */
		public void run()
		{
			Object mon = new Object();
			int boundary = 20;
			while(true)
			{
				try
				{
					synchronized(mon)
					{
						mon.wait(10000);
					}
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				synchronized(monitor)
				{
					System.gc();
					System.out.println("*************** DEBUG Collections ***************");
		
					System.out.println("*** ArrayLists ***"+" "+arraylists.size());
					printCollectionInfo(arraylists.iterator(), boundary);

					System.out.println("*** FastHashMaps ***"+" "+fasthashmaps.size());
					printCollectionInfo(fasthashmaps.iterator(), boundary);

					System.out.println("*** HashMaps ***"+" "+hashmaps.size());
					printCollectionInfo(hashmaps.iterator(), boundary);
		
					System.out.println("*** HashSets ***"+" "+hashsets.size());
					printCollectionInfo(hashsets.iterator(), boundary);

					System.out.println("*** HashTables ***"+" "+hashtables.size());
					printCollectionInfo(hashtables.iterator(), boundary);

					System.out.println("*** IndexMaps ***"+" "+indexmaps.size());
					printCollectionInfo(indexmaps.iterator(), boundary);
		
					System.out.println("*** LinkedHashSets ***"+" "+linkedhashsets.size());
					printCollectionInfo(linkedhashsets.iterator(), boundary);
		
					System.out.println("*** LinkedLists ***"+" "+linkedlists.size());
					printCollectionInfo(linkedlists.iterator(), boundary);
		
					System.out.println("*** LRUs ***"+" "+lrus.size());
					printCollectionInfo(lrus.iterator(), boundary);
		
					System.out.println("*** Multicollections ***"+" "+multicollections.size());
					printCollectionInfo(multicollections.iterator(), boundary);
		
					System.out.println("*** NestedMaps ***"+" "+nestedmaps.size());
					printCollectionInfo(nestedmaps.iterator(), boundary);
		
					System.out.println("*** Vectors ***"+" "+vectors.size());
					printCollectionInfo(vectors.iterator(), boundary);

					System.out.println("*** WeakHashMaps ***"+" "+weakhashmaps.size());
					printCollectionInfo(weakhashmaps.iterator(), boundary);

					System.out.println("*** WeakLists ***"+" "+weaklists.size());
					printCollectionInfo(weaklists.iterator(), boundary);

					System.out.println("*** WeakSets ***"+" "+weaksets.size());
					printCollectionInfo(weaksets.iterator(), boundary);

					System.out.println("*** Others ***"+" "+others.size());
					printCollectionInfo(others.iterator(), boundary);
		
					System.out.println("*************************************************");
				}
			}
		}
	}

	/**
	 *  Print out all collections with more than boundary elements.
	 */
	protected static void printCollectionInfo(Iterator it, int boundary)
	{
		while(it.hasNext())
		{
			Object o = it.next();

			assert o instanceof Map || o instanceof Collection || o instanceof IndexMap;

			if(o instanceof Map && ((Map)o).size()>boundary)
				System.out.println(o.hashCode()+": "+((Map)o).size());
			else if(o instanceof Collection && ((Collection)o).size()>boundary)
				System.out.println(o.hashCode()+": "+((Collection)o).size());
			else if(o instanceof IndexMap && ((IndexMap)o).size()>boundary)
				System.out.println(o.hashCode()+": "+((IndexMap)o).size());
		}
	}

	public static void	main(String[] args)	throws InterruptedException
	{
		for(int i=1; i<5000; i++)
		{
			createArrayList();
			createHashMap();
			createHashSet();
			createHashtable();
			createIndexMap();
			createVector();
			Thread.sleep(10);
		}
		System.out.println("finished");
		Thread.sleep(10000);
		System.exit(0);
	}
}



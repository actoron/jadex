package jadex.platform.service.cron;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import jadex.commons.ComposedFilter;
import jadex.commons.ConstantFilter;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.transformation.annotations.Exclude;

/**
 *  Create a new time pattern.
 */
public class TimePatternFilter extends ComposedFilter<Long>
{
	//-------- attributes --------
	
	/** The always true filter. */
	public static final IFilter<Integer> ALWAYS = new ConstantFilter<Integer>(true);
	
	/** The pattern. */
	protected String pattern;
	
	//-------- constructors --------
	
	/**
	 *  Create a new TimePatternFilter. 
	 */
	public TimePatternFilter()
	{
		super(null);
	}
	
	/**
	 *  Create a new time pattern.
	 *  @param pattern The pattern.
	 */
	public TimePatternFilter(String pattern)
	{
		super(parsePattern(pattern), OR);
		this.pattern = pattern;
	}

	//-------- methods --------
	
	/**
	 *  Get the pattern.
	 *  @return The pattern.
	 */
	public String getPattern()
	{
		return pattern;
	}

	/**
	 *  Set the pattern.
	 *  @param pattern The pattern to set.
	 */
	public void setPattern(String pattern)
	{
		this.pattern = pattern;
		setFilters(parsePattern(pattern));
	}
	
	/**
	 *  Get the filters.
	 *  @return the filters.
	 */
	@Exclude
	public IFilter<Long>[] getFilters()
	{
		return super.getFilters();
	}
	
//	/**
//	 *  Get the next matching time point or
//	 *  raise an exception in none is in the range.
//	 *  @param start The start time.
//	 *  @param end The end time.
//	 *  @return The time.
//	 */
//	public long getNextTimepoint(long start, long end)
//	{
//		long ret = -1;
//		long cur = (start/60000)*60000; // round to full min
//		
//		GregorianCalendar gc = new GregorianCalendar();
//		gc.setTimeInMillis(start);
////		c.setTimeZone(zone);
//		
//		while(cur<=end)
//		{
//			cur = gc.getTimeInMillis();
//			if(filter(new Long(cur)))
//			{
//				ret = cur;
//				break;
//			}
//			else
//			{
//				gc.add(GregorianCalendar.MINUTE, 1);
//			}
//		}
//		
//		if(ret==-1)
//			throw new RuntimeException("No timepoint found");
//		
//		return ret;
//	}
	
	/**
	 *  Get the next matching time point or
	 *  raise an exception in none is in the range.
	 *  @param start The start time.
	 *  @param end The end time.
	 *  @return The time.
	 */
	public long getNextTimepoint(long start, long end)
	{
		long ret = -1;
		long cur = (start/60000)*60000; // round to full min
		if(start>cur)
			cur += 60000;
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(cur);
//		c.setTimeZone(zone);
		
		while(cur<=end)
		{
			cur = gc.getTimeInMillis();
			if(filter(Long.valueOf(cur)))
			{
				ret = cur;
				break;
			}
			else
			{
				gc.add(GregorianCalendar.MINUTE, 1);
			}
		}
		
		if(ret==-1)
			throw new RuntimeException("No timepoint found");
		
		return ret;
	}

	/**
	 *  Parse the time pattern.
	 */
	public static IFilter<Long>[] parsePattern(String pattern)
	{
		IFilter<Long>[] ret = null;
		
		if(pattern.indexOf("|")!=-1)
		{
			StringTokenizer stok = new StringTokenizer(pattern, "|");
			
			ret = new IFilter[stok.countTokens()];
			for(int i=0; stok.hasMoreTokens(); i++)
			{
				String tok = stok.nextToken();
				ret[i] = parseOnePattern(tok);
			}
		}
		else
		{
			ret = new IFilter[]{parseOnePattern(pattern)};
		}
		
		return ret;
	}
	
	/**
	 *  Parse one pattern (not a combined one with |)
	 *  @param tok The token.
	 *  @return The filter. 
	 */
	protected static IFilter<Long> parseOnePattern(String tok)
	{
		StringTokenizer stok = new StringTokenizer(tok);
		if(stok.countTokens()!=5)
			throw new RuntimeException("Invalid pattern: "+tok);
		
		IFilter<Integer>[] fil = new IFilter[5];
		fil[0] = createFilter(stok.nextToken().trim(), 0, 59);
		fil[1] = createFilter(stok.nextToken(), 0, 23);
		fil[2] = createFilter(stok.nextToken(), 1, 31);
		fil[3] = createFilter(stok.nextToken(), 1, 12);
		fil[4] = createFilter(stok.nextToken(), 0, 6); // todo: add 7 for sunday 
		
		return new TimeFilter(fil);
	}
	
	/**
	 *  Create a new time filter for one part of the time (min, hour, month, ...)
	 *  @param tok The pattern part.
	 *  @param min The min value.
	 *  @param max The max value.
	 *  @return The partial filter.
	 */
	protected static IFilter<Integer> createFilter(String tok, int min, int max)
	{
		IFilter<Integer> ret = null;
		
		int idx = tok.indexOf("/");
		
		// wildcard
		if(tok.equals("*"))
		{
			ret = ALWAYS;
		}
		
		// steps
		else if(idx!=-1)
		{
			String first = tok.substring(0, idx);
			String sec = tok.substring(idx+1);
			int step = Integer.parseInt(sec);
			Set<Integer> vals = new HashSet<Integer>();
			
			int s = min;
			int e = max;
			int idx2 = first.indexOf("-");
			if(idx2!=-1)
			{
				s = Integer.parseInt(first.substring(0, idx2));
				e = Integer.parseInt(first.substring(idx2+1));
			}
			for(int i=s; i<=e; i+=step)
			{
				vals.add(Integer.valueOf(i));
			}
			ret = new SetFilter(vals);
		}
		
		// values
		else if(tok.indexOf(",")!=-1)
		{
			StringTokenizer stok = new StringTokenizer(tok, ",");
			Set<Integer> vals = new HashSet<Integer>();
			while(stok.hasMoreTokens())
			{
				String ntok = stok.nextToken();
				if(ntok.indexOf("-")!=-1)
				{
					parseRange(ntok, vals);
				}
				else
				{
					vals.add(Integer.parseInt(ntok));
				}
			}
			ret = new SetFilter(vals);
		}
		
		// range
		else if(tok.indexOf("-")!=-1)
		{
			Set<Integer> vals = new HashSet<Integer>();
			parseRange(tok, vals);
			ret = new SetFilter(vals);
		}
		
		// number
		else
		{
			Set<Integer> vals = new HashSet<Integer>();
			vals.add(Integer.parseInt(tok));
			ret = new SetFilter(vals);
		}
		
		return ret;
	}
	
	/**
	 *  Parse a range spec.
	 *  @param tok The range spec.
	 *  @param vals The result values.
	 */
	protected static void parseRange(String tok, Set<Integer> vals)
	{
		int idx2 = tok.indexOf("-");
		int s = Integer.parseInt(tok.substring(0, idx2));
		int	e = Integer.parseInt(tok.substring(idx2+1));
		for(int i=s; i<=e; i++)
		{
			vals.add(Integer.valueOf(i));
		}
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		// minute, hour, day of month, month, day of week
		String[] patterns = new String[]
		{
			"* * * * *", // every minute - ok
			"*/5 * * * *", // every 5 mins - ok
			"*/5 * * * *|*/3 * * * *", // every 5 3 mins - ok
			"3-18/5 * * * *", // every 5 mins from 3, 8, 13,18 - ok
			"*/5 */2 * * *", // every 5 mins every 2 hours - ok
			"*/15 9-15 * * *", // every 15 mins from 9-15 - ok
			"*/20 12 1-3,15,20-22 * *", // every 20 mins in 12 hour 
			"55 7 * * 1,2,3,4,5", // 7:55 Mo-Fr
			"55 7 * * 1-5", // 7:55 Mo-Fr			
			"0 0 * * *", // daily - ok
			"0 * * * *", // hourly ok
			"0 0 * * 0", // weekly - ok
			"0 0 1 * *", // monthly - ok
//			"0 0 1 1 *" // yearly - ok
		};
		
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis()+1000l*60*60*24*365*15; // 15 years

		System.out.println("start: "+start+" end: "+end+" "+(end-start));
		
		for(String pattern: patterns)
		{
			System.out.println("Testing pattern: "+pattern);
			TimePatternFilter tp = new TimePatternFilter(pattern);
			long cur = start;
			for(int i=0; i<10; i++)
			{
				cur = tp.getNextTimepoint(cur, end);
				System.out.println(i+": "+SUtil.SDF.get().format(cur));
				cur+=60000;
			}
		}
	}
}




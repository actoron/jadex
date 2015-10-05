package jadex.platform.service.cron;

import jadex.commons.SUtil;
import jadex.commons.Tuple2;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *  Test time patterns via simple simulation.
 */
public class TimePatternTest //extends TestCase
{
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
			"*/20 */2 * * *", // every 5 mins every 2 hours - ok
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

	@Before
	public void setUpGlobaleTimeZone() {
		// the expected results were created with Europe/Berlin :(
		TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Europe/Berlin")));
	}
	
	/**
	 *  Test pattern "*\/5 * * * *".
	 */
	@Test
	public void testEveryMinute()
	{
		long[] actual = simulate("* * * * *");
		long[] expected = new long[]{
			1328050800000L, 
			1328050860000L, 
			1328050920000L, 
			1328050980000L, 
			1328051040000L, 
			1328051100000L, 
			1328051160000L, 
			1328051220000L, 
			1328051280000L, 
			1328051340000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern "* * * * *".
	 */
	@Test
	public void testEvery5Mins()
	{
		long[] actual = simulate("*/5 * * * *");
		long[] expected = new long[]{
			1328050800000L, 
			1328051100000L, 
			1328051400000L, 
			1328051700000L, 
			1328052000000L, 
			1328052300000L, 
			1328052600000L, 
			1328052900000L, 
			1328053200000L, 
			1328053500000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern "*\/5 * * * *|*\/3 * * * *", every 5 3 mins 
	 */
	@Test
	public void testMultiPattern()
	{
		long[] actual = simulate("*/5 * * * *|*/3 * * * *");
		long[] expected = new long[]{
			1328050800000L, 
			1328050980000L, 
			1328051100000L, 
			1328051160000L, 
			1328051340000L, 
			1328051400000L, 
			1328051520000L, 
			1328051700000L, 
			1328051880000L, 
			1328052000000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern "3-18\/5 * * * *", every 5 mins from 3, 8, 13,18 - ok
	 */
	@Test
	public void testRangeStepsPattern()
	{
		long[] actual = simulate("3-18/5 * * * *");
		long[] expected = new long[]{
			1328050980000L, 
			1328051280000L, 
			1328051580000L, 
			1328051880000L, 
			1328054580000L, 
			1328054880000L, 
			1328055180000L, 
			1328055480000L, 
			1328058180000L, 
			1328058480000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern "*\/5 *\/2 * * *", // every 5 mins every 2 hours - ok
	 */
	@Test
	public void testEvery20MinsEvery2Hours()
	{
		long[] actual = simulate("*/20 */2 * * *");
		long[] expected = new long[]{
			1328050800000L, 
			1328052000000L, 
			1328053200000L, 
			1328058000000L, 
			1328059200000L, 
			1328060400000L, 
			1328065200000L, 
			1328066400000L, 
			1328067600000L, 
			1328072400000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern "*\/45 9-13 * * *", // at 0/45 mins from 9-12
	 */
	@Test
	public void testAt45MinsFrom9to12()
	{
		long[] actual = simulate("*/45 9-12 * * *");
		long[] expected = new long[]{
			1328083200000L, 
			1328085900000L, 
			1328086800000L, 
			1328089500000L, 
			1328090400000L, 
			1328093100000L, 
			1328094000000L, 
			1328096700000L,
			1328169600000L, 
			1328172300000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Needs 3 patterns to create a job every 45 mins
	 *  Test pattern 0,45 0,3,6,9,12,15,18,21  * * *|30 1,4,7,10,13,16,19,22 * * *|15 2,5,8,11,14,17,20,23 * * *			
	 */
	@Test
	public void testEvery45Mins()
	{
		long[] actual = simulate("0,45 0,3,6,9,12,15,18,21 * * *|30 1,4,7,10,13,16,19,22  * * *|15 2,5,8,11,14,17,20,23 * * *");
		long[] expected = new long[]{
			1328050800000L, 
			1328053500000L, 
			1328056200000L, 
			1328058900000L, 
			1328061600000L, 
			1328064300000L, 
			1328067000000L, 
			1328069700000L, 
			1328072400000L, 
			1328075100000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern *\/30 12 1-3,15,20-22 * *
	 */
	@Test
	public void testEvery30MinsAt12AtDates()
	{
		long[] actual = simulate("*/30 12 1-3,15,20-22 * *");
		long[] expected = new long[]{
			1328094000000L, 
			1328095800000L, 
			1328180400000L, 
			1328182200000L, 
			1328266800000L, 
			1328268600000L, 
			1329303600000L, 
			1329305400000L, 
			1329735600000L, 
			1329737400000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern 55 7 * * 1,2,3,4,5 7:55 Mo-Fr
	 */
	@Test
	public void test755MoToFr()
	{
		long[] actual = simulate("55 7 * * 1,2,3,4,5");
		long[] expected = new long[]{
			1328079300000L, 
			1328165700000L, 
			1328252100000L, 
			1328511300000L, 
			1328597700000L, 
			1328684100000L, 
			1328770500000L, 
			1328856900000L, 
			1329116100000L, 
			1329202500000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern 55 7 * * 1-5 7:55 Mo-Fr
	 */
	@Test
	public void test755MoToFr2()
	{
		long[] actual = simulate("55 7 * * 1-5");
		long[] expected = new long[]{
			1328079300000L, 
			1328165700000L, 
			1328252100000L, 
			1328511300000L, 
			1328597700000L, 
			1328684100000L, 
			1328770500000L, 
			1328856900000L, 
			1329116100000L, 
			1329202500000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern 0 * * * * hourly
	 */
	@Test
	public void testHourly()
	{
		long[] actual = simulate("0 * * * *");
		long[] expected = new long[]{
			1328050800000L, 
			1328054400000L, 
			1328058000000L, 
			1328061600000L, 
			1328065200000L, 
			1328068800000L, 
			1328072400000L, 
			1328076000000L, 
			1328079600000L, 
			1328083200000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern 0 0 * * * daily
	 */
	@Test
	public void testDaily()
	{
		long[] actual = simulate("0 0 * * *");
		long[] expected = new long[]{
			1328050800000L, 
			1328137200000L, 
			1328223600000L, 
			1328310000000L, 
			1328396400000L, 
			1328482800000L, 
			1328569200000L, 
			1328655600000L, 
			1328742000000L, 
			1328828400000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern "0 0 * * 0" weekly
	 */
	@Test
	public void testWeekly()
	{
		long[] actual = simulate("0 0 * * 0");
		long[] expected = new long[]{
			1328396400000L, 
			1329001200000L, 
			1329606000000L, 
			1330210800000L, 
			1330815600000L, 
			1331420400000L, 
			1332025200000L, 
			1332630000000L, 
			1333231200000L, 
			1333836000000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern "0 0 1 * *" weekly
	 */
	@Test
	public void testMonthly()
	{
		long[] actual = simulate("0 0 1 * *");
		long[] expected = new long[]{
			1328050800000L, 
			1330556400000L, 
			1333231200000L, 
			1335823200000L, 
			1338501600000L, 
			1341093600000L, 
			1343772000000L, 
			1346450400000L, 
			1349042400000L, 
			1351724400000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Test pattern 0 0 1 1 * yearly
	 */
	@Test
	public void testYearly()
	{
		long[] actual = simulate("0 0 1 1 *");
		long[] expected = new long[]{
			1356994800000L, 
			1388530800000L, 
			1420066800000L, 
			1451602800000L, 
			1483225200000L, 
			1514761200000L, 
			1546297200000L, 
			1577833200000L, 
			1609455600000L, 
			1640991600000L
		};
		Assert.assertTrue(Arrays.equals(expected, actual));
	}
	
	/**
	 *  Create the start and end date.
	 */
	protected Tuple2<Long, Long> createStartAndEnd()
	{
		GregorianCalendar cal = new GregorianCalendar(2012, 1, 1); // month from 0 -> feb :-(
		long start = cal.getTimeInMillis();
		cal.set(GregorianCalendar.YEAR, 2023);
		long end = cal.getTimeInMillis();
		
		return new Tuple2<Long, Long>(Long.valueOf(start), Long.valueOf(end));
	}
	
	/**
	 *  Simulate time progress and record matches.
	 */
	public long[] simulate(String pattern)
	{
		Tuple2<Long, Long> tup = createStartAndEnd();
		long start = tup.getFirstEntity().longValue();
		long end = tup.getSecondEntity().longValue();
		return simulate(pattern, start, end);
	}
	
	/**
	 *  Simulate time progress and record matches.
	 */
	public static long[] simulate(String pattern, long start, long end)
	{
//		System.out.println("Testing pattern: "+pattern);
		TimePatternFilter tp = new TimePatternFilter(pattern);

		int num = 10;
		long[] ret = new long[num];
				
		long cur = start;
		// find num (10) matches
		for(int i=0; i<num; i++)
		{
			cur = tp.getNextTimepoint(cur, end);
//			System.out.println(i+": "+SUtil.SDF.format(cur));
			ret[i] = cur;
			cur+=60000; // avoid to find the date more than once
		}
		
//		System.out.println(SUtil.arrayToString(ret));
		
		return ret;
	}
}

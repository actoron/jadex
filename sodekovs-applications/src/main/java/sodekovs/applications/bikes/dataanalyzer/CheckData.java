package sodekovs.applications.bikes.dataanalyzer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import sodekovs.applications.bikes.misc.Constants;
import sodekovs.applications.bikes.model.CheckDataResult;
import sodekovs.applications.bikes.model.SystemSnapshot;
import sodekovs.util.misc.TimeConverter;

public class CheckData {

	private static StationsDAO stationsDAO = StationsDAO.getInstance();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long tmp1 = System.currentTimeMillis();
		 String cityName = "Washington";
//		String cityName = "London";

		// load all data for this city
		ArrayList<SystemSnapshot> snapshotList = stationsDAO.loadAllLogsForCity(cityName);

		ArrayList<CheckDataResult> results = new ArrayList<CheckDataResult>();
		for (int i = 1; i < 32; i++) {
			Calendar day = Calendar.getInstance();
			day.setTime(new Date(System.currentTimeMillis()));
			day.set(Calendar.MONTH, Calendar.JULY);
			day.set(Calendar.DAY_OF_MONTH, i);

			// System.out.println(new Date(day.getTimeInMillis()).toString());

			results.add(isValidDay(snapshotList, cityName, new Date(day.getTimeInMillis())));
		}

		System.out.println("Time elapsed:  " + (System.currentTimeMillis() - tmp1));

		for (CheckDataResult s : results) {
			System.out.println(s.toStringShort());
		}

	}

	/**
	 * Checks whether there are enough snapshots for this day in this city
	 * 
	 * @param day
	 * @param cityName
	 */
	// TODO: Transformieren der Zeitzonen!!!!
	public static CheckDataResult isValidDay(ArrayList<SystemSnapshot> snapshotList, String cityName, Date date) {
		
		CheckDataResult result = new CheckDataResult();
		result.setCityName(cityName);
		result.setDay(date.getTime());
		
		// ArrayList<SystemSnapshot> snapshotList = stationsDAO.loadAllLogsForCity(cityName);

		// System.out.println("Snapshot size for city: " + cityName + "; size: " + snapshotList.size());

		// for (int i = 0; i < 100; i++) {
		// System.out.println(snapshots.get(i).toString());
		// }

		// sort list by time
		sortList(snapshotList);

		// System.out.println("******************************************************");
		//
		// for (int i = 0; i < 100; i++) {
		// System.out.println(snapshots.get(i).toString());
		// }

		// Prepare time boundaries: Startime and Endtime for a day
		Calendar startTime = Calendar.getInstance();
		startTime.setTime(date);// 0:00h
		startTime.set(Calendar.HOUR_OF_DAY, 0);
		startTime.set(Calendar.MINUTE, 0);
		startTime.set(Calendar.SECOND, 0);

		Calendar endTime = Calendar.getInstance();
		endTime.setTime(date);// 23:59h
		endTime.set(Calendar.HOUR_OF_DAY, 23);
		endTime.set(Calendar.MINUTE, 59);
		endTime.set(Calendar.SECOND, 59);

		// System.out.println(new Date(startTime.getTimeInMillis()).toString());
		// System.out.println(new Date(endTime.getTimeInMillis()).toString());

		// get start position of the first snapshot of this day: where does "this" day start and end within the whole data?
		int startPos = 0;
		while (startTime.getTimeInMillis() > snapshotList.get(startPos).getTimestamp()) {
			startPos++;
		}

		int endPos = startPos + 1;
		while (endTime.getTimeInMillis() >= snapshotList.get(endPos).getTimestamp()) {
			endPos++;
		}

		// contains all snapshots of this day
		List<SystemSnapshot> dayList = snapshotList.subList(startPos, endPos);

		// System.out.println("stats: endpos-startpos: " + (endPos - startPos) + " - listSize: " + dayList.size());

		// for (int i = 0; i < dayList.size(); i++) {
		// System.out.println(i + ":" + dayList.get(i));
		// }
		
		result.getDataResult().put(Constants.BUCKET_SIZE_CHOOSER_5_MIN, check(dayList, startTime.getTimeInMillis(), Constants.BUCKET_SIZE_CHOOSER_5_MIN));
		result.getDataResult().put(Constants.BUCKET_SIZE_CHOOSER_10_MIN, check(dayList, startTime.getTimeInMillis(), Constants.BUCKET_SIZE_CHOOSER_10_MIN));
		result.getDataResult().put(Constants.BUCKET_SIZE_CHOOSER_15_MIN, check(dayList, startTime.getTimeInMillis(), Constants.BUCKET_SIZE_CHOOSER_15_MIN));
		result.getDataResult().put(Constants.BUCKET_SIZE_CHOOSER_20_MIN, check(dayList, startTime.getTimeInMillis(), Constants.BUCKET_SIZE_CHOOSER_20_MIN));

		return result;

	}

	/**
	 * Returns the list of observed System Snapshots events ascendingly ordered by timestamp
	 */
	public static void sortList(ArrayList<SystemSnapshot> list) {
		Collections.sort(list, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return Long.valueOf(((SystemSnapshot) arg0).getTimestamp()).compareTo(Long.valueOf(((SystemSnapshot) arg1).getTimestamp()));
			}
		});
	}

	/**
	 * Check a day: Does it have enough data: at least a snapshot every 5 min. If not, the first data point which is to late is printed out.
	 * 
	 * @param dayList
	 *            the day to be analyzed
	 * @param startTime
	 * @param what
	 *            is the max. time distance between to data points
	 * @return
	 */
	public static boolean check(List<SystemSnapshot> dayList, long startTime, int bucketSizeChooser) {

		// BucketSizeChooser determines the maximum time between two data point. -> equidistant -> start always at full hour
		// alle 5min -> 12 -> 300000 -> 288
		// alle 10min -> 6 -> 600000 -> 144
		// alle 15min -> 4 -> 900000 -> 96
		// alle 20min -> 3 -> 1200000 -> 72

		int stepSize = -1;
		int listSize = -1;

		switch (bucketSizeChooser) {
		case 5:
			stepSize = 300000;
			listSize = 288;
			break;
		case 10:
			stepSize = 600000;
			listSize = 144;
			break;
		case 15:
			stepSize = 900000;
			listSize = 96;
			break;
		case 20:
			stepSize = 1200000;
			listSize = 72;
			break;
		}

		// TODO: what is the criterion for a day which has enough data: maybe a timestamp at least every 5 minutes?
		if (dayList.size() < listSize) { // 288 = 24*12 (12 events each hour -> every 5min)
			return false;
		}

		// Start time has to be 0:00h of a day
		// determines the size of a "time bucket"
		// int stepsize = 300000;
		startTime += stepSize;

		for (int i = 0; i < dayList.size(); i++) {

			// System.out.println(TimeConverter.longTime2DateString(dayList.get(i).getTimestamp()) + " - " + TimeConverter.longTime2DateString(startTime));

			if (dayList.get(i).getTimestamp() < startTime) {
				// OK
			} else if ((i != 0) && (startTime <= dayList.get(i).getTimestamp()) && dayList.get(i).getTimestamp() < (startTime + stepSize)) {
				// i!=0 -> does not apply for the first value
				startTime += stepSize;
			} else {
				// Data not valid: to much space between two values
				if (i == 0) {
					System.out.println("err# --> " + TimeConverter.longTime2DateString(dayList.get(i).getTimestamp()));
				} else {
					System.out.println("err#" + TimeConverter.longTime2DateString(dayList.get(i - 1).getTimestamp()) + " - " + TimeConverter.longTime2DateString(dayList.get(i).getTimestamp())
							+ " -> " + ((dayList.get(i).getTimestamp() - dayList.get(i - 1).getTimestamp()) / 60000) + "min");
				}
				return false;
			}
		}
		return true;
	}
}

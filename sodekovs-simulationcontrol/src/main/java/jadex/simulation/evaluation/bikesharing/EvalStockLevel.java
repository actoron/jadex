package jadex.simulation.evaluation.bikesharing;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;


public class EvalStockLevel {
	
	private HashMap<Integer,EvalStockLevelData> stockLevelByTimeSlice = new HashMap<Integer,EvalStockLevelData>();
	private NumberFormat numberFormat = new DecimalFormat("0.00");
	
	
	
	public void setStockLevelData(int timeSlice, EvalStockLevelData data){
		this.stockLevelByTimeSlice.put(timeSlice,data);		
	}
	
	public String resultsToString(){
		StringBuffer result = new StringBuffer();
		
		// Sort by Time Slice
		SortedSet<Integer> timeSliceKeys = new TreeSet<Integer>(stockLevelByTimeSlice.keySet());
		
		for (Integer timeSliceKey : timeSliceKeys) {
						
			result.append("TIME SLICE: ");
			result.append("\t");
			result.append(timeSliceKey);
			result.append("\n");
			

			result.append("RED: " + stockLevelByTimeSlice.get(timeSliceKey).getRedLevelAbsolute() + "(" + numberFormat.format( stockLevelByTimeSlice.get(timeSliceKey).getRedLevelRelative() * 100) + "%)\n");
			result.append("GREEN: " + stockLevelByTimeSlice.get(timeSliceKey).getGreenLevelAbsolute() + "(" + numberFormat.format( stockLevelByTimeSlice.get(timeSliceKey).getGreenLevelRelative() * 100) + "%)\n");
			result.append("BLUE: " + stockLevelByTimeSlice.get(timeSliceKey).getBlueLevelAbsolute() + "(" + numberFormat.format( stockLevelByTimeSlice.get(timeSliceKey).getBlueLevelRelative() * 100) + "%)\n");
			
			result.append("\n#####################################################################");
			result.append("\n#####################################################################\n");
		}
		
		return result.toString();
	}
	
	public ArrayList<EvalStockLevelData> resultsAsList(){
		ArrayList<EvalStockLevelData> res = new ArrayList<EvalStockLevelData>();
		
		// Sort by Time Slice
		SortedSet<Integer> timeSliceKeys = new TreeSet<Integer>(stockLevelByTimeSlice.keySet());
		
		for (Integer timeSliceKey : timeSliceKeys) {
			
			res.add(stockLevelByTimeSlice.get(timeSliceKey));						
		}		
		return res;
	}
}

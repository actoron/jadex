package de.unihamburg.vsis.jadexAndroid_test.chat;

import java.util.LinkedList;
import java.util.List;

public class PerformanceResult {

	public String fromDevice;
	public String toDevice;

	public static int[] byteLengths = { 32, 512, 1024, 2048, 10 * 1024,
			20 * 1024 };
	
//	public static int[] byteLengths = { 32, 512, 1024, 2048};
	
//	public static int[] byteLengths = { 32};
	
	private int[] byteLengthToTry = {32};

	public List<PingRun> pings;
	private PingRun currentPingRun;

	public PerformanceResult(int[] bytesLengthsToTry) {
		this.pings = new LinkedList<PerformanceResult.PingRun>();
		this.byteLengthToTry = bytesLengthsToTry;
	}
	
//	public void setMaxByteLength(int byteLen) {
//		int count = 0;
//		for (int i = 0; i < byteLengths.length; i++) {
//			if (byteLengths[i] >= byteLen) {
//				break;
//			}
//			count++;
//		}
//		byteLengthToTry = new int[count+1];
//		for (int i = 0; i < byteLengthToTry.length && i < byteLengths.length; i++) {
//			byteLengthToTry[i] = byteLengths[i];
//		}
//	}
	
	public void addDelay(int delay) {
		currentPingRun.putDelay(delay);
	}

	public void newPingRun(int byteLen) {
		currentPingRun = new PingRun();
		currentPingRun.byteLen = byteLen;
		pings.add(currentPingRun);
	}

	public int getPingRunCount() {
		return pings.size();
	}

	public class PingRun {
		public int byteLen;
		public int pingCount;
		public List<Integer> delays;
		public int maxDelay;
		public int minDelay;

		public PingRun() {
			delays = new LinkedList<Integer>();
			maxDelay = Integer.MIN_VALUE;
			minDelay = Integer.MAX_VALUE;
		}

		public void putDelay(int delay) {
			if (delay > maxDelay) {
				maxDelay = delay;
			}
			if (delay < minDelay) {
				minDelay = delay;
			}
			delays.add(delay);
		}

		public int getAverageDelay() {
			int sum = 0;
			for (Integer delay: delays) {
				sum = sum + delay;
			}
			
			if (delays.size() > 0) {
				return sum / delays.size();
			} else {
				return sum;
			}
		}
		
		public int getVarianz() {
			int erg = 0;
			int u = getAverageDelay();
			
			for (Integer wert : delays) {
				erg += Math.pow(wert -u, 2);
			}
			
			return erg / (delays.size() -1);
		}
	}

	public boolean isComplete() {
		return (getPingRunCount() == byteLengthToTry.length); 
	}

	public int getNextByteLength() {
		return byteLengthToTry[getPingRunCount()];
	}

	public int getCurrentPingRunAverage() {
		return currentPingRun.getAverageDelay();

	}
	
	public int getCurrentPingRunCount() {
		return currentPingRun.delays.size();
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("From ");
		s.append(this.fromDevice);
		s.append(" to ");
		s.append(this.toDevice);
		s.append("\n");
		for (PingRun ping : pings) {
			s.append(ping.byteLen);
			s.append("b - ");
			s.append("min: " + ping.minDelay);
			s.append(" max: ");
			s.append(ping.maxDelay);
			s.append(" average: ");
			s.append(ping.getAverageDelay());
			s.append(" varianz: ");
			s.append(ping.getVarianz());
			s.append("\n");
		}
		
		return s.toString();
	}

}

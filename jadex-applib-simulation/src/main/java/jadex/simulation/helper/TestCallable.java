package jadex.simulation.helper;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class TestCallable implements Callable<String> {

	private String res;
	private final String a;
	private Object monitor;

	TestCallable(String a, Object monitor) {
		this.a = a;
		this.res = null;
		this.monitor = monitor;
	}

	public String call() {
		for(int i=0; i < 1000; i++){
		System.out.println("i: "  + i);
		res = String.valueOf(i);
		}
		T2 t2 = new T2(monitor);
		t2.start();
		
		synchronized (monitor) {
			try {
				monitor.wait();
			} catch (InterruptedException  e) {

			}
		}
	
		return String.valueOf(res);
	}

}
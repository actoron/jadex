package jadex.simulation.helper;

public class T2 extends Thread {

	private Object monitor;

	public T2(Object monitor) {
		this.monitor = monitor;
	}

	public void run() {
		System.out.println("Started T2...and producing?");
//		while (true) {
			synchronized (monitor) {
				for (int i = 0; i < 100; i++) {
					System.out.println("iii: " + i);
				}
				monitor.notify();
			}
			
			try {
	            Thread.sleep(3000);
	         } catch (InterruptedException e) {
	            //nichts
	         }

//		}
			System.out.println("End T2...?");
	}
}
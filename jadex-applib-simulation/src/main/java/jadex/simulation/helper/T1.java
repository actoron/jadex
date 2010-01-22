package jadex.simulation.helper;

public class T1 extends Thread {

	private Object monitor;

	public T1(Object monitor) {
		this.monitor = monitor;
	}

	public void run() {
		System.out.println("Started T1...and wait?");
//		while (true) {
			synchronized (monitor) {
				try {
					monitor.wait();
				} catch (InterruptedException  e) {

				}
			}
//		}
			System.out.println("Continue T1...?");
	}
}
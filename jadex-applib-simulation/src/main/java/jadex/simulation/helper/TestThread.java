package jadex.simulation.helper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestThread extends Thread {

//	final boolean finished = false;
	
	final ObserverCallable obs;

//	public TestThread() {
//		// TODO Auto-generated constructor stub
//	};
	
	public TestThread(ObserverCallable obs) {
		this.obs = obs;
	}
	
	private boolean threadSuspended = true;

	public void run() {
//		while (true) {
			
			
//			try {
//				for (int i = 0; i < 10000; i++) {
//					System.out.println("i: " + i);
//				}
			System.out.println("Start Main Thread");
//				Thread.currentThread().sleep(10000);
				
//				System.out.println("Start Main Thread");
//				 new Runnable() {
//				
//					@Override
//					public void run() {
						System.out.println("Started Sub Thread");
						for (int i = 0; i < 20; i++) {
							System.out.println("subThread: " + i);
						}
//						try {
//							sleep(5000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						System.out.println("Finished Sub Thread");
						obs.threadSuspended = false;
						obs.notify();
//						threadSuspended = false;
//						setBoolean();
//					}
//				}.run();
				
//				System.out.println("Sleep Main Thread2");
				
				
//
//				if (threadSuspended) {
//					synchronized (this) {
//						while (threadSuspended)
//							wait();
//					}
//				}
//			} catch (InterruptedException e) {
//			}
//			System.out.println("Main Thread ready!");
//		}
	}

//	private void test() {
//		Callable<String> c = new TestCallable(new String("test"));
//		ExecutorService executor = Executors.newCachedThreadPool();
//
//		Future<String> result = executor.submit(c);
//		try {
//			System.out.println("calling res:");
//			String myRes = result.get();
//			System.out.println("got res:" + myRes);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
	public void setBoolean(){
		this.threadSuspended = false;
	}

}

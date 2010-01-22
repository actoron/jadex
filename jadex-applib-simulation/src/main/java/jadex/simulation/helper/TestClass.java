package jadex.simulation.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		ArrayList<String> l1 = new ArrayList<String>();
		ArrayList<String> l2 = new ArrayList<String>();
		
		l1.add("1");
		l1.add("2");
		l1.add("3");
		
		ArrayList<String> dstList = new ArrayList<String>(l1);

		
		
		Collections.copy(l2, l1);
		
		
		
//		byte[] b = new byte[ 4000000 ]; 
//		new Random().nextBytes( b ); 
		
		callableTest();

//		new TestThread().run().;
//		TestThread t = new TestThread();
//		t.start();
		
//		Object monitor = new Object();
//		
//		T1 t1 = new T1(monitor);
//		T2 t2 = new T2(monitor);
//		
//		t1.start();
//		t2.start();
//		
		
		
		
//		callableTest();
//		
//		
//		t.setBoolean();
		
		
	}

	
	private static void callableTest(){
		Callable<String> c = new TestCallable( new String("test"), new Object() ); 
		ExecutorService executor = Executors.newCachedThreadPool();
		
		Future<String> result = executor.submit( c );
		try {
			System.out.println("calling res:");
			String myRes  = result.get();
			System.out.println("got res:" + myRes);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

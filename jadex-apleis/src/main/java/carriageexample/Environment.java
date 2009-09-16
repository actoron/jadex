package carriageexample;

enum RobotAction { PUSH,WAIT };

public class Environment implements Runnable {

	private EnvironmentWindow window = new EnvironmentWindow();
	
	private RobotAction robotAction1 = RobotAction.WAIT;
	private RobotAction robotAction2 = RobotAction.WAIT;
	private long stepNum = 0;

	private int carriagePos = 0;

	private boolean incompleteInfo = false; // TODO implement

	public Environment() {
		
		Thread t = new Thread( this ); 
		//t.setPriority(Thread.MIN_PRIORITY);
		t.start();

	}
	
	
	public void run() {

		while(true) {

			if( robotAction1 == RobotAction.PUSH && robotAction2 == RobotAction.WAIT ) {

				carriagePos ++;
				if( carriagePos >= 3 ) 
					carriagePos = 0;
	
			}
			else if( robotAction1 == RobotAction.WAIT && robotAction2 == RobotAction.PUSH ) {
				
				carriagePos --;
				if( carriagePos < 0 ) 
					carriagePos = 2;

			}
			
			robotAction1 = RobotAction.WAIT;
			robotAction2 = RobotAction.WAIT;
			stepNum ++;

			window.setState(carriagePos);

			// block for 1 second
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e);

			}

		}

	}


	public int getCarriagePos() {

		return carriagePos;
	
	}

	public void robotPush1() {
		
		long currStep = stepNum;
		
		robotAction1 = RobotAction.PUSH;
		
		while (currStep == stepNum);
		
	}
	
	public void robotPush2() {

		long currStep = stepNum;

		robotAction2 = RobotAction.PUSH;

		while (currStep == stepNum);

	}	
	
	public void robotWait1() {
		
		long currStep = stepNum;

		robotAction1 = RobotAction.WAIT;

		while (currStep == stepNum);

	}
	
	public void robotWait2() {
		
		long currStep = stepNum;

		robotAction2 = RobotAction.WAIT;

		while (currStep == stepNum);

	}

	public long getStepNumber() {
		
		return stepNum;
	
	}


	public int getRobotPercepts1() {

		return carriagePos;
	
	}


	public int getRobotPercepts2() {
		
		return carriagePos;

	}


	public void release() {

		window.dispose();
		
	}	

}
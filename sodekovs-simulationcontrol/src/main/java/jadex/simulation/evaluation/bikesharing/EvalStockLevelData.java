package jadex.simulation.evaluation.bikesharing;

public class EvalStockLevelData {
	
	private int redLevelAbsolute;	
	private int greenLevelAbsolute;
	private int blueLevelAbsolute;
	
	private double redLevelRelative;
	private double greenLevelRelative;
	private double blueLevelRelative;
	
	
	/**
	 * PRECONDITION: Absolute values have to be set!!!!!
	 */
	public void computeRelativeValues(){
		int sum = redLevelAbsolute+greenLevelAbsolute+blueLevelAbsolute;		
		this.redLevelRelative = new Double(redLevelAbsolute)/sum;
		this.blueLevelRelative = new Double(blueLevelAbsolute)/sum;
		this.greenLevelRelative = new Double(greenLevelAbsolute)/sum;		
	}
	
	
	
	public int getRedLevelAbsolute() {
		return redLevelAbsolute;
	}
	public void setRedLevelAbsolute(int redLevelAbsolute) {
		this.redLevelAbsolute = redLevelAbsolute;
	}
	public int getGreenLevelAbsolute() {
		return greenLevelAbsolute;
	}
	public void setGreenLevelAbsolute(int greenLevelAbsolute) {
		this.greenLevelAbsolute = greenLevelAbsolute;
	}
	public int getBlueLevelAbsolute() {
		return blueLevelAbsolute;
	}
	public void setBlueLevelAbsolute(int blueLevelAbsolute) {
		this.blueLevelAbsolute = blueLevelAbsolute;
	}
	public double getRedLevelRelative() {
		return redLevelRelative;
	}
	public void setRedLevelRelative(double redLevelRelative) {
		this.redLevelRelative = redLevelRelative;
	}
	public double getGreenLevelRelative() {
		return greenLevelRelative;
	}
	public void setGreenLevelRelative(double greenLevelRelative) {
		this.greenLevelRelative = greenLevelRelative;
	}
	public double getBlueLevelRelative() {
		return blueLevelRelative;
	}
	public void setBlueLevelRelative(double blueLevelRelative) {
		this.blueLevelRelative = blueLevelRelative;
	}

}

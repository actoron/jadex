package jadex.micro.examples.mandelbrot;

/**
 *  Service for displaying the result of a calculation. 
 */
public interface IDisplayService
{
	/**
	 *  Display the result of a calculation.
	 */
	public void displayResult(Integer[][] result);
}

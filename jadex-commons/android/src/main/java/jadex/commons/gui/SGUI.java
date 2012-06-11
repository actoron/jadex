package jadex.commons.gui;

/**
 * Android implementation of {@link SGUI}.
 */
public class SGUI {
	public static final boolean HAS_GUI = false;

	/**
	 * Just run the given runnable.
	 * 
	 * @param runnable
	 */
	public static void invokeLater(Runnable runnable) {
		runnable.run();
	}
}

package jadex.android.standalone.clientapp;

/**
 * This interface should be implemented by Fragments of the ClientApp that are
 * not the main Fragment.
 * It provides additional functionality by defining callback methods for the Middleware to call.
 * @author Julian Kalinowski
 *
 */
public interface ClientAppFragment {
	/**
	 * Called after the Fragment has been attached to the activity
	 * to provide access to the main ClientAppFragment.
	 * @param mainFragment
	 */
	public void onAttachMainFragment(ClientAppMainFragment mainFragment);
}

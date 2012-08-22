package jadex.platform.service.remote;

/**
 *  Interface that is needed for dynamic proxy objects.
 *  Only if a dynamic proxy implements an interface that
 *  includes the finalize method it will be called when
 *  the proxy is garbage collected.
 */
public interface IFinalize
{
	/**
	 *  Finalize method called before gc.
	 */
	public void finalize() throws Throwable;
}

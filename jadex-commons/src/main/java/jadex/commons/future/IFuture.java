package jadex.commons.future;


/**
 *  Interface for futures. Similar to Java Future interface
 *  but adds a listener notification mechanism.
 */
public interface IFuture
{
    /**
     *  Test if done, i.e. result is available.
     *  @return True, if done.
     */
    public boolean isDone();

    /**
     *  Get the result - blocking call.
     *  @return The future result.
     */
    public Object get(ISuspendable caller);

    /**
     *  Get the result - blocking call.
     *  @param timeout The timeout in millis.
     *  @return The future result.
     */
    public Object get(ISuspendable caller, long timeout);
    
    /**
     *  Add a result listener.
     *  @param listener The listener.
     */
    public void addResultListener(IResultListener listener);
}

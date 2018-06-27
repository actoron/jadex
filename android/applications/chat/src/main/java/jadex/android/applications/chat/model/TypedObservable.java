package jadex.android.applications.chat.model;

import java.util.Observer;
import java.util.Vector;

public class TypedObservable<T> implements ITypedObservable<T> {

	 private boolean changed = false;
	    private Vector<ITypedObserver<T>> obs;

	    /** Construct an Observable with zero Observers. */

	    public TypedObservable() {
	        obs = new Vector<ITypedObserver<T>>();
	    }

	    /**
	     * Adds an observer to the set of observers for this object, provided
	     * that it is not the same as some observer already in the set.
	     * The order in which notifications will be delivered to multiple
	     * observers is not specified. See the class comment.
	     *
	     * @param   o   an observer to be added.
	     * @throws NullPointerException   if the parameter o is null.
	     */
	    @Override
		public synchronized void addObserver(ITypedObserver<T> o) {
	        if (o == null)
	            throw new NullPointerException();
	        if (!obs.contains(o)) {
	            obs.addElement(o);
	        }
	    }

	    /**
	     * Deletes an observer from the set of observers of this object.
	     * Passing <CODE>null</CODE> to this method will have no effect.
	     * @param   o   the observer to be deleted.
	     */
	    @Override
		public synchronized void deleteObserver(Observer o) {
	        obs.removeElement(o);
	    }

	    /**
	     * If this object has changed, as indicated by the
	     * <code>hasChanged</code> method, then notify all of its observers
	     * and then call the <code>clearChanged</code> method to
	     * indicate that this object has no longer changed.
	     * <p>
	     * Each observer has its <code>update</code> method called with two
	     * arguments: this observable object and <code>null</code>. In other
	     * words, this method is equivalent to:
	     * <blockquote><tt>
	     * notifyObservers(null)</tt></blockquote>
	     *
	     */
	    public void notifyObservers() {
	        notifyObservers(null, TypedObserver.NOTIFICATION_TYPE_NONE);
	    }

	    public void notifyObservers(T arg) {
	    	notifyObservers(arg, TypedObserver.NOTIFICATION_TYPE_NONE);
	    }
	    
	    /**
	     * If this object has changed, as indicated by the
	     * <code>hasChanged</code> method, then notify all of its observers
	     * and then call the <code>clearChanged</code> method to indicate
	     * that this object has no longer changed.
	     * <p>
	     * Each observer has its <code>update</code> method called with two
	     * arguments: this observable object and the <code>arg</code> argument.
	     *
	     * @param   arg   any object.
	     */
	    public void notifyObservers(T arg, int notificationType) {
	        /*
	         * a temporary array buffer, used as a snapshot of the state of
	         * current Observers.
	         */
	    	ITypedObserver<T>[] arrLocal;

	        synchronized (this) {
	            /* We don't want the Observer doing callbacks into
	             * arbitrary code while holding its own Monitor.
	             * The code where we extract each Observable from
	             * the Vector and store the state of the Observer
	             * needs synchronization, but notifying observers
	             * does not (should not).  The worst result of any
	             * potential race-condition here is that:
	             * 1) a newly-added Observer will miss a
	             *   notification in progress
	             * 2) a recently unregistered Observer will be
	             *   wrongly notified when it doesn't care
	             */
	            if (!changed)
	                return;
	            arrLocal = obs.toArray(new ITypedObserver[obs.size()]);
	            clearChanged();
	        }

	        for (int i = arrLocal.length-1; i>=0; i--)
	            arrLocal[i].update(this, arg, notificationType);
	    }

	    /**
	     * Clears the observer list so that this object no longer has any observers.
	     */
	    @Override
		public synchronized void deleteObservers() {
	        obs.removeAllElements();
	    }

	    /**
	     * Marks this <tt>Observable</tt> object as having been changed; the
	     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
	     */
	    public synchronized void setChanged() {
	        changed = true;
	    }

	    /**
	     * Indicates that this object has no longer changed, or that it has
	     * already notified all of its observers of its most recent change,
	     * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
	     * This method is called automatically by the
	     * <code>notifyObservers</code> methods.
	     *
	     * @see     java.util.Observable#notifyObservers()
	     * @see     java.util.Observable#notifyObservers(java.lang.Object)
	     */
	    protected synchronized void clearChanged() {
	        changed = false;
	    }

	    /**
	     * Tests if this object has changed.
	     *
	     * @return  <code>true</code> if and only if the <code>setChanged</code>
	     *          method has been called more recently than the
	     *          <code>clearChanged</code> method on this object;
	     *          <code>false</code> otherwise.
	     */
	    @Override
		public synchronized boolean hasChanged() {
	        return changed;
	    }

	    /**
	     * Returns the number of observers of this <tt>Observable</tt> object.
	     *
	     * @return  the number of observers of this object.
	     */
		public synchronized int countObservers() {
	        return obs.size();
	    }

}

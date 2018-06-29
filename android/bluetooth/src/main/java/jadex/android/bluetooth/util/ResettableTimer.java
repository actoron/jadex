package jadex.android.bluetooth.util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class was created by Jason S.
 * http://stackoverflow.com/questions/2142287/resettable-timeout-in-java
 */
public class ResettableTimer {
    final private ScheduledExecutorService scheduler;
    final private long timeout;
    final private TimeUnit timeUnit;
    final private Runnable task;
    final private AtomicReference<ScheduledFuture<?>> ticket
        = new AtomicReference<ScheduledFuture<?>>();
    /* use AtomicReference to manage concurrency 
     * in case reset() gets called from different threads
     */

    public ResettableTimer(ScheduledExecutorService scheduler, 
            long timeout, TimeUnit timeUnit, Runnable task)
    {
        this.scheduler = scheduler;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.task = task;
    }
    
    public void stop() {
    	ScheduledFuture<?> scheduledFuture = this.ticket.get();
    	if (scheduledFuture != null) {
    		scheduledFuture.cancel(false);
    	}
    	this.scheduler.shutdown();
    }
    
    public ResettableTimer reset(boolean mayInterruptIfRunning) {
        /*
         *  in with the new, out with the old;
         *  this may mean that more than 1 task is scheduled at once for a short time,
         *  but that's not a big deal and avoids some complexity in this code 
         */
        ScheduledFuture<?> newTicket = this.scheduler.schedule(
                this.task, this.timeout, this.timeUnit);
        ScheduledFuture<?> oldTicket = this.ticket.getAndSet(newTicket);
        if (oldTicket != null)
        {
            oldTicket.cancel(mayInterruptIfRunning);
        }
        return this;
    }
}

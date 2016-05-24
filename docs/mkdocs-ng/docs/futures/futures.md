<!--Starting a platform with standard arguments is very easy using the *createPlatform(...)* method of the *jadex.base.Starter* class. The return value of this method is a future object that contains the external access of the platform component, once the platform startup has finished successfully. For fetching the future result, you could use a result listener as known from the previous lessons. As the thread running the main() method is not managed by the Jadex concurrency model there is another option. Using a so called *ThreadSuspendable*, the main thread can be blocked until the future is available. This technique avoids the necessity of inner classes, which comes with the use of result listeners. Note, that usage of the thread suspendable is only allowed when running on threads, which are not managed by Jadex. If you try to use the thread suspendable in Jadex threads, e.g. in component or service code, the system will probably run into deadlocks.-->



## Future as return type
(allows you to perform asynchronous work)


## Types
ITuple2Future (create component result)
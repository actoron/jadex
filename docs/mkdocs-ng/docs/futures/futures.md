# Futures

In a typical method call, the caller is *blocked* waiting for the callee to finish it's processing. When the callee is done, the result is delivered to the caller. At this point, the caller gets unblocked and can continue with it's own program.
  
![Synchronous Call](synccall.png)

While the caller is blocked, it cannot perform any operations. To solve this, Jadex introduces *Futures*, which allow for asynchronous calls.  
After invocation, the callee returns a *Future*. The caller now has two options, shown in the figure below:

![Asynchronous Call](asynccall.png)

The options are:

- Block and wait for the result (left) by calling ```get()``` on the future
- Add a result listener (```addResultListener```) and continue (right)

Futures make it possible to perform asynchronous communication with local and/or remote components and services and are used throughout in Jadex Active Components.
For more information and motivation, please refer to [Asynchronous Programming](../guides/ac/03 Asynchronous Programming/).

# Working with Futures

The typical case of working with futures is calling a method from the Jadex framework which returns a ```IFuture<>``` type.
In this case, you can either call *get()* on the future, which will block until the result is available, or **add a result listener** (which will be the default case in this chapter from now on):

```java
IFuture<IComponentManagementService> fut = SServiceProvider.getService(access, IComponentManagementService.class);
fut.addResultListener(new DefaultResultListener<IComponentManagementService>() {
    public void resultAvailable(IComponentManagementService result) {
        // do your work with the result
    }
});
// do other, independent work
```

If you want or need a method to **return** a futurized result instead of a standard Java type, you can wrap it:

```java
public IFuture<String> getName() {
    return new Future<String>(name);
}
```

Most of the time, both cases will be combined, e.g., as asynchronous methods are called, the result of your computation will also be available asynchronously:

```java
public IFuture<String> getHelloString() {
    final Future<String> result = new Future<String>();
    IFuture<String> provName = nameProvider.getName();
    provName.addResultListener(new DefaultResultListener<String>() {
        public void resultAvailable(String name) {
            result.setResult("Hello, this is " + name);
        }
    });

    return result; // will be returned before setResult() is called!
}
```

<x-hint title="Futures and Void">
You can also wrap a *void* return type using IFuture<Void>.
This allows the callee to specify the point in time when it is ready by calling *setResult(null)* on the future.
</x-hint>

As you can see from the example above, a Future can be created and returned before the result is available.
Once your calculations are done, just call *setResult()* to make it available to all attached (and future) listeners. 

## Exceptions
If an exception rises, you can pass it to listeners of your Future object by calling *setException()* - it can then be handled by the listeners:

```java
IFuture<String> provName = nameProvider.getName();
provName.addResultListener(new DefaultResultListener<String>() {
	public void resultAvailable(String name) {
		...
	}
	
	public void exceptionOccurred(Exception e) {
		// handle exception
	}
});
```

When you use ```DefaultResultListener```, the **default implementation will print stack traces** of thrown exceptions.

When you are using *get()* to synchronously get the result, **exceptions will be thrown** as RuntimeExceptions and printed to the console by Java by default.

## Java 8 Features
Java 8 introduces *lambda expressions* together with *functional interfaces*.
That makes it possible to get rid of some boilerplate code when using futures.

Using Java 8, adding a listener to a future can look like this:

```java
IFuture<String> fut = ...
fut.addResultListener(str -> {
	System.out.println(str); // str is the result from the future
});
```

You can also handle exceptions (defaults to printing the stack trace) this way by adding a second lambda expression:

```java
IFuture<String> fut = ...
fut.addResultListener(
	str -> System.out.println(str),
	ex -> ex.printStackTrace()
);
```

Other Future types also provide support for functional interfaces - just look for parameters declared with type ```IFunctionalResultListener```:

|Future|Functional support in methods|
|------|-----------------------------|
|**Future**|*addResultListener()*|
|**Tuple2Future**|*addTuple2ResultListener()*|
|**IntermediateFuture**|*addIntermediateResultListener*|


TODO: intermediate mapAsync

TODO: thenApply, thenCompose, thenAccept, thenCombine?

# Future Types
The following is a short list of commonly used future types. For a more complete guide, visit [Asynchronous Programming](../guides/ac/03 Asynchronous Programming/#programming-futures-and-listeners).

## Tuple2 Futures
The Tuple2 Future can be used if exactly two result values should be returned. 
With the ```ITuple2ResultListener``` a listener type is available that offers two corresponding methods *firstResultAvailable()* and *secondResultAvailable()*. 
These methods may be called in any order, depending on the order in which the callee delivers the results.  

### Delivering Results
```java
Tuple2Future<String, Integer> fut = new Tuple2Future<String,Integer>();
fut.setFirstResult("first");
fut.setSecondResult(2);
```

### Listening to Results
```java
fut.addResultListener(new DefaultTuple2ResultListener<String, Integer>() {
		public void firstResultAvailable(String result) {
			// use first result
		}

		public void secondResultAvailable(Integer result) {
			// use second result
		}
		public void exceptionOccurred(Exception exception) {
			// handle exception
		}
	});
```

## Intermediate Futures
Intermediate futures allow for receiving multiple result values of the same type and handling them independently of each other.
As usual, retrieving the result can be done in a blocking fashion (*getNextIntermediateResult()*) or by using a listener of the type ```IntermediateResultListener```: 

### Delivering Results
```java
IntermediateFuture<String> fut = new IntermediateFuture<String>();
fut.addIntermediateResult("intermediate1");
fut.addIntermediateResult("intermediate2");
fut.addIntermediateResult("intermediate3");
fut.setFinished();
```

### Listening to Results
```java
fut.addIntermediateResultListener(new IntermediateDefaultResultListener<String>() {
	public void intermediateResultAvailable(String result) {
		// each result is passed individually to the listener, 
		// so it can handle the intermediate results before the future is finished.
	}

	public void finished() {
		// called when the future is finished (all results are available).
	}
});
```

## Subscription Futures
A subscription future can be used to establish a publish/subscribe relationship between the caller and callee. In general, it works just like the intermediate future, with the difference that it doesn't save intermediate results for listeners that are attached at a later point in time.
 
### Delivering Results
```java
SubscriptionIntermediateFuture<String> fut = new SubscriptionIntermediateFuture<String>();
fut.addIntermediateResult("event1");
fut.addIntermediateResult("event2");
fut.addIntermediateResult("event3");
```
 
### Listening to Results
```java
fut.addIntermediateResultListener(new IntermediateDefaultResultListener<String>() {
	public void intermediateResultAvailable(String result) {
		// handle intermediate result
	}
});
```

## Terminable Futures
TODO:
ITerminableFuture<E>: A terminable future allows the caller to cancel the task at any point in time by called terminate(). This termination will reach the called entity which may react to the request by stopping its activities regarding the invocation (the callee is not forced to do so). At callee side a termination command can be used to state what should be done when a call is cancelled.??

### Delivering Results
### Listening to Results

## Pull Futures
TODO

IPullIntermediateFuture<E>: A pull future allows for realizing an iterator relationship between caller and callee. This means that is this case the caller can decide when it wants to receive the next result by calling pullIntermediateResult(). The functionality that should be executed in case of a pull of the caller is supplied as command by the callee.
IPullSubscriptionIntermediateFuture<E>: The subscription version of the pull future.??

### Delivering Results
### Listening to Results

# Special Result Listeners

## Counting

## Delegation

# Debugging
TODO

# Common Pitfalls

## No Error listener
When adding Future listeners, it is important to always have *exceptionOccurred* implemented in a senseful way.
This is done by default by ```DefaultResultListener```, but needs to be done manually when using the interface ```IResultListener``` instead.
Also, if there is **no** listener attached at all, exceptions will be silent as well.
 
So if you're code doesn't do the right thing and no exceptions occur, this might be a good idea to start looking for.
 
## get() on Main/UI Thread
As the thread running the main() method is not managed by the Jadex concurrency model, *Future.get()* should not be executed while on the main thread. Instead, use the asynchronous *addResultListener()* methods. 
Calling *get()* on the main thread may result in a hung-up program.

# Advanced Topics


## SResultListener

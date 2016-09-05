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

### Asynchronous Map Function
With intermediate futures, you can receive many results from one call (see [future types](#future-types)).
To loop over all results and ['map'](https://en.wikipedia.org/wiki/Map_(higher-order_function)) them with a given asynchronous function, you can use ```mapAsync()```.
The result will be another intermediate future, where the results will be mapped by the given function:

```java
IIntermediateFuture<String> fut = new IntermediateFuture<>(Arrays.asList("potato", "carrot", "onion"));
IIntermediateFuture<String> res = abc.mapAsync(s -> new Future<String>(s.substring(0,1).toUpperCase() + s.substring(1)));

System.out.println(res.get());
```

### Asynchronous Combination
Following the [Java 8 CompletableFuture API](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html), Jadex Futures provide the following methods to chain asynchronous tasks: *thenApply*, *thenCompose*, *thenAccept*, *thenCombine*, *applyToEither*, *acceptEither*.

```java
Future<String> fut = new Future<String>("hello")

// apply a synchronous function that returns a result: 
IFuture<String> thenApply = fut.thenApply(s -> s + "_test");

// apply a synchronous function without result:
fut.thenAccept(s -> System.out.println(s));

// compose with a futurized function:
IFuture<String> thenCompose = fut.thenCompose(s -> new Future<String>(s+"_test"));

// combine two async results with a bifunction:
IFuture<String> translated = fut.thenCombine(getTranslator(), (s, translator) -> translator.translate(s));
```

There are also variants of *apply* and *accept*: *applyToEither* and *acceptEither* will take a second future and apply the given function to the result that is available first, ignoring the second result. 

For a complete documentation of these methods, please visit the [IFuture API documentation](${URLJavaDoc}/jadex/commons/future/IFuture.html).

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
A terminable future allows the caller to cancel the task at any point in time by calling terminate().
 The callee may specify it's behaviour in this case by providing a ```TerminationCommand```. In any case, the future cannot transfer results after termination but will instead throw an Exception.

### Delivering Results

```java
TerminableFuture<String> fut = new TerminableFuture<String>();
ITerminationCommand term = new ITerminationCommand() {

    public boolean checkTermination(Exception reason) {
        return true; // termination is accepted at this point
    }

    public void terminated(Exception reason) {
        stopWork();
    }
};
startWork(fut); // startWork() will call fut.setResult() eventually
```

### Listening to Results
Reception of the results works just like with any other future type.

### Terminating
To terminate the work that was scheduled, the caller can call ```terminate()``` and may also provide an exception as reason:
```java
fut.terminate(new RuntimeException("User clicked cancel"));
```

## Pull Futures
While intermediate futures implement a *push* semantic where the caller is informed about an available result, with pull intermediate futures, the caller decides when it wants to check for new results and receive them by calling ```pullIntermediateResult()```.
The callee has to specify how to deliver new results upon a pull request.


### Delivering Results
```java
ICommand<PullIntermediateFuture<String>> cmd = new ICommand<PullIntermediateFuture<String>>() {
    public void execute(PullIntermediateFuture<String> args) {
        args.addIntermediateResult("pull result");
    }
};
IPullIntermediateFuture<String> fut = new PullIntermediateFuture<String>();

```
### Listening to Results
```java
fut.addIntermediateResultListener(new IntermediateDefaultResultListener<String>() {
    public void intermediateResultAvailable(String result) {
        // handle intermediate result
    }
});
fut.pullIntermediateResult();
```

# Common Pitfalls
This section discusses problems frequently encountered in the Jadex Active Components community. 

## No Stack Trace, just a one-liner
Because exceptions are generally passed through a chain of result listeners, sometimes you may forget to print stack traces and only get a one-line warning message.  
Enabling future debugging as [configuration example](../platform/platform/#configuration-examples) suggests will always print useful stack traces when exceptions occur (if some kind of  ```DefaultResultListener``` is attached!).  
Note that enabling this feature will seriously **decrease performance**, as debug information is remembered throughout the application!
 
## No error message, calls silently fail
When adding Future listeners, it is important to always have *exceptionOccurred* implemented in a senseful way.
This is done by default by ```DefaultResultListener```, but needs to be done manually when using the interface ```IResultListener``` instead.
Also, if there is **no** listener attached at all, exceptions will be silent as well.
 
So if you're code doesn't do the right thing and no exceptions occur, this might be a good idea to start looking for.
 
## Get() hangs on Main/UI Thread
As the thread running the main() method is not managed by the Jadex concurrency model, *Future.get()* should not be executed while on the main thread. Instead, use the asynchronous *addResultListener()* methods. 
Calling *get()* on the main thread may result in a hung-up program.

# Advanced Topics

## Special Result Listeners

For some generic, re-occuring tasks, Jadex includes special result listeners.
Some of the most useful types are introduced below, for a complete list check out the ```IResultListener``` [subtypes](${URLJavaDoc}/jadex/commons/future/IResultListener.html).

### Delegation
As Futures, respectively the corresponding asynchronous calls, get nested, we need way to delegate results from one Future to the other.
E.g., in the following method, we want to add two integers and then convert them to a string.  
The ```ExceptionDelegationResultListener``` forwards exceptions to the given future and allows to implement a custom result handling.  
The ```DelegationResultListener``` forwards results and exceptions to the given future.
```java
public IFuture<String> addAndToString(int a, int b) {
	Future<String> res = new Future<String>();
	addService.add(a,b).addResultListener(new ExceptionDelegationResultListener<Integer, String>(res) {
		@Override
		public void customResultAvailable(Integer result) throws Exception {
			toStringService.toString(result).addResultListener(new DelegationResultListener<String>(res));
		}
	});
	return res;
}
```

Additionally, with Java 8 Lambda Expressions, you can also use ```SResultListener.delegate``` and ```SResultListener.delegateExceptions```:
```java
addService.add(a,b).addResultListener(SResultListener.delegateExceptions(res, 
	sum -> toStringService.toString(sum).addResultListener(SResultListener.delegate(res))));
```

### Counting
In many cases, one wants to wait for multiple asynchronous calls to be completed.
This can simply be implemented using a ```CounterResultListener```:

```java
				final Future<Void> completionFuture = new Future<Void>();
				Future<String> call1 = new Future<String>();
				Future<String> call2 = new Future<String>();
				Future<String> call3 = new Future<String>();

				CounterResultListener<String> res = new CounterResultListener(3, new DelegationResultListener<Void>(completionFuture));

				call1.addResultListener(res);
				call2.addResultListener(res);
				call3.addResultListener(res);
				completionFuture.get();
```

This will make completionFuture.get() block until all three Futures/Calls are done. 
It is also possible to manually advance the counter by calling ```res.resultAvailable(null)``` for more flexibility.

## SResultListener
When using Java 8 and [functional result listeners](#java-8-features), there is no need to create inner classes, even if you want to delegate results to another future or count results with a *CounterResultListener*.  
Instead, you can use the static methods of ```SResultListener```:
```java
// delegate results and exceptions:
fut.addResultListener(SResultListener.delegate(myFut));

// use results, delegate exceptions:
fut.addResultListener(res -> ... , SResultListener.delegate(myFut));

// delegate results, use exceptions:
fut.addResultListener(SResultListener.delegate(myFut), ex -> ...);

// count results
CounterResultListener<> counter = SResultListener.countResults(2, reached -> System.out.println("reached"), ex -> ex.printStackTrace());
```

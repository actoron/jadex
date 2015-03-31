package jadex.commons.future;

/**
 * Static helper class for creating result listeners.
 */
public class SResultListener {

	/**
	 * Constant with an no-op success listener.
	 */
	private final static IFunctionalResultListener<? extends Object> EMPTY_SUCCESS_LISTENER = new IFunctionalResultListener<Object>() {
		@Override
		public void resultAvailable(Object result) {
		}
	};
	
	/**
	 * Constant with an no-op exception listener.
	 */
	private final static IFunctionalExceptionListener EMPTY_EXCEPTION_LISTENER = new IFunctionalExceptionListener() {
		
		@Override
		public void exceptionOccurred(Exception exception) {
		}
	};
	
	/**
	 * Returns a SuccessListener that ignores all results.
	 * 
	 * @return {@link IFunctionalResultListener}
	 */
    @SuppressWarnings("unchecked")
    public static final <E> IFunctionalResultListener<E> ignoreResults() {
        return (IFunctionalResultListener<E>) EMPTY_SUCCESS_LISTENER;
    }
    
    /**
	 * Returns an OnExceptionListener that ignores all results.
	 * 
	 * @return {@link IFunctionalExceptionListener}
	 */
    public static final IFunctionalExceptionListener ignoreExceptions() {
        return (IFunctionalExceptionListener) EMPTY_EXCEPTION_LISTENER;
    }
    
    /**
     * Creates an {@link IResultListener} that delegates results to the given SuccessListener
     * and uses default exception handling.
     * 
     * @param sucListener The SuccessListener.
     * @return {@link IResultListener}
     */
	public static <E> IResultListener<E> resultListener(final IFunctionalResultListener<E> sucListener) {
		return resultListener(sucListener, true);
	}
	
	/**
     * Creates an {@link IResultListener} that delegates results to the given SuccessListener.
     * 
     * @param sucListener The SuccessListener.
     * @param defaultExceptionHandling Specifies whether to use a default handling for exceptions or not.
     * @return {@link IResultListener}
     */
	public static <E> IResultListener<E> resultListener(final IFunctionalResultListener<E> sucListener, final boolean defaultExceptionHandling) {
		return new DefaultResultListener<E>() {

			@Override
			public void resultAvailable(E result) {
				sucListener.resultAvailable(result);
			}

			@Override
			public void exceptionOccurred(Exception exception) {
				if (defaultExceptionHandling) {
					super.exceptionOccurred(exception);
				}
			}
		};
	}
	
	/**
     * Creates an {@link IResultListener} that delegates results to the given SuccessListener
     * and Exceptions to the given ExceptionListener.
     * 
     * @param sucListener The SuccessListener.
     * @param exListener The ExceptionListener.
     * @return {@link IResultListener}
     */
	public static <E> IResultListener<E> resultListener(final IFunctionalResultListener<E> sucListener, final IFunctionalExceptionListener exListener) {
		return new IResultListener<E>() {

			@Override
			public void resultAvailable(E result) {
				sucListener.resultAvailable(result);
			}

			@Override
			public void exceptionOccurred(Exception exception) {
				exListener.exceptionOccurred(exception);
			}
		};
	}
	
	/**
     * Creates an {@link IResultListener} that delegates all results to a given Future.
     * 
     * @param delegate The future used for success delegation.
     * @return {@link IResultListener}
     */
	public static <E> IResultListener<E> delegate(final Future<E> delegate) {
		return delegate(delegate, false);
	}
	
	/**
     * Creates an {@link IResultListener} that delegates all results to a given Future.
     * 
     * @param delegate The future used for success delegation.
     * @param undone Flag if undone methods should be used.
     * @return {@link IResultListener}
     */
	public static <E> IResultListener<E> delegate(final Future<E> delegate, boolean undone) {
		return new DelegationResultListener<E>(delegate, undone);
	}
	
	/**
     * Creates an {@link IResultListener} that delegates successful results to a given Future
     * and exceptions to a given ExceptionListener.
     * 
     * @param delegate The future used for success delegation.
     * @param exListener The ExceptionListener.
     * @return {@link IResultListener}
     */
	public static <E> IResultListener<E> delegateSuccess(final Future<E> delegate, final IFunctionalExceptionListener exListener) {
		return delegateSuccess(delegate, false, exListener);
	}
	
	/**
     * Creates an {@link IResultListener} that delegates successful results to a given Future
     * and exceptions to a given ExceptionListener.
     * 
     * @param delegate The future used for success delegation.
     * @param undone Flag if undone methods should be used.
     * @param exListener The ExceptionListener.
     * @return {@link IResultListener}
     */
	public static <E> IResultListener<E> delegateSuccess(final Future<E> delegate, boolean undone, final IFunctionalExceptionListener exListener) {
		return new DelegationResultListener<E>(delegate, undone) {
			@Override
			public void exceptionOccurred(Exception exception) {
				exListener.exceptionOccurred(exception);
			}
		};
	}
    
	/**
     * Creates an {@link IResultListener} that delegates exceptions to a given Future
     * and results to a given SuccessListener.
     * 
     * @param delegate The future used for exception delegation.
     * @param sucListener The SuccessListener.
     * @return {@link IResultListener}
     */
    public static <E,T> IResultListener<E> delegateExceptions(final Future<T> delegate, final IFunctionalResultListener<E> sucListener) {
    	return delegateExceptions(delegate, false, sucListener);
    }
    
	/**
     * Creates an {@link IResultListener} that delegates exceptions to a given Future
     * and results to a given SuccessListener.
     * 
     * @param delegate The future used for exception delegation.
     * @param undone Flag if undone methods should be used.
     * @param sucListener The SuccessListener.
     * @return {@link IResultListener}
     */
    public static <E,T> IResultListener<E> delegateExceptions(final Future<T> delegate, boolean undone, final IFunctionalResultListener<E> sucListener) {
    	return new ExceptionDelegationResultListener<E, T>(delegate, undone) {
			@Override
			public void customResultAvailable(E result) {
				sucListener.resultAvailable(result);
			}
		};
    }
    
    /**
     * Creates an {@link CounterResultListener}.
     * 
     * @param num The number of sub callbacks.
     * @param countReachedListener Listener to be called when the given number is reached.
     * @return {@link CounterResultListener}
     */
    public static <E> CounterResultListener<E> countResults(int num, IFunctionalResultListener<Void> countReachedListener) {
    	return countResults(num, countReachedListener, true);
    }
    
    /**
     * Creates an {@link CounterResultListener}.
     * 
     * @param num The number of sub callbacks.
     * @param defaultExceptionHandling Whether to use default exception handling.
     * @param countReachedListener Listener to be called when the given number is reached.
     * @return {@link CounterResultListener}
     */
    public static <E> CounterResultListener<E> countResults(int num, IFunctionalResultListener<Void> countReachedListener, boolean defaultExceptionHandling) {
    	return new CounterResultListener<E>(num, resultListener(countReachedListener, defaultExceptionHandling));
    }
    
    /**
     * Creates an {@link CounterResultListener}.
     * 
     * @param num The number of sub callbacks.
     * @param countReachedListener Listener to be called when the given number is reached.
     * @param exListener Listener to be called for exceptions.
     * @return {@link CounterResultListener}
     */
    public static <E> CounterResultListener<E> countResults(int num, IFunctionalResultListener<Void> countReachedListener, IFunctionalExceptionListener exListener) {
    	return new CounterResultListener<E>(num, resultListener(countReachedListener, exListener));
    }

    /**
     * Creates an {@link CounterResultListener}.
     * 
     * @param num The number of sub callbacks.
     * @param countReachedListener Listener to be called when the given number is reached.
     * @param intermediateListener Listener to be called for intermediate Results.
     * @param exListener Listener to be called for exceptions.
     * @return {@link CounterResultListener}
     */
    public static <E> CounterResultListener<E> countResults(int num, IFunctionalResultListener<Void> countReachedListener, final IOnIntermediateResultListener<E> intermediateListener, IFunctionalExceptionListener exListener) {
    	return new CounterResultListener<E>(num, resultListener(countReachedListener, exListener)) {

			@Override
			public void intermediateResultAvailable(E result) {
				intermediateListener.intermediateResultAvailable(result);
			}
    	};
    }
    
    public static <E, F> ITuple2ResultListener<E, F> tuple2Result(final IFunctionalResultListener<E> firstSuccessListener, final IFunctionalResultListener<F> secondSuccessListener) {
    	return tuple2Result(firstSuccessListener, secondSuccessListener, true);
	}
    
    public static <E, F> ITuple2ResultListener<E, F> tuple2Result(final IFunctionalResultListener<E> firstSuccessListener, final IFunctionalResultListener<F> secondSuccessListener, final boolean defaultExceptionHandling) {
    	return tuple2Result(firstSuccessListener, secondSuccessListener, new IFunctionalExceptionListener() {
			@Override
			public void exceptionOccurred(Exception exception) {
				if (defaultExceptionHandling) {
					new DefaultResultListener<E>() {
						@Override
						public void resultAvailable(E result) {
						}
					}.exceptionOccurred(exception);
				}
			}
		});
    }
    
    public static <E, F> ITuple2ResultListener<E, F> tuple2Result(final IFunctionalResultListener<E> firstSuccessListener, final IFunctionalResultListener<F> secondSuccessListener, final IFunctionalExceptionListener exListener) {
    	return new DefaultTuple2ResultListener<E, F>() {

			@Override
			public void firstResultAvailable(E result) {
				firstSuccessListener.resultAvailable(result);
			}

			@Override
			public void secondResultAvailable(F result) {
				secondSuccessListener.resultAvailable(result);
			}

			@Override
			public void exceptionOccurred(Exception exception) {
				exListener.exceptionOccurred(exception);
			}
		};
    }
    
    public static <E> IIntermediateResultListener<E> intermediate(final IFunctionalResultListener<E> intermediateListener) {
		return intermediate(intermediateListener, null, true);
    }
    
    public static <E> IIntermediateResultListener<E> intermediate(final IFunctionalResultListener<E> intermediateListener, final IFunctionalResultListener<Void> finishedListener) {
		return intermediate(intermediateListener, finishedListener, true);
    }
    
    public static <E> IIntermediateResultListener<E> intermediate(final IFunctionalResultListener<E> intermediateListener, final IFunctionalResultListener<Void> finishedListener, final boolean defaultExceptionHandling) {
		return new IntermediateDefaultResultListener<E>() {

			@Override
			public void intermediateResultAvailable(E result) {
				intermediateListener.resultAvailable(result);
			}

			@Override
			public void finished() {
				finishedListener.resultAvailable(null);
			}
			
			@Override
			public void exceptionOccurred(Exception exception) {
				if (defaultExceptionHandling) {
					super.exceptionOccurred(exception);
				}
			}
		};
    }
    
    public static <E> IIntermediateResultListener<E> intermediate(final IFunctionalResultListener<E> intermediateListener, final IFunctionalResultListener<Void> finishedListener, final IFunctionalExceptionListener exListener) {
		return new IntermediateDefaultResultListener<E>() {

			@Override
			public void intermediateResultAvailable(E result) {
				intermediateListener.resultAvailable(result);
			}

			@Override
			public void finished() {
				if (finishedListener != null) {
					finishedListener.resultAvailable(null);
				}
			}
			
			@Override
			public void exceptionOccurred(Exception exception) {
				exListener.exceptionOccurred(exception);
			}
		};
    }
    
 
}

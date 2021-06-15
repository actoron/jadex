package jadex.micro.examples.quiz;

/**
 *  Event with quiz result.
 */
public class ResultEvent extends QuizEvent
{
	/** The result string. */
	protected QuizResults results;

	/**
	 *  Create a new result event.
	 */
	public ResultEvent()
	{
	}
	
	/**
	 *  Create a new result event.
	 */
	public ResultEvent(QuizResults results)
	{
		this.results = results;
	}

	/**
	 * @return the results
	 */
	public QuizResults getResults()
	{
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(QuizResults results)
	{
		this.results = results;
	}
}

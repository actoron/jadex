package jadex.micro.examples.quiz;

import java.util.ArrayList;
import java.util.List;

/**
 *  Result representation.
 */
public class QuizResults
{
	/** The result list. */
	protected List<Result> results;
	
	/**
	 *  Create a new results object.
	 */
	public QuizResults()
	{
		this.results = new ArrayList<Result>();
	}
	
	/**
	 *  Get the results.
	 *  @return The results:
	 */
	public List<Result> getResults()
	{
		return results;
	}

	/**
	 *  Set the results.
	 *  @param results The results to set.
	 */
	public void setResults(List<Result> results)
	{
		this.results = results;
	}

	/**
	 *  Add a result.
	 *  @param no The number.
	 *  @param correct Is result ok.
	 */
	public void addResult(int no, boolean correct)
	{
		// replace old result
		for(Result result: results)
		{
			if(result.getNo()==no)
			{
				results.remove(result);
				break;
			}
		}
		results.add(new Result(no, correct));
	}
	
	/**
	 *  Get the size.
	 *  @return The size.
	 */
	public int size()
	{
		return results.size();
	}
	
	/**
	 *  Get the number of correct results.
	 *  @return The number of correct results.
	 */
	public int countCorrect()
	{
		return (int)results.stream().filter(x -> x.isCorrect()).count();
	}
	
	/**
	 *  Get the string representation.
	 */
	@Override
	public String toString()
	{
		return countCorrect()+" / "+size()+" "+((int)((double)countCorrect()/size()*100)+"%");
	}
	
	/**
	 *  Struct for a single result.
	 */
	public static class Result
	{
		protected int no;
		protected boolean correct;
		
		/**
		 *  Create a new result.
		 */
		public Result()
		{
		}
		
		/**
		 *  Create a new result.
		 */
		public Result(int no, boolean correct)
		{
			this.no = no;
			this.correct = correct;
		}
		/**
		 * @return the no
		 */
		public int getNo()
		{
			return no;
		}
		/**
		 * @param no the no to set
		 */
		public void setNo(int no)
		{
			this.no = no;
		}
		/**
		 * @return the correct
		 */
		public boolean isCorrect()
		{
			return correct;
		}
		/**
		 * @param correct the correct to set
		 */
		public void setCorrect(boolean correct)
		{
			this.correct = correct;
		}
	}
}

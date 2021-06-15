package jadex.micro.examples.quiz;

import java.util.List;

/**
 *  The question data class.
 */
public class Question
{
	/** The question. */
	protected String question;

	/** The answers. */
	protected List<String> answers;
	
	/** The solution. */
	protected int solution;
	
	/**
	 *  Create a new question.
	 */
	public Question()
	{
	}

	/**
	 *  Create a new question event.
	 */
	public Question(String question, List<String> answers, int solution)
	{
		this.question = question;
		this.answers = answers;
		this.solution = solution;
	}

	/**
	 *  Get the question.
	 *  @return The question.
	 */
	public String getQuestion()
	{
		return question;
	}

	/**
	 *  Set the question.
	 *  @param question The question to set.
	 */
	public void setQuestion(String question)
	{
		this.question = question;
	}

	/**
	 *  Get the answers.
	 *  @return The answers.
	 */
	public List<String> getAnswers()
	{
		return answers;
	}

	/** 
	 *  Set the answers.
	 *  @param answers The answers to set
	 */
	public void setAnswers(List<String> answers)
	{
		this.answers = answers;
	}

	/**
	 *  Get the solution.
	 *  @return The solution
	 */
	public int getSolution()
	{
		return solution;
	}

	/**
	 *  Set the solution.
	 *  @param solution The solution to set
	 */
	public void setSolution(int solution)
	{
		this.solution = solution;
	}
}

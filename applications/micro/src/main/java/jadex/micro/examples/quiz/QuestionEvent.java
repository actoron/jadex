package jadex.micro.examples.quiz;

/**
 *  A question event.
 */
public class QuestionEvent extends QuizEvent
{
	/** The question. */
	protected Question question;
	
	/** The question number. */
	protected int cnt;

	/**
	 *  Create a new question event.
	 */
	public QuestionEvent()
	{
	}
	
	/**
	 *  Create a new question event.
	 */
	public QuestionEvent(Question question, int cnt)
	{
		this.question = question;
		this.cnt = cnt;
	}

	/**
	 * @return the question
	 */
	public Question getQuestion()
	{
		return question;
	}

	/**
	 * @param question the question to set
	 */
	public void setQuestion(Question question)
	{
		this.question = question;
	}

	/**
	 * @return the no
	 */
	public int getCount()
	{
		return cnt;
	}

	/**
	 * @param no the no to set
	 */
	public void setCount(int cnt)
	{
		this.cnt = cnt;
	}
	
	
}

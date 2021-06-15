package jadex.micro.examples.quiz;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface for a quiz service.
 */
@Service
public interface IQuizService
{
	/**
	 *  Method to participate in the quiz.
	 *  @return The subscription for receiving quiz events.
	 */
	public ISubscriptionIntermediateFuture<QuizEvent> participate();

	/**
	 *  Send an answer.
	 *  @param answer The answer.
	 */
	public IFuture<Void> sendAnswer(int answer, int questioncnt);
}

package jadex.bdiv3.tutorial.b5;

import jadex.bridge.service.annotation.ParameterInfo;
import jadex.commons.future.IFuture;

/**
 *  Translation service interface.
 */
public interface ITranslationService
{
	/**
	 *  Translate an English word to German.
	 *  @param eword The english word.
	 *  @return The german translation.
	 */
	public IFuture<String> translateEnglishGerman(@ParameterInfo("eword") String eword);
}

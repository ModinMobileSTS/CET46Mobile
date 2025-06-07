package cET46Initializer.actions;

import cET46Initializer.helpers.BookConfig;
import cET46Initializer.helpers.BookConfig.LexiconEnum;
import cET46Initializer.relics.QuizRelic;

public class GeneralQuizAction extends QuizAction {

    public GeneralQuizAction(QuizRelic quizRelic, BookConfig bookConfig, LexiconEnum usingLexicon) {
        super(
                quizRelic,
                usingLexicon
        );
    }

}

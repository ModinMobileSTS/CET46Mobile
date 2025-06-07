package cET46Initializer.actions;

import cET46Initializer.CET46Initializer;
import cET46Initializer.actions.QuizAction.QuizData;
import cET46Initializer.helpers.BookConfig.LexiconEnum;
import cET46Initializer.relics.BuildQuizDataRequest;
import cET46Initializer.relics.QuizRelic;
import cET46Initializer.screens.QuizScreen;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.android.mods.BaseMod;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CorrectAction extends AbstractGameAction {
    private static final Logger logger = LogManager.getLogger(CorrectAction.class.getName());
    private static final String LEXICON;
    private final int target_id;
    private final LexiconEnum lexicon;
    private final QuizRelic quizRelic;
    public CorrectAction(String target, QuizRelic quizRelic) {
        String[] tmp = target.split("_");
        this.target_id = Integer.parseInt(tmp[1]);
        String key = tmp[0].substring(CET46Initializer.JSON_MOD_KEY.length());
        this.lexicon = LexiconEnum.valueOf(key);
        this.actionType = ActionType.CARD_MANIPULATION;
        this.duration = Settings.ACTION_DUR_FASTER;
        this.quizRelic = quizRelic;
    }

    @Override
    public void update() {
        if (this.duration == Settings.ACTION_DUR_FASTER) {
            QuizData quizData = quizRelic.buildQuizData(BuildQuizDataRequest.Factory.fromTargetId(lexicon, target_id));
            logger.info("quizData = {}", quizData);
            BaseMod.openCustomScreen(AbstractDungeon.CurrentScreen.WORD_SCREEN, quizData.getShow(), LEXICON,
                    quizData.getCorrectOptions(), quizData.getAllOptions(), quizData.getWordUiStringsId(), true);
            tickDuration();
            return;
        }
        tickDuration();
    }

    static {
        LEXICON = CardCrawlGame.languagePack.getUIString("CET46:WordScreen").TEXT[4];
    }

}

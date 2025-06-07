package cET46Initializer.relics;

import cET46Initializer.CET46Initializer;
import cET46Initializer.helpers.BookConfig;
import cET46Initializer.helpers.BookConfig.LexiconEnum;
import com.badlogic.gdx.math.MathUtils;
import lombok.*;

/**
 * 构造QuizData所需的变量
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuildQuizDataRequest {
    LexiconEnum usingLexicon;
    int vocabularySize;
    int targetId;
    String targetUiStringsId;
    String uiStringsIdStart;
    protected int maxOptionNum;


    public static class Factory {
        public static BuildQuizDataRequest fromRandom(LexiconEnum lexicon) {
            return fromTargetId(lexicon, getRandomWord(lexicon));
        }

        private static int getRandomWord(LexiconEnum lexicon) {
            int size = BookConfig.VOCABULARY_MAP.get(lexicon);
            return MathUtils.random(0, size - 1);
        }

        public static BuildQuizDataRequest fromTargetId(LexiconEnum lexicon, int targetId) {
            BuildQuizDataRequest config = BuildQuizDataRequest.builder()
                    .usingLexicon(lexicon)
                    .vocabularySize(BookConfig.VOCABULARY_MAP.get(lexicon))
                    .maxOptionNum(9)
                    .build();
            config.setUiStringsIdStart(CET46Initializer.JSON_MOD_KEY + lexicon.name() + "_");
            config.setTargetId(targetId);
            config.setTargetUiStringsId(config.getUiStringsIdStart() + config.getTargetId());
            return config;
        }
    }


}

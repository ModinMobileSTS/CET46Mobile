package cET46Initializer.patches;

import cET46Initializer.relics.QuizRelic;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;


/**
 * 在打出卡牌之前触发小测, 并进行数据修正
 */
public class AbstractPlayerPatch {
    public static AbstractPlayer p = null;
    public static AbstractMonster m = null;
    public static AbstractCard c = null;
    public static int energy = 0;

    /**
     * 这个部分在玩家须接受测验时会终止打出卡牌, 卡牌只有在玩家接受测验后才能打出
     */

    public static class ChangeCardDataPrePlay {

    }

}

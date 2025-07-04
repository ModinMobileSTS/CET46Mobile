package CET46InSpire.ui;

import CET46InSpire.helpers.ImageElements;
import CET46InSpire.screens.QuizScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.android.mods.BaseMod;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class CheckButton extends UIButton {
    public static final float IMG_W;
    public static final float IMG_H;
    private static final Color HOVER_BLEND_COLOR;

    public CheckButton(float pos_x, float pos_y) {
        super(pos_x, pos_y, IMG_W, IMG_H);
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(ImageElements.INFO_BUTTON, this.current_x, this.current_y, IMG_W, IMG_H);
        if (this.hb.hovered && !this.hb.clickStarted) {
            sb.setBlendFunction(770, 1);
            sb.setColor(HOVER_BLEND_COLOR);
            sb.draw(ImageElements.INFO_BUTTON, this.current_x, this.current_y, IMG_W, IMG_H);
            sb.setBlendFunction(770, 771);
        }
        super.render(sb);
    }

    @Override
    public void buttonClicked() {
        this.hide();
        ((QuizScreen)BaseMod.getCustomScreen(AbstractDungeon.CurrentScreen.WORD_SCREEN)).checkAns();
    }

    static {
        IMG_W = 400.0F * Settings.xScale;
        IMG_H = 60.0F * Settings.yScale;
        HOVER_BLEND_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.4F);
    }
}

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.megacrit.cardcrawl.neow;

import CET46InSpire.events.CallOfCETRoom;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.blights.GrotesqueTrophy;
import com.megacrit.cardcrawl.blights.MimicInfestation;
import com.megacrit.cardcrawl.blights.Muzzle;
import com.megacrit.cardcrawl.blights.Shield;
import com.megacrit.cardcrawl.blights.Spear;
import com.megacrit.cardcrawl.blights.TimeMaze;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.characters.AnimatedNpc;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.helpers.SaveHelper;
import com.megacrit.cardcrawl.helpers.TipTracker;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile.SaveType;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.FastCardObtainEffect;
import com.megacrit.cardcrawl.vfx.InfiniteSpeechBubble;
import com.megacrit.cardcrawl.vfx.scene.LevelTransitionTextOverlayEffect;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NeowEvent extends AbstractEvent {
    private static final Logger logger = LogManager.getLogger(NeowEvent.class.getName());
    private static final CharacterStrings characterStrings;
    public static final String[] NAMES;
    public static final String[] TEXT;
    public static final String[] OPTIONS;
    private AnimatedNpc npc;
    public static final String NAME;
    private int screenNum;
    private int bossCount;
    private boolean setPhaseToEvent;
    private ArrayList<NeowReward> rewards;
    public static Random rng;
    private boolean pickCard;
    public static boolean waitingToSave;
    private static final float DIALOG_X;
    private static final float DIALOG_Y;

    public NeowEvent(boolean isDone) {
        this.screenNum = 2;
        this.setPhaseToEvent = false;
        this.rewards = new ArrayList();
        this.pickCard = false;
        waitingToSave = false;
        if (this.npc == null) {
            this.npc = new AnimatedNpc(1534.0F * Settings.xScale, AbstractDungeon.floorY - 60.0F * Settings.yScale, "images/npcs/neow/skeleton.atlas", "images/npcs/neow/skeleton.json", "idle");
        }

        this.roomEventText.clear();
        this.playSfx();
        if (!Settings.isEndless || AbstractDungeon.floorNum <= 1) {
            if (!Settings.isStandardRun() && (!Settings.isEndless || AbstractDungeon.floorNum > 1)) {
                if (Settings.seedSet) {
                    this.bossCount = 1;
                } else {
                    this.bossCount = 0;
                }
            } else {
                this.bossCount = CardCrawlGame.playerPref.getInteger(AbstractDungeon.player.chosenClass.name() + "_SPIRITS", 0);
                AbstractDungeon.bossCount = this.bossCount;
            }
        }

        this.body = "";
        if (Settings.isEndless && AbstractDungeon.floorNum > 1) {
            this.talk(TEXT[MathUtils.random(12, 14)]);
            this.screenNum = 999;
            this.roomEventText.addDialogOption(OPTIONS[0]);
        } else if (this.shouldSkipNeowDialog()) {
            this.screenNum = 10;
            this.talk(TEXT[10]);
            this.roomEventText.addDialogOption(OPTIONS[1]);
        } else if (!isDone) {
            if (!(Boolean)TipTracker.tips.get("NEOW_INTRO")) {
                this.screenNum = 0;
                TipTracker.neverShowAgain("NEOW_INTRO");
                this.talk(TEXT[0]);
                this.roomEventText.addDialogOption(OPTIONS[1]);
            } else {
                this.screenNum = 1;
                this.talk(TEXT[MathUtils.random(1, 3)]);
                this.roomEventText.addDialogOption(OPTIONS[1]);
            }

            AbstractDungeon.topLevelEffects.add(new LevelTransitionTextOverlayEffect(AbstractDungeon.name, AbstractDungeon.levelNum, true));
        } else {
            this.screenNum = 99;
            this.talk(TEXT[8]);
            this.roomEventText.addDialogOption(OPTIONS[3]);
        }

        this.hasDialog = true;
        this.hasFocus = true;
    }

    public NeowEvent() {
        this(false);
    }

    private boolean shouldSkipNeowDialog() {
        if (Settings.seedSet && !Settings.isTrial && !Settings.isDailyRun) {
            return false;
        } else {
            return !Settings.isStandardRun();
        }
    }

    public void update() {
        super.update();
        if (this.pickCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            CardGroup group = new CardGroup(CardGroupType.UNSPECIFIED);

            for(AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                group.addToBottom(c.makeCopy());
            }

            AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, TEXT[11]);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }

        for(NeowReward r : this.rewards) {
            r.update();
        }

        if (!this.setPhaseToEvent) {
            AbstractDungeon.getCurrRoom().phase = RoomPhase.EVENT;
            this.setPhaseToEvent = true;
        }

        if (!RoomEventDialog.waitForInput) {
            this.buttonEffect(this.roomEventText.getSelectedOption());
        }

        if (waitingToSave && !AbstractDungeon.isScreenUp && AbstractDungeon.topLevelEffects.isEmpty() && AbstractDungeon.player.relicsDoneAnimating()) {
            boolean doneAnims = true;

            for(AbstractRelic r : AbstractDungeon.player.relics) {
                if (!r.isDone) {
                    doneAnims = false;
                    break;
                }
            }

            if (doneAnims) {
                waitingToSave = false;
                SaveHelper.saveIfAppropriate(SaveType.POST_NEOW);
            }
        }

    }

    private void talk(String msg) {
        AbstractDungeon.effectList.add(new InfiniteSpeechBubble(DIALOG_X, DIALOG_Y, msg));
    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screenNum) {
            case 0:
                this.dismissBubble();
                this.talk(TEXT[4]);
                if (this.bossCount == 0 && !Settings.isTestingNeow) {
                    this.miniBlessing();
                } else {
                    this.blessing();
                }
                break;
            case 1:
                this.dismissBubble();
                logger.info(this.bossCount);
                if (this.bossCount == 0 && !Settings.isTestingNeow) {
                    this.miniBlessing();
                } else {
                    this.blessing();
                }
                break;
            case 2:
                if (buttonPressed == 0) {
                    this.blessing();
                } else {
                    this.openMap();
                }
                break;
            case 3:
                this.dismissBubble();
                this.roomEventText.clearRemainingOptions();
                switch (buttonPressed) {
                    case 0:
                        ((NeowReward)this.rewards.get(0)).activate();
                        this.talk(TEXT[8]);
                        break;
                    case 1:
                        ((NeowReward)this.rewards.get(1)).activate();
                        this.talk(TEXT[8]);
                        break;
                    case 2:
                        ((NeowReward)this.rewards.get(2)).activate();
                        this.talk(TEXT[9]);
                        break;
                    case 3:
                        ((NeowReward)this.rewards.get(3)).activate();
                        this.talk(TEXT[9]);
                }

                this.screenNum = 99;
                this.roomEventText.updateDialogOption(0, OPTIONS[3]);
                this.roomEventText.clearRemainingOptions();
                waitingToSave = true;
                break;
            case 10:
                this.dailyBlessing();
                this.roomEventText.clearRemainingOptions();
                this.roomEventText.updateDialogOption(0, OPTIONS[3]);
                this.screenNum = 99;
                break;
            case 999:
                this.endlessBlight();
                this.roomEventText.clearRemainingOptions();
                this.roomEventText.updateDialogOption(0, OPTIONS[3]);
                this.screenNum = 99;
                break;
            default:
                if (AbstractDungeon.floorNum == 0) {
                    (AbstractDungeon.getCurrRoom()).phase = AbstractRoom.RoomPhase.COMPLETE;
                    logger.info("To call of CET room");
                    RoomEventDialog.optionList.clear();
                    MapRoomNode node = new MapRoomNode(0, -1);
                    node.room = new CallOfCETRoom();
                    AbstractDungeon.player.releaseCard();
                    AbstractDungeon.overlayMenu.hideCombatPanels();
                    AbstractDungeon.previousScreen = null;
                    AbstractDungeon.dynamicBanner.hide();
                    AbstractDungeon.dungeonMapScreen.closeInstantly();
                    AbstractDungeon.closeCurrentScreen();
                    AbstractDungeon.topPanel.unhoverHitboxes();
                    AbstractDungeon.fadeIn();
                    AbstractDungeon.effectList.clear();
                    AbstractDungeon.topLevelEffects.clear();
                    AbstractDungeon.topLevelEffectsQueue.clear();
                    AbstractDungeon.effectsQueue.clear();
                    AbstractDungeon.dungeonMapScreen.dismissable = true;
                    AbstractDungeon.nextRoom = node;
                    AbstractDungeon.setCurrMapNode(node);
                    AbstractDungeon.getCurrRoom().onPlayerEntry();
                    AbstractDungeon.rs = (node.room.event instanceof com.megacrit.cardcrawl.events.AbstractImageEvent) ? AbstractDungeon.RenderScene.EVENT : AbstractDungeon.RenderScene.NORMAL;
                    AbstractDungeon.nextRoom = null;

                    return;
                }
                this.openMap();
        }

    }

    private void endlessBlight() {
        if (AbstractDungeon.player.hasBlight("DeadlyEnemies")) {
            AbstractBlight tmp = AbstractDungeon.player.getBlight("DeadlyEnemies");
            tmp.incrementUp();
            tmp.flash();
        } else {
            AbstractDungeon.getCurrRoom().spawnBlightAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, new Spear());
        }

        if (AbstractDungeon.player.hasBlight("ToughEnemies")) {
            AbstractBlight tmp = AbstractDungeon.player.getBlight("ToughEnemies");
            tmp.incrementUp();
            tmp.flash();
        } else {
            AbstractDungeon.getCurrRoom().spawnBlightAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, new Shield());
        }

        this.uniqueBlight();
    }

    private void uniqueBlight() {
        AbstractBlight temp = AbstractDungeon.player.getBlight("MimicInfestation");
        if (temp != null) {
            temp = AbstractDungeon.player.getBlight("TimeMaze");
            if (temp != null) {
                temp = AbstractDungeon.player.getBlight("FullBelly");
                if (temp != null) {
                    temp = AbstractDungeon.player.getBlight("GrotesqueTrophy");
                    if (temp != null) {
                        AbstractDungeon.player.getBlight("GrotesqueTrophy").stack();
                    } else {
                        AbstractDungeon.getCurrRoom().spawnBlightAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, new GrotesqueTrophy());
                    }
                } else {
                    AbstractDungeon.getCurrRoom().spawnBlightAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, new Muzzle());
                }
            } else {
                AbstractDungeon.getCurrRoom().spawnBlightAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, new TimeMaze());
            }

        } else {
            AbstractDungeon.getCurrRoom().spawnBlightAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, new MimicInfestation());
        }
    }

    private void dailyBlessing() {
        rng = new Random(Settings.seed);
        this.dismissBubble();
        this.talk(TEXT[8]);
        if (ModHelper.isModEnabled("Heirloom")) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, AbstractDungeon.returnRandomRelic(RelicTier.RARE));
        }

        boolean addedCards = false;
        CardGroup group = new CardGroup(CardGroupType.UNSPECIFIED);
        if (ModHelper.isModEnabled("Allstar")) {
            addedCards = true;

            for(int i = 0; i < 5; ++i) {
                AbstractCard colorlessCard = AbstractDungeon.getColorlessCardFromPool(AbstractDungeon.rollRareOrUncommon(0.5F));
                UnlockTracker.markCardAsSeen(colorlessCard.cardID);
                group.addToBottom(colorlessCard.makeCopy());
            }
        }

        if (ModHelper.isModEnabled("Specialized")) {
            if (!ModHelper.isModEnabled("SealedDeck") && !ModHelper.isModEnabled("Draft")) {
                addedCards = true;
                AbstractCard rareCard = AbstractDungeon.returnTrulyRandomCard();
                UnlockTracker.markCardAsSeen(rareCard.cardID);
                group.addToBottom(rareCard.makeCopy());
                group.addToBottom(rareCard.makeCopy());
                group.addToBottom(rareCard.makeCopy());
                group.addToBottom(rareCard.makeCopy());
                group.addToBottom(rareCard.makeCopy());
            } else {
                AbstractCard rareCard = AbstractDungeon.returnTrulyRandomCard();

                for(int i = 0; i < 5; ++i) {
                    AbstractCard tmpCard = rareCard.makeCopy();
                    AbstractDungeon.topLevelEffectsQueue.add(new FastCardObtainEffect(tmpCard, MathUtils.random((float)Settings.WIDTH * 0.2F, (float)Settings.WIDTH * 0.8F), MathUtils.random((float)Settings.HEIGHT * 0.3F, (float)Settings.HEIGHT * 0.7F)));
                }
            }
        }

        if (addedCards) {
            AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, TEXT[11]);
        }

        if (ModHelper.isModEnabled("Draft")) {
            AbstractDungeon.cardRewardScreen.draftOpen();
        }

        this.pickCard = true;
        if (ModHelper.isModEnabled("SealedDeck")) {
            CardGroup sealedGroup = new CardGroup(CardGroupType.UNSPECIFIED);

            for(int i = 0; i < 30; ++i) {
                AbstractCard card = AbstractDungeon.getCard(AbstractDungeon.rollRarity());
                if (!sealedGroup.contains(card)) {
                    sealedGroup.addToBottom(card.makeCopy());
                } else {
                    --i;
                }
            }

            for(AbstractCard c : sealedGroup.group) {
                UnlockTracker.markCardAsSeen(c.cardID);
            }

            AbstractDungeon.gridSelectScreen.open(sealedGroup, 10, OPTIONS[4], false);
        }

        this.roomEventText.clearRemainingOptions();
        this.screenNum = 99;
    }

    private void miniBlessing() {
        AbstractDungeon.bossCount = 0;
        this.dismissBubble();
        this.talk(TEXT[MathUtils.random(4, 6)]);
        this.rewards.add(new NeowReward(true));
        this.rewards.add(new NeowReward(false));
        this.roomEventText.clearRemainingOptions();
        this.roomEventText.updateDialogOption(0, ((NeowReward)this.rewards.get(0)).optionLabel);
        this.roomEventText.addDialogOption(((NeowReward)this.rewards.get(1)).optionLabel);
        this.screenNum = 3;
    }

    private void blessing() {
        logger.info("BLESSING");
        rng = new Random(Settings.seed);
        logger.info("COUNTER: " + rng.counter);
        AbstractDungeon.bossCount = 0;
        this.dismissBubble();
        this.talk(TEXT[7]);
        this.rewards.add(new NeowReward(0));
        this.rewards.add(new NeowReward(1));
        this.rewards.add(new NeowReward(2));
        this.rewards.add(new NeowReward(3));
        this.roomEventText.clearRemainingOptions();
        this.roomEventText.updateDialogOption(0, ((NeowReward)this.rewards.get(0)).optionLabel);
        this.roomEventText.addDialogOption(((NeowReward)this.rewards.get(1)).optionLabel);
        this.roomEventText.addDialogOption(((NeowReward)this.rewards.get(2)).optionLabel);
        this.roomEventText.addDialogOption(((NeowReward)this.rewards.get(3)).optionLabel);
        this.screenNum = 3;
    }

    private void dismissBubble() {
        for(AbstractGameEffect e : AbstractDungeon.effectList) {
            if (e instanceof InfiniteSpeechBubble) {
                ((InfiniteSpeechBubble)e).dismiss();
            }
        }

    }

    private void playSfx() {
        int roll = MathUtils.random(3);
        if (roll == 0) {
            CardCrawlGame.sound.play("VO_NEOW_1A");
        } else if (roll == 1) {
            CardCrawlGame.sound.play("VO_NEOW_1B");
        } else if (roll == 2) {
            CardCrawlGame.sound.play("VO_NEOW_2A");
        } else {
            CardCrawlGame.sound.play("VO_NEOW_2B");
        }

    }

    public void logMetric(String actionTaken) {
        AbstractEvent.logMetric(NAME, actionTaken);
    }

    public void render(SpriteBatch sb) {
        this.npc.render(sb);
    }

    public void dispose() {
        super.dispose();
        if (this.npc != null) {
            logger.info("Disposing Neow asset.");
            this.npc.dispose();
            this.npc = null;
        }

    }

    static {
        characterStrings = CardCrawlGame.languagePack.getCharacterString("Neow Event");
        NAMES = characterStrings.NAMES;
        TEXT = characterStrings.TEXT;
        OPTIONS = characterStrings.OPTIONS;
        NAME = NAMES[0];
        rng = null;
        waitingToSave = false;
        DIALOG_X = 1100.0F * Settings.xScale;
        DIALOG_Y = AbstractDungeon.floorY + 60.0F * Settings.yScale;
    }
}

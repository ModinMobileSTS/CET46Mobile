package cET46Initializer;

import cET46Initializer.events.CallOfCETEvent.BookEnum;
import cET46Initializer.helpers.BookConfig;
import cET46Initializer.helpers.BookConfig.LexiconEnum;
import cET46Initializer.helpers.ImageElements;
import cET46Initializer.relics.TestCET;
import cET46Initializer.relics.TestJLPT;
import cET46Initializer.screens.QuizScreen;
import cET46Initializer.ui.ModConfigPanel;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import com.megacrit.cardcrawl.android.mods.BaseMod;
import com.megacrit.cardcrawl.android.mods.DevConsole;
import com.megacrit.cardcrawl.android.mods.ModPanel;
import com.megacrit.cardcrawl.android.mods.interfaces.EditRelicsSubscriber;
import com.megacrit.cardcrawl.android.mods.interfaces.EditStringsSubscriber;
import com.megacrit.cardcrawl.android.mods.interfaces.PostInitializeSubscriber;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.megacrit.cardcrawl.android.mods.BaseMod.addCustomScreen;


public class CET46Initializer implements
        EditRelicsSubscriber,
        EditStringsSubscriber,
        PostInitializeSubscriber {
    private static final Logger logger = LogManager.getLogger(CET46Initializer.class.getName());
    public static String MOD_ID = "CET46Initializer";  //MOD_ID必须与ModTheSpire.json中的一致
    public static String JSON_MOD_KEY = "CET46:";
    private ModConfigPanel settingsPanel = null;

    /**
     * 所有可选范围
     */
    public static Map<BookEnum, BookConfig> allBooks = new HashMap<>();
    /**
     * 用户已选择范围
     */
    public static Set<BookConfig> userBooks = new HashSet<>();
    /**
     * 需要加载的范围, 指词库范围
     */
    public static Set<LexiconEnum> needLoadBooks = new HashSet<LexiconEnum>();
    static {
        // test
        allBooks.put(BookEnum.JLPT, new BookConfig(BookEnum.JLPT,
                Arrays.asList(LexiconEnum.N1, LexiconEnum.N2, LexiconEnum.N3, LexiconEnum.N4, LexiconEnum.N5), () -> new TestJLPT()));
        allBooks.put(BookEnum.CET, new BookConfig(BookEnum.CET, Arrays.asList(LexiconEnum.CET4, LexiconEnum.CET6), () -> new TestCET()));}
    private static void initBooks() {
        CET46Initializer.allBooks.values().forEach(bookConfig -> {
            if (bookConfig.needNotLoad()) {
                return;
            }
            userBooks.add(bookConfig);
//            needLoadBooks.add(bookConfig.bookEnum);
            needLoadBooks.addAll(bookConfig.lexicons);
        });
        // test
//        ModConfigPanel.addRelicPage(BookEnum.CET4, Arrays.asList(LexiconEnum.CET4, LexiconEnum.CET6));
        ModConfigPanel.addRelicPage(BookEnum.CET, Arrays.asList(LexiconEnum.CET4, LexiconEnum.CET6));
        ModConfigPanel.addRelicPage(BookEnum.JLPT, Arrays.asList(LexiconEnum.N1, LexiconEnum.N2, LexiconEnum.N3, LexiconEnum.N4, LexiconEnum.N5));

        logger.info("initBooks: userBooks = {}, needLoadBooks = {}.", userBooks.stream().map(it -> it.bookEnum).collect(Collectors.toList()), needLoadBooks);
    }

    public CET46Initializer() {
        logger.info("Initialize: {}", MOD_ID);
        BaseMod.subscribe(this);
//        settingsPanel = new CET46Panel("config");
        initBooks();
        // 放在init books 后面来保证注册遗物设置页面成功
        settingsPanel = new ModConfigPanel();
    }

    public static void initialize() {
        new CET46Initializer();

    }

    @Override
    public void receiveEditRelics() {
        CET46Initializer.userBooks.forEach(bookConfig -> {
            AbstractRelic relic = bookConfig.relicSupplier.get();
            BaseMod.addRelic(relic);
            UnlockTracker.markRelicAsSeen(relic.relicId);
        });
    }

    @Override
    public void receiveEditStrings() {
        String lang = "eng";
        if (Objects.requireNonNull(Settings.language) == Settings.GameLanguage.ZHS) {
            lang = "zhs";
        }
        if (Objects.requireNonNull(Settings.language) == Settings.GameLanguage.ZHT) {
            lang = "zhs";
        }

        BaseMod.loadCustomStringsFile(MOD_ID,EventStrings.class, "CET46Resource/localization/events_" + lang + ".json");
        BaseMod.loadCustomStringsFile(MOD_ID,PowerStrings.class, "CET46Resource/localization/powers_" + lang + ".json");
        BaseMod.loadCustomStringsFile(MOD_ID,RelicStrings.class, "CET46Resource/localization/relics_" + lang + ".json");
        BaseMod.loadCustomStringsFile(MOD_ID,UIStrings.class, "CET46Resource/localization/ui_" + lang + ".json");

        loadVocabulary();
    }

    public void loadVocabulary() {
        long startTime = System.currentTimeMillis();

        needLoadBooks.forEach(lexiconEnum -> {
            BaseMod.loadCustomStringsFile(MOD_ID,UIStrings.class, "CET46Resource/vocabulary/" + lexiconEnum.name() + ".json");
        });
        logger.info("Vocabulary load time: {}ms", System.currentTimeMillis() - startTime);
    }

    @Override
    public void receivePostInitialize() {

        BookConfig.init_map();
        ((ModConfigPanel) settingsPanel).receivePostInitialize();
        BaseMod.registerModBadge(MOD_ID,ImageElements.MOD_BADGE,
                "CET46 In Spire", "__name__, Dim", "Do_not_forget_CET46!", settingsPanel);

       addCustomScreen(new QuizScreen());

        // 注册命令
        // 修改 BaseMod 控制台字体
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("CET46Resource/font/VictorMono-Medium.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(22.0F * Settings.scale);
        DevConsole.consoleFont = generator.generateFont(parameter);
        generator.dispose();
    }

}

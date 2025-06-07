//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package CET46InSpire.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;

import com.megacrit.cardcrawl.android.mods.BaseMod;
import com.megacrit.cardcrawl.android.mods.ModPanel;
import com.megacrit.cardcrawl.android.mods.Pair;
import com.megacrit.cardcrawl.android.mods.interfaces.IUIElement;
import com.megacrit.cardcrawl.android.mods.ui.ModLabel;
import com.megacrit.cardcrawl.android.mods.ui.ModLabeledButton;
import com.megacrit.cardcrawl.android.mods.ui.ModLabeledToggleButton;
import com.megacrit.cardcrawl.android.mods.ui.ModMinMaxSlider;
import com.megacrit.cardcrawl.android.mods.utils.SpireConfig;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen.CurScreen;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EasyConfigPanel extends ModPanel {
    private static final float ELEMENT_X = 355.0F;
    private static final float MIN_SLIDER_X = 805.0F;
    private static final float PAGE_START_Y = 730.0F;
    private static final float PAGE_MIN_Y = 140.0F;
    private final String modID;
    private final UIStrings uiStrings;
    private SpireConfig config;
    private final List<ConfigField> configFields;
    private final Map<String, List<Pair<IUIElement, Float>>> customElements;
    private final Map<String, Map<String, Object>> buildParams;
    private float elementPadding;
    private List<List<IUIElement>> pages;
    private static final Map<Class<?>, BiFunction<SpireConfig, Field, ConfigField>> configFieldBuilders = new HashMap();

    public EasyConfigPanel(String modID, String localizationID) {
        this(modID, CardCrawlGame.languagePack.getUIString(localizationID), "config");
    }

    public EasyConfigPanel(String modID, UIStrings text, String configName) {
        super(EasyConfigPanel::buildInterface);
        this.customElements = new HashMap();
        this.buildParams = new HashMap();
        this.elementPadding = 8.0F;
        this.pages = new ArrayList();
        this.modID = modID;
        if (text == null) {
            text = new UIStrings();
        }

        this.uiStrings = text;
        if (this.uiStrings.TEXT_DICT == null) {
            this.uiStrings.TEXT_DICT = Collections.emptyMap();
        }

        Properties configDefaults = new Properties();
        this.configFields = new ArrayList();

        try {
            List<Field> validFields = new ArrayList();

            for(Field f : this.getClass().getDeclaredFields()) {
                int modifiers = f.getModifiers();
                if (Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isTransient(modifiers)) {
                    configDefaults.put(f.getName(), String.valueOf(f.get((Object)null)));
                    validFields.add(f);
                }
            }

            this.config = new SpireConfig(modID, configName, configDefaults);

            for(Field f : validFields) {
                this.configFields.add(makeConfigField(this.config, f));
            }

        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException("Failed to set up SpireConfig for " + modID, e);
        }
    }

    public void addUIElement(String fieldName, IUIElement element, float elementHeight) {
        this.customElements.compute(fieldName, (k, v) -> {
            if (v == null) {
                v = new ArrayList();
            }

            v.add(new Pair(element, elementHeight));
            return v;
        });
    }

    public void setNumberRange(String fieldName, float minValue, float maxValue) {
        this.buildParams.compute(fieldName, (k, v) -> {
            if (v == null) {
                v = new HashMap();
            }

            v.put("MIN", minValue);
            v.put("MAX", maxValue);
            return v;
        });
    }

    public void setupTextField(String fieldName, float width, int charLimit) {
        this.setupTextField(fieldName, width, charLimit, (Predicate)null, (Predicate)null);
    }

    public void setupTextField(String fieldName, float width, int charLimit, Predicate<Character> charFilter, Predicate<String> resultValidation) {
        this.buildParams.compute(fieldName, (k, v) -> {
            if (v == null) {
                v = new HashMap();
            }

            v.put("WIDTH", width);
            v.put("CHARACTERS", charLimit);
            v.put("FILTER", charFilter);
            v.put("VALIDATION", resultValidation);
            return v;
        });
    }

    public void setPadding(float padding) {
        this.elementPadding = padding;
    }

    private static void buildInterface(ModPanel modPanel) {
        if (modPanel instanceof EasyConfigPanel) {
            try {
                ((EasyConfigPanel)modPanel).buildInterface0();
            } catch (Exception e) {
                throw new RuntimeException("Failed to build EasyConfigPanel for " + ((EasyConfigPanel)modPanel).modID, e);
            }
        }
    }

    private void buildInterface0() throws IllegalAccessException {
        List<IUIElement> page = new ArrayList();
        this.pages.add(page);
        float pagePos = 730.0F;

        for(ConfigField field : this.configFields) {
            List<Pair<IUIElement, Float>> fieldElement = (List)this.customElements.get(field.getName());
            if (fieldElement == null) {
                fieldElement = field.buildElements(this, (String)this.uiStrings.TEXT_DICT.get(field.getName()), (Map)this.buildParams.get(field.getName()));
            }

            float totalHeight = 0.0F;

            for(Pair<IUIElement, Float> element : fieldElement) {
                if ((Float)element.getValue() > 0.0F) {
                    totalHeight += (Float)element.getValue();
                    totalHeight += this.elementPadding;
                }
            }

            if (pagePos - totalHeight < 140.0F) {
                page = new ArrayList();
                this.pages.add(page);
                pagePos = 730.0F;
            }

            for(Pair<IUIElement, Float> element : fieldElement) {
                ((IUIElement)element.getKey()).setY(pagePos);
                page.add(element.getKey());
                if ((Float)element.getValue() > 0.0F) {
                    pagePos -= (Float)element.getValue() + this.elementPadding;
                }
            }
        }

        if (this.pages.size() > 1) {
            int pageNum = 0;

            for(List<IUIElement> finishedPage : this.pages) {
                int finalPageNum1 = pageNum;
                finishedPage.add(new ModLabeledButton(">", 1365.0F, 710.0F, Settings.CREAM_COLOR, Color.WHITE, FontHelper.cardEnergyFont_L, this, (button) -> this.setPage(finalPageNum1 + 1)));
                int finalPageNum = pageNum;
                finishedPage.add(new ModLabeledButton("<", 1265.0F, 710.0F, Settings.CREAM_COLOR, Color.WHITE, FontHelper.cardEnergyFont_L, this, (button) -> this.setPage(finalPageNum - 1)));
                ++pageNum;
            }
        }

        this.setPage(0);
    }

    public void setPage(int pageIndex) {
        if (!this.pages.isEmpty()) {
            if (pageIndex < 0) {
                pageIndex = this.pages.size() - 1;
            }

            pageIndex %= this.pages.size();
            this.getUpdateElements().clear();
            this.getRenderElements().clear();

            for(IUIElement element : this.pages.get(pageIndex)) {
                this.addUIElement(element);
            }

        }
    }

    public void save() {
        try {
            for(ConfigField field : this.configFields) {
                field.readValue();
            }

            this.config.save();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to read field", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config", e);
        }
    }

    public void update() {
        int size = this.getUpdateElements().size();

        for(int i = 0; i < size; ++i) {
            IUIElement next = (IUIElement)this.getUpdateElements().get(i);
            next.update();
            if (this.getUpdateElements().size() != size || !((IUIElement)this.getUpdateElements().get(i)).equals(next)) {
                break;
            }
        }

        if (InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            BaseMod.modSettingsUp = false;
        }

        if (!BaseMod.modSettingsUp) {
            this.waitingOnEvent = false;
            Gdx.input.setInputProcessor(this.oldInputProcessor);
            CardCrawlGame.mainMenuScreen.lighten();
            CardCrawlGame.mainMenuScreen.screen = CurScreen.MAIN_MENU;
            CardCrawlGame.cancelButton.hideInstantly();
            this.isUp = false;
        }

    }

    private static ConfigField makeConfigField(SpireConfig config, Field f) {
        BiFunction<SpireConfig, Field, ConfigField> builder = (BiFunction)configFieldBuilders.get(f.getType());
        if (builder == null) {
            throw new RuntimeException("Type " + f.getName() + " is not supported by EasyConfigPanel. If you require this type, registerFieldType can be used.");
        } else {
            return (ConfigField)builder.apply(config, f);
        }
    }

    public static void registerFieldType(Class<?> cls, BiFunction<SpireConfig, Field, ConfigField> builder) {
        if (configFieldBuilders.containsKey(cls)) {
            BaseMod.logger.warn("Duplicate ConfigField builder registered for EasyConfigPanel for class " + cls.getName());
        }

        configFieldBuilders.put(cls, builder);
    }

    private static void buildSlider(List<Pair<IUIElement, Float>> elements, ModPanel panel, String displayName, String format, float min, float max, float pos, Consumer<Float> setVal) {
        elements.add(new Pair(new AutoOffsetLabel(displayName, 355.0F, 0.0F, Settings.CREAM_COLOR, FontHelper.charDescFont, panel, (label) -> {
        }), 0.0F));
        float textWidth = FontHelper.getWidth(FontHelper.charDescFont, displayName, 1.0F / Settings.scale);
        elements.add(new Pair(new AutoOffsetSlider("", Math.max(805.0F, 455.0F + textWidth), 0.0F, min, max, pos, format, panel, (slider) -> setVal.accept(slider.getValue())), 50.0F));
    }

    static {
        registerFieldType(String.class, (config, field) -> new ConfigField(config, field, (s) -> field.set((Object)null, s), (configField, elements, params, panel, displayName) -> {
                elements.add(new Pair(new AutoOffsetLabel(displayName, 355.0F, 0.0F, Settings.CREAM_COLOR, FontHelper.charDescFont, panel, (label) -> {
                }), 0.0F));
                float textWidth = FontHelper.getWidth(FontHelper.charDescFont, displayName, 1.0F / Settings.scale);
                float width = (Float)params.getOrDefault("WIDTH", 300.0F);
                int charLimit = (Integer)params.getOrDefault("CHARACTERS", 50);
                Predicate<Character> filter = (Predicate)params.getOrDefault("FILTER", (Object)null);
                Predicate<String> validator = (Predicate)params.getOrDefault("VALIDATION", (Object)null);
            }));
        registerFieldType(Byte.TYPE, (config, field) -> new ConfigField(config, field, (s) -> field.set((Object)null, Byte.parseByte(s)), (configField, elements, params, panel, displayName) -> {
                float min = (Float)params.getOrDefault("MIN", 0.0F);
                float max = (Float)params.getOrDefault("MAX", 100.0F);
                if (max < min) {
                    float temp = min;
                    min = max;
                    max = temp;
                }

                buildSlider(elements, panel, displayName, "%.0f", min, max, (float)(Byte)field.get((Object)null), (fVal) -> configField.setValue(fVal.byteValue(), true));
            }));
        registerFieldType(Short.TYPE, (config, field) -> new ConfigField(config, field, (s) -> field.set((Object)null, Short.parseShort(s)), (configField, elements, params, panel, displayName) -> {
                float min = (Float)params.getOrDefault("MIN", 0.0F);
                float max = (Float)params.getOrDefault("MAX", 100.0F);
                if (max < min) {
                    float temp = min;
                    min = max;
                    max = temp;
                }

                buildSlider(elements, panel, displayName, "%.0f", min, max, (float)(Short)field.get((Object)null), (fVal) -> configField.setValue(fVal.shortValue(), true));
            }));
        registerFieldType(Integer.TYPE, (config, field) -> new ConfigField(config, field, (s) -> field.set((Object)null, Integer.parseInt(s)), (configField, elements, params, panel, displayName) -> {
                float min = (Float)params.getOrDefault("MIN", 0.0F);
                float max = (Float)params.getOrDefault("MAX", 100.0F);
                if (max < min) {
                    float temp = min;
                    min = max;
                    max = temp;
                }

                buildSlider(elements, panel, displayName, "%.0f", min, max, (float)(Integer)field.get((Object)null), (fVal) -> configField.setValue(MathUtils.round(fVal), true));
            }));
        registerFieldType(Long.TYPE, (config, field) -> new ConfigField(config, field, (s) -> field.set((Object)null, Long.parseLong(s)), (configField, elements, params, panel, displayName) -> {
                float min = (Float)params.getOrDefault("MIN", 0.0F);
                float max = (Float)params.getOrDefault("MAX", 100.0F);
                if (max < min) {
                    float temp = min;
                    min = max;
                    max = temp;
                }

                buildSlider(elements, panel, displayName, "%.0f", min, max, (float)(Long)field.get((Object)null), (fVal) -> configField.setValue(fVal.longValue(), true));
            }));
        registerFieldType(Float.TYPE, (config, field) -> new ConfigField(config, field, (s) -> field.set((Object)null, Float.parseFloat(s)), (configField, elements, params, panel, displayName) -> {
                float min = (Float)params.getOrDefault("MIN", 0.0F);
                float max = (Float)params.getOrDefault("MAX", 100.0F);
                if (max < min) {
                    float temp = min;
                    min = max;
                    max = temp;
                }

                buildSlider(elements, panel, displayName, "%.2f", min, max, (Float)field.get((Object)null), (fVal) -> configField.setValue(fVal, true));
            }));
        registerFieldType(Double.TYPE, (config, field) -> new ConfigField(config, field, (s) -> field.set((Object)null, Double.parseDouble(s)), (configField, elements, params, panel, displayName) -> {
                float min = (Float)params.getOrDefault("MIN", 0.0F);
                float max = (Float)params.getOrDefault("MAX", 100.0F);
                if (max < min) {
                    float temp = min;
                    min = max;
                    max = temp;
                }

                buildSlider(elements, panel, displayName, "%.3f", min, max, ((Double)field.get((Object)null)).floatValue(), (fVal) -> configField.setValue(fVal.doubleValue(), true));
            }));
        registerFieldType(Boolean.TYPE, (config, field) -> new ConfigField(config, field, (s) -> field.set((Object)null, Boolean.parseBoolean(s)), (configField, elements, params, panel, displayName) -> elements.add(new Pair(new ModLabeledToggleButton(displayName, 355.0F, 0.0F, Settings.CREAM_COLOR, FontHelper.charDescFont, (Boolean)field.get((Object)null), panel, (label) -> {
                }, (button) -> configField.setValue(button.enabled, true)), 50.0F))));
    }

    public static class ConfigField {
        private final SpireConfig config;
        private final Field field;
        private final FieldSetter fieldSetter;
        private final UIBuilder uiBuilder;

        public ConfigField(SpireConfig config, Field field, FieldSetter setter, UIBuilder uiBuilder) {
            this.config = config;
            this.field = field;
            this.fieldSetter = setter;
            this.uiBuilder = uiBuilder;
            this.loadValue();
        }

        void loadValue() {
            this.setValue(this.config.getString(this.field.getName()), false);
        }

        void readValue() throws IllegalAccessException {
            this.config.setString(this.field.getName(), this.field.get((Object)null).toString());
        }

        void setValue(Object val, boolean save) {
            try {
                this.fieldSetter.set(val.toString());
                this.readValue();
                if (save) {
                    this.config.save();
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set value of config field " + this.field.getName(), e);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save config", e);
            }
        }

        public String getName() {
            return this.field.getName();
        }

        public String toString() {
            return this.field.toString();
        }

        private List<Pair<IUIElement, Float>> buildElements(ModPanel panel, String displayName, Map<String, Object> buildParams) throws IllegalAccessException {
            if (displayName == null) {
                displayName = this.getName();
            }

            if (buildParams == null) {
                buildParams = Collections.emptyMap();
            }

            List<Pair<IUIElement, Float>> elements = new ArrayList();
            this.uiBuilder.build(this, elements, buildParams, panel, displayName);
            return elements;
        }

        public interface FieldSetter {
            void set(String var1) throws IllegalAccessException;
        }

        public interface UIBuilder {
            void build(ConfigField var1, List<Pair<IUIElement, Float>> var2, Map<String, Object> var3, ModPanel var4, String var5) throws IllegalAccessException;
        }
    }

    private static class AutoOffsetLabel extends ModLabel {
        public AutoOffsetLabel(String labelText, float xPos, float yPos, ModPanel p, Consumer<ModLabel> updateFunc) {
            super(labelText, xPos + 40.0F, yPos + 8.0F, p, updateFunc);
        }

        public AutoOffsetLabel(String labelText, float xPos, float yPos, Color color, ModPanel p, Consumer<ModLabel> updateFunc) {
            super(labelText, xPos + 40.0F, yPos + 8.0F, color, p, updateFunc);
        }

        public AutoOffsetLabel(String labelText, float xPos, float yPos, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
            super(labelText, xPos + 40.0F, yPos + 8.0F, font, p, updateFunc);
        }

        public AutoOffsetLabel(String labelText, float xPos, float yPos, Color color, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
            super(labelText, xPos + 40.0F, yPos + 8.0F, color, font, p, updateFunc);
        }

        public void setX(float xPos) {
            super.setX(xPos + 40.0F);
        }

        public void setY(float yPos) {
            super.setY(yPos + 8.0F);
        }
    }

    private static class AutoOffsetSlider extends ModMinMaxSlider {
        public AutoOffsetSlider(String lbl, float posX, float posY, float min, float max, float val, String format, ModPanel p, Consumer<ModMinMaxSlider> changeAction) {
            super(lbl, posX, posY + 15.0F, min, max, val, format, p, changeAction);
        }

        public void setY(float yPos) {
            super.setY(yPos + 15.0F);
        }
    }
}

package cET46Initializer.savedata;

import cET46Initializer.relics.QuizRelic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

public class SaveData {
    public static Logger logger = LogManager.getLogger("CET46SaveData");
    public static final String KEY_ITEMS = "ITEMS";
    public static final String KEY_REMOVED_ITEMS = "REMOVED_ITEMS";
    public static HashMap<String, Integer> items = null;
    public static ArrayList<String> removedItems = null;


}

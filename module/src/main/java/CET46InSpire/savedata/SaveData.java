package CET46InSpire.savedata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveData {
    public static Logger logger = LogManager.getLogger("CET46SaveData");
    public static final String KEY_ITEMS = "ITEMS";
    public static final String KEY_REMOVED_ITEMS = "REMOVED_ITEMS";
    public static HashMap<String, Integer> items = null;
    public static ArrayList<String> removedItems = null;


}

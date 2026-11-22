package de.multiapplications.komposter;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class GameStore {

    private static final String PREFS = "komposter";
    private static final String CONFIG = "komposter_config";
    private static final String USED_WORDS = "komposter_used_words";

    public static final class Config {
        public final List<String> players = new ArrayList<>();
        public boolean showHints = true;
        public final List<String> selectedCategories = new ArrayList<>();
    }

    private GameStore() {
    }

    public static Config loadConfig(Context context) {
        Config config = new Config();
        config.players.addAll(Arrays.asList("Spieler 1", "Spieler 2", "Spieler 3"));
        config.selectedCategories.addAll(Arrays.asList("easy_words", "animals", "food", "daily_life", "sports"));

        String raw = preferences(context).getString(CONFIG, null);
        if (raw == null) {
            return config;
        }

        try {
            JSONObject json = new JSONObject(raw);
            JSONArray savedPlayers = json.optJSONArray("players");
            if (savedPlayers != null && savedPlayers.length() >= 3) {
                config.players.clear();
                for (int i = 0; i < savedPlayers.length(); i++) {
                    config.players.add(savedPlayers.optString(i));
                }
            }

            if (json.has("showHints")) {
                config.showHints = json.optBoolean("showHints", true);
            }

            JSONArray savedCategories = json.optJSONArray("selectedCategories");
            if (savedCategories != null && savedCategories.length() > 0) {
                config.selectedCategories.clear();
                for (int i = 0; i < savedCategories.length(); i++) {
                    config.selectedCategories.add(savedCategories.optString(i));
                }
            }
        } catch (JSONException ignored) {
        }

        return config;
    }

    public static void saveConfig(Context context, List<String> players, boolean showHints, Iterable<String> selectedCategories) {
        try {
            JSONObject json = new JSONObject();
            JSONArray savedPlayers = new JSONArray();
            for (String player : players) {
                savedPlayers.put(player);
            }
            JSONArray savedCategories = new JSONArray();
            for (String category : selectedCategories) {
                savedCategories.put(category);
            }
            json.put("players", savedPlayers);
            json.put("showHints", showHints);
            json.put("selectedCategories", savedCategories);
            preferences(context).edit().putString(CONFIG, json.toString()).apply();
        } catch (JSONException ignored) {
        }
    }

    public static List<String> loadUsedWords(Context context) {
        List<String> words = new ArrayList<>();
        String raw = preferences(context).getString(USED_WORDS, null);
        if (raw == null) {
            return words;
        }
        try {
            JSONArray array = new JSONArray(raw);
            for (int i = 0; i < array.length(); i++) {
                words.add(array.optString(i));
            }
        } catch (JSONException ignored) {
        }
        return words;
    }

    public static void saveUsedWords(Context context, List<String> words) {
        JSONArray array = new JSONArray();
        for (String word : words) {
            array.put(word);
        }
        preferences(context).edit().putString(USED_WORDS, array.toString()).apply();
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
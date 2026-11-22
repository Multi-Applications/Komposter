package de.multiapplications.komposter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameContent {

    public static final class Category {
        public final String id;
        public final String name;

        public Category(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static final class Word {
        public final String category;
        public final String word;
        public final String hint;

        public Word(String category, String word, String hint) {
            this.category = category;
            this.word = word;
            this.hint = hint;
        }
    }

    public static final List<Category> CATEGORIES;
    public static final List<Word> WORDS;

    static {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("easy_words", "Einfache Wörter"));
        categories.add(new Category("animals", "Tiere & Natur"));
        categories.add(new Category("food", "Essen & Trinken"));
        categories.add(new Category("daily_life", "Alltag & Zuhause"));
        categories.add(new Category("sports", "Sport & Freizeit"));
        categories.add(new Category("fairy_tales", "Märchen & Geschichten"));
        categories.add(new Category("professions", "Berufe"));
        categories.add(new Category("school", "Schule & Lernen"));
        categories.add(new Category("space", "Weltraum & Wissenschaft"));
        categories.add(new Category("celebrations", "Feiern & Geburtstage"));
        categories.add(new Category("winter", "Winter & Schnee"));
        CATEGORIES = Collections.unmodifiableList(categories);

        List<Word> words = new ArrayList<>();
        words.add(new Word("easy_words", "Apfel", "Vesper"));
        words.add(new Word("easy_words", "Auto", "Fortbewegung"));
        words.add(new Word("easy_words", "Ball", "Freizeit"));
        words.add(new Word("easy_words", "Baum", "Natur"));
        words.add(new Word("easy_words", "Bett", "Liegen"));
        words.add(new Word("easy_words", "Blume", "Natur"));
        words.add(new Word("easy_words", "Brot", "Ofen"));
        words.add(new Word("easy_words", "Buch", "Schule"));
        words.add(new Word("easy_words", "Bus", "Ticket"));
        words.add(new Word("easy_words", "Fahrrad", "Gleichgewicht"));
        words.add(new Word("easy_words", "Fisch", "Wasser"));
        words.add(new Word("easy_words", "Haus", "Schutz"));
        words.add(new Word("easy_words", "Hund", "Leine"));
        words.add(new Word("easy_words", "Katze", "Pfote"));
        words.add(new Word("easy_words", "Kuchen", "Geburtstag"));
        words.add(new Word("easy_words", "Lampe", "Sehen"));
        words.add(new Word("easy_words", "Milch", "Kuh"));
        words.add(new Word("easy_words", "Mond", "Nacht"));

        words.add(new Word("animals", "Eichhörnchen", "Nüsse"));
        words.add(new Word("animals", "Pinguin", "Eis"));
        words.add(new Word("animals", "Elefant", "Rüssel"));
        words.add(new Word("animals", "Delfin", "Meer"));
        words.add(new Word("animals", "Schmetterling", "Flügel"));
        words.add(new Word("animals", "Eule", "Nacht"));
        words.add(new Word("animals", "Giraffe", "Hals"));
        words.add(new Word("animals", "Igel", "Stacheln"));
        words.add(new Word("animals", "Koala", "Baum"));
        words.add(new Word("animals", "Löwe", "Mähne"));
        words.add(new Word("animals", "Panda", "Bambus"));
        words.add(new Word("animals", "Schildkröte", "Panzer"));

        words.add(new Word("food", "Pizza", "Käse"));
        words.add(new Word("food", "Pfannkuchen", "Pfanne"));
        words.add(new Word("food", "Eiscreme", "Kalt"));
        words.add(new Word("food", "Spaghetti", "Nudeln"));
        words.add(new Word("food", "Kakao", "Warm"));
        words.add(new Word("food", "Obstsalat", "Früchte"));
        words.add(new Word("food", "Waffeln", "Zucker"));
        words.add(new Word("food", "Erdbeere", "Rot"));
        words.add(new Word("food", "Brezel", "Salz"));
        words.add(new Word("food", "Burger", "Brötchen"));

        words.add(new Word("daily_life", "Zahnbürste", "Zähne"));
        words.add(new Word("daily_life", "Rucksack", "Rücken"));
        words.add(new Word("daily_life", "Kissen", "Weich"));
        words.add(new Word("daily_life", "Regenschirm", "Regen"));
        words.add(new Word("daily_life", "Schlüssel", "Tür"));
        words.add(new Word("daily_life", "Wecker", "Morgen"));
        words.add(new Word("daily_life", "Trinkflasche", "Wasser"));

        words.add(new Word("sports", "Fußball", "Tor"));
        words.add(new Word("sports", "Schwimmbad", "Wasser"));
        words.add(new Word("sports", "Trampolin", "Hüpfen"));
        words.add(new Word("sports", "Schaukel", "Schwingen"));
        words.add(new Word("sports", "Tischtennis", "Schläger"));
        words.add(new Word("sports", "Inliner", "Rollen"));
        words.add(new Word("sports", "Badminton", "Federball"));

        words.add(new Word("fairy_tales", "Prinzessin", "Krone"));
        words.add(new Word("fairy_tales", "Drache", "Feuer"));
        words.add(new Word("fairy_tales", "Einhorn", "Horn"));
        words.add(new Word("fairy_tales", "Pirat", "Schatz"));
        words.add(new Word("fairy_tales", "Zauberer", "Magie"));
        words.add(new Word("fairy_tales", "Ritter", "Rüstung"));

        words.add(new Word("professions", "Bäcker", "Brot"));
        words.add(new Word("professions", "Feuerwehrmann", "Wasser"));
        words.add(new Word("professions", "Arzt", "Krank"));
        words.add(new Word("professions", "Gärtner", "Pflanzen"));
        words.add(new Word("professions", "Pilot", "Flugzeug"));
        words.add(new Word("professions", "Lehrer", "Schule"));

        words.add(new Word("school", "Schultasche", "Bücher"));
        words.add(new Word("school", "Lineal", "Gerade"));
        words.add(new Word("school", "Pausenhof", "Spielen"));
        words.add(new Word("school", "Buntstifte", "Malen"));
        words.add(new Word("school", "Tafel", "Kreide"));

        words.add(new Word("space", "Rakete", "All"));
        words.add(new Word("space", "Astronaut", "Schweben"));
        words.add(new Word("space", "Sternschnuppe", "Wunsch"));
        words.add(new Word("space", "Teleskop", "Sterne"));

        words.add(new Word("celebrations", "Luftballon", "Schweben"));
        words.add(new Word("celebrations", "Geburtstagskuchen", "Kerzen"));
        words.add(new Word("celebrations", "Geschenk", "Schleife"));
        words.add(new Word("celebrations", "Konfetti", "Papier"));

        words.add(new Word("winter", "Schneemann", "Karotte"));
        words.add(new Word("winter", "Schlitten", "Berg"));
        words.add(new Word("winter", "Lebkuchen", "Zimt"));
        words.add(new Word("winter", "Tannenbaum", "Zimmer"));
        WORDS = Collections.unmodifiableList(words);
    }

    private GameContent() {
    }
}
package com.algorithmandblues.lightsout.gamesummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GamePerformanceConstants {
    private static final HashMap<Integer, List<String>> SCORE_PERFORMANCE_DESCRIPTION_MAP = new HashMap<Integer, List<String>>() {{
        put(0, new ArrayList<String>() {{
            add("try not to cheat next time?");
            add("copycat");
            add("was it really that difficult?");
            add("no points for cheating");
        }});

        put(1, new ArrayList<String>() {{
            add("okay I guess?");
            add("come on! you can do better...");
            add("maybe practice to improve?");
            add("too many moves");
        }});

        put(2, new ArrayList<String>() {{
            add("not too bad!");
            add("almost there!");
            add("nicely done!");
            add("not bad. not great");
        }});

        put(3, new ArrayList<String>() {{
            add("let's go!!");
            add("what a pro!");
            add("amazing!");
            add("heck yeah");
            add("excellent!");
        }});
    }};

    public static String getPerformanceRandomDescriptionFor(int score) {
        List<String> descriptionForScore = Objects.requireNonNull(SCORE_PERFORMANCE_DESCRIPTION_MAP.get(score));
        Random random = new Random();
        return descriptionForScore.get(random.nextInt(descriptionForScore.size()));
    }
}

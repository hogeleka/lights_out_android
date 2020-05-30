package com.algorithmandblues.lightsout;

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
            add("con artist");
            add("was it really that difficult?");
            add("no points for cheating");
        }});

        put(1, new ArrayList<String>() {{
            add("okay I guess?");
            add("come on! you can do better...");
            add("maybe practice to improve?");
        }});

        put(2, new ArrayList<String>() {{
            add("not bad!");
            add("pass!");
            add("nicely done!");
        }});

        put(3, new ArrayList<String>() {{
            add("let's go!!");
            add("bravo!!");
            add("what a pro!");
        }});
    }};

    static String getPerformanceRandomDescriptionFor(int score) {
        List<String> descriptionForScore = Objects.requireNonNull(SCORE_PERFORMANCE_DESCRIPTION_MAP.get(score));
        Random random = new Random();
        return descriptionForScore.get(random.nextInt(descriptionForScore.size()));
    }
}

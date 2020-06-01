package com.algorithmandblues.lightsout;
import java.util.HashMap;

public class SkillLevelConstants {
    public static final HashMap<Integer, String> SKILL_LEVEL_MAP = new HashMap<Integer, String>() {{
        put(1, "novice");
        put(2, "beginner");
        put(3, "amateur");
        put(4, "student");
        put(5, "engineer");
        put(6, "electrician");
        put(7, "expert");
        put(8, "professor");
        put(9, "genius");
        put(10, "legend");
    }};

    public static String getSkillLevelForLevel(int dimension) {
        return SKILL_LEVEL_MAP.get(dimension);
    }
}

package com.algorithmandblues.lightsout;
import java.util.HashMap;

public class SkillLevelConstants {
    public static final HashMap<Integer, String> SKILL_LEVEL_MAP = new HashMap<Integer, String>() {{
       put(2, "Student");
       put(3, "Engineer");
       put(4, "Electrician");
       put(5, "Expert");
       put(6, "Professor");
       put(7, "CEO");
       put(8, "Edison");
       put(9, "Tesla");
       put(10, "Legend");
    }};

    public static String getSkillLevelForLevel(int dimension) {
        return SKILL_LEVEL_MAP.get(dimension);
    }
}

package com.gmail.vovan762000.scriptengineshell;

import com.gmail.vovan762000.scriptengineshell.entity.Script;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ScriptTestData {

    private static int startSeq = 1;

    public static final Script SCRIPT_1 = new Script(startSeq++, "function m(){\n" +
            "return 'test';\n" +
            "}\n" +
            "m(); ", "FINISHED", "test");
    public static final Script SCRIPT_2 = new Script(startSeq++, "function m(){\n" +
            "return 'test';\n" +
            "}\n" +
            "m(); ", "FINISHED", "test");
    public static final Script SCRIPT_3 = new Script(startSeq++, "function m(){\n" +
            "return 'test';\n" +
            "}\n" +
            "m(); ", "FINISHED", "test");

    public static final Script SCRIPT_4 = new Script(startSeq++, "function m(){\n" +
            "return 'test';\n" +
            "}\n" +
            "m(); ", "FINISHED", "test");
    public static final List<Script> SCRIPTS = Arrays.asList(SCRIPT_1, SCRIPT_2, SCRIPT_3);

    public static void assertMatch(Script actual, Script expected) {
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    public static void assertMatch(Iterable<Script> actual, Iterable<Script> expected) {
        assertThat(actual).hasSameElementsAs(expected);
    }

    public static void assertMatch(Iterable<Script> actual, Script... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }
}

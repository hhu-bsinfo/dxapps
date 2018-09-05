package de.hhu.bsinfo.dxterm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalCommandStringTest {


    private static final String COMMAND_STRING = "foo";

    private static final int EXPECTED_INT = 1;

    private static final int EXPECTED_ARGUMENT_COUNT = 2;

    private static final String EXPECTED_STRING = "HelloWorld";

    private static final String ARG_BAR = "bar";

    private static final String ARG_BAZ = "baz";

    private static final TerminalCommandString COMMAND = new TerminalCommandString(
            String.format("%s %s=%d %s=%s", COMMAND_STRING, ARG_BAR, EXPECTED_INT, ARG_BAZ, EXPECTED_STRING)
    );

    @Test
    void convertNamedArgument() {

        int bar = COMMAND.getNamedArgument(ARG_BAR, Integer::valueOf, -1);

        assertEquals(EXPECTED_INT, bar);

        String baz  = COMMAND.getNamedArgument(ARG_BAZ);

        assertEquals(EXPECTED_STRING, baz);
    }

    @Test
    void getName() {

        assertEquals(COMMAND_STRING, COMMAND.getName());
    }

    @Test
    void getArgumentCount() {

        assertEquals(EXPECTED_ARGUMENT_COUNT, COMMAND.getArgc());
    }

}
package com.fasterxml.jackson.core.jsonptr;

import com.fasterxml.jackson.core.JsonPointer;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class FSMTest extends JsonPointer{

    @Test
    void testNormalSegmentParsing() {
        String input = "/segment/aaa/bbb";
        JsonPointer expected = JsonPointer.compile("/segment/aaa/bbb");
        assertEquals(expected, JsonPointer._parseTail(input));
    }

    @Test
    void testEscapedCharacterParsing() {
        String input = "/seg~ment";
        JsonPointer expected = JsonPointer.compile("/seg~ment");
        assertEquals(expected, JsonPointer._parseTail(input));
    }


    @Test
    void testEmptyInputString() {
        String input = "";
        assertThrows(StringIndexOutOfBoundsException.class, () -> JsonPointer._parseTail(input));
    }

    @Test
    void testSingleSegmentParsing() {
        String input = "/single";
        JsonPointer expected = JsonPointer.compile("/single");
        assertEquals(expected, JsonPointer._parseTail(input));
    }

    @Test
    void testNullInputString() {
        String input = null;
        assertThrows(NullPointerException.class,() -> JsonPointer._parseTail(input));
    }

    @Test
    void testEscapedCharacterAtEnd() {
        String input = "/segment~";
        JsonPointer expected = JsonPointer.compile("/segment~");
        assertEquals(expected, JsonPointer._parseTail(input));
    }


}


/*
    public static JsonPointer _parseTail(final String fullPath)
    {
        PointerParent parent = null;

        // first char is the contextual slash, skip
        int i = 1;
        final int end = fullPath.length();
        int startOffset = 0;

        while (i < end) {
        
            char c = fullPath.charAt(i);
            if (c == '/') { // common case, got a segment
                parent = new PointerParent(parent, startOffset,
                        fullPath.substring(startOffset + 1, i));
                startOffset = i;
                ++i;
                continue;
            }
            ++i;

            // quoting is different; offline this case
            if (c == '~' && i < end) { // possibly, quote
                // 04-Oct-2022, tatu: Let's decode escaped segment
                //   instead of recursive call
                StringBuilder sb = new StringBuilder(32);
                i = _extractEscapedSegment(fullPath, startOffset+1, i, sb);
                final String segment = sb.toString();
                if (i < 0) { // end!
                    return _buildPath(fullPath, startOffset, segment, parent);
                }
                parent = new PointerParent(parent, startOffset, segment);
                startOffset = i;
                ++i;
                continue;
            }
            // otherwise, loop on
        }
        // end of the road, no escapes
        return _buildPath(fullPath, startOffset, fullPath.substring(startOffset + 1), parent);
    }

    private static JsonPointer _buildPath(final String fullPath, int fullPathOffset,
            String segment, PointerParent parent) {
        JsonPointer curr = new JsonPointer(fullPath, fullPathOffset, segment, EMPTY);
        for (; parent != null; parent = parent.parent) {
            curr = new JsonPointer(fullPath, parent.fullPathOffset, parent.segment, curr);
        }
        return curr;
    }
 */

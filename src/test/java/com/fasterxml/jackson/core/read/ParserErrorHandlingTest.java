package com.fasterxml.jackson.core.read;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.*;

import static org.junit.jupiter.api.Assertions.*;

public class ParserErrorHandlingTest
    extends JUnit5TestBase
{
    private final JsonFactory JSON_F = newStreamFactory();

    @Test
    public void testInvalidKeywordsBytes() throws Exception {
        _testInvalidKeywords(MODE_INPUT_STREAM);
        _testInvalidKeywords(MODE_INPUT_STREAM_THROTTLED);
        _testInvalidKeywords(MODE_DATA_INPUT);
    }

    @Test
    public void testInvalidKeywordsChars() throws Exception {
        _testInvalidKeywords(MODE_READER);
    }

    // Tests for [core#105] ("eager number parsing misses errors")
    @Test
    public void testMangledIntsBytes() throws Exception {
        _testMangledNumbersInt(MODE_INPUT_STREAM);
        _testMangledNumbersInt(MODE_INPUT_STREAM_THROTTLED);
        _testMangledNumbersInt(MODE_DATA_INPUT);
    }

    @Test
    public void testMangledFloatsBytes() throws Exception {
        _testMangledNumbersFloat(MODE_INPUT_STREAM);
        _testMangledNumbersFloat(MODE_INPUT_STREAM_THROTTLED);

        // 02-Jun-2017, tatu: Fails as expected, unlike int one. Bit puzzling...
        _testMangledNumbersFloat(MODE_DATA_INPUT);
    }

    @Test
    public void testMangledNumbersChars() throws Exception {
        _testMangledNumbersInt(MODE_READER);
        _testMangledNumbersFloat(MODE_READER);
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private void _testInvalidKeywords(int mode) throws Exception
    {
        doTestInvalidKeyword1(mode, "nul");
        doTestInvalidKeyword1(mode, "Null");
        doTestInvalidKeyword1(mode, "nulla");
        doTestInvalidKeyword1(mode, "fal");
        doTestInvalidKeyword1(mode, "False");
        doTestInvalidKeyword1(mode, "fals0");
        doTestInvalidKeyword1(mode, "falsett0");
        doTestInvalidKeyword1(mode, "tr");
        doTestInvalidKeyword1(mode, "truE");
        doTestInvalidKeyword1(mode, "treu");
        doTestInvalidKeyword1(mode, "trueenough");
        doTestInvalidKeyword1(mode, "C");
    }

    private void doTestInvalidKeyword1(int mode, String value)
        throws IOException
    {
        String doc = "{ \"key1\" : "+value+" }";
        JsonParser p = createParser(JSON_F, mode, doc);
        assertToken(JsonToken.START_OBJECT, p.nextToken());
        // Note that depending on parser impl, we may
        // get the exception early or late...
        try {
            assertToken(JsonToken.FIELD_NAME, p.nextToken());
            p.nextToken();
            fail("Expected an exception for malformed value keyword");
        } catch (JsonParseException jex) {
            verifyException(jex, "Unrecognized token");
            verifyException(jex, value);
        } finally {
            p.close();
        }

        // Try as root-level value as well:
        doc = value + " "; // may need space after for DataInput
        p = createParser(JSON_F, mode, doc);
        try {
            p.nextToken();
            fail("Expected an exception for malformed value keyword");
        } catch (JsonParseException jex) {
            verifyException(jex, "Unrecognized token");
            verifyException(jex, value);
        } finally {
            p.close();
        }
    }

    private void _testMangledNumbersInt(int mode) throws Exception
    {
        JsonParser p = createParser(JSON_F, mode, "123true");
        try {
            JsonToken t = p.nextToken();
            fail("Should have gotten an exception; instead got token: "+t);
        } catch (JsonParseException e) {
            verifyException(e, "expected space");
        }
        p.close();
    }

    private void _testMangledNumbersFloat(int mode) throws Exception
    {
        // Also test with floats
        JsonParser p = createParser(JSON_F, mode, "1.5false");
        try {
            JsonToken t = p.nextToken();
            fail("Should have gotten an exception; instead got token: "+t);
        } catch (JsonParseException e) {
            verifyException(e, "expected space");
        }
        p.close();
    }
}

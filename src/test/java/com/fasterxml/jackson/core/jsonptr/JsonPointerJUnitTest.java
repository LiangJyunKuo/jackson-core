package com.fasterxml.jackson.core.jsonptr;


import com.fasterxml.jackson.core.JsonPointer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

public class JsonPointerJUnitTest {

    private JsonPointer jsonPointer;

    private static final String ERROR_PATH = "a/b/c";
    private static final String EMPTY_PATH = "";
    private static final String SINGLE_CHAR_PATH = "/a";
    private static final String SPECIAL_CHAR_PATH = "/user/!@#$%^&*()-_=+";

    @BeforeEach
    void setUp() {
        jsonPointer = JsonPointer.compile("");
    }

    //Test valid path
    @Test
    @DisplayName("Test for an empty path")
    void testEmptyPath() {
        JsonPointer jsonPointer = JsonPointer.compile(EMPTY_PATH);
        Assertions.assertNull(jsonPointer.getMatchingProperty());
        Assertions.assertNull(jsonPointer.head());
    }

    @Test
    @DisplayName("Test for error path")
    void testErrorPath() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            JsonPointer jsonPointer = JsonPointer.compile(ERROR_PATH);
        });

        Assertions.assertEquals("Invalid input: JSON Pointer expression must start with '/': \"a/b/c\"",
                exception.getMessage());
    }

    @Test
    @DisplayName("Test for a single-character path")
    void testSingleCharacterPath() {
        JsonPointer pointer = JsonPointer.compile(SINGLE_CHAR_PATH);
        Assertions.assertEquals("a", pointer.getMatchingProperty());
        Assertions.assertEquals("", pointer.head().toString());
    }



    @Test
    @DisplayName("Test for a path with special characters")
    void testPathWithSpecialChar() {
        JsonPointer pointer = JsonPointer.compile(SPECIAL_CHAR_PATH);
        Assertions.assertEquals("/!@#$%^&*()-_=+", pointer.tail().toString());
    }


    // Test JsonPointer functions:

    // Test cases for appendPath with various path inputs
    @Test
    void testAppendPathWithEmptyPath() {
        JsonPointer basePointer = JsonPointer.compile("/base");
        JsonPointer appendPointer = JsonPointer.compile("");

        JsonPointer result = basePointer.append(appendPointer);

        Assertions.assertEquals("/base", result.toString());
    }

    @Test
    void testAppendPathWithComplexPaths() {
        JsonPointer basePointer = JsonPointer.compile("/base");
        JsonPointer appendPointer = JsonPointer.compile("/path1/path2");

        JsonPointer result = basePointer.append(appendPointer);

        Assertions.assertEquals("/base/path1/path2", result.toString());
    }

    // Test cases for appendProperty with various property inputs
    @Test
    void testAppendPropertyWithEmptyProperty() {
        JsonPointer basePointer = JsonPointer.compile("/base");
        JsonPointer result = basePointer.appendProperty("");

        Assertions.assertNotEquals("/base", result );
        Assertions.assertEquals("/base" +"/", result.toString());
    }

    @Test
    void testAppendPropertyWithSpecialCharacters() {
        JsonPointer basePointer = JsonPointer.compile("/base");
        JsonPointer result = basePointer.appendProperty("!@#$%^&*()-_=+");

        Assertions.assertEquals("/base/!@#$%^&*()-_=+", result.toString());
    }

    // Test cases for appendIndex with various index inputs
    @Test
    void testAppendIndexWithNegativeIndex() {
        JsonPointer basePointer = JsonPointer.compile("/base");


        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            JsonPointer jsonPointer =basePointer.appendIndex(-1);
        });

        Assertions.assertEquals("Negative index cannot be appended",
                exception.getMessage());
    }

    @Test
    void testAppendIndexWithZeroIndex() {
        JsonPointer basePointer = JsonPointer.compile("/base");
        JsonPointer result = basePointer.appendIndex(0);

        Assertions.assertEquals("/base/0", result.toString());
    }

    @Test
    void testAppendIndexWithLargeIndex() {
        JsonPointer basePointer = JsonPointer.compile("/base");
        JsonPointer result = basePointer.appendIndex(999999);

        Assertions.assertEquals("/base/999999", result.toString());
    }


    @Test
    void testMatchProperty() {
        // Create a JsonPointer object with a segment that matches a property
        JsonPointer parentPointer = JsonPointer.compile("/");
        JsonPointer childPointer = JsonPointer.compile("/child/aaa");

        // Set up the relationship between parent and child pointers
        parentPointer.append(childPointer);

        // Test matching an invalid property name
        Assertions.assertNull(parentPointer.matchProperty("invalid"));

        // Test matching a valid property name
        Assertions.assertNull(parentPointer.matchProperty("child"));
    }

    @Test
    void testHashCode() {
        // Create two JsonPointer instances with the same content
        JsonPointer pointer1 = JsonPointer.compile("/test");
        JsonPointer pointer2 = JsonPointer.compile("/test");

        // Test that equal objects have the same hash code
        Assertions.assertEquals(pointer1.hashCode(), pointer2.hashCode());

        // Modify one of the JsonPointer instances
        JsonPointer pointer3 = JsonPointer.compile("/different");

        // Test that unequal objects have different hash codes
        Assertions.assertNotEquals(pointer1.hashCode(), pointer3.hashCode());
    }

    @Test
    void testMatchElement() {
        // Create a JsonPointer object with a matching element index
        JsonPointer parentPointer = JsonPointer.compile("/");
        JsonPointer childPointer = JsonPointer.compile("/child");

        // Set up the relationship between parent and child pointers
        parentPointer.append(childPointer);


        // Test matching an invalid element index
        Assertions.assertNull(parentPointer.matchElement(1));

        // Test matching with a negative index
        Assertions.assertNull(parentPointer.matchElement(-1));
    }

    @Test
    void testLast() {

        JsonPointer parentPointer = JsonPointer.compile("");
        Assertions.assertNull(parentPointer.last());
    }

}


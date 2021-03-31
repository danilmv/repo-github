package J3.lesson6;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class TestingTest {
    private Testing testing;

    @BeforeEach
    void setUp() {
        testing = new Testing();
    }

    public static Stream<Arguments> dataForGetLast4Followers() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[]{1, 2, 4, 4, 2, 3, 4, 1, 7}, new int[]{1, 7}));
        out.add(Arguments.arguments(new int[]{4, 4, 2}, new int[]{2}));
        out.add(Arguments.arguments(new int[]{4, 4, 4}, new int[]{}));

        return out.stream();
    }

    @ParameterizedTest
    @MethodSource("dataForGetLast4Followers")
    public void massTestGetLast4Followers(int[] array, int[] result) {
        Assertions.assertArrayEquals(result, testing.getLast4Followers(array));
    }

    @Test
    void testGetLast4FollowersException() {
        Assertions.assertThrows(RuntimeException.class, () -> testing.getLast4Followers(new int[]{1, 1, 1}));
    }

    public static Stream<Arguments> dataForCheckArrayHas1And4(){
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(new int[]{1, 2, 4, 4, 2, 3, 4, 1, 7}, true));
        out.add(Arguments.arguments(new int[]{4, 4, 2}, false));
        out.add(Arguments.arguments(new int[]{1}, false));
        out.add(Arguments.arguments(new int[]{4, 1}, true));
        out.add(Arguments.arguments(new int[]{}, false));

        return out.stream();
    }

    @ParameterizedTest
    @MethodSource("dataForCheckArrayHas1And4")
    public void testCheckArrayHas1And4(int[] array, boolean result) {
        Assertions.assertEquals(result, testing.checkArrayHas1And4(array));
    }
}
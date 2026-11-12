import java.util.List;
import java.util.Objects;

/**
 * Minimal, dependency-free assertions and pass/fail accounting for the lab.
 * This file is test infrastructure, not a student test.
 */
public final class TestSupport {

    private static int passed = 0;
    private static int failed = 0;

    private TestSupport() {
    }

    /** Runs one named test and records its result. */
    public static void check(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("PASS: " + name);
        } catch (Throwable failure) {
            failed++;
            System.out.println("FAIL: " + name);
            System.out.println("      " + failure);
        }
    }

    public static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertEquals(String message, Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /** Convenience overload for student tests that need no custom message. */
    public static void assertTrue(boolean condition) {
        assertTrue(condition, "expected true but was false");
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertNull(Object actual, String message) {
        if (actual != null) {
            throw new AssertionError(message + ": expected null but was <" + actual + ">");
        }
    }

    public static void assertNotNull(Object actual, String message) {
        if (actual == null) {
            throw new AssertionError(message + ": expected non-null value");
        }
    }

    public static void assertThrows(Class<? extends Throwable> expectedType, Runnable action) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }
            throw new AssertionError(
                    "expected " + expectedType.getSimpleName() + " but got " + thrown.getClass().getSimpleName());
        }
        throw new AssertionError("expected " + expectedType.getSimpleName() + " but nothing was thrown");
    }

    /** Convenience helper for building expected value lists. */
    @SafeVarargs
    public static <T> List<T> listOf(T... values) {
        return List.of(values);
    }

    public static int passed() {
        return passed;
    }

    public static int failed() {
        return failed;
    }

    public static void summary() {
        System.out.println("--------------------------------------------------");
        System.out.println("Results: " + passed + " passed, " + failed + " failed");
    }
}

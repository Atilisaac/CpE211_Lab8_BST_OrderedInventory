import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Dependency-free entry point for the lab tests.
 *
 * <p>The student-authored test lives in {@code StudentAuthoredTest.run()} and
 * is discovered at runtime, so this runner compiles before that file exists.
 * Acceptance tests are added to {@link #runAcceptanceTests()} during STEP 2.</p>
 */
public final class LabTestRunner {

    private LabTestRunner() {
    }

    public static void main(String[] args) {
        System.out.println("CpE 211 Lab 1 - BST Ordered Inventory");
        System.out.println("Test runner (no external dependencies)");
        System.out.println("--------------------------------------------------");

        runStudentAuthoredTest();
        runAcceptanceTests();

        TestSupport.summary();
        if (TestSupport.failed() > 0) {
            System.exit(1);
        }
    }

    private static void runStudentAuthoredTest() {
        try {
            Class<?> testClass = Class.forName("StudentAuthoredTest");
            Method run = testClass.getMethod("run");
            run.invoke(null);
        } catch (ClassNotFoundException notAuthoredYet) {
            System.out.println("SKIP: StudentAuthoredTest (student test not authored yet)");
        } catch (NoSuchMethodException wrongShape) {
            System.out.println("FAIL: StudentAuthoredTest must expose public static void run()");
        } catch (InvocationTargetException wrapped) {
            System.out.println("FAIL: StudentAuthoredTest threw " + wrapped.getCause());
        } catch (ReflectiveOperationException reflectionFailure) {
            System.out.println("FAIL: could not run StudentAuthoredTest: " + reflectionFailure);
        }
    }

    /**
     * Acceptance tests added during STEP 2. They are intentionally run against
     * the TODO stubs first so the pre-implementation failures are real.
     */
    private static void runAcceptanceTests() {
        TestSupport.check("1. empty tree", () -> {
            BinarySearchTree<Integer, Integer> tree = new BinarySearchTree<>();
            TestSupport.assertEquals(0, tree.size());
            TestSupport.assertEquals(-1, tree.height());
            TestSupport.assertEquals(List.of(), tree.inOrder());
            TestSupport.assertEquals(List.of(), tree.preOrder());
            TestSupport.assertEquals(List.of(), tree.postOrder());
            TestSupport.assertEquals(List.of(), tree.rangeSearch(1, 10));
            TestSupport.assertNull(tree.get(1), "get on empty tree");
            TestSupport.assertFalse(tree.delete(1), "delete on empty tree");
        });

        TestSupport.check("2. missing key", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(40, 20, 60);
            TestSupport.assertNull(tree.get(99), "missing key should return null");
        });

        TestSupport.check("3. null key and value rejection", () -> {
            BinarySearchTree<Integer, Integer> tree = new BinarySearchTree<>();
            TestSupport.assertThrows(IllegalArgumentException.class, () -> tree.put(null, 1));
            TestSupport.assertThrows(IllegalArgumentException.class, () -> tree.put(1, null));
            TestSupport.assertThrows(IllegalArgumentException.class, () -> tree.get(null));
            TestSupport.assertThrows(IllegalArgumentException.class, () -> tree.rangeSearch(null, 5));
            TestSupport.assertThrows(IllegalArgumentException.class, () -> tree.rangeSearch(1, null));
            TestSupport.assertThrows(IllegalArgumentException.class, () -> tree.delete(null));
        });

        TestSupport.check("4. basic insertion", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(40, 20, 60, 10, 30, 50, 70);
            TestSupport.assertEquals(7, tree.size());
            TestSupport.assertEquals(40, tree.get(40));
            TestSupport.assertEquals(10, tree.get(10));
            TestSupport.assertEquals(70, tree.get(70));
            TestSupport.assertEquals(List.of(10, 20, 30, 40, 50, 60, 70), tree.inOrder());
        });

        TestSupport.check("5. duplicate-key replacement", () -> {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
            tree.put(40, "A");
            tree.put(20, "B");
            tree.put(40, "C");
            TestSupport.assertEquals(2, tree.size());
            TestSupport.assertEquals("C", tree.get(40));
            TestSupport.assertEquals(List.of("B", "C"), tree.inOrder());
        });

        TestSupport.check("6. size behavior", () -> {
            BinarySearchTree<Integer, Integer> tree = new BinarySearchTree<>();
            TestSupport.assertEquals(0, tree.size());
            tree.put(5, 5);
            TestSupport.assertEquals(1, tree.size());
            tree.put(3, 3);
            TestSupport.assertEquals(2, tree.size());
            tree.put(5, 55);
            TestSupport.assertEquals(2, tree.size());
            TestSupport.assertTrue(tree.delete(3), "delete existing key");
            TestSupport.assertEquals(1, tree.size());
            TestSupport.assertFalse(tree.delete(99), "delete missing key");
            TestSupport.assertEquals(1, tree.size());
            TestSupport.assertTrue(tree.delete(5), "delete last key");
            TestSupport.assertEquals(0, tree.size());
        });

        TestSupport.check("7. in-order traversal", () -> {
            BinarySearchTree<Integer, Integer> tree = auditTree();
            TestSupport.assertEquals(List.of(10, 20, 30, 40, 55, 60, 70, 85), tree.inOrder());
        });

        TestSupport.check("8. pre-order traversal", () -> {
            BinarySearchTree<Integer, Integer> tree = auditTree();
            TestSupport.assertEquals(List.of(40, 20, 10, 30, 70, 55, 60, 85), tree.preOrder());
        });

        TestSupport.check("9. post-order traversal", () -> {
            BinarySearchTree<Integer, Integer> tree = auditTree();
            TestSupport.assertEquals(List.of(10, 30, 20, 60, 55, 85, 70, 40), tree.postOrder());
        });

        TestSupport.check("10. inclusive range endpoints", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(10, 20, 30, 40, 50, 60);
            TestSupport.assertEquals(List.of(20, 30, 40, 50), tree.rangeSearch(20, 50));
            TestSupport.assertEquals(List.of(20), tree.rangeSearch(20, 20));
            TestSupport.assertEquals(List.of(50), tree.rangeSearch(50, 50));
        });

        TestSupport.check("11. empty range result", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(10, 20, 30, 70, 80);
            TestSupport.assertEquals(List.of(), tree.rangeSearch(35, 60));
        });

        TestSupport.check("12. inverted range", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(10, 20, 30, 40, 50);
            TestSupport.assertEquals(List.of(), tree.rangeSearch(50, 10));
        });

        TestSupport.check("13. leaf deletion", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(40, 20, 60);
            TestSupport.assertTrue(tree.delete(20), "delete leaf");
            TestSupport.assertEquals(2, tree.size());
            TestSupport.assertNull(tree.get(20), "deleted leaf");
            TestSupport.assertEquals(List.of(40, 60), tree.inOrder());
            TestSupport.assertNull(tree.getRoot().left, "leaf pointer removed");
        });

        TestSupport.check("14. one-child deletion", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(40, 20, 60, 30);
            TestSupport.assertTrue(tree.delete(20), "delete one-child node");
            TestSupport.assertEquals(3, tree.size());
            TestSupport.assertEquals(30, tree.getRoot().left.key);
            TestSupport.assertNull(tree.getRoot().left.left, "only child spliced");
            TestSupport.assertNull(tree.getRoot().left.right, "only child spliced");
            TestSupport.assertEquals(List.of(30, 40, 60), tree.inOrder());
        });

        TestSupport.check("15. two-child deletion", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(40, 20, 60, 10, 30);
            TestSupport.assertTrue(tree.delete(20), "delete two-child node");
            TestSupport.assertEquals(4, tree.size());
            TestSupport.assertEquals(30, tree.getRoot().left.key);
            TestSupport.assertEquals(10, tree.getRoot().left.left.key);
            TestSupport.assertEquals(List.of(10, 30, 40, 60), tree.inOrder());
        });

        TestSupport.check("16. root deletion", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(40, 20, 60, 30, 50, 70);
            TestSupport.assertTrue(tree.delete(40), "delete root");
            TestSupport.assertEquals(5, tree.size());
            TestSupport.assertEquals(50, tree.getRoot().key);
            TestSupport.assertEquals(20, tree.getRoot().left.key);
            TestSupport.assertEquals(30, tree.getRoot().left.right.key);
            TestSupport.assertEquals(60, tree.getRoot().right.key);
            TestSupport.assertNull(tree.getRoot().right.left, "successor removed from old position");
            TestSupport.assertEquals(70, tree.getRoot().right.right.key);
            TestSupport.assertEquals(List.of(20, 30, 50, 60, 70), tree.inOrder());
        });

        TestSupport.check("17. delete missing key", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(40, 20, 60);
            TestSupport.assertFalse(tree.delete(99), "delete missing key");
            TestSupport.assertEquals(3, tree.size());
            TestSupport.assertEquals(List.of(20, 40, 60), tree.inOrder());
        });

        TestSupport.check("18. successor with right-child reattachment", () -> {
            BinarySearchTree<Integer, Integer> tree = auditTree();
            TestSupport.assertTrue(tree.delete(40), "delete root 40");

            TestSupport.assertEquals(7, tree.size());
            TestSupport.assertEquals(55, tree.getRoot().key);
            TestSupport.assertEquals(20, tree.getRoot().left.key);
            TestSupport.assertEquals(70, tree.getRoot().right.key);
            TestSupport.assertEquals(60, tree.getRoot().right.left.key);
            TestSupport.assertNull(tree.getRoot().right.left.left, "re-attached successor child");
            TestSupport.assertNull(tree.getRoot().right.left.right, "re-attached successor child");
            TestSupport.assertEquals(85, tree.getRoot().right.right.key);
            TestSupport.assertEquals(List.of(10, 20, 30, 55, 60, 70, 85), tree.inOrder());
        });

        TestSupport.check("19. sorted insertion 1..8 has height 7", () -> {
            BinarySearchTree<Integer, Integer> tree = treeOf(1, 2, 3, 4, 5, 6, 7, 8);
            TestSupport.assertEquals(8, tree.size());
            TestSupport.assertEquals(7, tree.height());
            TestSupport.assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8), tree.inOrder());
            TestSupport.assertNull(tree.getRoot().left, "sorted insertion is a right chain");
            TestSupport.assertEquals(2, tree.getRoot().right.key);
            TestSupport.assertEquals(3, tree.getRoot().right.right.key);
        });

        TestSupport.check("20. empty height -1 and leaf height 0", () -> {
            BinarySearchTree<Integer, Integer> tree = new BinarySearchTree<>();
            TestSupport.assertEquals(-1, tree.height());
            tree.put(5, 5);
            TestSupport.assertEquals(0, tree.height());
            tree.put(3, 3);
            TestSupport.assertEquals(1, tree.height());
            tree.put(7, 7);
            TestSupport.assertEquals(1, tree.height());
        });
    }

    private static BinarySearchTree<Integer, Integer> auditTree() {
        return treeOf(40, 20, 70, 10, 30, 55, 85, 60);
    }

    private static BinarySearchTree<Integer, Integer> treeOf(Integer... keys) {
        BinarySearchTree<Integer, Integer> tree = new BinarySearchTree<>();
        for (Integer key : keys) {
            tree.put(key, key);
        }
        return tree;
    }
}


public class StudentAuthoredTest {

    public static void run() {
        TestSupport.check("successor with a right child", () -> {

            BinarySearchTree<Integer, Integer> tree =
                    new BinarySearchTree<>();

            int[] values = {40, 20, 70, 10, 30, 55, 85, 60};

            for (int value : values) {
                tree.put(value, value);
            }

            TestSupport.assertEquals(8, tree.size());

            TestSupport.assertTrue(tree.delete(40));

            TestSupport.assertEquals(55, tree.getRoot().key);
            TestSupport.assertEquals(70, tree.getRoot().right.key);
            TestSupport.assertEquals(60, tree.getRoot().right.left.key);

            TestSupport.assertEquals(null, tree.getRoot().right.left.left);
            TestSupport.assertEquals(null, tree.getRoot().right.left.right);

            TestSupport.assertEquals(7, tree.size());

            TestSupport.assertEquals(
                    java.util.Arrays.asList(10, 20, 30, 55, 60, 70, 85),
                    tree.inOrder()
            );
        });
    }
}

    

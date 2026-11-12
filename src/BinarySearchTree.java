import java.util.ArrayList;
import java.util.List;

/**
 * An ordered inventory index backed by a binary search tree.
 *
 * <p>BST ordering invariant: for every node, all keys in its left subtree are
 * strictly smaller and all keys in its right subtree are strictly larger.</p>
 *
 * @param <K> key type; must be comparable
 * @param <V> value type
 */
public class BinarySearchTree<K extends Comparable<K>, V> {

    private BSTNode<K, V> root;
    private int size;

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Exposes the root for structural tests and the audit.
     * This is test/audit support; tree operations must still use the BST
     * ordering invariant rather than mutating through this accessor.
     */
    public BSTNode<K, V> getRoot() {
        return root;
    }

    /**
     * Inserts a key/value pair, or replaces the value when the key already
     * exists.
     *
     * @throws IllegalArgumentException if {@code key} or {@code value} is null
     */
    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("key and value must not be null");
        }

        if (root == null) {
            root = new BSTNode<>(key, value);
            size++;
            return;
        }

        put(root, key, value);
    }

    /**
     * Recursively walks down the tree. Equal keys replace the value in place,
     * so the size does not change for a duplicate.
     */
    private void put(BSTNode<K, V> node, K key, V value) {
        int comparison = key.compareTo(node.key);

        if (comparison == 0) {
            node.value = value;
        } else if (comparison < 0) {
            if (node.left == null) {
                node.left = new BSTNode<>(key, value);
                size++;
            } else {
                put(node.left, key, value);
            }
        } else {
            if (node.right == null) {
                node.right = new BSTNode<>(key, value);
                size++;
            } else {
                put(node.right, key, value);
            }
        }
    }

    /**
     * Returns the value associated with {@code key}, or {@code null} when the
     * key is absent. Null keys are rejected.
     *
     * @throws IllegalArgumentException if {@code key} is null
     */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }
        return get(root, key);
    }

    private V get(BSTNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int comparison = key.compareTo(node.key);
        if (comparison == 0) {
            return node.value;
        } else if (comparison < 0) {
            return get(node.left, key);
        } else {
            return get(node.right, key);
        }
    }

    /**
     * Returns all values whose keys lie in the inclusive range
     * [{@code min}, {@code max}], in ascending key order.
     *
     * <p>Returns an empty list for an inverted range. Subtrees that cannot
     * contain a key in the range are skipped (pruned) instead of visited.</p>
     *
     * @throws IllegalArgumentException if either bound is null
     */
    public List<V> rangeSearch(K min, K max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("range bounds must not be null");
        }

        List<V> values = new ArrayList<>();
        if (min.compareTo(max) > 0) {
            return values;
        }

        rangeSearch(root, min, max, values);
        return values;
    }

    /**
     * In-order recursion gives ascending keys for free: left, node, right.
     * The two pruning checks keep the work proportional to the useful part of
     * the tree rather than always visiting every node.
     */
    private void rangeSearch(BSTNode<K, V> node, K min, K max, List<V> values) {
        if (node == null) {
            return;
        }

        // Only the left subtree can contain keys smaller than this node's key.
        if (min.compareTo(node.key) < 0) {
            rangeSearch(node.left, min, max, values);
        }

        if (min.compareTo(node.key) <= 0 && max.compareTo(node.key) >= 0) {
            values.add(node.value);
        }

        // Only the right subtree can contain keys larger than this node's key.
        if (max.compareTo(node.key) > 0) {
            rangeSearch(node.right, min, max, values);
        }
    }

    /**
     * Deletes {@code key} if present.
     *
     * @return {@code true} when a node was removed, {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is null
     */
    public boolean delete(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null");
        }

        // Values are never null, so a null lookup proves the key is absent.
        if (get(key) == null) {
            return false;
        }

        root = delete(root, key);
        size--;
        return true;
    }

    private BSTNode<K, V> delete(BSTNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        int comparison = key.compareTo(node.key);

        if (comparison < 0) {
            node.left = delete(node.left, key);
        } else if (comparison > 0) {
            node.right = delete(node.right, key);
        } else {
            // Case 1 and case 2: no left child or no right child.
            // Returning the existing child (or null for a leaf) lets the
            // caller's assignment splice the node out.
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }

            // Case 3: two children. Copy the in-order successor's contents
            // into this node, then remove the successor from the right
            // subtree so no duplicate key remains.
            BSTNode<K, V> successor = minNode(node.right);
            node.key = successor.key;
            node.value = successor.value;
            node.right = deleteMin(node.right);
        }

        return node;
    }

    /**
     * Removes the minimum node from a subtree. The successor has no left child,
     * so returning its right child reattaches that child to the successor's
     * former parent.
     */
    private BSTNode<K, V> deleteMin(BSTNode<K, V> node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteMin(node.left);
        return node;
    }

    /** Returns values in ascending key order. */
    public List<V> inOrder() {
        List<V> values = new ArrayList<>();
        inOrder(root, values);
        return values;
    }

    private void inOrder(BSTNode<K, V> node, List<V> values) {
        if (node == null) {
            return;
        }
        inOrder(node.left, values);
        values.add(node.value);
        inOrder(node.right, values);
    }

    /** Returns values in root-left-right order. */
    public List<V> preOrder() {
        List<V> values = new ArrayList<>();
        preOrder(root, values);
        return values;
    }

    private void preOrder(BSTNode<K, V> node, List<V> values) {
        if (node == null) {
            return;
        }
        values.add(node.value);
        preOrder(node.left, values);
        preOrder(node.right, values);
    }

    /** Returns values in left-right-root order. */
    public List<V> postOrder() {
        List<V> values = new ArrayList<>();
        postOrder(root, values);
        return values;
    }

    private void postOrder(BSTNode<K, V> node, List<V> values) {
        if (node == null) {
            return;
        }
        postOrder(node.left, values);
        postOrder(node.right, values);
        values.add(node.value);
    }

    /** Returns the number of stored key/value pairs. */
    public int size() {
        return size;
    }

    /**
     * Returns the height in edges. An empty tree has height -1; a leaf has
     * height 0.
     */
    public int height() {
        return height(root);
    }

    private int height(BSTNode<K, V> node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /**
     * Returns the node with the minimum key in the subtree rooted at
     * {@code node}, or {@code null} for an empty subtree.
     */
    private BSTNode<K, V> minNode(BSTNode<K, V> node) {
        if (node == null) {
            return null;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
}

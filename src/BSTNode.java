/**
 * A single node in a binary search tree.
 *
 * <p>The fields are intentionally package-visible so the tree and the test
 * runner can inspect the structure during the audit.</p>
 *
 * @param <K> key type; must be comparable so ordering is well defined
 * @param <V> value type
 */
public class BSTNode<K extends Comparable<K>, V> {

    public K key;
    public V value;
    public BSTNode<K, V> left;
    public BSTNode<K, V> right;

    /**
     * Creates a leaf node with no children.
     *
     * @param key   non-null key
     * @param value non-null value
     */
    public BSTNode(K key, V value) {
        this.key = key;
        this.value = value;
        this.left = null;
        this.right = null;
    }
}

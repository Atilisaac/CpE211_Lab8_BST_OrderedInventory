# WORKSHEET — CpE 211 Lab 1: Ordered Inventory Index

## A. Draw and Validate

### Eight-row insertion table

Insertion order: **40, 20, 70, 10, 30, 55, 85, 60**

| Step | Inserted key | Comparison path | Tree after step |
|---:|---:|---|---|
| 1 | 40 | Tree is empty; 40 becomes the root. | `40` |
| 2 | 20 | 20 < 40 → go left; left is empty. | `40` with left child `20` |
| 3 | 70 | 70 > 40 → go right; right is empty. | `40` with left `20`, right `70` |
| 4 | 10 | 10 < 40 → left; 10 < 20 → left; empty. | `20` gets left child `10` |
| 5 | 30 | 30 < 40 → left; 30 > 20 → right; empty. | `20` gets right child `30` |
| 6 | 55 | 55 > 40 → right; 55 < 70 → left; empty. | `70` gets left child `55` |
| 7 | 85 | 85 > 40 → right; 85 > 70 → right; empty. | `70` gets right child `85` |
| 8 | 60 | 60 > 40 → right; 60 < 70 → left; 60 > 55 → right; empty. | `55` gets right child `60` |

Tree diagrams after each insertion:

```text
Step 1              Step 2              Step 3
  40                  40                  40
                     /                  /    \
                   20                 20      70

Step 4              Step 5              Step 6
      40                  40                  40
     /                   /  \                /  \
   20                  20    70            20    70
   /                   / \                 / \   /
 10                  10  30              10 30 55

Step 7              Step 8 (final)
      40                  40
     /  \                /  \
   20    70            20    70
  / \   / \           / \   / \
10 30 55 85         10 30 55 85
                              \
                              60
```

### Original BST

```text
            40
          /    \
        20      70
       /  \    /  \
     10   30  55   85
                \
                 60
```

Height = **3** (longest path in edges: 40 → 70 → 55 → 60).

In-order traversal: `10, 20, 30, 40, 55, 60, 70, 85`

Pre-order traversal: `40, 20, 10, 30, 70, 55, 60, 85`

Post-order traversal: `10, 30, 20, 60, 55, 85, 70, 40`

### After deleting 40

The successor is the minimum key in the right subtree of 40. Starting at 70 and walking left gives **55**.

- Copy 55 into the root position.
- Remove the old 55 node from the right subtree.
- Old 55 had right child 60, so 60 is reattached as the left child of 70.

```text
            55
          /    \
        20      70
       /  \    /  \
     10   30  60   85
```

In-order traversal after deletion: `10, 20, 30, 55, 60, 70, 85`

Size after deletion: **7**

Actual link checks from the audit:

- New root key = 55
- Root's right child key = 70
- 70's left child key = 60
- Node 60 has no left or right child

## B. Implement and Test

### Methods implemented

| Method | What it does |
|---|---|
| `put(key, value)` | Rejects null key/value, inserts by BST comparisons, replaces the value for an equal key, increments size only for a new key. |
| `get(key)` | Rejects null, descends by comparisons, returns the value or null. |
| `rangeSearch(min, max)` | Rejects null bounds, returns empty for inverted bounds, visits only branches that can contain range keys, returns values in ascending order. |
| `delete(key)` | Rejects null, returns false when absent, and handles leaf, one-child, and two-child removal. |
| `inOrder()` | Left → node → right; ascending key order. |
| `preOrder()` | Node → left → right. |
| `postOrder()` | Left → right → node. |
| `size()` | Returns the stored pair count. |
| `height()` | Returns edges to the deepest leaf; empty = -1, leaf = 0. |
| `minNode` / `deleteMin` | Find the leftmost successor and remove it while reattaching its right child. |

### Student-authored test

The student selected **successor with a right child** because a two-child deletion must replace the node with its in-order successor and must not lose the successor's right subtree.

Location: `test/StudentAuthoredTest.java`

Setup:
- Insert `40, 20, 70, 10, 30, 55, 85, 60`.
- Delete `40`.
- Check the actual links using `getRoot()`.
- Check size and in-order traversal.

Expected behavior:
- New root = 55
- Root's right child = 70
- 70's left child = 60
- 60 has no children
- Size = 7
- In-order = `10, 20, 30, 55, 60, 70, 85`

This test passed after implementation. Before implementation it failed with `UnsupportedOperationException: TODO: put`, which showed that the failure was caused by the missing feature rather than a syntax error.

### Acceptance-test coverage

`test/LabTestRunner.java` contains 20 acceptance tests:

1. Empty tree
2. Missing key
3. Null key and value rejection
4. Basic insertion
5. Duplicate-key replacement
6. Size behavior
7. In-order traversal
8. Pre-order traversal
9. Post-order traversal
10. Inclusive range endpoints
11. Empty range result
12. Inverted range
13. Leaf deletion
14. One-child deletion
15. Two-child deletion
16. Root deletion
17. Delete missing key
18. Successor with right-child reattachment
19. Sorted insertion 1..8 has height 7
20. Empty height -1 and leaf height 0

Final runner result: **21 passed, 0 failed** (20 acceptance tests plus the student-authored test).

## C. Query and Audit

### `rangeSearch(25, 60)` trace

The inclusive range is [25, 60].

| Step | Node visited | Decision | Ascending result so far |
|---:|---:|---|---|
| 1 | 40 | Visit 40, but first explore its left subtree because `min < 40`. | — |
| 2 | 20 | Since 20 < 25, prune the left subtree containing 10. Continue right to 30. | — |
| 3 | 30 | 30 is inside the range; add it. Return through 20 to 40, then add 40. | `30, 40` |
| 4 | 70 | Since 70 > 60, prune the right subtree containing 85. Continue left to 55. | `30, 40` |
| 5 | 55 | 55 is inside the range; add it. Continue right to 60. | `30, 40, 55` |
| 6 | 60 | 60 is the inclusive upper endpoint; add it. Do not enter its right subtree. | `30, 40, 55, 60` |

Visited node keys in actual audit order: **`40, 20, 30, 70, 55, 60`**

Pruned branches:

- Subtree rooted at **10**: skipped at node 20 because `20 < min`.
- Subtree rooted at **85**: skipped at node 70 because `70 > max`.

Results in ascending order: **`30, 40, 55, 60`**

Measured cost: 6 visited nodes, with height `h = 3` and `k = 4` results.

## D. Complexity Audit

| Operation | Typical / Expected | Worst case |
|---|---|---|
| put / get / delete | O(h), balanced typical O(log n) | O(n) |
| Traversal | O(n) | O(n) |
| Range search | O(h + k) with pruning | O(n) |
| Recursive stack | O(h) | O(n) |

`h` is tree height and `k` is the number of values returned by the range query.

### Measured evidence from the audit

The audit used temporary visited-node counters, then removed them.

- Audit tree height = **3**.
- `get(60)` on the audit tree visited **4** nodes.
- `put(65)` on the audit tree visited **4** nodes.
- `delete(40)` visited **4** nodes including the existence lookup and successor walk.
- `rangeSearch(25, 60)` visited **6** nodes and returned **4** values.
- A full range query visited **8** nodes and returned **8** values.
- A range entirely above the tree visited **3** nodes and returned **0** values.
- Each traversal (`inOrder`, `preOrder`, `postOrder`) visited exactly **8** nodes on the 8-node tree.

Height comparison:

| Input order | Height | Search measured |
|---|---:|---|
| Sorted `1..8` | 7 | `get(8)` visited 8 nodes |
| Balanced order `50, 25, 75, 12, 37, 62, 87, 6` | 3 | `get(6)` visited 4 nodes |

The recursive stack follows the height: the audit tree's longest path contains 4 node calls plus the null base case, while the sorted 8-node tree's longest path contains 8 node calls plus the null base case.

## E. Reflection

**1. Which deletion case required the most careful pointer repair?**

The two-child case required the most care. For a leaf or a one-child node, returning the existing child lets the parent's assignment remove the node cleanly. For two children, the successor is the leftmost node in the right subtree. After copying the successor's key and value into the deleted node, the old successor node must be removed. If that successor has a right child, the child must be connected to the successor's former parent. In the audit, deleting 40 copied 55 into the root and reattached 60 as the left child of 70. Checking the actual links, not only the traversal, confirmed this repair.

**2. What evidence showed that sorted-input insertion produced a linear-height tree?**

Inserting 1 through 8 in ascending order always sent each new key to the right of the previous maximum, producing a right chain. The measured height was **7** for 8 nodes, and searching for key 8 visited **8** nodes. In contrast, the balanced insertion order produced height **3**, and searching for a key visited **4** nodes. This shows that sorted input gives linear height and therefore O(n) search/insert/delete behavior, while a balanced shape keeps operations logarithmic.

# CpE 211 – Lab 1: Ordered Inventory Index Using Binary Search Tree

A beginner-friendly Java implementation of an **Ordered Inventory Index** backed by a
generic Binary Search Tree (BST). The project stores comparable keys with associated
values and supports ordered insertion, lookup, range queries, deletion, and tree
traversals.

**Course:** CpE 211 – Data Structures and Algorithms  
**Repository:** [Atilisaac/CpE211_Lab8_BST_OrderedInventory](https://github.com/Atilisaac/CpE211_Lab8_BST_OrderedInventory)

---

## Project Description

An inventory index needs fast access to records while keeping them ordered by a
comparable key. This project demonstrates how a Binary Search Tree provides that
ordering:

- Every key in a node's left subtree is smaller than the node's key.
- Every key in a node's right subtree is larger than the node's key.
- In-order traversal therefore returns the stored values in ascending key order.

The implementation is written in plain Java and has **no external dependencies**. The
test runner is also dependency-free and runs directly from the command line.

---

## Learning Objectives

After completing this lab, the project demonstrates the ability to:

1. Implement a generic Binary Search Tree using `Comparable` keys.
2. Preserve the BST ordering invariant during insertion and deletion.
3. Apply recursion for search, traversal, range querying, and deletion.
4. Handle the three deletion cases: leaf, one child, and two children.
5. Use the in-order successor when deleting a node with two children.
6. Prune irrelevant subtrees during a range search.
7. Analyze tree operations in terms of height and worst-case performance.
8. Build and run a dependency-free Java test suite from PowerShell.

---

## Features and BST Operations

### Core Classes

| Class | Responsibility |
|---|---|
| `BSTNode<K extends Comparable<K>, V>` | Stores a key, value, and left/right child references. |
| `BinarySearchTree<K extends Comparable<K>, V>` | Implements the ordered inventory operations. |

### Implemented Operations

| Operation | Description |
|---|---|
| `put(key, value)` | Inserts a key/value pair. Rejects null keys and values. Replacing an existing key updates its value without increasing the size. |
| `get(key)` | Returns the value for a key, or `null` when absent. Rejects null keys. |
| `rangeSearch(min, max)` | Returns values with keys inside the inclusive range, in ascending order. Returns an empty list for inverted bounds and prunes subtrees that cannot contain matching keys. |
| `delete(key)` | Removes a key and returns whether deletion occurred. Handles leaf, one-child, and two-child nodes. |
| `inOrder()` | Returns values in left → node → right order, which is ascending by key. |
| `preOrder()` | Returns values in node → left → right order. |
| `postOrder()` | Returns values in left → right → node order. |
| `size()` | Returns the number of stored key/value pairs. |
| `height()` | Returns the number of edges on the longest path. An empty tree has height `-1`; a leaf has height `0`. |

The two-child deletion uses the **minimum key in the right subtree** as the in-order
successor and correctly reattaches the successor's right child.

---

## Project Folder Structure

```text
CpE211_Lab8_BST_OrderedInventory/
├── src/
│   ├── BSTNode.java              # Generic BST node
│   └── BinarySearchTree.java     # BST operations and helpers
├── test/
│   ├── TestSupport.java          # Lightweight assertions and test accounting
│   ├── StudentAuthoredTest.java  # Collaborative test developed by the student and ChatGPT to verify successor deletion and right-child reattachment.
│   └── LabTestRunner.java        # Dependency-free test entry point
├── AGENT_LOG.md                  # Development workflow and verification record
├── WORKSHEET.md                  # Lab worksheet, traces, and complexity evidence
├── README.md                     # This file
└── .gitignore                    # Excludes compiled classes and IDE files
```

`out/` is created automatically by `javac` and contains compiled `.class` files. It is
excluded from Git by `.gitignore`.

---

## Java JDK Requirements

- **Java 9 or newer** is required because the code uses `List.of(...)` in the tests.
- The project was verified with **JDK 21.0.11 LTS**.
- `java` and `javac` must be available on your `PATH`.

Check your installation from PowerShell:

```powershell
java -version
javac -version
```

---

## Compile and Run

Run the following commands from the project root folder
(`CpE211_Lab8_BST_OrderedInventory`).

### 1. Compile

```powershell
javac -d out src\BSTNode.java src\BinarySearchTree.java test\TestSupport.java test\StudentAuthoredTest.java test\LabTestRunner.java
```

This compiles the source and test files into the `out` directory.

### 2. Run the test runner

```powershell
java -cp out LabTestRunner
```

The runner executes the student-authored test and the acceptance tests. The expected
result is:

```text
Results: 21 passed, 0 failed
```

---

## Test Results

Verified with the commands above:

| Test group | Result |
|---|---:|
| Collaborative test (student + ChatGPT): successor with a right child | 1 passed |
| Acceptance tests | 20 passed |
| **Total** | **21 passed, 0 failed** |

The acceptance suite covers:

- Empty tree and missing keys
- Null key/value rejection
- Basic insertion and duplicate replacement
- Size behavior and all three traversals
- Inclusive, empty, and inverted ranges
- Leaf, one-child, two-child, and root deletion
- Deleting a missing key
- Successor reattachment when the successor has a right child
- Sorted insertion height and empty/leaf height conventions

---

## BST Time Complexity

Let `h` be the height of the tree and `n` the number of nodes.

| Operation | Typical / Expected | Worst Case |
|---|---|---|
| `put`, `get`, `delete` | `O(h)`; `O(log n)` when balanced | `O(n)` |
| `inOrder`, `preOrder`, `postOrder` | `O(n)` | `O(n)` |
| `rangeSearch` | `O(h + k)` with pruning, where `k` is the number of results | `O(n)` |
| Recursive call stack | `O(h)` | `O(n)` |

A balanced BST keeps `h` close to `log n`, so search, insertion, and deletion are fast.
If keys are inserted in sorted order, the tree can become a long chain with height
`n - 1`, making operations linear. This project verifies that behavior: inserting
`1..8` in order produces height `7`, while a balanced insertion order produces height
`3`.

---

## Course Information

**CpE 211 – Data Structures and Algorithms**  
Lab 1: Ordered Inventory Index Using Binary Search Tree

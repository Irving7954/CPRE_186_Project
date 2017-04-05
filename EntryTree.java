package edu.iastate.cs228.hw4;

import java.util.Arrays;

/**
 * A class that models a prefix tree (Trie) in which the letters can 
 * also have values. It is implemented as a child-sibling tree as well. It has five
 * public methods that treat it as a prefix tree.
 * @author Luke Schoeberle
 */
public class EntryTree<K, V> {

	/**
	 * The root of the tree, which is a node with no value or key.
	 */
	protected Node root;

	/**
	 * The length of the last result of a call to prefix(). It is not used in my code, but
	 * I do not want to remove it in case they test it.
	 */
	protected int prefixlen;

	/**
	 * A node class that is used for storing each of the values at a node. This
	 * node contains pointers to the left-most child, the parent, the next
	 * sibling, the previous sibling, the key, and the value for this node.
	 * @author Luke Schoeberle
	 */
	protected class Node implements EntryNode<K, V> {
		
		/**
		 * This node's first child (the left-most one).
		 */
		protected Node child;

		/**
		 * This node's parent.
		 */
		protected Node parent; 

		/**
		 * This node's previous sibling.
		 */
		protected Node prev; 

		/**
		 * This node's next sibling.
		 */
		protected Node next; 

		/**
		 * This node's key.
		 */
		protected K key;

		/**
		 * This node's value.
		 */
		protected V value; 

		/**
		 * Initializes the key and value to the parameters, setting the rest of
		 * the pointers to null.
		 * @param aKey The key for this node.
		 * @param aValue The value for this node.
		 */
		public Node(K aKey, V aValue) {
			key = aKey;
			value = aValue;
			child = null;
			parent = null;
			prev = null; //set everything to null except the parameters
			next = null;
		}

		/**
		 * Returns the parent of this node.
		 * @return This node's parent.
		 */
		@Override
		public EntryNode<K, V> parent() {
			return parent;
		}

		/**
		 * Returns the first child of this node.
		 * @return This node's first child.
		 */
		@Override
		public EntryNode<K, V> child() {
			return child;
		}

		/**
		 * Returns the next sibling of this node. 
		 * @return This node's next sibling.
		 */
		@Override
		public EntryNode<K, V> next() {
			return next;
		}

		/**
		 * Returns the previous sibling of this node.
		 * @return This node's previous sibling.
		 */
		@Override
		public EntryNode<K, V> prev() {
			return prev;
		}

		/**
		 * Returns the key of this node.
		 * @return This node's key.
		 */
		@Override
		public K key() {
			return key;
		}

		/**
		 * Returns the value of this node. 
		 * @return This node's value.
		 */
		@Override
		public V value() {
			return value;
		}
	}

	public EntryTree() {
		root = new Node(null, null);
		prefixlen = 0;
	}
	
	/**
	 * A helper method that finds a node based on the key sequence. It is used for calls
	 * to search() and remove().
	 * @param keys The key sequence to be found.
	 * @return The node at which the sequence is found, or null if the sequence is not found.
	 */
	private Node find(K[] keys) {
		if (keys == null || keys.length == 0)
			return null; 
		checkForNulls(keys); //appropriate error checking
		int index = 0;
		Node curr = root.child; //start at the root's child
		while (curr != null) {
			K currKey = keys[index];
			if (curr.key.equals(currKey)) { //compare the two keys
				if(index == keys.length - 1) {
					prefixlen = index + 1; //update prefixlen (pointless in my code but just in case)
					return curr; //exit if the last correct one is found
				}	
				curr = curr.child; 
				index++; //go down to the child and advance the index if we have found the right key
			} 
			else
				curr = curr.next; //otherwise, move to the next sibling
		}
		prefixlen = index; //update prefixlen (pointless in my code but just in case)
		return curr; //return the node that we landed on
	}

	/**
	 * Returns the value of the entry with a specified key sequence, or null if
	 * this tree contains no entry with the key sequence.
	 * @param keyarr The key sequence to be found.
	 * @return The value at the key sequence, or null if that key sequence is not found.
	 */
	public V search(K[] keyarr) { 
		Node node = find(keyarr); //uses find
		if(node == null)
			return null; //returns null if not found
		return node.value; //otherwise returns its value
	}

	/**
	 * Returns the key sequence of the longest prefix of keyarr that is in the tree.
	 * It uses the same search method as search(), but it does not fail if the
	 * entire sequence is not found.
	 * @param keyarr The key sequence in which to find a prefix.
	 * @return The longest prefix that is in both keyarr and the tree.
	 */
	public K[] prefix(K[] keyarr) {
		if(keyarr == null || keyarr.length == 0)
			return null;
		checkForNulls(keyarr); //appropriate error checking
		int index = 0;
		Node curr = root.child;
		while (curr != null && index < keyarr.length) {
			K currKey = keyarr[index];
			if (curr.key.equals(currKey)) {
				index++;
				curr = curr.child; //search as before (but without checking for a success)
			} 
			else
				curr = curr.next;
		}
		K[] result = Arrays.copyOf(keyarr, index); //create a copy with length of our found
		prefixlen = index; //update prefixlen (pointless in my code but just in case)
		if(result.length == 0)
			return null; //return null if the length is zero
		return result; //return the array copy otherwise
	}

	/**
	 * Adds the specified key sequence to the list. It essentially has two cases:
	 * if the entire key array is in the tree, it overrides the value at that node; otherwise,
	 * it appends null values to the tree to create a path for the value at the last node. So it has
	 * to find the prefix, but it also needs to know the node, so it has a slightly different search implementation.
	 * @param keyarr The key sequence to be added.
	 * @param aValue The value to be put at the end.
	 * @return True if anything was changed, false otherwise.
	 */
	public boolean add(K[] keyarr, V aValue) {
		if (keyarr == null || keyarr.length == 0 || aValue == null)
			return false;
		checkForNulls(keyarr); //appropriate error checking
		int index = 0;
		Node curr = root.child;
		while (curr != null && index < keyarr.length) {
			K currKey = keyarr[index];
			if (curr.key.equals(currKey)) {
				if (index == keyarr.length - 1) {
					curr.value = aValue; //end if the key sequence is found
					return true;
				}
				index++;  //search as before
				if (curr.child != null)
					curr = curr.child; //except we don't want to end on a null value (node of a prefix), except the root
				else
					break;
			} 
			else if(curr.next != null)
				curr = curr.next; //except we don't want to end on a null value (node of a prefix) (except the root)
			else
				break;

		}
		if (curr == null) //this occurs when the tree does not yet have a branch for this key sequence
			curr = root;
		if ((index > 0 && keyarr[index - 1].equals(curr.parent.key)) || (keyarr[0] != curr.key && curr.parent == root)) { 
			//check to see if we want to add a sibling first
			Node newNode = new Node(keyarr[index++], null);
			newNode.prev = curr;
			curr.next = newNode;
			newNode.parent = curr.parent; //links a sibling into the list
			curr = newNode; //updates curr
		}
		while (index < keyarr.length) { //now we can add the rest as children
			Node newNode = new Node(keyarr[index++], null);
			newNode.parent = curr;
			curr.child = newNode; //link the rest as children
			curr = newNode; //updates curr
		}
		curr.value = aValue; //changes the last one's value
		return true;
	}

	/**
	 * Removes the entry for a key sequence from this tree and returns its value
	 * if it is present. Otherwise, it makes no change to the tree and returns null. Additionally,
	 * related nodes whose values are null and that have no children are also removed because they do not
	 * have any function in the tree.
	 * @param keyarr The key sequence to be removed.
	 * @return The value of the item removed.
	 */
	public V remove(K[] keyarr) {
		Node node = find(keyarr); //finds the node (which also performs error checking)
		if(node == null)
			return null; //returns null if the keyarr is not found
		V val = node.value;
		node.value = null; //nulls its value first (to start the loop)
		while(node != root && node.child == null && node.value == null) { //removes useless nodes repeatedly
			unlink(node); //unlinks it
			node.value = null; //nulls its value (necssary for iterations after the first one)
			node = node.parent; //updates node
		}
		return val;
	}
	
	/**
	 * A helper method used to remove a node. It has the typical linked list remove form, except it
	 * also updates the parent pointers if necessary.
	 * @param node The node to be unlinked from the list.
	 */
	private void unlink(Node node) {
		if(node.prev != null) //updates the previous value if it exists
			node.prev.next = node.next;
		if(node.next != null) //updates the next value if it exists
			node.next.prev = node.prev;
		if(node.parent.child == node) //updates the parent link if it is the left-most child
			node.parent.child = node.next;
	}

	/**
	 * Prints the tree on the console in the output format shown in
	 * an example output file. It appears to be a form of an in-order traversal.
	 */
	public void showTree() {
		System.out.println("null->null");
		Node curr = root.child;
		int depth = 1; //begins at depth 1 (determines amount of indentation)
		String tab = "   "; //sets the amount of indentation
		while (curr != null) {
			String indentation = "";
			for(int i = 0; i < depth; i++)
				indentation += tab; //determine the indentation based on the depth
			System.out.println(indentation + curr.key + "->" + curr.value); //print the current key and value with
			if(curr.child != null) {										//the proper indentation
				curr = curr.child; //go as far left as possible
				depth++; //increment the depth as you go down
			}
			else {
				while(curr != root && curr.next == null) { //go back up to the next sequence of children
					curr = curr.parent; //go up until you some siblings
					depth--; //decrement the depth as you go back up
				}
				curr = curr.next; //go to the next sibling
			}
		}
	}
	
	/**
	 * A helper method used in the four methods with key sequence parameter. It checks to
	 * see if any of the keys in the key sequence are null.
	 * @param keys The key sequence to be checked.
	 */
	private void checkForNulls(K[] keys) {
		for(K k : keys)
			if(k == null)
				throw new NullPointerException("At least one element in the key sequence is null!");
	}
}

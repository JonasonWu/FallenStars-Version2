package project5;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This BST class shows a BST data structure. Parts of this class is taken from ED workspace.
 * 
 * @author Jonason Wu, Joanna Klukowska
 * @version 12/3/2020
 *
 * @param <T> The generic type of element stored by the BST
 */
public class BST < T extends Comparable <T>> {
	
    private BSTNode root;   //reference to the root node of the tree 
    private int size;       //number of values stored in this tree 
    private Comparator<T> comparator;   //comparator object to overwrite the 
                                //natural ordering of the elements 

    
	private boolean found;  //helper variable used by the remove methods
    private boolean added ; //helper variable used by the add method 


    /**
	 * Constructs a new, empty tree, sorted according to the natural ordering of its elements.
	 */
    public BST () {
        root = null; 
        size = 0; 
        comparator = null; 
    }

    /**
	 * Constructs a new, empty tree, sorted according to the specified comparator.
	 */
    public BST(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    /**
     * Returns true if this tree contains the specified element. More formally, returns true 
     * if and only if this tree contains an element e such that Objects.equals(o, e). This 
     * operation should be O(H).
     * 
     * @param o - object to be checked for containment in this set
     * @return true if this tree contains the specified element
     * @throws ClassCastException - if the specified object cannot be compared with the 
     * elements currently in the set
     * @throws NullPointerException - if the specified element is null and this tree uses 
     * natural ordering, or its comparator does not permit null elements
     */
	@SuppressWarnings("unchecked")
	public boolean contains (Object o) throws ClassCastException, NullPointerException {
		//If the BST is empty, nothing needs to be checked, just return false since it is 
		//	impossible for any element to exist in an empty BST.
		if (this.isEmpty())
    		return false;

		if (o == null && comparator == null) 
			throw new NullPointerException("The parameter is null and this tree uses "
					+ "natural ordering");
		BSTNode find;
    	try {
    		//If o cannot be cast to T, then !(o instanceof Comparable), 
    		//	ClassCastException thrown. If o == null, then the cast is done.
    		find = new BSTNode((T) o);
    		//See if they are comparable. If not, then either exception could be thrown
    		root.compareTo(find);
    	}
    	catch (ClassCastException ex) {
    		throw new ClassCastException("Specified object cannot be compared with elements "
    				+ "currently in the set");
    	}
    	catch(NullPointerException ex) {
    		//o == null && comparator != null, but comparator does not permit null elements.
    		throw new NullPointerException("The comparator does not permit null "
					+ "elements");
    	}
		return contains(find, root);
    }
    
    /**
     * Returns true if this tree contains the specified element. More formally, returns true 
     * if and only if this tree contains an element e such that Objects.equals(o, e). This 
     * operation should be O(H). Recursive implementation!
     * 
     * @param o - object to be checked for containment in this set
     * @return true if this tree contains the specified element
     */
    private boolean contains(BSTNode find, BSTNode root) {
    	if (root == null) 
    		return false;
    	
    	//The compareTo method compares using the comparator is there exists one.
    	int relation = root.compareTo(find);
    	if (relation > 0)
    		//root > find, so find could only possibly be on the left
    		return contains(find, root.left);
    	else if (relation < 0)
    		//root < find, so find could only possibly be on the right
    		return contains(find, root.right);
    	else
    		//relation == 0, so find should be found. Make sure that both objects 
    		//	are equal determined by equals() method as well. If it is not, then false 
    		//  is returned because method of adding to the tree makes it so that there
    		//	will not be duplicates determined by the comparison.
    		return Objects.equals(find.data, root.data);
    }
    
    /**
     * Returns true if this tree contains no elements. This operation should be O(1).
     * 
     * @return true if this tree contains no elements
     */
    public boolean isEmpty() {
    	if (root == null)
    		return true;
    	return false;
    }
    
    /**
     * Returns an iterator over the elements in this tree in ascending order. This operation 
     * should be O(N).
     * 
     * @return an iterator over the elements in this tree in ascending order
     */
    public Iterator<T> iterator() {
		return new Itr();	
    }
    
    /**
     * Used by {@link BST#iterator()} to create an iterator. This implementation creates
     * an array and adds all the elements of the BST inorder for iterating.
     * 
     * @author Jonason Wu
     */
    private class Itr implements Iterator<T> {
    	/*
    	 * An alternative implementation would be to use 
    	 * {@link BST#getRange(Comparable, Comparable)} with the parameters as 
    	 * {@link BST#first()} and {@link BST#last()} to create an ArrayList for iterating
    	 * through. However, the alternate implementation will use a few extra traversals 
    	 * than the current implementation.
    	 * The current implementation may also save more memory space during runtime.
    	 */
    	
    	Object[] arr = new Object[size];
    	//To keep track of which location in array to add the element to.
    	int adding = 0;
    	
    	//The iteration determined by the number of calls to next()
    	int current = 0; 
    	
    	/**
    	 * Add all the elements of BST by inorder traversal into the array
    	 */
    	Itr() {
    		addTree(BST.this.root);
    	}
    	
    	/**
    	 * Inorder traversal of BST to add the elements into the array.
    	 * 
    	 * @param root - the root of the BST tree
    	 */    	
    	private void addTree(BSTNode root) {
    		if (root == null)
    			return;
    		addTree(root.left);
    		arr[adding++] = root.data;
    		addTree(root.right);   		
    	}
    	
		@Override
		public boolean hasNext() {
			return current != adding;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			return (T) arr[current++];
		}
    }
    
    /**
     * Returns a collection whose elements range from fromElement, inclusive, to toElement, 
     * inclusive. The returned collection/list is backed by this tree, so changes in the 
     * returned list are reflected in this tree, and vice-versa (i.e., the two structures 
     * share elements. The returned collection should be organized according to the 
     * natural ordering of the elements (i.e., it should be sorted). This operation should 
     * be O(M) where M is the number of elements in the returned list.
     * 
     * @param fromElement - low endpoint (inclusive) of the returned collection
     * @param toElement - high endpoint (inclusive) of the returned collection
     * @return a collection containing a portion of this tree whose elements range from 
     * fromElement, inclusive, to toElement, inclusive
     * @throws NullPointerException - if fromElement or toElement is null
     * @throws IllegalArgumentException - if fromElement is greater than toElement
     */
    public ArrayList<T> getRange(T fromElement, T toElement) throws NullPointerException, 
    IllegalArgumentException {
    	if (fromElement == null || toElement == null)
    		throw new NullPointerException ("The parameters passed in cannot be null");
    	//fromElement cannot be > than toElement
    	BSTNode from = new BSTNode(fromElement);
    	BSTNode to = new BSTNode(toElement);
    	if (from.compareTo(to) > 0)
    		throw new IllegalArgumentException ("The lower endpoint cannot be greater than the "
    				+ "higher endpoint");
    	
    	//The array to return
    	ArrayList<T> arr = new ArrayList<>();
    	
		return getRange(from, to, root, arr);
    }

    /**
     * Continuation of {@link BST#getRange(Comparable, Comparable)}. The parameters are 
     * validated. Recursive implementation!
     * 
     * @param fromElement - low endpoint (inclusive) of the returned collection
     * @param toElement - high endpoint (inclusive) of the returned collection
     * @return a collection containing a portion of this tree whose elements range from 
     * fromElement, inclusive, to toElement, inclusive
     */
    private ArrayList<T> getRange(BSTNode fromElement, BSTNode toElement, BSTNode root, 
    		ArrayList<T> arr) {
    	if (root == null)
    		return arr;
    	
    	//To be included in the ArrayList:
    	//	must be greater than or equal to 0.  
    	if (root.compareTo(fromElement) < 0)
    		//The root is too small to be included, so only right elements are possibly included
    		return getRange(fromElement, toElement, root.right, arr);
    	//	must be lesser than or equal to 0.
    	if (root.compareTo(toElement) > 0) 
    		//The root is too big to be included, so only left elements are possibly included
    		return getRange(fromElement, toElement, root.left, arr);
    	
    	//root is greater than or equal to fromElement and less than or equal to toElement:
    	//	both sides work, so perform inorder traversal.
    	getRange(fromElement, toElement, root.left, arr);
    	arr.add(root.data);
    	getRange(fromElement, toElement, root.right, arr);
		return arr;
	}


    /**
     * Returns a collection whose elements range from fromElement, inclusive, to toElement, 
     * inclusive. The returned collection/list is backed by this tree, so changes in the 
     * returned list are reflected in this tree, and vice-versa (i.e., the two structures 
     * share elements. The returned collection should be organized according to the 
     * natural ordering of the elements (i.e., it should be sorted). This operation should 
     * be O(M) where M is the number of elements in the returned list.
     * 
     * @param fromElement - low endpoint (inclusive) of the returned collection
     * @param toElement - high endpoint (inclusive) of the returned collection
     * @return a collection containing a portion of this tree whose elements range from 
     * fromElement, inclusive, to toElement, inclusive. A BST ordered by the comparator 
     * is returned.
     * @throws NullPointerException - if fromElement or toElement is null
     * @throws IllegalArgumentException - if fromElement is greater than toElement
     */
    public BST<T> getRangeBST(T fromElement, T toElement) throws NullPointerException, 
    IllegalArgumentException {
    	if (fromElement == null || toElement == null)
    		throw new NullPointerException ("The parameters passed in cannot be null");
    	//fromElement cannot be > than toElement
    	BSTNode from = new BSTNode(fromElement);
    	BSTNode to = new BSTNode(toElement);
    	if (from.compareTo(to) > 0)
    		throw new IllegalArgumentException ("The lower endpoint cannot be greater than the "
    				+ "higher endpoint");
    	
    	//The BST to return
    	BST<T> tree = new BST<>(this.comparator);
    	
		return getRangeBST(from, to, root, tree);
    }

    /**
     * Continuation of {@link BST#getRangeBST(Comparable, Comparable)}. The parameters are 
     * validated. Recursive implementation!
     * 
     * @param fromElement - low endpoint (inclusive) of the returned collection
     * @param toElement - high endpoint (inclusive) of the returned collection
     * @return a collection containing a portion of this tree whose elements range from 
     * fromElement, inclusive, to toElement, inclusive. BST ordered according to {@code tree}
     * comparator is returned.
     */
    private BST<T> getRangeBST(BSTNode fromElement, BSTNode toElement, BSTNode root, 
    		BST<T> tree) {
    	if (root == null)
    		return tree;
    	
    	//To be included in the BST:
    	//	must be greater than or equal to 0.  
    	if (root.compareTo(fromElement) < 0)
    		//The root is too small to be included, so only right elements are possibly included
    		return getRangeBST(fromElement, toElement, root.right, tree);
    	//	must be lesser than or equal to 0.
    	if (root.compareTo(toElement) > 0) 
    		//The root is too big to be included, so only left elements are possibly included
    		return getRangeBST(fromElement, toElement, root.left, tree);
    	
    	//root is greater than or equal to fromElement and less than or equal to toElement:
    	//	both sides work, so perform preorder traversal to diminish chance of returning
    	//	a linkedlist-like tree
    	tree.add(root.data);
    	getRangeBST(fromElement, toElement, root.left, tree);
    	getRangeBST(fromElement, toElement, root.right, tree);
		return tree;
	}
    
    /**
     * Returns the first (lowest) element currently in this tree. This operation should be O(H).
     * 
     * @return the first (lowest) element currently in this tree
     * @throws NoSuchElementException - if this tree is empty
     */
    public T first() throws NoSuchElementException {
    	if (root == null)
    		throw new NoSuchElementException("The tree is empty.");
    	BSTNode first = root;
    	//Find the left-most element
    	while (first.left != null)
    		first = first.left;
		return first.data;	
    }
    
    /**
     * Returns the last (highest) element currently in this tree. This operation should be O(H).
     * 
     * @returns the  last (highest) element currently in this tree
     * @throws NoSuchElementException - if this tree is empty
     */
    public T last() throws NoSuchElementException {
    	if (root == null)
    		throw new NoSuchElementException("The tree is empty.");
    	BSTNode last = root;
    	//Find the right-most element
    	while (last.right != null)
    		last = last.right;
		return last.data;	
    }
    
    /**
     * Compares the specified object with this tree for equality. Returns true if the given
     * object is also a tree, the two trees have the same size, and the inorder traversal of 
     * the two trees returns the same nodes in the same order. This operation should be O(N).
     * 
     * @param obj - object to be compared for equality with this tree
     * @return true if the specified object is equal to this tree
     */
    @Override
    public boolean equals(Object obj) {
    	if (this == obj) return true;
    	if (obj == null) return false;
    	if (!(obj instanceof BST<?>)) return false;
    	
    	@SuppressWarnings("unchecked")
		BST<T> obj2 = (BST<T>) obj;
    	if (this.size() != obj2.size()) 
    		return false;
    	
    	Iterator<T> itr1 = this.iterator();
		Iterator<T> itr2 = obj2.iterator();
    	//Both iterators have the same number of elements since the BSTs have the same size
    	while (itr1.hasNext()) {
    		//Return false if the objects are not equal
    		if (!itr1.next().equals(itr2.next()))
    			return false;
    	}
    	return true;
    }

    /**
     * Returns a string representation of this tree. The string representation consists of a 
     * list of the tree's elements in the order they are returned by its iterator (inorder 
     * traversal), enclosed in square brackets ("[]"). Adjacent elements are separated by 
     * the characters ", " (comma and space). Elements are converted to strings as by 
     * String.valueOf(Object). This operation should be O(N).
     * 
     * @returns a string representation of this collection  
     */
    @Override
    public String toString() {
    	Iterator<T> itr = this.iterator();
    	String total = "[";
    	if (itr.hasNext())
    		total += String.valueOf(itr.next());
    	while (itr.hasNext()) 
    		total += ", " + String.valueOf(itr.next());
    	total += "]";
		return total;
    }
    
    /**
     * This function returns an array containing all the elements returned by this tree's 
     * iterator, in the same order, stored in consecutive elements of the array, starting with 
     * index 0. The length of the returned array is equal to the number of elements returned 
     * by the iterator. This operation should be O(N).
     * 
	 * @return an array, whose runtime component type is Object, containing all of the 
	 * elements in this tree
     */
    public Object[] toArray() {
    	Object[] arr = new Object[size];
    	Iterator<T> itr = this.iterator();
    	for (int i = 0; i < size; i++) 
    		arr[i] = itr.next();
		return arr;
    }
    
    
    /**
	 * Adds the specified element to this tree if it is not already present. 
	 * If this tree already contains the element, the call leaves the 
     * tree unchanged and returns false.
	 * @param data element to be added to this tree 
     * @return true if this tree did not already contain the specified element 
     * @throws NullPointerException if the specified element is null  
	 */
    public boolean add ( T data ) { 
         added = false; 
         if (data == null) return added; 
         //replace root with the reference to the tree after the new 
         //value is added
         root = add (data, root);
         //update the size and return the status accordingly 
         if (added) {
        	 size++; 
         }
         return added; 
    }
    
    /**
	 * Actual recursive implementation of add. 
     *
     * This function returns a reference to the subtree in which 
     * the new value was added. 
	 *
     * @param data element to be added to this tree 
     * @param node node at which the recursive call is made 
	 */
    private BSTNode add (T data, BSTNode node ) {
        if (node == null) {
            added = true; 
            return new BSTNode(data); 
        }
        //decide how comparisons should be done 
        int comp = 0 ;
        if (comparator == null ) //use natural ordering of the elements 
            comp = node.data.compareTo(data); 
        else                     //use the comparator 
            comp = comparator.compare(node.data, data ) ;
        
        //find the location to add the new value 
        if (comp > 0 ) { //add to the left subtree 
            node.left = add(data, node.left); 
        }
        else if (comp < 0 ) { //add to the right subtree
            node.right = add(data, node.right); 
        }
        else { //duplicate found, do not add 
            added = false; 
            return node; 
        }
        return node; 
    }
    
    /**
	 * Removes the specified element from this tree if it is present. 
	 * Returns true if this tree contained the element (or equivalently, 
     * if this tree changed as a result of the call). 
     * (This tree will not contain the element once the call returns.)
     * 
	 * @param target object to be removed from this tree, if present
     * @return true if this set contained the specified element 
     * @throws NullPointerException if the specified element is null  
	 */
	public boolean remove(T target) {
        //replace root with a reference to the tree after target was removed 
		root = recRemove(target, root);
        //update the size and return the status accordingly 
		if (found) size--; 
		return found;
	}


	/**
	 * Actual recursive implementation of remove method: find the node to remove.
     *
	 * This function recursively finds and eventually removes the node with the target element 
     * and returns the reference to the modified tree to the caller. 
     * 
	 * @param target object to be removed from this tree, if present
     * @param node node at which the recursive call is made 
	 */
	private BSTNode recRemove(T target, BSTNode node) {
		if (node == null)  { //value not found 
			found = false;
            return node; 
        }
        
        //decide how comparisons should be done 
        int comp = 0 ;
        if (comparator == null ) //use natural ordering of the elements 
            comp = target.compareTo(node.data); 
        else                     //use the comparator 
            comp = comparator.compare(target, node.data ) ;

        
		if (comp < 0)       // target might be in a left subtree 
			node.left = recRemove(target, node.left);
		else if (comp > 0)  // target might be in a right subtree 
			node.right = recRemove(target, node.right );
		else {          // target found, now remove it 
			node = removeNode(node);
			found = true;
		}
		return node;
	}

	/**
	 * Actual recursive implementation of remove method: perform the removal.
	 *
	 * @param target the item to be removed from this tree
	 * @return a reference to the node itself, or to the modified subtree
	 */
	private BSTNode removeNode(BSTNode node) {
		T data;
		if (node.left == null)   //handle the leaf and one child node with right subtree 
			return node.right ; 
		else if (node.right  == null)  //handle one child node with left subtree 
			return node.left;
		else {                   //handle nodes with two children 
			data = getPredecessor(node.left);
			node.data = data;
			node.left = recRemove(data, node.left);
			return node;
		}
	}

	/**
	 * Returns the information held in the rightmost node of subtree
	 *
	 * @param subtree root of the subtree within which to search for the rightmost node
	 * @return returns data stored in the rightmost node of subtree
	 */
	private T getPredecessor(BSTNode subtree) {
		if (subtree==null) //this should not happen 
            throw new NullPointerException("getPredecessor called with an empty subtree");
		BSTNode temp = subtree;
		while (temp.right  != null)
			temp = temp.right ;
		return temp.data;
	}


	/**
	 * Returns the number of elements in this tree.
	 * @return the number of elements in this tree
	 */
	public int size() {
		return size;
	}

	/**
	 * Create a tree representation as a string
	 * 
	 * @return a string that represents the tree
	 */
    public String toStringTree( ) {
        StringBuffer sb = new StringBuffer(); 
        toStringTree(sb, root, 0);
        return sb.toString();
    }
    
    /**
     * uses preorder traversal to display the tree 
     * WARNING: will not work if the data.toString returns more than one line 
     * 
     * @param sb - string to add the elements
     * @param node - the node to add to sb
     * @param level - level of the tree
     */
    private void toStringTree( StringBuffer sb, BSTNode node, int level ) {
        //display the node 
        if (level > 0 ) {
            for (int i = 0; i < level-1; i++) {
                sb.append("   ");
            }
            sb.append("|--");
        }
        if (node == null) {
            sb.append( "->\n"); 
            return;
        }
        else {
            sb.append( node.data + "\n"); 
        }

        //display the left subtree 
        toStringTree(sb, node.left, level+1); 
        //display the right subtree 
        toStringTree(sb, node.right, level+1); 
    }


    /**
     * Node class for this BST 
     * 
     * @author Joanna Klukowska
     */ 
    private class BSTNode implements Comparable < BSTNode > {

        T data;
        BSTNode  left;
        BSTNode  right;

        public BSTNode ( T data ) {
            this.data = data;
        }

        @SuppressWarnings("unused")
		public BSTNode (T data, BSTNode left, BSTNode right ) {
            this.data = data;
            this.left = left;
            this.right = right;
        }

        public int compareTo ( BSTNode other ) {
            if (BST.this.comparator == null )
                return this.data.compareTo ( other.data );
            else 
                return comparator.compare(this.data, other.data); 
        }
    }
}

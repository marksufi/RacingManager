package hippos.data.container;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 23, 2005
 * AlphaNumber: 8:12:29 PM
 * To change this template use Options | File Templates.
 */
public class AVLTree implements Map {
    AVLNode head = null;

    public int size() {
        return 0;
    }

    public void clear() {
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean containsKey(Object o) {
        return false;
    }

    public boolean containsValue(Object o) {
        return false;
    }

    public Collection values() {
        return null;
    }

    public void putAll(Map map) {
    }

    public Set entrySet() {
        return null;
    }

    public Set keySet() {
        return null;
    }

    public Object get(Object o) {
        AVLNode getNode = (AVLNode)o;
        return get(head, getNode);
    }

    public Object remove(Object o) {
        return null;
    }

    public Object put(Object key, Object value) {
        AVLNode newNode = new AVLNode((Comparable)key, value);

        System.out.println("insert key = " + key);
        head = put(head, newNode);
        return null;
    }

    private AVLNode put(AVLNode node, AVLNode newNode) {
        if(node != null) {
            switch(node.compareTo(newNode)) {
                case 1: return node.setLeft(put(node.getLeft(), newNode));
                case 0: return node;
                case -1:return node.setRight(put(node.getRight(), newNode));
            }
            //return node;
        }
        return newNode;
    }

    public AVLNode get(AVLNode node, AVLNode getNode) {
        if(node != null) {
            switch(node.compareTo(getNode)) {
                case 1: get(node.left, getNode);

                case 0: return node;
                case -1:get(node.right, getNode);
            }
        }
        return null;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        append(head, str);
        return str.toString();
    }

    private void append(AVLNode node, StringBuffer str) {
        if(node != null) {
            append(node.left, str);
            str.append(node + ", ");
            append(node.right, str);
        }
    }

    public static void main(String args[]) {
        AVLTree tree = new AVLTree();

        tree.put(new BigDecimal(10), new BigDecimal(1));
        System.out.println(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(9), new BigDecimal(1));
        System.out.println(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(8), new BigDecimal(1));
        System.out.println(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(7), new BigDecimal(1));
        System.out.println(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(5), new BigDecimal(1));
        System.out.println(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(6), new BigDecimal(1));
        System.out.print(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(4), new BigDecimal(1));
        System.out.print(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(15), new BigDecimal(1));
        System.out.print(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(20), new BigDecimal(1));
        System.out.print(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(30), new BigDecimal(1));
        System.out.print(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(25), new BigDecimal(1));
        System.out.print(tree.head + " -> " + tree.toString());
        tree.put(new BigDecimal(11), new BigDecimal(1));
        System.out.print(tree.head + " -> " + tree.toString());
        //tree.put(new BigDecimal(7), new BigDecimal(1));
        //System.out.print(tree.head + " -> " + tree.toString());
    }
}

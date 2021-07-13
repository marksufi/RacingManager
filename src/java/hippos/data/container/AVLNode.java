package hippos.data.container;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 23, 2005
 * AlphaNumber: 8:13:00 PM
 * To change this template use Options | File Templates.
 */
public class AVLNode implements Comparable {
    AVLNode left;
    AVLNode right;
    Comparable key;
    Object value;
    int balance;
    static int change = 0;

    public AVLNode(Comparable key, Object value) {
        this.key = key;
        this.value = value;
        balance = 0;
    }

    public String toString() {
        return "<" + key + "/" + balance + ">";
        //return "<" + key + "/" + balance + "/" + depth + ">";
        //return "<" + key + "/" + value + "(" + left + "<->" + right + ">";
    }

    public int compareTo(Object o) {
        AVLNode node = (AVLNode)o;
        return key.compareTo(node.key);
    }

    public AVLNode setLeft(AVLNode nextNode) {
        //System.out.println("AVLNode.setLeft...A(" + this + " -> " + nextNode + ")");
        if(left == null) {
            balance -= 1;
            change = balance;
        }
        else {
            balance -= Math.abs(AVLNode.change);
        }
        //System.out.println("AVLNode.setLeft...B(" + this + " -> " + nextNode + ")");
        left = nextNode;
        return chooseHead(nextNode);
    }

    public AVLNode setRight(AVLNode nextNode) {
        //System.out.println("AVLNode.setRight...A(" + this + "-> " + nextNode + ")");
        if(right == null) {
            balance += 1;
            change = balance;
        } else {
            balance += Math.abs(AVLNode.change);
        }
        //System.out.println("AVLNode.setRight...B(" + this + "-> " + nextNode + ")");
        right = nextNode;
        return chooseHead(nextNode);
    }

    private AVLNode chooseHead(AVLNode newNode) {
        if(newNode != null) {
            if(balance < -1) {
                switch(newNode.balance) {
                    case -1: return case1(newNode);
                    case 1: return case2(newNode);
                }
            } if(balance > 1) {
                switch(newNode.balance) {
                    case -1: return case3(newNode);
                    case 1: return case4(newNode);
                }
            }
        }
        return this;
    }

    /**
     * case -2 -> -1
     * @param nextNode
     * @return
     */
    private AVLNode case1(AVLNode nextNode) {
        //System.out.println("AVLNode.case1(" + this + " / " + nextNode);
        left = nextNode.right;
        nextNode.right = this;
        balance = 0;
        nextNode.balance = 0;
        change =0;
        //System.out.println("1..." + nextNode + "<" + nextNode.left + ", " + nextNode.right + ">");
        return nextNode;
    }

    /**
     * -2 -> 1
     * @param nextNode
     * @return
     */
    private AVLNode case2(AVLNode nextNode) {
        //System.out.println("AVLNode.case2(" + this + " / " + nextNode);
        nextNode = nextNode.right;
        left.right = null;
        nextNode.right = this;
        nextNode.left = left;
        nextNode.left.balance = 0;
        left = null;
        balance = 0;
        nextNode.balance = 0;
        change = 0;
        //System.out.println("2..." + nextNode + "<" + nextNode.left + ", " + nextNode.right + ">");
        return nextNode;
    }

    /**
     * 2 -> -1
     * @param nextNode
     * @return
     */
    private AVLNode case3(AVLNode nextNode) {
        //System.out.println("AVLNode.case3(" + this + " / " + nextNode);
        nextNode = nextNode.left;
        right.left = null;
        nextNode.left = this;
        nextNode.right = right;
        nextNode.right.balance = 0;
        right = null;
        balance = 0;
        nextNode.balance = 0;
        change = 0;
        //System.out.println("3..." + nextNode + "<" + nextNode.left + ", " + nextNode.right + ">");
        return nextNode;
    }

    /**
     *  2 -> 1
     * @param nextNode
     * @return
     */
    private AVLNode case4(AVLNode nextNode) {
        //System.out.println("AVLNode.case4(" + this + " / " + nextNode);
        right = nextNode.left;
        nextNode.left = this;
        balance = 0;
        nextNode.balance = 0;
        change = 0;
        //System.out.println("4..." + nextNode + "<" + nextNode.left + ", " + nextNode.right + ">");
        return nextNode;
    }

    public AVLNode getLeft() {
        return left;
    }

    public AVLNode getRight() {
        return right;
    }
}

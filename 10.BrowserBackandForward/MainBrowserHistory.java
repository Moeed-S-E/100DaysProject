class BrowserHistory {
    private class Node {
        String page;
        Node prev;
        Node next;

        public Node(String page) {
            this.page = page;
            this.next = null;
            this.prev = null;
        }
    }
    private Node current;
    private Node head;
    @SuppressWarnings("unused")
    private Node tail;

    public BrowserHistory() {
        this.head = null;
        this.tail = null;
        this.current = null;
    }

    public void visit(String page){
        Node newNode = new Node(page);

        if (current != null) {
            current.next = null;
        }

        if (head == null) {
            head = newNode;
            tail = newNode;
        }else{
            current.next = newNode;
            newNode.prev = current;
            tail = newNode;
        }

        current = newNode;
    }
    public void back() {
        if (current != null && current.prev != null) {
            current = current.prev;
            System.out.println("Back to: " + current.page);
        } else {
            System.out.println("No previous page.");
        }
    }
    public void forward() {
        if (current != null && current.next != null) {
            current = current.next;
            System.out.println("Forward to: " + current.page);
        } else {
            System.out.println("No next page.");
        }
    }
    public void currentPage() {
        if (current != null) {
            System.out.println("Currently viewing: " + current.page);
        } else {
            System.out.println("No pages visited yet.");
        }
    }
    public void displayHistory() {
        Node temp = head;
        System.out.println("**********************************");
        while (temp != null) {
            System.out.print(temp.page + " <-> ");
            temp = temp.next;
        }
        System.out.println("END");
        System.out.println("**********************************");
    }

}
public class MainBrowserHistory {
    public static void main(String[] args) {    
        BrowserHistory browserHistory = new BrowserHistory();
        
        browserHistory.visit("www.facebook.com");
        browserHistory.visit("www.x.com");
        browserHistory.visit("www.google.com");

        browserHistory.displayHistory();

        browserHistory.currentPage();

        browserHistory.back();
        browserHistory.back();

        browserHistory.currentPage();


    }
}
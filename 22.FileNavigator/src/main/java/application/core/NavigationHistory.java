package application.core;

import java.nio.file.Path;

public class NavigationHistory {
	private Node current;
	private class Node{
		Path path;
		Node prev,next;
		public Node(Path path) {
			this.path = path;
			this.next = null;
			this.prev = null;
		}
	}
	public void addPath(Path path) {
		Node newNode = new Node(path);
		if (current != null) {
			current.next = newNode;
			newNode.prev = current;
		}
		current = newNode;
	}
	public Path goBack() {
		if (current != null && current.prev != null) {
			current = current.prev;
			return current.path;
		}
		return null;
	}
	public Path goForward() {
		if (current != null && current.next != null) {
			current = current.next;
			return current.path;
		}
		return null;
	}
	
	public boolean canGoBack() {
		return current != null && current.prev != null;
	}
	public boolean canGoForward() {
		return current != null && current.next != null;
	}
}

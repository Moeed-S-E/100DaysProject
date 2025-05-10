package application.core;

import java.util.Stack;

public class UndoRedoManager {
	private Stack<FileOperation> undoStack =  new Stack<>();
	private Stack<FileOperation> redoStack =  new Stack<>();
	
	public void pushUndo(FileOperation operation) {
		undoStack.push(operation);
		redoStack.clear();
	}
	public void undo() {
		if (!undoStack.isEmpty()) {
			FileOperation operation = undoStack.pop();
			operation.undo();
			redoStack.push(operation);
		}
	}
	public void redo() {
		if (!redoStack.isEmpty()) {
			FileOperation operation = redoStack.pop();
			operation.redo();
			undoStack.push(operation);
		}
	}
	public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}

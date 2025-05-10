package application.core;

import java.nio.file.Path;

public class OpenOperation extends FileOperation{
	private NavigationHistory navigationHistory;

    public OpenOperation(Path targetPath, NavigationHistory navigationHistory) {
        super(targetPath);
        this.navigationHistory = navigationHistory;
    }

    @Override
	public
    void execute() {
    	System.out.println("Opened: " + targetPath);
        navigationHistory.addPath(targetPath);
    }

    @Override
	public
    void undo() {
        // No meaningful undo for opening a folder; could go back in history
        System.out.println("Undo open not implemented for: " + targetPath);
    }

    @Override
	public
    void redo() {
        // No meaningful redo for opening a folder; could re-add to history
        System.out.println("Redo open not implemented for: " + targetPath);
    }
}

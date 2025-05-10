package application.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RenameOperation extends FileOperation{
	private Path oldPath;
    private Path newPath;

    public RenameOperation(Path oldPath, Path newPath) {
        super(oldPath);
        this.oldPath = oldPath;
        this.newPath = newPath;
    }

    @Override
	public
    void execute() {
        try {
            Files.move(oldPath, newPath);
            System.out.println("Renamed: " + oldPath + " to " + newPath);
        } catch (IOException e) {
            System.err.println("Error renaming file: " + e.getMessage());
        }
    }

    @Override
	public
    void undo() {
        try {
            Files.move(newPath, oldPath);
            System.out.println("Undo rename: " + newPath + " to " + oldPath);
        } catch (IOException e) {
            System.err.println("Error undoing rename: " + e.getMessage());
        }
    }

    @Override
	public
    void redo() {
        try {
            Files.move(oldPath, newPath);
            System.out.println("Redo rename: " + oldPath + " to " + newPath);
        } catch (IOException e) {
            System.err.println("Error redoing rename: " + e.getMessage());
        }
    }
    
}

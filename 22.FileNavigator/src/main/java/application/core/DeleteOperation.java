package application.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteOperation extends FileOperation {
	private byte[] fileContent;
	
	public DeleteOperation(Path targetPath) {
		super(targetPath);
	}

	@Override
	public void undo() {
		try {
			Files.write(targetPath, fileContent);
			System.out.println("Restored: "+targetPath);
		} catch (IOException e) {
			System.err.println("Error restoring file: " + e.getMessage());
		}
	}

	@Override
	public void redo() {
		try {
            Files.delete(targetPath);
            System.out.println("Re-deleted: " + targetPath);
        } catch (IOException e) {
            System.err.println("Error re-deleting file: " + e.getMessage());
        }
	}

	@Override
	public void execute() {
		try {
            fileContent = Files.readAllBytes(targetPath);
            Files.delete(targetPath);
            System.out.println("Deleted: " + targetPath);
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
        }
	}

}

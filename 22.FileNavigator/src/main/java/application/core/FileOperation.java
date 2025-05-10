package application.core;

import java.nio.file.Path;

public abstract class FileOperation {
	protected Path targetPath;

    public FileOperation(Path targetPath) {
        this.targetPath = targetPath;
    }

	public abstract void undo();

	public abstract void redo();

	public abstract void execute();
    
}

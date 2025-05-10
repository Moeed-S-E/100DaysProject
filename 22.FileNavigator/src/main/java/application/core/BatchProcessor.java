package application.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BatchProcessor {
	private Queue<FileOperation> operationQueue = new ConcurrentLinkedQueue<>();
	public void addOperation(FileOperation operation) {
        operationQueue.add(operation);
    }
	public void processBatch() {
        while (!operationQueue.isEmpty()) {
            FileOperation operation = operationQueue.poll();
            operation.execute();
        }
    }
}

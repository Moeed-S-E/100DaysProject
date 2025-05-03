package app;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.json.JSONObject;

import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpeechRecognizer {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Recognizer recognizer;
    private TargetDataLine line;
    private final CommandListener listener;
    private boolean isMicrophonePaused = false;

    public SpeechRecognizer(CommandListener listener) {
        this.listener = listener;
    }

    public void start() {
        executor.submit(() -> {
            try {
                Model model = new Model("src/main/resources/model");
                recognizer = new Recognizer(model, 16000);

                AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();

                byte[] buffer = new byte[4096];
                while (!Thread.currentThread().isInterrupted()) {
                    synchronized (this) {
                        if (isMicrophonePaused) {
                            line.stop();
                            wait(); // Wait until resumed
                            line.start();
                        }
                    }
                    int bytesRead = line.read(buffer, 0, buffer.length);
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        String result = recognizer.getResult();
                        System.out.println("Raw JSON result: " + result); // Debug log
                        String command = extractCommand(result);
                        if (!command.isEmpty()) {
                            listener.onCommandReceived(command);
                        } else {
                            System.out.println("Empty or invalid command ignored.");
                            listener.onCommandReceived(""); // Notify empty command
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stop();
            }
        });
    }

    private String extractCommand(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String text = jsonObject.optString("text", "").trim();
            return text.isEmpty() ? "" : text;
        } catch (Exception e) {
            System.err.println("Failed to parse JSON: " + json + ", Error: " + e.getMessage());
            return "";
        }
    }

    public synchronized void pauseMicrophone() {
        isMicrophonePaused = true;
    }

    public synchronized void resumeMicrophone() {
        isMicrophonePaused = false;
        notify();
    }

    public void stop() {
        try {
            if (line != null) {
                line.stop();
                line.close();
            }
            if (recognizer != null) recognizer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdownNow();
    }

    public interface CommandListener {
        void onCommandReceived(String command);
    }
}
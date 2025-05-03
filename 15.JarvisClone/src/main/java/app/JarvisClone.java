package app;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.sound.sampled.*;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JarvisClone extends Application {
    private static final TextToSpeech tts = new TextToSpeech();
    private static final SpeechRecognizer recognizer = 
        new SpeechRecognizer(new SpeechRecognizer.CommandListener() {
            @Override
            public void onCommandReceived(String command) {
                handleCommand(command.toLowerCase().trim());
            }
        });
    private static FloatControl speakerControl;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UI.fxml"));
            Parent root = loader.load();

            // Find the circle node and animate it
            Circle circle = (Circle) root.lookup("#animatedCircle");
            if (circle != null) {
                animateCircle(circle);
            }

            // Initialize the main window
            Scene scene = new Scene(root);
            primaryStage.setTitle("Jarvis Clone");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(event -> {
                shutdown(); // Call shutdown to clean up resources
            });
            primaryStage.show();

            // Start the application logic
            initializeSpeakerControl();
            tts.setSpeechRecognizer(recognizer); // Pass recognizer to TTS for mic control
            recognizer.start();
            tts.speak("Jarvis Clone activated. Say 'exit' to quit.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void animateCircle(Circle circle) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), circle);
        scaleTransition.setFromX(0.5);
        scaleTransition.setFromY(0.5);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
    }

    private static void initializeSpeakerControl() {
        try {
            Mixer.Info[] mixers = AudioSystem.getMixerInfo();
            for (Mixer.Info mixerInfo : mixers) {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                Line.Info speakerInfo = new Line.Info(SourceDataLine.class);
                if (mixer.isLineSupported(speakerInfo)) {
                    Line line = mixer.getLine(speakerInfo);
                    line.open();
                    if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        speakerControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                        return;
                    }
                    line.close();
                }
            }
            System.out.println("Warning: Speaker volume control not available.");
        } catch (LineUnavailableException e) {
            System.out.println("Warning: Could not initialize speaker control: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
	private static void handleCommand(String command) {
        // Debug log
        System.out.println("Received command: '" + command + "'");

        // Mute speaker during command processing (mic is active)
        muteSpeaker(true);

        if (command.isEmpty()) {
            System.out.println("Command received: ");
            muteSpeaker(false);
            return;
        }

        if (command.contains("exit") || command.contains("bye") || command.contains("quit") || command.contains("bye bye")) {
            shutdown();
        } else if (command.contains("time")) {
        	String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
            System.out.println("Command received: time");
            tts.speak("The current time is " + time);
        } else if (command.contains("date")) {
			String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
			System.out.println("Command received: date (" + dateString + ")");
			tts.speak("The current date is: "+dateString);
		}else if (command.contains("chrome") || command.contains("google")) {
			try {
				Runtime.getRuntime().exec("cmd.exe /c start chrome www.google.com");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (command.contains("wikipedia")) {
			try {
				Runtime.getRuntime().exec("cmd.exe /c start chrome www.wikipedia.com");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (command.contains("youtube")) {
			try {
				Runtime.getRuntime().exec("cmd.exe /c start chrome www.youtube.com");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (command.contains("tiktok")) {
			try {
				Runtime.getRuntime().exec("cmd.exe /c start chrome www.tiktok.com");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (command.contains("instagram")) {
			try {
				Runtime.getRuntime().exec("cmd.exe /c start chrome www.instagram.com");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (command.contains("facebook")) {
			try {
				Runtime.getRuntime().exec("cmd.exe /c start chrome www.facebook.com");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (command.contains("github")) {
			try {
				Runtime.getRuntime().exec("cmd.exe /c start chrome www.github.com");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (command.contains("chat") || command.contains("check")) {
			try {
				Runtime.getRuntime().exec("cmd.exe /c start chrome https://web.whatsapp.com/");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        else {
            String response = "I heard: " + command;
            System.out.println("Command received: " + command);
            tts.speak(response);
        }
        
        // Unmute speaker after processing (mic will be paused by TTS)
        muteSpeaker(false);
    }

    private static void muteSpeaker(boolean mute) {
        if (speakerControl != null) {
            float value = mute ? speakerControl.getMinimum() : 0.0f; // Mute or restore to 0 dB
            speakerControl.setValue(value);
        }
    }

    private static void shutdown() {
        System.out.println("Shutting down...");
        recognizer.stop();
        tts.shutdown();
        System.exit(0);
    }
}

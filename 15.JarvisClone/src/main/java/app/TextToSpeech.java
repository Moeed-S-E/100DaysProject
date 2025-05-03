package app;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class TextToSpeech {
    private final Voice voice;
    private boolean isSpeaking = false;
    private SpeechRecognizer recognizer;

    public TextToSpeech() {
        System.setProperty("freetts.voices", 
            "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager vm = VoiceManager.getInstance();
        voice = vm.getVoice("kevin16");
        if (voice == null) {
            throw new RuntimeException("Could not initialize TTS engine");
        }
        voice.allocate();
    }

    public void setSpeechRecognizer(SpeechRecognizer recognizer) {
        this.recognizer = recognizer;
    }

    public synchronized void speak(String text) {
        if (isSpeaking) return;
        isSpeaking = true;

        // Pause microphone before speaking
        if (recognizer != null) {
            recognizer.pauseMicrophone();
        }

        new Thread(() -> {
            try {
                voice.speak(text);
            } finally {
                isSpeaking = false;
                // Resume microphone after speaking
                if (recognizer != null) {
                    recognizer.resumeMicrophone();
                }
            }
        }).start();
    }

    public void shutdown() {
        voice.deallocate();
    }
}
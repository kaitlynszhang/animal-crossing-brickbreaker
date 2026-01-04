import java.awt.*;
import javax.swing.*;
import java.util.HashMap;
import javax.sound.sampled.*;
import java.io.File;

public class BrickBreakerGame extends JPanel {
    // here we are managing the different screen we show
    private HashMap<String, GameScreen> screens;
    private String currentScreen;
    private String gameMode;
    private Clip backgroundMusic;
    
    // making an instance of gameplay screen
    private GamePlayScreen gamePlayScreen;
    
    public BrickBreakerGame() {
        playBackgroundMusic();
        setLayout(new CardLayout());
        screens = new HashMap<>();
        
        // initialize all the screens
        screens.put("home", new HomeScreen(this));
        screens.put("doorKnock", new DoorKnockScreen(this));
        screens.put("tomNookTalking", new TomNookTalkingScreen(this));
        screens.put("howToPlay", new HowToPlayScreen(this));
        screens.put("menu", new MenuScreen(this));
        
        // settings up actual gameplay
        gamePlayScreen = new GamePlayScreen(this);
        screens.put("game", gamePlayScreen);
        
        screens.put("options", new OptionsScreen(this));
        screens.put("endGame", new EndGameScreen(this));
        
        // adding all screens to a panel
        for (String key : screens.keySet()) {
            add(screens.get(key), key);
        }
        
        // start with homescreen
        currentScreen = "home";
        showScreen(currentScreen);
    }
    
    public void showScreen(String screenName) {
        if (screens.containsKey(screenName)) {
            // if resetting the game
            if (screenName.equals("game")) {
                gamePlayScreen.resetGame();
            }
            
            CardLayout cardLayout = (CardLayout) getLayout();
            cardLayout.show(this, screenName);
            currentScreen = screenName;
            screens.get(screenName).onActivate();
        }
    }
    
    public GameScreen getScreen(String screenName) {
        return screens.get(screenName);
    }
    
    /*
    public void setGameMode(String mode) {
        this.gameMode = mode;
        if (gamePlayScreen != null) {
            gamePlayScreen.setGameMode(mode);
        }
    }*/
    
    public String getGameMode() {
        return gameMode;
    }
    

    public void endGame(int score) {
    }
    
    private void playBackgroundMusic() {
        try {
            File musicFile = new File("BackgroundMusic.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = -20.0f;
            volumeControl.setValue(volume);
            backgroundMusic.start();
        } catch (Exception e) {
            System.out.println("error loading background music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void setMusicVolume(float volumeLevel) {
        if (backgroundMusic != null) {
            try {
                FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(volumeLevel);
            } catch (Exception e) {
                System.out.println("error setting volume: " + e.getMessage());
            }
        }
    }
    
    public void playClickSound() {
        try {
            File audioFile = new File("ClickSound.wav");
            if (audioFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            }
        } catch (Exception e) {
            System.out.println("error playing click sound: " + e.getMessage());
        }
    }
}
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.sound.sampled.*;
import java.io.File;

public class GamePlayScreen extends GameScreen {
    // constants for game elements
    private static final int GRID_ROWS = 3;
    private static final int GRID_COLS = 7;
    private static final double BRICK_WIDTH = 59.7;
    private static final double BRICK_HEIGHT = 29.9;
    private static final double PADDLE_WIDTH = 122.3;
    private static final double PADDLE_HEIGHT = 19.3;
    private static final double BALL_SIZE = 20.1;
    private static final double BRICK_SPACING_X = 15.0; // increased horizontal spacing
    private static final double BRICK_SPACING_Y = 10.0; // increased vertical spacing
    
    // debug mode flag
    private boolean showDebugBounds = false;
    
    // game images
    private ImageIcon backgroundImage;
    private ImageIcon heartImage;
    
    // game state variables
    private String gameMode;
    private int score = 0;
    private int lives = 3;
    private boolean gameStarted = false;
    private boolean paused = false;
    private boolean gameOver = false;
    private int currentWave = 1;
    
    // game timers
    private Timer gameTimer;
    private Timer animationTimer;
    private int timeRemaining = 60; // for timed mode
    
    // game objects
    private Paddle paddle;
    private Ball ball;
    private Brick[][] bricks;
    private ArrayList<PowerUp> fallingPowerUps;
    
    // fruit collection tracking
    private int applesCollected = 0;
    private int orangesCollected = 0;
    private int pearsCollected = 0;
    private int blueberriesCollected = 0;
    
    private Random random;
    
    public GamePlayScreen(BrickBreakerGame game) {
        super(game);
        
        try {
            backgroundImage = new ImageIcon("GamePlayScreen.png");
            heartImage = new ImageIcon("Heart.png");
        } catch (Exception e) {
            System.out.println("error loading game images: " + e.getMessage());
            e.printStackTrace();
            backgroundImage = null;
            heartImage = null;
        }
        
        random = new Random();
        fallingPowerUps = new ArrayList<>();
        
        // initialize game objects with exact dimensions
        int screenWidth = 600;
        int paddleX = (screenWidth - (int)PADDLE_WIDTH) / 2;
        int paddleY = 580;
        
        paddle = new Paddle(paddleX, paddleY, (int)PADDLE_WIDTH, (int)PADDLE_HEIGHT, screenWidth);
        ball = new Ball(paddleX + (int)(PADDLE_WIDTH/2), paddleY - (int)BALL_SIZE, (int)(BALL_SIZE/2));
        
        // create the array for bricks
        bricks = new Brick[GRID_ROWS][GRID_COLS];
        
        // set up game timers
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameStarted && !paused && !gameOver) {
                    if (gameMode.equals("timed")) {
                        timeRemaining--;
                        if (timeRemaining <= 0) {
                            gameTimer.stop();
                            endGame();
                        }
                    }
                }
            }
        });
        
        animationTimer = new Timer(1000/60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameStarted && !paused && !gameOver) {
                    updateGame();
                }
                repaint();
            }
        });
        
        // add key listener for game controls
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                
                // d key to toggle debug mode
                if (keyCode == KeyEvent.VK_D) {
                    showDebugBounds = !showDebugBounds;
                    System.out.println("debug bounds: " + (showDebugBounds ? "on" : "off"));
                    repaint();
                    return;
                }
                
                // press space to start game
                if (keyCode == KeyEvent.VK_SPACE && !gameStarted) {
                    startGame();
                    return;
                }
                
                // other key handling
                handleKeyPress(e);
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyRelease(e);
            }
        });
    }
    
    // method to play sound effects
    private void playSoundEffect(String filename) {
        try {
            File audioFile = new File(filename);
            if (audioFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } else {
                System.out.println("sound file not found: " + filename);
            }
        } catch (Exception e) {
            System.out.println("error playing sound: " + e.getMessage());
        }
    }
    
    private void startGame() {
        gameStarted = true;
        ball.launch();
    }
    
    @Override
    public void onActivate() {
        requestFocusInWindow();
        gameMode = game.getGameMode();
        resetGame();
        
        // start animation timer but game physics won't update until space is pressed
        animationTimer.start();
        // only using classic mode for now
        // if (gameMode.equals("timed")) {
        //     timeRemaining = 60;
        //     gameTimer.start();
        // }
    }
    
    public void resetGame() {
        score = 0;
        lives = 3;
        paused = false;
        gameOver = false;
        gameStarted = false;
        currentWave = 1;
        
        // reset fruit counts
        applesCollected = 0;
        orangesCollected = 0;
        pearsCollected = 0;
        blueberriesCollected = 0;
        
        // clear any power-ups
        fallingPowerUps.clear();
        
        // reset paddle position
        int screenWidth = getWidth();
        int paddleX = (screenWidth - (int)PADDLE_WIDTH) / 2;
        int paddleY = 550;
        paddle.reset(paddleX, paddleY);
        
        // reset ball position (on paddle)
        ball.reset(paddleX + (int)(PADDLE_WIDTH/2), paddleY - (int)BALL_SIZE);
        
        // initialize brick grid for the first wave
        setupBrickGrid();
        
        // make sure timer is stopped if previously running
        gameTimer.stop();
        
        repaint();
    }
    
    private void setupBrickGrid() {
        // calculate total grid width including spacing
        double totalGridWidth = GRID_COLS * BRICK_WIDTH + (GRID_COLS - 1) * BRICK_SPACING_X;
        int startX = (int)((getWidth() - totalGridWidth) / 2);
        int startY = 100; // top position for the grid
        
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int x = startX + (int)(col * (BRICK_WIDTH + BRICK_SPACING_X));
                int y = startY + (int)(row * (BRICK_HEIGHT + BRICK_SPACING_Y));
                
                // create a brick with probability based on current wave
                Brick brick = createBrickForWave(x, y, (int)BRICK_WIDTH, (int)BRICK_HEIGHT);
                bricks[row][col] = brick;
            }
        }
    }
    
    private Brick createBrickForWave(int x, int y, int width, int height) {
        // adjust probabilities based on wave number
        double rand = random.nextDouble();
        
        // as waves progress, increase probability of harder bricks
        double appleProb = Math.max(0.1, 0.5 - (currentWave * 0.05));
        double orangeProb = Math.min(0.4, 0.2 + (currentWave * 0.02));
        double pearProb = Math.min(0.2, 0.1 + (currentWave * 0.01));
        double blueberryProb = Math.min(0.2, 0.05 + (currentWave * 0.015));
        double heartProb = 0.05;
        double plusProb = 0.05;
        
        // create appropriate brick type
        if (rand < appleProb) {
            return new AppleBrick(x, y, width, height);
        } else if (rand < appleProb + orangeProb) {
            return new OrangeBrick(x, y, width, height);
        } else if (rand < appleProb + orangeProb + pearProb) {
            return new PearBrick(x, y, width, height);
        } else if (rand < appleProb + orangeProb + pearProb + blueberryProb) {
            return new BlueberryBrick(x, y, width, height);
        } else if (rand < appleProb + orangeProb + pearProb + blueberryProb + heartProb) {
            return new HeartBrick(x, y, width, height);
        } else if (rand < appleProb + orangeProb + pearProb + blueberryProb + heartProb + plusProb) {
            return new PlusBrick(x, y, width, height);
        } else {
            return new PeachBrick(x, y, width, height);
        }
    }
    
    private void updateGame() {
        // move the paddle
        paddle.move();
        
        // move the ball
        ball.move();
        
        // check ball collisions with walls
        checkWallCollisions();
        
        // check ball collision with paddle
        checkPaddleCollision();
        
        // check ball collision with bricks
        checkBrickCollisions();
        
        // check if ball is lost (falls below screen)
        if (ball.getY() > getHeight()) {
            handleBallLost();
        }
        
        // update falling power-ups
        updatePowerUps();
        
        // check if all bricks are broken for next wave
        checkWaveCompletion();
    }
    
    private void checkWallCollisions() {
        // left and right walls
        if (ball.getX() - ball.getRadius() <= 0 || 
            ball.getX() + ball.getRadius() >= getWidth()) {
            ball.reverseX();
        }
        
        // top wall
        if (ball.getY() - ball.getRadius() <= 0) {
            ball.reverseY();
        }
    }
    
    private void checkPaddleCollision() {
        if (ball.getBounds().intersects(paddle.getCollisionBounds())) {
            // calculate bounce angle based on where ball hit the paddle
            double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
            double ballDistFromCenter = ball.getX() - paddleCenter;
            double normalizedDist = ballDistFromCenter / (paddle.getWidth() / 2);
            
            // set new ball direction (angle based on hit position)
            double ballSpeed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
            double angle = normalizedDist * (Math.PI / 4); // max 45 degree bounce
            
            ball.setDx(ballSpeed * Math.sin(angle));
            ball.setDy(-ballSpeed * Math.cos(angle));
            
            // ensure ball is above the paddle to prevent multiple collisions
            double newY = paddle.getY() - ball.getRadius() - 1;
            ball.setPosition((int)ball.getX(), (int)newY);
        }
    }
    
    private void checkBrickCollisions() {
        // check each brick
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                Brick brick = bricks[row][col];
                
                // skip broken bricks
                if (brick != null && !brick.isBroken()) {
                    if (ball.getBounds().intersects(brick.getBounds())) {
                        // handle brick hit
                        handleBrickHit(brick);
                        
                        // determine bounce direction (simplified)
                        Rectangle brickBounds = brick.getBounds();
                        Rectangle ballBounds = ball.getBounds();
                        
                        // calculate intersection to determine collision side
                        Rectangle intersection = ballBounds.intersection(brickBounds);
                        
                        if (intersection.width > intersection.height) {
                            // top or bottom collision
                            ball.reverseY();
                        } else {
                            // left or right collision
                            ball.reverseX();
                        }
                        
                        // only hit one brick per frame for simplicity
                        return;
                    }
                }
            }
        }
    }
    
    private void handleBrickHit(Brick brick) {
        String fruitType = brick.getFruitType();
        
        // play sound based on brick type
        if (fruitType.equals("peach")) {
            playSoundEffect("PeachSound.wav");
        } else {
            playSoundEffect("BrickSound.wav");
        }
        
        brick.hit();
        
        if (brick.isBroken()) {
            // add points based on brick type
            score += brick.getPoints();
            
            // check if this brick should drop a power-up or fruit (20% chance)
            if (brick.shouldDropPowerUp()) {
                // calculate center of brick for power-up spawn
                int powerUpX = brick.getX() + brick.getWidth() / 2 - 15; // center and adjust for power-up width
                int powerUpY = brick.getY() + brick.getHeight() / 2 - 15; // center and adjust for power-up height
                
                // create appropriate power-up based on brick type
                if (fruitType.equals("heart")) {
                    // heart brick guarantees a heart power-up
                    fallingPowerUps.add(new PowerUp(powerUpX, powerUpY, 30, 30, PowerUp.EXTRA_LIFE));
                } else if (fruitType.equals("plus")) {
                    // plus brick guarantees a mega basket power-up
                    fallingPowerUps.add(new PowerUp(powerUpX, powerUpY, 30, 30, PowerUp.MEGA_BASKET));
                } else if (!fruitType.equals("peach")) {
                    // all other fruit bricks (except peach) drop their corresponding fruit
                    fallingPowerUps.add(new PowerUp(powerUpX, powerUpY, 30, 30, fruitType));
                }
            }
        }
    }
    
    private void handlePowerUpCollected(PowerUp powerUp) {
        String type = powerUp.getType();
        
        switch (type) {
            case PowerUp.EXTRA_LIFE:
                if (lives < 3) {
                    lives++;
                }
                playSoundEffect("HeartSound.wav");
                break;
                
            case PowerUp.MEGA_BASKET:
                paddle.activateMegaBasket(600);
                playSoundEffect("MegaBasketSound.wav");
                break;
                
            case "apple":
                applesCollected++;
                score += 10;
                playSoundEffect("FruitSound.wav");
                break;
                
            case "orange":
                orangesCollected++;
                score += 20;
                playSoundEffect("FruitSound.wav");
                break;
                
            case "pear":
                pearsCollected++;
                score += 30;
                playSoundEffect("FruitSound.wav");
                break;
                
            case "blueberry":
                blueberriesCollected++;
                score += 30;
                playSoundEffect("FruitSound.wav");
                break;
                
            case "heart":
                if (lives < 3) {
                    lives++;
                }
                playSoundEffect("HeartSound.wav");
                break;
                
            case "plus":
                paddle.activateMegaBasket(600);
                playSoundEffect("MegaBasketSound.wav");
                break;
        }
    }
    
    private void updatePowerUps() {
        Iterator<PowerUp> iterator = fallingPowerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            powerUp.move();
            
            // check if power-up is collected - use the collection bounds instead of full bounds
            if (powerUp.getBounds().intersects(paddle.getCollectionBounds())) {
                handlePowerUpCollected(powerUp);
                iterator.remove();
            }
            // check if power-up falls off screen
            else if (powerUp.getY() > getHeight()) {
                iterator.remove();
            }
        }
    }
    
    private void handleBallLost() {
        lives--;
        
        if (lives <= 0) {
            endGame();
        } else {
            int paddleX = paddle.getX();
            ball.reset(paddleX + paddle.getWidth()/2, paddle.getY() - (int)BALL_SIZE);
            gameStarted = false;
        }
    }
    
    private void checkWaveCompletion() {
        boolean allBroken = true;
        
        // checking if any bricks still exist
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                Brick brick = bricks[row][col];
                if (brick != null && !brick.isBroken()) {
                    allBroken = false;
                    break;
                }
            }
        }
        
        if (allBroken) {
            // next wave starts
            startNextWave();
        }
    }
    
    private void startNextWave() {
        currentWave++;
        
        // resetting the ball and wait for user to pres the space bar
        int paddleX = paddle.getX();
        ball.reset(paddleX + paddle.getWidth()/2, paddle.getY() - (int)BALL_SIZE);
        gameStarted = false;
        
        // new bricks
        setupBrickGrid();
        
        // level up msg
        displayLevelUpMessage();
    }
    
    private void displayLevelUpMessage() {
        final JLabel levelUpLabel = new JLabel("Wave " + currentWave + "!");
        levelUpLabel.setFont(new Font("Arial", Font.BOLD, 36));
        levelUpLabel.setForeground(Color.YELLOW);
        levelUpLabel.setBackground(new Color(0, 0, 0, 150));
        levelUpLabel.setOpaque(true);
        levelUpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        levelUpLabel.setBounds(getWidth()/2 - 100, getHeight()/2 - 50, 200, 100);
        
        add(levelUpLabel);
        
        Timer messageTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(levelUpLabel);
                repaint();
            }
        });
        messageTimer.setRepeats(false);
        messageTimer.start();
    }
    
    private void endGame() {
        gameOver = true;
        animationTimer.stop();
        gameTimer.stop();
        
        // go to end game screen
        EndGameScreen endScreen = (EndGameScreen) game.getScreen("endGame");
        endScreen.setFinalScore(score);
        endScreen.setFruitStats(applesCollected, orangesCollected, pearsCollected, blueberriesCollected);
        game.showScreen("endGame");
    }
    
    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (keyCode == KeyEvent.VK_LEFT) {
            paddle.setMovingLeft(true);
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            paddle.setMovingRight(true);
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            paused = !paused;
        }
    }
    
    private void handleKeyRelease(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (keyCode == KeyEvent.VK_LEFT) {
            paddle.setMovingLeft(false);
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            paddle.setMovingRight(false);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // drawing all neccesary components
        if (backgroundImage != null) {
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }        
        drawGameInfo(g);
        
        drawBricks(g);
        
        paddle.draw(g);
        
        ball.draw(g);
        
        for (PowerUp powerUp : fallingPowerUps) {
            powerUp.draw(g);
        }
        
        if (!gameStarted && !gameOver) {
            drawStartMessage(g);
        }
        
        if (paused) {
            drawPauseScreen(g);
        }
        
        // draw debug outlines if debug mode is enabled
        if (showDebugBounds) {
            drawDebugBounds(g);
        }
    }
    
    // debug method to visualize collision areas
    private void drawDebugBounds(Graphics g) {
        // display debug information at top of screen
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("debug mode on", 20, 20);
        
        // draw ball bounds
        g.setColor(Color.RED);
        Rectangle ballBounds = ball.getBounds();
        g.drawRect(ballBounds.x, ballBounds.y, ballBounds.width, ballBounds.height);
        
        // draw paddle collision bounds
        g.setColor(Color.GREEN);
        Rectangle paddleCollisionBounds = paddle.getCollisionBounds();
        g.drawRect(paddleCollisionBounds.x, paddleCollisionBounds.y,
                 paddleCollisionBounds.width, paddleCollisionBounds.height);
        
        // draw paddle collection bounds
        g.setColor(Color.BLUE);
        Rectangle paddleCollectionBounds = paddle.getCollectionBounds();
        g.drawRect(paddleCollectionBounds.x, paddleCollisionBounds.y,
                 paddleCollectionBounds.width, paddleCollectionBounds.height);
        
        // draw full paddle bounds
        g.setColor(Color.YELLOW);
        Rectangle paddleBounds = paddle.getBounds();
        g.drawString("paddle", paddleBounds.x, paddleBounds.y - 5);
        g.drawRect(paddleBounds.x, paddleBounds.y,
                 paddleBounds.width, paddleBounds.height);
        
        // draw brick bounds and hit counts
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                Brick brick = bricks[row][col];
                if (brick != null && !brick.isBroken()) {
                    g.setColor(Color.MAGENTA);
                    Rectangle brickBounds = brick.getBounds();
                    g.drawRect(brickBounds.x, brickBounds.y,
                             brickBounds.width, brickBounds.height);
                    
                    // display hits taken/required
                    g.setFont(new Font("Arial", Font.BOLD, 12));
                    g.setColor(Color.WHITE);
                    String hits = "hits: " + brick.getCurrentHits() + "/" + brick.getHitsRequired();
                    g.drawString(hits, brickBounds.x + 5, brickBounds.y + brickBounds.height - 5);
                }
            }
        }
        
        // draw power-up bounds
        for (PowerUp powerUp : fallingPowerUps) {
            g.setColor(Color.CYAN);
            Rectangle powerUpBounds = powerUp.getBounds();
            g.drawRect(powerUpBounds.x, powerUpBounds.y,
                     powerUpBounds.width, powerUpBounds.height);
            
            // display power-up type
            g.setColor(Color.WHITE);
            g.drawString(powerUp.getType(), powerUpBounds.x, powerUpBounds.y - 5);
        }
        
        // display game stats in debug mode
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("score: " + score, 20, 40);
        g.drawString("lives: " + lives, 20, 55);
        g.drawString("wave: " + currentWave, 20, 70);
        g.drawString("game started: " + gameStarted, 20, 85);
        g.drawString("paused: " + paused, 20, 100);
        
        // display ball velocity
        g.drawString(String.format("ball velocity: dx=%.2f, dy=%.2f", ball.getDx(), ball.getDy()), 
                    20, 115);
    }
    
    private void drawBricks(Graphics g) {
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                Brick brick = bricks[row][col];
                if (brick != null && !brick.isBroken()) {
                    brick.draw(g);
                }
            }
        }
    }
    
    private void drawGameInfo(Graphics g) {
        // hearts for the top left
        if (heartImage != null) {
            int heartSpacing = 30;
            for (int i = 0; i < lives; i++) {
                g.drawImage(heartImage.getImage(), 10 + (i * heartSpacing), 10, 25, 25, this);
            }
        }
        
        // draw score in the middle top
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        String scoreText = "" + score;
        FontMetrics fm = g.getFontMetrics();
        int scoreWidth = fm.stringWidth(scoreText);
        g.drawString(scoreText, (getWidth() - scoreWidth) / 2 + 20, 45);
    }
    
    private void drawStartMessage(Graphics g) {
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // start next wave
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String startText = "Press SPACE to Start";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(startText);
        g.drawString(startText, (getWidth() - textWidth) / 2, getHeight() / 2);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        String waveText = "Wave " + currentWave;
        textWidth = fm.stringWidth(waveText);
        g.drawString(waveText, (getWidth() - textWidth) / 2, getHeight() / 2 + 50);
    }
    
    private void drawPauseScreen(Graphics g) {
        // pausing the game
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics fm = g.getFontMetrics();
        String pauseText = "PAUSED";
        int textWidth = fm.stringWidth(pauseText);
        g.drawString(pauseText, (getWidth() - textWidth) / 2, getHeight() / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String instructions = "Press ESC to resume";
        textWidth = fm.stringWidth(instructions);
        g.drawString(instructions, (getWidth() - textWidth) / 2, getHeight() / 2 + 40);
    }
}
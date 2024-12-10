package com.mycompany.batfeet;

// Import necessary libraries for GUI, event handling, and utilities
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javax.sound.sampled.*;
import javax.swing.*;

// Class representing the Bat (player)
class bat {
    Image fly; // Image of the bat
    int heigth = 199 / 5, width = 323 / 5; // Size of the bat image
    int x = 800 / 8 - 100, y = 500 / 2;   // Initial position of the bat

    // Constructor to initialize the bat's image
    bat(Image fly) {
        this.fly = fly;
    }

    // Method to get the Y-coordinate of the bat
    int getY() {
        return this.y;
    }
}

// Class representing obstacles (buildings)
class Building {
    Random rand = new Random(); // For generating random positions of obstacles
    Image building; // Image of the building
    boolean passed = false; // Flag to indicate if the building is passed
    int heigth = 600, width = 150; // Dimensions of the building
    int x = 350, y = rand.nextInt(-400, 0); // Initial position of the building

    // Constructor to initialize the building's image
    Building(Image building) {
        this.building = building;
    }
}

// Main game class extending JPanel for GUI rendering and implementing listeners
class FlappyBat extends JPanel implements ActionListener, KeyListener {
    private Image background; // Background image
    private Timer gameframes, Obstacles; // Timers for game updates and obstacles
    private Image bird, buildingtop, buildingdown; // Images for the bat and buildings
    private AudioInputStream flappy, smash, point, bonus; // Audio streams
    private Clip player, Point, hit, Bonus; // Audio clips
    private final bat Bat; // Instance of the bat
    private Queue<Building> up_buildings = new LinkedList<>(); // Queue for obstacles
    private float Gravity = 0.4f; // Gravity effect on the bat
    private float BatY; // Bat's vertical position
    private float vilocity = 0f; // Bat's velocity
    private boolean End_Game; // Game over flag
    private int score, time = 5000, levelup = 0; // Game state variables

    // Constructor for initializing the game
    public FlappyBat() {
        this.setPreferredSize(new Dimension(350, 622));

        // Load images
        background = new ImageIcon(getClass().getResource("/Images/back.png")).getImage();
        bird = new ImageIcon(getClass().getResource("/Images/bat.png")).getImage();
        buildingtop = new ImageIcon(getClass().getResource("/Images/Up.png")).getImage();
        buildingdown = new ImageIcon(getClass().getResource("/Images/Down.png")).getImage();

        try {
            // Load audio files
            flappy = AudioSystem.getAudioInputStream(getClass().getResource("/Sounds/Flappy.wav"));
            smash = AudioSystem.getAudioInputStream(getClass().getResource("/Sounds/Smash.wav"));
            bonus = AudioSystem.getAudioInputStream(getClass().getResource("/Sounds/Bonus.wav"));
            point = AudioSystem.getAudioInputStream(getClass().getResource("/Sounds/Point.wav"));

            // Initialize audio clips
            player = AudioSystem.getClip();
            hit = AudioSystem.getClip();
            Point = AudioSystem.getClip();
            Bonus = AudioSystem.getClip();

            player.open(flappy);
            hit.open(smash);
            Point.open(point);
            Bonus.open(bonus);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            // Handle exceptions (audio loading issues)
        }

        // Initialize the bat instance
        Bat = new bat(bird);
        BatY = Bat.getY();

        // Add the first obstacle
        up_buildings.offer(new Building(buildingtop));

        // Configure key listener and timers
        addKeyListener(this); //make the object recieve action
        setFocusable(true);   //set the focus true for our flappybird panel 
        gameframes = new Timer(1000 / 60, this); // Game update timer
        Obstacles = new Timer(time, this); // Obstacle generation timer
        Obstacles.start();
        gameframes.start();
    }

    // Method to paint the game components
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Method to draw the game elements
    void draw(Graphics g) {
        g.drawImage(background, 0, 0, 350, 622, null);

        // Draw obstacles
        for (Building B : up_buildings) {
            g.drawImage(B.building, B.x, B.y, B.width, B.heigth, null);
            g.drawImage(buildingdown, B.x, B.heigth + B.y - 90, B.width, B.heigth, null);
        }

        // Draw the bat
        g.drawImage(Bat.fly, Bat.x, (int) BatY, Bat.width, Bat.heigth, null);

        // Display score
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Score: " + score, 100, 50);

        // Display bonus message
        if (score == 10 || score == 32 || score == 80 || score == 180) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("+BONUS x2", 20, 150);
        }

        // Display game over message
        if (End_Game) {
            g.setColor(Color.MAGENTA);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game over", 20, 300);
            g.setColor(Color.CYAN);
            g.drawString("Score: " + score, 20, 400);
        }

        // Remove old obstacles
        if (up_buildings.size() == 4) {
            up_buildings.poll();
        }
    }

    // Method to handle movement logic
    void move() {
        vilocity += Gravity;
        BatY += vilocity;
        BatY = Math.min(BatY, 622 - Bat.heigth);

        for (Building B : up_buildings) {
            B.x -= (2 + levelup);
        }
    }

    // Method to check for game over conditions
    boolean GameOver(Building B) {
        if (BatY == 622 - Bat.heigth) {
            return true;
        }

        // Collision detection logic
        for (float y = BatY; y < BatY + Bat.heigth; y++) {
            if (y <= B.heigth + B.y - 135 || y >= B.heigth + B.y + 45) {
                if (y <= BatY + 57 / 5) {
                    for (int x = Bat.x; x < Bat.x + Bat.width; x++) {
                        if (x >= B.x && x <= B.x + B.width - 20) {
                            return true;
                        }
                    }
                } else if (y <= BatY + 122 / 5) {
                    for (int x = Bat.x + 40 / 5; x <= Bat.x + Bat.width - 30 / 5; x++) {
                        if (x >= B.x && x <= B.x + B.width - 20) {
                            return true;
                        }
                    }
                } else {
                    for (int x = Bat.x + 90 / 5; x <= Bat.x + Bat.width - 80 / 5; x++) {
                        if (x >= B.x && x <= B.x + B.width - 20) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Method to update score
    void score() {
        for (Building B : up_buildings) {
            if (B.x + B.width < Bat.x && !B.passed) {
                Point.stop();
                Point.setFramePosition(0);
                Point.start();
                B.passed = true;
                score++;
            }
        }
    }
    void Leveling(){
        switch (score){
            case 5:
                System.out.println("leveling up");
                levelup+=1;
                time-=2000;
                Obstacles.stop();
                Obstacles = new Timer(time,this);
                Obstacles.start();
                score*=2;
                 Bonus.stop();
            Bonus.setFramePosition(0);
            Bonus.start();
                break;
            case 16:
                System.out.println("leveling up");
                levelup+=2;
                time-=500;
                Obstacles.stop();
                Obstacles = new Timer(time,this);
                Obstacles.start();
                score*=2;
                 Bonus.stop();
            Bonus.setFramePosition(0);
            Bonus.start();
                break;
            case 40:
                System.out.println("leveling up");
                levelup+=1;
                score*=2;
                 Bonus.stop();
            Bonus.setFramePosition(0);
            Bonus.start();
                break;
            case 90:
                System.out.println("leveling up");
                levelup+=1;
                score*=2;
                 Bonus.stop();
            Bonus.setFramePosition(0);
            Bonus.start();
                break;
        }
    }
    // Timer actionlistner
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gameframes){
        this.move();
        this.score();
        this.Leveling();
        this.repaint();
        
        //reset everything if the player lose.to be ready to repeat again
        for(Building B : up_buildings ){
            if(GameOver(B)){
                hit.stop();
                hit.setFramePosition(0);
                hit.start();
               gameframes.stop();
               Obstacles.stop();
               End_Game=true;
               break;
            }
        }
        
        }
        //creat obstacle if the source of action was the timer of the obstacles
        if (e.getSource()==Obstacles){
            up_buildings.offer(new Building(buildingtop));
        }
         }
   

    @Override
    //if the player hit space bar or any button
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == 32){  //32 stand space bar
            vilocity=-5-levelup/4;
                 player.stop();
                 player.setFramePosition(0);
                 player.start();             // Play the sound
            if(End_Game){
                up_buildings.clear();
                BatY=Bat.y;
                vilocity=0;
                time=5000;
                score=0;
                levelup=0;
                up_buildings.offer(new Building(buildingtop));
                End_Game=false;
                Obstacles = new Timer(time,this);
                Obstacles.start();
                gameframes.start();
                
            }
    }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public void keyReleased(KeyEvent e) {
        //we dont need this
    }

    @Override
    public void keyTyped(KeyEvent e) {
      // we dont need this  
    }
 }
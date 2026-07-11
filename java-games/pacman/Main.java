import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// A simple Pac-Man game for a second-year college project.
public class GamePanel extends JPanel implements KeyListener, ActionListener {

    // 1 = Wall, 0 = Food/Empty, 2 = Eaten Food
    int[][] maze = {
        {1,1,1,1,1,1,1},
        {1,0,0,0,0,0,1},
        {1,0,1,1,0,0,1},
        {1,0,0,0,0,0,1},
        {1,1,1,1,1,1,1}
    };

    // Player (Pac-Man) coordinates
    int playerX = 1;
    int playerY = 1;
    
    // Ghost coordinates
    int ghostX = 5;
    int ghostY = 3;
    
    int score = 0;
    int tileSize = 40; // Size of each block in the maze
    
    boolean isGameOver = false;
    boolean isGameWon = false;
    Timer gameTimer; // Timer to control the ghost's speed

    // Constructor sets up the panel and starts the game loop
    public GamePanel() {
        addKeyListener(this);
        setFocusable(true);
        
        // Start the game loop (ghost moves every 500 milliseconds)
        gameTimer = new Timer(500, this);
        gameTimer.start();
    }

    // This method is responsible for drawing everything on the screen
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Draw the Maze
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[row].length; col++) {
                
                if (maze[row][col] == 1) {
                    // Draw Walls (Blue squares)
                    g.setColor(Color.BLUE);
                    g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                } else {
                    // Draw Background (Black squares)
                    g.setColor(Color.BLACK);
                    g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);

                    // Draw Food (White circles)
                    if (maze[row][col] == 0) {
                        g.setColor(Color.WHITE);
                        g.fillOval(col * tileSize + 15, row * tileSize + 15, 10, 10);
                    }
                }
            }
        }

        // 2. Draw Pac-Man
        g.setColor(Color.YELLOW);
        g.fillOval(playerX * tileSize + 5, playerY * tileSize + 5, 30, 30);

        // 3. Draw Ghost or Game Over text
        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", 40, 120);
        } else if (isGameWon) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("YOU WIN!", 60, 120);
        } else {
            g.setColor(Color.PINK);
            g.fillOval(ghostX * tileSize + 5, ghostY * tileSize + 5, 30, 30);
        }

        // 4. Draw Score
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        g.drawString("Score: " + score, 10, 220);
    }

    // Method to handle moving Pac-Man based on keyboard input
    public void movePlayer(int dx, int dy) {
        if (isGameOver || isGameWon) return; // Stop moving if the game is over

        int nextX = playerX + dx;
        int nextY = playerY + dy;

        // Check if the next position is NOT a wall
        if (maze[nextY][nextX] != 1) {
            playerX = nextX;
            playerY = nextY;

            // Check if Pac-Man eats food
            if (maze[playerY][playerX] == 0) {
                maze[playerY][playerX] = 2; // Mark food as eaten
                score += 10;
                checkWin(); // Check if all food is eaten
            }
        }

        checkCollision();
        repaint(); // Refresh the screen
    }

    // This method is called by the Timer every 500ms
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameOver || isGameWon) return;

        moveGhost();
        checkCollision();
        repaint();
    }

    // Basic AI for the Ghost to follow Pac-Man
    public void moveGhost() {
        int dx = 0;
        int dy = 0;
        
        // Try to move towards the player's X or Y position
        if (ghostX < playerX && maze[ghostY][ghostX + 1] != 1) dx = 1;
        else if (ghostX > playerX && maze[ghostY][ghostX - 1] != 1) dx = -1;
        else if (ghostY < playerY && maze[ghostY + 1][ghostX] != 1) dy = 1;
        else if (ghostY > playerY && maze[ghostY - 1][ghostX] != 1) dy = -1;

        // If the ghost is stuck, pick a random direction
        if (dx == 0 && dy == 0) {
            int randomDirection = (int)(Math.random() * 4);
            if (randomDirection == 0 && maze[ghostY][ghostX + 1] != 1) dx = 1;
            else if (randomDirection == 1 && maze[ghostY][ghostX - 1] != 1) dx = -1;
            else if (randomDirection == 2 && maze[ghostY + 1][ghostX] != 1) dy = 1;
            else if (randomDirection == 3 && maze[ghostY - 1][ghostX] != 1) dy = -1;
        }

        ghostX += dx;
        ghostY += dy;
    }

    // Check if Pac-Man and the Ghost are on the same tile
    public void checkCollision() {
        if (playerX == ghostX && playerY == ghostY) {
            isGameOver = true;
        }
    }

    // Check if Pac-Man has eaten all the food
    public void checkWin() {
        boolean foodLeft = false;
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[row].length; col++) {
                if (maze[row][col] == 0) {
                    foodLeft = true;
                    break;
                }
            }
        }
        if (!foodLeft) {
            isGameWon = true;
        }
    }

    // KeyListener methods for arrow keys
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_LEFT) {
            movePlayer(-1, 0);
        } else if (key == KeyEvent.VK_RIGHT) {
            movePlayer(1, 0);
        } else if (key == KeyEvent.VK_UP) {
            movePlayer(0, -1);
        } else if (key == KeyEvent.VK_DOWN) {
            movePlayer(0, 1);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
}

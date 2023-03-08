import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Pong extends Frame implements KeyListener, Runnable {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 10;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_RADIUS = 10;
    private static final int BALL_SPEED = 5;

    private int player1Score = 0;
    private int player2Score = 0;

    private Rectangle paddlel;
    private Rectangle paddler;
    private Rectangle ball;

    private double dx;
    private double dy;

    private boolean gameStarted = false;

    public Pong() {
        // Set up game objects
        paddlel = new Rectangle(0, HEIGHT / 2 - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
        paddler = new Rectangle(WIDTH - PADDLE_WIDTH, HEIGHT / 2 - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
        ball = new Rectangle(WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
        resetBall();

        // Set up game window
        setTitle("Pong");
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        addKeyListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Exit the application when the window is closed
                System.exit(0);
            }
        });
        setVisible(true);
    }

    public void paint(Graphics g) {
        // Set color to black
        g.setColor(Color.BLACK);

        // Draw background
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Set color to white
        g.setColor(Color.WHITE);

        // Draw paddles
        g.fillRect(paddlel.x, paddlel.y, paddlel.width, paddlel.height);
        g.fillRect(paddler.x, paddler.y, paddler.width, paddler.height);

        // Draw ball
        g.fillOval(ball.x, ball.y, ball.width, ball.height);

        // Set font to "bold arial"
        g.setFont(new Font("Arial", Font.BOLD, 32));

        // Draw scoreboard
        g.drawString(player1Score + " - " + player2Score, WIDTH / 2 - 50, 50);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W && paddlel.y > 0) {
            paddlel.y -= 20;
        } else if (e.getKeyCode() == KeyEvent.VK_S && paddlel.y + PADDLE_HEIGHT < HEIGHT) {
            paddlel.y += 20;
        } else if (e.getKeyCode() == KeyEvent.VK_UP && paddler.y > 0) {
            paddler.y -= 20;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && paddler.y + PADDLE_HEIGHT < HEIGHT) {
            paddler.y += 20;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameStarted) {
            gameStarted = true;
            Thread t = new Thread(this);
            t.start();
        } else if (e.getKeyCode() == KeyEvent.VK_Q) {
            System.exit(0);
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void run() {
        while (gameStarted) {
            moveBall();

            // ugh
            if (ball.intersects(paddlel)) {
                Random random = new Random();
                int angle = random.nextInt(30) + 20;
                dx = Math.cos(Math.toRadians(angle));
                dy = Math.sin(Math.toRadians(angle));
                if (random.nextBoolean()) {
                    dy = -dy;
                }
                dx = Math.abs(dx);
            } else if (ball.intersects(paddler)) {
                Random random = new Random();
                int angle = random.nextInt(30) + 20;
                dx = Math.cos(Math.toRadians(angle));
                dy = Math.sin(Math.toRadians(angle));
                if (random.nextBoolean()) {
                    dy = -dy;
                }
                dx = -Math.abs(dx);
            }

            if (paddlel.y < 0) {
                paddlel.y = 0;
            } else if (paddlel.y + PADDLE_HEIGHT > HEIGHT) {
                paddlel.y = HEIGHT - PADDLE_HEIGHT;
            }

            if (paddler.y < 0) {
                paddler.y = 0;
            } else if (paddler.y + PADDLE_HEIGHT > HEIGHT) {
                paddler.y = HEIGHT - PADDLE_HEIGHT;
            }

            repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    private void moveBall() {
        ball.x += dx * BALL_SPEED;
        ball.y += dy * BALL_SPEED;

        if (ball.x < 0) {
            player2Score++;
            resetBall();
        } else if (ball.x + BALL_RADIUS * 2 > WIDTH) {
            player1Score++;
            resetBall();
        }

        if (ball.y < 0 || ball.y + BALL_RADIUS * 2 > HEIGHT) {
            dy = -dy;
        }
    }

    private void resetBall() {
        Random random = new Random();
        int angle = random.nextInt(30) + 20;
        dx = Math.cos(Math.toRadians(angle));
        dy = Math.sin(Math.toRadians(angle));
        if (random.nextBoolean()) {
            dy = -dy;
        }
        if (random.nextBoolean()) {
            dx = -dx;
        }
        ball.x = WIDTH / 2 - BALL_RADIUS;
        ball.y = HEIGHT / 2 - BALL_RADIUS;
        paddlel.y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        paddler.y = HEIGHT / 2 - PADDLE_HEIGHT / 2;
        gameStarted = false;
    }

    public static void main(String[] args) {
        Pong pong = new Pong();
    }
}
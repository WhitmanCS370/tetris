import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

class Board extends JPanel implements ActionListener {
    private final int TIMER_DELAY = 300;
    private final Tetris parent;
    private final Random random;
    private Timer timer;
    private boolean isStarted = false;
    private boolean isGameOver = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int currentX = 0;
    private int currentY = 0;
    private Shape currentPiece;
    private ShapeType[][] board;

    public Board(Tetris parent) {
        this.parent = parent;
        this.random = new Random();
        initBoard();
        initTimer();
        setFocusable(true);
        addKeyListener(new TAdapter());
    }

    public void start() {
        newPiece();
        initBoard();
        initTimer();
        isStarted = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        doGameCycle();
    }

    private void initBoard() {
        board = new ShapeType[parent.getBoardWidth()][parent.getBoardHeight()];
        clearBoard();
    }

    private void clearBoard() {
        for (int i = 0; i < parent.getBoardWidth() ; i++) {
            for (int j = 0; j < parent.getBoardHeight() ; j++) {
                board[i][j] = ShapeType.NoShape;
            }
        }
    }

    private void initTimer() {
        timer = new Timer(TIMER_DELAY, this);
        timer.start();
    }

    private int squareWidth() {
        return (int) getSize().getWidth() / parent.getBoardWidth();
    }

    private int squareHeight() {
        return (int) getSize().getHeight() / parent.getBoardHeight();
    }

    private ShapeType shapeAt(int x, int y) {
        return board[x][y];
    }

    private void setShapeAt(int x, int y, ShapeType shape) {
        board[x][y] = shape;
    }

    private void doGameCycle() {
        update();
        repaint();
    }

    private void update() {
        if (isPaused) {
            return;
        }

        if (!isGameOver) {
            oneLineDown();
        }
    }

    private void oneLineDown() {
        if (!tryMove(currentPiece, currentX, currentY - 1)) {
            pieceDropped();
        }
    }

    private void dropDown() {
        int y = 0;
        while (!tryMove(currentPiece, currentX, y)) {
            y++;
        }
        pieceDropped();
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = currentX + currentPiece.x(i);
            int y = currentY - currentPiece.y(i);
            board[x][y] = currentPiece.getShapeType();
        }

        removeFullLines();

        if (!isGameOver) {
            newPiece();
        }
    }

    private void removeFullLines() {
        int numFullLines = 0;
        int rowsCleared = 0;

        for (int i = parent.getBoardHeight() - 1; i >= 0; i--) {
            boolean lineIsFull = true;

            for (int j = 0; j < parent.getBoardWidth(); j++) {
                if (shapeAt(j, i) == ShapeType.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < parent.getBoardHeight() - 1; k++) {
                    for (int j = 0; j < parent.getBoardWidth(); j++) {
                        setShapeAt(j, k, shapeAt(j, k + 1));
                    }
                }

                for (int j = 0; j < parent.getBoardWidth(); j++) {
                    setShapeAt(j, parent.getBoardHeight() - 1, ShapeType.NoShape);
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            parent.getStatusBar().setText(String.valueOf(numLinesRemoved));
            currentPiece.setShape(ShapeType.NoShape);
            repaint();
        }
    }

    private void newPiece() {
        currentPiece = new Shape();

        ShapeType[] shapes = ShapeType.values();
        currentPiece.setShape(shapes[1 + random.nextInt(shapes.length - 1)]);

        currentX = parent.getBoardWidth() / 2;
        currentY = parent.getBoardHeight() - 1 + currentPiece.minY();

        if (!tryMove(currentPiece, currentX, currentY)) {
            isGameOver = true;
            timer.stop();
            currentPiece.setShape(ShapeType.NoShape);
            var msg = String.format("Game over. Score: %d", numLinesRemoved);
            parent.getStatusBar().setText(msg);
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= parent.getBoardWidth() || y < 0 || y >= parent.getBoardHeight()) {
                return false;
            }

            if (shapeAt(x, y) != ShapeType.NoShape) {
                return false;
            }
        }

        currentPiece = newPiece;
        currentX = newX;
        currentY = newY;
        repaint();

        return true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        var g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        var size = getSize();
        int boardTop = (int) size.getHeight() - parent.getBoardHeight() * squareHeight();

        for (int i = 0; i < parent.getBoardHeight(); i++) {
            for (int j = 0; j < parent.getBoardWidth(); j++) {
                var shapeType = shapeAt(j, parent.getBoardHeight() - i - 1);

                if (shapeType != ShapeType.NoShape) {
                    drawSquare(g2d, j * squareWidth(), boardTop + i * squareHeight(), shapeType);
                }
            }
        }

        if (currentPiece.getShapeType() != ShapeType.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = currentX + currentPiece.x(i);
                int y = currentY - currentPiece.y(i);
                drawSquare(g2d, x * squareWidth(), boardTop + (parent.getBoardHeight() - y - 1) * squareHeight(),
                        currentPiece.getShapeType());
            }
        }
    }

    private void drawSquare(Graphics2D g2d, int x, int y, ShapeType shapeType) {
        var color = shapeType.getColor();

        g2d.setColor(color);
        g2d.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g2d.setColor(color.brighter());
        g2d.drawLine(x, y + squareHeight() - 1, x, y);
        g2d.drawLine(x, y, x + squareWidth() - 1, y);

        g2d.setColor(color.darker());
        g2d.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g2d.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (keyCode == 'p' || keyCode == 'P') {
                isPaused = !isPaused;
                if (isPaused) {
                    parent.getStatusBar().setText("PAUSED");
                } else {
                    parent.getStatusBar().setText(String.format("%d", numLinesRemoved));
                }
            }

            if (!isStarted || isPaused || isGameOver) {
                return;
            }

            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                    tryMove(currentPiece, currentX - 1, currentY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(currentPiece, currentX + 1, currentY);
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(currentPiece.rotateRight(), currentX, currentY);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(currentPiece.rotateLeft(), currentX, currentY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case 'd':
                case 'D':
                    oneLineDown();
                    break;
            }
        }
    }
}
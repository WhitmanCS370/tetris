import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class Tetris extends JFrame {
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    private Board board;
    private JLabel statusBar;

    public Tetris() {
        initUI();
    }

    private void initUI() {
        board = new Board(this);
        add(board);

        statusBar = new JLabel(" 0");
        add(statusBar, BorderLayout.SOUTH);

        setTitle("Tetris");
        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        board.start();

    }

    JLabel getStatusBar() {
        return statusBar;
    }

    public int getBoardWidth() {
        return BOARD_WIDTH;
    }

    public int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    public Board getBoard() {
        return board;
    }

    public static void main(String[] args) {
        Tetris game = new Tetris();
        game.setVisible(true);
    }
}

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

        currentX = parent.getBoardWidth() / 2 + 1;
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

    private enum GameState {
        Paused, Running, GameOver
    }

    private enum ShapeType {
        NoShape(new Color(0, 0, 0), 0),

        ZShape(new Color(204, 102, 102), 1),
        SShape(new Color(102, 204, 102), 2),
        LineShape(new Color(102, 102, 204), 3),
        TShape(new Color(204, 204, 102), 4),
        SquareShape(new Color(204, 102, 204), 5),
        LShape(new Color(102, 204, 204), 6),
        MirroredLShape(new Color(218, 170, 0), 7);

        private final Color color;
        private final int value;

        ShapeType(Color color, int value) {
            this.color = color;
            this.value = value;
        }

        public Color getColor() {
            return color;
        }

        public int getValue() {
            return value;
        }
    }

    private static class Shape {
        private ShapeType shapeType;
        private final int[][] coordinates = new int[4][2];

        public Shape() {
            setShape(ShapeType.NoShape);
        }

        public void setShape(ShapeType shapeType) {
            var coords = ShapeCoordinates.getCoordinates(shapeType);
            for (int i = 0; i < 4; i++) {
                System.arraycopy(coords[i], 0, coordinates[i], 0, 2);
            }
            this.shapeType = shapeType;
        }

        public ShapeType getShapeType() {
            return shapeType;
        }

        public int x(int index) {
            return coordinates[index][0];
        }

        public int y(int index) {
            return coordinates[index][1];
        }

        public void setX(int index, int x) {
            coordinates[index][0] = x;
        }

        public void setY(int index, int y) {
            coordinates[index][1] = y;
        }

        public int minX() {
            int m = coordinates[0][0];
            for (int i = 1; i < 4; i++) {
                m = Math.min(m, coordinates[i][0]);
            }
            return m;
        }

        public int minY() {
            int m = coordinates[0][1];
            for (int i = 1; i < 4; i++) {
                m = Math.min(m, coordinates[i][1]);
            }
            return m;
        }

        public Shape rotateLeft() {
            if (shapeType == ShapeType.SquareShape) {
                return this;
            }
            var result = new Shape();
            result.shapeType = shapeType;

            for (int i = 0; i < 4; i++) {
                result.setX(i, y(i));
                result.setY(i, -x(i));
            }

            return result;
        }

        public Shape rotateRight() {
            if (shapeType == ShapeType.SquareShape) {
                return this;
            }
            var result = new Shape();
            result.shapeType = shapeType;

            for (int i = 0; i < 4; i++) {
                result.setX(i, -y(i));
                result.setY(i, x(i));
            }

            return result;
        }
    }

    private static class ShapeCoordinates {
        private static final int[][][] coordsTable = new int[][][]{
                {{0, 0}, {0, 0}, {0, 0}, {0, 0}}, // NoShape
                {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}, // ZShape
                {{0, -1}, {0, 0}, {1, 0}, {1, 1}}, // SShape
                {{0, -1}, {0, 0}, {0, 1}, {0, 2}}, // LineShape
                {{-1, 0}, {0, 0}, {1, 0}, {0, 1}}, // TShape
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}}, // SquareShape
                {{-1, -1}, {0, -1}, {0, 0}, {0, 1}}, // LShape
                {{1, -1}, {0, -1}, {0, 0}, {0, 1}} // MirroredLShape
        };

        public static int[][] getCoordinates(ShapeType shapeType) {
            return coordsTable[shapeType.getValue()];
        }
    }

}
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


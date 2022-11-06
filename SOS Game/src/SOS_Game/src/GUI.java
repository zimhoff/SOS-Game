package SOS_Game.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;

public class GUI extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int CELL_SIZE = 100;
    private static final int GRID_WIDTH = 8;
    private static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    private static final int WIDTH_OFFSET = 200;

    private static final int CELL_PADDING = CELL_SIZE / 6;
    private static final int SYMBOL_STROKE_WIDTH = 2;

    private int CANVAS_WIDTH;
    private int CANVAS_HEIGHT;

    private GameBoardCanvas gameBoardCanvas;
    private JLabel gameStatusBar;

    private Board board;

    public GUI(Board board) {
        this.board = board;
        setContentPane();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setTitle("SOS");
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI(new Board()));
    }

    public Board getBoard() {
        return board;
    }

    private void setContentPane() {
        this.gameBoardCanvas = new GameBoardCanvas();
        CANVAS_WIDTH = CELL_SIZE * board.getBoardSize();
        CANVAS_HEIGHT = CELL_SIZE * board.getBoardSize();
        gameBoardCanvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

        gameStatusBar = new JLabel("");
        gameStatusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
        gameStatusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));

        JPanel topMenu = generateGameMenu();
        JPanel leftMenu = generateTileMenu(this.board.playerOne);
        JPanel rightMenu = generateTileMenu(this.board.playerTwo);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.removeAll();
        contentPane.add(topMenu, BorderLayout.PAGE_START);
        contentPane.add(leftMenu, BorderLayout.WEST);
        contentPane.add(rightMenu, BorderLayout.EAST);
        contentPane.add(gameBoardCanvas, BorderLayout.CENTER);
        contentPane.add(gameStatusBar, BorderLayout.AFTER_LAST_LINE);
    }

    private void resetGame() {
        this.board.initBoard();
        setContentPane();
        pack();
        this.gameBoardCanvas.repaint();
        this.gameBoardCanvas.printStatusBar();
    }

    private void replayGame() throws AWTException {
        GameReader reader;
        int delayTimeMs = 20;
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir") + "/recorded");
        int dialog = fileChooser.showOpenDialog(new JFrame("Open File"));
        if (dialog == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.getName().contains(".sos")) {
                try {
                    reader = new GameReader(file);
                } catch (FileNotFoundException e) {
                    System.out.println("Error: File not found.");
                    return;
                }
            } else {
                return;
            }
        } else {
            return;
        }

        this.board = new Board(reader.boardSize);
        if (reader.gameMode != null) board.setGameMode(reader.gameMode);
        resetGame();

        Robot robot = new Robot();
        robot.setAutoDelay(delayTimeMs);

        Thread safe = new Thread(() -> {
            for (GameReader.Move move : reader.moves) {
                robot.mouseMove(220 + (move.column * CELL_SIZE), WIDTH_OFFSET + (move.row * CELL_SIZE));
                if (move.tile.equals("S")) {
                    this.board.getTurn().setTile(BoardTile.TileValue.S);
                }
                if (move.tile.equals("O")) {
                    this.board.getTurn().setTile(BoardTile.TileValue.O);
                }
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.waitForIdle();
                robot.delay(delayTimeMs);

                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                robot.waitForIdle();
                robot.delay(delayTimeMs);
            }
        });
        safe.start();
    }

    public JPanel generateGameMenu() {
        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(CANVAS_WIDTH + WIDTH_OFFSET, 100));

        JPanel boardSizeMenu = new JPanel();
        boardSizeMenu.setPreferredSize(new Dimension(CANVAS_WIDTH + WIDTH_OFFSET, 20));

        ButtonGroup gameModeSelection = new ButtonGroup();
        menu.setLayout(new BorderLayout());
        JRadioButton simpleGame = new JRadioButton("Simple");
        JRadioButton generalGame = new JRadioButton("General");
        JButton newGame = new JButton("New Game");
        JButton replayGame = new JButton("Replay Game");
        JButton incButton = new JButton("+");
        JButton decButton = new JButton("-");
        JCheckBox recordGame = new JCheckBox("Record");

        Runnable resetSelected = () -> {
            simpleGame.setSelected(this.board.getGameMode() == Board.GameMode.Simple);
            generalGame.setSelected(this.board.getGameMode() == Board.GameMode.General);
            recordGame.setSelected(this.board.recordGame);
        };

        newGame.addActionListener(e -> {
            resetGame();
            resetSelected.run();
        });

        resetSelected.run();

        simpleGame.addActionListener(e -> board.setGameMode(Board.GameMode.Simple));
        generalGame.addActionListener(e -> board.setGameMode(Board.GameMode.General));
        recordGame.addActionListener(e -> board.toggleRecording());
        replayGame.addActionListener(e -> {
            resetGame();
            try {
                replayGame();
            } catch (AWTException ex) {
                ex.printStackTrace();
            }
        });

        incButton.addActionListener(e -> {
            if (this.board.getBoardSize() >= Board.MAX_BOARD_SIZE) {
                return;
            }
            board.setGrid(new BoardTile[board.getBoardSize() + 1][board.getBoardSize() + 1]);
            resetGame();
        });

        decButton.addActionListener(e -> {
            if (board.getBoardSize() <= Board.MIN_BOARD_SIZE) {
                return;
            }
            board.setGrid(new BoardTile[board.getBoardSize() - 1][board.getBoardSize() - 1]);
            resetGame();
        });

        menu.setLayout(new BorderLayout());
        menu.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));

        boardSizeMenu.setLayout(new BorderLayout());
        boardSizeMenu.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));

        boardSizeMenu.add(decButton, BorderLayout.WEST);
        boardSizeMenu.add(incButton, BorderLayout.EAST);
        gameModeSelection.add(simpleGame);
        gameModeSelection.add(generalGame);

        JPanel gameStartMenu = new JPanel();
        gameStartMenu.setLayout(new BorderLayout());
        gameStartMenu.add(newGame, BorderLayout.WEST);
        gameStartMenu.add(replayGame, BorderLayout.EAST);

        menu.add(boardSizeMenu, BorderLayout.PAGE_START);
        menu.add(simpleGame, BorderLayout.WEST);
        menu.add(recordGame, BorderLayout.CENTER);
        menu.add(generalGame, BorderLayout.EAST);
        menu.add(gameStartMenu, BorderLayout.PAGE_END);
        return menu;
    }

    public JPanel generateTileMenu(Player player) {
        JPanel menu = new JPanel();
        ButtonGroup tileSelection = new ButtonGroup();
        ButtonGroup styleSelection = new ButtonGroup();
        menu.setLayout(new FlowLayout());
        menu.add(new Label(player.getName()));
        JRadioButton s = new JRadioButton("S");
        JRadioButton o = new JRadioButton("O");
        JRadioButton human = new JRadioButton("Human");
        JRadioButton comp = new JRadioButton("Computer");

        s.setSelected(player.getTile().getValue() == BoardTile.TileValue.S);
        o.setSelected(player.getTile().getValue() == BoardTile.TileValue.O);
        human.setSelected(player.getStyle() == Player.PlayStyle.Human);
        comp.setSelected(player.getStyle() == Player.PlayStyle.Computer);

        s.addActionListener(e -> player.setTile(BoardTile.TileValue.S));
        o.addActionListener(e -> player.setTile(BoardTile.TileValue.O));
        human.addActionListener(e -> player.setStyle(Player.PlayStyle.Human));
        comp.addActionListener(e -> {
            player.setStyle(Player.PlayStyle.Computer);
            if (board.getTurn() == player) {
                board.makeComputerMove();
                repaint();
            }
        });

        tileSelection.add(s);
        tileSelection.add(o);
        styleSelection.add(human);
        styleSelection.add(comp);
        menu.add(s);
        menu.add(o);
        menu.add(human);
        menu.add(comp);
        menu.setPreferredSize(new Dimension(100, CANVAS_HEIGHT + 15));
        return menu;
    }

    class GameBoardCanvas extends JPanel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		GameBoardCanvas() {
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (board.getGameState() == Board.State.PLAYING || board.getGameState() == Board.State.INIT) {
                        int rowSelected = e.getY() / CELL_SIZE;
                        int colSelected = e.getX() / CELL_SIZE;
                        board.makeMove(rowSelected, colSelected);
                        repaint();
                    } else {
                        gameStatusBar.setText("");
                        board.initBoard();
                    }
                    repaint();
                }

                public void mouseReleased(MouseEvent e) {
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.WHITE);
            drawGridLines(g);
            drawBoard(g);
            printStatusBar();
        }

        private void drawGridLines(Graphics g) {
            g.setColor(Color.LIGHT_GRAY);

            for (int row = 1; row < board.getBoardSize(); row++) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
                        CANVAS_WIDTH - 1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);

                g.fillRoundRect(CELL_SIZE * row - GRID_WIDTH_HALF, 0,
                        GRID_WIDTH, CANVAS_HEIGHT - 1, GRID_WIDTH, GRID_WIDTH);
            }
        }

        private void drawBoard(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setFont(new Font("Helvetica", Font.PLAIN, GRID_WIDTH * 12));

            for (int row = 0; row < board.getBoardSize(); row++) {
                for (int col = 0; col < board.getBoardSize(); col++) {
                    int x1 = col * CELL_SIZE + CELL_PADDING;
                    int y1 = row * CELL_SIZE + CELL_PADDING;
                    if (board.getTile(row, col).getValue() == BoardTile.TileValue.S) {
                        g2d.setColor(Color.RED);
                        g2d.drawString("S", x1 + 5, y1 + 70);
                    } else if (board.getTile(row, col).getValue() == BoardTile.TileValue.O) {
                        g2d.setColor(Color.BLUE);
                        g2d.drawString("O", x1 - 2, y1 + 70);
                    }
                }
            }
        }

        private void printStatusBar() {
            switch (board.getGameState()) {
                case PLAYING:
                case INIT: {
                    gameStatusBar.setForeground(Color.BLACK);
                    gameStatusBar.setText(String.format("%s's Turn (%s points)", board.getTurn().getName(), board.getTurn().getPoints()));
                    break;
                }
                case DRAW: {
                    gameStatusBar.setForeground(Color.MAGENTA);
                    gameStatusBar.setText("It's a draw! Click to play again.");
                    break;
                }
                case PLAYER_ONE_WON: {
                    gameStatusBar.setForeground(Color.RED);
                    gameStatusBar.setText(String.format("%s wins! Click to play again.", board.playerOne.getName()));
                    break;
                }
                case PLAYER_TWO_WON: {
                    gameStatusBar.setForeground(Color.BLUE);
                    gameStatusBar.setText(String.format("%s wins! Click to play again.", board.playerTwo.getName()));
                    break;
                }
                default:
                    break;
            }
        }
    }
}
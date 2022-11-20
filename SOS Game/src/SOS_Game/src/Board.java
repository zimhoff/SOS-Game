package SOS_Game.src;


import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

import SOS_Game.src.BoardTile.TileValue;



public class Board {
    private static final Random RANDOM = new Random();
    public static final int MIN_BOARD_SIZE = 3;
    public static final int MAX_BOARD_SIZE = 9;

    private int boardSize = MIN_BOARD_SIZE;
    public final Player playerOne = new Player(TileValue.S, "Player 1");
    public final Player playerTwo = new Player(TileValue.O, "Player 2");
    public boolean recordGame = false;
    private GameWriter writer;
    private GameMode gameMode = GameMode.General;
    private BoardTile[][] grid;
    private TreeSet<Match> wins;
    private Player turn = playerOne;
    private State gameState;

    public Board() {
        grid = new BoardTile[MIN_BOARD_SIZE][MIN_BOARD_SIZE];
        this.writer = new GameWriter();
        initBoard();
    }

    public Board(int boardSize) {
        setBoardSize(boardSize);
        grid = new BoardTile[getBoardSize()][getBoardSize()];
        this.writer = new GameWriter();
        initBoard();
    }

    public void toggleRecording() {
        this.recordGame = !this.recordGame;
    }

    public void setGrid(BoardTile[][] grid) {
        assert (grid.length >= MIN_BOARD_SIZE && grid.length <= MAX_BOARD_SIZE);
        this.grid = grid;
        this.setBoardSize(grid.length);
    }

    public void initBoard() {
        this.playerOne.resetPoints();
        this.playerTwo.resetPoints();
        this.recordGame = false;
        gameState = State.INIT;
        turn = playerOne;
        this.wins = new TreeSet<>();

        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                grid[row][col] = new BoardTile(TileValue.None);
            }
        }

        this.writer.clearBuffer();
        this.writer = new GameWriter();
        this.writer.writeMessage(String.format("Board has been initialized at size %s.\n", this.getBoardSize()));
    }

    public int getBoardSize() {
        return this.boardSize;
    }

    public void setBoardSize(int boardSize) {
        assert (boardSize >= MIN_BOARD_SIZE && boardSize <= MAX_BOARD_SIZE);
        this.boardSize = boardSize;
    }

    public State getGameState() {
        return this.gameState;
    }

    public void setGameState(State state) {
        this.gameState = state;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(GameMode mode) {
        // The game mode should not be changed if any tiles have been set.
        for (BoardTile[] row : grid) {
            for (BoardTile tile : row) {
                if (tile.getValue() != TileValue.None) {
                    return;
                }
            }
        }
        this.gameMode = mode;
        this.writer.writeMessage(String.format("Game mode has been switched to %s.\n", this.gameMode));
    }

 
    public BoardTile getTile(int row, int column) {
        assert (row >= 0 && row < this.getBoardSize() &&
                column >= 0 && column < this.getBoardSize()) :
                String.format("getTile(%s, %s) out of bounds for board size %s.", row, column, this.getBoardSize());

        return grid[row][column];
    }

    public ArrayList<Pair> getEmptyTiles() {
        ArrayList<Pair> emptyTiles = new ArrayList<>();
        for (int i = 0; i < this.getBoardSize(); i++) {
            for (int j = 0; j < this.getBoardSize(); j++) {
                if (this.grid[i][j].getValue() == TileValue.None) {
                    emptyTiles.add(new Pair(i, j));
                }
            }
        }
        return emptyTiles;
    }

  
    public Player getTurn() {
        return turn;
    }

    public boolean boardHasWinner() {
        return this.getGameState() == State.PLAYER_ONE_WON || this.getGameState() == State.PLAYER_TWO_WON;
    }

    /**
     * Places a tile from the active player at the given (row, column) index.
     *
     * @param row    The row index of the tile to be placed.
     * @param column The column index of the tile to be placed.
     */
    public void makeMove(int row, int column) {
        assert (0 <= row && row < this.getBoardSize() &&
                0 <= column && column < this.getBoardSize())
                : String.format("makeMove(%s, %s) out of bounds for board size %s.", row, column, this.getBoardSize());

        if (grid[row][column].getValue() == TileValue.None) {
            grid[row][column] = (this.turn == playerOne) ? playerOne.getTile() : playerTwo.getTile();

            if (this.recordGame) this.writer.writeMove(row, column, turn);

            updateGameState();
            this.turn = (this.turn == playerOne) ? playerTwo : playerOne;
        }
        if (this.boardHasWinner()) {
            return;
        }

        if (turn.getStyle() == Player.PlayStyle.Computer
                && !isFull()) {
            makeComputerMove();
        }
    }

    /**
     * Places a tile in a random location on behalf of an active player who has selected the Computer play style.
     *
     * @see sprint_5.src.Player.PlayStyle
     */
    public void makeComputerMove() {
        if (this.boardHasWinner()) {
            return;
        }

        ArrayList<Pair> emptyTiles = getEmptyTiles();
        Pair choice = emptyTiles.get(RANDOM.nextInt(emptyTiles.size()));
        BoardTile tile = new BoardTile(TileValue.values()[RANDOM.nextInt(2)]);

        grid[choice.first][choice.second] = tile;
        turn.setTile(tile.getValue());
        if (this.recordGame) this.writer.writeMove(choice.first, choice.second, turn);
        updateGameState();

        if (emptyTiles.size() <= 1) {
            return;
        }

        turn = (turn == playerOne) ? playerTwo : playerOne;
        if (turn.getStyle() == Player.PlayStyle.Computer) {
            makeComputerMove();
        }
    }

    /**
     * @return True if the board is full, false otherwise.
     */
    public boolean isFull() {
        for (int i = 0; i < this.getBoardSize(); i++) {
            for (int j = 0; j < this.getBoardSize(); j++) {
                if (this.grid[i][j].getValue() == BoardTile.TileValue.None) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Given the selected game mode, register any newly completed wins update the game state accordingly.
     */
    private void updateGameState() {
        registerWin();
        switch (this.getGameMode()) {
            case Simple:
                if (hasWonSimple()) {
                    this.setGameState(this.turn == playerOne ? State.PLAYER_ONE_WON : State.PLAYER_TWO_WON);
                    Player playerWon = this.gameState == State.PLAYER_ONE_WON ? this.playerOne : this.playerTwo;
                    this.writer.writeMessage(String.format("%s has won!\n", playerWon.getName()));
                    if (this.recordGame) this.writer.writeToFile();
                } else {
                    this.setGameState(this.isFull() ? State.DRAW : State.PLAYING);
                }
                break;
            case General: {
                if (this.isFull()) {
                    if (hasWonGeneral()) {
                        this.setGameState(playerOne.getPoints() > playerTwo.getPoints() ? State.PLAYER_ONE_WON : State.PLAYER_TWO_WON);
                        Player playerWon = this.gameState == State.PLAYER_ONE_WON ? this.playerOne : this.playerTwo;
                        Player playerLost = playerWon == playerOne ? playerTwo : playerOne;
                        this.writer.writeMessage(
                                String.format("%s has won with %s points! %s finished with %s points.\n",
                                        playerWon.getName(), playerWon.getPoints(), playerLost.getName(), playerLost.getPoints())
                        );
                        if (this.recordGame) this.writer.writeToFile();
                    } else if (hasDrawGeneral()) {
                        this.setGameState(State.DRAW);
                        if (this.recordGame) this.writer.writeToFile();
                    }
                }
            }
        }
    }

    private void addWin(Match win) {
        if (!this.wins.contains(win)) {
            this.wins.add(win);
            turn.incrementPoints();
        }
    }

    private boolean checkHorizontalWin(int i, int j) {
        assert (i <= this.getBoardSize() && j <= this.getBoardSize() - 2);
        return this.grid[i][j].getValue() == TileValue.S && this.grid[i][j + 1].getValue() == TileValue.O
                && this.grid[i][j + 2].getValue() == TileValue.S;
    }

    private boolean checkDiagonalWin(int i, int j) {
        assert (i <= this.getBoardSize() - 2 && j <= this.getBoardSize() - 2);
        return this.grid[i][j].getValue() == TileValue.S && this.grid[i + 1][j + 1].getValue() == TileValue.O
                && this.grid[i + 2][j + 2].getValue() == TileValue.S;
    }

    private boolean checkBackwardsDiagonalWin(int i, int j) {
        assert (i <= this.getBoardSize() - 2 && j >= 2 && j <= this.getBoardSize());
        return this.grid[i][j].getValue() == TileValue.S && this.grid[i + 1][j - 1].getValue() == TileValue.O
                && this.grid[i + 2][j - 2].getValue() == TileValue.S;
    }

    private boolean checkVerticalWin(int i, int j) {
        assert (i <= this.getBoardSize() - 2 && j <= this.getBoardSize());
        return this.grid[i][j].getValue() == TileValue.S && this.grid[i + 1][j].getValue() == TileValue.O
                && this.grid[i + 2][j].getValue() == TileValue.S;
    }

    private void registerWin() {
        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                if (col >= 2 && row < getBoardSize() - 2) {
                    if (checkBackwardsDiagonalWin(row, col)) {
                        Match win = new Match(new Pair(row, col), new Pair(row + 1, col - 1), new Pair(row + 2, col - 2));
                        addWin(win);
                    }
                }
                if (col < getBoardSize() - 2 && row < getBoardSize() - 2) {
                    if (checkDiagonalWin(row, col)) {
                        Match win = new Match(new Pair(row, col), new Pair(row + 1, col + 1), new Pair(row + 2, col + 2));
                        addWin(win);
                    }
                }
                if (col < getBoardSize() - 2) {
                    if (checkHorizontalWin(row, col)) {
                        Match win = new Match(new Pair(row, col), new Pair(row, col + 1), new Pair(row, col + 2));
                        addWin(win);
                    }
                }
                if (row < getBoardSize() - 2) {
                    if (checkVerticalWin(row, col)) {
                        Match win = new Match(new Pair(row, col), new Pair(row + 1, col), new Pair(row + 2, col));
                        addWin(win);
                    }
                }
            }
        }
    }

    private boolean hasWonGeneral() {
        return !playerOne.getPoints().equals(playerTwo.getPoints());
    }

    private boolean hasDrawGeneral() {
        return playerOne.getPoints().equals(playerTwo.getPoints());
    }

    private boolean hasWonSimple() {
        return playerOne.getPoints() == 1 || playerTwo.getPoints() == 1;
    }

    public enum State {
        INIT, PLAYING, DRAW, PLAYER_ONE_WON, PLAYER_TWO_WON
    }

    public enum GameMode {
        Simple, General
    }
}
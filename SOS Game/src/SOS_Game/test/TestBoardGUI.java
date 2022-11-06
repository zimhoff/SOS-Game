package SOS_Game.test;


import org.junit.Before;
import org.junit.Test;
import SOS_Game.src.Board;
import SOS_Game.src.GUI;
import SOS_Game.src.Player;
import SOS_Game.src.BoardTile;

public class TestBoardGUI {
    private final int sleepTime = 0;
    private Board board;

    @Before
    public void setUp() {
        board = new Board(9);
    }
    @Test
    public void testSimpleGameWinner() {
        board = new Board(3);
        board.setGameMode(Board.GameMode.Simple);
        GUI gui = new GUI(board);
        board.makeMove(0, 0);
        board.makeMove(0, 1);
        board.makeMove(0, 2);

        assert (gui.getBoard().getGameMode() == Board.GameMode.Simple);
        assert (gui.getBoard().getGameState() == Board.State.PLAYER_ONE_WON);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // AC6.2: Simple game, no winner
    @Test
    public void testSimpleGameNoWinner() {
        board = new Board(3);
        board.setBoardSize(3);
        GUI gui = new GUI(board);
        board.playerTwo.setTile(BoardTile.TileValue.S);
        board.setGameMode(Board.GameMode.Simple);

        assert (gui.getBoard().getGameMode() == Board.GameMode.Simple);

        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                board.makeMove(i, j);
            }
        }

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert (gui.getBoard().getGameState() == Board.State.DRAW);


    }

    // AC7.1: General game, winner
    @Test
    public void testGeneralGameWinner() {
        board = new Board(9);
        board.setGameMode(Board.GameMode.General);
        GUI gui = new GUI(board);
        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                board.makeMove(i, j);
            }
        }

        assert (gui.getBoard().getGameMode() == Board.GameMode.General);
        assert (gui.getBoard().getGameState() == Board.State.PLAYER_ONE_WON);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // AC7.2: General game, no winner
    @Test
    public void testGeneralGameNoWinner() {
        board = new Board(9);
        board.playerTwo.setTile(BoardTile.TileValue.S);
        GUI gui = new GUI(board);
        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                board.makeMove(i, j);
            }
        }

        assert (gui.getBoard().getGameMode() == Board.GameMode.General);
        assert (gui.getBoard().getGameState() == Board.State.DRAW);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testComputerCanMove() {
        board = new Board(3);
        GUI gui = new GUI(board);
        board.playerTwo.setStyle(Player.PlayStyle.Computer);

        board.makeMove(1, 2);

        assert (board.playerTwo.getStyle() == Player.PlayStyle.Computer);
        assert (gui.getBoard().getEmptyTiles().size() == 7);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
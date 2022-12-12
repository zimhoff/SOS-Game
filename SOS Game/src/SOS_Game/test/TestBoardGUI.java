package SOS_Game.test;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

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
    public void testChangeGameModeBeforeGame() {
        board = new Board(9);
        GUI gui = new GUI(board);
        board.setGameMode(Board.GameMode.General);
        assert (gui.getBoard().getGameMode() == Board.GameMode.General);

        board.setGameMode(Board.GameMode.Simple);
        assert (gui.getBoard().getGameMode() == Board.GameMode.Simple);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testChangeGameModeDuringGame() {
        board = new Board(9);
        GUI gui = new GUI(board);
        board.setGameMode(Board.GameMode.General);
        assert (gui.getBoard().getGameMode() == Board.GameMode.General);

        board.makeMove(1, 1);

        board.setGameMode(Board.GameMode.Simple);
        //Game mode should not have updated
        assert (gui.getBoard().getGameMode() == Board.GameMode.General);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

    @Test
    public void testNewGame() {
        board = new Board(9);
        GUI gui = new GUI(board);
        board.makeMove(1, 2);

        assert (gui.getBoard().getTile(1, 2).getValue() == BoardTile.TileValue.S);
        gui.getBoard().initBoard();

        // Test that ALL tiles are registered as Tile.TileValue.None
        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int col = 0; col < board.getBoardSize(); col++) {
                assert (gui.getBoard().getTile(row, col).getValue() == BoardTile.TileValue.None);
            }
        }

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEmptyBoard() {
        board = new Board(9);
        GUI gui = new GUI(board);

        assert (gui.getBoard() != null);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testChangeTileSelection() {
        board = new Board(9);
        GUI gui = new GUI(board);

        gui.getBoard().playerOne.setTile(BoardTile.TileValue.O);
        board.makeMove(0, 0);
        assert (gui.getBoard().getTile(0, 0).getValue() == BoardTile.TileValue.O);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPlaceTileOverFilled() {
        board = new Board(9);
        GUI gui = new GUI(board);

        board.makeMove(1, 1);
        board.makeMove(1, 1);

        assert (gui.getBoard().getTile(1, 1).getValue() == BoardTile.TileValue.S);

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testPlaceTileOverEmpty() {
        board = new Board(9);
        GUI gui = new GUI(board);

        board.makeMove(0, 0);
        board.makeMove(1, 1);
        board.makeMove(8, 8);

        assert (gui.getBoard().getTile(0, 0).getValue() == BoardTile.TileValue.S);
        assert (gui.getBoard().getTile(1, 1).getValue() == BoardTile.TileValue.O);
        assert (gui.getBoard().getTile(8, 8).getValue() == BoardTile.TileValue.S);


        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    @Test
    public void testTwoComputers() {
        board = new Board(9);
        GUI gui = new GUI(board);
        gui.getBoard().playerOne.setStyle(Player.PlayStyle.Computer);
        gui.getBoard().playerTwo.setStyle(Player.PlayStyle.Computer);

        gui.getBoard().makeComputerMove();

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert (gui.getBoard().isFull());
    }

    @Test
    public void testRecordGame() {
        String[] initialFileList = Objects.requireNonNull(new File(String.format("%s/recorded/", System.getProperty("user.dir"))).list());
        int initialFileCount = initialFileList.length;
        board = new Board(3);
        board.setGameMode(Board.GameMode.Simple);
        GUI gui  = new GUI(board);
        gui.getBoard().toggleRecording();


        for (int i = 0; i < board.getBoardSize(); i++) {
            board.makeMove(i, 0);
        }

        String[] finalFileList = Objects.requireNonNull(new File(String.format("%s/recorded/", System.getProperty("user.dir"))).list());
        Arrays.sort(finalFileList);
        int finalFileCount = finalFileList.length;
        String lastRecordingName = finalFileList[finalFileCount - 2];
        File lastRecordingFile = new File(System.getProperty("user.dir") + "/recorded/" + lastRecordingName);
        lastRecordingFile.deleteOnExit();

        Scanner reader = null;
        try {
            reader = new Scanner(lastRecordingFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert reader != null;

        StringBuilder contents;
        contents = new StringBuilder();
        while (reader.hasNextLine()) {
            try {
                contents.append(reader.nextLine()).append("\n");
            } catch (NoSuchElementException ex) {
                break;
            }
        }

        String expected = "Board has been initialized at size 3.\n" +
                "Game mode has been switched to Simple.\n" +
                "Player 1 (S, Human): (0,0)\n" +
                "Player 2 (O, Human): (1,0)\n" +
                "Player 1 (S, Human): (2,0)\n" +
                "Player 1 has won!\n";

        try {
            Thread.sleep(sleepTime);
            gui.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assert (!Arrays.asList(initialFileList).contains(lastRecordingName));
        assert (finalFileCount == initialFileCount + 1);
        assert (contents.toString().equals(expected));

    }
}
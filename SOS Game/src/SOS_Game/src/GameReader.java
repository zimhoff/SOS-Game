package SOS_Game.src;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameReader {
    ArrayList<Move> moves;
    int boardSize;
    Board.GameMode gameMode;
    public GameReader(File file) throws FileNotFoundException {
        assert(file.getName().contains(".sos"));
        Scanner reader = new Scanner(file);

        StringBuilder contents = new StringBuilder();

        while (reader.hasNextLine()) {
            try {
                contents.append(reader.nextLine()).append("\n");
            } catch (NoSuchElementException ex) {
                break;
            }
        }

        this.moves = read(String.valueOf(contents));
    }

    
    public ArrayList<Move> read(String string) {
        final String move_pattern = "Player (?<Player>[12]) \\((?<Tile>[SO]), (?<Style>Human|Computer)\\): \\((?<Row>(\\d)),(?<Column>(\\d))\\)";
        final String initialize_pattern = "Board has been initialized at size (?<size>\\d).";
        final String mode_pattern = "Game mode has been switched to (?<mode>\\w+).";

        final Pattern match_move = Pattern.compile(move_pattern, Pattern.MULTILINE);
        final Pattern match_init = Pattern.compile(initialize_pattern, Pattern.MULTILINE);
        final Pattern match_mode = Pattern.compile(mode_pattern, Pattern.MULTILINE);

        final Matcher init_matcher = match_init.matcher(string);
        if (init_matcher.find()) {
            this.boardSize = Integer.parseInt(init_matcher.group("size"));
        }

        final Matcher mode_matcher = match_mode.matcher(string);
        if (mode_matcher.find()) {
            if (Objects.equals(mode_matcher.group("mode"), "Simple")) {
                this.gameMode = Board.GameMode.Simple;
            } else {
                this.gameMode = Board.GameMode.General;
            }
        }

        final Matcher matcher = match_move.matcher(string);
        ArrayList<Move> moves = new ArrayList<>();

        while (matcher.find()) {
            String player = matcher.group("Player");
            String tile = matcher.group("Tile");
            String style = matcher.group("Style");
            String row = matcher.group("Row");
            String column = matcher.group("Column");

            moves.add(new Move(player, tile, style, row, column));
        }
        return moves;
    }

    public static class Move {
        public final String player;
        public final String tile;
        public final String style;
        final int row;
        final int column;

        public Move(String player, String tile, String style, String row, String column) {
            this.player = player;
            this.tile = tile;
            this.style = style;
            this.row = Integer.parseInt(row);
            this.column = Integer.parseInt(column);
        }

        @Override
        public String toString() {
            return "Move{" +
                    "player='" + player + '\'' +
                    ", tile='" + tile + '\'' +
                    ", style='" + style + '\'' +
                    ", row=" + row +
                    ", column=" + column +
                    '}';
        }
    }
}
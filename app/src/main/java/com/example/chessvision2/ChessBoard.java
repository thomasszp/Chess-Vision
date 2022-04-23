package com.example.chessvision2;

import androidx.appcompat.widget.SwitchCompat;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

public class ChessBoard {
    private List<ChessPiece> allPieces = new ArrayList<ChessPiece>();
    private boolean whiteCastleQ = false;
    private boolean whiteCastleK = false;
    private boolean blackCastleQ = false;
    private boolean blackCastleK = false;
    private boolean isWhiteTurn = true;  //white turn default true

    //default constructor i guess
    public ChessBoard() {}

    public List<ChessPiece> getPieces() {
        return allPieces;
    }

    //resets board to default state
    public void wipeBoard() {
        allPieces.clear();
        whiteCastleQ = false;
        whiteCastleK = false;
        blackCastleQ = false;
        blackCastleK = false;
    }

    //adds piece to board piece list
    // only used when creating boards from scratch (probably)
    public void addPiece(ChessPiece piece) { allPieces.add(piece); }

    public void generateFromFEN(String FEN) {
        wipeBoard();
        String board = FEN.split(" ", 2)[0];
        String extra = FEN.split(" ", 2)[1];
        String[] rows = board.split("/");
        String details[] = extra.split(" ", 5);

        int rowIndex;
        int colIndex = 0;
        //does cool stuff
        for (String row : rows) {
            rowIndex = 0;
            for (char piece : row.toCharArray()) {
                if (Character.isDigit(piece)) {
                    rowIndex += Character.getNumericValue(piece);
                } else {
                    allPieces.add(new ChessPiece(rowIndex, colIndex, getPlayer(piece), getType(piece)));
                    rowIndex++;
                }
            }
            colIndex++;
        }

        //Sets player turn by flipping switch and storing variable
        if (details[0].equals("w")) {
            MainActivity.turnSwitch.setChecked(true);   //setChecked does not fire off the listener for some reason
            isWhiteTurn = true;
        } else {
            MainActivity.turnSwitch.setChecked(false);  //setChecked does not fire off the listener for some reason
            isWhiteTurn = false;
        }
        //store castle data
        if (details[1].contains("K"))
            whiteCastleK = true;
        if (details[1].contains("Q"))
            whiteCastleQ = true;
        if (details[1].contains("k"))
            blackCastleK = true;
        if (details[1].contains("q"))
            blackCastleQ = true;
    }

    //generates FEN from allPieces and class data
    //its messy and could be refactored... but it works!
    public String generateFEN() {
        ChessPiece piece;
        String newFEN = "";
        int empty;
        for (int col = 0; col < 8; col++) {
            empty = 0;
            for (int row = 0; row < 8; row++) {
                piece = pieceLocation(row, col);
                if (piece == null)
                    empty++;
                else {
                    if (empty != 0) {
                        newFEN += empty;
                        empty = 0;
                    }
                    newFEN += pieceString(piece.getPlayer(), piece.getType());
                }
                if (row == 7)
                    if (empty != 0) {
                        newFEN += empty;
                        empty = 0;
                    }
            }
            if (col != 7)
                newFEN += "/";
        }
        //turn data
        if (isWhiteTurn)
            newFEN += " w ";
        else
            newFEN += " b ";
        //castle data
        if (whiteCastleK)
            newFEN += "K";
        if (whiteCastleQ)
            newFEN += "Q";
        if (blackCastleK)
            newFEN += "k";
        if (blackCastleQ)
            newFEN += "q";

        return newFEN;
    }

    //compares two FEN values, and finds the last moved piece
    //Returned String array contains piece name, and locations of coordinates moved
    // {name, x1, y1, x2, y2}
    public String[] pieceChanged(String otherFEN) {
        //get each piece row of fen values
        String[] originalRows = generateFEN().split(" ", 2)[0].split("/");
        String[] otherRows = otherFEN.split(" ", 2)[0].split("/");
        originalRows = fillFEN(originalRows);
        otherRows = fillFEN(otherRows);

        String pieceToRow = "";
        String pieceToCol = "";
        String pieceFromRow = "";
        String pieceFromCol = "";
        boolean foundChange1 = false;
        boolean foundChange2 = false;

        //loop through each row
        for (int i = 0; i < 8; i++) {
            //loop through each col
            for (int j = 0; j < 8; j++) {
                //finds where piece is being moved to
                if (otherRows[i].charAt(j) != originalRows[i].charAt(j) && otherRows[i].charAt(j) != '-') {
                    pieceToRow = String.valueOf(i);
                    pieceToCol = String.valueOf(j);
                    foundChange1 = true;
                }
                //finds where piece is being moved from
                if (otherRows[i].charAt(j) != originalRows[i].charAt(j) && otherRows[i].charAt(j) == '-') {
                    pieceFromRow = String.valueOf(i);
                    pieceFromCol = String.valueOf(j);
                    foundChange2 = true;
                }
                //if to and from and found, return all data
                if (foundChange1 && foundChange2) {
                    ChessPlayer player1 = getPlayer(originalRows[Integer.parseInt(pieceFromRow)].charAt(Integer.parseInt(pieceFromCol)));
                    ChessType piece1 = getType(originalRows[Integer.parseInt(pieceFromRow)].charAt(Integer.parseInt(pieceFromCol)));
                    String pieceName = new ChessPiece(0, 0, player1, piece1).findPieceName();
                    return new String[] {pieceName, pieceFromRow, pieceFromCol, pieceToRow, pieceToCol};
                }
            }
        }
        //default location in case something goes wrong
        return new String[] {"white_pawn", "0", "0", "0", "0"};
    }

    //pass in array of fen rows
    //return fen with empty number tiles filled in by '-'
    public String[] fillFEN(String[] FEN) {
        for (int i = 0; i < 8; i++) {
            String toFill = "";
            for (int j = 0; j < FEN[i].length(); j++) {
                toFill = "";
                if (Character.isDigit(FEN[i].charAt(j))) {
                    for (int len = 0; len < Integer.parseInt(String.valueOf(FEN[i].charAt(j))); len++) {
                        toFill += "-";
                    }
                    FEN[i] = FEN[i].substring(0, j) + toFill + FEN[i].substring(j + 1);
                }
            }
        }
        return FEN;
    }

    //changes coordinates for a piece
    //if moving onto a space already occupied, obliterate the piece currently there
    //no need for move validation
    public void movePiece(ChessPiece piece, int coordx, int coordy) {
        //check for pieces at the spot being moved to
        ChessPiece testPiece = pieceLocation(coordx, coordy);
        if (testPiece != null) {
            allPieces.remove(testPiece);
        }

        piece.setRow(coordx);
        piece.setCol(coordy);
        setWhiteTurn(!isWhiteTurn());
    }

    //Delete piece at location
    public void deletePiece(int coordx, int coordy) {
        //check location to see if piece exists to be deleted
        ChessPiece testPiece = pieceLocation(coordx, coordy);
        if (testPiece != null) {
            allPieces.remove(testPiece);
        }
    }

    //returns piece at location if exists
    public ChessPiece pieceLocation(int row, int col) {
        for (ChessPiece piece : allPieces) {
            if (col == piece.getCol() && row == piece.getRow()) {
                return piece;
            }
        }
        return null;
    }

    //gets piece type based on char
    public ChessType getType(char type) {
        switch (type) {
            case 'k':
            case 'K':
                return ChessType.KING;
            case 'q':
            case 'Q':
                return ChessType.QUEEN;
            case 'r':
            case 'R':
                return ChessType.ROOK;
            case 'b':
            case 'B':
                return ChessType.BISHOP;
            case 'n':
            case 'N':
                return ChessType.KNIGHT;
            case 'p':
            case 'P':
                return ChessType.PAWN;
            default:
                return null;
        }
    }

    //gets player color based on char
    public ChessPlayer getPlayer(char player) {
        if (Character.isUpperCase(player))
            return ChessPlayer.WHITE;
        return ChessPlayer.BLACK;
    }

    //returns char based on piece and player
    public char pieceString(ChessPlayer player, ChessType type) {
        switch (type) {
            case KING:
                if (player == ChessPlayer.WHITE) return 'K';
                else return 'k';
            case QUEEN:
                if (player == ChessPlayer.WHITE) return 'Q';
                else return 'q';
            case ROOK:
                if (player == ChessPlayer.WHITE) return 'R';
                else return 'r';
            case BISHOP:
                if (player == ChessPlayer.WHITE) return 'B';
                else return 'b';
            case KNIGHT:
                if (player == ChessPlayer.WHITE) return 'N';
                else return 'n';
            case PAWN:
                if (player == ChessPlayer.WHITE) return 'P';
                else return 'p';
            default:
                return '-';
        }
    }

    //returns a list with all the current pieces on the board
    //only useful for debugging
    public List getPieceList() {
        ArrayList<String> pieceList = new ArrayList<String>();
        String temp = "";
        for (ChessPiece pieces : allPieces) {
            temp = pieces.getPlayer().toString() + " " + pieces.getType().toString() + ": " + pieces.getRow() + ", " + pieces.getCol();
            pieceList.add(temp);
        }
        return pieceList;
    }

    //prints board state
    @Override
    public String toString() {
        //String rows[] = {"A", "B", "C", "D", "E", "F", "G", "H"}
        String board = " \n";
        for (int i = 0; i < 8; i++) {
            board += i + 1 + " ";
            for (int j = 0; j < 8; j++) {
                ChessPiece tempPiece = pieceLocation(j, i);
                if (tempPiece == null)
                    board += " -";
                else {
                    board += " " + pieceString(tempPiece.getPlayer(), tempPiece.getType());
                }
            }
            board += "\n";
        }
        board += "   A B C D E F G H";
        return board;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        isWhiteTurn = whiteTurn;
    }
}

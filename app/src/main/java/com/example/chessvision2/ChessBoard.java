package com.example.chessvision2;

import android.support.v4.app.INotificationSideChannel;
import android.support.v4.os.IResultReceiver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ChessBoard {
    private List<ChessPiece> allPieces = new ArrayList<ChessPiece>();
    private String FEN;
    private boolean whiteCastleQ = false;
    private boolean whiteCastleK = false;
    private boolean blackCastleQ = false;
    private boolean blackCastleK = false;
    private boolean playerTurn = true;  //white turn default true

    //default constructor i guess
    public ChessBoard() {}

    public List<ChessPiece> getPieces() {
        return allPieces;
    }

    //resets board to default state
    public void wipeBoard() {
        allPieces.clear();
        FEN = "";
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

        //hopefully we only need to deal with 2, maybe 3 of these details
        if (details[0] == "w")
            playerTurn = true;
        else
            playerTurn = false;

        if (details[1].contains("K"))
            whiteCastleK = true;
        if (details[1].contains("Q"))
            whiteCastleQ = true;
        if (details[1].contains("k"))
            blackCastleK = true;
        if (details[1].contains("q"))
            blackCastleQ = true;
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
        playerTurn = !playerTurn;
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
}

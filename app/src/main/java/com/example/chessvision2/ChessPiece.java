package com.example.chessvision2;

import java.io.IOException;

public class ChessPiece {
    private int col, row;
    private ChessPlayer player;
    private ChessType type;
    private String pieceName;

    public ChessPiece(int newRow, int newCol, ChessPlayer newPlayer, ChessType newType, char pieceFEN) throws IOException {
        col = newCol;
        row = newRow;
        player = newPlayer;
        type = newType;
        pieceName = findPieceName(pieceFEN);
    }

    public int getCol() {return col;}
    public int getRow() {return row;}
    public ChessPlayer getPlayer() {return player;}
    public ChessType getType() {return type;}
    public String getPieceName() {return pieceName;}
    public void setCol(int newCol) {col = newCol;}
    public void setRow(int newRow) {row = newRow;}

    private String findPieceName(char piece) throws IOException {
        switch (piece) {
            case 'p': return "black_pawn";
            case 'r': return "black_rook";
            case 'n': return "black_knight";
            case 'b': return "black_bishop";
            case 'q': return "black_queen";
            case 'k': return "black_king";
            case 'P': return "white_pawn";
            case 'R': return "white_rook";
            case 'N': return "white_knight";
            case 'B': return "white_bishop";
            case 'Q': return "white_queen";
            case 'K': return "white_king";
            default: throw new IOException("Unexpected piece letter");
        }
    }
}

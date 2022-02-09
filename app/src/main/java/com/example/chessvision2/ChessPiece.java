package com.example.chessvision2;

public class ChessPiece {
    private int col, row;
    private ChessPlayer player;
    private ChessType type;

    public ChessPiece(int newCol, int newRow, ChessPlayer newPlayer, ChessType newType) {
        col = newCol;
        row = newRow;
        player = newPlayer;
        type = newType;
    }

    public int getCol() {return col;}
    public int getRow() {return row;}
    public ChessPlayer getPlayer() {return player;}
    public ChessType getType() {return type;}


}

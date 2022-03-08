package com.example.chessvision2;

public class ChessPiece {
    private int col, row;
    private ChessPlayer player;
    private ChessType type;

    public ChessPiece(int newRow, int newCol, ChessPlayer newPlayer, ChessType newType) {
        col = newCol;
        row = newRow;
        player = newPlayer;
        type = newType;
    }

    public int getCol() {return col;}
    public int getRow() {return row;}
    public ChessPlayer getPlayer() {return player;}
    public ChessType getType() {return type;}
    public void setCol(int newCol) {col = newCol;}
    public void setRow(int newRow) {row = newRow;}


    public String findPieceName() {
        switch (type) {
            case PAWN:
                if (player == ChessPlayer.BLACK)
                    return "black_pawn";
                return "white_pawn";
            case KNIGHT:
                if (player == ChessPlayer.BLACK)
                    return "black_knight";
                return "white_knight";
            case BISHOP:
                if (player == ChessPlayer.BLACK)
                    return "black_bishop";
                return "white_bishop";
            case ROOK:
                if (player == ChessPlayer.BLACK)
                    return "black_rook";
                return "white_rook";
            case KING:
                if (player == ChessPlayer.BLACK)
                    return "black_king";
                return "white_king";
            case QUEEN:
                if (player == ChessPlayer.BLACK)
                    return "black_queen";
                return "white_queen";
        }
        return null;
    }
}

package com.example.chess;

import android.support.v4.app.INotificationSideChannel;
import android.support.v4.os.IResultReceiver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ChessBoard {
    private List<ChessPiece> allPieces = new ArrayList<ChessPiece>();
    private String FEN;
    private boolean whiteCastle;
    private boolean blackCastle;



    public ChessBoard() {}

    public void addPiece(ChessPiece piece) {
        allPieces.add(piece);
    }

    //returns piece at location if exists
    public ChessPiece pieceLocation(int col, int row) {
        for (ChessPiece piece : allPieces) {
            if (col == piece.getCol() && row == piece.getRow()) {
                return piece;
            }
        }
        return null;
    }

    //returns char based on piece and player
    public char pieceString(ChessPlayer player, ChessType type) {
        char output = 0;
        switch (type) {
            case KING:
                if (player == ChessPlayer.WHITE) output = 'K';
                else output = 'k';
                break;
            case QUEEN:
                if (player == ChessPlayer.WHITE) output = 'Q';
                else output = 'q';
                break;
            case ROOK:
                if (player == ChessPlayer.WHITE) output = 'R';
                else output = 'r';
                break;
            case BISHOP:
                if (player == ChessPlayer.WHITE) output = 'B';
                else output = 'b';
                break;
            case KNIGHT:
                if (player == ChessPlayer.WHITE) output = 'N';
                else output = 'n';
                break;
            case PAWN:
                if (player == ChessPlayer.WHITE) output = 'P';
                else output = 'p';
                break;
            default:
                output = '-';
                break;
        }
        return output;
    }

    //prints board state
    @Override
    public String toString() {
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
        board += "   1 2 3 4 5 6 7 8";
        return board;
    }
}

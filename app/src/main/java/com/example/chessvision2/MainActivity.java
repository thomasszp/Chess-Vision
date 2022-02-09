package com.example.chessvision2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "mainTag";

    ChessBoard exampleBoard = new ChessBoard();
    ChessPiece examplePiece = new ChessPiece(1, 1, ChessPlayer.WHITE, ChessType.KING);
    ChessPiece examplePiece2 = new ChessPiece(5, 2, ChessPlayer.BLACK, ChessType.QUEEN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        exampleBoard.addPiece(examplePiece);
        exampleBoard.addPiece(examplePiece2);
        Log.d(TAG, exampleBoard.toString());
    }
}
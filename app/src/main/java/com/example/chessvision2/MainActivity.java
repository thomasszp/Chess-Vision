package com.example.chessvision2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "mainTag";
    LinearLayout option1;
    LinearLayout option2;
    LinearLayout option3;
    Spinner prevMovesDropdown;
    String[] pastMoves = {};
    ArrayAdapter<String> spinnerArrayAdapter;
    ChessBoard exampleBoard = new ChessBoard();
    ChessPiece examplePiece = new ChessPiece(1, 1, ChessPlayer.WHITE, ChessType.KING);
    ChessPiece examplePiece2 = new ChessPiece(5, 2, ChessPlayer.BLACK, ChessType.QUEEN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Load all XML controls
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        prevMovesDropdown = findViewById(R.id.prevMoves);

        // Control listeners
        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextMove((LinearLayout) view);
                GetNewMoves();
            }
        });

        // Load past move into array for dropdown
        pastMoves = Arrays.copyOf(pastMoves, pastMoves.length + 1);
        pastMoves[pastMoves.length - 1] = "Kc6";
        SetDropdown();

        exampleBoard.addPiece(examplePiece);
        exampleBoard.addPiece(examplePiece2);
        Log.d(TAG, exampleBoard.toString());


    }

    private void NextMove(LinearLayout option) {
        ImageView pieceImage = (ImageView) option.getChildAt(0);
        TextView currentSquareView = (TextView) option.getChildAt(1);
        TextView nextSquareView = (TextView) option.getChildAt(3);

        // Get views by ID in string form
        String currentSquareText = (String) currentSquareView.getText();
        String nextSquareText = (String) nextSquareView.getText();
        int currentSquareID = getResources().getIdentifier(currentSquareText, "id", getPackageName());
        int nextSquareID = getResources().getIdentifier(nextSquareText, "id", getPackageName());
        ImageView currentSquare = findViewById(currentSquareID);
        ImageView nextSquare = findViewById(nextSquareID);

        // Put piece into nextSquare, remove piece from currentSquare
        nextSquare.setBackgroundResource(getResources().getIdentifier((String) pieceImage.getTag(), "drawable", getPackageName()));
        currentSquare.setBackgroundResource(0);
    }

    private void GetNewMoves() {
        // Set new dropdown move
        pastMoves = Arrays.copyOf(pastMoves, pastMoves.length + 1);
        pastMoves[1] = pastMoves[0];
        pastMoves[0] = "Be3";
        SetDropdown();

        // Generate new moves as options
        ImageView pieceImage = (ImageView) option1.getChildAt(0);
        TextView currentSquareView = (TextView) option1.getChildAt(1);
        TextView nextSquareView = (TextView) option1.getChildAt(3);
        TextView winPct = (TextView) option1.getChildAt(4);
        TextView tiePct = (TextView) option1.getChildAt(5);
        TextView lossPct = (TextView) option1.getChildAt(6);

        pieceImage.setBackgroundResource(R.drawable.white_pawn);
        currentSquareView.setText("F2");
        nextSquareView.setText("E3");
        winPct.setText("52%");
        tiePct.setText("12%");
        lossPct.setText("36%");

        pieceImage = (ImageView) option2.getChildAt(0);
        currentSquareView = (TextView) option2.getChildAt(1);
        nextSquareView = (TextView) option2.getChildAt(3);
        winPct = (TextView) option2.getChildAt(4);
        tiePct = (TextView) option2.getChildAt(5);
        lossPct = (TextView) option2.getChildAt(6);

        pieceImage.setBackgroundResource(R.drawable.white_pawn);
        currentSquareView.setText("D2");
        nextSquareView.setText("E3");
        winPct.setText("51%");
        tiePct.setText("12%");
        lossPct.setText("37%");

        pieceImage = (ImageView) option3.getChildAt(0);
        currentSquareView = (TextView) option3.getChildAt(1);
        nextSquareView = (TextView) option3.getChildAt(3);
        winPct = (TextView) option3.getChildAt(4);
        tiePct = (TextView) option3.getChildAt(5);
        lossPct = (TextView) option3.getChildAt(6);

        pieceImage.setBackgroundResource(R.drawable.white_knight);
        currentSquareView.setText("A2");
        nextSquareView.setText("C3");
        winPct.setText("42%");
        tiePct.setText("21%");
        lossPct.setText("37%");
    }

    private void SetDropdown() {
        spinnerArrayAdapter = new ArrayAdapter<>
                (this, R.layout.spinner_item,
                        pastMoves); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(R.layout
                .spinner_item);
        prevMovesDropdown.setAdapter(spinnerArrayAdapter);
    }
}
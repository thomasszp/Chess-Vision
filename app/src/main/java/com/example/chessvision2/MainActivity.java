package com.example.chessvision2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private static final String [][] boardSquares = {
            {"A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1"},
            {"A2", "B2", "C2", "D2", "E2", "F2", "G2", "H2"},
            {"A3", "B3", "C3", "D3", "E3", "F3", "G3", "H3"},
            {"A4", "B4", "C4", "D4", "E4", "F4", "G4", "H4"},
            {"A5", "B5", "C5", "D5", "E5", "F5", "G5", "H5"},
            {"A6", "B6", "C6", "D6", "E6", "F6", "G6", "H6"},
            {"A7", "B7", "C7", "D7", "E7", "F7", "G7", "H7"},
            {"A8", "B8", "C8", "D8", "E8", "F8", "G8", "H8"}};
    private static final String TAG = "mainTag";
    LinearLayout optionLayout;                  //LinearLayout housing all options
    View.OnClickListener optionListener;        //Listener for all options
    Spinner prevMovesDropdown;                  //Spinner for previously executed moves in descending order
    String[] prevMoves = {};                    //Array of all past moves, adapted into spinner dynamically
    ArrayAdapter<String> spinnerArrayAdapter;   //Spinner adapter
    //ChessBoard exampleBoard = new ChessBoard();

    @Override
    // General onCreate function. This runs when the app launches
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();   //Hide default toolbar
        ChessBoard baseBoard = new ChessBoard();

        // Load all XML controls into variables
        optionLayout = findViewById(R.id.optionLayout);
        prevMovesDropdown = findViewById(R.id.prevMoves);

        // Control listeners
        optionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextMove((LinearLayout) v);
                GetNewMoves();
            }
        };

        // Load options manually until we have the DB
        optionLayout.removeAllViews();
        SetHardcodedOptions();

        // Load past move into array for dropdown
        AddPrevMove("Nc6");
        LoadPrevMoveSpinner();

        // Test loading the board from FEN
        try {
            //LoadBoardFromFEN("r1bqk1nr/pppp1ppp/2n5/2b1p3/1PB1P3/5N2/P1PP1PPP/RNBQK2R b KQkq - 0 4");
            //LoadBoardFromFEN("rnbqkbnr/pppp1ppp/8/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 2");
            baseBoard.generateFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
            //LoadBoardFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, baseBoard.toString());
    }

    //loads frontend board from board object currently in use
    private void loadBoard(ChessBoard board) {
        //clear board
        clearBoard();

        //put all pieces on new board
        for (ChessPiece piece : board.getPieces()) {
            SetGridSpace(piece, piece.getRow(), piece.getCol());
        }

    }

    //TODO: CLEAR FRONTEND OF ALL IMAGES
    //please write this adam
    private void clearBoard() {

    }

    private void SetGridSpace(ChessPiece piece, int col, int row) {
        //String tmp = String.valueOf(GetColLetter(col)) + row;
//        int gridID = getResources().getIdentifier(String.valueOf(GetColLetter(col)) + row, "id", getPackageName());
//        ImageView pieceImage = findViewById(gridID);
//        if (piece == ' ') { //Add blank space to the grid spot
//            pieceImage.setBackgroundResource(0);
//        } else {            //Add piece to that grid spot
//            pieceImage.setBackgroundResource(getResources().getIdentifier((String) findPieceName(piece), "drawable", getPackageName()));
//        }
        int gridID = getResources().getIdentifier(boardSquares[row][col], "id", getPackageName());
        ImageView pieceImage = findViewById(gridID);
        pieceImage.setBackgroundResource(getResources().getIdentifier((String) piece.findPieceName(), "drawable", getPackageName()));
    }

    // Add new previously made move signature (in PGN) to the prevMoves array
    private void AddPrevMove(String newMove) {
        prevMoves = Arrays.copyOf(prevMoves, prevMoves.length + 1); //Add new array element
        prevMoves[prevMoves.length-1] = newMove;
    }

    // Execute the selected move. This is called from an option's listener
    private void NextMove(LinearLayout option) {
        ImageView pieceImage = (ImageView) option.getChildAt(0);
        TextView currentSquareView = (TextView) option.getChildAt(1);
        TextView nextSquareView = (TextView) option.getChildAt(3);

        // Get views by ID in string form
        int currentSquareID = getResources().getIdentifier((String) currentSquareView.getText(), "id", getPackageName());
        int nextSquareID = getResources().getIdentifier((String) nextSquareView.getText(), "id", getPackageName());
        ImageView currentSquare = findViewById(currentSquareID);
        ImageView nextSquare = findViewById(nextSquareID);

        // Put piece into nextSquare, remove piece from currentSquare
        nextSquare.setBackgroundResource(getResources().getIdentifier((String) pieceImage.getTag(), "drawable", getPackageName()));
        currentSquare.setBackgroundResource(0);

        // Set move in prev moves dropdown
        String tmp = "";
        if (((String) pieceImage.getTag()).contains("bishop")) {
            tmp = "B";
        } else if (((String) pieceImage.getTag()).contains("queen")) {
            tmp = "Q";
        } else if (((String) pieceImage.getTag()).contains("king")) {
            tmp = "K";
        } else if (((String) pieceImage.getTag()).contains("rook")) {
            tmp = "R";
        } else if (((String) pieceImage.getTag()).contains("knight")) {
            tmp = "N";
        } else if (((String) pieceImage.getTag()).contains("pawn")) {
            tmp = "";
        }
        tmp += ((String) nextSquareView.getText()).toLowerCase();
        AddPrevMove(tmp);
        LoadPrevMoveSpinner();
    }

    // Fetch a list of possible next moves from the DB
    // (For now, it sets hardcoded options that assume you moved the bishop from the first set of hardcoded options)
    // TODO: Add database functionality
    private void GetNewMoves() {
        optionLayout.removeAllViews();
        optionLayout.addView(NewOption("white_pawn", "F2","E3",52,12,36));
        optionLayout.addView(NewOption("white_pawn", "D2","E3",51,12,37));
        optionLayout.addView(NewOption("white_knight", "B1","C3",42,21,37));
    }

    // Link the array of previous moves to the Spinner
    private void LoadPrevMoveSpinner() {
        spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, prevMoves);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        prevMovesDropdown.setAdapter(spinnerArrayAdapter);
        prevMovesDropdown.setSelection(prevMoves.length-1);
    }

    // Generate a list of hardcoded options for use in demos
    private void SetHardcodedOptions() {
        optionLayout.addView(NewOption("black_bishop", "C5","E3",60,10,30));
        optionLayout.addView(NewOption("black_queen", "D8","F6",47,5,48));
        optionLayout.addView(NewOption("black_knight", "C6","B4",56,15,29));
    }

    // Return dips for use in setting a control's layout parameters
    private int GetDips(float dips) {
        return (int)(dips * getResources().getDisplayMetrics().density);
    }

    // Add a new option to the list of moves
    private LinearLayout NewOption(String pieceName, String currSquareText, String nextSquareText, int pctWinValue, int pctTieValue, int pctLossValue) {
        // General stuff all options share
        LinearLayout option = new LinearLayout(this);
        LayoutParams optionLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, GetDips(80));
        optionLayoutParams.setMargins(0, GetDips(2), 0, GetDips(2));
        option.setLayoutParams(optionLayoutParams);
        option.setGravity(Gravity.CENTER);
        option.setPadding(0,GetDips(5),0,GetDips(5));
        option.setBackgroundColor(getResources().getColor(R.color.colorBoardLight));
        option.setOnClickListener(optionListener);

        // Now make the children of the layout,
        //   Piece Image
        ImageView pieceImage = new ImageView(this);
        pieceImage.setLayoutParams(new LayoutParams(GetDips(70), ViewGroup.LayoutParams.MATCH_PARENT));
        pieceImage.setScaleType(ScaleType.CENTER_CROP);
        pieceImage.setBackgroundResource(getResources().getIdentifier((String) pieceName, "drawable", getPackageName()));
        pieceImage.setTag(pieceName);

        //   Piece current square location
        TextView currSquare = new TextView(this);
        currSquare.setLayoutParams(new LayoutParams(GetDips(38), ViewGroup.LayoutParams.MATCH_PARENT));
        currSquare.setGravity(Gravity.CENTER);
        currSquare.setTextSize(30);
        currSquare.setText(currSquareText);

        //   Arrow image
        ImageView arrowImage = new ImageView(this);
        LayoutParams arrowImageParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        arrowImageParams.gravity = Gravity.CENTER;
        arrowImage.setLayoutParams(arrowImageParams);
        arrowImage.setBackgroundResource(R.drawable.arrow_right);

        //   Piece next square location
        TextView nextSquare = new TextView(this);
        nextSquare.setLayoutParams(new LayoutParams(GetDips(38), ViewGroup.LayoutParams.MATCH_PARENT));
        nextSquare.setGravity(Gravity.CENTER_VERTICAL);
        nextSquare.setTextSize(30);
        nextSquare.setText(nextSquareText);

        //   in percentage
        TextView pctWin = new TextView(this);
        LayoutParams pctWinParams = new LayoutParams(GetDips(73), ViewGroup.LayoutParams.WRAP_CONTENT);
        pctWinParams.setMargins(GetDips(5),0,0,0);
        pctWin.setLayoutParams(pctWinParams);
        pctWin.setGravity(Gravity.CENTER);
        pctWin.setTextColor(getResources().getColor(R.color.colorWin));
        pctWin.setTextSize(30);
        pctWin.setText(String.format(Locale.US, "%d%%", pctWinValue));

        //   Tie percentage
        TextView pctTie = new TextView(this);
        pctTie.setLayoutParams(new LayoutParams(GetDips(73), ViewGroup.LayoutParams.WRAP_CONTENT));
        pctTie.setGravity(Gravity.CENTER);
        pctTie.setTextColor(getResources().getColor(R.color.colorTie));
        pctTie.setTextSize(30);
        pctTie.setText(String.format(Locale.US, "%d%%", pctTieValue));

        //   Loss percentage
        TextView pctLoss = new TextView(this);
        pctLoss.setLayoutParams(new LayoutParams(GetDips(73), ViewGroup.LayoutParams.WRAP_CONTENT));
        pctLoss.setGravity(Gravity.CENTER);
        pctLoss.setTextColor(getResources().getColor(R.color.colorLose));
        pctLoss.setTextSize(30);
        pctLoss.setText(String.format(Locale.US, "%d%%", pctLossValue));

        // Add everything to the new option and return it
        option.addView(pieceImage);
        option.addView(currSquare);
        option.addView(arrowImage);
        option.addView(nextSquare);
        option.addView(pctWin);
        option.addView(pctTie);
        option.addView(pctLoss);
        return option;
    }

    //Should be deprecated -tom
    // Return column letter from given column number, zero indexed
    private char GetColLetter(int col) {
        switch (col) {
            case 0: return 'A';
            case 1: return 'B';
            case 2: return 'C';
            case 3: return 'D';
            case 4: return 'E';
            case 5: return 'F';
            case 6: return 'G';
            default: return 'H';
        }
    }
}

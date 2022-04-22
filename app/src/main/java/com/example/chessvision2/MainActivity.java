package com.example.chessvision2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.math.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private static final String [][] boardSquares = {
            {"A8", "B8", "C8", "D8", "E8", "F8", "G8", "H8"},
            {"A7", "B7", "C7", "D7", "E7", "F7", "G7", "H7"},
            {"A6", "B6", "C6", "D6", "E6", "F6", "G6", "H6"},
            {"A5", "B5", "C5", "D5", "E5", "F5", "G5", "H5"},
            {"A4", "B4", "C4", "D4", "E4", "F4", "G4", "H4"},
            {"A3", "B3", "C3", "D3", "E3", "F3", "G3", "H3"},
            {"A2", "B2", "C2", "D2", "E2", "F2", "G2", "H2"},
            {"A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1"}};
    private static final String[] moveColNames = {
            "MOVE1", "MOVE2", "MOVE3", "MOVE4", "MOVE5", "MOVE6", "MOVE7", "MOVE8",
            "MOVE9", "MOVE10", "MOVE11", "MOVE12", "MOVE13", "MOVE14", "MOVE15", "MOVE16",
            "MOVE17", "MOVE18", "MOVE19", "MOVE20", "MOVE21", "MOVE22", "MOVE23", "MOVE24",
            "MOVE25", "MOVE26", "MOVE27", "MOVE28", "MOVE29", "MOVE30", "MOVE31", "MOVE32",
            "MOVE33", "MOVE34", "MOVE35", "MOVE36", "MOVE37", "MOVE38"
    };
    private static final String TAG = "mainTag";
    ChessBoard baseBoard;
    GridLayout boardGrid;
    LinearLayout boardBackground;                       //LinearLayout delete space for pieces
    LinearLayout optionLayout;                          //LinearLayout housing all options
    View.OnClickListener optionListener;                //Listener for all options
    View.OnClickListener backgroundListener;            //Listener for delete space for pieces
    SwitchCompat.OnCheckedChangeListener turnListener;  //Listener for turn toggle
    Spinner prevMovesDropdown;                          //Spinner for previously executed moves in descending order
    String[] prevMoves = {};                            //Array of all past moves, adapted into spinner dynamically
    ArrayAdapter<String> spinnerArrayAdapter;           //Spinner adapter
    SwitchCompat turnSwitch;                            //switch for toggling white or black turn state
    Map<String, int[]> nextMoves = new HashMap<String, int[]>();    //Map of FENs back from the
    //ChessBoard exampleBoard = new ChessBoard();

    @Override
    // General onCreate function. This runs when the app launches
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();   //Hide default toolbar

        baseBoard = new ChessBoard();

        // Load all XML controls into variables
        optionLayout = findViewById(R.id.optionLayout);
        prevMovesDropdown = findViewById(R.id.prevMoves);
        turnSwitch = findViewById(R.id.turnSwitch);
        // Control listeners
        optionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextMove((LinearLayout) v);
            }
        };
        turnListener = new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean switchState) {
                baseBoard.setWhiteTurn(switchState);
            }
        };

        //listener for clicking outside board
        boardBackground = (LinearLayout) findViewById(R.id.boardBackground);
        boardBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePieceClick();
            }
        });

        //listener for each box in GridLayout board
        boardGrid = (GridLayout)findViewById(R.id.boardGrid);
        boardClickedEvent(boardGrid);

        // Load past move into array for dropdown
        AddPrevMove("Nc6");
        LoadPrevMoveSpinner();



        // Test loading the board from FEN
        try {
            //baseBoard.generateFromFEN("r1bqk1nr/pppp1ppp/2n5/2b1p3/1PB1P3/5N2/P1PP1PPP/RNBQK2R b KQkq - 0 4");
            baseBoard.generateFromFEN("rnbqkbnr/pppp1ppp/8/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 2");
            //baseBoard.generateFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
            //baseBoard.generateFromFEN("rnbqkbnr/pppp1ppp/8/4p3/3P4/8/PPP1PPPP/RNBQKBNR w KQkq - 0 1");
            loadBoard(baseBoard);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
    }

    // Connect to our MySQL server through JDBC
    private void getConnection() throws Exception {
        try {
            if (! Python.isStarted()) {
                Python.start(new AndroidPlatform(this));
            }
            //create instance
            Python py = Python.getInstance();
            //create object
            PyObject pyObj = py.getModule("pythonQueries");
            //call function
            PyObject obj = pyObj.callAttr("main");
            //get returned String
            Log.d("TAG", obj.toString());

        }
        catch (Exception e){
            Log.d(TAG, e.toString());
            throw new RuntimeException(e);
        }
    }

    //loads frontend board from board object currently in use
    private void loadBoard(ChessBoard board) {
        //clear board
        clearBoard();

        //put all pieces on new board
        for (ChessPiece piece : board.getPieces()) {
            SetGridSpace(piece, piece.getRow(), piece.getCol());
        }
        //debugging tools to show stuff
        Log.d(TAG, baseBoard.toString());
        Log.d(TAG, baseBoard.generateFEN());
        Snackbar mySnackbar = Snackbar.make(optionLayout, baseBoard.generateFEN(), BaseTransientBottomBar.LENGTH_LONG);
        mySnackbar.show();

        //get new recommended moves
        if (calculateMinMoves(baseBoard.generateFEN()) > 0) {
            loadDisplayedMoves();
        }   //TODO: Make this work without this if statement
    }

    private void clearBoard() {
        for(int i = 0; i < boardGrid.getChildCount(); i++) {
            LinearLayout square = (LinearLayout) boardGrid.getChildAt(i);
            ImageView image = (ImageView) square.getChildAt(0);
            image.setBackgroundResource(0);
        }
    }

    private void SetGridSpace(ChessPiece piece, int col, int row) {
        int gridID = getResources().getIdentifier(boardSquares[row][col], "id", getPackageName());
        ImageView pieceImage = findViewById(gridID);
        pieceImage.setBackgroundResource(getResources().getIdentifier((String) piece.findPieceName(), "drawable", getPackageName()));
    }

    //checks for when the board is clicked and gets the index of the box
    private void boardClickedEvent(GridLayout board) {
        //for each box on the grid
        for(int i = 0; i < board.getChildCount(); i++) {
            LinearLayout cardView = (LinearLayout) board.getChildAt(i);
            final int clickIndex = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickRecord(boardSquares[clickIndex%8][clickIndex/8], clickIndex);
                }
            });
        }
    }

    //stores and highlights squares that have been clicked on
    private Integer click1, click2;
    private void clickRecord(String gridId, int index) {
        if (click1 == null) {
            click1 = index;
            boardGrid.getChildAt(click1).setElevation(100);
            return;
        } else if (click2 == null) {
            //if same square clicked twice, reset stored indexes
            if (click1 == index) {
                click1 = null;
                boardGrid.getChildAt(index).setElevation(0);
                return;
            }
            click2 = index;
            boardGrid.getChildAt(click1).setElevation(0);

        }
        moveFromIndexes(click1, click2);
        click1 = null;
        click2 = null;
    }

    //Lets user delete piece by clicking off the board
    //Checks if piece is selected, then deletes if so
    private void deletePieceClick() {
        //if no piece selected
        if (click1 == null)
            return;
        int row1 = (click1 % 8);
        int col1 = (click1 / 8);
        //wipe data about piece being deleted
        baseBoard.deletePiece(row1, col1);
        boardGrid.getChildAt(click1).setElevation(0);
        click1 = null;
        loadBoard(baseBoard);
    }

    //moves piece in index1, to location of index2
    private void moveFromIndexes(int index1, int index2) {
        int row1 = (index1 % 8);
        int col1 = (index1 / 8);
        ChessPiece firstPiece =  baseBoard.pieceLocation(row1, col1);
        if (firstPiece != null) {
            int row2 = (index2 % 8);
            int col2 = (index2 / 8);
            baseBoard.movePiece(firstPiece, row2, col2);
            loadBoard(baseBoard);
            return;
        }
    }

    //when called, updates data in recommended move fields
    private void loadDisplayedMoves() {
        //clear data
        optionLayout.removeAllViews();

        //query db for each move and respective data
        queryFutureMoves();

        // Get top 4 options for display
        Map<String, Integer> topMoves = new HashMap<String, Integer>();
        String[] topMove1 = {"", "0"};
        String[] topMove2 = {"", "0"};
        String[] topMove3 = {"", "0"};
        String[] topMove4 = {"", "0"};

        for (String fen : nextMoves.keySet()) {
            int[] outcomes = nextMoves.get(fen);
            int total = outcomes[0] + outcomes[1] + outcomes[2];
            if (total > Integer.parseInt(topMove1[1])) {
                topMove4 = topMove3;
                topMove3 = topMove2;
                topMove2 = topMove1;
                topMove1 = new String[]{fen, String.valueOf(total)};
            } else if (total > Integer.parseInt(topMove2[1])) {
                topMove4 = topMove3;
                topMove3 = topMove2;
                topMove2 = new String[]{fen, String.valueOf(total)};
            } else if (total > Integer.parseInt(topMove3[1])) {
                topMove4 = topMove3;
                topMove3 = new String[]{fen, String.valueOf(total)};
            } else if (total > Integer.parseInt(topMove4[1])) {
                topMove4 = new String[]{fen, String.valueOf(total)};
            }
        }
        Log.d("TAG", topMove1[0] + " : " + topMove1[1]);
        Log.d("TAG", topMove2[0] + " : " + topMove2[1]);
        Log.d("TAG", topMove3[0] + " : " + topMove3[1]);
        Log.d("TAG", topMove4[0] + " : " + topMove4[1]);

        int tieCount, winCount, lossCount;
        String tiePct, winPct, lossPct;

        // Create option for topMove1
        tieCount = nextMoves.get(topMove1[0])[0];
        if (baseBoard.isWhiteTurn()) {
            winCount = nextMoves.get(topMove1[0])[1];
            lossCount = nextMoves.get(topMove1[0])[2];
        } else {
            winCount = nextMoves.get(topMove1[0])[2];
            lossCount = nextMoves.get(topMove1[0])[1];
        }
        tiePct = String.valueOf((int)((tieCount / Double.parseDouble(topMove1[1]))*100));
        winPct = String.valueOf((int)((winCount / Double.parseDouble(topMove1[1]))*100));
        lossPct = String.valueOf((int)((lossCount / Double.parseDouble(topMove1[1]))*100));
        optionLayout.addView(NewOption(baseBoard.pieceChanged(topMove1[0]), "", "", winPct,tiePct,lossPct));

        // Create option for topMove2
        tieCount = nextMoves.get(topMove2[0])[0];
        if (baseBoard.isWhiteTurn()) {
            winCount = nextMoves.get(topMove2[0])[2];
            lossCount = nextMoves.get(topMove2[0])[1];
        } else {
            winCount = nextMoves.get(topMove2[0])[1];
            lossCount = nextMoves.get(topMove2[0])[2];
        }
        tiePct = String.valueOf((int)((tieCount / Double.parseDouble(topMove2[1]))*100));
        winPct = String.valueOf((int)((winCount / Double.parseDouble(topMove2[1]))*100));
        lossPct = String.valueOf((int)((lossCount / Double.parseDouble(topMove2[1]))*100));
        optionLayout.addView(NewOption(baseBoard.pieceChanged(topMove2[0]), "", "", winPct,tiePct,lossPct));

        // Create option for topMove3
        tieCount = nextMoves.get(topMove3[0])[0];
        if (baseBoard.isWhiteTurn()) {
            winCount = nextMoves.get(topMove3[0])[1];
            lossCount = nextMoves.get(topMove3[0])[2];
        } else {
            winCount = nextMoves.get(topMove3[0])[2];
            lossCount = nextMoves.get(topMove3[0])[1];
        }
        tiePct = String.valueOf((int)((tieCount / Double.parseDouble(topMove3[1]))*100));
        winPct = String.valueOf((int)((winCount / Double.parseDouble(topMove3[1]))*100));
        lossPct = String.valueOf((int)((lossCount / Double.parseDouble(topMove3[1]))*100));
        optionLayout.addView(NewOption(baseBoard.pieceChanged(topMove3[0]), "", "", winPct,tiePct,lossPct));

        // Create option for topMove4
        tieCount = nextMoves.get(topMove4[0])[0];
        if (baseBoard.isWhiteTurn()) {
            winCount = nextMoves.get(topMove4[0])[1];
            lossCount = nextMoves.get(topMove4[0])[2];
        } else {
            winCount = nextMoves.get(topMove4[0])[2];
            lossCount = nextMoves.get(topMove4[0])[1];
        }
        tiePct = String.valueOf((int)((tieCount / Double.parseDouble(topMove4[1]))*100));
        winPct = String.valueOf((int)((winCount / Double.parseDouble(topMove4[1]))*100));
        lossPct = String.valueOf((int)((lossCount / Double.parseDouble(topMove4[1]))*100));
        optionLayout.addView(NewOption(baseBoard.pieceChanged(topMove4[0]), "", "", winPct,tiePct,lossPct));

    }

    //is called every time the board updates
    //gets every next move from current FEN
    private void queryFutureMoves() {
        String moveSelects = generateQueryMoves();
        String currentFEN = baseBoard.generateFEN();
        int winCount,lossCount,tieCount;

        try {
            if (! Python.isStarted()) {
                Python.start(new AndroidPlatform(this));
            }
            Python py = Python.getInstance();                                           //create instance
            PyObject pyObj = py.getModule("pythonQueries");                             //create object
            PyObject obj = pyObj.callAttr("getNewOptions", currentFEN, moveSelects);    //call function
            //Log.d("TAG", obj.toString());                                               //get returned String
            for (PyObject o : obj.asList()) {
                //Log.d("TAG", o.toString());
                for (int i = 5; i < o.asList().size(); i++) {
                    if ("rnbqkbnr/pppp1ppp/8/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR b KQkq".equals(o.asList().get(i).toString())) {
                        // Do we already track this next move?
                        int[] outcomes = {0,0,0};
                        if (nextMoves.containsKey(o.asList().get(i+1).toString()))
                            outcomes = nextMoves.get(o.asList().get(i+1).toString());

                        // Increment game end state
                        if (o.asList().get(2).toInt() == 0)         // tie
                            outcomes[0] += 1;
                        else if (o.asList().get(2).toInt() == 1)    //White win
                            outcomes[1] += 1;
                        else if (o.asList().get(2).toInt() == 2)    //Black win
                            outcomes[2] += 1;

                        // Save FEN and corresponding end-game states to global map
                        nextMoves.put(o.asList().get(i+1).toString(), outcomes);

                        //Log.d("TAG", nextMoves.toString());
                        //Log.d("TAG", "   Tie: " + nextMoves.get(o.asList().get(i+1).toString())[0]);
                        //Log.d("TAG", " White: " + nextMoves.get(o.asList().get(i+1).toString())[1]);
                        //Log.d("TAG", " Black: " + nextMoves.get(o.asList().get(i+1).toString())[2]);
                        break;
                        // TODO: Put this shit in a function for Tom^^^^
                    }   // TODO: Make sure we don't do out of bounds error if i+2 > o.asList().size()
                }
            }
        }
        catch (Exception e){
            Log.d(TAG, e.toString());
            throw new RuntimeException(e);
        }
    }

    //Returns string of all moves cols that need to be searched in db query
    private String generateQueryMoves() {
        String fullText = "";
        String fen = baseBoard.generateFEN();
        int minMoves = calculateMinMoves(fen);
        for (int i = minMoves - 1; i < 38; i++) {
            fullText += moveColNames[i] + " LIKE '" + fen + "%'";
            if (i != 37)
                fullText += " OR ";
        }
        return fullText;
    }

    public void boardRefresh(View v) {
        baseBoard.generateFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
        loadBoard(baseBoard);
    }

    // Add new previously made move signature (in PGN) to the prevMoves array
    private void AddPrevMove(String newMove) {
        prevMoves = Arrays.copyOf(prevMoves, prevMoves.length + 1); //Add new array element
        prevMoves[prevMoves.length-1] = newMove;
    }

    // Execute the selected move. This is called from an option's listener
    // TODO: Rewrite hardcoded buttons
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

    // Link the array of previous moves to the Spinner
    private void LoadPrevMoveSpinner() {
        spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, prevMoves);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        prevMovesDropdown.setAdapter(spinnerArrayAdapter);
        prevMovesDropdown.setSelection(prevMoves.length-1);
    }

    // Return dips for use in setting a control's layout parameters
    private int GetDips(float dips) {
        return (int)(dips * getResources().getDisplayMetrics().density);
    }

    // Add a new option to the list of moves
    private LinearLayout NewOption(String pieceName, String currSquareText, String nextSquareText, String pctWinValue, String pctTieValue, String pctLossValue) {
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
        pctWin.setText(String.format(Locale.US, "%s%%", pctWinValue));

        //   Tie percentage
        TextView pctTie = new TextView(this);
        pctTie.setLayoutParams(new LayoutParams(GetDips(73), ViewGroup.LayoutParams.WRAP_CONTENT));
        pctTie.setGravity(Gravity.CENTER);
        pctTie.setTextColor(getResources().getColor(R.color.colorTie));
        pctTie.setTextSize(30);
        pctTie.setText(String.format(Locale.US, "%s%%", pctTieValue));

        //   Loss percentage
        TextView pctLoss = new TextView(this);
        pctLoss.setLayoutParams(new LayoutParams(GetDips(73), ViewGroup.LayoutParams.WRAP_CONTENT));
        pctLoss.setGravity(Gravity.CENTER);
        pctLoss.setTextColor(getResources().getColor(R.color.colorLose));
        pctLoss.setTextSize(30);
        pctLoss.setText(String.format(Locale.US, "%s%%", pctLossValue));

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

    //given a FEN, roughly calculate the minimum number of moves required to get to a board state
    //useful for lowering db query time
    //ugly but it works... FINALLY!
    public int calculateMinMoves(String FEN) {
        int moveCount = 0;
        //new board and base board converted into arrays or pieces for 'easy' compare
        String board = FEN.split(" ", 2)[0];
        String[] rows = board.split("/");
        String defaultFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        String[] defaultRows = defaultFEN.split("/");

        //fill numbers with '-' to make comparing easier
        rows = baseBoard.fillFEN(rows);

        //loops through each row
        for (int i = 0; i < 8; i++) {
            //only checks starting rows
            if (i == 0 || i == 1 || i == 6 || i == 7) {
                //compares backline columns, and looks for differences
                for (int j = 0; j < 8; j++) {
                    if (rows[i].charAt(j) != defaultRows[i].charAt(j))
                        moveCount++;
                }
            }
        }
        return moveCount;
    }
}

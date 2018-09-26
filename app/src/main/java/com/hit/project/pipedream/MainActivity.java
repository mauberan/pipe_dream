package com.hit.project.pipedream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;

import com.hit.project.pipedream.data.ScoreRecord;
import com.hit.project.pipedream.data.ScoresTable;
import com.hit.project.pipedream.logic.GameBoard;
import com.hit.project.pipedream.logic.Pipe;
import com.hit.project.pipedream.logic.Point;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import static com.hit.project.pipedream.logic.Pipe.FlowStatus.FLOW_STARTED_IN_PIPE;

public class MainActivity extends Activity implements View.OnClickListener , Observer {
    private static final String SAVED_BOARD_FILE_NAME = "gameBoard.dat";

    GameBoard gameBoard;
    Map<Point, BoxButton> layoutBoard = new HashMap<>();
    CountDownTimer timer;
    int levelSpeedPerFrame = 50;
    boolean shouldPlayAnimation = false;
    RequierdBoxesBar requierdBlocks = new RequierdBoxesBar();

    /** alert dialogs */
    AlertDialog gameOverAlertDialog = null;
    AlertDialog nextLevelAlertDialog = null;
    /** end alert dialogs */

    public abstract class PipeAnimation extends AnimationDrawable {

        Handler mAnimationHandler;

        public PipeAnimation(AnimationDrawable aniDrawable, int levelFlowTimePerFrame) {

            int i = 0;

            for (; i < aniDrawable.getNumberOfFrames(); i++) {

                this.addFrame(aniDrawable.getFrame(i), levelFlowTimePerFrame);

            }
            this.setOneShot(true);

        }

        @Override
        public void start() {
            super.start();

            mAnimationHandler = new Handler();
            mAnimationHandler.post(new Runnable() {
                @Override
                public void run() {
                    onAnimationStart();
                }
            });
            mAnimationHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onAnimationFinish();
                }
            }, getTotalDuration());

        }

        public int getTotalDuration() {

            int iDuration = 0;

            for (int i = 0; i < this.getNumberOfFrames(); i++) {
                iDuration += this.getDuration(i);
            }

            return iDuration;
        }

        public abstract void onAnimationFinish();

        public abstract void onAnimationStart();

        public Drawable SkipAnimation() {
            return this.getFrame(getNumberOfFrames() - 1);
        }


    }

    public class RequierdBoxesBar {
        int requiredPipes = 0;

        public void RequierdBoxes() {

        }

        public void setNewRequiredAmount(int newAmount) {
            requiredPipes = newAmount;
            updateDisplay();
        }

        private void updateDisplay() {
            LinearLayout requierdBlocksLayout = findViewById(R.id.requierd_blocks);

            LinearLayout.LayoutParams imageButtonLayoutParams = new LinearLayout.LayoutParams(65, 65);
            imageButtonLayoutParams.weight = 1f;
            imageButtonLayoutParams.gravity = Gravity.LEFT;


            requierdBlocksLayout.removeAllViews();

            for (int i = 0; i < requiredPipes; i++) {
                ImageView thumb = new ImageView(MainActivity.this);
                thumb.setLayoutParams(imageButtonLayoutParams);
                thumb.setImageResource(R.drawable.ic_requierd_pipe_thumb);
                thumb.setPadding(4, 0, 4, 0);
                thumb.setForegroundGravity(Gravity.LEFT);
                requierdBlocksLayout.addView(thumb);

            }
        }

        public void DecrementAmount() {
            requiredPipes--;
            if (requiredPipes >= 0) {
                LinearLayout requierdBlocksLayout = findViewById(R.id.requierd_blocks);
                requierdBlocksLayout.removeViewAt(0);
            }
        }
    }

    public class BoxButton extends ImageButton {
        Pipe.PipeType _type = null;
        Point _point = null;
        //        Dictionary<Pipe.Directions, AnimationDrawable> _animations = new Hashtable<>();


        public BoxButton(Context context) {
            super(context);
            LinearLayout.LayoutParams imageButtonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageButtonLayoutParams.weight = 1f;
            imageButtonLayoutParams.gravity = Gravity.CENTER;

            this.setFocusableInTouchMode(false);
            this.setAdjustViewBounds(true);
            this.setBaselineAlignBottom(false);
            this.setCropToPadding(true);
            this.setScaleType(ImageButton.ScaleType.FIT_XY);
            this.setBackgroundColor(Color.TRANSPARENT);
            this.setLayoutParams(imageButtonLayoutParams);
            this.setPadding(0, 0, 0, 0);

        }

        public void DrawPipe() {
            if (_type != null) {
                this.setImageResource(android.R.color.transparent);
                this.setScaleX(1);
                this.setScaleY(1);
                this.setRotation(0);

                switch (_type) {
                    case TOP_LEFT:
                        this.setImageResource(R.drawable.corner);
                        this.setRotation(90);
                        this.setScaleX(-1);
                        break;
                    case TOP_RIGHT:
                        this.setImageResource(R.drawable.corner);
                        this.setRotation(180);
                        this.setScaleX(-1);
                        break;
                    case BOTTOM_LEFT:
                        this.setImageResource(R.drawable.corner);
                        this.setScaleX(-1);
                        break;
                    case BOTTOM_RIGHT:
                        this.setImageResource(R.drawable.corner);
                        break;
                    case CROSS:
                        this.setImageResource(R.drawable.cross);
                        break;
                    case HORIZONTAL:
                        this.setImageResource(R.drawable.vertical);
                        this.setRotation(90);
                        break;
                    case VERTICAL:
                        this.setImageResource(R.drawable.vertical);
                        break;
                    case START_UP:
                        this.setImageResource(R.drawable.ic_start_pipe);
                        this.setRotation(180);

                        break;
                    case START_DOWN:
                        this.setImageResource(R.drawable.ic_start_pipe);

                        break;
                    case START_LEFT:
                        this.setImageResource(R.drawable.ic_start_pipe);
                        this.setRotation(90);
                        break;
                    case START_RIGHT:
                        this.setImageResource(R.drawable.ic_start_pipe);
                        this.setRotation(270);
                        break;
                }
            } else {

                this.setImageResource(R.drawable.empty_box);
            }
        }

        public void setPoint(Point point) {
            this._point = point;
        }

        public Point getPoint() {
            return this._point;
        }

        public void setType(Pipe.PipeType type) {
            this._type = type;
        }

        public Pipe.PipeType getType() {
            return _type;
        }


        public void AnimateClick() {
            this.setImageResource(R.drawable.ic_wrench);


            Animation a = new RotateAnimation(0.0f, 360.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            a.setRepeatCount(Animation.ABSOLUTE);
            a.setDuration(110);

            this.startAnimation(a);
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    DrawPipe();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        public void AnimateFlow(final Pipe.Directions endDirection, int numberOfVisits) {
            Pipe.PipeType type = getType();
            this.setImageResource(android.R.color.transparent);
            this.setScaleX(1);
            this.setScaleY(1);
            this.setRotation(0);

            switch (type) {
                case TOP_LEFT:
                    this.setImageResource(R.drawable.corner_animation);
                    if (endDirection == Pipe.Directions.UP) {
                        this.setRotation(180);
                    } else {  //DIRECTION IS LEFT
                        this.setRotation(270);

                        this.setScaleY(-1);

                        this.setImageResource(R.drawable.corner_animation);
                    }
                    break;
                case TOP_RIGHT:
                    this.setImageResource(R.drawable.corner_animation);

                    if (endDirection == Pipe.Directions.UP) {
                        this.setRotation(180);
                        this.setScaleX(-1);
                    } else {  //DIRECTION IS RIGHT
                        this.setRotation(90);
                        this.setScaleX(-1);
                        this.setScaleY(-1);
                    }

                    break;
                case BOTTOM_LEFT:
                    this.setImageResource(R.drawable.corner_animation);
                    if (endDirection == Pipe.Directions.DOWN) {
                        this.setRotation(0);
                        this.setScaleX(-1);
                    } else {  //DIRECTION IS LEFT
                        this.setRotation(90);
                    }
                    break;
                case BOTTOM_RIGHT:
                    this.setImageResource(R.drawable.corner_animation);

                    if (endDirection == Pipe.Directions.RIGHT) {
                        this.setRotation(90);
                        this.setScaleY(-1);
                    }
                    break;
                case CROSS:
                    if (numberOfVisits > 1) {
                        this.setImageResource(R.drawable.cross_second_flow_animation);
                        if (endDirection == Pipe.Directions.RIGHT) {
                            this.setRotation(0);
                        }
                        if (endDirection == Pipe.Directions.LEFT) {
                            this.setRotation(180);

                        }
                        if (endDirection == Pipe.Directions.UP) {
                            this.setRotation(270);

                        }
                        if (endDirection == Pipe.Directions.DOWN) {
                            this.setRotation(90);

                        }

                    } else {
                        this.setImageResource(R.drawable.cross_first_flow_animation);
                        if (endDirection == Pipe.Directions.RIGHT) {
                            this.setRotation(270);

                        }
                        if (endDirection == Pipe.Directions.LEFT) {
                            this.setRotation(90);

                        }
                        if (endDirection == Pipe.Directions.UP) {
                            this.setRotation(180);
                        }
                        if (endDirection == Pipe.Directions.DOWN) {
                            this.setRotation(0);

                        }
                    }

                    break;
                case HORIZONTAL:
                    this.setImageResource(R.drawable.vertical_animation);
                    this.setRotation(90);
                    if (endDirection == Pipe.Directions.RIGHT) {
                        this.setScaleY(-1);
                    }

                    break;
                case VERTICAL:
                    this.setImageResource(R.drawable.vertical_animation);
                    if (endDirection == Pipe.Directions.UP) {
                        this.setRotation(180);
                    }
                    break;
                case START_RIGHT:
                    this.setImageResource(R.drawable.start_animation);
                    this.setRotation(270);
                    break;
                case START_LEFT:
                    this.setImageResource(R.drawable.start_animation);
                    this.setRotation(90);
                    break;
                case START_UP:
                    this.setImageResource(R.drawable.start_animation);
                    this.setRotation(180);
                    break;
                case START_DOWN:
                    this.setImageResource(R.drawable.start_animation);
                    break;
            }

            PipeAnimation animation = new PipeAnimation(
                    (AnimationDrawable) this.getDrawable(), levelSpeedPerFrame) {
                @Override
                public void onAnimationStart() {
                    System.out.println("ANIM STARTED");
                }

                @Override
                public void onAnimationFinish() {
                    System.out.println("ANIM FINISHED");
                    if (shouldPlayAnimation) {
                        gameBoard.notifyPipeIsFull();
                    }
                }
            };
            this.setImageDrawable(animation);
            ((AnimationDrawable) this.getDrawable()).start();

        }

        //TODO CHECK THIS FUNCTION ON BOARD
        public void SkipAnimation(int numberOfVisits) {
            Pipe.PipeType type = getType();
            this.setImageResource(android.R.color.transparent);
            this.setScaleX(1);
            this.setScaleY(1);
            this.setRotation(0);

            switch (type) {
                case TOP_LEFT:
                    this.setImageResource(R.drawable.corner_animation);
                    this.setRotation(270);

                    this.setScaleY(-1);


                    break;
                case TOP_RIGHT: //TODO FINISH THIS
                    this.setImageResource(R.drawable.corner_animation);

                    this.setRotation(90);
                    this.setScaleX(-1);
                    this.setScaleY(-1);

                    break;
                case BOTTOM_LEFT:
                    this.setImageResource(R.drawable.corner_animation);
                    this.setRotation(90);

                    break;
                case BOTTOM_RIGHT:
                    this.setImageResource(R.drawable.corner_animation);

                    //TODO CROSS NOT HANDLED
                    break;
                case CROSS:
                    if (numberOfVisits == 1) {
                        this.setImageResource(R.drawable.cross_first_flow_animation);
                        Pipe currentPipe = gameBoard.getPipeByPoint(this._point);
                        if (currentPipe != null) {
                            if (currentPipe.getFlowDirection() == Pipe.Directions.RIGHT || currentPipe.getFlowDirection() == Pipe.Directions.LEFT) {
                                this.setRotation(90);
                            }
                        }
                    } else if (numberOfVisits == 2) {
                        this.setImageResource(R.drawable.cross_second_flow_animation);

                    }
                    break;
                case HORIZONTAL:
                    this.setImageResource(R.drawable.vertical_animation);
                    this.setRotation(90);


                    break;
                case VERTICAL:
                    this.setImageResource(R.drawable.vertical_animation);
                    this.setRotation(180);
                    break;
                case START_RIGHT:
                    this.setImageResource(R.drawable.start_animation);
                    this.setRotation(270);
                    break;
                case START_LEFT:
                    this.setImageResource(R.drawable.start_animation);
                    this.setRotation(90);
                    break;
                case START_UP:
                    this.setImageResource(R.drawable.start_animation);
                    this.setRotation(180);
                    break;
                case START_DOWN:
                    this.setImageResource(R.drawable.start_animation);
                    break;
            }

            PipeAnimation animation = new PipeAnimation(
                    (AnimationDrawable) this.getDrawable(), gameBoard.getCurrentLevel().getFlowTimeInPipe()) {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationFinish() {
                }
            };
            this.setImageDrawable(animation.SkipAnimation());
        }


    }

    @Override
    public void update(Observable observable, Object o) {
        if (!(observable instanceof GameBoard)) {
            return;
        }

        Pipe.FlowStatus currentPipeFlowStatus = (Pipe.FlowStatus) o;

        switch (currentPipeFlowStatus) {
            case FLOW_STARTED_IN_PIPE:
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Pipe currentPipe = gameBoard.getCurrentPipe();
                        BoxButton box = getBoxFromPipe(currentPipe);

                        box.AnimateFlow(currentPipe.getFlowDirection(), currentPipe.getNumOfVisits());
                    }
                });
                System.out.println(String.format("pipe location:%s flow direction:%s", gameBoard.getCurrentPipe().getPosition(), gameBoard.getCurrentPipe().getFlowDirection()));
                UpdatePointBar();
                requierdBlocks.DecrementAmount();

                break;
            case FOUND_NEXT_PIPE:
                break;
            case GAMEOVER:

                this.GameOverDialog(IsRecord(gameBoard.getTotalScore()));

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
            case NEXT_LEVEL:
                LevelDoneDialog();

            case NO_MORE_LEVELS:

        }
    }

    NextBlockBar nextBar = new NextBlockBar();

    @Override
    public void onBackPressed() {
        PauseGame();
        MainDialog();
    }

    @Override
    public void onClick(View view) {

        BoxButton box = (BoxButton) view;

        if (box.getType() == Pipe.PipeType.START_DOWN || box.getType() == Pipe.PipeType.START_UP || box.getType() == Pipe.PipeType.START_LEFT || box.getType() == Pipe.PipeType.START_LEFT) {
            return;
        }

        Pipe.PipeType nextType = nextBar.PeekNextType();

        boolean result = gameBoard.addPipeToBoard(box.getPoint(), nextType);

        if (result) {
            box.setType(nextBar.OnClickAction());
            box.AnimateClick();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + "makhina.ttf");

        TextView title = findViewById(R.id.title_text_box);
        title.setTypeface(tf);

        //get saved GameBoard or create new one if not exists
        gameBoard = getGameBoard();
        //register as observer
        gameBoard.addObserver(this);
        //create layout for the game board and load already positioned pipes
        InitializeBoard();
        DisplayGame();

        ImageButton fastForward = findViewById(R.id.fast_forward_button);
        fastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameBoard.getIsInGame() == false) {
                    timer.cancel();
                    shouldPlayAnimation = true;
                    gameBoard.startGame();
                }
                levelSpeedPerFrame = 16;
            }

        });

        //show and initialize "next pipes" box
        nextBar.InitializeBlockBar();
        nextBar.DrawBar();
        ScoresTable.loadFromDevice(MainActivity.this);

    }

    private GameBoard getGameBoard()
    {
        GameBoard newGameBoard = null;
        try {

            File gameBoardFile =  this.getApplicationContext().getFileStreamPath(SAVED_BOARD_FILE_NAME);
            if (gameBoardFile.exists())
            {
                FileInputStream fis = this.getApplicationContext().openFileInput(SAVED_BOARD_FILE_NAME);
                ObjectInputStream is = new ObjectInputStream(fis);
                Object dataFromFile = is.readObject();
                if (dataFromFile != null)
                {
                    newGameBoard = (GameBoard)dataFromFile;
                    newGameBoard.fixObserver();

                }
                is.close();
                fis.close();
                //we don't need this file anymore - delete it from the filesystem
                gameBoardFile.delete();
            }
        }
        catch (FileNotFoundException e) { System.out.println("in getGameBoard, FileNotFoundException:" + e.getMessage());}
        catch (IOException e) {System.out.println("in getGameBoard, IOException:" + e.getMessage());}
        catch (ClassNotFoundException e) {System.out.println("in getGameBoard, ClassNotFoundException:" + e.getMessage());}

        if (newGameBoard == null)
        {
            newGameBoard = new GameBoard(7);
        }

        return newGameBoard;
    }

    public void SaveGame() {
        if (gameBoard == null)
        {
            System.out.println("in SaveGame, gameBoard is null!");
            return;
        }
        try {
            //save GameBoard on the filesystem
            FileOutputStream fos = this.getApplicationContext().openFileOutput(SAVED_BOARD_FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(gameBoard);
            os.close();
            fos.close();
        }
        catch (IOException e) {
            System.out.println("in SaveGame, IOException:" + e.getMessage());
        }
    }

    public void UpdatePointBar() {
        TextView scoreTextView = findViewById(R.id.points_text_view);
        scoreTextView.setText(gameBoard.getTotalScore() + "");
    }

    public void startGameTimer()
    {
        int levelGraceTime = gameBoard.getCurrentLevel().getTimeBeforeFlow();
        int remainingGraceTime = levelGraceTime - gameBoard.getPassedGraceTime();
        timer = new CountDownTimer(remainingGraceTime * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                gameBoard.incrementPassedGraceTime();
            }

            public void onFinish() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shouldPlayAnimation = true;
                        gameBoard.startGame();
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //show the main dialog
        MainDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PauseGame();
    }

    public void InitializeBoard() {
        LinearLayout.LayoutParams rowLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams boxContainerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        boxContainerLayoutParams.weight = 1f;
        rowLinearLayoutParams.gravity = Gravity.FILL_HORIZONTAL;
        LinearLayout mainLinearLayout = findViewById(R.id.grid_linear);

        for (int j = 6; j > -1; j--) {
            LinearLayout row = new LinearLayout(MainActivity.this);
            row.setLayoutParams(rowLinearLayoutParams);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setWeightSum(7);

            for (int i = 6; i > -1; i--) {

                LinearLayout boxContainer = new LinearLayout(MainActivity.this);
                boxContainer.setLayoutParams(boxContainerLayoutParams);
                row.addView(boxContainer);

                BoxButton newButton = new BoxButton(MainActivity.this);
                newButton.setScaleX(1);
                newButton.setScaleY(1);
                newButton.setRotation(0);

                newButton.setPoint(new Point(i, j));
                newButton.setOnClickListener(MainActivity.this);
                layoutBoard.put(newButton.getPoint(), newButton);


                newButton.DrawPipe();
                boxContainer.addView(newButton);

            }
            mainLinearLayout.addView(row);
            mainLinearLayout.invalidate();
        }
    }

    public void DisplayGame() {
        SetLevelBackground(gameBoard.getLevelNumber());

        for (Map.Entry<Point, BoxButton> entry : layoutBoard.entrySet()) {
            BoxButton box = entry.getValue();
            Pipe currentPipe = gameBoard.getPipeByPoint(entry.getKey());
            if (currentPipe == null) {
                box.setType(null);
                box.DrawPipe();
            } else {
                box.setType(currentPipe.getPipeType());
                int numOfVisits = currentPipe.getNumOfVisits();
                if (numOfVisits > 0) {
                    box.SkipAnimation(numOfVisits);
                } else {
                    box.DrawPipe();
                }
            }
        }

        requierdBlocks.setNewRequiredAmount(gameBoard.getLeftPipesToComplete());
        requierdBlocks.updateDisplay();

        CreatePointsBar();
    }

    public void StartNewGame(boolean newGame) {
        requierdBlocks.setNewRequiredAmount(gameBoard.getCurrentLevel().getRequiredPipeLength());
        requierdBlocks.updateDisplay();
        levelSpeedPerFrame = gameBoard.getCurrentLevel().getFlowTimeInPipe();
        //check if should start from scratch or continue to next level
        if (newGame)
        {
            gameBoard.resetGame();
        } else {
            gameBoard.nextGame();
        }
        DisplayGame();
        startGameTimer();
    }

    public void PauseGame() {
        shouldPlayAnimation = false;

        if (gameBoard.getIsInGame()) {
            System.out.println("GAME PAUSED");
            Pipe currentFlowingPipe = gameBoard.getCurrentPipe();
            BoxButton currentFlowingBox = layoutBoard.get(currentFlowingPipe.getPosition());
            AnimationDrawable animation = (AnimationDrawable) currentFlowingBox.getDrawable();
            animation.stop();
        }else if (timer != null) {
            timer.cancel();
        }
        SaveGame();
    }

    public BoxButton getBoxFromPoint(Point point) {
        BoxButton ret = layoutBoard.get(point);
        if (ret != null) {
            return ret;
        }

        return null;
    }

    public BoxButton getBoxFromPipe(Pipe pipe) {
        return getBoxFromPoint(pipe.getPosition());
    }

    public void LevelDoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View levelDoneDialog = getLayoutInflater().inflate(R.layout.level_done_dialog, null);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + "makhina.ttf");

        TextView levelDoneTextView = levelDoneDialog.findViewById(R.id.dialog_title_text_view);
        levelDoneTextView.setText("Level " + (gameBoard.getLevelNumber()+1) + " Done");
        levelDoneTextView.setGravity(Gravity.CENTER);
        levelDoneTextView.setTextSize(50);
        levelDoneTextView.setPadding(0,0,0,40);


        levelDoneTextView.setTypeface(tf);
        LinearLayout dialogLinearLayout = levelDoneDialog.findViewById(R.id.dialog_main);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 40);

        Button nextLevelButton = new Button(MainActivity.this);
        nextLevelButton.setLayoutParams(params);
        nextLevelButton.setText("YAY");
        nextLevelButton.setPadding(40, 40, 40, 40);
        nextLevelButton.setTypeface(tf);
        nextLevelButton.setTextSize(20);
        nextLevelButton.setGravity(Gravity.CENTER);
        nextLevelButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        nextLevelButton.setBackgroundResource(R.drawable.menu_button_border);
        dialogLinearLayout.addView(nextLevelButton);

        builder.setView(levelDoneDialog);
        final AlertDialog Dialog = builder.show();

        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartNewGame(false);
                Dialog.dismiss();
            }
        });
    }

    public boolean shouldShowContinueButton()
    {
        return ( (gameBoard.getIsInGame() == true) || (gameBoard.getPassedGraceTime() != 0) );
    }

    public void closeOpenDialogs()
    {
        //close game over dialog
        if (gameOverAlertDialog != null)
        {
            if (gameOverAlertDialog.isShowing())
            {
                gameOverAlertDialog.dismiss();
            }
        }
    }

    public void MainDialog() {
        //close any open dialog
        closeOpenDialogs();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View mainDialog = getLayoutInflater().inflate(R.layout.level_done_dialog, null);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + "makhina.ttf");
        builder.setCancelable(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        TextView titleTextView = mainDialog.findViewById(R.id.dialog_title_text_view);
        titleTextView.setText("Pipe Dream");
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setTextSize(40);
        titleTextView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        titleTextView.setPadding(10, 10, 10, 40);
        titleTextView.setTypeface(tf);



        params.setMargins(0, 0, 0, 40);

        LinearLayout dialogMainLinearLayout = mainDialog.findViewById(R.id.dialog_main);


        Button continueButton = new Button(MainActivity.this);
        continueButton.setLayoutParams(params);
        continueButton.setText("Continue");
        continueButton.setPadding(40, 40, 40, 40);
        continueButton.setTypeface(tf);
        continueButton.setTextSize(20);
        continueButton.setGravity(Gravity.CENTER);
        continueButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        continueButton.setBackgroundResource(R.drawable.menu_button_border);


        Button newGameButton = new Button(MainActivity.this);
        newGameButton.setLayoutParams(params);
        newGameButton.setText("New Game");
        newGameButton.setPadding(40, 40, 40, 40);
        newGameButton.setTypeface(tf);
        newGameButton.setTextSize(20);
        newGameButton.setGravity(Gravity.CENTER);
        newGameButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        newGameButton.setBackgroundResource(R.drawable.menu_button_border);


        Button highScoreButton = new Button(MainActivity.this);
        highScoreButton.setLayoutParams(params);
        highScoreButton.setText("High Scores");
        highScoreButton.setPadding(40, 40, 40, 40);
        highScoreButton.setTypeface(tf);
        highScoreButton.setTextSize(20);
        highScoreButton.setGravity(Gravity.CENTER);
        highScoreButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        highScoreButton.setBackgroundResource(R.drawable.menu_button_border);


        Button quitButton = new Button(MainActivity.this);
        quitButton.setLayoutParams(params);
        quitButton.setText("Quit Game");
        quitButton.setPadding(40, 40, 40, 40);
        quitButton.setTypeface(tf);
        quitButton.setTextSize(20);
        quitButton.setGravity(Gravity.CENTER);
        quitButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        quitButton.setBackgroundResource(R.drawable.menu_button_border);


        if (shouldShowContinueButton()) {

            dialogMainLinearLayout.addView(continueButton);

        }
        dialogMainLinearLayout.addView(newGameButton);
        dialogMainLinearLayout.addView(highScoreButton);
        dialogMainLinearLayout.addView(quitButton);


        builder.setView(mainDialog);
        final AlertDialog Dialog = builder.show();


        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartNewGame(true);
                Dialog.dismiss();
            }
        });


        highScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, ActivityScores.class);
                startActivity(intent);
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog.dismiss();
                finish();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameBoard.getIsInGame()) {
                    shouldPlayAnimation = true;
                    Pipe currentFlowingPipe = gameBoard.getCurrentPipe();
                    BoxButton currentFlowingBox = layoutBoard.get(currentFlowingPipe.getPosition());
                    currentFlowingBox.AnimateFlow(currentFlowingPipe.getFlowDirection(), currentFlowingPipe.getNumOfVisits());
                } else {
                    startGameTimer();
                }
                Dialog.dismiss();
            }
        });
    }

    public void GameOverDialog(boolean isRecord) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View gameOverDialog = getLayoutInflater().inflate(R.layout.level_done_dialog, null);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + "makhina.ttf");

        if (isRecord == true) {
            //TODO: FINISH THIS SHIT
        }

        TextView levelDoneTextView = gameOverDialog.findViewById(R.id.dialog_title_text_view);
        levelDoneTextView.setText("Game Over");
        levelDoneTextView.setGravity(Gravity.CENTER);
        levelDoneTextView.setTextSize(60);

        levelDoneTextView.setPadding(10, 10, 10, 20);
        levelDoneTextView.setTypeface(tf);


        TextView points_text_view = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            points_text_view = new TextView(MainActivity.this, null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }

        LinearLayout pointsAndIconLine = new LinearLayout(MainActivity.this, null, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        pointsAndIconLine.setOrientation(LinearLayout.HORIZONTAL);

        points_text_view.setText("Your score: " + gameBoard.getTotalScore());
        points_text_view.setGravity(Gravity.LEFT);
        points_text_view.setPadding(10, 10, 10, 20);
        points_text_view.setTextSize(20);
        points_text_view.setTypeface(tf);

        Button tryAgianButton = new Button(MainActivity.this, null, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tryAgianButton.setText("I'm a loser");
        tryAgianButton.setPadding(40, 40, 40, 40);
        tryAgianButton.setTypeface(tf);
        tryAgianButton.setTextSize(30);
        tryAgianButton.setGravity(Gravity.CENTER);
        tryAgianButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tryAgianButton.setBackgroundResource(R.drawable.menu_button_border);


        LinearLayout dialogMainLinearLayout = gameOverDialog.findViewById(R.id.dialog_main);
        pointsAndIconLine.addView(points_text_view);
        dialogMainLinearLayout.addView(pointsAndIconLine);
        dialogMainLinearLayout.addView(tryAgianButton);

        builder.setView(gameOverDialog);
        gameOverAlertDialog = builder.show();
        tryAgianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainDialog();
            }
        });

    }

    public class NextBlockBar {
        Queue<BoxButton> nextPipeQueue = new LinkedList<>();

        public NextBlockBar() {
        }

        public Pipe.PipeType getRandomPipeType() {
            List<Pipe.PipeType> all_boxes = new ArrayList<>();

            all_boxes.add(Pipe.PipeType.TOP_LEFT);
            all_boxes.add(Pipe.PipeType.TOP_RIGHT);
            all_boxes.add(Pipe.PipeType.BOTTOM_LEFT);
            all_boxes.add(Pipe.PipeType.BOTTOM_RIGHT);
            all_boxes.add(Pipe.PipeType.CROSS);
            all_boxes.add(Pipe.PipeType.VERTICAL);
            all_boxes.add(Pipe.PipeType.HORIZONTAL);
            int rand = (int) (Math.random() * (7));
            return all_boxes.get(rand);
        }

        public void InitializeBlockBar() {
            for (int i = 0; i < 5; i++) {

                BoxButton newBox = new BoxButton(MainActivity.this);
                newBox.setType(getRandomPipeType());
                newBox.DrawPipe();
                newBox.setBackgroundResource(R.drawable.border_block_bar);
                newBox.setPadding(8, 0, 8, 0);
                nextPipeQueue.add(newBox);
            }
        }

        public Pipe.PipeType OnClickAction() {
            BoxButton selected = nextPipeQueue.remove();

            BoxButton newBox = new BoxButton(MainActivity.this);
            newBox.setType(getRandomPipeType());
            newBox.setPadding(8, 0, 8, 0);


            newBox.DrawPipe();
            newBox.setBackgroundResource(R.drawable.border_block_bar);
            nextPipeQueue.add(newBox);

            DrawBar();
            return selected.getType();
        }

        public void DrawBar() {
            LinearLayout nextBlockLayout = findViewById(R.id.next_block_bar);
            nextBlockLayout.removeAllViews();
            Iterator iter = nextPipeQueue.iterator();
            while (iter.hasNext()) {
                View current = (View) iter.next();
                nextBlockLayout.addView(current);
            }
            BoxButton nextPipe = (BoxButton) nextBlockLayout.getChildAt(0);
            nextPipe.setBackgroundResource(R.drawable.border_block_bar_next);

        }

        public Pipe.PipeType PeekNextType() {
            return nextPipeQueue.peek().getType();
        }
    }

    public void CreatePointsBar() {
        //point bar starts hereeee
        TextView point_bar = findViewById(R.id.points_text_view);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + "makhina.ttf");
        point_bar.setTypeface(tf);
        point_bar.setTextSize(50);
        point_bar.setGravity(Gravity.LEFT);
        point_bar.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        point_bar.setText(gameBoard.getTotalScore() + "");
    }

    public void SetLevelBackground(int levelNumber) {
        LinearLayout backgroundLayout = findViewById(R.id.background_layout);
        switch (levelNumber) {
            case 1:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_1);
                break;
            case 2:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_2);

                break;
            case 3:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_3);

                break;
            case 4:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_4);

                break;
            case 5:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_5);

                break;
            case 6:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_6);

                break;
            case 7:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_7);

                break;
            case 8:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_8);

                break;
            case 9:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_9);

                break;
            case 10:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_20);

                break;
            default:
                backgroundLayout.setBackgroundResource(R.drawable.background_level_4);
        }
    }

    public boolean IsRecord(int score) {
        List<ScoreRecord> records = ScoresTable.getAllScores();
        for (ScoreRecord record: records) {
            if (score > record.getScore()) {
             return true;
            }

        }
        return false;
    }
}
package com.hit.project.pipedream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import com.hit.project.pipedream.logic.GameBoard;
import com.hit.project.pipedream.logic.Pipe;
import com.hit.project.pipedream.logic.Point;
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

public class MainActivity extends Activity implements View.OnClickListener , Observer{
    GameBoard gameBoard = new GameBoard(7);
    Map<Point,BoxButton> layoutBoard = new HashMap<Point,BoxButton>();

    @Override
    public void update(Observable observable, Object o) {
        if (!(observable instanceof GameBoard)) {
            return;
        }
        Pipe currentPipe = gameBoard.getCurrentPipe();
       Pipe.FlowStatus currentPipeFlowStatus =  (Pipe.FlowStatus)o;
       switch (currentPipeFlowStatus) {
           case FLOW_STARTED_IN_PIPE:
               currentPipe.getPipeType();
//               currentPipe.g
               break;
           case FOUND_NEXT_PIPE:
               break;
           case END_OF_PIPE:
               break;
       }
    }

    NextBlockBar nextBar = new NextBlockBar();


    @Override
    public void onClick(View view) {
        BoxButton box = (BoxButton) view;
        Pipe.PipeType nextType = nextBar.OnClickAction();
        box.setType(nextType);
        box.DrawPipe();
        Toast.makeText(this, box.getPoint().toString(), Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + "makhina.ttf");
        TextView title = findViewById(R.id.title_text_box);
        gameBoard.addObserver(this);
        gameBoard.getFirstPipe();

        title.setTypeface(tf);

        CreateBoard();
        nextBar.InitializeBlockBar();
        nextBar.DrawBar();

        BoxButton boxi = getBoxFromPoint(new Point(2,2));
        if (boxi != null) {
            boxi.setType(Pipe.PipeType.TOP_LEFT);
            boxi.DrawPipe();
        }
    }

    public void CreateBoard() {
        LinearLayout.LayoutParams rowLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams boxContainerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        boxContainerLayoutParams.weight = 1f;
        rowLinearLayoutParams.gravity = Gravity.FILL_HORIZONTAL;
        LinearLayout mainLinearLayout = findViewById(R.id.grid_linear);

        //delete this
        BoxButton special = new BoxButton(MainActivity.this);
        special.setPoint(new Point(0, 0));
        special.setType(Pipe.PipeType.TOP_LEFT);
//        special.DrawPipe();
//------------------------

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

                //to delete entire if
                if (i == 3 && j == 3) {

//                    newButton.setOnClickListener(MainActivity.this);

                    boxContainer.addView(special);

                    continue;
                }
                //-----------------

                newButton.setPoint(new Point(i, j));
                newButton.setType(null);
                newButton.setOnClickListener(MainActivity.this);

                newButton.DrawPipe();
                boxContainer.addView(newButton);
                layoutBoard.put(newButton.getPoint(),newButton);

            }
            mainLinearLayout.addView(row);
            mainLinearLayout.invalidate();
        }
        special.AnimateFlow(Pipe.Directions.LEFT); //TODO DELETE

    }

    public BoxButton getBoxFromPoint(Point point) {
        LinearLayout layout = findViewById(R.id.grid_linear);
        BoxButton ret = layoutBoard.get(point);
        if (ret != null) {
            return ret;
        }

        return null;


    }



    class BoxButton extends ImageButton {
        Pipe.PipeType _type = null;
        Point _point = null;

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
                this.setImageResource(0);

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
                }
            } else {
                this.setImageResource(0);

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

        public void AnimateFlow(Pipe.Directions endDirection) {
            Pipe.PipeType type = getType();
            if ((Pipe.AvailableDirections.get(type) & endDirection.getVal()) != 0) {

                switch (type) {
                    case TOP_LEFT:
                        this.setImageResource(R.drawable.corner_animation);

                        if (endDirection == Pipe.Directions.UP) {
                            this.setRotation(180);

                        } else {  //DIRECTION IS LEFT
                            this.setRotation(90);
                            this.setScaleX(-1);
                            this.setImageResource(R.drawable.corner_animation);
                        }
                        break;
                    case TOP_RIGHT: //TODO FINISH THIS
                        this.setImageResource(R.drawable.corner_animation);
                        this.setRotation(180);
                        this.setScaleX(-1);
                        this.setImageResource(R.drawable.corner_animation);
                        if (endDirection == Pipe.Directions.UP) {

                        } else {  //DIRECTION IS RIGHT

                        }

                        break;
                    case BOTTOM_LEFT: //TODO: FINISH THIS
                        this.setImageResource(R.drawable.corner_animation);
                        if (endDirection == Pipe.Directions.DOWN) {

                        } else {  //DIRECTION IS LEFT

                        }
                        this.setScaleX(-1);
                        break;
                    case BOTTOM_RIGHT: //TODO: FINISH THIS
                        this.setImageResource(R.drawable.corner_animation);

                        this.setImageResource(R.drawable.corner_animation);
                        if (endDirection == Pipe.Directions.DOWN) {

                        } else {  //DIRECTION IS RIGHT

                        }
                        break;
                    case CROSS:
                        this.setImageResource(R.drawable.cross); //TODO: FIGURE OUT THIS SHIT
                        break;
                    case HORIZONTAL:
                        this.setImageResource(R.drawable.vertical_animation);
                        this.setRotation(90);
                        if (endDirection == Pipe.Directions.RIGHT) {
                            this.setScaleY(-1);
                        }

                        this.setRotation(90);
                        break;
                    case VERTICAL:
                        this.setImageResource(R.drawable.vertical_animation);
                        if (endDirection == Pipe.Directions.UP) {
                            this.setRotation(180);
                        }
                        break;
                }
                PipeAnimation anim = new PipeAnimation((AnimationDrawable) this.getDrawable()) {
                    @Override
                    public void onAnimationFinish() {

                    }

                    @Override
                    public void onAnimationStart() {

                    }
                };
                Toast.makeText(MainActivity.this, "ANIM TIME: " + anim.getTotalDuration(), Toast.LENGTH_LONG).show();

             ((AnimationDrawable) this.getDrawable()).start();


//        }
            }
        }
    }

    class NextBlockBar {
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
                    newBox.setBackgroundResource(R.drawable.border);
                    nextPipeQueue.add(newBox);
                }
            }

            public Pipe.PipeType OnClickAction() {
                BoxButton selected = nextPipeQueue.remove();

                BoxButton newBox = new BoxButton(MainActivity.this);
                newBox.setType(getRandomPipeType());
                newBox.DrawPipe();
                newBox.setBackgroundResource(R.drawable.border);
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
                    // do something with current
                }

            }


        }

    }

 abstract class PipeAnimation extends AnimationDrawable {

     /**
      * Handles the animation callback.
      */
     Handler mAnimationHandler;

     public PipeAnimation(AnimationDrawable aniDrawable) {
         /* Add each frame to our animation drawable */
         for (int i = 0; i < aniDrawable.getNumberOfFrames(); i++) {
             this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
         }
     }

     @Override
     public void start() {
         super.start();
         /*
          * Call super.start() to call the base class start animation method.
          * Then add a handler to call onAnimationFinish() when the total
          * duration for the animation has passed
          */
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

     /**
      * Gets the total duration of all frames.
      *
      * @return The total duration.
      */
     public int getTotalDuration() {

         int iDuration = 0;

         for (int i = 0; i < this.getNumberOfFrames(); i++) {
             iDuration += this.getDuration(i);
         }

         return iDuration;
     }

     public abstract void onAnimationFinish();

     public abstract void onAnimationStart();

 }


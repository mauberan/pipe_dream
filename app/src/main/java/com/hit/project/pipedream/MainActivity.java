package com.hit.project.pipedream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
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

import static com.hit.project.pipedream.logic.Pipe.FlowStatus.FLOW_STARTED_IN_PIPE;

public class MainActivity extends Activity implements View.OnClickListener , Observer{
    GameBoard gameBoard = new GameBoard(7);
    Map<Point,BoxButton> layoutBoard = new HashMap<>();
    int required_blocks_for_level = 4;

    @Override
    public void update(Observable observable, Object o) {
        if (!(observable instanceof GameBoard)) {
            return;
        }

        Pipe.FlowStatus currentPipeFlowStatus =  (Pipe.FlowStatus)o;

       switch (currentPipeFlowStatus) {
           case FLOW_STARTED_IN_PIPE:
               MainActivity.this.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Pipe currentPipe = gameBoard.getCurrentPipe();
                       BoxButton box = getBoxFromPipe(currentPipe);
                       box.AnimateFlow(currentPipe.getFlowDirection());
                   }
               });
                System.out.println(String.format("pipe location:%s flow direction:%s",gameBoard.getCurrentPipe().getPosition(),gameBoard.getCurrentPipe().getFlowDirection()));
//               currentPipe.g
               break;
           case FOUND_NEXT_PIPE:
                 Toast.makeText(this, "FOUND NEXT PIPE", Toast.LENGTH_LONG).show();
                //TODO: CREATE SCORE VIEW AND UPDATE IT HERE
               break;
           case END_OF_PIPE:
               Toast.makeText(this, "GAME OVER", Toast.LENGTH_LONG).show();
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               ResetBoard();

               break;
       }
    }

    NextBlockBar nextBar = new NextBlockBar();


    @Override
    public void onClick(View view) {

        BoxButton box = (BoxButton) view;

        if (box.getType() == Pipe.PipeType.START_DOWN || box.getType() == Pipe.PipeType.START_UP || box.getType() == Pipe.PipeType.START_LEFT || box.getType() == Pipe.PipeType.START_LEFT) {
            return;
        }

        Pipe.PipeType nextType = nextBar.PeekNextType();

        boolean result = gameBoard.addPipeToBoard(box.getPoint(),nextType);

        if (result) {
            box.setType(nextBar.OnClickAction());
            box.AnimateClick();

        }
//        box.DrawPipe();

//        Toast.makeText(this, box.getPoint().toString(), Toast.LENGTH_LONG).show()
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + "makhina.ttf");
        // add api if here for lower versions
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        getWindow().setStatusBarColor(Color.BLUE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        TextView title = findViewById(R.id.title_text_box);
        title.setTypeface(tf);
        gameBoard.addObserver(this);
        CreateBoard();
        ResetBoard();
        nextBar.InitializeBlockBar();
        nextBar.DrawBar();




        BoxButton horiz = getBoxFromPoint(new Point(2,3));
//            horiz.setType(Pipe.PipeType.VERTICAL);

//
//        BoxButton topRight = getBoxFromPoint(new Point(3,3));
//        topRight.setType(Pipe.PipeType.TOP_RIGHT);
//
////        BoxButton selected_pipe1 = getBoxFromPoint(new Point(1,3));
////        selected_pipe1.setType(Pipe.PipeType.START_DOWN);
//
//        BoxButton topLeft = getBoxFromPoint(new Point(4,3));
//        topLeft.setType(Pipe.PipeType.TOP_LEFT);
//
//        BoxButton bottomLeft = getBoxFromPoint(new Point(4,4));
//        bottomLeft.setType(Pipe.PipeType.BOTTOM_LEFT);
//
//        BoxButton bottomRight = getBoxFromPoint(new Point(3,4));
//        bottomRight.setType(Pipe.PipeType.BOTTOM_RIGHT);

//        BoxButton selected_pipe5 = getBoxFromPoint(new Point(5,3));
//        selected_pipe5.setType(Pipe.PipeType.VERTICAL);
//        BoxButton selected_pipe6 = getBoxFromPoint(new Point(5,2));
//        selected_pipe6.setType(Pipe.PipeType.CROSS);
//        BoxButton selected_pipe7 = getBoxFromPoint(new Point(1,2));
//        selected_pipe7.setType(Pipe.PipeType.START_DOWN);
//        BoxButton selected_pipe8 = getBoxFromPoint(new Point(2,2));
//        selected_pipe8.setType(Pipe.PipeType.START_UP);
//        BoxButton selected_pipe9 = getBoxFromPoint(new Point(3,2));
//        selected_pipe9.setType(Pipe.PipeType.START_LEFT);
//        BoxButton selected_pipe10 = getBoxFromPoint(new Point(4,2));
//        selected_pipe10.setType(Pipe.PipeType.START_RIGHT);
//
//        topRight.DrawPipe();
////        selected_pipe1.DrawPipe();
//        bottomLeft.DrawPipe();
//        bottomRight.DrawPipe();
//        horiz.DrawPipe();
//
//        horiz.AnimateFlow(Pipe.Directions.DOWN);
//        selected_pipe5.DrawPipe();
//        selected_pipe6.DrawPipe();
//        selected_pipe7.DrawPipe();
//        selected_pipe8.DrawPipe();
//        selected_pipe9.DrawPipe();
//        selected_pipe10.DrawPipe();

//        selected_pipe.AnimateFlow(Pipe.Directions.UP);
    }

    public void CreateBoard() {
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


                newButton.setPoint(new Point(i, j));
                newButton.setOnClickListener(MainActivity.this);
                layoutBoard.put(newButton.getPoint(),newButton);


                newButton.DrawPipe();
                boxContainer.addView(newButton);

            }
            mainLinearLayout.addView(row);
            mainLinearLayout.invalidate();
        }


    }

    public void ResetBoard() {
        for (Map.Entry<Point,BoxButton> entry: layoutBoard.entrySet()) {
            BoxButton box = entry.getValue();
            box.setType(null);
            box.DrawPipe();
        }
        LinearLayout requierdBlocksLayout = findViewById(R.id.requierd_blocks);
        LinearLayout.LayoutParams imageButtonLayoutParams = new LinearLayout.LayoutParams(60, 60);
        imageButtonLayoutParams.weight = 1f;
        imageButtonLayoutParams.gravity = Gravity.CENTER;

        requierdBlocksLayout.removeAllViews();

        for (int i=0; i < required_blocks_for_level; i++) {
            BoxButton requierdBlock = new BoxButton(MainActivity.this);
            requierdBlock.setLayoutParams(imageButtonLayoutParams);
            requierdBlock.setAdjustViewBounds(false);
            requierdBlock.setScaleType(ImageView.ScaleType.FIT_CENTER);
            requierdBlock.setType(Pipe.PipeType.HORIZONTAL);
            requierdBlock.setPadding(3,3,3,3);
            requierdBlock.DrawPipe();
            requierdBlocksLayout.addView(requierdBlock);
        }


        gameBoard.resetGame();
        Pipe first = gameBoard.getFirstPipe();
        BoxButton firstBox = getBoxFromPoint(first.getPosition());
        firstBox.setType(first.getPipeType());
        firstBox.DrawPipe();
        gameBoard.startGame();
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

    class BoxButton extends ImageButton {
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

        public void AnimateFlow(final Pipe.Directions endDirection) {
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

//                            this.setScaleX(-1);
                            this.setScaleY(-1);

                            this.setImageResource(R.drawable.corner_animation);
                        }
                        break;
                    case TOP_RIGHT: //TODO FINISH THIS
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
                        this.setImageResource(R.drawable.cross_first_flow_animation); //TODO: FIGURE OUT THIS SHIT
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

                PipeAnimation cad = new PipeAnimation(
                        (AnimationDrawable) this.getDrawable()) {
                    @Override
                    public void onAnimationStart() {
                Toast.makeText(MainActivity.this,endDirection.toString(), Toast.LENGTH_LONG).show();
                        System.out.println("ANIM START");
                    }

                    @Override
                    public void onAnimationFinish(){
                        System.out.println("ANIM FINISHED");

                        gameBoard.notifyPipeIsFull();
                    }
                };
                this.setImageDrawable(cad);
                ((AnimationDrawable) this.getDrawable()).start();

//                Toast.makeText(MainActivity.this, "ANIM TIME: " + anim.getTotalDuration(), Toast.LENGTH_LONG).show();
//        }
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
                    newBox.setBackgroundResource(R.drawable.border_block_bar);
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
                }
                    TextView point_bar = findViewById(R.id.points_text_view);
                Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + "makhina.ttf");
point_bar.setTypeface(tf);
point_bar.setTextSize(50);

            }
            public Pipe.PipeType PeekNextType() {
                return nextPipeQueue.peek().getType();

            }


        }


 public abstract class PipeAnimation extends AnimationDrawable {

     /**
      * Handles the animation callback.
      */
     Handler mAnimationHandler;

     public PipeAnimation(AnimationDrawable aniDrawable) {
         /* Add each frame to our animation drawable */
//         for (int i = 0; i < aniDrawable.getNumberOfFrames(); i++) {
//             this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
//         }
         int i = 0;
         for (; i < 18; i++) {
                 this.addFrame(aniDrawable.getFrame(i), 0);

         }

         //time manipulation
         for (; i < aniDrawable.getNumberOfFrames(); i++) {

                 this.addFrame(aniDrawable.getFrame(i), 25);

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

}

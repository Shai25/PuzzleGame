package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();
//public PuzzleBoard solution;

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap, View parent) {
        int width = parent.getWidth();
        puzzleBoard = new PuzzleBoard(imageBitmap, width);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {


        int n = random.nextInt(19)+10;
        while (n != 0) {
            if (animation == null && puzzleBoard != null) {


                animation = puzzleBoard.neighbours();
                int asize = animation.size();
                int index = random.nextInt(asize - 1) + 0;
                puzzleBoard = animation.get(index);
                animation = null;

            }
        n--;
        }

        invalidate();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve() {


        if(puzzleBoard==null)return;
        puzzleBoard.reset();
        PriorityQueue<PuzzleBoard>pq=new PriorityQueue<>(11, new Comparator<PuzzleBoard>() {
            @Override
            public int compare(PuzzleBoard t1, PuzzleBoard t2) {


                return t1.priority()-t2.priority();
            }
        });


        int count=0;
        pq.add(puzzleBoard);
        while (!pq.isEmpty())
        {
            Log.i("count: ",Integer.toString(count));
            count++;
            PuzzleBoard temp=pq.peek();
            pq.remove();
            if(!temp.resolved())
            {
                ArrayList<PuzzleBoard>listtemp=temp.neighbours();
                Iterator<PuzzleBoard> it=listtemp.iterator();
                while(it.hasNext())
                {
                    PuzzleBoard tp=it.next();
                    if(!tp.equals(temp.getPrevBoard()))
                    {
                        pq.add(tp);

                    }
                }
            }
            else
            {
                ArrayList<PuzzleBoard> resultlist=new ArrayList<PuzzleBoard>();
                while (temp!=null)
                {
                    resultlist.add(temp);
                    temp=temp.getPrevBoard();
                }
                Collections.reverse(resultlist);
                animation=resultlist;
                pq.clear();
                invalidate();
                break;
            }
        }

    }

}

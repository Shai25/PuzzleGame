package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Iterator;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    int steps=0;

    PuzzleBoard sol;
    PuzzleBoard(Bitmap bitmap, int parentWidth) {



        bitmap=Bitmap.createScaledBitmap(bitmap,parentWidth,parentWidth,true);
        tiles=new ArrayList<PuzzleTile>(NUM_TILES*NUM_TILES);
        tiles.ensureCapacity(NUM_TILES*NUM_TILES);
        int size=parentWidth/NUM_TILES;
        int i,j,count=0;
        for( i=0;i<NUM_TILES;i++)
        {
            for(j=0;j<NUM_TILES;j++)
            {
                if(i==NUM_TILES-1 && j==NUM_TILES-1)
                {
                    tiles.add(null);
                    continue;
                }
                Bitmap bit=Bitmap.createBitmap(bitmap,j*size,i*size,size,size);
                tiles.add(new PuzzleTile(bit,count));
                count++;
            }

        }


        sol=this;
    }

    PuzzleBoard previousBoard;

    PuzzleBoard(PuzzleBoard otherBoard) {
        previousBoard=otherBoard;
        steps=otherBoard.steps+1;
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
    }

    public PuzzleBoard getPrevBoard()
    {
        return previousBoard;
    }
    public void reset() {

        previousBoard=null;
        steps=0;
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {

        int arr[]=new int[4];

        ArrayList<PuzzleBoard> pb=new ArrayList<PuzzleBoard>();
       // ArrayList<PuzzleTile> pt=new ArrayList<PuzzleTile>();


        PuzzleBoard copy=new PuzzleBoard(this);

        //pb.add(copy);
        for(int i=0;i<NUM_TILES*NUM_TILES;i++) {
            if (tiles.get(i) == null)
            {

                arr[0]=i-1;
                arr[1]=i+3;
                arr[2]=i+1;
                arr[3]=i-3;

                for(int j=0;j<4;j++)
                {
                    if(arr[j]>=0 && arr[j]<9)
                    {
                        copy.tiles.set(i,copy.tiles.get(arr[j]));
                        copy.tiles.set(arr[j],null);
                        pb.add(copy);
                        copy=new PuzzleBoard(this);
                    }
                }

            }
        }
        return pb;
    }

    public int man_distance()
    {
        int dis=0,count=0;
        Iterator<PuzzleTile> it=tiles.iterator();
        while(it.hasNext())
        {
            PuzzleTile t=it.next();
            if(t!=null)
            {
                int ox,oy,nx,ny;
                ox=t.getNumber()%NUM_TILES;
                oy=t.getNumber()/NUM_TILES;
                nx=count%NUM_TILES;
                ny=count/NUM_TILES;
                dis+=Math.abs(ox-nx)+Math.abs(oy-ny);
            }
            count++;
        }
        return dis;
    }

    public int priority() {


        /*int sum=0;
        for(int i=0;i<NUM_TILES*NUM_TILES;i++)
        { for(int j=0;j<NUM_TILES*NUM_TILES;j++)
        {
            if(tiles.get(i)==sol.tiles.get(j))
            {

            }
        }

        }
        int distance=10*sum ;*/
        int len=man_distance()+steps;
        return len;
    }

}

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jadex.android.puzzle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import jadex.android.exampleproject.bdiv3.R;


/**
 * TileView: a View-variant designed for handling arrays of "icons" or other
 * drawables.
 * 
 */
public class TileView extends View {

    /**
     * Parameters controlling the size of the tiles and their range within view.
     * Width/Height are in pixels, and Drawables will be scaled to fit to these
     * dimensions. X/Y Tile Counts are the number of tiles that will be drawn.
     */

    protected static int mScaledTileSize;

    /**
     * game field is mTileCount * mTileCount
     */
    protected static int mTileCount;

    private static int mXOffset;
    private static int mYOffset;


    /**
     * A hash that maps integer handles specified by the subclasser to the
     * drawable that will be used for that reference
     */
    private Bitmap[] mTileArray; 

    /**
     * A two-dimensional array of integers in which the number represents the
     * index of the tile that should be drawn at that locations
     */
    private int[][] mTileGrid;

    private final Paint mPaint = new Paint();

	private static int mTileSize;

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);

        mTileCount = 5;
        mScaledTileSize = 1;
        mTileSize = 53;

        mTileGrid = new int[mTileCount][mTileCount];

        a.recycle();
    }

    public TileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Rests the internal array of Bitmaps used for drawing tiles, and
     * sets the maximum index of tiles to be inserted
     * 
     * @param tilecount
     */
    
    public void resetTiles(int tilecount) {
    	mTileArray = new Bitmap[tilecount];
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	
    	int xTileSize = (int) Math.floor(w / mTileCount);
    	int yTileSize = (int) Math.floor(h / mTileCount);
    	
    	mScaledTileSize = Math.min(xTileSize, yTileSize);
    	
//        mXTileCount = (int) Math.floor(w / mTileSize);
//        mYTileCount = (int) Math.floor(h / mTileSize);

//        mXOffset = ((w - (mTileSize * mTileCount)) / 2);
//        mYOffset = ((h - (mTileSize * mYTileCount)) / 2);
    	
    	mXOffset = ((w - mScaledTileSize * mTileCount) / 2);
    	mYOffset = ((h - mScaledTileSize * mTileCount) / 2);;

        mTileGrid = new int[mTileCount][mTileCount];
        clearTiles();
        
        for (int i = 0; i < mTileArray.length; i++)
		{
        	Bitmap bitmap = mTileArray[i];
        	
        	if (bitmap != null) {
        		mTileArray[i] = Bitmap.createScaledBitmap(mTileArray[i], mScaledTileSize, mScaledTileSize, false);
        	}
		}
    }

    /**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key
     * @param tile
     */
    public void loadTile(int key, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, mTileSize, mTileSize);
        tile.draw(canvas);
        
        mTileArray[key] = bitmap;
    }

    /**
     * Resets all tiles to 0 (empty)
     * 
     */
    public void clearTiles() {
        for (int x = 0; x < mTileCount; x++) {
            for (int y = 0; y < mTileCount; y++) {
                setTile(0, x, y);
            }
        }
    }

    /**
     * Used to indicate that a particular tile (set with loadTile and referenced
     * by an integer) should be drawn at the given x/y coordinates during the
     * next invalidate/draw cycle.
     * 
     * @param tileindex
     * @param x
     * @param y
     */
    public void setTile(int tileindex, int x, int y) {
        mTileGrid[x][y] = tileindex;
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < mTileCount; x += 1) {
            for (int y = 0; y < mTileCount; y += 1) {
                if (mTileGrid[x][y] > 0) {
                    canvas.drawBitmap(mTileArray[mTileGrid[x][y]], 
                    		mXOffset + x * mScaledTileSize,
                    		mYOffset + y * mScaledTileSize,
                    		mPaint);
                }
            }
        }

    }

}

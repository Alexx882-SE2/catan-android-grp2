package com.group2.catan_android;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class demoboard extends AppCompatActivity {

    // hexagon icon measurements (square)
    static int hexagonSize = 220;
    static int halfHexagonSize = hexagonSize/2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_demoboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ConstraintLayout constraintLayout = findViewById(R.id.main);

        //TODO: Insert these drawables according to the playing field from the server (not ready yet)
        int[] ids = {
                R.drawable.hexagon_brick_svg,
                R.drawable.hexagon_wood_svg,
                R.drawable.hexagon_stone_svg,
                R.drawable.hexagon_brick_svg,
                R.drawable.hexagon_sheep_svg,
                R.drawable.hexagon_sheep_svg,
                R.drawable.hexagon_wheat_svg,
                R.drawable.hexagon_wood_svg,
                R.drawable.hexagon_sheep_svg,
                R.drawable.hexagon_wheat_svg,
                R.drawable.hexagon_wheat_svg,
                R.drawable.hexagon_wood_svg,
                R.drawable.hexagon_stone_svg,
                R.drawable.hexagon_brick_svg,
                R.drawable.hexagon_sheep_svg,
                R.drawable.hexagon_stone_svg,
                R.drawable.hexagon_brick_svg,
                R.drawable.hexagon_wheat_svg,
                R.drawable.hexagon_wood_svg
        };

        ImageView[] hexagonViews = new ImageView[ids.length];

        for (int i = 0; i < ids.length; i++){
            ImageView hexagonView = new ImageView(this);
            hexagonView.setId(ViewCompat.generateViewId());
            Log.d("ImageView IDs", "ImageView ID: " + hexagonView.getId()); //log HexagonID
            hexagonView.setImageDrawable(ContextCompat.getDrawable(this, ids[i]));

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams((int) (hexagonSize), (int) (hexagonSize));
            constraintLayout.addView(hexagonView, params);
            hexagonViews[i] = hexagonView;
        }

        constraintLayout.post(() -> {
            int layoutWidth = constraintLayout.getWidth(); //screen width and height
            int layoutHeight = constraintLayout.getHeight();
            applyConstraints(constraintLayout, hexagonViews, layoutWidth, layoutHeight);
        });
    }

    private void applyConstraints(ConstraintLayout constraintLayout, ImageView[] hexagonViews, int layoutWidth, int layoutHeight) {
        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        int margin = -24;
        int thirdRow = (layoutWidth / 2) - (2*hexagonSize + halfHexagonSize) - (2*margin);
        int secondRow = thirdRow + halfHexagonSize + (margin/2);
        int firstRow = thirdRow + hexagonSize + margin;
        int firstTop = (layoutHeight/2) - (2*hexagonSize+halfHexagonSize) - (4*margin) - getStatusBarHeight();

        int prevDrawable = ConstraintSet.PARENT_ID;
        int firstDrawableInRow = hexagonViews[0].getId();

        for (int i = 0; i < hexagonViews.length; i++) {
            ImageView hexagonView = hexagonViews[i];

            // first hexagons in each line
            if (i == 0 || i == 3 || i == 7 || i == 12 || i == 16) { // start of new line 3-4-5-4-3
                switch(i) {
                    case 0:
                    case 16:
                        set.connect(hexagonView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, firstRow);
                        break;
                    case 3:
                    case 12:
                        set.connect(hexagonView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, secondRow);
                        break;
                    case 7:
                        set.connect(hexagonView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START, thirdRow);
                        break;
                }

                firstDrawableInRow = hexagonView.getId(); // beginning of current line

                // if not the first-element in the FIRST line consider topMargin to the hex above
                if (i != 0) {
                    set.connect(hexagonView.getId(), ConstraintSet.TOP, prevDrawable, ConstraintSet.BOTTOM, margin*2);
                }
            } else {
                set.connect(hexagonView.getId(), ConstraintSet.START, prevDrawable, ConstraintSet.END, margin);
                set.connect(hexagonView.getId(), ConstraintSet.TOP, firstDrawableInRow, ConstraintSet.TOP, 0);
            }

            // first-line first-element constraint top
            if (i == 0) {
                set.connect(hexagonView.getId(), ConstraintSet.TOP, prevDrawable, ConstraintSet.TOP, firstTop);
            }

            prevDrawable = hexagonView.getId();
        }

        set.applyTo(constraintLayout);
    }

    //TODO: find alternative Method to get StatusBarHeight
    // Status Bar Height needed to center Board
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}



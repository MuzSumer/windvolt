/*
    This file is part of windvolt.org.

    Copyright (c) 2020 Max Sumer

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.windvolt.diagram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.snackbar.Snackbar;

import org.windvolt.R;

public class WhoIsWho extends AppCompatActivity {

    Drawable icon;
    BoxTreeLayout diagram;

    int w = 480;
    int h = 720;

    final int tab = 240;

    TextView root;
    int rx, ry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.page1_hello); // values
            actionBar.setTitle(title);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();

            display.getRealMetrics(displayMetrics);

            w = displayMetrics.widthPixels;
            h = displayMetrics.heightPixels;
        }


        icon = AppCompatResources.getDrawable(this, R.drawable.gui_roundbox);






        RelativeLayout.LayoutParams diagramLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        diagram = new BoxTreeLayout(this);


        setContentView(diagram, diagramLayout);

        drawTree();

        /* switch to fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_settings, new PreferenceFragment())
                .commit();
         */
    }


    private void drawTree() {
        root = addRoot("POWER", 20, 40);

        addChild(" Ampirion ", "apirina.de",15);

        addChild(" Tennet TSO ", "tennet.de",45);
        addChild(" TransnetBW ", "transnet.de",-15);

        addChild(" 50Hertz ", "50hertz.de", -45);
    }




    private class BoxTreeLayout extends RelativeLayout {
        Paint paint;

        public BoxTreeLayout(Context context) {
            super(context);
            paint = new Paint();
        }


        /* draw */
        protected void dispatchDraw(Canvas canvas) {

            /* diagram symbol */
            paint.setColor(Color.RED);
            paint.setStrokeWidth(8);

            canvas.drawLine(0, 0, 20, 20, paint);
            canvas.drawLine(20, 20, 20, 0, paint);
            canvas.drawLine(20, 20, 0, 20, paint);


            // draw connections
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(4);

            int size = getChildCount();
            for (int v = 1; v < size; v++) {
                View tv = getChildAt(v);

                int x1 = tv.getLeft();
                int y1 = tv.getTop() + tv.getHeight()/2;

                canvas.drawLine(rx, ry, x1, y1, paint);
            }


            super.dispatchDraw(canvas);
        }
    }




    private TextView addRoot(String name, int pw, int ph) {
        TextView tv = new TextView(this);
        tv.setText(name);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.width = 100;
        params.height = 40;

        rx = pw * w / 100;
        ry = ph * h / 100;

        params.leftMargin = rx - params.width;
        params.topMargin = ry - params.height/2;


        diagram.addView(tv, params);

        return tv;
    }

    private TextView addChild(String name, String action, int angle) {

        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);


        TextView tv = new TextView(this);
        tv.setText(name);
        tv.setContentDescription(action);

        //tv.setBackground(icon);
        //tv.setBackgroundColor(Color.LTGRAY);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.width = 320;
        params.height = 100;


        params.leftMargin = (int) (rx + tab * Math.cos(Math.toRadians(angle)));
        params.topMargin = (int) (ry + tab * Math.sin(Math.toRadians(angle)));

        ImageView i = new ImageView(this);
        i.setImageResource(R.drawable.windvolt_small);

        //l.setBackground(icon);

        l.addView(i);
        l.addView(tv);
        diagram.addView(l, params);

        tv.setOnClickListener(new doClick(action));

        return tv;
    }


    private class doClick implements View.OnClickListener {

        String action;

        public doClick(String set_action) { action = set_action; }
        @Override
        public void onClick(View v) {
            Snackbar.make(diagram, action, Snackbar.LENGTH_SHORT).show();
        }
    }

}
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
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.snackbar.Snackbar;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramModel;
import org.windvolt.diagram.model.DiagramStore;

public class BusinessModel extends AppCompatActivity {

    Drawable icon;
    StructogramLayout diagram;
    WebView content;

    int w = 480;
    int h = 720;


    //final int TAG_TAB = 40;

    String focusId = "";

    final int CHILD_WIDTH = 440;
    final int CHILD_FOCUS_WIDTH = 480;


    DiagramStore store;

    protected void createStoreData() {

        store = new DiagramStore();


        String root = store.addChild("", "wind", "wind", "the wind",
                R.drawable.windvolt_small, R.string.diagram_flow0);

        String c1 = store.addChild(root, "producer", "producer", "the producer",
                R.drawable.windvolt_small, R.string.diagram_flow1);

        String c2 = store.addChild(c1, "distributor", "distributor", "the distributor",
                R.drawable.windvolt_small, R.string.diagram_flow0);

        String c3 = store.addChild(c2, "trader", "trader", "the trader",
                R.drawable.windvolt_small, R.string.diagram_flow0);

        String c4 = store.addChild(c3, "reseller", "reseller", "the reseller",
                R.drawable.windvolt_small, R.string.diagram_flow0);

        String c5 = store.addChild(c4, "consumer", "consumer", "the consumer",
                R.drawable.windvolt_small, R.string.diagram_flow0);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_flow);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.page2_hello); // values
            actionBar.setTitle(title);
        }

        icon = AppCompatResources.getDrawable(this, R.drawable.gui_roundbox);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();

            display.getRealMetrics(displayMetrics);

            w = displayMetrics.widthPixels;
            h = displayMetrics.heightPixels;
        }





        createStoreData();



        content = findViewById(R.id.diagram_flow);
        content.setBackgroundColor(getColor(R.color.diagram_flow));



        diagram = new StructogramLayout(this);
        diagram.setBackgroundColor(getColor(R.color.diagram_flow));

        RelativeLayout.LayoutParams diagramLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        LinearLayout layout = findViewById(R.id.flow_container);
        layout.addView(diagram, diagramLayout);


        /*
        final WindowMetrics metrics = WindowManager.getCurrentWindowMetrics();
        // Gets all excluding insets


         */


        addChildModelView("100", 0);

    }

    protected void setFocus(View view, String id) {


        focusId = id;

        DiagramModel model = store.findModel(id);
        if (null == model) return;

        // load html
        String value = getString(Integer.parseInt(model.getAdress())); // values


        content.loadDataWithBaseURL(null, value, "text/html", "utf-8", null);

        String c_id = model.getChildren();
        View found = findChildModelView(c_id);


        if (null == found) {
            int size = diagram.getChildCount();
            addChildModelView(c_id, size);
        } else {
            removeChildModelView(id);
        }

        layoutChildModelFlow();



        Snackbar.make(view, focusId, Snackbar.LENGTH_SHORT).show();
    }


    private void layoutChildModelFlow() {
        // layout children

        int size = diagram.getChildCount();

        for (int p=0; p<size; p++) {
            View layout = (View) diagram.getChildAt(p);

            int ww = CHILD_WIDTH;
            String p_id = layout.getContentDescription().toString();

            if (p_id.equals(focusId)) ww = CHILD_FOCUS_WIDTH;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);

            params.leftMargin = (w - ww)/2;
            params.topMargin = 40 + 120 * p;
            params.width = ww;
            params.height = 100;

            layout.setLayoutParams(params);
        }
    }

    private View findChildModelView(String id) {
        View found = null;

        int size = diagram.getChildCount();

        for (int p=0; p<size; p++) {
            View layout = (View) diagram.getChildAt(p);

            String p_id = layout.getContentDescription().toString();

            if (p_id.equals(id)) found = layout;
        }

        return found;
    }

    /* --------------------------------windvolt-------------------------------- */

    public void addChildModelView(String id, int position) {

        DiagramModel model = store.findModel(id);
        if (null == model) return;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        Drawable roundbox = getResources().getDrawable(R.drawable.gui_roundbox);

        layout.setBackground(roundbox);
        layout.setPadding(8, 8, 8, 8);

        ImageView image = new ImageView(this);
        int res = Integer.parseInt(model.getSymbol());
        image.setImageResource(res);

        TextView text = new TextView(this);
        text.setPadding(8, 8, 8, 8);
        //text.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Headline4); // 34sp
        //text.setTextAppearance(this, R.style.TextAppearance_AppCompat_Large); // 22sp
        text.setTextAppearance(this, R.style.TextAppearance_AppCompat_Headline); //24sp

        text.setText(model.getSubject());
        //text.setText(w + "/" + h);

        layout.setContentDescription(id);

        layout.addView(image);
        layout.addView(text);

        layout.setOnClickListener(new OnFocus(id));


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.leftMargin = (w - CHILD_WIDTH)/2;
        params.topMargin = 40 + 120 * position;
        params.width = CHILD_WIDTH;
        params.height = 100;

        diagram.addView(layout, params);

    }//createChildView

    public void removeChildModelView(String id) {

        DiagramModel model = store.findModel(id);
        if (null == model) return;

        String c_id = model.getChildren();
        View found = findChildModelView(c_id);

        if (null == found) {}
        else {

            removeChildModelView(c_id);

            diagram.removeView(found);

        }

    }//removeChildren

    class OnFocus implements View.OnClickListener {

        String id;

        public OnFocus(String set_id) { id = set_id; }
        @Override
        public void onClick(View view) {

            setFocus(view, id);

        }
    }//OnFocus



    /* --------------------------------windvolt-------------------------------- */



    private class StructogramLayout extends RelativeLayout {
        Paint paint;

        public StructogramLayout(Context context) {
            super(context);
            paint = new Paint();
        }// StructogramLayout


        /* draw arrows */
        protected void dispatchDraw(Canvas canvas) {

            /* diagram symbol */
            paint.setColor(Color.RED);
            paint.setStrokeWidth(8);

            canvas.drawLine(0, 0, 20, 20, paint);
            canvas.drawLine(20, 20, 20, 0, paint);
            canvas.drawLine(20, 20, 0, 20, paint);



            /* draw connections */
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(4);

            int size = this.getChildCount();
            for (int v = 0; v < size-1; v++) {
                View v0 = getChildAt(v);
                View v1 = getChildAt(v+1);

                int w = v0.getRight() - v0.getLeft();
                int x0 = v0.getLeft() + w/2;
                int x1 = x0;

                int y0 = v0.getBottom();
                int y1 = v1.getTop();


                canvas.drawLine(x0, y0, x1, y1, paint);
                canvas.drawLine(x1 - 8, y1 - 8, x1, y1, paint);
                canvas.drawLine(x1 + 8, y1 - 8, x1, y1, paint);

            }// for





            /* draw loop connection */

            /*
            paint.setColor(Color.BLUE);

            if (size > 2) {

                // draw stub with tip
                View v0 = getChildAt(0);
                int x0 = v0.getRight();
                int h0 = v0.getBottom()-v0.getTop();
                int y0 = v0.getTop() + h0/2;

                canvas.drawLine(x0, y0, x0 + TAG_TAB, y0, paint);
                canvas.drawLine(x0, y0, x0 + 8, y0 - 8, paint);
                canvas.drawLine(x0, y0, x0 + 8, y0 + 8, paint);


                // draw stub
                View v1 = getChildAt(3);
                int x1 = v1.getRight();
                int h1 = v1.getBottom()-v1.getTop();
                int y1 = v1.getTop() + h1/2;

                canvas.drawLine(x1, y1, x1 + TAG_TAB, y1, paint);


                // connect stubs
                canvas.drawLine(x0 + TAG_TAB, y0, x1 + TAG_TAB, y1, paint);
            }
             */



            super.dispatchDraw(canvas);
        }//dispatchDraw
    }

}
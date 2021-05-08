/*
    This file is part of windvolt.

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
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramModel;
import org.windvolt.diagram.model.DiagramStore;

public class BusinessModel extends AppCompatActivity {

    Drawable windvolt_icon;
    FlowTreeLayout diagram;
    WebView content;

    int w = 480;
    int h = 720;


    //final int TAG_TAB = 40;

    final int CHILD_HEIGHT = 72;
    final int CHILD_MARGIN = 88;

    String focusId = "";

    final int CHILD_WIDTH = 360;

    DiagramStore store;


    protected void createStore() {

        store = new DiagramStore();

        //* try to load model */
        //String url = "https://windvolt.org/economy.xml";
        //String url = "https://github.com/MuzSumer/windvolt/blob/main/fastlane/models/economy.xml";
        String url = "https://github.com/MuzSumer/windvolt/tree/main/fastlane/models/economy.xml";

        if (store.loadStoreModel(this, url)) {

        } else {
            Toast.makeText(this, "using local model", Toast.LENGTH_LONG).show();
            createLocalStore();
        }



    }//createStore

    private void createLocalStore() {
        String root = store.addChild("", "wind",
                "Der Wind", R.drawable.windvolt_small, R.string.diagram_flow0,
                "wind");

        String c1 = store.addChild(root, "producer",
                "Kollektoren", R.drawable.page0_v10, R.string.diagram_flow1,
                "producer");

        String c2 = store.addChild(c1, "distributor",
                "Netze", R.drawable.wiw_net, R.string.diagram_flow2,
                "distributor");

        String c3 = store.addChild(c2, "trader",
                "Handel", R.drawable.wiw_exchange, R.string.diagram_flow3,
                "trader");

        String c4 = store.addChild(c3, "reseller",
                "Versorger", R.drawable.wiw_com, R.string.diagram_flow4,
                "reseller");

        String c5 = store.addChild(c4, "consumer",
                "Verbraucher", android.R.drawable.ic_menu_myplaces, R.string.diagram_flow5,
                "consusmer");
    }//createLocalStore



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

        windvolt_icon = AppCompatResources.getDrawable(this, R.drawable.app_rbox);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();

            display.getRealMetrics(displayMetrics);

            w = displayMetrics.widthPixels;
            h = displayMetrics.heightPixels;
        }





        createStore();



        content = findViewById(R.id.diagram_flow);
        content.setBackgroundColor(getColor(R.color.diagram_flow));



        diagram = new FlowTreeLayout(this);
        diagram.setBackgroundColor(getColor(R.color.diagram_flow));


        LinearLayout layout = findViewById(R.id.flow_container);
        layout.addView(diagram);


        addModelView(store.getRootId());
        setFocus(store.getRootId(), false);

    }

    protected void setFocus(String id, boolean expand) {


        boolean hasFocus = id.equals(focusId);

        focusId = id;

        DiagramModel model = store.findModel(id);
        if (null == model) return;

        // load html
        String value = getString(Integer.parseInt(model.getAdress())); // values

        content.loadDataWithBaseURL(null, value, "text/html", "utf-8", null);

        String c_id = model.getChildren();
        View found = findModelView(c_id);


        if (null == found) {
            if (hasFocus) {
                if (expand) {
                    addModelView(c_id);

                    // changes behaviour
                    setFocus(c_id, false);
                }
            }

        } else {
            removeChildren(id);
        }

        layoutModelFlow();

        //Snackbar.make(view, focusId, Snackbar.LENGTH_SHORT).show();
    }


    private void layoutModelFlow() {
        // layout children

        Drawable roundbox = getResources().getDrawable(R.drawable.app_rbox);
        Drawable focusbox = getResources().getDrawable(R.drawable.app_rbox_focus);

        int size = diagram.getChildCount();

        for (int p=0; p<size; p++) {
            View layout = diagram.getChildAt(p);



            layout.setBackground(roundbox);

            int ww = CHILD_WIDTH;
            String p_id = layout.getContentDescription().toString();

            if (p_id.equals(focusId)) layout.setBackground(focusbox);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);

            params.leftMargin = (w - CHILD_WIDTH)/2;
            params.topMargin = 20 + CHILD_MARGIN * p;
            params.width = CHILD_WIDTH;
            params.height = CHILD_HEIGHT;

            layout.setLayoutParams(params);
        }
    }//layoutModelFlow

    private View findModelView(String id) {
        View found = null;

        int size = diagram.getChildCount();

        for (int p=0; p<size; p++) {
            View layout = diagram.getChildAt(p);

            String p_id = layout.getContentDescription().toString();
            if (p_id.equals(id)) found = layout;

            // detect multiple id error here
        }

        return found;
    }//findModelView

    /* --------------------------------windvolt-------------------------------- */

    public void addModelView(String id) {

        DiagramModel model = store.findModel(id);
        if (null == model) return;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);


        layout.setPadding(8, 8, 8, 8);

        ImageView image = new ImageView(this);
        int res = Integer.parseInt(model.getSymbol());
        image.setImageResource(res);

        TextView text = new TextView(this);
        text.setPadding(8, 8, 8, 8);
        //text.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Headline4); // 34sp
        //text.setTextAppearance(this, R.style.TextAppearance_AppCompat_Large); // 22sp
        //text.setTextAppearance(this, R.style.TextAppearance_AppCompat_Headline); //24sp

        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setText(model.getSubject());
        //text.setText(w + "/" + h);

        layout.setContentDescription(id);

        layout.addView(image);
        layout.addView(text);

        layout.setOnClickListener(new OnFocus(id));

        diagram.addView(layout);

    }//addModelView

    public void removeChildren(String id) {

        DiagramModel model = store.findModel(id);
        if (null == model) return;

        String c_id = model.getChildren();
        View found = findModelView(c_id);

        if (null == found) {}
        else {

            removeChildren(c_id);

            diagram.removeView(found);

        }

    }//removeChildren

    class OnFocus implements View.OnClickListener {

        String id;

        public OnFocus(String set_id) { id = set_id; }
        @Override
        public void onClick(View view) {

            setFocus(id, true);

        }
    }//OnFocus



    /* --------------------------------windvolt-------------------------------- */



    private class FlowTreeLayout extends RelativeLayout {
        Paint paint;

        public FlowTreeLayout(Context context) {
            super(context);
            paint = new Paint();
        }// StructogramLayout


        /* draw arrows */
        protected void dispatchDraw(Canvas canvas) {

            //* diagram symbol */
            //drawDiagramSybol(canvas);




            //* draw connections */
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(4);

            int size = getChildCount();

            for (int v = 0; v < size-1; v++) {

                View v0 = getChildAt(v);
                View v1 = getChildAt(v+1);


                int w0 = v0.getRight() - v0.getLeft();
                int x0 = v0.getLeft() + w0/2;

                int y0 = v0.getBottom();
                int y1 = v1.getTop();


                canvas.drawLine(x0, y0, x0, y1, paint);
                canvas.drawLine(x0 - 8, y1 - 8, x0, y1, paint);
                canvas.drawLine(x0 + 8, y1 - 8, x0, y1, paint);

            }// for





            //* draw interconnections */

            /*
            paint.setColor(Color.BLUE);

            if (size > 11) {

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
                x1 = v1.getRight();
                int h1 = v1.getBottom()-v1.getTop();
                int y1 = v1.getTop() + h1/2;

                canvas.drawLine(x1, y1, x1 + TAG_TAB, y1, paint);


                // connect stubs
                canvas.drawLine(x0 + TAG_TAB, y0, x1 + TAG_TAB, y1, paint);
            }
             */



            super.dispatchDraw(canvas);
        }//dispatchDraw


        // unused since v2020
        private void drawDiagramSybol(Canvas canvas) {
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(8);

            canvas.drawLine(0, 0, 20, 20, paint);
            canvas.drawLine(20, 20, 20, 0, paint);
            canvas.drawLine(20, 20, 0, 20, paint);
        }
    }//FlowTreeLayout

}
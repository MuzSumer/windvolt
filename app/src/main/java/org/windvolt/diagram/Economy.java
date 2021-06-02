/*
    This file is part of windvolt.

    created 2020 by Max Sumer

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
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramActivity;
import org.windvolt.diagram.model.DiagramModel;

public class Economy extends DiagramActivity {

    final String MODEL_URL = "https://windvolt.eu/model/economy/0diagram.xml";

    boolean ALLOW_BEEP = false;

    Drawable windvolt_icon;
    FlowTreeLayout diagram;
    WebView web;

    int w = 480;
    int h = 720;


    final int CHILD_HEIGHT = 72;
    final int CHILD_MARGIN = 88;

    String focus_id = "";

    final int CHILD_WIDTH = 360;



    /* --------------------------------windvolt-------------------------------- */


    public void createStore() {

        loadModel(this, MODEL_URL);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_economy);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.page2_hello); // values
            actionBar.setTitle(title);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();

            display.getRealMetrics(displayMetrics);

            w = displayMetrics.widthPixels;
            h = displayMetrics.heightPixels;
        }

        windvolt_icon = AppCompatResources.getDrawable(this, R.drawable.app_rbox);


        web = findViewById(R.id.diagram_flow);
        web.setBackgroundColor(getColor(R.color.diagram_background));
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        diagram = new FlowTreeLayout(this);
        diagram.setBackgroundColor(getColor(R.color.diagram_background));


        LinearLayout layout = findViewById(R.id.flow_container);
        layout.addView(diagram);




        createStore();

    }//onCreate


    @Override
    public void setFocus(String id, boolean expand) {

        if (id == null) {
            id = getStore().getRootId();
        }

        boolean hasFocus = id.equals(focus_id);
        focus_id = id;

        DiagramModel focus = getStore().findModel(id);
        if (null == focus) return;

        View layout = findModelView(id);
        if (layout == null) {
            addModelView(id);
        }

        // load html
        web.loadUrl(focus.getAddress());

        String c_id = focus.getChildren();
        View child = findModelView(c_id);


        if (null == child) {
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

        layoutDiagram();
        doBeep();

        //Snackbar.make(view, focusId, Snackbar.LENGTH_SHORT).show();
    }//setFocus


    private void layoutDiagram() {
        // layout children

        //Drawable roundbox = getResources().getDrawable(R.drawable.app_rbox);
        //Drawable focusbox = getResources().getDrawable(R.drawable.app_rbox_focus);

        Drawable roundbox = AppCompatResources.getDrawable(this, R.drawable.app_rbox);
        Drawable focusbox = AppCompatResources.getDrawable(this, R.drawable.app_rbox_focus);


        int size = diagram.getChildCount();

        for (int p=0; p<size; p++) {
            View layout = diagram.getChildAt(p);

            String p_id = layout.getContentDescription().toString();

            if (p_id.equals(focus_id)) {
                layout.setBackground(focusbox);
            } else {
                layout.setBackground(roundbox);
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);

            params.leftMargin = (w - CHILD_WIDTH)/2;
            params.topMargin = 20 + CHILD_MARGIN * p;
            params.width = CHILD_WIDTH;
            params.height = CHILD_HEIGHT;

            layout.setLayoutParams(params);
        }
    }//layoutDiagram

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

        DiagramModel model = getStore().findModel(id);
        if (null == model) return;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);


        layout.setPadding(8, 8, 8, 8);


        ImageView image = new ImageView(this);
        image.setPadding(4, 2, 4, 2);
        loadViewImage(image, model.getSymbol());


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

        DiagramModel model = getStore().findModel(id);
        if (null == model) return;

        String c_id = model.getChildren(); // single schild only
        View found = findModelView(c_id);

        if (found != null) {

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



    private static class FlowTreeLayout extends RelativeLayout {
        Paint paint;

        public FlowTreeLayout(Context context) {
            super(context);
            paint = new Paint();
        }// StructogramLayout


        /* draw arrows */
        protected void dispatchDraw(Canvas canvas) {

            //* diagram symbol */
            drawDiagramSybol(canvas);


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

            final int TAG_TAB = 50;
            final int TAG_POS = 4;

            paint.setColor(Color.BLUE);

            if (size > TAG_POS) {

                // draw stub
                View v0 = getChildAt(1);
                int x0 = v0.getRight();
                int h0 = v0.getBottom() - v0.getTop();
                int y0 = v0.getTop() + h0 / 2;

                canvas.drawLine(x0, y0, x0 + TAG_TAB, y0, paint);

                /*
                // draw arrow
                canvas.drawLine(x0, y0, x0 + 8, y0 - 8, paint);
                canvas.drawLine(x0, y0, x0 + 8, y0 + 8, paint);
                 */



                // draw stub
                View v1 = getChildAt(TAG_POS);
                int x1 = v1.getRight();
                int h1 = v1.getBottom() - v1.getTop();
                int y1 = v1.getTop() + h1 / 2;

                canvas.drawLine(x1, y1, x1 + TAG_TAB, y1, paint);


                // draw arrow
                canvas.drawLine(x1, y1, x1 + 8, y1 - 8, paint);
                canvas.drawLine(x1, y1, x1 + 8, y1 + 8, paint);


                // connect stubs
                canvas.drawLine(x0 + TAG_TAB, y0, x1 + TAG_TAB, y1, paint);
            }



            super.dispatchDraw(canvas);
        }//dispatchDraw


        // unused since v2020
        private void drawDiagramSybol(Canvas canvas) {
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(8);

            canvas.drawLine(0, 0, 20, 20, paint);
            canvas.drawLine(20, 20, 0, 40, paint);
            canvas.drawLine(20, 20, 40, 0, paint);
        }
    }//FlowTreeLayout



    private void doBeep() {
        if (ALLOW_BEEP) {
            ToneGenerator beep = new ToneGenerator(AudioManager.FLAG_PLAY_SOUND, 80);
            beep.startTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE, 400);
        }
    }
}
package org.windvolt.diagram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramActivity11;
import org.windvolt.diagram.model.DiagramModel11;

public class Diagram extends DiagramActivity11 {

    final String MODEL_URL = "https://windvolt.eu/model/dossier/de/0diagram.xml";

    ScrollView scroll;
    LinearLayout content;

    DiagramLayout diagram;
    int diagram_width, diagram_height;

    String root_id;
    String focus_id;


    int show_sources = 0;
    int show_targets = 0;

    class DiagramLayout extends RelativeLayout {

        Paint paint = new Paint();

        public DiagramLayout(Context context) {
            super(context);
        }

        protected void dispatchDraw(Canvas canvas) {

            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(4);

            int n = diagram.getChildCount();

            Cell focus = (Cell) diagram.getChildAt(0);


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(diagram_width, diagram_height);


            params.width = focus.cellWidth();
            params.height = focus.cellHeight();

            params.leftMargin = (diagram_width - params.width)/2;
            params.topMargin = (diagram_height - params.height)/2;

            focus.setLayoutParams(params);


            
            super.dispatchDraw(canvas);
        }

    }

    class Cell extends LinearLayout {
        String id = null;

        TextView title;
        ImageView image;
        TextView subject;

        int cell_width = 160;
        int cell_height = 160;

        DiagramModel11 model;

        public Cell(Context context, String set_id) {
            super(context);
            id = set_id;
            setContentDescription(id);


            setOrientation(LinearLayout.VERTICAL);

            setBackground(AppCompatResources.getDrawable(context, R.drawable.app_roundbox));
            setPadding(4, 4, 4, 4);

            setGravity(Gravity.CENTER_HORIZONTAL);




            model = getStore().findModel(id);

            // title
            title = new TextView(context);
            {
                title.setPadding(2, 2, 2, 2);
                title.setTextSize(12);
                //text.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Headline4); // 34sp
                //text.setTextAppearance(this, R.style.TextAppearance_AppCompat_Large); // 22sp
                //text.setTextAppearance(this, R.style.TextAppearance_AppCompat_Headline); //24sp

                title.setText(model.getTitle());
                title.setGravity(Gravity.CENTER);
            }

            image = new ImageView(context);
            {
                image.setPadding(2, 2, 2, 2);
                loadViewImage(image, model.getSymbol());
            }

            subject = new TextView(context);
            {
                subject.setPadding(2, 2, 2, 2);
                subject.setTextSize(11);
                subject.setLines(3);

                subject.setText(model.getSubject());
                subject.setGravity(Gravity.CENTER);
            }

            addView(title);
            addView(image);
            addView(subject);


        }//new

        public int cellWidth() {
            cell_width = 160;
            {
                int l = model.getTitle().length();
                int tw = l * 18;
                cell_width = Math.max(cell_width, tw);

                int iw = image.getWidth();
                cell_width = Math.max(cell_width, iw);

            }
            return cell_width;

        }

        public int cellHeight() {
            cell_height = 160;
            {
                int h = 18;
                h += image.getHeight();
                h += subject.getHeight();

                cell_height = Math.max(cell_height, h);
            }

            return cell_height;
        }

        public int mid_x() {
            int dx = getRight() - getLeft();
            return getRight() + dx/2;
        }

        public int mid_y() {
            int dy = getBottom() - getTop();
            return getTop() + dy/2;
        }

    }



    @Override
    public String getNamespace() {
        return MODEL_URL;
    }

    @Override
    public void createStore() {

        loadRemoteModel(this, getNamespace());
    }//createStore

    @Override
    public void setFocus(String id, boolean expand) {
        if (id == null) {
            id = getStore().getRootId();
            root_id = id;
        }

        boolean hasFocus = id.equals(focus_id);
        focus_id = id;

        // remove current views
        diagram.removeAllViews();

        // add root
        relateCell(null, id);

        expandTargets(id);
    }//setFocus

    private void expandTargets(String souce_id) {

        DiagramModel11 model = getStore().findModel(souce_id);
        if (model == null) {
            return;
        }

        // add targets
        String targets = model.getTargets();
        if (!targets.isEmpty()) {
            String[] alltargets = targets.split(",");

            for (String target_id : alltargets) {

                if (!target_id.isEmpty()) {
                    relateCell(souce_id, target_id);
                }

            }//target
        }//targets
    }

    @Override
    public void onBackPressed() {
        DiagramModel11 parent = getStore().findParent(focus_id);
        if (null == parent) {
            super.onBackPressed();
        } else {

            String parent_id = parent.getId();
            setFocus(parent_id, true);
        }
    }//onBackPressed

    // create complex child view and relate it
    public void relateCell(String parent_id, String id) {
        DiagramModel11 model = getStore().findModel(id);
        if (model == null) {
            return;
        }

        Cell outer = new Cell(this, id);
        outer.setOnClickListener(setFocus);

        diagram_width = scroll.getWidth();
        diagram_height = scroll.getHeight();


        diagram.addView(outer);


    }//relateCell


    private final SetFocus setFocus = new SetFocus();
    private class SetFocus implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String id = view.getContentDescription().toString();
            setFocus(id, false);
        }
    }//SetFocus


    private Cell findModelView(String id) {
        Cell found = null;

        int size = diagram.getChildCount();

        for (int p=0; p<size; p++) {
            Cell cell = (Cell) diagram.getChildAt(p);

            String p_id = cell.getContentDescription().toString();
            if (p_id.equals(id)) {
                found = cell;
            }

            // detect multiple id error here
        }

        return found;
    }//findModelView

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_diagram);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.diagram_app); // values
            actionBar.setTitle(title);
        }


        scroll = findViewById(R.id.diagram_scroll);
        content = findViewById(R.id.diagram_content);

        diagram = new DiagramLayout(this);
        diagram.setBackgroundColor(getColor(R.color.diagram_background));

        content.addView(diagram);


        createStore();
    }


}
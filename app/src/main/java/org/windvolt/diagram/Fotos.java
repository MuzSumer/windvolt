package org.windvolt.diagram;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramActivity;
import org.windvolt.diagram.model.DiagramModel;

public class Fotos extends DiagramActivity {

    final String MODEL_URL = "https://windvolt.eu/shop/gallery/0diagram.xml";

    ScrollView scroll;
    LinearLayout diagram;

    String focus_id;

    @Override
    public String getNamespace() {
        return MODEL_URL;
    }

    @Override
    public void createStore() {

        loadRemoteModel(this, getNamespace());
    }

    @Override
    public void setFocus(String id, boolean expand) {
        if (id == null) {
            id = getStore().getRootId();
        }
        if (id.isEmpty()) {
            id = getStore().getRootId();
        }

        DiagramModel model = getStore().findModel(id);
        if (model == null) {
            return;
        }


        diagram.removeAllViews();


        // add focus children
        String targets = model.getTargets();

        if (targets.isEmpty()) {
            addImageview(id);
        } else {
            String[] alltargets = targets.split(",");

            for (String target_id : alltargets) {

                if (!target_id.isEmpty()) {
                    addModelView(target_id);
                }
            }//target
        }//targets

        focus_id = id;
    }//setFocus


    @Override
    public void onBackPressed() {
        DiagramModel parent = getStore().findParent(focus_id);

        if (null == parent) {
            super.onBackPressed();
        } else {

            // one up
            String parent_id = parent.getId();
            setFocus(parent_id, true);
        }
    }//onBackPressed


    private void addModelView(String id) {
        DiagramModel model = getStore().findModel(id);
        if (model == null) {
            return;
        }

        Drawable roundbox = AppCompatResources.getDrawable(this, R.drawable.app_roundbox);

        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.HORIZONTAL);
        outer.setContentDescription(id);

        outer.setPadding(8,8,8,8);
        outer.setBackground(roundbox);

        ImageView symbol = new ImageView(this);
        symbol.setPadding(8,8,8,8);

        loadViewImage(symbol, model.getContent(), 200, 120);



        LinearLayout innertexts = new LinearLayout(this);
        innertexts.setOrientation(LinearLayout.VERTICAL);

        innertexts.setPadding(8,8,8,8);

        // title
        TextView title = new TextView(this);
        title.setPadding(8,8,8,8);

        title.setText(model.getTitle());


        // subject
        TextView subject = new TextView(this);
        subject.setPadding(8,8,8,8);

        subject.setText(model.getSubject());


        innertexts.addView(title);
        innertexts.addView(subject);



        outer.addView(symbol);
        outer.addView(innertexts);

        outer.setOnClickListener(onFocus);

        diagram.addView(outer);
    }//addViewModel

    private void addImageview(String id) {


        DiagramModel model = getStore().findModel(id);
        if (model == null) {
            return;
        }

        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);

        ImageView image = new ImageView(this);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(scroll.getWidth(), scroll.getHeight());
        image.setLayoutParams(params);

        loadViewImage(image, model.getContent());

        //image.setOnClickListener(new OnFocus(id));

        cell.addView(image);
        diagram.addView(cell);

    }//addImageview

    private final OnFocus onFocus = new OnFocus();
    private class OnFocus implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String id = view.getContentDescription().toString();

            DiagramModel model = getStore().findModel(id);
            if (model == null) {
                return;
            }

            setFocus(id, true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_fotos);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.app_fotos); // values
            actionBar.setTitle(title);
        }

        scroll = findViewById(R.id.fotos_scroll);
        diagram = findViewById(R.id.fotos_content);

        createStore();
    }
}
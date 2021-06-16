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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramActivity;
import org.windvolt.diagram.model.DiagramModel;

public class News extends DiagramActivity {

    final String MODEL_URL = "https://windvolt.eu/model/news/0diagram.xml";

    String focus_id;

    LinearLayout diagram;


    @Override
    public String getNamespace() {
        return MODEL_URL;
    }

    @Override
    public void createStore() {

        // load model
        loadRemoteModel(this, getNamespace());

    }//createStore

    @Override
    public void setFocus(String id, boolean expand) {

        if (id == null) {
            id = getStore().getRootId();
        }
        if (id.isEmpty()) {
            id = getStore().getRootId();
        }

        DiagramModel focus = getStore().findModel(id);
        if (focus == null) {
            return;
        }

        // cleanup
        diagram.removeAllViews();


        // add focus targets
        String targets = focus.getTargets();

        if (!targets.isEmpty()) {
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

            String parent_id = parent.getId();
            setFocus(parent_id, true);

        }
    }//onBackPressed


    /* --------------------------------windvolt-------------------------------- */


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

    // complex view
    public void addModelView(String id) {


        DiagramModel model = getStore().findModel(id);
        if (null == model) return;

        Drawable roundbox = AppCompatResources.getDrawable(this, R.drawable.app_roundbox);

        LinearLayout outer = new LinearLayout(this);
        outer.setBackground(roundbox);
        outer.setOrientation(LinearLayout.HORIZONTAL);
        outer.setPadding(4, 4, 4, 4);


        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(4, 4, 4, 4);


        ImageView image = new ImageView(this);
        image.setPadding(4, 16, 4, 8);

        String symbol = model.getSymbol();
        loadViewImage(image, symbol, 80, 80);


        TextView title = new TextView(this);
        title.setPadding(8, 8, 8, 8);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setText(model.getTitle());


        TextView subject = new TextView(this);
        subject.setPadding(8, 8, 8, 8);
        subject.setTextSize(17);

        subject.setGravity(Gravity.CENTER_VERTICAL);
        subject.setText(model.getSubject());
        subject.setTextColor(Color.parseColor("#0277BD"));

        //text.setText(w + "/" + h);

        outer.setContentDescription(id);

        outer.addView(image);


        inner.addView(subject);
        inner.addView(title);

        outer.addView(inner);

        outer.setOnClickListener(new OnFocus(id));

        diagram.addView(outer);

    }//addModelView

    class OnFocus implements View.OnClickListener {

        String id;

        public OnFocus(String set_id) { id = set_id; }

        @Override
        public void onClick(View view) {

            DiagramModel model = getStore().findModel(id);
            if (model == null) {
                return;
            }

            if (model.getTargets().isEmpty()) {

                // open address
                String address = model.getContent();
                if (!address.isEmpty()) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(address)));
                }
            } else {

                // dive into category
                setFocus(id, false);
            }

        }//onClick

    }//OnFocus


    /* --------------------------------windvolt-------------------------------- */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_news);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.app_news); // values
            actionBar.setTitle(title);
        }



        // create diagram
        diagram = findViewById(R.id.diagram_news);

        createStore();
    }//onCreate

}
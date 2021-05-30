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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.windvolt.R;
import org.windvolt.diagram.model.Diagram;
import org.windvolt.diagram.model.DiagramModel;

public class News extends Diagram {

    final String MODEL_URL = "https://windvolt.eu/model/news/0diagram.xml";

    LinearLayout diagram;


    public void createStore() {

        // load model
        loadModel(this, MODEL_URL);

    }//createStore

    public void setFocus(String any_id, boolean expand) {

        for (int p=0; p< getStore().storeSize(); p++) {

            DiagramModel model = getStore().getModel(p);

            String id = model.getId();

            View layout = findModelView(id);
            if (layout == null) {
                addModelView(id);
            }
        }
    }


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

        Drawable roundbox = AppCompatResources.getDrawable(this, R.drawable.app_rbox);

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
        loadViewImage(image, symbol);


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
            if (model == null) { return; }

            String address = model.getAddress();
            if (!address.isEmpty()) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(address)));
            }
        }
    }//OnFocus


    /* --------------------------------windvolt-------------------------------- */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_news);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.title_news); // values
            actionBar.setTitle(title);
        }



        // create diagram
        diagram = findViewById(R.id.diagram_news);

        createStore();
    }//onCreate

}
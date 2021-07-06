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

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.content.res.AppCompatResources;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramActivity11;
import org.windvolt.diagram.model.DiagramModel;

public class Gallery extends DiagramActivity11 {


    final String MODEL_URL = "https://windvolt.eu/model/dossier/de/0diagram.xml";

    boolean ALLOW_BEEP = false;


    final String DIAGRAM_NAME = "who is who";
    final String DIAGRAM_PATH_DELIM = ">";


    ImageView diagram_symbol;
    TextView diagram_path;
    TextView diagram_title;
    TextView diagram_subject;



    LinearLayout diagram_space;
    WebView web;

    String focus_id;


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
        }

        boolean hasFocus = id.equals(focus_id);
        focus_id = id;

        DiagramModel focus = getStore().findModel(id);



        web.loadUrl(focus.getContent());

        // set focus title and subject
        diagram_title.setText(focus.getTitle());
        diagram_subject.setText(focus.getSubject());


        // remove current views
        diagram_space.removeAllViews();


        // add targets
        String targets = focus.getTargets();
        if (!targets.isEmpty()) {
            String[] alltargets = targets.split(",");

            for (String target_id : alltargets) {

                if (!target_id.isEmpty()) {
                    addTargetModelView(focus, target_id);
                }

            }//target
        }//targets

        // calculate path
        String path = focus.getTags();

        DiagramModel parent = getStore().findParent(id);
        while (null != parent) {

            String tag = parent.getTags();
            path = prepose(path, tag);


            String parent_id = parent.getId();
            parent = getStore().findParent(parent_id);
        }

        if (path.contains(DIAGRAM_NAME + DIAGRAM_PATH_DELIM)) {
            path = path.substring(DIAGRAM_NAME.length() + DIAGRAM_PATH_DELIM.length());
        }

        diagram_path.setText(path);
    }//setFocus



    @Override
    public void onBackPressed() {
        DiagramModel parent = getStore().findParent(focus_id);
        if (null == parent) {
            super.onBackPressed();
        } else {
            doBeep();

            String parent_id = parent.getId();
            setFocus(parent_id, true);
        }
    }//onBackPressed


    /* --------------------------------windvolt-------------------------------- */

    //* create complex child view */
    public void addTargetModelView(DiagramModel parent, String id) {
        DiagramModel model = getStore().findModel(id);
        if (model == null) {
            return;
        }


        LinearLayout layout = new LinearLayout(this);

        {
            layout.setOrientation(LinearLayout.HORIZONTAL);

            layout.setBackground(AppCompatResources.getDrawable(this, R.drawable.app_roundbox));
            layout.setPadding(8, 8, 8, 8);
        }



        ImageView image = new ImageView(this);
        {
            image.setPadding(2, 2, 2, 2);
            loadViewImage(image, model.getSymbol());
        }



        TextView text = new TextView(this);
        {
            text.setPadding(8, 8, 8, 8);
            //text.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Headline4); // 34sp
            //text.setTextAppearance(this, R.style.TextAppearance_AppCompat_Large); // 22sp
            text.setTextAppearance(this, R.style.TextAppearance_AppCompat_Headline); //24sp

            text.setText(model.getTitle());
        }


        layout.addView(image);
        layout.addView(text);

        layout.setContentDescription(id);
        layout.setOnClickListener(setFocus);

        diagram_space.addView(layout);
    }//createChildView

    private final SetFocus setFocus = new SetFocus();
    private class SetFocus implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String id = view.getContentDescription().toString();

            doBeep();
            setFocus(id, false);
        }
    }//SetFocus

    private final LevelUpFocus doLevelUp = new LevelUpFocus();
    private class LevelUpFocus implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String id = view.getContentDescription().toString();

            DiagramModel parent = getStore().findParent(id);
            if (null != parent) {
                String parent_id = parent.getId();
                setFocus(parent_id, false);
            }
        }
    }//LevelUpFocus

    private final OpenFocus openFocus = new OpenFocus();
    private static class OpenFocus implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String id = v.getContentDescription().toString();

        }
    }//OpenFocus


    /* --------------------------------windvolt-------------------------------- */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_gallery);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.page1_hello); // values
            actionBar.setTitle(title);
        }


        // diagram elements
        diagram_symbol = findViewById(R.id.gallery_symbol);
        diagram_path = findViewById(R.id.gallery_path);

        diagram_title = findViewById(R.id.gallery_title);
        diagram_subject = findViewById(R.id.gallery_subject);

        diagram_space = findViewById(R.id.gallery_content);
        web = findViewById(R.id.gallery_dossier);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            web.setBackgroundColor(getColor(R.color.diagram_background));
        }

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // unused
        //diagram_title.setOnClickListener(doOpenFocus);


        // create the store

        createStore();


    }//onCreate

    // preposes value to t
    private String prepose(String t, String value) {
        return value + t;
    }//prepose


    private void doBeep() {
        if (ALLOW_BEEP) {
            ToneGenerator beep = new ToneGenerator(AudioManager.FLAG_PLAY_SOUND, 80);
            beep.startTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE, 400);
        }
    }
}
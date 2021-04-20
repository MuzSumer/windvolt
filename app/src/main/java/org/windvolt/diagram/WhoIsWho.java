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

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramModel;
import org.windvolt.diagram.model.DiagramStore;

public class WhoIsWho extends AppCompatActivity {

    final String DIAGRAM_NAME = "who is who";
    final String DIAGRAM_PATH_DELIM = ">";
    DiagramStore store;

    ImageView diagram_symbol;
    TextView diagram_path;
    TextView diagram_title;
    TextView diagram_subject;



    LinearLayout diagram_space;
    WebView diagram_web;

    String focus_id;

    boolean beep = false;

    private void createStoreData() {
        int symbol = R.drawable.windvolt_small;


        String root = store.addChild("", DIAGRAM_NAME,"windvolt", "Windenergie Galerie",
                symbol, R.string.windvolt);


        // net
        {

            int net = R.drawable.wiw_net;

            String netz = store.addChild(root, "net", "Netzbetreiber", "Die deutschen Netzbetreiber",
                    net, R.string.net_0);



            store.addChild(netz, "","50Hertz", "50Hertz Transmission GmbH",
                    net, R.string.net_50herz);
            store.addChild(netz, "","Amprion", "Amprion GmbH",
                    net, R.string.net_ampirion);
            store.addChild(netz, "","Tennet", "Tennet TSO",
                    net, R.string.net_tennet);
            store.addChild(netz, "","Transnet BW", "Transnet BW GmbH",
                    net, R.string.net_transnet);
        }

        // pricing
        {
            int stock = R.drawable.wiw_exchange;

            store.addChild(root, "exc", "Börse", "Strombörse EEX",
                    stock, R.string.com_stock);
        }

        // com
        {
            int com = R.drawable.wiw_com;

            String konzern = store.addChild(root, "com","Versorger", "Stromversorger in Deutschland",
                    com, R.string.com_0);



            String k1 = store.addChild(konzern, "fossile", "konventionelle", "Versorgung mit konventioneller Energie",
                    com, R.string.com_conventional);

            store.addChild(k1, "", "RWE", "Rheinisch-Westfälische Energiebetriebe",
                    com, R.string.com_rwe);
            store.addChild(k1, "", "eon", "EON Energie Deutschland",
                    com, R.string.com_eon);
            store.addChild(k1, "","OVAG", "Oberhessische Versorgung Aktiengesellschaft",
                    com, R.string.com_ovag);



            // eco
            {
                int green = R.drawable.wiw_green;

                String k2 = store.addChild(konzern, "eco", "Ökoanbieter", "Ökostromversorger",
                        green, R.string.com_ecology);

                store.addChild(k2, "","Lichtblick", "Lichtblick SE",
                        green, R.string.com_lichtblick);
                store.addChild(k2, "", "Naturstrom", "Naturstrom AG",
                        green, R.string.com_naturstrom);
                store.addChild(k2, "", "EWS Schönau", "EWS Schönau eG",
                        green, R.string.com_schoenau);
                store.addChild(k2, "", "greenpeace", "greenpeace energy eG",
                        green, R.string.com_greenpeace);
                store.addChild(k2, "", "Bürgerwerke", "Bürgerwerke eG",
                        green, R.string.com_buergerwerke);
                store.addChild(k2, "", "Polarstern", "Polarstern GmbH",
                        green, R.string.com_polarstern);
            }


        }


        // network
        {
            int pol = R.drawable.wiw_politics;

            String k3 = store.addChild(root, "shapers","Netzwerke", "EEG, Regulierung, Forschung, Beratung",
                    pol, R.string.pol_0);

            store.addChild(k3, "gov", "BM Wirtschaft/Energie", "Bundesministerium für für Wirtschaft und Energie",
                    pol, R.string.pol_bmwi);

            store.addChild(k3, "gov", "Bundesnetzagentur", "Bundesnetzagentur",
                    pol, R.string.pol_netzagentur);

            store.addChild(k3, "","Bundesverband Windenergie", "Bundesverband Windenergie e.V.",
                    pol, R.string.pol_verbandwind);

            store.addChild(k3, "","Forum Regenerative Energien", "Internationales Wirtschaftsforum Regenerative Energien",
                    pol, R.string.pol_iwr);

        }

    }//createStoreData



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_boxtree);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.page1_hello); // values
            actionBar.setTitle(title);
        }




        // diagram elements
        diagram_symbol = (ImageView) findViewById(R.id.diagram_symbol);
        diagram_path = (TextView) findViewById(R.id.diagram_path);

        diagram_title = (TextView) findViewById(R.id.diagram_title);
        diagram_subject = (TextView) findViewById(R.id.diagram_subject);

        diagram_space = (LinearLayout) findViewById(R.id.diagram_space);
        diagram_web = (WebView) findViewById(R.id.diagram_web);

        diagram_web.setBackgroundColor(getColor(R.color.dossier));

        // listeners

        // use onBackPressed()
        //diagram_symbol.setOnClickListener(doLevelUp);

        //diagram_title.setOnClickListener(doOpenFocus);


        // create the store
        store = new DiagramStore();
        createStoreData();

        // start diagram
        setFocus(store.getRootId());

    }//onCreate

    //* set focus */
    private void setFocus(String id) {
        focus_id = id;
        DiagramModel focus = store.findModel(id);


        doLevelUp.setId(id);
        doOpenFocus.setId(id);


        String html = getString(Integer.parseInt(focus.getAdress())); // values
        diagram_web.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        // set focus title and subject
        diagram_title.setText(focus.getTitle());
        diagram_subject.setText(focus.getSubject());


        // remove current children
        diagram_space.removeAllViews();


        // add focus children
        String children = focus.getChildren();
        if (!children.isEmpty()) {
            String[] allchildren = children.split(",");

            for (String child_id : allchildren) {

                if (!child_id.isEmpty()) createChildView(focus, child_id);

            }//child
        }//children

        // calculate path
        String path = focus.getTag();

        DiagramModel parent = store.findParent(id);
        while (null != parent) {

            String tag = parent.getTag();

            if (!tag.isEmpty()) {

                if (path.isEmpty()) { path = tag; }
                else { path = tag + DIAGRAM_PATH_DELIM + path; }

            }


            String parent_id = parent.getId();
            parent = store.findParent(parent_id);
        }

        if (path.contains(DIAGRAM_NAME + DIAGRAM_PATH_DELIM)) {
            path = path.substring(DIAGRAM_NAME.length() + DIAGRAM_PATH_DELIM.length());
        }

        diagram_path.setText(path);
    }//setFocus


    //* create complex child view */
    public void createChildView(DiagramModel parent, String id) {
        DiagramModel child = store.findModel(id);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        Drawable roundbox = getResources().getDrawable(R.drawable.gui_roundbox);

        layout.setBackground(roundbox);
        layout.setPadding(8, 8, 8, 8);


        ImageView image = new ImageView(this);
        int res = Integer.parseInt(child.getSymbol());
        image.setImageResource(res);

        TextView text = new TextView(this);
        text.setPadding(8, 8, 8, 8);
        //text.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Headline4); // 34sp
        //text.setTextAppearance(this, R.style.TextAppearance_AppCompat_Large); // 22sp
        text.setTextAppearance(this, R.style.TextAppearance_AppCompat_Headline); //24sp

        text.setText(child.getTitle());

        layout.addView(image);
        layout.addView(text);
        layout.setOnClickListener(new SetFocus(id));

        diagram_space.addView(layout);
    }//createChildView

    /* --------------------------------windvolt-------------------------------- */

    @Override
    public void onBackPressed() {
        DiagramModel parent = store.findParent(focus_id);
        if (null == parent) {
            super.onBackPressed();
        } else {
            if (beep) {
                ToneGenerator beep = new ToneGenerator(AudioManager.FLAG_PLAY_SOUND, 80);
                beep.startTone(ToneGenerator.TONE_CDMA_ANSWER, 100);
            }

            String parent_id = parent.getId();
            setFocus(parent_id);
        }
    }//onBackPressed

    private class SetFocus implements View.OnClickListener {
        String id = "";
        public SetFocus(String set_id) {
            id = set_id;
        }

        @Override
        public void onClick(View view) {
            if (beep) {
                ToneGenerator beep = new ToneGenerator(AudioManager.FLAG_PLAY_SOUND, 80);
                beep.startTone(ToneGenerator.TONE_CDMA_ANSWER, 100);
            }

            setFocus(id);
        }
    }//SetFocus


    private LevelUpFocus doLevelUp = new LevelUpFocus();
    private class LevelUpFocus implements View.OnClickListener {
        String id = "";
        public void setId(String set_id) {
            id = set_id;
        }

        @Override
        public void onClick(View v) {
            DiagramModel parent = store.findParent(id);
            if (null != parent) {
                //ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_ALARM, 80);
                //beep.startTone(ToneGenerator.TONE_CDMA_ANSWER, 200);

                String parent_id = parent.getId();
                setFocus(parent_id);
            }
        }
    }//LevelUpFocus

    private OpenFocus doOpenFocus = new OpenFocus();
    private class OpenFocus implements View.OnClickListener {
        String id = "";
        public void setId(String set_id) {
            id = set_id;
        }

        @Override
        public void onClick(View v) {
            // not used here
        }
    }//OpenFocus
}
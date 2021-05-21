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

import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramModel;
import org.windvolt.diagram.model.DiagramStore;

public class WhoIsWho extends AppCompatActivity {

    final String DIAGRAM_NAME = "who is who";
    final String DIAGRAM_PATH_DELIM = ">";

    final String MODEL_URL = "https://windvolt.eu/model/dossier/0diagram.xml";

    DiagramStore store;

    ImageView diagram_symbol;
    TextView diagram_path;
    TextView diagram_title;
    TextView diagram_subject;



    LinearLayout diagram_space;
    WebView web;

    String focus_id;

    boolean ALLOW_BEEP = false;



    protected void createStore() {

        store = new DiagramStore();

        //* try to load model or create a local */

        if (store.loadModel(WhoIsWho.this, MODEL_URL)) {

        } else {
            Toast.makeText(this, store.getError(), Toast.LENGTH_LONG).show();

            createLocalStore();
        }


    }//createStore

    private void createLocalStore() {
        String symbol = "https://windvolt.eu/model/windvolt_small.png";


        String root = store.addChild("", "windvolt", "Windenergie Galerie",
                symbol, "https://windvolt.eu/model/dossier/0diagram.html", //R.string.diagram_dossier,
                DIAGRAM_NAME);


        // net
        {

            String net = "https://windvolt.eu/model/wiw_net.png";

            String netz = store.addChild(root, "Netzbetreiber", "Die deutschen Netzbetreiber",
                    net, "https://windvolt.eu/model/dossier/net_.html", //R.string.net_0,
                    "net");



            store.addChild(netz, "50Hertz", "50Hertz Transmission GmbH",
                    net, "https://windvolt.eu/model/dossier/net_50hertz.html", //R.string.net_50herz,
                    "");
            store.addChild(netz, "Amprion", "Amprion GmbH",
                    net, "https://windvolt.eu/model/dossier/net_amprion.html", //R.string.net_ampirion,
                    "");
            store.addChild(netz, "Tennet", "Tennet TSO",
                    net, "https://windvolt.eu/model/dossier/net_tennet.html", //R.string.net_tennet,
                    "");
            store.addChild(netz, "Transnet BW", "Transnet BW GmbH",
                    net, "https://windvolt.eu/model/dossier/net_transnet.html", //R.string.net_transnet,
                    "");
        }

        // pricing
        {
            String stock = "https://windvolt.eu/model/wiw_exchange.png";

            store.addChild(root, "Börse", "Strombörse EEX",
                    stock, "https://windvolt.eu/model/dossier/stock_.html", //R.string.com_stock,
                    "exc");
        }

        // com
        {
            String com = "https://windvolt.eu/model/wiw_com.png";

            String konzern = store.addChild(root, "Versorger", "Stromversorger in Deutschland",
                    com, "https://windvolt.eu/model/dossier/com_.html", //R.string.com_0,
                    "com");



            String k1 = store.addChild(konzern, "konventionelle", "Versorgung mit konventioneller Energie",
                    com, "https://windvolt.eu/model/dossier/com_conventional.html", //R.string.com_conventional,
                    "fossile");

            store.addChild(k1, "RWE", "Rheinisch-Westfälische Energiebetriebe",
                    com, "https://windvolt.eu/model/dossier/com_rwe.html", //R.string.com_rwe,
                    "");
            store.addChild(k1, "eon", "EON Energie Deutschland",
                    com, "https://windvolt.eu/model/dossier/com_eon.html", //R.string.com_eon,
                    "");
            store.addChild(k1, "OVAG", "Oberhessische Versorgung Aktiengesellschaft",
                    com, "https://windvolt.eu/model/dossier/com_ovag.html", //R.string.com_ovag,
                    "");



            // eco
            {
                String green = "https://windvolt.eu/model/wiw_green.png";

                String k2 = store.addChild(konzern, "Ökoanbieter", "Ökostromversorger",
                        green, "https://windvolt.eu/model/dossier/com_green.html", //R.string.com_ecology,
                        "eco");

                store.addChild(k2, "Lichtblick", "Lichtblick SE",
                        green, "https://windvolt.eu/model/dossier/com_lichtblick.html", //R.string.com_lichtblick,
                        "");
                store.addChild(k2, "Naturstrom", "Naturstrom AG",
                        green, "https://windvolt.eu/model/dossier/com_naturstrom.html", //R.string.com_naturstrom,
                        "");
                store.addChild(k2, "EWS Schönau", "EWS Schönau eG",
                        green, "https://windvolt.eu/model/dossier/com_schoenau.html", //R.string.com_schoenau,
                        "");
                store.addChild(k2, "greenpeace", "greenpeace energy eG",
                        green, "https://windvolt.eu/model/dossier/com_greenpeace.html", //R.string.com_greenpeace,
                        "");
                store.addChild(k2, "Bürgerwerke", "Bürgerwerke eG",
                        green, "https://windvolt.eu/model/dossier/com_buergerwerke.html", //R.string.com_buergerwerke,
                        "");
                store.addChild(k2, "Polarstern", "Polarstern GmbH",
                        green, "https://windvolt.eu/model/dossier/com_polarstern.html", //R.string.com_polarstern,
                        "");
            }


        }


        // network
        {
            String pol = "https://windvolt.eu/model/wiw_politics.png";

            String k3 = store.addChild(root, "Netzwerke", "Regulierung, Forschung, Beratung",
                    pol, "https://windvolt.eu/model/dossier/pol_.html", //R.string.pol_0,
                    "shapers");

            store.addChild(k3, "Wirtschaft/Energie", "Bundesministerium",
                    pol, "https://windvolt.eu/model/dossier/pol_bmwi.html", //R.string.pol_bmwi,
                    "gov");

            store.addChild(k3, "Bundesnetzagentur", "Bundesnetzagentur",
                    pol, "https://windvolt.eu/model/dossier/pol_bunetza.html", //R.string.pol_netzagentur,
                    "gov");

            store.addChild(k3, "Verband Windenergie", "Bundesverband Windenergie e.V.",
                    pol, "https://windvolt.eu/model/dossier/pol_windverband.html", //R.string.pol_verbandwind,
                    "");

            store.addChild(k3, "Forum Regenerative Energien", "Internationales Forum",
                    pol, "https://windvolt.eu/model/dossier/pol_iwr.html", //R.string.pol_iwr,
                    "");

        }
    }//createLocalStore



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
        web = (WebView) findViewById(R.id.diagram_dossier);

        web.setBackgroundColor(getColor(R.color.diagram_background));

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

        // start diagram
        if (store.size() > 0) {
            setFocus(store.getRootId());
        }

    }//onCreate

    //* set focus */
    private void setFocus(String id) {

        boolean hasFocus = id.equals(focus_id);
        focus_id = id;

        DiagramModel focus = store.findModel(id);


        doLevelUp.setId(id);
        doOpenFocus.setId(id);


        /*
        String html = getString(Integer.parseInt(focus.getAdress())); // values
        web.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
         */
        web.loadUrl(focus.getAdress());

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
        String path = focus.getTags();

        DiagramModel parent = store.findParent(id);
        while (null != parent) {

            String tag = parent.getTags();

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

        Drawable roundbox = getResources().getDrawable(R.drawable.app_rbox);

        layout.setBackground(roundbox);
        layout.setPadding(8, 8, 8, 8);


        ImageView image = new ImageView(this);
        image.setPadding(2, 2, 2, 2);
        store.loadViewImage(image, child.getSymbol());


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
            doBeep();

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
            doBeep();
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



    private void doBeep() {
        if (ALLOW_BEEP) {
            ToneGenerator beep = new ToneGenerator(AudioManager.FLAG_PLAY_SOUND, 80);
            beep.startTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE, 400);
        }
    }
}
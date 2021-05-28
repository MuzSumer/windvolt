package org.windvolt.diagram.model;

import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DiagramAbstraction extends AppCompatActivity {


    public void setFocus(String id, boolean expand) {}

    public void loadModel(DiagramAbstraction diagram, String url) {
        new ModelLoader(diagram).execute(url);
    }


    /* --------------------------------windvolt-------------------------------- */


    private static class ModelLoader extends AsyncTask<String, Void, Boolean> {

        HttpsURLConnection connection = null;
        InputStream content = null;
        String url = null;

        DiagramAbstraction diagram;


        public ModelLoader(DiagramAbstraction set_diagram) {
            diagram = set_diagram;
        }

        @Override
        protected Boolean doInBackground(String... values) {
            url = values[0];

            try {
                URL uri = new URL(url);
                connection = (HttpsURLConnection) uri.openConnection();

                connection.setRequestMethod("GET");
                connection.setDoInput(true);


                connection.connect();

                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {

                    content = connection.getInputStream();
                    diagram.getConnector().buildContent(diagram.getStore(), content);

                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            {
                if (content != null) {
                    try {
                        content.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }//cleanup

            if (!result) {
                createLocalStore();
            }

            // start diagram
            diagram.setFocus(null, false);

        }//onPostExecute

        private void createLocalStore() {

            diagram.getStore().addChild("", "Fehler",
                    "Online-Modell nicht geladen", "https://windvolt.eu/model/windvolt_small.png", "https://windvolt.eu/model/diagram_error.html", //R.string.diagram_flow0,
                    "#error");

        }//createLocalStore

    }//ModelLoader


    /* --------------------------------windvolt-------------------------------- */

    DiagramStore store = null;
    public void setStore(DiagramStore set_store) {
        store = set_store;
    }
    public DiagramStore getStore() {
        return store;
    }

    DiagramConnector connector = null;
    public void setConnector(DiagramConnector set_connector) {
        connector = set_connector;
    }
    public DiagramConnector getConnector() {
        return connector;
    }
}

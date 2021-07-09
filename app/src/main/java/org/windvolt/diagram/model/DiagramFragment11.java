package org.windvolt.diagram.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.windvolt.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DiagramFragment11 extends Fragment {

    public void createStore() {}
    public void setFocus(String id, boolean expand) {}

    public String getNamespace() {
        return null;
    }

    public void loadModel(DiagramFragment11 diagram, String url) {
        setStore(new DiagramStore11());

        new ModelLoader(diagram).execute(url);
    }

    public void loadViewImage(ImageView view, String url) {
        new ImageLoader(view, -1, -1).execute(url);
    }
    public void loadViewImage(ImageView view, String url, int w, int h) {
        new ImageLoader(view, w, h).execute(url);
    }

    /* --------------------------------windvolt-------------------------------- */


    public void buildContent(DiagramStore11 store, InputStream stream) {
        try {

            // create builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();



            // convert byte array
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            int b;
            while ((b = stream.read()) != -1) {
                output.write(b);
            }

            ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());


            // build and parse
            Document document = builder.parse(input);

            stream.close();
            parseContent(store, document);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//buildContent

    private void parseContent(DiagramStore11 store, Document document) {
        Element root = document.getDocumentElement();//diagram
        root.normalize();

        NodeList models = root.getElementsByTagName("model");
        int msize = models.getLength();

        for (int p=0; p<msize; p++) {
            Node node = models.item(p);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String id = readItem(element,"id");
                String type = readItem(element, "type");
                String state = readItem(element,"state");
                String symbol = readItem(element, "symbol");

                String title = readItem(element, "title");
                String subject = readItem(element, "subject");

                String content = readItem(element, "content");
                String targets = readItem(element, "targets");
                String tags = readItem(element, "tags");

                DiagramModel11 model = new DiagramModel11();

                model.setId(id);
                model.setType(type);
                model.setState(state);
                model.setSymbol(symbol);

                model.setTitle(title);
                model.setSubject(subject);

                model.setContent(content);

                model.setTargets(targets);
                model.setTags(tags);


                store.addModel(model);
            }//element

        }//node

    }//parseContent

    private String readItem(Element element, String item_name) {
        String result = "";

        NodeList node = element.getElementsByTagName(item_name);
        if (node != null) {
            result = node.item(0).getTextContent();
        }

        return result;
    }//readItem


    /* --------------------------------windvolt-------------------------------- */


    private static class ModelLoader extends AsyncTask<String, Void, Boolean> {

        HttpsURLConnection connection = null;
        InputStream contentstream = null;
        String url = null;

        DiagramFragment11 diagram;


        public ModelLoader(DiagramFragment11 set_diagram) {
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

                    contentstream = connection.getInputStream();
                    diagram.buildContent(diagram.getStore(), contentstream);

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
                if (contentstream != null) {
                    try {
                        contentstream.close();
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

            DiagramModel11 model = new DiagramModel11();


            model.setId(diagram.getStore().getNewId());
            model.setType("alert");
            model.setState("error");
            model.setSymbol("https://windvolt.eu/model/windvolt_small.png");

            model.setTitle("Fehler");
            model.setSubject("Online-Modell nicht geladen");

            model.setContent("https://windvolt.eu/model/diagram_error.html");

            model.setTargets("");
            model.setTags("#error");



            diagram.getStore().addModel(model);

        }//createLocalStore

    }//ModelLoader


    /* --------------------------------windvolt-------------------------------- */


    private static class ImageLoader extends AsyncTask<String, Void, Bitmap> {
        HttpsURLConnection connection = null;
        InputStream content = null;
        ImageView view;
        int w, h;


        public ImageLoader(ImageView set_view, int set_w, int set_h) {
            view = set_view;
            w = set_w;
            h = set_h;
        }

        protected Bitmap doInBackground(String... values) {
            String url = values[0];

            Bitmap bitmap = null;
            if (url.isEmpty()) { return bitmap; }


            try {
                URL uri = new URL(url);
                connection = (HttpsURLConnection) uri.openConnection();

                connection.setRequestMethod("GET");
                connection.setDoInput(true);


                connection.connect();


                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {

                    content = connection.getInputStream();

                    bitmap = BitmapFactory.decodeStream(content);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            {//*cleanup
                if (content != null) {
                    try {
                        content.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }//cleanup

            if (result == null) {
                view.setImageResource(R.drawable.ic_error);

            } else {

                if (w<0) {
                    w = result.getWidth();
                }
                if (h<0) {
                    h = result.getHeight();
                }

                Bitmap scaled = Bitmap.createScaledBitmap(result, w, h, false);
                view.setImageBitmap(scaled);

            }
        }

    }//ImageLoader


    /* --------------------------------windvolt-------------------------------- */

    DiagramStore11 store = null;
    public void setStore(DiagramStore11 set_store) {
        store = set_store;
    }
    public DiagramStore11 getStore() {
        return store;
    }
}
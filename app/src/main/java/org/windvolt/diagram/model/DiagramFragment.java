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
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DiagramFragment extends Fragment {

    /*
     *
     * entend this class for fragments that load models from a webserver
     *
     */

    final int BUFFER_SIZE = 8192;

    public void createStore() {}
    public void setFocus(String id, boolean expand) {}


    public void loadModel(DiagramFragment diagram, String url) {
        setStore(new DiagramStore());

        new ModelLoader(diagram).execute(url);
    }

    public void loadViewImage(ImageView view, String url) {
        new ImageLoader(view, -1, -1).execute(url);
    }
    public void loadViewImage(ImageView view, String url, int w, int h) {
        new ImageLoader(view, w, h).execute(url);
    }

    /* --------------------------------windvolt-------------------------------- */


    public void buildContent(DiagramStore store, InputStream stream) {
        try {

            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader buffer = new BufferedReader(reader);

            // convert
            byte[] bytes = new byte[BUFFER_SIZE];
            int c = 0;

            int r = buffer.read();
            while (r != -1) {
                bytes[c] = (byte) r;
                c++;

                r = buffer.read();
            }
            buffer.close();

            ByteArrayInputStream input = new ByteArrayInputStream(bytes);


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(input);

            {// cleanup
                buffer.close();
                reader.close();
            }


            parseContent(store, document);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }//buildContent

    private void parseContent(DiagramStore store, Document document) {
        Element root = document.getDocumentElement();//diagram
        root.normalize();

        NodeList models = root.getElementsByTagName("model");
        int msize = models.getLength();

        for (int p=0; p<msize; p++) {
            Node node = models.item(p);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                String id = element.getElementsByTagName("id").item(0).getTextContent();
                String type = element.getElementsByTagName("type").item(0).getTextContent();
                String symbol = element.getElementsByTagName("symbol").item(0).getTextContent();

                String title = element.getElementsByTagName("title").item(0).getTextContent();
                String subject = element.getElementsByTagName("subject").item(0).getTextContent();

                String address = element.getElementsByTagName("address").item(0).getTextContent();
                String children = element.getElementsByTagName("children").item(0).getTextContent();
                String tags = element.getElementsByTagName("tags").item(0).getTextContent();


                DiagramModel model = new DiagramModel();

                model.setId(id);
                model.setType(type);
                model.setSymbol(symbol);

                model.setTitle(title);
                model.setSubject(subject);

                model.setAddress(address);

                model.setChildren(children);
                model.setTags(tags);


                store.addModel(model);
            }//element

        }//node

    }//parseContent


    /* --------------------------------windvolt-------------------------------- */


    private static class ModelLoader extends AsyncTask<String, Void, Boolean> {

        HttpsURLConnection connection = null;
        InputStream content = null;
        String url = null;

        DiagramFragment diagram;


        public ModelLoader(DiagramFragment set_diagram) {
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
                    diagram.buildContent(diagram.getStore(), content);

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

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
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

                if (w < 0 && h < 0) {

                    view.setImageBitmap(result);
                } else {
                    Bitmap scaled = Bitmap.createScaledBitmap(result, w, h, false);
                    view.setImageBitmap(scaled);
                }



            }
        }

    }//ImageLoader


    /* --------------------------------windvolt-------------------------------- */

    DiagramStore store = null;
    public void setStore(DiagramStore set_store) {
        store = set_store;
    }
    public DiagramStore getStore() {
        return store;
    }
}
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
package org.windvolt.diagram.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.windvolt.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class DiagramActivity extends AppCompatActivity {

    public void createStore() {}
    public void setFocus(String id, boolean expand) {}

    public String getNamespace() {
        return null;
    }

    private Document doc = null;


    public void loadRemoteModel(DiagramActivity diagram, String url) {
        setStore(new DiagramStore());

        new ModelLoader(diagram).execute(url);
    }

    public boolean saveRemoteModel(String username, String password, String url) {

        doc = buildDocument();

        if (doc == null) {
            return false;
        }


        // TODO draft

        try {
            URL uri = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();

            // login
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encode(auth.getBytes(), Base64.DEFAULT);
            String authHeaderValue = "Basic " + new String(encodedAuth);

            connection.setRequestProperty("Authorization", authHeaderValue);

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            //DataOutputStream output = new DataOutputStream(connection.getOutputStream());






            // save
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            //transformerFactory.setAttribute("indent-number", 1);
            Transformer transformer = transformerFactory.newTransformer();
            //transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            //transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(connection.getOutputStream());
            transformer.transform(source, result);


            



            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /* --------------------------------windvolt-------------------------------- */

    public boolean loadPrivateModel(String filename) {
        setStore(new DiagramStore());

        try {

            FileInputStream fileInputStream = openFileInput(filename);

            buildContent(getStore(), fileInputStream);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean savePrivateModel(String filename) {

        try {
            FileOutputStream fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            doc = buildDocument();

            if (doc == null) {
                return false;
            }

            // save
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            //transformerFactory.setAttribute("indent-number", 1);
            Transformer transformer = transformerFactory.newTransformer();
            //transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            //transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(fileOutputStream);
            transformer.transform(source, result);


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }



    /* --------------------------------windvolt-------------------------------- */

    public void loadViewImage(ImageView view, String url) {
        new ImageLoader(view, -1, -1).execute(url);
    }

    public void loadViewImage(ImageView view, String url, int w, int h) {
        new ImageLoader(view, w, h).execute(url);
    }




    /* --------------------------------windvolt-------------------------------- */


    public void buildContent(DiagramStore store, InputStream stream) {
        Document document;

        try {
            // create builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            if (stream instanceof FileInputStream) {
                // do not convert
                document = builder.parse(stream);
            } else {

                // convert byte array
                ByteArrayOutputStream output = new ByteArrayOutputStream();

                int b;
                while ((b = stream.read()) != -1) {
                    output.write(b);
                }

                ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());

                // build and parse
                document = builder.parse(input);

            }

            stream.close();
            parseContent(store, document);

        } catch (Exception e) {
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

                String id = readItem(element,"id");
                String type = readItem(element, "type");
                String state = readItem(element,"state");
                String symbol = readItem(element, "symbol");

                String title = readItem(element, "title");
                String subject = readItem(element, "subject");

                String content = readItem(element, "content");
                String targets = readItem(element, "targets");
                String tags = readItem(element, "tags");


                DiagramModel model = new DiagramModel();

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

    private Document buildDocument() {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();

            doc = builder.newDocument();
            //document.setXmlStandalone(false);

            Element d = doc.createElement("diagram");
            doc.appendChild(d);

            for (int p = 0; p<getStore().size(); p++) {
                DiagramModel model = getStore().getModel(p);

                // create model
                Element m = doc.createElement("model");
                d.appendChild(m);


                addElement(m,"id", model.getId());
                addElement(m,"type", model.getType());
                addElement(m,"state", model.getState());
                addElement(m,"symbol", model.getSymbol());
                addElement(m,"title", model.getTitle());
                addElement(m,"subject", model.getSubject());
                addElement(m,"content", model.getContent());
                addElement(m,"targets", model.getTargets());
                addElement(m,"tags", model.getTags());
            }

            return doc;


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Element addElement(Element parent, String element_name, String element_value) {

        Element e = doc.createElement(element_name);
        e.appendChild(doc.createTextNode(element_value));
        parent.appendChild(e);

        return e;
    }


    /* --------------------------------windvolt-------------------------------- */


    private static class ModelLoader extends AsyncTask<String, Void, Boolean> {

        HttpsURLConnection connection = null;
        InputStream content = null;
        String url = null;

        DiagramActivity diagram;


        public ModelLoader(DiagramActivity set_diagram) {
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

            DiagramModel model = new DiagramModel();


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
        String url;
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
            url = values[0];

            if (url.isEmpty()) { return null; }

            if (url == "windvolt") {
                return null;
            }

            if (isNumeric(url)) {
                String numeric = url;

                while (numeric.length() < 3) {
                    numeric = "0" + numeric;
                }

                url = "https://windvolt.eu/model/icons/actn/actn" + numeric + ".gif";
            }


            Bitmap bitmap = null;

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

                switch (url) {
                    case "windvolt":
                        view.setImageResource(R.drawable.windvolt_small);
                        break;

                    case "mobile":
                        break;

                    case "smartphone":
                        break;

                    default:
                        view.setImageResource(R.drawable.ic_error);
                }


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

    public static boolean isNumeric(String str) {
        return str != null && str.matches("[-+]?\\d*\\.?\\d+");
    }

    DiagramStore store = null;
    public void setStore(DiagramStore set_store) {
        store = set_store;
    }
    public DiagramStore getStore() {
        return store;
    }

}

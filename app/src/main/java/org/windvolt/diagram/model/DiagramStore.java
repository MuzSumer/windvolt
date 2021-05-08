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
package org.windvolt.diagram.model;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DiagramStore {

    ArrayList<DiagramModel> store = new ArrayList<>();

    //HttpsURLConnection connection;
    InputStream content_stream;


    public boolean loadStoreModel(Context context, String url) {

        boolean success = true;

        //if (success) return false;

        Loader loader = new Loader();

        String result = loader.doInBackground(url);

        if (!result.isEmpty()) {
            success = false;

            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }


        return success;
    }

    class Loader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            String url = strings[0];

            try {
                loadContent(url);

                BufferedInputStream content = new BufferedInputStream(content_stream);

                result = readContent(content);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "MalformedURLException";
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException";
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }//Loader



    private void loadContent(String url) throws IOException, MalformedURLException {

        HttpsURLConnection connection = null;

        try {
            URL uri = new URL(url);
            connection = (HttpsURLConnection) uri.openConnection();
            connection.setReadTimeout(15*1000);
            connection.setConnectTimeout(30*1000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.connect();

            //int responseCode = connection.getResponseCode();

            content_stream = connection.getInputStream();


        } finally {
            if (content_stream != null) {
                content_stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    private String readContent(BufferedInputStream content) {
        String result = "";

        try {
            byte[] buff = new byte[2048];
            int c = 0;


            int r = content.read();
            while (r != -1) {
                buff[c] = (byte) r;
                c++;

                r = content.read();
            }

            if (content_stream != null) {
                content_stream.close();
            }


            ByteArrayInputStream byte_stream = new ByteArrayInputStream(buff);



            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(byte_stream);

            parseContent(document);


        } catch (IOException e) {
            result = "IOException";
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            result = "ParserConfigurationException";
            e.printStackTrace();
        } catch (SAXException e) {
            result = "SAXException";
            e.printStackTrace();
        }

        return result;

    }

    private void parseContent(Document document) {

        Element root = document.getDocumentElement();
        root.normalize();

        NodeList models = root.getElementsByTagName("model");
        int size = models.getLength();

        for (int p=0; p<size; p++) {
            Node device = models.item(p);

            if (device.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) device;

                String id = element.getElementsByTagName("id").item(0).getTextContent();
                String title = element.getElementsByTagName("title").item(0).getTextContent();
                String subject = element.getElementsByTagName("subject").item(0).getTextContent();
                String symbol = element.getElementsByTagName("symbol").item(0).getTextContent();
                String address = element.getElementsByTagName("address").item(0).getTextContent();
                String children = element.getElementsByTagName("children").item(0).getTextContent();
                String tags = element.getElementsByTagName("tag").item(0).getTextContent();


                DiagramModel model = new DiagramModel();

                model.setId(id);
                model.setTitle(title);
                model.setSubject(subject);
                model.setSymbol(symbol);
                model.setAddress(address);
                model.setChildren(children);
                model.setTags(tags);

                addModel(model);
            }
        }
    }


    final int rootId = 100;
    public String getRootId() { return Integer.toString(rootId); }



    public void addModel(DiagramModel model) {
        store.add(model);
    }

    public String addChild(String parent_id, String title, String subject, int symbol, int address, String tags) {

        DiagramModel parent = null;
        DiagramModel child = new DiagramModel();

        if (!parent_id.isEmpty()) {
            parent = findModel(parent_id);
        }

        int size = store.size();
        String id = Integer.toString(rootId + size);
        child.setId(id);


        // add to parent
        if (parent != null) {
            String children = parent.getChildren();

            if (children.isEmpty()) {
                children = id;
            } else {
                children += "," +id;
            }

            parent.setChildren(children);
        }


        child.setTitle(title);
        child.setSubject(subject);
        child.setSymbol(Integer.toString(symbol));
        child.setAddress(Integer.toString(address));
        child.setTags(tags);


        store.add(child);

        return id;
    }//addChild


    public String getChildren(DiagramModel parent) {
        return parent.getChildren();
    }



    public DiagramModel findModel(String id) {
        DiagramModel found = null;

        if (id.isEmpty()) return found;

        int size = store.size();
        for (int m=0; m<size; m++) {
            DiagramModel model = store.get(m);
            String m_id = model.getId();
            if (m_id.equals(id)) {
                found = model;
                return found; // return first hit

                // you could detect id not unique error here
            }
        }

        return found;
    }//findModel

    public DiagramModel findParent(String id) {
        DiagramModel parent = null;

        int size = store.size();
        for (int m=0; m<size; m++) {
            DiagramModel model = store.get(m);
            String children = model.getChildren();

            if (children.contains(id)) {
                parent = model;
                return parent; // return first hit

                // you could detect multiple parents error here
            }
        }

        return parent;
    }//findParent

}

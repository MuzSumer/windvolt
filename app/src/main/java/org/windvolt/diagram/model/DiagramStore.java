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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DiagramStore {

    boolean DISABLE_REMOTE_MODEL = true;

    ArrayList<DiagramModel> store = new ArrayList<>();



    String error = "";
    public String getError() {
        return error;
    }

    public int size() {
        return store.size();
    }




    public boolean loadModel(Context context, String url) {


        //* disable remote models
        if (DISABLE_REMOTE_MODEL) {
            error = "remote model not supported at this time";

            boolean success = true;
            if (success) return false;
        }


        // load the xml model
        new ModelLoader().execute(url);


        if (error.isEmpty()) {
            return true;
        }


        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
        return false;
    }



    // AsyncTask used to download XML model
    private class ModelLoader extends AsyncTask<String, Void, String> {

        HttpsURLConnection connection = null;
        InputStream content = null;


        @Override
        protected String doInBackground(String... values) {
            String url = values[0];

            try {
                URL uri = new URL(url);
                connection = (HttpsURLConnection) uri.openConnection();

                connection.setReadTimeout(10*1000);
                connection.setConnectTimeout(15*1000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                /*{//* connection
                    connection.setReadTimeout(10*1000);
                    connection.setConnectTimeout(15*1000);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);

                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("X-Environment", "android");

                    connection.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());

                    connection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            //* if necessarry get url verfication
                            //return HttpsURLConnection.getDefaultHostnameVerifier().verify("your_domain.com", session);
                            return true;
                        }
                    });
                }//connection


                {//* cookies
                    CookieManager cookieManager = CookieManager.getInstance();
                    String cookie = cookieManager.getCookie(url);

                    if (cookie != null)
                        connection.setRequestProperty("Cookie", cookie);

                    List<String> cookieList = connection.getHeaderFields().get("Set-Cookie");
                    if (cookieList != null) {
                        for (String cookieTemp : cookieList) {
                            cookieManager.setCookie(connection.getURL().toString(), cookieTemp);
                        }
                    }
                }//cookies
                */

                connection.connect();
                content = connection.getInputStream();


                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    buildContent();
                }

            } catch (MalformedURLException e) {
                error = "MalformedURLException";
                e.printStackTrace();
            } catch (IOException e) {
                error = "IOException";
                e.printStackTrace();
            }

            return error;
        }

        @Override
        protected void onPostExecute(String result) {
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
        }

        private void buildContent() {

            try {

                InputStreamReader reader = new InputStreamReader(content);
                BufferedReader buffer = new BufferedReader(reader);

                // convert
                byte[] bytes = new byte[2048];
                int c = 0;

                int r = buffer.read();
                while (r != -1) {
                    bytes[c] = (byte) r;
                    c++;

                    r = buffer.read();
                }
                ByteArrayInputStream input = new ByteArrayInputStream(bytes);


                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document document = builder.parse(input);

                {// cleanup
                    buffer.close();
                    reader.close();
                }


                parseContent(document);

            } catch (IOException e) {
                error = "IOException";
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                error = "ParserConfigurationException";
                e.printStackTrace();
            } catch (SAXException e) {
                error = "SAXException";
                e.printStackTrace();
            }


        }//buildContent

        private void parseContent(Document document) {

            Element root = document.getDocumentElement();//diagram
            root.normalize();

            NodeList models = root.getElementsByTagName("model");
            int size = models.getLength();

            for (int p=0; p<size; p++) {
                Node node = models.item(p);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String id = element.getElementsByTagName("id").item(0).getTextContent();
                    String title = element.getElementsByTagName("title").item(0).getTextContent();
                    String subject = element.getElementsByTagName("subject").item(0).getTextContent();
                    String symbol = element.getElementsByTagName("symbol").item(0).getTextContent();
                    String address = element.getElementsByTagName("address").item(0).getTextContent();
                    String children = element.getElementsByTagName("children").item(0).getTextContent();
                    String tags = element.getElementsByTagName("tags").item(0).getTextContent();


                    DiagramModel model = new DiagramModel();

                    model.setId(id);
                    model.setTitle(title);
                    model.setSubject(subject);
                    model.setSymbol(symbol);
                    model.setAddress(address);
                    model.setChildren(children);
                    model.setTags(tags);


                    addRawModel(model);
                }
            }


        }//parseContent

    }//XmlModelLoader


    /* --------------------------------windvolt-------------------------------- */



    final int rootId = 100;
    public String getRootId() {
        return "100";
    }

    public void addRawModel(DiagramModel model) {
        store.add(model);
    }


    public String getChildren(DiagramModel parent) {
        return parent.getChildren();
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


    /* --------------------------------windvolt-------------------------------- */

    public DiagramModel findModel(String id) {
        DiagramModel found = null;

        if (id.isEmpty()) return found;

        int size = store.size();
        for (int m=0; m<size; m++) {
            DiagramModel model = store.get(m);
            String m_id = model.getId();

            if (m_id.equals(id)) {

                if (found == null) { // first hit
                    found = model;
                } else {
                    found = model;

                    error = "model id is not unique " + id;
                }
            }
        }//for

        return found;
    }//findModel

    public DiagramModel findParent(String id) {
        DiagramModel parent = null;

        int size = store.size();
        for (int m=0; m<size; m++) {
            DiagramModel model = store.get(m);
            String children = model.getChildren();

            if (children.contains(id)) {

                if (parent == null) { // first hit
                    parent = model;

                } else { // detect multiple parents error here
                    parent = model;

                    error = "multiple parents error";
                }
            }
        }//for

        return parent;
    }//findParent

}

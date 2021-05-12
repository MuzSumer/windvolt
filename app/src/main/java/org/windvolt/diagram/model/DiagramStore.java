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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DiagramStore {

    ArrayList<DiagramModel> store = new ArrayList<>();

    HttpURLConnection connection = null;
    InputStream content = null;

    String error = "";
    public String getError() {
        return error;
    }

    public int size() {
        return store.size();
    }

    public boolean loadXmlModel(Context context, String url) {

        boolean success = true;
        if (success) return false;


        new DownloadXmlTask().execute(url);

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


        if (error.isEmpty()) {
            return true;
        }

        Toast.makeText(context, error, Toast.LENGTH_LONG).show();

        return false;
    }

    // implementation of AsyncTask used to download XML model
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... values) {
            String url = values[0];

            // TODO
            // load xml
            try {

                URL uri = new URL(url);

                connection = (HttpURLConnection) uri.openConnection();

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

                //connection.connect();
                content = connection.getInputStream();


                buildContent();

                //if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) { }

            } catch (MalformedURLException e) {
                error = "MalformedURLException";
                e.printStackTrace();
            } catch (IOException e) {
                error = "IOException";
                e.printStackTrace();
            }


            return getError();
        }

        @Override
        protected void onPostExecute(String result) {
            // do anything
        }
    }


    /* --------------------------------windvolt-------------------------------- */


    private void buildContent() {


        try {

            /*
             BufferedReader br =
        new BufferedReader(
            new InputStreamReader(con.getInputStream()));

       String input;

       while ((input = br.readLine()) != null){
          System.out.println(input);
       }
       br.close();
             */
            InputStreamReader reader = new InputStreamReader(content);
            BufferedReader buffer = new BufferedReader(reader);

            //BufferedInputStream content = new BufferedInputStream(this.content);

            byte[] buff = new byte[2048];
            int c = 0;


            int r = buffer.read();
            while (r != -1) {
                buff[c] = (byte) r;
                c++;

                r = buffer.read();
            }
            buffer.close();

            ByteArrayInputStream byte_stream = new ByteArrayInputStream(buff);



            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(byte_stream);

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


    }//readContent

    private void parseContent(Document document) {

        Element root = document.getDocumentElement();//diagram
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
    }//parseContent

    /* DRAFT1
    private HttpsURLConnection getConnection(String url) throws MalformedURLException {
    URL request_url = new URL(url);
    try {
        if (!isHttps()) {
            throw new ConnectException("you have to use SSL certifacated url!");
        }
        urlConnection = (HttpsURLConnection) request_url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setReadTimeout(95 * 1000);
        urlConnection.setConnectTimeout(95 * 1000);
        urlConnection.setDoInput(true);
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("X-Environment", "android");

        // Cookie Sets...
    String cookie = cookieManager.getCookie(urlConnection.getURL().toString());
    cookieManager = CookieManager.getInstance();
        if (cookie != null)
            urlConnection.setRequestProperty("Cookie", cookie);

    List<String> cookieList = urlConnection.getHeaderFields().get("Set-Cookie");
        if (cookieList != null) {
        for (String cookieTemp : cookieList) {
            cookieManager.setCookie(urlConnection.getURL().toString(), cookieTemp);
        }
    }
    // Cookie Sets...

        urlConnection.setHostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            //* if it necessarry get url verfication
            //return HttpsURLConnection.getDefaultHostnameVerifier().verify("your_domain.com", session);
            return true;
        }
    });
        urlConnection.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());


        urlConnection.connect();

} catch (IOException e) {
        e.printStackTrace();
        }

        return urlConnection;
        }
     */

    /* DRAFT2
    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkClientTrusted(certs, authType);
                        } catch (CertificateException ignored) {
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkServerTrusted(certs, authType);
                        } catch (CertificateException ignored) {
                        }
                    }
                }
        };
    }

    private SSLSocketFactory getSSLSocketFactory() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = getResources().openRawResource(R.raw.your_cert);
            Certificate ca = cf.generateCertificate(caInput);
            caInput.close();

            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, getWrappedTrustManagers(tmf.getTrustManagers()), null);

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            return HttpsURLConnection.getDefaultSSLSocketFactory();
        }
    }

    private class GETRequest extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("https://your_server_url");
                String token = "rbkY34HnL...";
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setSSLSocketFactory(getSSLSocketFactory());
                urlConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
                        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                        return hv.verify("your_domain.com", session);
                    }
                });
                urlConnection.setRequestProperty("Authorization", "Bearer " + token);
                urlConnection.connect();
                InputStream inputStream;
                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getErrorStream();
                } else {
                    inputStream = urlConnection.getInputStream();
                }
                return String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage() + "\r\n" + parseStream(inputStream);
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            // do something...
        }
     */





    /* --------------------------------windvolt-------------------------------- */


    final int rootId = 100;
    public String getRootId() {
        return Integer.toString(rootId);
    }

    public String getChildren(DiagramModel parent) {
        return parent.getChildren();
    }


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


    /* --------------------------------windvolt-------------------------------- */

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

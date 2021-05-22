package org.windvolt.diagram.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

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

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DiagramConnector {


    public void buildContent(DiagramStore store, InputStream stream) {
        try {

            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader buffer = new BufferedReader(reader);

            // convert
            byte[] bytes = new byte[8192];
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


    public void loadViewImage(ImageView view, String url) {
        new ImageLoader(view).execute(url);
    }

    // AsyncTask used to download image
    static class ImageLoader extends AsyncTask<String, Void, Bitmap> {
        HttpsURLConnection connection = null;
        InputStream content = null;
        ImageView view;

        public ImageLoader(ImageView set_view) {
            view = set_view;
        }

        protected Bitmap doInBackground(String... values) {
            String url = values[0];

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

            if (result != null) {
                view.setImageBitmap(result);
            }
        }
    }//ImageLoader
}

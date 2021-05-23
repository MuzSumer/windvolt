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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
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



    public DiagramStore() {
        store.clear();
    }

    public int storeSize() {
        return store.size();
    }


    /* --------------------------------windvolt-------------------------------- */


    final int rootId = 100;
    public String getRootId() {
        return "100";
    }//getRootId
    public String getNewId() {
        return Integer.toString(rootId + store.size());
    }//getNewId


    public void addModel(DiagramModel model) {
        store.add(model);
    }

    public String getChildren(DiagramModel parent) {
        return parent.getChildren();
    }

    public String addChild(String parent_id, String title, String subject, String symbol, String address, String tags) {

        DiagramModel parent = null;
        DiagramModel child = new DiagramModel();

        if (!parent_id.isEmpty()) {
            parent = findModel(parent_id);
        }

        String id = getNewId();
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
        child.setSymbol(symbol);
        child.setAddress(address);
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

                    //model id is not unique
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

                    //multiple parents error
                }
            }
        }//for

        return parent;
    }//findParent
}

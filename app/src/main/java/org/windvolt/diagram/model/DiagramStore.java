/*
    This file is part of windvolt.org.

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

import java.util.ArrayList;

public class DiagramStore {

    /* --------------------------------windvolt-------------------------------- */

    final int rootId = 100;

    ArrayList<DiagramModel> store = new ArrayList<>();

    public String getRootId() { return Integer.toString(rootId); }

    public String getChildren(DiagramModel parent) {
        return parent.getChildren();
    }

    public String addChild(String parent_id, String tag, String title, String subject, int symbol, int address) {

        DiagramModel parent = null;
        DiagramModel child = new DiagramModel();

        if (!parent_id.isEmpty()) parent = findModel(parent_id);

        int size = store.size();
        String id = Integer.toString(rootId + size);
        child.setId(id);


        // add to parent
        if (parent != null) {
            String children = parent.getChildren();
            if (children.isEmpty()) { children = id; }
            else { children += "," +id; }

            parent.setChildren(children);
        }

        child.setTag(tag);
        child.setTitle(title);
        child.setSubject(subject);
        child.setSymbol(Integer.toString(symbol));
        child.setAddress(Integer.toString(address));

        store.add(child);

        return id;
    }//addChild

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

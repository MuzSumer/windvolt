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

import java.util.ArrayList;

public class DiagramStore {

    ArrayList<DiagramModel> store = new ArrayList<>();


    public DiagramStore() {
        store.clear();
    }//DiagramStore

    public int size() {
        return store.size();
    }//storeSize

    public DiagramModel getModel(int p) {
        if (p < 0) return null;

        if (p > size() - 1) return null;

        return store.get(p);
    }//getModel

    /* --------------------------------windvolt-------------------------------- */


    final int rootId = 100;
    public String getRootId() {
        return "100";
    }//getRootId
    public String getNewId() {
        return Integer.toString(rootId + size());
    }//getNewId




    public String getTargets(DiagramModel parent) {
        return parent.getTargets();
    }//getTargets

    public void addModel(DiagramModel model) {
        store.add(model);
    }

    public String addTarget(String parent_id, String type, String state, String symbol, String title, String subject, String content, String tags) {

        DiagramModel parent = null;
        DiagramModel target = new DiagramModel();

        if (!parent_id.isEmpty()) {
            parent = findModel(parent_id);
        }

        String id = getNewId();
        target.setId(id);

        target.setType(type);
        target.setState(state);

        target.setSymbol(symbol);

        // add to parent
        if (parent != null) {
            String targets = parent.getTargets();

            if (targets.isEmpty()) {
                targets = id;
            } else {
                targets += "," +id;
            }

            parent.setTargets(targets);
        }


        target.setTitle(title);
        target.setSubject(subject);
        target.setContent(content);

        target.setTargets("");
        target.setTags(tags);


        store.add(target);

        return id;
    }//addTarget

    public void removeModelPosition(int position) {

        if (position < 0) {
            return;
        }
        if (position > size()) {
            return;
        }


        // copy to new array
        ArrayList<DiagramModel> new_store = new ArrayList<>();

        int p = 0;

        for (DiagramModel model : store) {
            if (p != position) {
                new_store.add(model);
            }

            p++;
        }

        store.clear();


        // copy back
        store.addAll(new_store);
        new_store.clear();

    }

    /* --------------------------------windvolt-------------------------------- */

    public DiagramModel findModel(String id) {
        if (id == null) {
            return null;
        }
        if (id.isEmpty()) {
            return null;
        }

        DiagramModel found = null;

        for (DiagramModel model : store) {
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

        if (id == null) {
            return null;
        }
        if (id.isEmpty()) {
            return null;
        }

        DiagramModel parent = null;

        for (DiagramModel model : store) {
            String targets = model.getTargets();

            if (!targets.isEmpty()) {
                String[] alltargets = targets.split(",");

                for (String target_id : alltargets) {

                    if (target_id.equals(id)) {

                        if (parent == null) { // first hit
                            parent = model;

                        } else { // detect multiple parents error here
                            parent = model;

                            //multiple parents error
                        }
                    }

                }//target
            }//targets

        }//for

        return parent;
    }//findParent
}

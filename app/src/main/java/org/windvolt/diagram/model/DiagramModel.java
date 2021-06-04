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

public class DiagramModel {

    String id = "";
    public String getId() { return id; }
    public void setId(String value) { id = value; }

    String type = "<type>";
    public String getType() { return type; }
    public void setType(String value) { type = value; }

    String state = "";
    public String getState() { return state; }
    public void setState(String value) { state = value; }

    String symbol = "";
    public String getSymbol() { return symbol; }
    public void setSymbol(String value) { symbol = value; }

    /* --------------------------------windvolt-------------------------------- */

    String title = "<title>";
    public String getTitle() { return title; }
    public void setTitle(String value) { title = value; }

    String subject = "<subject>";
    public String getSubject() { return subject; }
    public void setSubject(String value) { subject = value; }


    String content = "";
    public String getContent() { return content; }
    public void setContent(String vlaue) { content = vlaue; }


    String targets = "";
    public String getTargets() { return targets; }
    public void setTargets(String value) { targets = value; }

    String tags = "";
    public String getTags() { return tags; }
    public void setTags(String value) { tags = value; }


}

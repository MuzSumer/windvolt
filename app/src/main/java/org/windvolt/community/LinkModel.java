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
package org.windvolt.community;

public class LinkModel {
    private String linkSubject = "";
    private String linkAddress = "";
    private int linkImage = 0;

    public LinkModel(String subject, String address, int image) {
        setSubject(subject);
        setAddress(address);
        setImage(image);
    }

    /* --------------------------------windvolt-------------------------------- */

    public String getSubject() { return linkSubject; }
    public void setSubject(String value) { linkSubject = value; }

    public String getAddress() { return linkAddress; }
    public void setAddress(String value) { linkAddress = value; }

    public int getImage() { return linkImage; }
    public void setImage(int value) { linkImage = value; }
}

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
package org.windvolt.system.device_store;

public class DeviceModel {

    /* --------------------------------windvolt-------------------------------- */
    String name = "<name>";
    public String getName() { return name; }
    public void setName(String value) { name = value; }

    String type = "0";
    public String getType() { return type; }
    public void setType(String value) { type = value; }

    String power = "100";
    public String getPower() { return power; }
    public void setPower(String value) { power = value; }

}

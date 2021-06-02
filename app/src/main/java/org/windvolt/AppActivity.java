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
package org.windvolt;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuCompat;

import org.windvolt.diagram.Economy;
import org.windvolt.diagram.News;
import org.windvolt.diagram.Gallery;
import org.windvolt.story.StoryOfWindvolt;
import org.windvolt.system.About;
import org.windvolt.system.DeviceManagement;
import org.windvolt.system.Settings;

public class AppActivity extends AppCompatActivity {


    /* --------------------------------windvolt-------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_coordinator);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // show devider
        MenuCompat.setGroupDividerEnabled(menu, true);

        return true;
    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        // system group
        {
            // edit settings
            if (id == R.id.action_settings) {

                startActivity(new Intent(this, Settings.class));
                return true;
            }

            // edit devices
            if (id == R.id.action_devices) {

                startActivity(new Intent(this, DeviceManagement.class));
                return true;
            }

            // show about
            if (id == R.id.action_about) {

                startActivity(new Intent(this, About.class));
                return true;
            }
        }



        // windvolt group
        {
            // show news
            if (id == R.id.action_news) {

                startActivity(new Intent(this, News.class));
                return true;
            }

            // show story
            if (id == R.id.action_story) {

                startActivity(new Intent(this, StoryOfWindvolt.class));
                return true;
            }

        }


        // diagram group
        {

            // show who is who
            if (id == R.id.action_whoiswho) {

                startActivity(new Intent(this, Gallery.class));
                return true;
            }

            // show economy
            if (id == R.id.action_economy) {

                startActivity(new Intent(this, Economy.class));
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected

}
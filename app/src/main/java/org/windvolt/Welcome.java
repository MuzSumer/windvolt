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
package org.windvolt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.windvolt.diagram.BusinessModel;
import org.windvolt.diagram.WhoIsWho;
import org.windvolt.story.StoryOfWindvolt;
import org.windvolt.system.DeviceManagement;

public class Welcome extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.welcome, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // buttons: devices, wind, solar, geothermal. neutronal
        bindButtons(view);




        //* load live chart */
        WebView webView = (WebView) view.findViewById(R.id.welcome_widget);

        //String value = getString(R.string.welcome_html); // values
        //webView.loadDataWithBaseURL(null, value, "text/html", "utf-8", null);

        String value = "https://www.windjournal.de/einspeisung-live.php?w=320&h=160";

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(value);

    }

    /* --------------------------------windvolt-------------------------------- */

    private void bindButtons(View view) {
        // view devices
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.action_open_devices);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DeviceManagement.class);
                view.getContext().startActivity(intent);
            }
        });


        // open page0 wind
        ImageButton open_page0 = (ImageButton) view.findViewById(R.id.open_page0);
        open_page0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), StoryOfWindvolt.class));

                //NavHostFragment.findNavController(Welcome.this).navigate(R.id.action_open_wind);
            }
        });

        // open page1 solar
        ImageButton open_page1 = (ImageButton) view.findViewById(R.id.open_page1);
        open_page1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(view.getContext(), WhoIsWho.class));

                //NavHostFragment.findNavController(Welcome.this).navigate(R.id.action_open_solar);

            }
        });

        // open page2 geo
        ImageButton open_page2 = (ImageButton) view.findViewById(R.id.open_page2);
        open_page2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), BusinessModel.class));

                //NavHostFragment.findNavController(Welcome.this).navigate(R.id.action_open_geo);
            }
        });
        //open_geo.setVisibility(ImageButton.INVISIBLE);

        // open page3 neutro
        ImageButton open_page3 = (ImageButton) view.findViewById(R.id.open_page3);
        open_page3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(Welcome.this).navigate(R.id.action_open_neutron);
            }
        });
        //open_neutron.setVisibility(ImageButton.INVISIBLE);

    }
}
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
package org.windvolt.recommendation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.windvolt.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/*
    Recommendation
    this fragment provides
        - location display
        - location editor
        - location services dialog

        - latitude/longitude display


        - battery tracker display
        - battery tracking dialog
 */
public class Recommendation extends Fragment {

    final int BATTERY_TRACK_ENTRIES = 10;



    final int LOAD_NOT_AVAILABLE = -1;
    final int LOAD_NOT_RECOMMENDED = 0;
    final int LOAD_SMART_DEVICES = 1;
    final int LOAD_MORE_DEVICES = 10;
    final int LOAD_MANY_DEVICES = 11;

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> allnames = new ArrayList<>();

    AutoCompleteTextView location_chooser;
    String location;

    TextView loc_display, geo_display, bat_display;


    String battery_level_now, battery_level_before;
    String battery_time_now, battery_time_before;



    /* VIEW */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // record battery
        recordBattery();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recommendation, container, false);



        // UPDATE RECOMMENDATION
        // TODO calculate recommendation
        //
        setRecommendation(view, LOAD_NOT_AVAILABLE);


        // UPDATE LOCATION AND GEODATA
        //
        //ImageView rec_image = view.findViewById(R.id.recommendation_image);
        loc_display = (TextView) view.findViewById(R.id.location_display);
        geo_display = (TextView) view.findViewById(R.id.location_geodata);

        // update battery
        bat_display = (TextView) view.findViewById(R.id.location_battery);
        bat_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BatteryDialog dialog = new BatteryDialog();
                dialog.show(getActivity().getSupportFragmentManager(), "battery");
            }
        });

        // load location name
        //
        location = zLoadLocation();

        // display location
        //
        String loc = location;
        String notice = getString(R.string.location_notice); // values
        if (location.isEmpty()) { loc = notice; }

        loc_display.setText(loc);


        /* START EDITIG LOCATION */
        loc_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // load stations
                loadStations();
                location_chooser.setText(location);

                // toogle visibilty
                loc_display.setVisibility(View.GONE);
                geo_display.setVisibility(View.GONE);
                bat_display.setVisibility(View.GONE);

                location_chooser.setVisibility(View.VISIBLE);
            }
        });



        /* refresh display */
        displayGeodata();
        displayBattery();


        /* open services */
        FloatingActionButton services_open = (FloatingActionButton) view.findViewById(R.id.services_open);
        services_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_ALARM, 80);
                beep.startTone(ToneGenerator.TONE_PROP_BEEP, 200);

                ServicesDialog dialog = new ServicesDialog();
                dialog.show(getActivity().getSupportFragmentManager(), getString(R.string.location_services));
            }
        });


        /* allow or hide services */
        if (!zLocationServiceAllowed()) {
            services_open.setVisibility(View.GONE);
        }


        /* return inflated view */
        return view;
    }


    /* EDIT */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        /* location autocomplete */
        location_chooser = (AutoCompleteTextView) view.findViewById(R.id.location_chooser);

        location_chooser.setText("");
        location_chooser.clearListSelection();

        location_chooser.setThreshold(1); //will start working from first character
        location_chooser.setTextColor(Color.BLACK); // must

        /* adapt stations */
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.location_item, names);
        location_chooser.setAdapter(adapter);


        /* STOP EDITIG LOCATION */
        location_chooser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                /* save location */
                location = location_chooser.getText().toString();
                loc_display.setText(location);

                zSaveLocation(location);



                /* save geodata */
                String[] geo = loadGeodata().split(":");

                String longitude = geo[0];
                String latitude = geo[1];

                zSaveLongitude(longitude);
                zSaveLatitude(latitude);



                /* display geodata */
                displayGeodata();



                /* toogle visibilty */
                location_chooser.setVisibility(View.GONE);

                loc_display.setVisibility(View.VISIBLE);
                geo_display.setVisibility(View.VISIBLE);
                bat_display.setVisibility(View.VISIBLE);


                /* free memory */
                names.clear();
                allnames.clear();

                location_chooser.setText("");
                location_chooser.clearListSelection();



                /* user assurance */
                String location_saved = getString(R.string.location_saved); // values
                Toast.makeText(getContext(), location_saved, Toast.LENGTH_SHORT).show();
            }
        });

    }





    /* set recommendation */
    public void setRecommendation(View view, int state) {

        // display recommendation
        TextView recommend = view.findViewById(R.id.recommendation_text);

        switch (state) {
            case LOAD_NOT_RECOMMENDED:
                recommend.setText(getString(R.string.recommendation_load_prevent)); // vlaues
                break;

            case LOAD_SMART_DEVICES:
                recommend.setText(getString(R.string.recommendation_load_small)); // values
                break;

            case LOAD_MORE_DEVICES:
                recommend.setText(getString(R.string.recommendation_load_more)); // values
                break;

            case LOAD_MANY_DEVICES:
                recommend.setText(getString(R.string.recommendation_load_all)); // values
                break;

            default:
                recommend.setText(getString(R.string.recommendation_unavailable));
        }
    }


    /* services */
    public static class ServicesDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.location_services, null);


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String loc = sharedPreferences.getString("location_input", "");

            //String services = getString(R.string.location_services); // values
            builder.setView(view).setTitle("Dienste: " + loc);


            // register services
            registerServices(view);



            return builder.create();
        }


        private void registerServices(View view) {

            TextView windy = view.findViewById(R.id.service_windy);
            windy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                    String longitude = sharedPreferences.getString("location_longitude", "");
                    String latitude = sharedPreferences.getString("location_latitude", "");

                    String url = "https://www.windy.com/?";
                    url += latitude;
                    url += ",";
                    url += longitude;
                    url += ",";
                    url += "10";

                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });


            TextView maps = view.findViewById(R.id.service_maps);
            maps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                    String longitude = sharedPreferences.getString("location_longitude", "");
                    String latitude = sharedPreferences.getString("location_latitude", "");

                    // 33.000/-118.000
                    String url = "https://www.openstreetmap.org/#map=6/";
                    url += latitude;
                    url += "/";
                    url += longitude;

                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });


            TextView knowledge = view.findViewById(R.id.service_knowledge);
            knowledge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                    String loc = sharedPreferences.getString("location_input", "");

                    String url = "https://de.wikipedia.org/w/index.php?search=";
                    url += loc;

                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            });
        }

    }


    public static class BatteryDialog extends DialogFragment {

        final int CHART_LINES = 5;

        //final String CHART_DOT = "•";
        final String CHART_DOT = "+";
        final String CHART_NO_DOT = " ";

        final String CHART_DELIM = "  ";
        final int CHART_SIZE = 3;

        @Override
        public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.battery_tracker, null);

            // makeSampleData() creates sample data for diagnostic

            if (false) {
                makeSampleData();
            }



            /* load tracking data from preferences */
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String levels = sharedPreferences.getString("battery_level", "");
            String times = sharedPreferences.getString("battery_time", "");


            String[] vlevels = levels.split(";");
            String[] vtimes = times.split(";");






            /* create chart */
            ArrayList<TextView> lines = new ArrayList<>();

            TextView l0 = view.findViewById(R.id.chart_line0);
            TextView l1 = view.findViewById(R.id.chart_line1);
            TextView l2 = view.findViewById(R.id.chart_line2);
            TextView l3 = view.findViewById(R.id.chart_line3);
            TextView l4 = view.findViewById(R.id.chart_line4);

            lines.add(l0);
            lines.add(l1);
            lines.add(l2);
            lines.add(l3);
            lines.add(l4);


            int size = vlevels.length;

            // print chart lines
            for (int l=0; l<lines.size(); l++) {
                TextView tv = lines.get(l);
                int border = l * 100/CHART_LINES;

                // print line
                String load = "";
                for (int p=0; p<size; p++) {
                    String vlevel = vlevels[p];

                    if (!vlevel.isEmpty()) {
                        Float flevel = Float.parseFloat(vlevel);
                        int ilevel = flevel.intValue();


                        if (ilevel > border) { load = prepose(load, CHART_DOT); }
                        else { load = prepose(load, CHART_NO_DOT); }
                    }
                }//for

                tv.setText(load);
            }//for





            // print chart legend
            String legend0 = "", legend1 = "";
            int sumup = 0;

            for (int p=0; p<size; p++) {
                String vlevel = vlevels[p], vtime = vtimes[p];

                if (!vlevel.isEmpty()) {

                    // get level
                    Float flevel = Float.parseFloat(vlevel);
                    int ilevel = flevel.intValue();

                    // sumup
                    sumup += ilevel;


                    // calculate time difference
                    long milliseconds = 0;

                    try { // calculate time since last launch
                        Date this_time = new Date(System.currentTimeMillis());
                        Date last_time = new Date(Long.parseLong(vtime));

                        milliseconds = this_time.getTime() - last_time.getTime();

                    } catch (Exception e) {}


                    // analyze time difference
                    long minutes = milliseconds/1000/60;
                    long hours = minutes/60;
                    long days = hours/24;

                    if (days > 0) {
                        legend0 = prepose(legend0,"" + days);
                        legend1 = prepose(legend1, "d");
                    } else  if (hours > 0) {
                        legend0 = prepose(legend0,"" + hours);
                        legend1 = prepose(legend1, "h");
                    } else {
                        legend0 = prepose(legend0,"" + minutes);
                        legend1 = prepose(legend1, "m");
                    }

                }//vlevel.isEmpty()

            }//for



            TextView vlegend0 = view.findViewById(R.id.chart_legend0);
            vlegend0.setText(legend1);

            TextView vlegend1 = view.findViewById(R.id.chart_legend1);
            vlegend1.setText(legend0);

            TextView level = view.findViewById(R.id.track_level);

            int avergae = sumup/size;
            level.setText("average " + avergae + "%");


            // dialog features
            builder.setView(view).setTitle("battery");

            builder.setPositiveButton("exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            builder.setNegativeButton("clear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // query decision
                    AlertDialog.Builder query = new AlertDialog.Builder(getActivity());

                    query.setTitle("clear battery track");
                    query.setMessage("the battery track will be rebuilt over time");

                    query.setPositiveButton("okay", null);
                    query.create().show();

                    // cleat battery track
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // clear
                    editor.putString("battery_level", "");
                    editor.apply();

                    editor.putString("battery_time", "");
                    editor.apply();


                    // reassure user
                    Toast.makeText(getContext(), "battery track cleared", Toast.LENGTH_LONG).show();
                }
            });



            return builder.create();
        }

        /* preposes value to t */
        private String prepose(String t, String value) {
            String output;

            if (t.isEmpty()) {
                output = value;
            } else {
                output = value + CHART_DELIM;
                output = output.substring(0, CHART_SIZE);
            }

            return output + t;
        }



        /*
        * creates sample data for diagnostic
        */
        private void makeSampleData() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();


            // control values
            String levels = "10.0";
            levels += ";20.0";
            levels += ";30.0";
            levels += ";40.0";
            levels += ";50.0";
            levels += ";60.0";
            levels += ";70.0";
            levels += ";80.0";
            levels += ";90.0";
            levels += ";100.0";

            Date now = new Date(System.currentTimeMillis());
            Calendar calendar = Calendar.getInstance();
            String times = Long.toString(now.getTime());

            for (int t=0; t<10; t++) {
                calendar.setTime(now);

                calendar.add(Calendar.HOUR, -7);

                now = calendar.getTime();

                times += ";" + now.getTime();
            }


            editor.putString("battery_level", levels);
            editor.apply();

            editor.putString("battery_time", times);
            editor.apply();

        }
    }




    /* -------------------------------------------------------------------------------- */


    /* load latitude:longitude */
    private String loadGeodata() {
        String longitude = "";
        String latitude = "";

        for (int geo=0; geo<allnames.size(); geo++) {
            String[] fullgeo = allnames.get(geo).split(";");

            if (location.equals(fullgeo[0])) {
                String[] coordinates = fullgeo[2].split(",");

                longitude = coordinates[0];
                latitude = coordinates[1];

                /*
                String altitude = coordinates[2];
                Double dlongitude = Location.convert(longitude);
                Double dlatitude = Location.convert(latitude);
                Double daltitude = Location.convert(altitude);
               */
            }

        }

        return longitude + ":" + latitude;
    }

    /* display latitude/longitude */
    private void displayGeodata() {
        String longitude = zLoadLongitude();
        String latitude = zLoadLatitude();

        if (longitude.isEmpty()) { longitude = "-"; }
        if (latitude.isEmpty()) { latitude = "-"; }

        String loc = "Breite: " + latitude + "  Länge: " + longitude;
        geo_display.setText(loc);
    }



    /* read and set battery level */
    private void recordBattery() {

        battery_level_before = zLoadBatteryLevel();
        battery_time_before = zLoadBatteryTime();


        /* get load % */
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        Float lbattery = level * 100 / (float)scale;
        battery_level_now = lbattery.toString();




        // calculate time
        long milliseconds = 0;

        long time_now = System.currentTimeMillis();

        battery_time_now = Long.toString(time_now);

        long time_before = time_now;


        try { // calculate time difference

            time_before = Long.parseLong(battery_time_before);

            /*
            // tune date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(last_time);

            // adjust time: -0 do not adjust, -1 hour, -2 hour, ...
            int adjust_hours = -0;
            calendar.add(Calendar.HOUR, adjust_hours);

            last_time = calendar.getTime();
            */

            milliseconds = time_now - time_before;

        } catch (Exception e) {

            // initialialize values
            zSaveBatteryLevel(battery_level_now);
            zSaveBatteryTime(battery_time_now);
        }

        long hours = milliseconds/1000/60/60;

        // save if more than 1 hour ago
        if (hours > 0) {
            zSaveBatteryLevel(battery_level_now);
            zSaveBatteryTime(battery_time_now);

            battery_time_before = Long.toString(time_before);
        }

    }

    /* display battery */
    private void displayBattery() {

        String bat = "battery: ";



        /* show time since last */
        long milliseconds = 0;

        try { // calculate time difference

            long time_now = Long.parseLong(battery_time_now);
            long time_before = Long.parseLong(battery_time_before);


            milliseconds = time_now - time_before;

        } catch (Exception e) {}


        long minutes = milliseconds/1000/60;
        long hours = minutes/60;
        long days = hours/24;


        if (days > 0) { bat += days + "d "; }
        else  if (hours > 0) { bat += hours + "h "; }
        else { bat += minutes + "m "; }


        /* display load difference */
        Float level_now = Float.parseFloat(battery_level_now);

        try {
            float level_before = Float.parseFloat(battery_level_before);

            Float delta_level = level_now - level_before;
            int intdelta = delta_level.intValue();

            if (delta_level < 0) { bat += "" + intdelta; }
            else { bat += "+" + intdelta; }


        } catch (Exception e) {
            bat += "" + level_now.intValue();
        }
        bat += "%";



        /* display battery text */
        bat_display.setText(bat);
    }



    /* read and adapt stations */
    private void loadStations() {
        // load stations
        InputStream inputStream = getResources().openRawResource(R.raw.stations2);
        try {

            int size = inputStream.available();
            byte[] station_list = new byte[size];
            inputStream.read(station_list);
            inputStream.close();


            String allstations = new String(station_list);
            String[] stations = allstations.split("\\|");

            for (int i=0; i<stations.length; i++){

                allnames.add(stations[i]);

                String[] values = stations[i].split(";");
                String name = values[0];

                names.add(name);
            }

            Collections.sort(names, null);


        } catch (IOException e) {
            // could not read stations

        }
    }




    /* preferences load/save */

    private String zLoadLocation() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String loc = sharedPreferences.getString("location_input", "");
        return loc;
    }
    private void zSaveLocation(String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("location_input", value);
        editor.apply();
    }


    private String zLoadLongitude() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String longitude = sharedPreferences.getString("location_longitude", "");
        return longitude;
    }
    private void zSaveLongitude(String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("location_longitude", value);
        editor.apply();
    }

    private String zLoadLatitude() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String latitude = sharedPreferences.getString("location_latitude", "");
        return latitude;
    }
    private void zSaveLatitude(String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("location_latitude", value);
        editor.apply();
    }


    private void zClearBatteryTrack() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // clear
        editor.putString("battery_level", "");
        editor.apply();

        editor.putString("battery_time", "");
        editor.apply();
    }

    private String zLoadBatteryLevel() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String battery_levels = sharedPreferences.getString("battery_level", "");
        String[] values = battery_levels.split(";");

        String battery_level = "";

        if (values.length > 0) {
            battery_level = values[0];
        }

        return battery_level;
    }
    private void zSaveBatteryLevel(String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String battery_levels = sharedPreferences.getString("battery_level", "");
        String[] values = battery_levels.split(";");

        String battery_level = value;
        int hits = 1;

        if (values.length > 0) {
            for (int p=0; p<values.length; p++) {
                String level = values[p];

                if (!level.isEmpty()) {
                    if (hits < BATTERY_TRACK_ENTRIES) {
                        battery_level +=  ";" + level;
                        hits++;
                    }
                }

            }
        }

        editor.putString("battery_level", battery_level);
        editor.apply();
    }

    private String zLoadBatteryTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String battery_times = sharedPreferences.getString("battery_time", "");
        String[] values = battery_times.split(";");

        String battery_time = "";

        if (values.length > 0) battery_time = values[0];

        return battery_time;
    }
    private void zSaveBatteryTime(String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String battery_times = sharedPreferences.getString("battery_time", "");
        String[] values = battery_times.split(";");

        String battery_time = value;
        int hits = 1;

        if (values.length > 0) {
            for (int p=0; p<values.length; p++) {
                String time = values[p];

                if (!time.isEmpty()) {
                    if (hits < BATTERY_TRACK_ENTRIES) {
                        battery_time +=  ";" + time;
                        hits++;
                    }
                }

            }
        }

        editor.putString("battery_time", battery_time);
        editor.apply();
    }


    private boolean zLocationServiceAllowed() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        return sharedPreferences.getBoolean("location_services", false);
    }





    //class
}
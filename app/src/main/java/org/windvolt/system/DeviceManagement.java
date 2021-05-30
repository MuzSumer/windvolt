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
package org.windvolt.system;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import org.windvolt.R;
import org.windvolt.system.device_store.DeviceModelStore;

public class DeviceManagement extends AppCompatActivity {

    static DeviceModelStore deviceStore;
    static TextView sumupDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_device_listview);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(R.string.devices_title); // devices_title
        }


        //* MODEL */

        deviceStore = new DeviceModelStore(this);
        deviceStore.createListViewAdapter(R.layout.system_device_item, R.id.device_name);

        ListView lv = (ListView) findViewById(R.id.list_devices);
        lv.setAdapter(deviceStore.getListViewAdapter());

        deviceStore.loadModel();
        deviceStore.initialize();

        if (deviceStore.isNotificationAllowed()) {
            // TODO notify

        }


        sumupDevices = findViewById(R.id.device_sum);
        sumupDevices.setText(deviceStore.getPowerCapability());









        /* --------------------------------windvolt-------------------------------- */

        //* DIALOG ACTIONS */

        // share action
        findViewById(R.id.device_share).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO share devices

                        //String androidId = Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

                        String notice = "derzeit nicht unterstützt";

                        Snackbar.make(view, notice, Snackbar.LENGTH_LONG).show();
                    }
                }
        );



        // add action
        findViewById(R.id.device_add).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        DialogAddDevice dialog = new DialogAddDevice();
                        dialog.show(getSupportFragmentManager(), "add device");
                    }
                }
        );

        // remove action
        findViewById(R.id.device_remove).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        DialogDeleteDevice dialog = new DialogDeleteDevice();
                        dialog.show(getSupportFragmentManager(), "remove device");
                    }
                }
        );



    }//onCreate


    /* --------------------------------windvolt-------------------------------- */


    //* DEVICE DIALOGS */
    public static class DialogAddDevice extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.system_device_add, null);

            builder.setView(view)
                    .setTitle(getString(R.string.device_add_title))

                    .setPositiveButton(getString(R.string.device_action_add), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // create device
                            EditText edit_name = view.findViewById(R.id.position_input);
                            String name = edit_name.getText().toString();

                            // evaluate type
                            RadioButton edit_type;
                            String type = "10";

                            edit_type = view.findViewById(R.id.type_mobile);
                            if (edit_type.isChecked()) type = "0";

                            edit_type = view.findViewById(R.id.type_ecar);
                            if (edit_type.isChecked()) type = "1";

                            edit_type = view.findViewById(R.id.type_household);
                            if (edit_type.isChecked()) type = "2";


                            EditText edit_capacity = view.findViewById(R.id.capacity_edit);
                            String capacity = edit_capacity.getText().toString();

                            deviceStore.addDevice(name, type, capacity);
                            deviceStore.saveModel();

                            // refresh power sumup
                            sumupDevices.setText(deviceStore.getPowerCapability());

                        }

                    })
                    .setNegativeButton(getString(R.string.device_action_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }//DialogAddDevice

    public static class DialogDeleteDevice extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.system_device_delete, null);

            builder.setView(view)
                    .setTitle(getString(R.string.device_del_title))

                    .setPositiveButton(getString(R.string.device_action_delete), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            
                            // delete device
                            EditText del_name = view.findViewById(R.id.position_input);
                            String intpos = del_name.getText().toString();
                            int position = Integer.parseInt(intpos);

                            // remove position
                            deviceStore.removeDevice(position);
                            deviceStore.saveModel();

                            // refresh power sumup
                            sumupDevices.setText(deviceStore.getPowerCapability());

                        }

                    })
                    .setNegativeButton(getString(R.string.device_action_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }//DialogDeleteDevice

}
package org.windvolt.diagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramActivity;
import org.windvolt.diagram.model.DiagramModel;

public class Devices extends DiagramActivity {

    final String MODEL_URL = "devices.xml";

    LinearLayout diagram;
    TextView analysis;
    /* --------------------------------windvolt-------------------------------- */

    @Override
    public String getNamespace() {
        return MODEL_URL;
    }

    @Override
    public void createStore() {

        loadPrivateModel(getNamespace());

        if (getStore().size() == 0) {
            createBuildModel();

            savePrivateModel(getNamespace());
        }

        setFocus(null, false);
    }


    @Override
    public void setFocus(String id, boolean expand) {

        diagram.removeAllViews();

        int power = 0;

        for (int p=0; p<getStore().size(); p++) {

            DiagramModel model = getStore().getModel(p);
            id = model.getId();

            String model_power = model.getContent();
            if (isNumeric(model_power)) {
                power += Integer.parseInt(model_power);
            }
            addViewModel(id);

        }

        analysis.setText("Gesamtleistung " + power + " mAh");

    }//setFocus



    private void addViewModel(String id) {
        DiagramModel model = getStore().findModel(id);
        if (model == null) {
            return;
        }

        LinearLayout outer = new LinearLayout(this);
        LinearLayout inner = new LinearLayout(this);
        {
            outer.setOrientation(LinearLayout.HORIZONTAL);
            outer.setPadding(4,4,4,4);


            inner.setOrientation(LinearLayout.HORIZONTAL);
            inner.setPadding(4,4,4,4);

        }



        TextView title = new TextView(this);
        {
            title.setPadding(4, 4, 4, 4);
            title.setTextColor(Color.RED);


            String value = model.getContent() + " mAh";
            title.setText(value);
        }


        TextView subject = new TextView(this);
        {
            subject.setPadding(4, 4, 4, 4);

            subject.setText(model.getSubject());
            //text.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Headline4); // 34sp
            subject.setTextAppearance(this, R.style.TextAppearance_AppCompat_Large); // 22sp
            //subject.setTextAppearance(this, R.style.TextAppearance_AppCompat_Headline); //24sp
        }


        ImageView image = new ImageView(this);
        {
            image.setPadding(2, 2, 2, 2);
            loadViewImage(image, model.getSymbol(), 80, 80);

            image.setContentDescription(id);
            image.setOnClickListener(editContent);
        }




        inner.addView(title);
        inner.addView(subject);

        outer.addView(image);
        outer.addView(inner);

        diagram.addView(outer);
    }


    private EditContent editContent = new EditContent();
    private class EditContent implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String id = view.getContentDescription().toString();

            DiagramModel model = getStore().findModel(id);
            if (model == null) {
                return;
            }

            EditDeviceDialog dialog = new EditDeviceDialog(Devices.this, model);
            dialog.show(getSupportFragmentManager(), "edit device");
        }
    }

    private void createBuildModel() {
        // add current device
        String cell_manufacturer = Build.MANUFACTURER;
        String cell_model = Build.MODEL;

        /*
        Android ID via Settings.Secure
        Android Build.SERIAL 	HT6C90202028
        Android Build.MODEL 	Pixel XL
        Android Build.BRAND 	google
        Android Build.MANUFACTURER 	Google
        Android Build.DEVICE 	marlin
        Android Build.PRODUCT 	marlin

        String androidId = Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        */


        // create model
        DiagramModel model = new DiagramModel();


        model.setId(getStore().getNewId());
        model.setType("0");
        model.setState("full");
        model.setSymbol("windvolt");

        model.setTitle(cell_manufacturer);
        model.setSubject(cell_model);

        model.setContent("11");

        model.setTargets("");
        model.setTags("");


        getStore().addModel(model);

    }
    /* --------------------------------windvolt-------------------------------- */

    public static class EditDeviceDialog extends DialogFragment {

        DiagramActivity activity;
        DiagramModel model;

        RadioButton type_mobile;
        RadioButton type_ebike;
        RadioButton type_ecar;
        RadioButton type_household;
        RadioButton type_other;

        EditText edit_name;
        EditText edit_capacity;

        boolean create;

        public EditDeviceDialog(DiagramActivity set_activity, DiagramModel set_model) {
            activity = set_activity;
            model = set_model;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.dialog_add_device, null);

            builder.setView(view).setTitle(getString(R.string.device_add_title));

            type_mobile = view.findViewById(R.id.type_mobile);
            type_ebike = view.findViewById(R.id.type_ebike);
            type_ecar = view.findViewById(R.id.type_ecar);
            type_household = view.findViewById(R.id.type_household);
            type_other = view.findViewById(R.id.type_other);

            edit_name = view.findViewById(R.id.position_input);
            edit_capacity = view.findViewById(R.id.content_edit);

            create = model == null;
            if (create) {
                model = new DiagramModel();
                model.setId(activity.getStore().getNewId());
            } else {
                // preset values
                edit_name.setText(model.getSubject());
                edit_capacity.setText(model.getContent());

                type_mobile.setChecked(true);
            }

            builder.setPositiveButton(getString(R.string.record_add_action), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    // evaluate type
                    String type = "0";
                    {
                        if (type_mobile.isChecked()) type = "0";

                        if (type_ebike.isChecked()) type = "1";

                        if (type_ecar.isChecked()) type = "10";

                        if (type_household.isChecked()) type = "11";

                        if (type_other.isChecked()) type = "99";

                    }
                    model.setType(type);


                    model.setState("active");
                    model.setSymbol("windvolt");

                    model.setTitle("mobile");


                    // subject

                    String name = edit_name.getText().toString();
                    model.setSubject(name);


                    String capacity = edit_capacity.getText().toString();
                    model.setContent(capacity);

                    model.setTargets("");

                    model.setTags("");

                    if (create) {
                        activity.getStore().addModel(model);
                    }


                    // save changes
                    activity.savePrivateModel(activity.getNamespace());

                    // redraw diagram
                    activity.setFocus(null, false);

                }

            });

            builder.setNegativeButton(getString(R.string.record_cancel_action), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // do nothing
                }
            });

            return builder.create();
        }

    }//AddDeviceDialog

    public static class RemoveDeviceDialog extends DialogFragment {
        DiagramActivity activity;

        public RemoveDeviceDialog(DiagramActivity set_activity) {
            activity = set_activity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.dialog_remove_position, null);

            builder.setView(view)
                    .setTitle(getString(R.string.device_del_title))

                    .setPositiveButton(getString(R.string.record_delete_action), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // delete device
                            EditText del_name = view.findViewById(R.id.position_input);
                            String intpos = del_name.getText().toString();
                            int position = Integer.parseInt(intpos);

                            // remove position
                            activity.getStore().removeModelPosition(position);

                            activity.savePrivateModel(activity.getNamespace());

                            activity.setFocus(null, false);

                        }

                    })
                    .setNegativeButton(getString(R.string.record_cancel_action), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }//RemoveDeviceDialog

    /* --------------------------------windvolt-------------------------------- */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_devices);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(R.string.devices_title); // devices_title
        }



        bindActions();

        analysis = findViewById(R.id.device_label);

        // start diagram
        diagram = findViewById(R.id.device_content);

        createStore();
    }



    private void bindActions() {

        // share action
        findViewById(R.id.device_share).setOnClickListener(
                new View.OnClickListener() {

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
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditDeviceDialog dialog = new EditDeviceDialog(Devices.this, null);

                        dialog.show(getSupportFragmentManager(), "add device");
                    }
                }
        );

        // remove action
        findViewById(R.id.device_remove).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        RemoveDeviceDialog dialog = new RemoveDeviceDialog(Devices.this);

                        dialog.show(getSupportFragmentManager(), "remove device");
                    }
                }
        );

    }
}
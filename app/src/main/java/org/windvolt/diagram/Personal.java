package org.windvolt.diagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramActivity;
import org.windvolt.diagram.model.DiagramModel;
import org.windvolt.diagram.model.DiagramStore;

public class Personal extends DiagramActivity {

    final String MODEL_URL = "personal.xml";

    LinearLayout diagram;

    @Override
    public String getNamespace() {
        return MODEL_URL;
    }

    @Override
    public void createStore() {

        setStore(new DiagramStore());

        loadPrivateModel(getNamespace());


        setFocus(null, false);
    }


    @Override
    public void setFocus(String id, boolean expand) {

        diagram.removeAllViews();


        for (int p=0; p<getStore().size(); p++) {

            DiagramModel model = getStore().getModel(p);
            id = model.getId();

            addViewModel(id);

        }

    }//setFocus

    private void addViewModel(String id) {
        DiagramModel model = getStore().findModel(id);
        if (model == null) {
            return;
        }

        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.HORIZONTAL);
        outer.setPadding(4,4,4,4);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(4,4,4,4);


        TextView title = new TextView(this);

        title.setPadding(4, 4, 4, 4);
        title.setTextColor(Color.RED);

        //text.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Headline4); // 34sp
        title.setTextAppearance(this, R.style.TextAppearance_AppCompat_Large); // 22sp
        //subject.setTextAppearance(this, R.style.TextAppearance_AppCompat_Headline); //24sp

        title.setText(model.getTitle());



        TextView subject = new TextView(this);
        subject.setPadding(4, 4, 4, 4);

        subject.setText(model.getContent());



        ImageView image = new ImageView(this);
        image.setPadding(2, 2, 2, 2);

        loadViewImage(image, model.getSymbol(), 80, 80);


        inner.addView(title);
        inner.addView(subject);

        outer.addView(image);
        outer.addView(inner);

        diagram.addView(outer);
    }

    /* --------------------------------windvolt-------------------------------- */

    public static class AddRecordDialog extends DialogFragment {
        DiagramActivity activity;

        public AddRecordDialog(DiagramActivity set_activity) {
            activity = set_activity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();

            final View view = inflater.inflate(R.layout.dialog_add_personal_record, null);

            builder.setView(view)
                    .setTitle(getString(R.string.personal_add_record))

                    .setPositiveButton(getString(R.string.personal_add_record), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            // create device model
                            DiagramModel model = new DiagramModel();


                            model.setId(activity.getStore().getNewId());



                            model.setType("personal");


                            model.setState("");

                            EditText edit_symbol = view.findViewById(R.id.symbol_edit);
                            model.setSymbol(edit_symbol.getText().toString());



                            // subject

                            EditText edit_name = view.findViewById(R.id.position_input);
                            model.setTitle(edit_name.getText().toString());

                            model.setSubject(edit_name.getText().toString());


                            EditText edit_content = view.findViewById(R.id.content_edit);
                            model.setContent(edit_content.getText().toString());

                            model.setTargets("");

                            model.setTags("");


                            activity.getStore().addModel(model);


                            // save chage
                            activity.savePrivateModel(activity.getNamespace());

                            // redraw diagram
                            activity.setFocus(null, false);

                        }

                    })
                    .setNegativeButton(getString(R.string.personal_record_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }

    }//AddRecordDialog

    public static class RemoveRecordDialog extends DialogFragment {
        DiagramActivity activity;

        public RemoveRecordDialog(DiagramActivity set_activity) {
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
                    .setTitle(getString(R.string.personal_remove_record))

                    .setPositiveButton(getString(R.string.personal_remove_record), new DialogInterface.OnClickListener() {
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
                    .setNegativeButton(getString(R.string.personal_record_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }//RemoveRecordDialog


    /* --------------------------------windvolt-------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_personal);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(R.string.personal_title); // title
        }



        bindActions();


        // start diagram
        diagram = findViewById(R.id.record_content);

        createStore();
    }

    private void bindActions() {

        // share action
        findViewById(R.id.record_share).setOnClickListener(
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
        findViewById(R.id.record_add).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        AddRecordDialog dialog = new AddRecordDialog(Personal.this);

                        dialog.show(getSupportFragmentManager(), "add record");
                    }
                }
        );

        // remove action
        findViewById(R.id.record_remove).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        RemoveRecordDialog dialog = new RemoveRecordDialog(Personal.this);

                        dialog.show(getSupportFragmentManager(), "remove record");
                    }
                }
        );

    }
}
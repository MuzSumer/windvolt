package org.windvolt.diagram;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramFragment11;
import org.windvolt.diagram.model.DiagramModel;

public class NewsPage extends DiagramFragment11 {


    final String MODEL_URL = "https://windvolt.eu/model/news/de/0diagram.xml";

    LinearLayout diagram;

    Drawable roundbox = AppCompatResources.getDrawable(getContext(), R.drawable.app_roundbox);

    @Override
    public String getNamespace() {
        return MODEL_URL;
    }

    @Override
    public void createStore() {

        // load model
        loadModel(this, getNamespace());

    }//createStore

    @Override
    public void setFocus(String any_id, boolean expand) {

        for (int p = 0; p< getStore().size(); p++) {

            DiagramModel model = getStore().getModel(p);

            String id = model.getId();

            View layout = findModelView(id);
            if (layout == null) {
                addModelView(id);
            }
        }
    }//setFocus


    /* --------------------------------windvolt-------------------------------- */


    private View findModelView(String id) {
        View found = null;

        int size = diagram.getChildCount();

        for (int p=0; p<size; p++) {
            View layout = diagram.getChildAt(p);

            String p_id = layout.getContentDescription().toString();
            if (p_id.equals(id)) found = layout;

            // detect multiple id error here
        }

        return found;
    }//findModelView

    // complex view
    public void addModelView(String id) {


        DiagramModel model = getStore().findModel(id);
        if (null == model) return;



        LinearLayout outer = new LinearLayout(getContext());
        LinearLayout inner = new LinearLayout(getContext());
        {
            outer.setBackground(roundbox);
            outer.setOrientation(LinearLayout.HORIZONTAL);
            outer.setPadding(4, 4, 4, 4);



            inner.setOrientation(LinearLayout.VERTICAL);
            inner.setPadding(4, 4, 4, 4);
        }



        ImageView image = new ImageView(getContext());
        {
            image.setPadding(4, 16, 4, 8);

            loadViewImage(image, model.getSymbol());
        }


        TextView title = new TextView(getContext());
        {
            title.setPadding(8, 8, 8, 8);
            title.setGravity(Gravity.CENTER_VERTICAL);
            title.setText(model.getTitle());
        }



        TextView subject = new TextView(getContext());
        {
            subject.setPadding(8, 8, 8, 8);
            subject.setTextSize(17);

            subject.setGravity(Gravity.CENTER_VERTICAL);
            subject.setText(model.getSubject());
            subject.setTextColor(Color.parseColor("#0277BD"));

            //text.setText(w + "/" + h);
        }





        outer.addView(image);
        outer.addView(inner);

        inner.addView(subject);
        inner.addView(title);

        outer.setContentDescription(id);
        outer.setOnClickListener(onFocus);

        diagram.addView(outer);

    }//addModelView


    private final OnFocus onFocus = new OnFocus();
    class OnFocus implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String id = view.getContentDescription().toString();

            DiagramModel model = getStore().findModel(id);
            if (model == null) {
                return;
            }

            String address = model.getContent();
            if (!address.isEmpty()) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(address)));
            }
        }
    }//OnFocus


    /* --------------------------------windvolt-------------------------------- */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.diagram_news, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // create diagram
        diagram = view.findViewById(R.id.news_content);

        createStore();
    }
}
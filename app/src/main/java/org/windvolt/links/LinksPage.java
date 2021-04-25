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
package org.windvolt.links;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.windvolt.R;

import java.util.ArrayList;

public class LinksPage extends Fragment {

    /* --------------------------------windvolt-------------------------------- */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.links_recyclerview, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.links);
        //recyclerView.setHasFixedSize(true);
        //vagrant error if set true

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        LinkAdapter adapter = new LinkAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.LinkViewHolder> {

        private ArrayList<LinkModel> links = new ArrayList<>();

        public LinkAdapter() {
            links.add(new LinkModel("Livedaten der Netzagentur", "https://www.smard.de", R.drawable.link_smard));
            links.add(new LinkModel("Nachrichten (viele Tracker)", "https://www.windjournal.de", R.drawable.link_windjournal));
            links.add(new LinkModel("BMWE Energiewende", "https://www.erneuerbare-energien.de", R.drawable.link_energiewende));
            links.add(new LinkModel("Windkraft beim Umweltamt", "https://www.umweltbundesamt.de/themen/klima-energie/erneuerbare-energien/windenergie", R.drawable.link_umweltamt));
            links.add(new LinkModel("Verband Windenergie", "https://www.wind-energie.de", R.drawable.link_windverband));
            links.add(new LinkModel("Windbranche", "https://www.windbranche.de/wind/windstrom/windenergie-deutschland", R.drawable.link_windbranche));
            links.add(new LinkModel("ENTSO-E Transparenzplattform", "https://www.netztransparenz.de/Weitere-Veroeffentlichungen/Windenergie-Hochrechnung", R.drawable.link_netztransparenz));

            // no https for bricklebrit
            links.add(new LinkModel("Strombörse EEX (kein https)", "http://bricklebrit.com/stromboerse_leipzig.html", R.drawable.link_bricklebrit));
        }


        @NonNull
        @Override
        public LinkAdapter.LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item, parent, false);

            return new LinkAdapter.LinkViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LinkAdapter.LinkViewHolder holder, final int position) {
            holder.getSubject().setText("\n " + links.get(position).getSubject());
            holder.getImage().setImageResource(links.get(position).getImage());

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = links.get(position).getAddress();
                    v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            };

            holder.getImage().setOnClickListener(listener);
            holder.getSubject().setOnClickListener(listener);
        }

        @Override
        public int getItemCount() { return links.size(); }



        class LinkViewHolder extends RecyclerView.ViewHolder {

            private TextView subject;
            private ImageView image;

            public LinkViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.link_image);
                subject = itemView.findViewById(R.id.link_subject);
            }

            public TextView getSubject() { return subject; }
            public ImageView getImage() { return image; }

        }
    }
}

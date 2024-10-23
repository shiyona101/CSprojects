package com.example.phonebook;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<ContactViews>{

    private Context mainContext;
    private int adapterXML;
    List<ContactViews> contacts;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<ContactViews> objects) {
        super(context, resource, objects);
        mainContext = context;
        adapterXML = resource;
        contacts = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mainContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(adapterXML, parent, false);
        }

        ContactViews pos = contacts.get(position);

        ImageView image = view.findViewById(R.id.contact_picture);
        image.setImageResource(pos.getContactImageID());

        TextView name = view.findViewById(R.id.contact_name);
        name.setText(pos.getContactName());

        TextView number = view.findViewById(R.id.contact_number);
        number.setText(pos.getContactNumber());

        TextView delete = view.findViewById(R.id.delete_tv);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contacts.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;

    }

}

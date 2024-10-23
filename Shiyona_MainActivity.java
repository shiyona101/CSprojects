package com.example.phonebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<ContactViews> contactList;
    int position = -1;
    private ImageView image_land;
    private TextView name_land;
    private TextView number_land;
    private TextView bio_land;
    private String POS_KEY = "barbie";
    private String NUM_KEY = "lifeinthedreamhouse";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        contactList = new ArrayList<ContactViews>();

        contactList.add(new ContactViews(R.drawable.barbie, "Barbie", "264-386-2121", "Barbie is a fashion icon and has had over 135 careers in her life. Though a celebrity in Malibu, she is friendly, humble and good-natured."));
        contactList.add(new ContactViews(R.drawable.ken, "Ken", "893-265-3445", "Ken is Barbie's boyfriend and is always there when Barbie needs him. He is an inventor who makes gadgets for Barbie to use, though they usually end up malfunctioning."));
        contactList.add(new ContactViews(R.drawable.ryan, "Ryan", "653-276-8989", "Ryan is the twin brother of Raquelle. Ryan is also an aspiring wannabe musician, who is always trying to woo Ken or Barbie with his songs. He has a crush on Barbie."));
        contactList.add(new ContactViews(R.drawable.raquelle, "Raquelle", "143-099-1001", "Raquelle is Barbie's frenemy and has a crush on Ken, Barbie's boyfriend. She lives a very posh lifestyle in an attempt to outshine Barbie. She is portrayed as vain and arrogant and always attain what she wants."));
        contactList.add(new ContactViews(R.drawable.skipper, "Skipper", "765-469-6336", "Skipper is Barbie's teenage sister and lives with her in the Dreamhouse. She is a tech whiz who loves trying out the latest tech toys. She is competitive and lazy, but kind and thoughtful"));
        contactList.add(new ContactViews(R.drawable.stacey, "Stacey", "832-434-6666", "Stacie is Barbie's sporty, organized sister. She is the second oldest of Barbie's three younger sisters and lives with them in the Dreamhouse. Stacie is known for being hyperactive, honest, and always up for an adventure or mystery."));
        contactList.add(new ContactViews(R.drawable.chelsea, "Chelsea", "123-963-6678", "Chelsea is the youngest of the sisters and knows how to work her cuteness well. This adorable kid always seems to come up with the exact super-cute thing to say in order to win over her sisters and their friends."));
        contactList.add(new ContactViews(R.drawable.blissa, "Blissa", "135-446-7765", "Blissa is Barbie's pet cat and lives with her and her family in the Dreamhouse. Blissa is only nice to people sometimes, but she's always nice to Barbie. She is mostly seen with Chelsea."));
        contactList.add(new ContactViews(R.drawable.tawny, "Tawny", "102-457-3452", "Tawny is Barbie's pet Palamino horse and lives with Barbie in the Dreamhouse. Tawny is obsessed with her looks. "));
        contactList.add(new ContactViews(R.drawable.taffy, "Taffy", "309-344-3223", "Taffy is Barbie's puppy. She is a playful, sweet, and fun-loving dog. Taffy enjoys playing with frisbees, does not enjoy baths and is relatively well-behaved when she wants to be."));
        contactList.add(new ContactViews(R.drawable.theresa, "Theresa", "773-310-2004", "She is Barbie's best friend and is a mellow, live-and-let-live type of person. Teresa is a fashion icon who is always there for her friends, even if she is not on time."));
        contactList.add(new ContactViews(R.drawable.nikki, "Nikki", "724-349-2200", "Barbie's best friend and is also close friends with Teresa. Smart, sassy, and totally fun to be around, Nikki is a “what you see is what you get” kind of girl. For her, no adventure is too crazy."));

        if (savedInstanceState != null)
        {
            contactList = (ArrayList<ContactViews>) savedInstanceState.getSerializable(NUM_KEY);
            CustomAdapter customAdapter = new CustomAdapter(this, R.layout.adapter_layout, contactList);
            listView.setAdapter(customAdapter);
            position = savedInstanceState.getInt(POS_KEY);
            Log.d("MainActivity", "Restored contactList");
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            CustomAdapter customAdapter = new CustomAdapter(this, R.layout.adapter_layout, contactList);
            listView.setAdapter(customAdapter);

            if (position >= 0){
                ContactViews current = contactList.get(position);
                image_land = findViewById(R.id.contact_image_land);
                image_land.setImageResource(current.getContactImageID());

                bio_land = findViewById(R.id.contact_bio_land);
                bio_land.setText(current.getContactBio());

                number_land = findViewById(R.id.contact_number_land);
                number_land.setText(current.getContactNumber());

                name_land = findViewById(R.id.contact_name_land);
                name_land.setText(current.getContactName());
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    position = i;

                    if (i >= 0){
                        ContactViews current = contactList.get(position);
                        image_land = findViewById(R.id.contact_image_land);
                        image_land.setImageResource(current.getContactImageID());

                        bio_land = findViewById(R.id.contact_bio_land);
                        bio_land.setText(current.getContactBio());

                        number_land = findViewById(R.id.contact_number_land);
                        number_land.setText(current.getContactNumber());

                        name_land = findViewById(R.id.contact_name_land);
                        name_land.setText(current.getContactName());
                    }
                }
            });


        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            CustomAdapter customAdapter = new CustomAdapter(this, R.layout.adapter_layout, contactList);
            listView.setAdapter(customAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(MainActivity.this, "Hi! I am " + contactList.get(i).getContactName() + "!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(POS_KEY, position);
        outState.putSerializable(NUM_KEY, contactList);
        Log.d("MainActivity", "onSaveInstanceState called");
    }

}
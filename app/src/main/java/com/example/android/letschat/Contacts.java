package com.example.android.letschat;


import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Contacts extends Fragment {
    private ArrayList<Users> usersList = new ArrayList<>();
    private String  contactNumber;
    private RecyclerView myRecyclerView;
    private HashMap<String,String> contactsHashMap = new HashMap<>();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private MyRecyclerViewAdapter adapter;
    private DatabaseReference databaseReference;

    public Contacts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        myRecyclerView = (RecyclerView) rootView.findViewById(R.id.usersList);
        myRecyclerView.setHasFixedSize(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        new GetContactsTask().execute();
        Log.i("In Oncreate",getContext().toString());
        return rootView;
    }



    private class GetContactsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            getContacts();
           return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (Map.Entry<String, String> entry : contactsHashMap.entrySet()) {
                       String key=entry.getKey();
                    Log.i("Phone", key);
                        Log.i("CHECK", dataSnapshot.child(key).exists()+"");
                    if (dataSnapshot.child(key).exists()) {
                        Users user = new Users();
                        user.setName(entry.getValue());
                        Log.i("NAme",entry.getValue());
                        user.setPhone_no(key);
                        user.setStatus(dataSnapshot.child(key).child("status").getValue().toString());
                        user.setImage(dataSnapshot.child(key).child("image").getValue().toString());
                        user.setThumb_image(dataSnapshot.child(key).child("thumb_image").getValue().toString());
                        usersList.add(user);
                        adapter.notifyDataSetChanged();
                    }
                  }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            adapter = new MyRecyclerViewAdapter(getContext(),usersList);
            myRecyclerView.setAdapter(adapter);
        }


    }



    public void getContacts() {

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

        ContentResolver contentResolver = getContext().getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                final String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    
                    //This is to read multiple phone numbers associated with the same contact
                    Cursor phoneCursor = contentResolver.query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {
                        contactNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                           contactNumber = contactNumber.replaceAll("\\s", "");

                            contactNumber = contactNumber.replaceAll("-", "");

                            if (contactNumber.length() > 3 && contactNumber.contains("+91")) {
                                 contactNumber=contactNumber.substring(3);
                            }
                            if(contactNumber.contains("*")|| contactNumber.contains("#")){
                                continue;
                            }
                            contactNumber.replaceAll("/\\+91/", "");
                            contactsHashMap.put("+91"+contactNumber,name);


                    }

                    phoneCursor.close();

            }

        }

    }

   }

}

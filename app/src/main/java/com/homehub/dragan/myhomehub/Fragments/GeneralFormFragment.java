package com.homehub.dragan.myhomehub.Fragments;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.homehub.dragan.myhomehub.Activities.AccountSubActivity;
import com.homehub.dragan.myhomehub.Classes.firebase.FirebaseDatabaseHelper;
import com.homehub.dragan.myhomehub.Classes.model.General_Form_User;
import com.homehub.dragan.myhomehub.R;

import java.util.List;


public class GeneralFormFragment extends Fragment {

    public static GeneralFormFragment newInstance() { return new GeneralFormFragment(); }

    private Activity activity;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPhoneNum;
    private EditText inputBday;
    private EditText inputPostalCode;

    //storage for user key
    private String mainKey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //store activity
        activity = getActivity();

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_general_form, container, false);

        //get view id of all inputs
        inputFirstName = view.findViewById(R.id.input_first_name);
        inputLastName = view.findViewById(R.id.input_last_name);
        inputEmail = view.findViewById(R.id.input_email);
        inputPhoneNum = view.findViewById(R.id.input_phone_number);
        inputBday = view.findViewById(R.id.input_birthday);
        inputPostalCode = view.findViewById(R.id.input_postal_code);

        //get data from firebase (but also maintain connection)
        new FirebaseDatabaseHelper().readUsers(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<General_Form_User> users, List<String> keys) {
                view.findViewById(R.id.loading_general_form_info_pb).setVisibility(View.GONE);

                inputFirstName.setText(users.get(0).getFirst_name());
                inputLastName.setText(users.get(0).getLast_name());
                inputEmail.setText(users.get(0).getEmail());
                inputPhoneNum.setText(users.get(0).getPhone_num());
                inputBday.setText(users.get(0).getBirthday());
                inputPostalCode.setText(users.get(0).getPostal_code());

                //store value of current key
                mainKey = keys.get(0);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    //check command for on back

    //add bottom stuff to method
    //then add option/check

    /*@Override
    public void onBackPressed(){
        super.onBackPressed();

    }*/

    @Override
    public void onDestroy(){
        General_Form_User user = new General_Form_User();

        user.setFirst_name(inputFirstName.getText().toString());
        user.setLast_name(inputLastName.getText().toString());
        user.setEmail(inputEmail.getText().toString());
        user.setPhone_num(inputPhoneNum.getText().toString());
        user.setBirthday(inputBday.getText().toString());
        user.setPostal_code(inputPostalCode.getText().toString());


        //update firebase db
        //get data from firebase (but also maintain connection)
        new FirebaseDatabaseHelper().updateUsers(mainKey, user, new FirebaseDatabaseHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(List<General_Form_User> users, List<String> keys) {

                    }

                    @Override
                    public void DataIsInserted() {

                    }

                    @Override
                    public void DataIsUpdated() {
                        Toast.makeText(activity, "User info has been" +
                                " updated successfully", Toast.LENGTH_SHORT ).show();

                    }

                    @Override
                    public void DataIsDeleted() {

                    }
                });

        // Write a message to the database
        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue(firstName);
        myRef.setValue(lastName);
        myRef.setValue(email);
        myRef.setValue(phoneNum);
        myRef.setValue(bday);
        myRef.setValue(postalCode);*/


        super.onDestroy();
    }


}

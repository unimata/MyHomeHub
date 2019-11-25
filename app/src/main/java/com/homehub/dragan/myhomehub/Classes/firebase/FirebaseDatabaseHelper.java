package com.homehub.dragan.myhomehub.Classes.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.homehub.dragan.myhomehub.Classes.model.Device_Registration;
import com.homehub.dragan.myhomehub.Classes.model.General_Form_User;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;
    private DatabaseReference mReferenceDeviceRegs;
    private List<General_Form_User> users = new ArrayList<>();
    private List<Device_Registration> deviceRegistrations = new ArrayList<>();


    public interface UserDataStatus{
        void UserDataIsLoaded(List<General_Form_User> users, List<String> keys);
        void DataIsInserted();
        void UserDataIsUpdated();
        void DataIsDeleted();
    }

    public interface DeviceDataStatus{
        void DeviceDataIsLoaded(List<Device_Registration> deviceRegistrations, List<String> keys);
        void DeviceDataIsInserted();
        void DeviceDataIsUpdated();
        void DeviceDataIsDeleted();
    }
    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUsers = mDatabase.getReference("users");
        mReferenceDeviceRegs = mDatabase.getReference("devices");

    }

    //user-based methods -------------------

    public void readUsers(final UserDataStatus userDataStatus){
        mReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());

                    General_Form_User user = keyNode.getValue(General_Form_User.class);
                    users.add(user);

                }
                userDataStatus.UserDataIsLoaded(users, keys);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void updateUsers(String key, General_Form_User user, final UserDataStatus userDataStatus){
        mReferenceUsers.child(key).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        userDataStatus.UserDataIsUpdated();
                    }
                });
    }



    //device-based methods -------------------

    public void readDeviceRegs(final DeviceDataStatus deviceDataStatus){
        mReferenceDeviceRegs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deviceRegistrations.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());

                    Device_Registration deviceReg = keyNode.getValue(Device_Registration.class);
                    deviceRegistrations.add(deviceReg);

                }
                deviceDataStatus.DeviceDataIsLoaded(deviceRegistrations, keys);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void addDeviceReg(String key, Device_Registration deviceReg, final DeviceDataStatus deviceDataStatus){
        //String key = mReferenceDeviceRegs.push().getKey();
        mReferenceDeviceRegs.child(key).setValue(deviceReg)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deviceDataStatus.DeviceDataIsInserted();
                    }
                });
    }

    public void deleteDeviceReg(String key, final DeviceDataStatus deviceDataStatus){
        mReferenceDeviceRegs.child(key).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        deviceDataStatus.DeviceDataIsDeleted();
                    }
                });

    }

    //for updating devices
    /*public void updateUsers(String key, General_Form_User user, final DataStatus dataStatus){
        mReferenceUsers.child(key).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataStatus.DataIsUpdated();
                    }
                });
    }*/


}

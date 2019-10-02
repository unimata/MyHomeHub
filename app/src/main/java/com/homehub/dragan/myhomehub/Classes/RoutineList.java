package com.homehub.dragan.myhomehub.Classes;

import com.homehub.dragan.myhomehub.UI.Routine;

import java.util.ArrayList;

public class RoutineList {

    //most of these methods were copied from DeviceList class and altered
    private static final RoutineList list = new RoutineList();

    private ArrayList<Object> routines = new ArrayList<Object>();

    private RoutineList(){

        routines.add(new Routine("Bulb"));
        routines.add(new Routine("Bolbus"));
    }

    public static RoutineList getInstance(){
        return list;
    }

    public ArrayList<Object> getRoutines(){
        return routines;
    }
}

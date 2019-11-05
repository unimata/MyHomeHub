package com.homehub.dragan.myhomehub.Classes;

import com.homehub.dragan.myhomehub.UI.Routine;

import java.util.ArrayList;

public class RoutineList {

    //most of these methods were copied from DeviceList class and altered
    private static final RoutineList list = new RoutineList();

    public ArrayList<Object> routines = new ArrayList<Object>();

    public RoutineList(){

        setRoutines(routines);

        routines.add(new Routine("Bulb", "turn on", "sunset"));
        routines.add(new Routine("Bolbus","go away", "high noon"));
    }

    public static RoutineList getInstance(){ return list; }

    public ArrayList<Object> getRoutines(){ return routines; }

    public void setRoutines(ArrayList<Object> routines) { this.routines = routines; }
}

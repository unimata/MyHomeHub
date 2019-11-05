package com.homehub.dragan.myhomehub.Classes;

public class HAHelpMethods {
    private static HAHelpMethods ourInstance = new HAHelpMethods();

    public static HAHelpMethods getInstance() {
        return ourInstance;
    }

    private HAHelpMethods() {
    }

    public static String findStateType(String text){
        int dotLocation = text.toLowerCase().indexOf('.');
        if (dotLocation < 1) return text;

        final String type = text.substring(0,dotLocation);
        return type;
    }
}

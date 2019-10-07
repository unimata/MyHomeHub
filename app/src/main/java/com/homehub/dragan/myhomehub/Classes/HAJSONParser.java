package com.homehub.dragan.myhomehub.Classes;

import com.google.gson.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HAJSONParser {
    // not able to instantiate this class
    private HAJSONParser(){ }

    public static Object deserializeParse(JsonElement data){
        if(data.isJsonArray()) {
            JsonArray jsonArray = (JsonArray) data;
            List<Object> tmpList = new ArrayList<>();
            for (JsonElement element : jsonArray
            ) {
                tmpList.add(deserializeParse(element));
            }
            return tmpList;

        }else if (data.isJsonObject()){
            JsonObject jsonObject = (JsonObject) data;
            Set<String> keys = jsonObject.keySet();
            Map<Object, Object> tmpMap = new HashMap<>();
            for (String s: keys
            ) {
                JsonElement tmpElement = jsonObject.get(s);
                //tmpMap.put(getInt(s),deserializeParse(tmpElement));
                tmpMap.put(s,deserializeParse(tmpElement));
            }
            return tmpMap;

        }else if (data.isJsonPrimitive()){
            JsonPrimitive jsPrim = data.getAsJsonPrimitive();
            if(jsPrim.isBoolean()){
                return jsPrim.getAsBoolean();
            }else if(jsPrim.isNumber()) {
                return jsPrim.getAsInt();
            }else if(jsPrim.isString()){
                // Try converting to int
                try {
                    int i = Integer.parseInt(jsPrim.getAsString());
                    return i;
                } catch (java.lang.NumberFormatException e){
                    //e.printStackTrace();
                }
                // Try converting to long
                try {
                    long l = Long.parseLong(jsPrim.getAsString());
                    return l;
                } catch (java.lang.NumberFormatException e){
                    //e.printStackTrace();
                }

                // Try converting to float
                try {
                    float f = Float.parseFloat(jsPrim.getAsString());
                    return f;
                } catch (java.lang.NumberFormatException e){
                    //e.printStackTrace();
                }

                // Try converting to double
                try {
                    double d = Double.parseDouble(jsPrim.getAsString());
                    return d;
                } catch (java.lang.NumberFormatException e){
                    //e.printStackTrace();
                }


                // Try converting to date format 1
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssX");
                    Date parsedDate = dateFormat.parse(jsPrim.getAsString());
                    return parsedDate;

                } catch (IllegalArgumentException | ParseException e){
                    //e.printStackTrace();
                }

                // Try converting to date format 2
                try {
                    String oldDate = jsPrim.getAsString();
                    int findDot = oldDate.indexOf('.') -1;
                    if(findDot < 1 || oldDate.length() < findDot+7) { throw new IllegalArgumentException("not a good number"); }
                    String thisDate = oldDate.substring(0,findDot);
                    thisDate += oldDate.substring(findDot+7);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssX");
                    Date parsedDate = dateFormat.parse(thisDate);
                    return parsedDate;

                } catch (IllegalArgumentException | ParseException e){
                    //e.printStackTrace();
                }

                // Try converting to UUID v4
                try {
                    String oldString = jsPrim.getAsString();
                    String newUUID;
                    newUUID = oldString.substring(0,8);
                    newUUID += "-";
                    newUUID += oldString.substring(8,12);
                    newUUID += "-";
                    newUUID += oldString.substring(12,16);
                    newUUID += "-";
                    newUUID += oldString.substring(16,20);
                    newUUID += "-";
                    newUUID += oldString.substring(20);
                    UUID u = UUID.fromString(newUUID);
                    return u;
                } catch (java.lang.IllegalArgumentException | java.lang.StringIndexOutOfBoundsException e){
                    //e.printStackTrace();
                }


                // Just a string
                /*
                try{
                    int stringToInt = (int) getInt(jsPrim.getAsString());
                    return stringToInt;
                }catch (ClassCastException e){
                    // e.printStackTrace();
                }
                */
                return jsPrim.getAsString();
            }
        }
        return null;
    }

    public static JsonElement serializeParse(Object object){
        if(object == null) return new JsonPrimitive("null");


        HashMap<String, Object> testHashMap = null;
        List<Object> testList = null;
        try {
            testHashMap = (HashMap<String, Object>) object;
        } catch (ClassCastException e){

        }

        try {
            testList = (ArrayList<Object>)object;
        }catch (ClassCastException e){

        }

        if(null != testHashMap) {
            JsonObject jsObj = new JsonObject();
            for (Map.Entry<String, Object> obj : testHashMap.entrySet()
            ) {
                JsonElement jsEl = serializeParse(obj.getValue());

                jsObj.add(obj.getKey(),jsEl);

            }
            return jsObj;
        } else if(null != testList){
            JsonArray jsArray = new JsonArray();
            for (Object obj: testList
                 ) {
                JsonElement jsEl = serializeParse(obj);
                jsArray.add(jsEl);
            }
            return jsArray;
        }else {
            return new JsonPrimitive(object.toString());
            //System.out.println(object.toString());
        }

        //return null;

    }
}

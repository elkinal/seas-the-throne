package edu.cornell.jade.seasthethrone.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import com.badlogic.gdx.utils.Array;
import java.util.Map;
import java.util.HashMap;

public class JsonHandler {

    /**
     * Converts a JSON file to a HashMap.
     *
     * Currently all values are Objects
     *
     * @param fileName name of the JSON file in assets (should be like levels/filename.json).
     *
     * @return map the HashMap representation of the JSON file.
     * */
    public static HashMap<String, Object> jsonToMap(String fileName) {
        FileHandle fileHandle = Gdx.files.internal(fileName);
        String jsonString = fileHandle.readString();
        HashMap<String, Object> map = new HashMap<>();

        JsonValue jsonValue = new JsonReader().parse(jsonString);
        populateMap(map, jsonValue);

        return map;
    }

    /**
     * Takes a root JsonValue representing a map and fills in the map with the fields of the JsonValue.
     *
     * @param map the map to be filled in with values
     * @param jsonValue the root of the JSON containing the data to be sent to the map
     * */
    private static void populateMap(Map<String, Object> map, JsonValue jsonValue) {
        if (jsonValue.isObject()) {
            for (JsonValue child : jsonValue) {
                map.put(child.name(), convertJsonValue(child));
            }
        }
    }

    /**
     * Converts the value from a value:key pair in a JSON to a Java type.
     *
     * @param thisValue the value in the JSON to be converted
     *
     * @return the Java object representation of the JSON value
     * */
    private static Object convertJsonValue(JsonValue thisValue) {
        if (thisValue.isObject()) {
            Map<String, Object> objectMap = new HashMap<>();
            populateMap(objectMap, thisValue);

            return objectMap;
        } else if (thisValue.isArray()) {
            Array<Object> list = new Array<>();
            for (JsonValue child : thisValue) {
                list.add(convertJsonValue(child));
            }
            return list;
        } else {
            return thisValue.asString();
        }
    }

    /**
     * Finds the specified field in the specified map and returns its value. Assumes the value is an integer.
     *
     * @param map the JSON map containing the desired field
     * @param fieldName the name of the field in the map
     *
     * @return the integer value stored at the given field
     *
     * @throws Exception if no field with fieldName is found in the map
     * */
    public static Integer getInt(HashMap<String, Object> map, String fieldName) {
        try {
            return Integer.parseInt((String) map.get(fieldName));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Finds the specified field in the specified map and returns its value. Assumes the value is a float.
     *
     * @param map the JSON map containing the desired field
     * @param fieldName the name of the field in the map
     *
     * @return the integer value stored at the given field
     *
     * @throws Exception if no field with fieldName is found in the map
     * */
    public static float getFloat(HashMap<String, Object> map, String fieldName) {
        try {
            return Float.parseFloat((String) map.get(fieldName));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Finds the specified field in the specified map and returns its value. Assumes the value is a string.
     *
     * @param map the JSON map containing the desired field
     * @param fieldName the name of the field in the map
     *
     * @return the integer value stored at the given field
     *
     * @throws Exception if no field with fieldName is found in the map
     * */
    public static String getString(HashMap<String, Object> map, String fieldName) {
        try {
            return (String) map.get(fieldName);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Finds the specified field in the specified map and returns its value. Assumes the value is an array.
     *
     * @param map the JSON map containing the desired field
     * @param fieldName the name of the field in the map
     *
     * @return the integer value stored at the given field
     *
     * @throws Exception if no field with fieldName is found in the map
     * */
    public static Array<HashMap<String, Object>> getArray(HashMap<String, Object> map, String fieldName) {
        try {
            return (Array<HashMap<String, Object>>) map.get(fieldName);
        } catch (Exception e) {
            throw e;
        }
    }

    public static Object getProperty(HashMap<String, Object> map, String propName) {
        Array<HashMap<String, Object>> properties = getArray(map,"properties");
        if (properties == null) {
            throw new Error("Object has no custom properties");
        }
        for (HashMap<String, Object> prop : properties) {
            if (( (String)prop.get("name") ).equals(propName)) {
                return prop.get("value");
            }
        }
        throw new Error("No layer with name " + propName);
    }
}

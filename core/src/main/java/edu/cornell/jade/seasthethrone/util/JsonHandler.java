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
     * TODO: Implement handling for specific value types to prevent casting later
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
}

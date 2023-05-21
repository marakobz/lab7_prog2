package models;


import java.io.Serializable;

public enum Country implements Serializable {
    RUSSIA,
    UNITED_KINGDOM,
    USA,
    FRANCE,
    SPAIN;

    public static String nameList() {
        String nameList = "";
        for (Country nationality: values()) {
            nameList += nationality.name() + ", ";
        }
        return nameList.substring(0, nameList.length()-2);
    }
}

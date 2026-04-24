package lib;

import freemarker.template.SimpleDate;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
    public static  String getRandomEmail(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "learnqa" + timestamp + "@example.com";
    }
    public static Map<String,String> getRegistrationData(){
        Map<String,String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmail());
        data.put("password","123");
        data.put("username","learnqa");
        data.put("firstName","learnqa");
        data.put("lastName","learnqa");
        return data;
    }
    public static  Map<String,String> getRegistrationData(Map<String,String> nonDefaultValue){
        Map<String,String> defaultValues = DataGenerator.getRegistrationData();
        Map<String,String> userData = new HashMap<>();
        String[] keys = {"email","password","username","firstName","lastName"};
        for (String key : keys){
            if (nonDefaultValue.containsKey(key)){
                userData.put(key,nonDefaultValue.get(key));
            } else {
                userData.put(key, defaultValues.get(key));
            }
        }

        return userData;
    }
}

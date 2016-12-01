package com.pingnotes.tools;

/**
 * Created by shaobo on 10/28/16.
 */
public class ClssUtility {
    public String buildParamName(String typeName){
        return Character.toLowerCase(typeName.charAt(0)) + typeName.substring(1);
    }
}

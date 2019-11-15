package com.sleepy.tok.Utils;

public class StringManipulator {

        public static String addSpacesToUsername(String username){
            return username.replace(".", " ");
        }

        public static String removeSpacesFromUsername(String username){
            return username.replace(" ", ".");
        }

}

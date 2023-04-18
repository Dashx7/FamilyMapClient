package com.example.familymapclient.cache;

public class Settings {


    //The different settings possible
    public static boolean isSpouseLines = true;
    public static boolean isFamilyTreeLines = true;
    public static boolean isLifeStoryLines = true;
    public static boolean isFilterMale = true;
    public static boolean isFilterFemale = true;
    public static boolean isFilterByMomsSide = true;
    public static boolean isFilterByDadsSide = true;
    public static void Reset(){
        isSpouseLines = true;
        isFamilyTreeLines = true;
        isLifeStoryLines = true;
        isFilterMale = true;
        isFilterFemale = true;
        isFilterByMomsSide = true;
        isFilterByDadsSide = true;
    }
}

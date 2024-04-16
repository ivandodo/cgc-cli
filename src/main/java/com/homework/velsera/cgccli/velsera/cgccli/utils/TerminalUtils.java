package com.homework.velsera.cgccli.velsera.cgccli.utils;

public class TerminalUtils {
    public static void resetLastTerminalLine(){
        System.out.print("\33[2K\r"); 
        System.out.flush(); 
    }
}

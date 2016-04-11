package com.example.yassine.sunlamp;

import android.graphics.Color;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by YassIne on 13/08/2015.
 */
public class Utils {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    final static String LOG = "Utils";
    public static enum rgb {RED, GREEN, BLUE};

    /**
     * Trasforma un array di byte in una stringa in esadecimale
     * @param bArray
     * @return
     */
    public static String byteArrayToHexString(byte [] bArray){
        char[] hexChars = new char[bArray.length * 2];
        for(int j = 0; j < bArray.length; j++){
            int v = bArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
    return new String(hexChars);
    }

    /*
    Trasforma una stringa in un arrayList di strighe separate
     */
    public static ArrayList<String> HexStringToArray(String hexString){
        ArrayList<String> hexArray= new ArrayList<>();
        char [] charArray = hexString.toCharArray();
        String data = "#";
        for(int i = 0; i < charArray.length; i++){
            if(charArray[i] != ','){
                data += charArray[i];
            }
            else{
                hexArray.add(data);
                data = "#";
            }
        }
        return hexArray;
    }

    public static int [] fromHexStringToColorArray(String hexString){
        ArrayList<Integer> colorArrayList = new ArrayList<>();
        char [] hexArray = hexString.toCharArray();
        int actual = 0;

        //0 rosso - red
        //1 verde - green
        //2 blue - blue 8===D

        String sR = "",
                sG = "",
                sB = "";

        for(char c: hexArray){
            if(c == ','){
                actual++;
            }
            else if(c == ';'){
                int iR = Integer.parseInt(sR , 16);
                int iG = Integer.parseInt(sG , 16);
                int iB = Integer.parseInt(sB , 16);
                colorArrayList.add(Color.rgb(iR,iG,iB));
                sR = "";
                sG = "";
                sB = "";
                actual = 0;
            }
            else{
                if(actual == 0)
                    sR += c;
                else if (actual == 1)
                    sG += c;
                else
                    sB += c;
            }
        }

        return convertIntegers(colorArrayList);
    }

    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }
    /*
   Trasforma un arrayList di string in un Array di colori
     */
    public static int [] fromHexArrayListToColorArray(ArrayList<String> hexArrayList){
        int [] color = new int[hexArrayList.size()+1];
        for(int i = 0; i < hexArrayList.size(); i++){
            color[i] = Color.parseColor(hexArrayList.get(i));
        }
        return color;
    }

    public static String getActualDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}

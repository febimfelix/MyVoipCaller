package com.febi.myvoipcaller;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Patterns;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;

/**
 * Created by Febi.M.Felix on 7/11/16.
 */

public class AndroidCommonUtils {
    //Checks whether email is in valid format or not
    public final static boolean isValidEmail(CharSequence target) {
        if(TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    //Get byte[] of an image bitmap to store in sqlite
    public static byte[] getBlobFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    //Get current date in a specific format
    public static String getTimeStampValue(){
        SimpleDateFormat sdf    =    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        String date             =    sdf.format(new Date());
        return date;
    }

    //Get date string from input date string
    public static String getDateFromStringDate(String time) {
        try {
            SimpleDateFormat curFormat	=	new SimpleDateFormat("MM-dd-yyyy HH:mm:ss",
                    Locale.getDefault());
            Date dateObj				=	curFormat.parse(time);
            SimpleDateFormat postFormat	=	new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                    Locale.getDefault());
            time						=	postFormat.format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }
        return time;
    }

    //Return US formatted phone number
    public static String returnUSFormattedPhoneNumber(String unformatted){
        String pre	=	"";
        if(unformatted == null || unformatted.length() == 0)
            return unformatted;
        unformatted	=	unformatted.toString().replace("(", "").replace(")", "").replace(
                "-", "").replace(" ", "");

        if(unformatted.charAt(0) == '1'){
            pre		=	"1";
            unformatted	=	unformatted.length() > 1 ? unformatted.substring(1) : "";
        }

        if(unformatted.length() > 10)
            return pre+unformatted; // no need to format.

        if(unformatted.length() > 6){
            return pre + "(" + unformatted.substring(0, 3) + ")" + unformatted.substring(3, 6)
                    + "-" + unformatted.substring(6);
        }

        if(unformatted.length() > 3){
            return pre + "(" + unformatted.substring(0, 3) + ")" + unformatted.substring(3);
        }
        return pre+unformatted;
    }

    //Validate IP
    public static boolean validateIP(final String ip){
        Matcher matcher = Patterns.IP_ADDRESS.matcher(ip);
        return matcher.matches();
    }


}

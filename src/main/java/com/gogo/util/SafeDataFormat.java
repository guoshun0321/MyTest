package com.gogo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 80107436 on 2015-05-11.
 */
public class SafeDataFormat {

    public static String format(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

}

package com.livenessrnexample.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    /**
     * format date from format: inputFormat to outputFormat
     * @param inputDate
     * @param inputFormatStr must be valid format
     * @param outputFormatStr must be valid format
     * @return
     */
    public static String formatDate(String inputDate, String inputFormatStr, String outputFormatStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputFormatStr);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputFormatStr);
        String outputDate = "";
        try {
            Date date = inputFormat.parse(inputDate);
            outputDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            outputDate = "";
        }
        return outputDate;
    }

    public static Date fromString(String inputDate, String inputFormatStr) {
        Date result;
        SimpleDateFormat dateFormat = new SimpleDateFormat(inputFormatStr);
        try {
            result = dateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
            result = new Date();
        }
        return result;
    }
}

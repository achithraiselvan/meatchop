package com.meatchop.utils;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TMCUtil {

    private static TMCUtil tmcutilOne = null;

    static {
        tmcutilOne = new TMCUtil();
    }

    private TMCUtil() {
    }

    public static TMCUtil getInstance() {
        return tmcutilOne;
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
        String currentTime = sdf.format(new Date());
        currentTime = currentTime.replace(".", "");
        return currentTime;
    }

    public static long getCurrentTimeInLong() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
            String currentTime = sdf.format(new Date());
            currentTime = currentTime.replace(".", "");
            Date date = sdf.parse(currentTime);
            return date.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static String getCurrentDate() {
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);

            String monthstring = getMonthString(month);
            String datestring = getCalendarDayOfWeek(dayofweek) + ", " + day + " " +
                    monthstring + " " + year;
            return datestring;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getCurrentDateNew() {
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            String monthstr = "" + month;
            if (month < 10) {
                monthstr = "0" + month;
            }
            String daystr = "" + day;
            if (day < 10) {
                daystr = "0" + day;
            }

            String datestring = year + "-" + monthstr + "-" + daystr;
            return datestring;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getTomorrowDateNew() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);

            String monthstr = "" + month;
            if (month < 10) {
                monthstr = "0" + month;
            }
            String daystr = "" + day;
            if (day < 10) {
                daystr = "0" + day;
            }

            String datestring = year + "-" + monthstr + "-" + daystr;
            return datestring;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public static String getTimeAfterMinuteAddition(String datetime, String minutes) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Date date = sdf.parse(datetime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int mins = Integer.parseInt(minutes);
            calendar.add(Calendar.MINUTE, mins);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String hourstr = ""; String minutestr = "";
            if (hour < 10) {
                hourstr = "0" + hour;
            } else {
                hourstr = "" + hour;
            }
            if (minute < 10) {
                minutestr = "0" + minute;
            } else {
                minutestr = "" + minute;
            }


            String timestring = hourstr + ":" + minutestr;
            return timestring;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static String getTomorrowDate() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);

            String monthstring = getMonthString(month);
            String datestring = getCalendarDayOfWeek(dayofweek) + ", " + day + " " +
                    monthstring + " " + year;
            return datestring;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Date getDateFromTime(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date datetime = sdf.parse(time);
            return datetime;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Date getCurrentTimeForDeliverySlot() {
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            // int am = calendar.get(Calendar.AM);
            int am = calendar.get(Calendar.AM);
            int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);

            String amstr = "";
            if (am == Calendar.PM) {
                amstr = "PM";
            } else if (am == Calendar.AM) {
                amstr = "AM";
            }

            String monthstring = getMonthString(month);
            String datestring = getCalendarDayOfWeek(dayofweek) + ", " + day + " " +
                    monthstring + " " + year + " " + hour + ":" + minute;
            String timestring = hour + ":" + minute;
            // Log.d("DVUtil", "datestring "+datestring);
         // SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
         // return sdf.parse(datestring);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.parse(timestring);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static String getMonthString(int value) {
        if (value == 0) {
            return "Jan";
        } else if (value == 1) {
            return "Feb";
        } else if (value ==2) {
            return "Mar";
        } else if (value ==3) {
            return "Apr";
        } else if (value ==4) {
            return "May";
        } else if (value ==5) {
            return "Jun";
        } else if (value ==6) {
            return "Jul";
        } else if (value ==7) {
            return "Aug";
        } else if (value ==8) {
            return "Sep";
        } else if (value ==9) {
            return "Oct";
        } else if (value ==10) {
            return "Nov";
        } else if (value ==11) {
            return "Dec";
        }
        return "";
    }

    private static String getCalendarDayOfWeek(int dayofweek) {
        if (dayofweek == 1) {
            return "Sun";
        } else if (dayofweek == 2) {
            return "Mon";
        } else if (dayofweek == 3) {
            return "Tue";
        } else if (dayofweek == 4) {
            return "Wed";
        } else if (dayofweek == 5) {
            return "Thu";
        } else if (dayofweek == 6) {
            return "Fri";
        } else if (dayofweek == 7) {
            return "Sat";
        }
        return "";
    }

    public long getTimeInLong(String datetime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            Date date = sdf.parse(datetime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getDifferenceTime(String datetime1, String datetime2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.US);
            Date date1 = sdf.parse(datetime1);
            Date date2 = sdf.parse(datetime2);
            long difftime = date2.getTime() - date1.getTime();
            return difftime;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }


}

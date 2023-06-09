package lct.feedbacksrv.resource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static lct.feedbacksrv.resource.Utils.truncate;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public class RandomGenerators {
    public static final Random r = new Random();
    private static final Double moscowLongMax = 37.967778;
    private static final Double moscowLongMin = 36.80325;
    private static final Double moscowLatMax = 56.021389;
    private static final Double moscowLatMin = 55.143833;

    public static Double getRandMoscowLatitude(){
        return truncate(BigDecimal.valueOf(getRandoubleInRange(moscowLatMin, moscowLatMax))
                .setScale(6, RoundingMode.HALF_EVEN).doubleValue(),6);
    }

    public static Double getRandMoscowLongitude(){
        return truncate(BigDecimal.valueOf(getRandoubleInRange(moscowLongMin, moscowLongMax))
                .setScale(6, RoundingMode.HALF_EVEN).doubleValue(),6);
    }

    public static String getRandomIp(){
        return String.format("%d.%d.%d.%d",
                r.nextInt(256), r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }

    public static Date getRandDate(){
        int year = getRandIntInRange(2015, 2023);
        int dayOfYear = r.nextInt(364) + 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        return calendar.getTime();
    }

    public static int getRandIntInRange(int min, int max){
        int d = Math.max(max - min, 1);
        return min + r.nextInt(d);
    }

    public static double getRandoubleInRange(double min, double max){
        return min + (max - min) * r.nextDouble();
    }
}

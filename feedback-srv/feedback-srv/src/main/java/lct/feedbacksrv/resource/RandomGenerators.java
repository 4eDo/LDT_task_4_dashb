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

    public static Double getRandMoscowLatitude(){
        return truncate(BigDecimal.valueOf(55.143833 + (56.021389 - 55.143833) * r.nextDouble())
                .setScale(6, RoundingMode.HALF_EVEN).doubleValue(),6);
    }

    public static Double getRandMoscowLongitude(){
        return truncate(BigDecimal.valueOf(36.80325 + (37.967778 - 36.80325) * r.nextDouble())
                .setScale(6, RoundingMode.HALF_EVEN).doubleValue(), 6);
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
        return min + r.nextInt(max - min);
    }
}

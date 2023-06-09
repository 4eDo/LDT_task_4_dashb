package lct.feedbacksrv.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import lct.feedbacksrv.csvTemplates.PostamatTemplate;
import lct.feedbacksrv.domain.Postamat;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Utils
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Slf4j
public class Utils {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static Postamat templateToPostamat(PostamatTemplate template) {
        Postamat.PostamatBuilder p = Postamat.builder();
        if(template.hasAddress()) p.address(template.getAddress());
        if(template.hasPostIndex()) p.postIndex(template.getPostIndex());
        if(template.hasIpAddress()) p.ipAddress(template.getIpAddress());
        if(template.hasLatitude()) p.latitude(template.getLatitude());
        if(template.hasLongitude()) p.longitude(template.getLongitude());
        if(template.hasSetupDate()) p.setupDate(template.getSetupDate());
        if(template.hasSoftwareVersion()) p.softwareVersion(template.getSoftwareVersion());
        if(template.hasComment()) p.comment(template.getComment());
        if(template.hasType()) p.comment(template.getType());
        if(template.hasLocation()) p.comment(template.getLocation());
        return p.build();
    }

    public static Double roundStringNumber(String numberString){
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        Double d = Double.valueOf(numberString);
        String result = df.format(d);
        try {
            return Double.valueOf(result);
        } catch (Exception e) {
            return result.contains(",") ? Double.valueOf(result.replace(",", ".")) :  Double.valueOf(result.replace(".", ","));
        }
    }

    public static Date stringToDate(String stringDate) {
        Date d = null;
        try{
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = df.parse(stringDate);
        } catch (ParseException pe) {
            try{
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                d = df.parse(stringDate.replace("T", " "));
            } catch (ParseException pe2) {
                try{
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    stringDate = stringDate.substring(0, 18);
                    d = df.parse(stringDate.replace("T", " "));
                } catch (ParseException pe3) {
                    log.error("can't parse date {}", stringDate, pe3);
                }
            }
        }
        return d;
    }

    public static double truncate(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = (long) value;
        return (double) tmp / factor;
    }

    public static Double roundTail(double value, int tail){
        return truncate(BigDecimal.valueOf(value)
                .setScale(tail, RoundingMode.HALF_EVEN).doubleValue(),tail);
    }
}

package lct.feedbacksrv.csvTemplates;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

import static lct.feedbacksrv.resource.Utils.stringToDate;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@JsonPropertyOrder({ "address", "postIndex", "ipAddress", "latitude",
        "longitude", "setupDate", "latitude", "softwareVersion", "comment", "type", "location" })
public class PostamatTemplate implements Serializable {
    private String address;
    private String postIndex;
    private String ipAddress;
    private Double latitude;
    private Double longitude;
    private Date setupDate;
    private String softwareVersion;
    private String comment;
    private String type;
    private String location;

    public void setSetupDate(String setupDate) {
        try{
            this.setupDate = stringToDate(setupDate);
        } catch (Exception e) {
            log.error("Can't parse date: {}", setupDate, e);
        }
    }

    public boolean hasAddress(){return StringUtils.isNotBlank(address);}
    public boolean hasPostIndex(){return StringUtils.isNotBlank(postIndex);}
    public boolean hasIpAddress(){return StringUtils.isNotBlank(ipAddress);}
    public boolean hasLatitude(){return latitude != null;}
    public boolean hasLongitude(){return longitude != null;}
    public boolean hasSetupDate(){return setupDate != null;}
    public boolean hasSoftwareVersion(){return StringUtils.isNotBlank(softwareVersion);}
    public boolean hasComment(){return StringUtils.isNotBlank(comment);}
    public boolean hasType(){return StringUtils.isNotBlank(type);}
    public boolean hasLocation(){return StringUtils.isNotBlank(location);}
}

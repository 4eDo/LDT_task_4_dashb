package lct.feedbacksrv.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * TODO: Add class description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="postamats")
public class Postamat implements Serializable {
    @Id
    private String id;
    @Column(name="address")
    private String address;
    @Column(name="post_index")
    private String postIndex;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name="latitude")
    private Double latitude;
    @Column(name="longitude")
    private Double longitude;
    @Column(name="setup_date")
    private Date setupDate;
    @Column(name="software_version")
    private String softwareVersion;
    @Column(name="comment")
    private String comment;
    @Column(name="type")
    private String type;
    @Column(name="location")
    private String location;
    @Column(name="is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

}

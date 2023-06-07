package lct.feedbacksrv.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
@Table(name="messages")
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private Long id;

    @Column(name="username")
    private String username;

    @Column(name = "create_date")
    @ApiModelProperty(hidden = true)
    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "postamat", referencedColumnName = "id")
    private Postamat postamat;

    @Column(name="order_id")
    private String orderId;

    @Column(name="message")
    private String message;

    @Column(name="stars")
    private Integer stars;

    @ManyToOne
    @JoinColumn(name = "category", referencedColumnName = "id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "partner", referencedColumnName = "name")
    private Partner partner;

    @Column(name = "tone")
    private Float tone;

    @Column(name="status")
    private String status;

    @Column(name="tickets")
    private String tickets;

    public List<String> getTicketsArr(){
        return StringUtils.isNotBlank(tickets) ? List.of(tickets.split("-")) : Collections.emptyList();
    }

    public void setTickets(List<String> tickets) {
        this.tickets = String.join("-", tickets);
    }

    public void addTicket(String ticket) {
        List<String> temp = new ArrayList<>();
        temp.addAll(getTicketsArr());
        temp.add(ticket);
        setTickets(temp);
    }

    public Boolean hasTone() { return tone != null;}
}

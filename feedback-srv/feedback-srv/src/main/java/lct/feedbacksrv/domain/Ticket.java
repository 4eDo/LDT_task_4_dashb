package lct.feedbacksrv.domain;

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
@Table(name="tickets")
public class Ticket implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "category", referencedColumnName = "id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "partner", referencedColumnName = "name")
    private Partner partner;
    @Column(name="messages")
    private String messages;
    @Column(name="create_date")
    private Date createDate;
    @Column(name="comment")
    private String comment;
    @Column(name="is_closed")
    @Builder.Default
    private Boolean isClosed = false;
    @ManyToOne
    @JoinColumn(name = "subcat", referencedColumnName = "id")
    private Category subcat;

    public List<String> getMessagesArr(){
        return StringUtils.isNotBlank(messages) ? List.of(messages.split("-")) : Collections.emptyList();
    }

    public void setMessages(List<String> messages) {
        this.messages = String.join("-", messages);
    }

    public void addMessage(String messages) {
        List<String> temp = new ArrayList<>();
        temp.addAll(getMessagesArr());
        temp.add(messages);
        setMessages(temp);
    }
}

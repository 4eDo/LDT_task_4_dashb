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
    private Long messages;
    @Column(name="create_date")
    private Date createDate;
    @Column(name="comment")
    private String comment;
    @Column(name="is_closed")
    @Builder.Default
    private Boolean isClosed = false;
}

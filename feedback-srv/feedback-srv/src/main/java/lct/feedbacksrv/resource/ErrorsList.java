package lct.feedbacksrv.resource;

import lombok.Getter;

/**
 * TODO: Add enumerator description
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
@Getter
public enum ErrorsList {
    SERVICE_NOT_AVAILABLE("Service not available"),

    POSTAMAT_NOT_FOUND("Postamat not found"),

    PARTNER_NOT_FOUND("Partner not found"),

    TICKET_NOT_FOUND ("Ticket not found") ;
    private final String description;

    ErrorsList(String description) {
        this.description = description;
    }
}

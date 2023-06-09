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
    ERROR_ON_PARSE_FILE("Error on parse file"),
    EMPTY_FILE("Empty file"),

    POSTAMAT_NOT_FOUND("Postamat not found"),

    PARTNER_NOT_FOUND("Partner not found"),

    TICKET_NOT_FOUND ("Ticket not found") ;
    private final String description;

    ErrorsList(String description) {
        this.description = description;
    }
}

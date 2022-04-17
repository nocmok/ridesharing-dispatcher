package com.nocmok.orp.orp_solver.kafka.orp_input.validator;

import com.nocmok.orp.kafka.orp_input.RequestConfirmationMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AssignRequestValidator {

    /**
     * @return list of validation errors.
     */
    public List<String> validateAssignRequest(RequestConfirmationMessage assignRequestMessage) {
        var errors = new ArrayList<String>();

        validateReservationIdNotNull(assignRequestMessage).ifPresent(errors::add);
        validateSessionIdNotNull(assignRequestMessage).ifPresent(errors::add);
        validateServiceRequestIdNotNull(assignRequestMessage).ifPresent(errors::add);
        validateReservationIdFormat(assignRequestMessage).ifPresent(errors::add);
        validateSessionIdFormat(assignRequestMessage).ifPresent(errors::add);
        validateServiceRequestIdFormat(assignRequestMessage).ifPresent(errors::add);

        return errors;
    }

    private Optional<String> validateReservationIdNotNull(RequestConfirmationMessage assignRequestMessage) {
        return assignRequestMessage.getReservationId() == null ? Optional.of("reservation id should not be null") : Optional.empty();
    }

    private Optional<String> validateSessionIdNotNull(RequestConfirmationMessage assignRequestMessage) {
        return assignRequestMessage.getSessionId() == null ? Optional.of("session id should not be null") : Optional.empty();
    }

    private Optional<String> validateServiceRequestIdNotNull(RequestConfirmationMessage assignRequestMessage) {
        return assignRequestMessage.getServiceRequestId() == null ? Optional.of("service request id should not be null") : Optional.empty();
    }

    private Optional<String> validateReservationIdFormat(RequestConfirmationMessage assignRequestMessage) {
        if (assignRequestMessage.getReservationId() == null) {
            return Optional.empty();
        }
        return assignRequestMessage.getReservationId().matches("[a-zA-Z0-9]+") ? Optional.empty() : Optional.of("invalid reservation id format");
    }

    private Optional<String> validateSessionIdFormat(RequestConfirmationMessage assignRequestMessage) {
        if (assignRequestMessage.getSessionId() == null) {
            return Optional.empty();
        }
        return assignRequestMessage.getSessionId().matches("[a-zA-Z0-9]+") ? Optional.empty() : Optional.of("invalid session id format");

    }

    private Optional<String> validateServiceRequestIdFormat(RequestConfirmationMessage assignRequestMessage) {
        if (assignRequestMessage.getServiceRequestId() == null) {
            return Optional.empty();
        }
        return assignRequestMessage.getServiceRequestId().matches("[a-zA-Z0-9]+") ? Optional.empty() : Optional.of("invalid service request id format");
    }
}

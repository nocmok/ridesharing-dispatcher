package com.nocmok.orp.orp_solver.kafka.orp_input.validator;

import com.nocmok.orp.kafka.orp_input.ServiceRequestMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ServiceRequestValidator {

    /**
     * @return list of validation errors
     */
    public List<String> validateServiceRequest(ServiceRequestMessage serviceRequestMessage) {
        var errors = new ArrayList<String>();

        validateRequestIdNotNull(serviceRequestMessage).ifPresent(errors::add);
        validatePickupNodeIdNotNull(serviceRequestMessage).ifPresent(errors::add);
        validatePickupLatitudeNotNull(serviceRequestMessage).ifPresent(errors::add);
        validatePickupLongitudeNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateDropoffNodeIdNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateDropoffLatitudeNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateDropoffLongitudeNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateRequestedAtNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateDetourConstraintNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateMaxPickupDelayNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateLoadNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateMaxPickupDelaySecondPositive(serviceRequestMessage).ifPresent(errors::add);
        validateRequestIdFormat(serviceRequestMessage).ifPresent(errors::add);

        return errors;
    }

    private Optional<String> validateRequestIdNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getRequestId() == null ? Optional.of("request id should not be null") : Optional.empty();
    }

    private Optional<String> validatePickupNodeIdNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getPickupNodeId() == null ? Optional.of("origin node id should not be null") : Optional.empty();
    }

    private Optional<String> validatePickupLatitudeNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getPickupLat() == null ? Optional.of("origin latitude should not be null") : Optional.empty();
    }

    private Optional<String> validatePickupLongitudeNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getPickupLon() == null ? Optional.of("origin longitude should not be null") : Optional.empty();
    }

    private Optional<String> validateDropoffNodeIdNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getDropoffNodeId() == null ? Optional.of("destination node id should not be null") : Optional.empty();
    }

    private Optional<String> validateDropoffLatitudeNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getDropoffLat() == null ? Optional.of("destination latitude should not be null") : Optional.empty();
    }

    private Optional<String> validateDropoffLongitudeNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getDropoffLon() == null ? Optional.of("destination longitude should not be null") : Optional.empty();
    }

    private Optional<String> validateRequestedAtNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getRequestedAt() == null ? Optional.of("request time should not be null") : Optional.empty();
    }

    private Optional<String> validateDetourConstraintNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getDetourConstraint() == null ? Optional.of("detour constraint should not be null") : Optional.empty();
    }

    private Optional<String> validateMaxPickupDelayNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getMaxPickupDelaySeconds() == null ? Optional.of("maximum pickup delay should not be null") : Optional.empty();
    }

    private Optional<String> validateLoadNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getLoad() == null ? Optional.of("request load should not be null") : Optional.empty();
    }

    private Optional<String> validateMaxPickupDelaySecondPositive(ServiceRequestMessage serviceRequestMessage) {
        if (serviceRequestMessage.getMaxPickupDelaySeconds() == null) {
            return Optional.empty();
        }
        return serviceRequestMessage.getMaxPickupDelaySeconds() < 0 ? Optional.of("maximum pickup delay should not be negative") : Optional.empty();
    }

    private Optional<String> validateRequestIdFormat(ServiceRequestMessage serviceRequestMessage) {
        if (serviceRequestMessage.getRequestId() == null) {
            return Optional.empty();
        }
        return serviceRequestMessage.getRequestId().matches("[a-zA-Z0-9]+") ? Optional.empty() : Optional.of("invalid request id");
    }
}

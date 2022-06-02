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
        validateRecordedOriginLatitudeNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateRecordedOriginLongitudeNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateRecordedDestinationLatitudeNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateRecordedDestinationLongitudeNotNull(serviceRequestMessage).ifPresent(errors::add);
        validatePickupRoadSegmentStartNodeIdNotNull(serviceRequestMessage).ifPresent(errors::add);
        validatePickupRoadSegmentEndNodeIdNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateDropOffRoadSegmentStartNodeIdNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateDropOffRoadSegmentEndNodeIdNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateRequestedAtNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateDetourConstraintNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateMaxPickupDelayNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateLoadNotNull(serviceRequestMessage).ifPresent(errors::add);
        validateMaxPickupDelaySecondPositive(serviceRequestMessage).ifPresent(errors::add);
        validateRequestIdFormat(serviceRequestMessage).ifPresent(errors::add);

        return errors;
    }

    private Optional<String> validateDropOffRoadSegmentEndNodeIdNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getDropOffRoadSegmentEndNodeId() == null ? Optional.of("drop off road segment end node id should not be null") :
                Optional.empty();
    }

    private Optional<String> validateDropOffRoadSegmentStartNodeIdNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getDropOffRoadSegmentStartNodeId() == null ? Optional.of("drop off road segment start node id should not be null") :
                Optional.empty();
    }

    private Optional<String> validatePickupRoadSegmentEndNodeIdNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getPickupRoadSegmentEndNodeId() == null ? Optional.of("pick up road segment end node id should not be null") :
                Optional.empty();
    }

    private Optional<String> validatePickupRoadSegmentStartNodeIdNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getPickupRoadSegmentEndNodeId() == null ? Optional.of("pick up road segment start node id should not be null") :
                Optional.empty();
    }

    private Optional<String> validateRecordedDestinationLongitudeNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getRecordedDestinationLongitude() == null ? Optional.of("recorded destination longitude should not be null") :
                Optional.empty();
    }

    private Optional<String> validateRecordedDestinationLatitudeNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getRecordedDestinationLatitude() == null ? Optional.of("recorded destination latitude should not be null") :
                Optional.empty();
    }

    private Optional<String> validateRecordedOriginLongitudeNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getRecordedOriginLongitude() == null ? Optional.of("recorded origin longitude should not be null") :
                Optional.empty();
    }

    private Optional<String> validateRecordedOriginLatitudeNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getRecordedOriginLatitude() == null ? Optional.of("recorded origin latitude should not be null") :
                Optional.empty();
    }

    private Optional<String> validateRequestIdNotNull(ServiceRequestMessage serviceRequestMessage) {
        return serviceRequestMessage.getRequestId() == null ? Optional.of("request id should not be null") : Optional.empty();
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

package com.nocmok.orp.orp_solver.service.request_management;

import com.nocmok.orp.orp_solver.service.request_execution.OrderStatus;
import com.nocmok.orp.orp_solver.service.request_management.mapper.ServiceRequestMapper;
import com.nocmok.orp.orp_solver.storage.request_management.ServiceRequestStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceRequestStorageServiceImpl implements ServiceRequestStorageService {

    private final ServiceRequestStorage serviceRequestStorage;

    private final ServiceRequestMapper serviceRequestMapper;

    @Autowired
    public ServiceRequestStorageServiceImpl(ServiceRequestStorage serviceRequestStorage,
                                            @Qualifier("com.nocmok.orp.orp_solver.service.request_management.mapper.ServiceRequestMapper")
                                                    ServiceRequestMapper serviceRequestMapper) {
        this.serviceRequestStorage = serviceRequestStorage;
        this.serviceRequestMapper = serviceRequestMapper;
    }

    @Override
    public Optional<ServiceRequestStorageService.ServiceRequestDto> getRequestById(String id) {
        return serviceRequestStorage.getRequestById(id).map(serviceRequestMapper::mapStorageDtoToServiceDto);
    }

    @Override
    public void storeRequest(ServiceRequestStorageService.ServiceRequestDto request) {
        serviceRequestStorage.insertRequest(serviceRequestMapper.mapServiceDtoToStorageDto(request));
    }

    @Override public Optional<ServiceRequestStorageService.ServiceRequestDto> getRequestByIdForUpdate(String id) {
        return serviceRequestStorage.getRequestByIdForUpdate(id).map(serviceRequestMapper::mapStorageDtoToServiceDto);
    }

    @Override public void updateRequestStatus(String requestId, OrderStatus status) {
        serviceRequestStorage.updateRequestStatus(requestId, status);
    }

    @Override public void updateServingSessionId(String requestId, String sessionId) {
        serviceRequestStorage.updateServingSessionId(requestId, sessionId);
    }
}

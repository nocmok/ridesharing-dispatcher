package com.nocmok.orp.orp_solver.service.request_management;

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
    public Optional<ServiceRequestDto> getRequestById(String id) {
        return serviceRequestStorage.getRequestById(id).map(serviceRequestMapper::mapStorageDtoToServiceDto);
    }

    @Override
    public void storeRequest(ServiceRequestDto request) {
        serviceRequestStorage.insertRequest(serviceRequestMapper.mapServiceDtoToStorageDto(request));
    }
}

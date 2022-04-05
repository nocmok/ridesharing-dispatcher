package com.nocmok.orp.notifier.service;

import com.nocmok.orp.notifier.service.dto.VehicleGPSMessageServiceDto;

public interface VehicleGPSStreamingService {

    void sendGPS(VehicleGPSMessageServiceDto gpsMessageDto);
}

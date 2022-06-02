package com.nocmok.orp.notifier.service.telemetry;

public interface VehicleGPSStreamingService {

    void sendGPS(VehicleGPSMessageServiceDto gpsMessageDto);
}

export class MapObjectPositionUpdater {

    constructor(mapObject) {
        this.mapObject = mapObject
    }

    // TODO Сделать плавную анимацию
    handleTelemetry(message) {
        let telemetry = JSON.parse(message.body)
        this.mapObject.setCoordinates(telemetry.latitude, telemetry.longitude)
    }
}
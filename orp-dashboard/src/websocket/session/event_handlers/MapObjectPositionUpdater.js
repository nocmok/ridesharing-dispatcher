import {AnimationController} from "./AnimationController";

export class MapObjectPositionUpdater {

    constructor(mapObject) {
        this.mapObject = mapObject
        this.mapObject.getModel()
            .then(this.initialize.bind(this))
    }

    initialize(object) {
        this.object = object
        this.animationController = new AnimationController(object)
    }

    handleTelemetry(message) {
        if (!this.animationController) {
            return
        }
        let telemetry = JSON.parse(message.body)
        this.animationController.update(telemetry)
    }
}
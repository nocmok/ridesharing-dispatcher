import {MercatorProjection} from "../../../map/projections/MercatorProjection";
import {TWEEN} from "three/examples/jsm/libs/tween.module.min";
import {Vector3} from "three";

const projection = new MercatorProjection()

export class AnimationController {

    constructor(vehicle) {
        this.vehicle = vehicle
    }

    update(telemetry) {
        let nextPositionProjection = projection.getProjection(telemetry.latitude, telemetry.longitude)

        let currPosition = this.vehicle.position
        let nextPosition = {x: nextPositionProjection.x, y: this.vehicle.position.y, z: -nextPositionProjection.y}

        let animation = new TWEEN
            .Tween(currPosition)
            .to(nextPosition, 1000)
            .easing(TWEEN.Easing.Quadratic.Out)
            .onUpdate(coordinates => {
                this.vehicle.position.set(coordinates.x, coordinates.y, coordinates.z)
            })

        let currPositionVector = new Vector3(currPosition.x, currPosition.y, currPosition.z)
        let nextPositionVector = new Vector3(nextPosition.x, nextPosition.y, nextPosition.z)
        let directionVector = nextPositionVector.clone().sub(currPositionVector)
        let westVector = new Vector3(1, 0, 0)

        let dot = directionVector.x * westVector.x + directionVector.z * westVector.z
        let det = directionVector.x * westVector.z - directionVector.z * westVector.x
        let angleRadians = Math.atan2(det, dot)

        this.vehicle.rotation.set(0, angleRadians, 0);

        animation.start()
    }
}
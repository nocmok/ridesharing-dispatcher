import {MercatorProjection} from "../../../map/projections/MercatorProjection";
import {TWEEN} from "three/examples/jsm/libs/tween.module.min";

const projection = new MercatorProjection()

export class AnimationController {

    constructor(vehicle) {
        this.vehicle = vehicle
        this.telemetries = []
        this.lastAnimationPromise = new Promise(resolve => resolve())
    }

    coordinatesEquals(a, b) {
        return a.x === b.x && a.y === b.y && a.z === b.z
    }

    update(telemetry) {
        let sourcePosition = this.vehicle.position
        let {x: x, y: z} = projection.getProjection(telemetry.latitude, telemetry.longitude)
        let targetPosition = {x: x, y: this.vehicle.position.y, z: -z}

        if (this.coordinatesEquals(sourcePosition, targetPosition)) {
            return
        }

        if (this.telemetries.length === 0) {
            this.telemetries.push(telemetry)

            let animation = new TWEEN
                .Tween(sourcePosition)
                .to(targetPosition, 1000)
                .easing(TWEEN.Easing.Quadratic.Out)
                .onUpdate(coordinates => {
                    this.vehicle.position.set(coordinates.x, coordinates.y, coordinates.z)
                })

            let newAnimationPromise = new Promise(result => {
                animation.onComplete(() => result())
            })

            if (this.lastAnimationPromise) {
                this.lastAnimationPromise.then(() => {
                    animation.start()
                })
                this.lastAnimationPromise = newAnimationPromise
            } else {
                this.lastAnimationPromise = newAnimationPromise
                animation.start()
            }
        } else {
            if(Date.parse(this.telemetries[this.telemetries.length - 1].recordedAt).valueOf() > Date.parse(telemetry.recordedAt).valueOf()) {
                return;
            }

            while (this.telemetries.length > 1) {
                this.telemetries.shift()
            }
            this.telemetries.push(telemetry)

            let duration = Date.parse(this.telemetries[1].recordedAt).valueOf() - Date.parse(this.telemetries[0].recordedAt).valueOf()

            let animation = new TWEEN
                .Tween(sourcePosition)
                .to(targetPosition, duration)
                .easing(TWEEN.Easing.Quadratic.Out)
                .onUpdate(coordinates => {
                    this.vehicle.position.set(coordinates.x, coordinates.y, coordinates.z)
                })

            let newAnimationPromise = new Promise(result => {
                animation.onComplete(() => result())
            })

            if (this.lastAnimationPromise) {
                this.lastAnimationPromise.then(() => {
                    animation.start()
                })
                this.lastAnimationPromise = newAnimationPromise
            } else {
                this.lastAnimationPromise = newAnimationPromise
                animation.start()
            }
        }
    }
}
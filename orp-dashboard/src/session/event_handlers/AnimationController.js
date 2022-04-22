import {MercatorProjection} from "../../map/projections/MercatorProjection";
import {TWEEN} from "three/examples/jsm/libs/tween.module.min";

const projection = new MercatorProjection()

function coordinatesEquals(a, b) {
    return a.x === b.x && a.y === b.y && a.z === b.z
}

// + если вектор2 справа от вектора 1
// иначе минус
function getRadiansBetweenVectors(vec1, vec2) {
    let sign = Math.sign(vec1.z * vec2.x - vec1.x * vec2.z)
    let cos = (vec1.x * vec2.x + vec1.z * vec2.z) / Math.sqrt(vec1.x ** 2 + vec1.z ** 2) / Math.sqrt(vec2.x ** 2 + vec2.z ** 2)
    return sign * Math.acos(cos)
}

function getRotationToFollowDirection(directionVector) {
    // baseRotation - вектор ротаций если объект ориентирован по оси x

    // найти угол между двумя векторами через скалярное произведение. Второй вектор = (1,0,0)
    // считаем что объект лежит в плоскости xz поэтому полученный угол устанавливается в y

    let angle = getRadiansBetweenVectors({x: 1, y: 0, z: 0}, directionVector)
    return {x: 0, y: angle, z: 0}
}

export class AnimationController {

    constructor(vehicle) {
        this.vehicle = vehicle
        this.telemetries = []
        this.lastAnimationPromise = new Promise(resolve => resolve())
    }

    update(telemetry) {
        let sourcePosition = this.vehicle.position
        let {x: x, y: z} = projection.getProjection(telemetry.latitude, telemetry.longitude)
        let targetPosition = {x: x, y: this.vehicle.position.y, z: -z}

        let direction = {
            x: targetPosition.x - sourcePosition.x,
            y: targetPosition.y - sourcePosition.y,
            z: targetPosition.z - sourcePosition.z
        }

        if (coordinatesEquals(sourcePosition, targetPosition)) {
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
                    let rotation = getRotationToFollowDirection(direction)
                    this.vehicle.rotation.set(rotation.x, rotation.y, rotation.z)
                    animation.start()
                })
                this.lastAnimationPromise = newAnimationPromise
            } else {
                this.lastAnimationPromise = newAnimationPromise
                let rotation = getRotationToFollowDirection(direction)
                this.vehicle.rotation.set(rotation.x, rotation.y, rotation.z)
                animation.start()
            }
        } else {
            if (Date.parse(this.telemetries[this.telemetries.length - 1].recordedAt).valueOf() > Date.parse(telemetry.recordedAt).valueOf()) {
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
                    let rotation = getRotationToFollowDirection(direction)
                    this.vehicle.rotation.set(rotation.x, rotation.y, rotation.z)
                    animation.start()
                })
                this.lastAnimationPromise = newAnimationPromise
            } else {
                this.lastAnimationPromise = newAnimationPromise
                let rotation = getRotationToFollowDirection(direction)
                this.vehicle.rotation.set(rotation.x, rotation.y, rotation.z)
                animation.start()
            }
        }
    }
}
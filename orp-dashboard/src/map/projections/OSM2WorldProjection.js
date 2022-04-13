import {Projection} from "./Projection";

const EARTH_RADIUS_METERS = 6_371_000
const EARTH_CIRCUMFERENCE = 40_075_016.686

function getEarthCircumference(latitude) {
    return EARTH_CIRCUMFERENCE * Math.cos(degreesToRadians(latitude))
}

function degreesToRadians(angle) {
    return angle * Math.PI / 180
}

function lonToX(longitude) {
    return (longitude + 180) / 360
}

function latToY(latitude) {
    let sinLatitude = Math.sin(degreesToRadians(latitude))
    return Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI) + 0.5
}

export class OSM2WorldProjection extends Projection {

    /**
     * Computes cartesian coordinates relative to specified center
     */
    getLocalProjection(centerLatitude, centerLongitude, latitude, longitude) {
        let scaleFactor = getEarthCircumference(centerLatitude)
        let originX = lonToX(centerLongitude) * scaleFactor
        let originY = latToY(centerLatitude) * scaleFactor
        let x = lonToX(longitude) * scaleFactor - originX
        let y = latToY(latitude) * scaleFactor - originY
        return {x: x, z: -y}
    }

}
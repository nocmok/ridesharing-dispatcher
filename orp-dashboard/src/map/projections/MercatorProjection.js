import {Projection} from "./Projection";

// eslint-disable-next-line no-unused-vars
const EARTH_RADIUS_METERS = 6_371_000
const EARTH_CIRCUMFERENCE = 40_075_016.686

// eslint-disable-next-line no-unused-vars
const MAX_LATITUDE = 85.05112878;
const MAX_Y = 20037508.343038715;
// eslint-disable-next-line no-unused-vars
const MIN_LATITUDE = -85.05112878;
const MIN_Y = -20037508.343038715;

/**
 * +PI -> MAX_LATITUDE
 * -PI -> MIN_LATITUDE
 */
function gudermannian(y) {
    return Math.atan(Math.sinh(y)) * (180 / Math.PI)
}

/**
 * MAX_LATITUDE -> +PI
 * MIN_LATITUDE -> -PI
 */
function gudermannianInv(latitude) {
    let sign = Math.sign(latitude);
    let sin = Math.sin(
        latitude
        * (Math.PI / 180)
        * sign
    );
    return sign * (
        Math.log(
            (1 + sin) / (1 - sin)
        ) / 2
    );
}

/**
 * -PI -> MIN_Y
 * +PI -> MAX_Y
 */
function radiansToY(rad) {
    return (rad + Math.PI) * (MAX_Y - MIN_Y) / (2 * Math.PI) + MIN_Y
}

/**M
 * MIN_Y -> -PI
 * MAX_Y -> +PI
 */
function yToRadians(y) {
    return (y - MIN_Y) * 2 * Math.PI / (MAX_Y - MIN_Y) - Math.PI
}

export class MercatorProjection extends Projection {

    getProjection(latitude, longitude) {
        let x = (longitude + 180) * EARTH_CIRCUMFERENCE / 360
        let y = radiansToY(gudermannianInv(latitude))
        return {x: x, y: y}
    }

    getInvertedProjection(x, y) {
        let lat = gudermannian(yToRadians(y))
        let lon = x * 360 / EARTH_CIRCUMFERENCE - 180
        return {lat, lon}
    }
}
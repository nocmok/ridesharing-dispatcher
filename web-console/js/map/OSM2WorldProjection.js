export class OSM2WorldProjection {

    static EARTH_RADIUS_METERS = 6_371_000
    static EARTH_CIRCUMFERENCE = 40_075_016.686

    #degreesToRadians(angle) {
        return angle * Math.PI / 180
    }

    #getEarthCircumference(latitude) {
        return OSM2WorldProjection.EARTH_CIRCUMFERENCE * Math.cos(this.#degreesToRadians(latitude))
    }

    #lonToX(longitude) {
        return (longitude + 180) / 360
    }

    #latToY(latitude) {
        var sinLatitude = Math.sin(this.#degreesToRadians(latitude))
        return Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI) + 0.5
    }

    /**
     * Computes cartesian coordinates relative to specified center 
     */
    getLocalProjection(centerLatitude, centerLongitude, latitude, longitude) {
        var scaleFactor = this.#getEarthCircumference(centerLatitude)
        var originX = this.#lonToX(centerLongitude) * scaleFactor
        var originY = this.#latToY(centerLatitude) * scaleFactor
        var x = this.#lonToX(longitude) * scaleFactor - originX
        var y = this.#latToY(latitude) * scaleFactor - originY
        return { x: x, z: -y }
    }

}
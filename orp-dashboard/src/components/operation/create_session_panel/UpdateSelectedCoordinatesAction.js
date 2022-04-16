import {MercatorProjection} from "../../../map/projections/MercatorProjection";

export class UpdateSelectedCoordinatesAction {

    constructor(component) {
        this.component = component
        this.map = component.map
        this.projection = new MercatorProjection()
    }

    activate() {
    }

    deactivate() {
    }

    getCameraLookAt() {
        let {x, y, z} = this.map.camera.threeCamera.position
        let {x: xr, y: yr, z: zr} = this.map.camera.threeCamera.rotation

        x = x + Math.tan(zr) * y
        z = z + Math.tan(xr) * y

        let {lat, lon} = this.projection.getInvertedProjection(x, -z)

        return {lat: lat, lon: lon}
    }

    updateCoordinates() {
        let {lat, lon} = this.getCameraLookAt()
        this.component.setState({latitude: lat, longitude: lon})
    }
}
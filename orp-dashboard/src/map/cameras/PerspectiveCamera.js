import * as THREE from 'three';
import {MercatorProjection} from '../projections/MercatorProjection';
import {SpatialCamera} from "./SpatialCamera";

export class PerspectiveCamera extends SpatialCamera {

    #threeCamera

    constructor(projection) {
        super()

        this.#threeCamera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 10000);

        this.threeCamera.zoom = 1
        this.threeCamera.position.set(0, 200, 0)
        this.threeCamera.rotation.set(-0.79, 0, 0)

        this.projection = projection || new MercatorProjection();
    }

    get threeCamera() {
        return this.#threeCamera;
    }

    getCameraPositionByLookAt(latitude, longitude) {
        let {x: x, y: z} = this.projection.getProjection(latitude, longitude);
        let y = this.threeCamera.position.y
        let {x: xr, y: yr, z: zr} = this.threeCamera.rotation
        x = x - Math.tan(zr) * y
        z = z + Math.tan(xr) * y
        return {x: x, y: y, z: -z}
    }

    lookAt(latitude, longitude) {
        let {x, y, z} = this.getCameraPositionByLookAt(latitude, longitude)
        this.threeCamera.position.set(x, y, z)
    }
}
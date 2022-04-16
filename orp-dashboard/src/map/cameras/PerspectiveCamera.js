import * as THREE from 'three';
import {MercatorProjection} from '../projections/MercatorProjection';
import {SpatialCamera} from "./SpatialCamera";

export class PerspectiveCamera extends SpatialCamera {

    #threeCamera

    constructor(projection) {
        super()

        this.#threeCamera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 10000);

        this.threeCamera.zoom = 1
        this.threeCamera.position.set(0, 400, 0)
        this.threeCamera.rotation.set(-0.79, 0, 0)

        this.projection = projection || new MercatorProjection();
    }

    get threeCamera() {
        return this.#threeCamera;
    }

    lookAt(latitude, longitude) {
        let {x: x, y: z} = this.projection.getProjection(latitude, longitude)
        this.threeCamera.position.set(x, this.threeCamera.position.y, -z);
    }
}
import * as THREE from 'three';
import { OSM2WorldProjection } from '../projections/OSM2WorldProjection';
import {SpatialCamera} from "./SpatialCamera";

export class PerspectiveCamera extends SpatialCamera {

    constructor(projection) {
        super()

        this.camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 1, 1000);
        this.camera.zoom = 1
        this.camera.position.set(0,200,0)
        this.camera.rotation.set(-1,0,0)

        this.projection = projection || new OSM2WorldProjection();
    }

    get threeCamera() {
        return this.camera;
    }

    lookAt(latitude, longitude) {
        let { x, z } = this.projection.getLocalProjection(0,0,latitude,longitude)
        this.camera.position.set(x, this.camera.position.y, z);
    }
}
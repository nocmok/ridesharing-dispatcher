import * as THREE from 'three';
import { OSM2WorldProjection } from './OSM2WorldProjection.js';

export class SpatialCamera {

    constructor() {
        this.camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 1, 1000);
        this.camera.zoom = 1
        this.camera.position.set(0,100,0)
        this.camera.rotation.set(-1,0,0)

        this.projection = new OSM2WorldProjection();
    }

    getThreeJsCamera() {
        return this.camera;
    }

    lookAt(latitude, longitude) {
        let { x, z } = this.projection.getLocalProjection(0,0,latitude,longitude)
        this.camera.position.set(x, this.camera.position.y, z);
    }
}
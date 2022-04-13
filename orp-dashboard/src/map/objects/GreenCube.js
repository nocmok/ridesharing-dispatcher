import * as THREE from "three";
import {OSM2WorldProjection} from "../projections/OSM2WorldProjection";

export class GreenCube {

    constructor() {
        this.latitude = 0
        this.longitude = 0
        this.projection = new OSM2WorldProjection();

        this.model = new Promise((resolve, error) => {
            let model = new THREE.Mesh(new THREE.BoxGeometry(1,1,1), new THREE.MeshBasicMaterial({color: 0x00ff00}))
            resolve(model)
        })
    }

    setCoordinates(latitude, longitude) {
        this.latitude = latitude
        this.longitude = longitude

        this.model.then(model => {
            let {x, z} = this.projection.getLocalProjection(0, 0, this.latitude, this.longitude)
            model.position.set(x, 0, z)
        })
    }

    getModel() {
        return this.model
    }
}
import * as THREE from "three";
import {MercatorProjection} from "../projections/MercatorProjection";
import {MapObject} from "./MapObject";

export class GreenCube extends MapObject {

    constructor(position) {
        super()

        this.latitude = (position === undefined ? 0 : position.latitude) || 0
        this.longitude = (position === undefined ? 0 : position.longitude) || 0
        this.projection = new MercatorProjection();

        this.model = new Promise((resolve, error) => {
            let model = new THREE.Mesh(new THREE.BoxGeometry(10, 10, 10), new THREE.MeshBasicMaterial({color: 0x00ff00}))
            let {x: x, y: z} = this.projection.getProjection(this.latitude, this.longitude)
            model.position.set(x, 0, -z)
            resolve(model)
        })
    }

    setCoordinates(latitude, longitude) {
        this.latitude = latitude
        this.longitude = longitude

        this.model.then(model => {
            let {x: x, y: z} = this.projection.getProjection(this.latitude, this.longitude)
            model.position.set(x, 0, -z)
        })
    }

    getModel() {
        return this.model
    }
}
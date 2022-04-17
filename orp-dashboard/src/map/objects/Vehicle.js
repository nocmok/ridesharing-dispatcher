import {MapObject} from "./MapObject";
import {MercatorProjection} from "../projections/MercatorProjection";
import * as THREE from "three";

export class Vehicle extends MapObject {

    constructor() {
        super()

        this.latitude = 0
        this.longitude = 0
        this.projection = new MercatorProjection();

        this.model = new Promise((resolve, error) => {
            let model = new THREE.Mesh(new THREE.BoxGeometry(10,10,10), new THREE.MeshBasicMaterial({color: 0xff0000}))
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
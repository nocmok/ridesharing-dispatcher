import {MapObject} from "./MapObject";
import {MercatorProjection} from "../projections/MercatorProjection";
import {GLTFLoader} from "three/examples/jsm/loaders/GLTFLoader";

const loader = new GLTFLoader()

export class Vehicle extends MapObject {

    constructor() {
        super()

        this.latitude = 0
        this.longitude = 0
        this.projection = new MercatorProjection();

        this.model = loader.loadAsync("/models/vehicle/vehicle.gl.glb").then(glb => glb.scene)
    }

    setCoordinates(latitude, longitude) {
        this.latitude = latitude
        this.longitude = longitude

        this.model.then(model => {
            model.scale.set(10, 10, 10)
            model.rotation.set(0, 0, 0)
            let {x: x, y: z} = this.projection.getProjection(this.latitude, this.longitude)
            model.position.set(x, 10, -z)
        })
    }

    getModel() {
        return this.model
    }
}
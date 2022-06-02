import {MapObject} from "./MapObject";
import {MercatorProjection} from "../projections/MercatorProjection";
import {AxesHelper} from "three";

export class Axes extends MapObject {
    constructor() {
        super()

        this.latitude = 0
        this.longitude = 0
        this.projection = new MercatorProjection();

        this.model = new Promise((resolve, error) => {
            resolve(new AxesHelper(100))
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
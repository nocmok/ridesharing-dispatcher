import {MercatorProjection} from "../projections/MercatorProjection";
import {MapObject} from "./MapObject";
import {ObjLoader} from "./ObjLoader";

const loader = new ObjLoader()

export class MapPointer extends MapObject {

    constructor(position) {
        super()

        this.latitude = (position === undefined ? 0 : position.latitude) || 0
        this.longitude = (position === undefined ? 0 : position.longitude) || 0
        this.projection = new MercatorProjection();

        this.model = loader.loadModel("/models/map_pointer/flag.mtl", "/models/map_pointer/flag.obj").then(model => {
                model.scale.set(20,20,20)
                return model
            }
        )

        this.setCoordinates(this.latitude, this.longitude)
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
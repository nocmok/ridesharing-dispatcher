import {MapObject} from "./MapObject";
import {MercatorProjection} from "../projections/MercatorProjection";
import {ObjLoader} from "./ObjLoader";

export class OdintsovoMapTile extends MapObject {

    constructor() {
        super()

        this.latitude = 0
        this.longitude = 0
        this.projection = new MercatorProjection()
        this.model = new ObjLoader()
            .loadModel('/models/odintsovo/odintsovo.obj.mtl', '/models/odintsovo/odintsovo.obj')
    }

    getCoordinates() {
        return {latitude: this.latitude, longitude: this.longitude}
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
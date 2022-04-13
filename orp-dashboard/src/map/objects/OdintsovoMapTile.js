import {MapObject} from "./MapObject";
import {OSM2WorldProjection} from "../projections/OSM2WorldProjection";
import {ObjLoader} from "./ObjLoader";

export class OdintsovoMapTile extends MapObject {

    constructor() {
        super()

        this.latitude = 0
        this.longitude = 0
        this.projection = new OSM2WorldProjection()
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
            let {x, z} = this.projection.getLocalProjection(0, 0, this.latitude, this.longitude)
            model.position.set(x, 0, z)
        })
    }

    getModel() {
        return this.model
    }
}
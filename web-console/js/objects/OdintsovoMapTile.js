import { ObjLoader } from "./ObjLoader.js"

export class OdintsovoMapTile {

    getCoordinates() {
        return { latitude: 55.66971000000001, longitude: 37.28309499999999 }
    }

    getModel() {
        if(this.model == undefined) {
            this.model = new ObjLoader().loadModel('../../models/odintsovo/odintsovo.obj.mtl', '../../models/odintsovo/odintsovo.obj')
        }
        return this.model
    }
}
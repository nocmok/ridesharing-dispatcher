import {MercatorProjection} from "../projections/MercatorProjection";
import * as THREE from "three";
import {MapObject} from "./MapObject";
import {ObjLoader} from "./ObjLoader";

const loader = new ObjLoader()

export class MapPointer extends MapObject {

    constructor(position) {
        super()

        this.latitude = (position === undefined ? 0 : position.latitude) || 0
        this.longitude = (position === undefined ? 0 : position.longitude) || 0
        this.projection = new MercatorProjection();

        // this.model = new Promise((resolve, error) => {
        //     let model = new THREE.Mesh(new THREE.ConeGeometry( 10, 20, 3 ), new THREE.MeshPhongMaterial({color: 0xff0000}))
        //     let {x: x, y: z} = this.projection.getProjection(this.latitude, this.longitude)
        //     model.position.set(x, 20, -z)
        //     model.rotation.set(3.14, 0.57, 0)
        //     resolve(model)
        // })

        this.model = loader.loadModel("/models/map_pointer/flag.mtl", "/models/map_pointer/flag.obj").then(model => {
                // console.log(model)
                model.scale.set(20,20,20)
                return model
            }
        )
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
import {MapObject} from "./MapObject";
import {MercatorProjection} from "../projections/MercatorProjection";
import {GLTFLoader} from "three/examples/jsm/loaders/GLTFLoader";
import {ObjLoader} from "./ObjLoader";
import * as THREE from "three";
import {Matrix4} from "three";

// const loader = new GLTFLoader()
const loader = new ObjLoader()

export class Vehicle extends MapObject {

    constructor() {
        super()

        this.latitude = 0
        this.longitude = 0
        this.projection = new MercatorProjection();

        this.model = loader.loadModel(null, "/models/vehicle/vehicle.obj").then(model => {
            let rotationMatrix = new Matrix4()
            rotationMatrix.set(
                1, 0, 0, 0,
                0, 0, 1, 0,
                0, -1, 0, 0,
                0, 0, 0, 1)

            model.children[0].material = new THREE.MeshPhongMaterial({color: 0xA0A0A0, opacity: 0});
            model.children[0].geometry.applyMatrix4(rotationMatrix)
            return model
        })
    }

    setCoordinates(latitude, longitude) {
        this.latitude = latitude
        this.longitude = longitude

        this.model.then(model => {
            model.scale.set(10, 10, 10)
            let {x: x, y: z} = this.projection.getProjection(this.latitude, this.longitude)
            model.position.set(x, 15, -z)
        })
    }

    getModel() {
        return this.model
    }
}

// import {MapObject} from "./MapObject";
// import {MercatorProjection} from "../projections/MercatorProjection";
// import {GLTFLoader} from "three/examples/jsm/loaders/GLTFLoader";
//
// const loader = new GLTFLoader()
//
// export class Vehicle extends MapObject {
//
//     constructor() {
//         super()
//
//         this.latitude = 0
//         this.longitude = 0
//         this.projection = new MercatorProjection();
//
//         this.model = loader.loadAsync("/models/vehicle/vehicle.gl.glb").then(glb => glb.scene)
//     }
//
//     setCoordinates(latitude, longitude) {
//         this.latitude = latitude
//         this.longitude = longitude
//
//         this.model.then(model => {
//             model.scale.set(10, 10, 10)
//             model.rotation.set(0, 0, 0)
//             let {x: x, y: z} = this.projection.getProjection(this.latitude, this.longitude)
//             model.position.set(x, 10, -z)
//         })
//     }
//
//     getModel() {
//         return this.model
//     }
// }
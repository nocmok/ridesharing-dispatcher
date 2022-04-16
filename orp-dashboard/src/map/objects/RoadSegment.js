import {MapObject} from "./MapObject";
import {MercatorProjection} from "../projections/MercatorProjection";
import * as THREE from "three";

export class RoadSegment extends MapObject {

    constructor(sourceLatLon, targetLatLon) {
        super()

        this.sourceLatLon = sourceLatLon
        this.targetLatLon = targetLatLon
        this.projection = new MercatorProjection();

        this.model = new Promise((resolve, error) => {
            let sourceCube = new THREE.Mesh(new THREE.BoxGeometry(10, 10, 10), new THREE.MeshBasicMaterial({color: 0x00ff00}));
            let targetCube = new THREE.Mesh(new THREE.BoxGeometry(10, 10, 10), new THREE.MeshBasicMaterial({color: 0x00ff00}));

            let source = this.projection.getProjection(this.sourceLatLon.latitude, this.sourceLatLon.longitude)
            let target = this.projection.getProjection(this.targetLatLon.latitude, this.targetLatLon.longitude)

            sourceCube.position.set(source.x, 0, -source.y)
            targetCube.position.set(target.x, 0, -target.y)

            let group = new THREE.Group()
            group.add(sourceCube)
            group.add(targetCube)

            resolve(group)
        })
    }

    getModel() {
        return this.model
    }
}
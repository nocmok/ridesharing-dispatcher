import {MapObject} from "./MapObject";
import {MercatorProjection} from "../projections/MercatorProjection";
import * as THREE from "three";

const projection = new MercatorProjection()

function latLonToXYZ(latLon) {
    let {x: x, y: z} = projection.getProjection(latLon.latitude, latLon.longitude)
    return {x: x, y: 10, z: -z}
}

function getOrthonormal(sourceXYZ, targetXYZ) {
    let delta = {
        x: targetXYZ.x - sourceXYZ.x,
        y: targetXYZ.y - sourceXYZ.y,
        z: targetXYZ.z - sourceXYZ.z
    }
    let orthogonal = {x: delta.z, y: 0, z: -delta.x}
    let len = Math.sqrt(orthogonal.x ** 2 + orthogonal.z ** 2)
    return {x: orthogonal.x * 5 / len, y: 0, z: orthogonal.z * 5 / len}
}

function sum(xyz1, xyz2) {
    return {x: xyz1.x + xyz2.x, y: xyz1.y + xyz2.y, z: xyz1.z + xyz2.z}
}

function sub(xyz1, xyz2) {
    return {x: xyz1.x - xyz2.x, y: xyz1.y - xyz2.y, z: xyz1.z - xyz2.z}
}

export class RoadSegment extends MapObject {


    constructor(sourceLatLon, targetLatLon) {
        super()

        let sourceXYZ = latLonToXYZ(sourceLatLon)
        let targetXYZ = latLonToXYZ(targetLatLon)

        let orthonormal = getOrthonormal(sourceXYZ, targetXYZ)

        let pivots = [
            sum(sourceXYZ, orthonormal),
            sum(targetXYZ, orthonormal),
            sub(targetXYZ, orthonormal),
            sub(sourceXYZ, orthonormal)
        ]

        let vertices = new Float32Array([
            pivots[0].x, 1, pivots[0].z,
            pivots[1].x, 1, pivots[1].z,
            pivots[2].x, 1, pivots[2].z,

            pivots[2].x, 1, pivots[2].z,
            pivots[3].x, 1, pivots[3].z,
            pivots[0].x, 1, pivots[0].z,
        ])

        this.model = new Promise((resolve, error) => {
            let roadGeometry = new THREE.BufferGeometry();
            roadGeometry.setAttribute('position', new THREE.BufferAttribute(vertices, 3));
            let roadMaterial = new THREE.MeshBasicMaterial({color: 0x569DEF, side: THREE.DoubleSide,});
            let road = new THREE.Mesh(roadGeometry, roadMaterial);

            let sourceGeometry = new THREE.CircleGeometry( 10, 32 );
            let sourceMaterial = new THREE.MeshBasicMaterial( { color: 0x888888, side: THREE.DoubleSide } );
            let source = new THREE.Mesh(sourceGeometry, sourceMaterial);
            source.position.set(sourceXYZ.x, 2, sourceXYZ.z)
            source.rotation.set(-1.57,0,0)

            let targetGeometry = new THREE.CircleGeometry( 10, 32 );
            let targetMaterial = new THREE.MeshBasicMaterial( { color: 0x888888, side: THREE.DoubleSide } );
            let target = new THREE.Mesh(targetGeometry, targetMaterial);
            target.position.set(targetXYZ.x, 2, targetXYZ.z)
            target.rotation.set(-1.57,0,0)

            let group = new THREE.Group()
            group.add(road)
            group.add(source)
            group.add(target)

            resolve(group)
        })
    }

    getModel() {
        return this.model
    }
}
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

export class Route extends MapObject {

    constructor(latLonArray) {
        super()

        let xyzArray = latLonArray.map(latLonToXYZ)
        let vertices = []
        let group = new THREE.Group()

        for (let i = 1; i < xyzArray.length; ++i) {
            let sourceXYZ = xyzArray[i - 1]
            let targetXYZ = xyzArray[i]

            let orthonormal = getOrthonormal(sourceXYZ, targetXYZ)

            let pivots = [
                sum(sourceXYZ, orthonormal),
                sum(targetXYZ, orthonormal),
                sub(targetXYZ, orthonormal),
                sub(sourceXYZ, orthonormal)
            ]

            let newVertices = [
                    pivots[0].x, 1, pivots[0].z,
                    pivots[1].x, 1, pivots[1].z,
                    pivots[2].x, 1, pivots[2].z,

                    pivots[2].x, 1, pivots[2].z,
                    pivots[3].x, 1, pivots[3].z,
                    pivots[0].x, 1, pivots[0].z,
            ]

            newVertices.forEach(vertex => vertices.push(vertex))
        }

        let routeGeometry = new THREE.BufferGeometry();
        routeGeometry.setAttribute('position', new THREE.BufferAttribute(new Float32Array(vertices), 3));
        let routeMaterial = new THREE.MeshBasicMaterial({color: 0x569DEF, side: THREE.DoubleSide,});
        let route = new THREE.Mesh(routeGeometry, routeMaterial);

        group.add(route)

        for (let i = 0; i < xyzArray.length; ++i) {
            let circleGeometry = new THREE.CircleGeometry(10, 32);
            let circleMaterial = new THREE.MeshBasicMaterial({color: 0x888888, side: THREE.DoubleSide});
            let circle = new THREE.Mesh(circleGeometry, circleMaterial);
            circle.position.set(xyzArray[i].x, 2, xyzArray[i].z)
            circle.rotation.set(-1.57, 0, 0)

            group.add(circle)
        }

        this.model = new Promise((resolve, error) => {
            resolve(group)
        })
    }

    getModel() {
        return this.model
    }
}
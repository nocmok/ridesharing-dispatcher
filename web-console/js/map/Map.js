import * as THREE from 'three';
import { SpatialCamera } from './SpatialCamera.js';
import { OSM2WorldProjection } from './OSM2WorldProjection.js';

export class Map {

    constructor() {
        this.scene = new THREE.Scene();
        this.camera = new SpatialCamera();
        this.renderer = new THREE.WebGLRenderer({ antialias: true });

        this.#setUpScene(this.scene)
        this.#setUpRenderer(this.renderer)

        this.projection = new OSM2WorldProjection();
        this.objects = []
    }

    #setUpRenderer(renderer) {
        renderer.setSize(window.innerWidth, window.innerHeight);
        document.body.appendChild(renderer.domElement);
        renderer.setClearColor(0xffffffff)
    }

    #setUpScene(scene) {
        let ambientLight = new THREE.AmbientLight(0xffffff, 0.9);
        let directionalLight = new THREE.DirectionalLight(0xffffff, 0.1, 100);

        directionalLight.shadow.radius = 100
        directionalLight.position.set(1, 1, 0);
        directionalLight.castShadow = true;

        scene.add(directionalLight);
        scene.add(ambientLight);
    }

    addObject(object) {
        object.getModel().then((model => {

            let { latitude, longitude } = object.getCoordinates()
            let { x, z } = this.projection.getLocalProjection(0, 0, latitude, longitude)
            
            model.position.set(x, 0, z)
            this.scene.add(model)

        }).bind(this))
    }

    getCamera() {
        return this.camera;
    }

    getRenderer() {
        return this.renderer;
    }

    render() {
        this.renderer.render(this.scene, this.camera.getThreeJsCamera())
    }
}
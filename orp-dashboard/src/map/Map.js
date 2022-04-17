import * as THREE from "three";
import {PerspectiveCamera} from "./cameras/PerspectiveCamera";
import {MercatorProjection} from "./projections/MercatorProjection";

export class Map {

    #scene
    #camera
    #renderer

    constructor() {
        this.#scene = new THREE.Scene();
        this.#camera = new PerspectiveCamera(new MercatorProjection());
        this.#renderer = new THREE.WebGLRenderer({antialias: true});

        Map.#setUpScene(this.scene)
        Map.#setUpRenderer(this.renderer)
    }

    static #setUpRenderer(renderer) {
        renderer.setSize(window.innerWidth, window.innerHeight);
        renderer.setClearColor(0xffffff)
    }

    static #setUpScene(scene) {
        let ambientLight = new THREE.AmbientLight(0xffffff, 0.9);
        let directionalLight = new THREE.DirectionalLight(0xffffff, 0.1, 100);

        directionalLight.shadow.radius = 100
        directionalLight.position.set(1, 1, 0);
        directionalLight.castShadow = true;

        scene.add(directionalLight);
        scene.add(ambientLight);
    }

    addObject(object) {
        object.getModel().then(model => {
            this.scene.add(model)
        })
    }

    removeObject(object) {
        object.getModel().then(model => {
            this.scene.remove(model)
        })
    }

    get renderer() {
        return this.#renderer;
    }

    get camera() {
        return this.#camera
    }

    get scene() {
        return this.#scene
    }

    get threeCamera() {
        return this.#camera.threeCamera;
    }

    resize(newWidth, newHeight) {
        this.threeCamera.aspect = newWidth / newHeight;

        this.threeCamera.updateProjectionMatrix();
        this.renderer.setSize(newWidth, newHeight);
    }

    lookAt(latitude, longitude) {
        this.camera.lookAt(latitude, longitude)
    }

    render() {
        this.renderer.render(this.scene, this.threeCamera)
    }
}
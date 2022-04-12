export class ResizeAction {

    constructor(app) {
        this.onWindowResize = this.onWindowResize.bind(this)

        this.app = app
        window.addEventListener('resize', this.onWindowResize)
    }

    onWindowResize() {
        let camera = this.app.getMap().getCamera().getThreeJsCamera()
        let renderer = this.app.getMap().getRenderer()

        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
    }
}
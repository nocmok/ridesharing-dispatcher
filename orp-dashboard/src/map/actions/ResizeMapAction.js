export class ResizeMapAction {

    constructor(map) {
        this.map = map
        window.addEventListener('resize', this.onWindowResize.bind(this))
    }

    onWindowResize() {
        const canvasParent = this.map.renderer.domElement.parentElement;
        if(!canvasParent) {
            return
        }
        this.map.resize(canvasParent.clientWidth, canvasParent.clientHeight)
    }
}
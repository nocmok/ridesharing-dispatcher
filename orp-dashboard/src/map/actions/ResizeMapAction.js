export class ResizeMapAction {

    constructor(map) {
        this.map = map
        window.addEventListener('resize', this.onWindowResize.bind(this))
    }

    onWindowResize() {
        this.map.resize(window.innerWidth, window.innerHeight)
    }
}
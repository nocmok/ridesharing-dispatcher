export class DragCameraAction {

    constructor(map) {
        this.map = map
        this.isDragging = false

        this.map.renderer.domElement.addEventListener('mousemove', this.onMouseMove.bind(this))
        this.map.renderer.domElement.addEventListener('mousedown', this.onMouseDown.bind(this))
        this.map.renderer.domElement.addEventListener('mouseup', this.onMouseUp.bind(this))
    }

    onMouseDown() {
        this.isDragging = true
    }

    onMouseUp() {
        this.isDragging = false
    }

    onMouseMove(event) {
        if(!this.isDragging) {
            return
        }
        this.map.threeCamera.position.x -= event.movementX * 0.5;
        this.map.threeCamera.position.z -= event.movementY * 0.5;
    }
}
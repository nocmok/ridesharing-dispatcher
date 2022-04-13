export class DragCameraAction {

    constructor(map) {
        this.map = map
        this.isDragging = false

        window.addEventListener('mousemove', this.onMouseMove.bind(this))
        window.addEventListener('mousedown', this.onMouseDown.bind(this))
        window.addEventListener('mouseup', this.onMouseUp.bind(this))
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
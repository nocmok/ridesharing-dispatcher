export class DragCameraAction {

    constructor(app) {
        this.app = app

        this.onDocumentMouseMove = this.onDocumentMouseMove.bind(this)
        this.onMouseDown = this.onMouseDown.bind(this)
        this.onMouseUp = this.onMouseUp.bind(this)

        this.isDragging = false

        document.addEventListener('mousemove', this.onDocumentMouseMove)
        document.addEventListener('mousedown', this.onMouseDown)
        document.addEventListener('mouseup', this.onMouseUp)
    }

    onMouseDown() {
        this.isDragging = true
    }

    onMouseUp() {
        this.isDragging = false
    }

    onDocumentMouseMove(event) {
        if (!this.isDragging) {
            return
        }
        let camera = this.app.getMap().getCamera().getThreeJsCamera()
        camera.position.x -= event.movementX * 0.5;
        camera.position.z -= event.movementY * 0.5;
    }
}
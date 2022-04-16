import {MercatorProjection} from "../projections/MercatorProjection";

export class DragMapAction {

    constructor(map, callbacks) {
        this.map = map

        this.onDragCallback = callbacks.onDrag || (() => {
        })
        this.onDragStoppedCallback = callbacks.onDragStopped || (() => {
        })
        this.activateCallback = callbacks.activate || (() => {
        })
        this.deactivateCallback = callbacks.deactivate || (() => {
        })

        this.isDragging = false
        this.projection = new MercatorProjection()

        this.onMouseMove = this.onMouseMove.bind(this)
        this.onMouseDown = this.onMouseDown.bind(this)
        this.onMouseUp = this.onMouseUp.bind(this)
    }

    activate() {
        this.map.renderer.domElement.addEventListener('mousemove', this.onMouseMove)
        this.map.renderer.domElement.addEventListener('mousedown', this.onMouseDown)
        this.map.renderer.domElement.addEventListener('mouseup', this.onMouseUp)

        this.activateCallback()
    }

    deactivate() {
        this.map.renderer.domElement.removeEventListener('mousemove', this.onMouseMove)
        this.map.renderer.domElement.removeEventListener('mousedown', this.onMouseDown)
        this.map.renderer.domElement.removeEventListener('mouseup', this.onMouseUp)

        this.deactivateCallback()
    }

    onMouseDown() {
        this.isDragging = true
    }

    onMouseUp() {
        this.isDragging = false
        this.onDragStoppedCallback()
    }

    onMouseMove() {
        if (!this.isDragging) {
            return
        }
        this.onDragCallback()
    }
}
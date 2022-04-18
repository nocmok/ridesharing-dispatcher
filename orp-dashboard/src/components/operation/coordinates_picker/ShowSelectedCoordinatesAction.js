import {GreenCube} from "../../../map/objects/GreenCube";

export class ShowSelectedCoordinatesAction {

    constructor(component) {
        this.map = component.map;
        this.component = component
        this.pointer = new GreenCube()
        this.pointer.getModel().visible = false
        this.map.addObject(this.pointer)
    }

    activate() {

    }

    deactivate() {
        this.pointer.getModel().visible = false
    }

    showCoordinates() {
        if (!this.component.coordinates) {
            return
        }
        this.pointer.setCoordinates(this.component.coordinates.latitude, this.component.coordinates.longitude)
        this.pointer.getModel().visible = true
    }
}
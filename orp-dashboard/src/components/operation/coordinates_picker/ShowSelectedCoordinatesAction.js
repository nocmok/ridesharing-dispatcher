import {GreenCube} from "../../../map/objects/GreenCube";
import {MapPointer} from "../../../map/objects/MapPointer";

export class ShowSelectedCoordinatesAction {

    constructor(component) {
        this.map = component.map;
        this.component = component
        this.pointer = new MapPointer()
        this.pointer.getModel().visible = false
        this.map.addObject(this.pointer)
    }

    activate() {

    }

    deactivate() {
        this.pointer.getModel().then(model => model.visible = false)
    }

    showCoordinates() {
        if (!this.component.coordinates) {
            return
        }
        this.pointer.setCoordinates(this.component.coordinates.latitude, this.component.coordinates.longitude)
        this.pointer.getModel().visible = true
    }
}
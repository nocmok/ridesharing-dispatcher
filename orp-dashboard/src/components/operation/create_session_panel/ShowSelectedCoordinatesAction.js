import {Vehicle} from "../../../map/objects/Vehicle";

export class ShowSelectedCoordinatesAction {

    constructor(component) {
        this.map = component.map;
        this.component = component
        this.vehicle = new Vehicle()
        this.vehicle.getModel().then(model => model.visible = false)
        this.map.addObject(this.vehicle)
    }

    activate() {

    }

    deactivate() {
        this.vehicle.getModel().then(model => model.visible = false)
    }

    showCoordinates() {
        this.vehicle.setCoordinates(this.component.state.latitude, this.component.state.longitude)
        this.vehicle.getModel().then(model => model.visible = true)
    }
}
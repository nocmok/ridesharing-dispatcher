import {RoadSegment} from "../../../map/objects/RoadSegment";

export class ShowSelectedRoadAction {

    constructor(component) {
        this.component = component
    }

    activate() {
    }

    deactivate() {
        if (this.road) {
            this.component.map.removeObject(this.road)
        }
    }

    showSelectedRoad() {
        if (!this.component.roadPromise) {
            return
        }
        this.component.roadPromise.then(road => {
                if (this.road) {
                    this.component.map.removeObject(this.road)
                }
                this.road = new RoadSegment(road.source.coordinates, road.target.coordinates)
                this.component.map.addObject(this.road)
            }
        )
    }
}
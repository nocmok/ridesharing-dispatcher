import * as GeoApi from "../../../api/GeoApi";

export class UpdateSelectedRoadAction {

    constructor(component) {
        this.component = component
    }

    activate() {
    }

    deactivate() {
    }

    updateRoadSegment() {
        if(!this.component.state.coordinates || !this.component.state.coordinates.latitude || !this.component.state.coordinates.longitude) {
            return
        }
        let roadPromise = GeoApi.getRoadSegmentByLatLon({
            coordinates: {
                latitude: this.component.state.coordinates.latitude,
                longitude: this.component.state.coordinates.longitude,
            },
            rightHandTraffic: true
        }).then(response => response.road)

        this.component.roadPromise = roadPromise

        roadPromise.then(road => this.component.setState({road: road}))
        roadPromise.then(road => console.log(road))
    }
}
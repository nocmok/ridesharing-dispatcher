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
        let roadPromise = GeoApi.getRoadSegmentByLatLon({
            coordinates: {
                latitude: this.component.state.latitude,
                longitude: this.component.state.longitude,
            },
            rightHandTraffic: true
        }).then(response => response.road)

        this.component.roadPromise = roadPromise

        roadPromise.then(road => this.component.setState({road: road}))
    }
}
import {Route} from "../../../map/objects/Route";

export class ShowVehicleRouteHandler {

    constructor(map) {
        this.map = map

        this.route = null
    }

    handleRequestAssignment(notification) {
        let body = JSON.parse(notification.body)
        console.log(body)
        let routeScheduled = body.routeScheduled
        if (this.route) {
            this.map.removeObject(this.route)
        }
        this.route = new Route(routeScheduled)
        this.map.addObject(this.route)
    }
}
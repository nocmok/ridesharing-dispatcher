import * as GodApi from "./api/GodApi"
import {Vehicle} from "./map/objects/Vehicle";
import {SessionListener} from "./session/SessionListener";
import {MapObjectPositionUpdater} from "./session/event_handlers/MapObjectPositionUpdater";
import {ShowVehicleRouteHandler} from "./session/event_handlers/ShowVehicleRouteHandler";

export class Bootstrapper {

    constructor(di) {
        this.di = di
    }

    bootstrap() {
        return GodApi.getActiveSessionIds()
            .then(response => {

                console.log(response)


                return GodApi.getSessionGeodata({
                    sessionIds: response.activeSessionsIds
                })
            }).then(response => {

                console.log(response)

                response.sessions.forEach(sessionGeodata => {
                    let mapObject = new Vehicle()
                    mapObject.setCoordinates(sessionGeodata.coordinates.latitude, sessionGeodata.coordinates.longitude)

                    let sessionListener = new SessionListener(sessionGeodata.sessionId)

                    this.di.sessions[sessionGeodata.sessionId] = {
                        mapObject: mapObject,
                        sessionListener: sessionListener
                    }

                    let positionUpdater = new MapObjectPositionUpdater(mapObject)
                    sessionListener.addTelemetryEventHandler(telemetry => positionUpdater.handleTelemetry(telemetry))

                    let routeRenderer = new ShowVehicleRouteHandler(this.di.map);
                    sessionListener.addAssignRequestNotificationHandler(notification => routeRenderer.handleRequestAssignment(notification))

                    this.di.map.addObject(mapObject)

                    sessionListener.connect()
                })
            })
    }
}
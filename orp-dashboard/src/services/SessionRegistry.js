import {Vehicle} from "../map/objects/Vehicle";
import {SessionListener} from "../websocket/session/SessionListener";
import {MapObjectPositionUpdater} from "../websocket/session/event_handlers/MapObjectPositionUpdater";
import * as SessionApi from "../api/SessionApi"

export class SessionRegistry {

    #map
    #sessions

    constructor(di) {
        this.#map = di.map
        this.#sessions = {}
    }

    registerSessions(sessionIds) {
        sessionIds = sessionIds.filter(sessionId => !this.#sessions[sessionId])
        if(sessionIds.length === 0) {
            console.log("nothing to register, skip")
            return
        }
        SessionApi.sessions({
            filter: {
                filtering: [
                    {
                        fieldName: "sessionId",
                        values: sessionIds
                    }
                ],
                page: 0,
                pageSize: sessionIds.length
            }
        }).then(response => {
            const sessions = response.sessions
            if (!sessions) {
                console.log(`no sessions with ids ${sessionIds}, skip registering ...`)
                return
            }
            SessionApi.geodata({
                sessionIds: sessions.map(sessionInfo => sessionInfo.sessionId)
            }).then(response => {
                if (response == null) {
                    return
                }
                const geoDatas = {}
                response?.sessions?.forEach(session => {
                    geoDatas[session.sessionId] = session
                })
                sessions.forEach(sessionInfo => {
                    if (geoDatas[sessionInfo.sessionId] == null) {
                        console.log(`cannot find geo data for session with id ${sessionInfo.sessionId}, skip registering ...`)
                        return
                    }
                    let geoData = geoDatas[sessionInfo.sessionId]
                    let mapObject = new Vehicle()
                    let sessionListener = new SessionListener(sessionInfo.sessionId)
                    this.#sessions[sessionInfo.sessionId] = {
                        mapObject: mapObject,
                        sessionListener: sessionListener
                    }
                    let positionUpdater = new MapObjectPositionUpdater(mapObject)
                    sessionListener.addTelemetryEventHandler(telemetry => positionUpdater.handleTelemetry(telemetry))
                    mapObject.setCoordinates(geoData.coordinates.latitude, geoData.coordinates.longitude)
                    this.#map.addObject(mapObject)
                    sessionListener.connect()
                    this.#sessions[sessionInfo.sessionId] = {
                        sessionListener: sessionListener,
                        mapObject: mapObject
                    }
                })
            })
        })
    }

    deregisterSessions(sessionIds) {
        sessionIds = sessionIds.filter(sessionId => this.#sessions[sessionId])
        sessionIds.map(sessionId => this.#sessions[sessionId]).forEach(session => {
            this.#map.removeObject(session.mapObject)
            session.sessionListener.disconnect()
        })
        sessionIds.forEach(sessionId => delete this.#sessions[sessionId])
    }

    registeredSessionsIds() {
        return Object.keys(this.#sessions)
    }
}
/**
 * Класс для получения событий сессии
 */
import * as NotifierApi from "../api/NotifierApi";

export class SessionListener {

    constructor(sessionId) {
        this.sessionId = sessionId
        this.stompClient = NotifierApi.getStompClient()
        this.stompClient.debug = null

        this.telemetryEventHandlers = []
        this.disconnectEventHandler = []
    }

    connect() {
        this.stompClient.connect({}, () => {
            this.stompClient.subscribe(NotifierApi.TELEMETRY_TOPIC_PATH + "/" + this.sessionId, this.#handleTelemetry.bind(this))
        })
    }

    // TODO
    disconnect() {

    }

    #handleTelemetry(telemetry) {
        this.telemetryEventHandlers.forEach(handler => handler(telemetry))
    }

    addTelemetryEventHandler(handler) {
        this.telemetryEventHandlers.push(handler)
    }

    addDisconnectEventHandler(handler) {
        this.disconnectEventHandler.push(handler)
    }
}
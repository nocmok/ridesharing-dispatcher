/**
 * Класс для получения событий сессии
 */
import * as NotifierApi from "../../api/NotifierApi";

export class SessionListener {

    constructor(sessionId) {
        this.sessionId = sessionId
        this.stompClient = NotifierApi.getStompClient()
        this.stompClient.debug = null

        this.telemetryEventHandlers = []
        this.disconnectEventHandler = []
        this.assignRequestEventHandlers = []
    }

    connect() {
        this.stompClient.connect({}, () => {
            this.stompClient.subscribe(NotifierApi.TELEMETRY_TOPIC_PATH + "/" + this.sessionId, this.#handleTelemetry.bind(this))
            this.stompClient.subscribe(NotifierApi.DRIVER_TOPIC_PATH + NotifierApi.DRIVER_API_ASSIGN_REQUEST_PATH + "/" + this.sessionId, this.#handleAssignRequestNotification.bind(this))
        })
    }

    // TODO
    disconnect() {
        this.stompClient.disconnect()
    }

    #handleTelemetry(telemetry) {
        this.telemetryEventHandlers.forEach(handler => handler(telemetry))
    }

    #handleAssignRequestNotification(assignRequestNotification) {
        this.assignRequestEventHandlers.forEach(handler => handler(assignRequestNotification))
    }

    addTelemetryEventHandler(handler) {
        this.telemetryEventHandlers.push(handler)
    }

    addAssignRequestNotificationHandler(handler) {
        this.assignRequestEventHandlers.push(handler)
    }

    addDisconnectEventHandler(handler) {
        this.disconnectEventHandler.push(handler)
    }
}
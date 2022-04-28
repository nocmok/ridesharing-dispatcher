/**
 * Класс для получения событий сессии
 */
import * as NotifierApi from "../../api/NotifierApi";

export class OrderListener {

    constructor(orderId) {
        this.orderId = orderId

        this.stompClient = NotifierApi.getStompClient()
        this.stompClient.debug = null

        this.orderStatusUpdatedHandlers = []
    }

    connect() {
        this.stompClient.connect({}, () => {
            this.stompClient.subscribe(NotifierApi.RIDER_TOPIC_PATH + NotifierApi.RIDER_API_ORDER_STATUS_PATH + "/" + this.orderId, this.#handleOrderStatusUpdated.bind(this))
        })
    }

    disconnect() {
        this.stompClient.disconnect()
    }

    #handleOrderStatusUpdated(notification) {
        this.orderStatusUpdatedHandlers.forEach(handler => handler(notification))
    }

    addOrderStatusUpdatedHandler(handler) {
        this.orderStatusUpdatedHandlers.push(handler)
    }
}
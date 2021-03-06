import SockJS from 'sockjs-client'
import Stomp from 'stomp-websocket'

export const NOTIFIER_URL = "http://localhost:8081"
export const CONNECT_PATH = "/api/notifier/connect"
export const TELEMETRY_TOPIC_PATH = "/topic/telemetry/gps"

export const DRIVER_TOPIC_PATH = "/topic/driver"
export const DRIVER_API_ASSIGN_REQUEST_PATH = "/assign_request"

export const RIDER_TOPIC_PATH = "/topic/rider"
export const RIDER_API_ORDER_STATUS_PATH = "/order_status"

export function getStompClient() {
    return Stomp.over(new SockJS(NOTIFIER_URL + CONNECT_PATH))
}
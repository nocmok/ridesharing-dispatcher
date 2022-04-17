import SockJS from 'sockjs-client'
import Stomp from 'stomp-websocket'

export const NOTIFIER_URL = "http://localhost:8081"
export const CONNECT_PATH = "/api/notifier/connect"
export const TELEMETRY_TOPIC_PATH = "/topic/telemetry/gps"

export function getStompClient() {
    return Stomp.over(new SockJS(NOTIFIER_URL + CONNECT_PATH))
}
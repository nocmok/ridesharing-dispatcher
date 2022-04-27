const API_GATEWAY_BASE_URL = "http://localhost:8080"
const GOD_API_PATH = "/god_api/v0"

function resolve(path) {
    return API_GATEWAY_BASE_URL + GOD_API_PATH + path
}

export function getActiveSessionIds() {
    return fetch(resolve("/session/active_sessions/ids"), {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: "{}"
    }).then(response => response.json());
}

export function getSessionGeodata(request) {
    return fetch(resolve("/session/geodata"), {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(request)
    }).then(response => response.json());
}

export function getSessionInfo(request) {
    return fetch(resolve("/sessions/info"), {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(request)
    }).then(response => response.json());
}

export function getRequestInfo(request) {
    return fetch(resolve("/request/info"), {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(request)
    }).then(response => response.json());
}
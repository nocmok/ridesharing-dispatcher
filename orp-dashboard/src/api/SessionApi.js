import * as GodApi from "./GodApi"

const API_GATEWAY_BASE_URL = "http://localhost:8080"
const SESSION_API_PATH = "/session_api/v0"

function resolve(path) {
    return API_GATEWAY_BASE_URL + SESSION_API_PATH + path
}

export function sessions(request) {
    console.log(JSON.stringify(request))
    return fetch(resolve("/sessions"), {
        method: "POST",
        headers: {
            'Content-Type' : 'application/json',
        },
        body: JSON.stringify(request)
    }).then(response => response.json());
}

export function geodata(request) {
    return GodApi.getSessionGeodata(request)
}

export function route(request) {
    return fetch(resolve("/route"), {
        method: "POST",
        headers: {
            'Content-Type' : 'application/json',
        },
        body: JSON.stringify(request)
    }).then(response => response.json());
}
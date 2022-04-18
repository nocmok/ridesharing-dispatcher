const API_GATEWAY_BASE_URL = "http://localhost:8080"
const RIDER_API_PATH = "/rider_api/v0"

function resolve(path) {
    return API_GATEWAY_BASE_URL + RIDER_API_PATH + path
}

export function createRequest(request) {
    return fetch(resolve("/create_service_request"), {
        method: "POST",
        headers: {
            'Content-Type' : 'application/json',
        },
        body: JSON.stringify(request)
    }).then(response => response.json());
}
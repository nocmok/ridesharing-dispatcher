const API_GATEWAY_BASE_URL = "http://localhost:8080"
const DRIVER_API_PATH = "/driver_api/v0"

function resolve(path) {
    return API_GATEWAY_BASE_URL + DRIVER_API_PATH + path
}

export function createSession(request) {
    console.log(request)
    return fetch(resolve("/create_session"), {
        method: "POST",
        headers: {
            'Content-Type' : 'application/json',
        },
        body: JSON.stringify(request)
    }).then(response => response.json());
}
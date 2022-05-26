const API_GATEWAY_BASE_URL = "http://localhost:8080"
const SESSION_API_PATH = "/session_api/v0"

function resolve(path) {
    return API_GATEWAY_BASE_URL + SESSION_API_PATH + path
}

export function sessions(request) {
    return fetch(resolve("/sessions"), {
        method: "POST",
        headers: {
            'Content-Type' : 'application/json',
        },
        body: JSON.stringify(request)
    }).then(response => response.json());
}
const API_GATEWAY_BASE_URL = "http://localhost:8080"
const ORDER_API_PATH = "/order_api/v0"

function resolve(path) {
    return API_GATEWAY_BASE_URL + ORDER_API_PATH + path
}

export function orders(request) {
    return fetch(resolve("/orders"), {
        method: "POST",
        headers: {
            'Content-Type' : 'application/json',
        },
        body: JSON.stringify(request)
    }).then(response => response.json());
}
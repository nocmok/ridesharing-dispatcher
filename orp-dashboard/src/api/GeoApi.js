const API_GATEWAY_BASE_URL = "http://localhost:8080"
const GEO_API_PATH = "/geo_api/v0"

function resolve(path) {
    return API_GATEWAY_BASE_URL + GEO_API_PATH + path
}

export function getRoadSegmentByLatLon(request) {
    return fetch(resolve("/location/road_segment"), {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(request)
    }).then(response => response.json());
}
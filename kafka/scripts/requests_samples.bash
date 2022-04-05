echo \
"{\"requestId\":\"1\",    \
\"pickupNodeId\":\"1\",   \
\"pickupLat\":\"1\",      \
\"pickupLon\":\"1\",      \
\"dropoffNodeId\":\"1\",  \
\"dropoffLat\":\"1\",     \
\"dropoffLon\":\"1\",     \
\"requestedAt\":\"2022-02-05T13:20:24.848519Z\",    \
\"detourConstraint\":\"1.3\",                       \
\"load\":\"1\"}" \
|  kcat -P -b localhost:29092 -t orp.input -H "request_type=com.nocmok.orp.orp_solver.kafka.orp_input.dto.MatchVehiclesRequest"
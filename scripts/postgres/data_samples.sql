insert into request(request_id,rider_id,pickup_node_id,pickup_lat,pickup_lon,dropoff_node_id,dropoff_lat,dropoff_lon,requested_at,detour_constraint,load)
    values(1,1,3,-7.4147388E7,4.07659E7,2,-7.4143788E7,4.077E7,now(),2,1);

insert into vehicle_session (session_id, created_at, completed_at, status, total_capacity, residual_capacity, schedule_json, route_json)
values (1,now(),null,'PENDING',2,2,'[]','[]');


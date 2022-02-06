insert into user_info values(1);
insert into user_info values(2);
insert into user_info values(3);
insert into user_info values(4);
insert into user_info values(5);
insert into user_info values(6);

insert into request(request_id,rider_id,pickup_node_id,pickup_lat,pickup_lon,dropoff_node_id,dropoff_lat,dropoff_lon,requested_at,detour_constraint,load)
    values(1,1,3,-7.4147388E7,4.07659E7,2,-7.4143788E7,4.077E7,now(),2,1);

insert into vehicle_info values(0,'u169va','Renault', 'Logan','white', 'Hatchback', 3);
insert into vehicle_info values(1,'q259fa','Lada', 'Granta', 'white','Hatchback', 4);
insert into vehicle_info values(2,'w369gd','Toyota', 'Cruise', 'white','Hatchback', 3);
insert into vehicle_info values(3,'u123wu','Porsche', 'Cayen', 'white','Hatchback', 5);
insert into vehicle_info values(4,'a623vg','Paggani', 'Zonda-F', 'white','Hatchback', 2);
insert into vehicle_info values(5,'b335vf','Porsche', 'Panamera', 'white','Hatchback', 3);

--insert into vehicle_session (vehicle_id, driver_id, created_at, completed_at, status, total_capacity, residual_capacity, schedule_json)
--    values(0,1,now(),null,'PENDING',3,3,
--'[{ "deadline" : "2022-02-04T10:53:41.393467Z", "load" : "1", "nodeId" : "1", "lat" : "123.456", "lon" : "321.654", "kind" : "PICKUP", "orderId" : "1" }]'
--);

insert into vehicle_session (session_id, vehicle_id, driver_id, created_at, completed_at, status, total_capacity, residual_capacity, schedule_json, road_start_node_id, road_start_node_lat, road_start_node_lon, road_end_node_id, road_end_node_lat, road_end_node_lon, road_cost, road_progress, lat, lon, distance_scheduled)
    values(0,0,1,now(),null,'PENDING',3,3,'[]', 4, -7.4144788E7, 4.07675E7, 0, -7.4145588E7, 4.0768E7, 873, 1, -7.4145588E7, 4.0768E7, 0);

insert into vehicle_session (session_id, vehicle_id, driver_id, created_at, completed_at, status, total_capacity, residual_capacity, schedule_json, road_start_node_id, road_start_node_lat, road_start_node_lon, road_end_node_id, road_end_node_lat, road_end_node_lon, road_cost, road_progress, lat, lon, distance_scheduled)
    values(1,1,2,now(),null,'PENDING',3,3,'[]', 5, -7.4147588E7, 4.0769E7, 1, -7.4146388E7, 4.07683E7, 1275, 1, -7.4146388E7, 4.07683E7, 0);

insert into vehicle_session (session_id, vehicle_id, driver_id, created_at, completed_at, status, total_capacity, residual_capacity, schedule_json, road_start_node_id, road_start_node_lat, road_start_node_lon, road_end_node_id, road_end_node_lat, road_end_node_lon, road_cost, road_progress, lat, lon, distance_scheduled)
    values(2,2,3,now(),null,'PENDING',3,3,'[]', 10, -7.4142588E7, 4.07713E7, 2, -7.4143788E7, 4.077E7, 1763, 1, -7.4143788E7, 4.077E7, 0);

insert into vehicle_session (session_id, vehicle_id, driver_id, created_at, completed_at, status, total_capacity, residual_capacity, schedule_json, road_start_node_id, road_start_node_lat, road_start_node_lon, road_end_node_id, road_end_node_lat, road_end_node_lon, road_cost, road_progress, lat, lon, distance_scheduled)
    values(3,3,4,now(),null,'PENDING',3,3,'[]', 11, -7.4147988E7, 4.07661E7, 3, -7.4147388E7, 4.07659E7, 552, 1, -7.4147388E7, 4.07659E7, 0);

insert into vehicle_session (session_id, vehicle_id, driver_id, created_at, completed_at, status, total_capacity, residual_capacity, schedule_json, road_start_node_id, road_start_node_lat, road_start_node_lon, road_end_node_id, road_end_node_lat, road_end_node_lon, road_cost, road_progress, lat, lon, distance_scheduled)
    values(4,4,5,now(),null,'SERVING',3,3,'[{ "deadline" : "2022-02-07T20:16:10.590791Z", "load" : "1", "nodeId" : "3", "lat" : "-7.4147388E7", "lon" : "4.07659E7", "kind" : "PICKUP", "orderId" : "1" }, { "deadline" : "2022-02-07T20:16:10.590791Z", "load" : "1", "nodeId" : "2", "lat" : "-7.4143788E7", "lon" : "4.077E7", "kind" : "DROPOFF", "orderId" : "1" }]', 11, -7.4147988E7, 4.07661E7, 3, -7.4147388E7, 4.07659E7, 552, 1, -7.4147388E7, 4.07659E7, 5472);
create sequence if not exists user_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists vehicle_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists request_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists session_id_seq increment by 1 minvalue 1 no maxvalue start 1;

drop type if exists request_status cascade;
create type request_status as enum ('PENDING', 'SERVING', 'SERVED', 'DENIED', 'CANCELLED');

drop type if exists vehicle_status cascade;
create type vehicle_status as enum ('PENDING', 'SERVING');

drop type if exists schedule_node_kind cascade;
create type schedule_node_kind as enum ('PICKUP', 'DROPOFF');

drop type if exists graph_node cascade;
create type graph_node as 
(
	node_id bigint,
	lat float8,
	lon float8
);

drop table if exists vehicle_info cascade;
create table vehicle_info
(
	vehicle_id bigint default nextval('vehicle_id_seq') primary key,
	registration_code varchar unique,
	vendor varchar,
	model varchar,
	color varchar,
	kind varchar,
	capacity bigint
);

drop table if exists user_info cascade;
create table user_info
(
	user_id bigint primary key
);

drop table if exists vehicle_session cascade;
create table vehicle_session
(
    session_id bigint primary key default nextval('session_id_seq'),
	vehicle_id bigint,
	driver_id bigint,

	created_at timestamp with time zone,
	completed_at timestamp with time zone,

	status vehicle_status default 'PENDING',
	total_capacity bigint,
	residual_capacity bigint,

    schedule_json text,

    road_start_node_id bigint,
    road_start_node_lat float8,
    road_start_node_lon float8,

    road_end_node_id bigint,
    road_end_node_lat float8,
    road_end_node_lon float8,

    road_cost float8,
    road_progress float8,

    distance_scheduled float8,

    lat float8,
    lon float8,

	constraint fk_acve_veid__vein_veid foreign key (vehicle_id) references vehicle_info(vehicle_id),
	constraint fk_acve_drid__usin_drid foreign key (driver_id) references user_info(user_id)
);

drop table if exists request cascade;
create table request
(
	request_id bigint default nextval('request_id_seq') primary key,
	rider_id bigint,
	pickup_node_id bigint,
	pickup_lat float8,
	pickup_lon float8,
	dropoff_node_id bigint,
	dropoff_lat float8,
	dropoff_lon float8,
	requested_at timestamp with time zone,
	detour_constraint float8,
	load bigint,
	status request_status default 'PENDING',

	constraint fk_re_riid__usin_usid foreign key (rider_id) references user_info(user_id)
);

drop table if exists vehicle_route cascade;
create table vehicle_route
(
    session_id bigint primary key,
    route_json text,

    constraint fk_vero_seid__vese_seid foreign key (session_id) references vehicle_session(session_id)
);

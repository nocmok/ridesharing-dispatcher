create sequence if not exists user_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists vehicle_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists request_id_seq increment by 1 minvalue 1 no maxvalue start 1;

drop type if exists request_status cascade;
create type request_status as enum ('PENDING', 'SERVING', 'SERVED', 'DENIED', 'CANCELLED');

drop type if exists vehicle_status cascade;
create type vehicle_status as enum ('PENDING', 'SERVING');

drop type if exists schedule_node_kind cascade;
create type schedule_node_kind as enum ('PICKUP', 'DROPOFF');

drop type if exists gps2 cascade;
create type gps2 as
(
	longitude float8,
	latitude float8
);

drop type if exists graph_node cascade;
create type graph_node as 
(
	node_id bigint,
	gps gps2 
);

drop type if exists schedule_node cascade;
create type schedule_node as 
(
	gps gps2, 
	request_id bigint,
	kind schedule_node_kind
);

create table if not exists vehicle_info 
(
	vehicle_id bigint default nextval('vehicle_id_seq') primary key,
	registration_code varchar unique,
	vendor varchar,
	model varchar,
	color varchar,
	kind varchar,
	capacity bigint
);

create table if not exists user_info 
(
	user_id bigint primary key
);

create table if not exists active_vehicle 
(
	vehicle_id bigint primary key,
	driver_id bigint unique,
	schedule schedule_node[],
	status vehicle_status default 'PENDING',
	total_capacity bigint,
	residual_capacity bigint,

	constraint fk_acve_veid__vein_veid foreign key (vehicle_id) references vehicle_info(vehicle_id),
	constraint fk_acve_drid__usin_drid foreign key (driver_id) references user_info(user_id)
);

create table if not exists request 
(
	request_id bigint default nextval('request_id_seq') primary key,
	user_id bigint,
	pickup_location gps2,
	dropoff_location gps2,
	requested_at timestamp with time zone,
	detour_constraint float8,
	n_riders bigint,
	status request_status default 'PENDING',

	constraint fk_re_usid__usin_usid foreign key (user_id) references user_info(user_id)
);

create table if not exists vehicle_route 
(
	vehicle_id bigint primary key,
	route graph_node[],

	constraint fk_vero_veid__vein_veid foreign key (vehicle_id) references vehicle_info(vehicle_id)
);

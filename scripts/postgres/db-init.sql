create sequence if not exists session_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists reservation_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists orp_output_outbox_seq increment by 1 minvalue 1 no maxvalue start 1;

drop type if exists vehicle_status cascade;
create type vehicle_status as enum ('PENDING', 'SERVING');

drop type if exists schedule_node_kind cascade;
create type schedule_node_kind as enum ('PICKUP', 'DROPOFF');

drop type if exists service_request_status cascade;
create type service_request_status as enum ('PENDING', 'SERVING', 'SERVED', 'DENIED', 'SERVING_DENIED');

drop table if exists vehicle_session cascade;
create table vehicle_session
(
    session_id bigint primary key default nextval('session_id_seq'),

	created_at timestamp with time zone,
	completed_at timestamp with time zone,

	status vehicle_status default 'PENDING',
	total_capacity bigint,
	residual_capacity bigint,

    schedule_json text,
    route_json text
);

drop table if exists service_request cascade;
create table service_request
(
    request_id bigint primary key not null,
    recorded_origin_latitude float8 not null,
    recorded_origin_longitude float8 not null,
    recorded_destination_latitude float8 not null,
    recorded_destination_longitude float8 not null,
    pickup_road_segment_start_node_id text not null,
    pickup_road_segment_end_node_id text not null,
    dropoff_road_segment_start_node_id text not null,
    dropoff_road_segment_end_node_id text not null,
    detour_constraint float8 not null,
    max_pickup_delay_seconds bigint not null,
    requested_at timestamp with time zone not null,
    load bigint not null,
    status service_request_status,
    serving_session_id bigint
);

drop table if exists session_route_cache cascade;
create table session_route_cache
(
    session_id bigint primary key,
    route_json text
);

drop table if exists vehicle_reservation cascade;
create table vehicle_reservation
(
    reservation_id bigint primary key default nextval('reservation_id_seq'),
    session_id bigint not null,
    request_id bigint not null,
    created_at timestamp with time zone default now(),
    expired_at timestamp with time zone,

    constraint fk_vere_seid__vese_seid foreign key (session_id) references vehicle_session(session_id)
);

drop table if exists orp_output_outbox cascade;
create table orp_output_outbox
(
    message_id bigint,
    partition_key text,
    message_kind text,
    payload text,
    created_at timestamp with time zone,
    sent_at timestamp with time zone
);

drop table if exists telemetry cascade;
create table telemetry
(
    session_id bigint not null,
    latitude float8,
    longitude float8,
    accuracy float8,
    recorded_at timestamp with time zone
);


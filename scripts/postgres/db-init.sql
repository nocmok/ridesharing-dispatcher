create sequence if not exists session_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists reservation_id_seq increment by 1 minvalue 1 no maxvalue start 1;

drop type if exists vehicle_status cascade;
create type vehicle_status as enum ('PENDING', 'SERVING');

drop type if exists schedule_node_kind cascade;
create type schedule_node_kind as enum ('PICKUP', 'DROPOFF');

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
    route_json text,
    geotag_json text
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

drop table if exists service_request_outbox cascade;
create table service_request_outbox
(
    request_id bigint not null primary key,
    session_id bigint not null,
    reservation_id bigint not null,
    sent_at timestamp with time zone,

    constraint fk_sereou_reid__vere_reid foreign key (reservation_id) references vehicle_reservation(reservation_id),
    constraint fk_sereou_seid__vese_seid foreign key (session_id) references vehicle_session(session_id)
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


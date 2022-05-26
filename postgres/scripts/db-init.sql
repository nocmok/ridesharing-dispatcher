create sequence if not exists session_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists reservation_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists orp_output_outbox_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists request_id_seq increment by 1 minvalue 1 no maxvalue start 1;

-- FROZEN - сессия больше не может принимать запросы, так как истекла.
-- Из состояния FROZEN сессия может перейти только в состояние CLOSED, когда выполнит текущий план
drop type if exists vehicle_status cascade;
create type vehicle_status as enum ('PENDING', 'SERVING', 'FROZEN', 'CLOSED');

drop type if exists schedule_node_kind cascade;
create type schedule_node_kind as enum ('PICKUP', 'DROPOFF');

-- SERVICE_PENDING - запрос отправлен, но вердикт по нему не известен
-- SERVICE_DENIED - запрос не был никем принят и отклонен
-- ACCEPTED - заказ был успешно принят
-- PICKUP_PENDING - исполнитель ожидает посадки клиента
-- SERVING - заказ в процессе выполнения
-- SERVED - заказ выполнен
-- CANCELLED - заказ отменен после того как был принят
drop type if exists service_request_status cascade;
create type service_request_status as enum ('SERVICE_PENDING', 'SERVICE_DENIED', 'ACCEPTED', 'PICKUP_PENDING', 'SERVING', 'SERVED', 'CANCELLED');

drop type if exists service_deny_reason cascade;
create type service_deny_reason as enum('NO_SUITABLE_VEHICLE');

drop table if exists vehicle_session cascade;
create table vehicle_session
(
    session_id bigint primary key default nextval('session_id_seq'),
	status vehicle_status default 'PENDING',
	total_capacity bigint,
	residual_capacity bigint,
    schedule_json text,
    started_at timestamp with time zone not null,
    terminated_at timestamp with time zone
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
    completed_at timestamp with time zone,
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

drop table if exists session_route_log cascade;
create table session_route_log
(
    session_id bigint references vehicle_session(session_id),
    route_json text,
    updated_at timestamp with time zone,

    primary key(session_id, updated_at)
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

drop table if exists order_assignment cascade;
create table order_assignment
(
    order_id bigint primary key references service_request(request_id) not null,
    session_id bigint references vehicle_session(session_id) not null,
    assigned_at timestamp with time zone not null
);

drop table if exists service_deny cascade;
create table service_deny
(
    order_id bigint primary key references service_request(request_id),
    reason service_deny_reason,
    reason_text text,
    denied_at timestamp with time zone
);

drop table if exists order_status_log cascade;
create table order_status_log
(
    order_id bigint references service_request(request_id) not null,
    status service_request_status not null,
    updated_at timestamp with time zone not null,

    primary key(order_id, updated_at)
);

drop table if exists session_status_log cascade;
create table session_status_log
(
    session_id bigint references vehicle_session(session_id),
    status vehicle_status,
    updated_at timestamp with time zone,

    primary key(session_id, updated_at)
);

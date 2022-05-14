create sequence if not exists session_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists reservation_id_seq increment by 1 minvalue 1 no maxvalue start 1;
create sequence if not exists orp_output_outbox_seq increment by 1 minvalue 1 no maxvalue start 1;

-- FROZEN - сессия больше не может принимать запросы, так как истекла.
-- Из состояния FROZEN сессия может перейти только в состояние CLOSED, когда выполнит текущий план
drop type if exists vehicle_status cascade;
create type vehicle_status as enum ('PENDING', 'SERVING', 'FROZEN', 'CLOSED');

drop type if exists schedule_node_kind cascade;
create type schedule_node_kind as enum ('PICKUP', 'DROPOFF');

drop type if exists service_request_status cascade;
-- SERVICE_PENDING - запрос отправлен, но вердикт по нему не известен
-- SERVICE_DENIED - запрос не был никем принят и отклонен
-- ACCEPTED - заказ был успешно принят
-- PICKUP_PENDING - исполнитель ожидает посадки клиента
-- SERVING - заказ в процессе выполнения
-- SERVED - заказ выполнен
-- CANCELLED - заказ отменен после того как был принят
create type service_request_status as enum ('SERVICE_PENDING', 'SERVICE_DENIED', 'ACCEPTED', 'PICKUP_PENDING', 'SERVING', 'SERVED', 'CANCELLED');

drop type if exists service_deny_reason cascade;
create type service_deny_reason as enum('NO_SUITABLE_VEHICLE');

drop table if exists vehicle_session cascade;
create table vehicle_session
(
    session_id bigint primary key default nextval('session_id_seq'),

	created_at timestamp with time zone,
	completed_at timestamp with time zone,

	status vehicle_status default 'PENDING',
	total_capacity bigint,
	residual_capacity bigint,

    schedule_json text
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

-- Для хранения причин отказа в обработке заказа
drop table if exists service_deny cascade;
create table service_deny
(
    order_id bigint primary key references service_request(request_id),
    reason service_deny_reason,
    reason_text text,
    denied_at timestamp with time zone
);

-- Для расчета времени нахождения в разных состояниях выполнения
-- По этой таблице можно рассчитать время в пути
drop table if exists order_status_log cascade;
create table order_status_log
(
    order_id bigint references service_request(request_id) not null,
    status service_request_status not null,
    updated_at timestamp with time zone not null,

    primary key(order_id, updated_at)
);

-- Как считать пройденное в рамках запроса расстояние
-- 1) Считать фактическое пройденное расстояние по телеметрии
-- 2) Считать запланированное пройденное расстояние

--Для варианта 1)
-- Использовать gps трек тс. Абстрагировать работу с треками в отдельный сервис.
-- Вынести в api сервиса возможность запросить пройденное расстояние за интервал времени

-- Для варианта 2)
-- 1) Брать историю маршрутов тс во временных рамках запроса затем мержить
-- Знаем по таблице order_status_log время пикапа и время дропофа
-- Забираем все маршруты тс, которые пересекаются по времени с полученным промежутком
-- Откусываем ненужные части от первого и последнего маршрута
-- Полученные маршруты мержим
-- 2) Для каждого заказа хранить лог построенных маршрутов
-- То есть если тс перестраивает маршрут, то для каждого запроса, ассоциированного с тс в лог добавляется запись с новым маршрутом

-- Как получать последнюю версию маршрута выполнения запроса
-- 1) Новый маршрут тс всегда строится от какой-то точки из старого маршрута
-- Значит не должно быть проблем смержить новый и старый маршрут (Но это ненадежно)
-- 2) Каким-то образом логировать посчитанные между контрольными точками маршруты
-- Тогда можно брать точки которые попадают между точками интересующего запроса и собирать маршрут по кусочкам
-- (Это надежно, но как-то запутанно и супер негибко)
-- 3) Не добавлять в api запросов получение актуального маршрута.
-- Можно сделать возможность получения пройденного маршрута постфактум, когда запрос выполнен.
-- В этом случае можно использовать модуль для работы с телеметрией тс

-- Для расчета общего времени нахождения в разных состояниях сессии
create table session_status_log
(
    session_id bigint references vehicle_session(session_id),
    status vehicle_status,
    updated_at timestamp with time zone,

    primary key(session_id, updated_at)
);

-- Как считать суммарное пройденное сессией расстояние
-- 1) Хранить весь gps трек, затем по нему делать привязку к графу и считать расстояние
-- 2) Хранить весь gps трек и затем по нему рассчитывать пройденное расстояние
-- 3) Хранить лог перемещений по графу и по нему вычислять пройденное расстояние
-- 4) Хранить лог перестроек маршрута и по нему считать пройденное расстояние

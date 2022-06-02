create extension postgis;
create extension pgRouting;

drop table if exists graph_object cascade;
create table graph_object
(
    object_id bigint primary key,
    segment_source_osm bigint,
    segment_target_osm bigint,
    latitude float8,
    longitude float8
);

drop index if exists idx_grob_sesoos cascade;
create index idx_grob_sesoos on graph_object(segment_source_osm);

drop index if exists idx__wa__soos_taos cascade;
create index idx__wa__soos_taos on ways ((source_osm::text || ':' || target_osm::text));
-- Удаление петель
delete from ways
where source_osm = target_osm;

-- Делаем все ребра для которых неизвестна ориентированность делаем неориентированными
update ways
set
	oneway = 'NO',
	one_way = 2
where one_way = 0;

-- Удаляем обратные ребра
delete from ways
where gid in (
	select t1.gid
	from ways as t1 join ways as t2
	on t1.source_osm = t2.target_osm and t1.target_osm = t2.source_osm
		and t1.source_osm != t1.target_osm
)
and source_osm > target_osm;

-- Удаление мульти-ребер
delete from ways
where gid in (
    select t1.gid from ways as t1
    join
    (
        select source_osm, target_osm, min(cost_s) as cost_s
        from ways
        group by source_osm, target_osm
    ) as t2
    on t1.source_osm = t2.source_osm
    and t1.target_osm = t2.target_osm
    and t1.cost_s <> t2.cost_s
);

-- Вставляем обратные ребра
insert into
ways(the_geom,osm_id,tag_id,length,length_m,maxspeed_backward,priority,gid,
source,target,source_osm,target_osm,cost,reverse_cost,cost_s,reverse_cost_s,one_way,x1,y1,x2,y2,maxspeed_forward,name,rule,oneway)
select the_geom,osm_id,tag_id,length,length_m,maxspeed_forward,priority,100000+gid,
       target,source,target_osm,source_osm,reverse_cost,cost,reverse_cost_s,cost_s,one_way,x2,y2,x1,y1,maxspeed_backward,name,rule,oneway
from ways
where one_way = 2;
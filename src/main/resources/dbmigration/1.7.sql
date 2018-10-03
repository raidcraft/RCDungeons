-- apply changes
create table rc_dungeons_dungeons (
  id                            integer auto_increment not null,
  name                          varchar(255) not null,
  friendly_name                 varchar(255),
  description                   varchar(255),
  reset_time_millis             bigint not null,
  locked                        tinyint(1) default 0 not null,
  constraint uq_rc_dungeons_dungeons_name unique (name),
  constraint pk_rc_dungeons_dungeons primary key (id)
);

create table rc_dungeons_dungeon_instances (
  id                            integer auto_increment not null,
  dungeon_id                    integer,
  creation_time                 datetime(6) not null,
  active                        tinyint(1) default 0 not null,
  completed                     tinyint(1) default 0 not null,
  locked                        tinyint(1) default 0 not null,
  constraint pk_rc_dungeons_dungeon_instances primary key (id)
);

create table rc_dungeons_dungeon_instance_players (
  id                            integer auto_increment not null,
  player_id                     integer,
  instance_id                   integer,
  constraint pk_rc_dungeons_dungeon_instance_players primary key (id)
);

create table rc_dungeons_dungeon_players (
  id                            integer auto_increment not null,
  player                        varchar(255),
  player_id                     varchar(40),
  last_world                    varchar(255),
  last_x                        double not null,
  last_y                        double not null,
  last_z                        double not null,
  last_yaw                      float not null,
  last_pitch                    float not null,
  constraint pk_rc_dungeons_dungeon_players primary key (id)
);

create table rc_dungeons_dungeon_spawns (
  id                            integer auto_increment not null,
  dungeon_id                    integer,
  spawn_x                       integer not null,
  spawn_y                       integer not null,
  spawn_z                       integer not null,
  spawn_yaw                     float not null,
  spawn_pitch                   float not null,
  constraint pk_rc_dungeons_dungeon_spawns primary key (id)
);

create index ix_rc_dungeons_dungeon_instances_dungeon_id on rc_dungeons_dungeon_instances (dungeon_id);
alter table rc_dungeons_dungeon_instances add constraint fk_rc_dungeons_dungeon_instances_dungeon_id foreign key (dungeon_id) references rc_dungeons_dungeons (id) on delete restrict on update restrict;

create index ix_rc_dungeons_dungeon_instance_players_player_id on rc_dungeons_dungeon_instance_players (player_id);
alter table rc_dungeons_dungeon_instance_players add constraint fk_rc_dungeons_dungeon_instance_players_player_id foreign key (player_id) references rc_dungeons_dungeon_players (id) on delete restrict on update restrict;

create index ix_rc_dungeons_dungeon_instance_players_instance_id on rc_dungeons_dungeon_instance_players (instance_id);
alter table rc_dungeons_dungeon_instance_players add constraint fk_rc_dungeons_dungeon_instance_players_instance_id foreign key (instance_id) references rc_dungeons_dungeon_instances (id) on delete restrict on update restrict;

create index ix_rc_dungeons_dungeon_spawns_dungeon_id on rc_dungeons_dungeon_spawns (dungeon_id);
alter table rc_dungeons_dungeon_spawns add constraint fk_rc_dungeons_dungeon_spawns_dungeon_id foreign key (dungeon_id) references rc_dungeons_dungeons (id) on delete restrict on update restrict;


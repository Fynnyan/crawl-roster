create table Players (
    id UUID not null primary key,
    player_name varchar(100) null,
    discord_name varchar(100) null,
    discord_id varchar(100) null,
    google_id varchar(100) null
);

create table DungeonMasters (
    id UUID not null primary key
);

create table PlayerCharacters (
    id UUID not null primary key
);

create table Events (
    id UUID not null primary key,
    event_date DATE not null
);

create table EventRegistrations (
    id UUID not null primary key,
    event_id UUID null,
    player_id UUID not null,

    foreign key (event_id) references Events(id)
);

create table GameTableOfferings (
    id UUID not null primary key,
    event_id UUID null,
    dungeon_master_id UUID not null,

    foreign key (event_id) references Events(id),
    foreign key (dungeon_master_id) references DungeonMasters(id)
);

create table GameTableRegistrations (
    id UUID not null primary key,
    table_offering_id UUID not null,
    player_id UUID not null,
    player_character_id UUID null,

    foreign key (table_offering_id) references GameTableOfferings(id),
    foreign key (player_id) references Players(id),
    foreign key (player_character_id) references PlayerCharacters(id)
);
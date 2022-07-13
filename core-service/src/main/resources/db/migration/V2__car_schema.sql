create table "car"
(
    id                  serial
        constraint car_pk
            primary key,
    mark                varchar(30)   not null,
    model               varchar(30)   not null,
    img_link            varchar(255),
    rent_price_per_hour numeric(5, 2) not null,
    book_price_per_hour numeric(5, 2) not null,
    status              varchar(40)   not null
);

alter table "car"
    owner to postgres;
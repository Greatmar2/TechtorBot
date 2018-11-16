CREATE TABLE raffle
(
  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  guild_id INT(11) NOT NULL,
  owner_id BIGINT(20) NOT NULL,
  prize VARCHAR(100) NOT NULL,
  description VARCHAR(250),
  duration SMALLINT(4),
  duration_unit VARCHAR(20),
  entrants SMALLINT(4),
  winners SMALLINT(4),
  thumb VARCHAR(150),
  image VARCHAR(150),
  channel_id BIGINT(20),
  message_id BIGINT(20),
  raffle_end TIMESTAMP,
  delete_on_end BOOLEAN
);
CREATE UNIQUE INDEX raffle_guild_id_raffle_id_uindex ON raffle (guild_id, id);

CREATE TABLE raffle_blacklist
(
  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  guild_id INT(11) NOT NULL,
  user_id BIGINT(20) NOT NULL,
  raffle_id INT NOT NULL,
  currently BOOLEAN NOT NULL
);
CREATE UNIQUE INDEX raffle_guild_id_user_id_raffle_id_uindex ON raffle_blacklist (guild_id, user_id, raffle_id);
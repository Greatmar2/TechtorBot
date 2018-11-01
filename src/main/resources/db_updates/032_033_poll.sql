CREATE TABLE poll
(
  guild_id INT(11),
  channel_id BIGINT(20),
  message TEXT,
  message_id BIGINT(20) NOT NULL,
  message_expire TIMESTAMP NOT NULL,
  single BOOLEAN NOT NULL,
  id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT
);
CREATE UNIQUE INDEX poll_guild_id_message_id_uindex ON poll (guild_id, message_id);

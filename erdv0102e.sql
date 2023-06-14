CREATE TABLE `user` (
  `id` char(36) PRIMARY KEY,
  `username` varchar(255) UNIQUE NOT NULL,
  `email` varchar(255) UNIQUE NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `profile_name` varchar(255) NOT NULL,
  `birthdate` date NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_at` timestamp NOT NULL
);

CREATE TABLE `follow` (
  `follower_id` char(36),
  `followee_id` char(36),
  `created_at` timestamp NOT NULL,
  PRIMARY KEY (`follower_id`, `followee_id`)
);

CREATE TABLE `tweet` (
  `id` char(36) PRIMARY KEY,
  `text` text NOT NULL,
  `user_id` char(36) NOT NULL,
  `created_at` timestamp NOT NULL
);

CREATE TABLE `like_tweet` (
  `tweet_id` char(36),
  `user_id` char(36),
  `created_at` timestamp NOT NULL,
  PRIMARY KEY (`tweet_id`, `user_id`)
);

ALTER TABLE `follow` ADD FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`);

ALTER TABLE `follow` ADD FOREIGN KEY (`followee_id`) REFERENCES `user` (`id`);

ALTER TABLE `tweet` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `like_tweet` ADD FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `like_tweet` ADD FOREIGN KEY (`tweet_id`) REFERENCES `tweet` (`id`);
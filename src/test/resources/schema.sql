-- Table structure for table `follow`
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
  `follower_id` char(36) NOT NULL,
  `followee_id` char(36) NOT NULL,
  `created_at` timestamp NOT NULL,
  PRIMARY KEY (`follower_id`,`followee_id`),
  KEY `followee_id` (`followee_id`),
  KEY `idx_follower_followee` (`follower_id`,`followee_id`),
  KEY `idx_followee_follower` (`followee_id`,`follower_id`),
  CONSTRAINT `follow_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`),
  CONSTRAINT `follow_ibfk_2` FOREIGN KEY (`followee_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table structure for table `like_tweet`
DROP TABLE IF EXISTS `like_tweet`;
CREATE TABLE `like_tweet` (
  `tweet_id` char(36) NOT NULL,
  `user_id` char(36) NOT NULL,
  `created_at` timestamp NOT NULL,
  PRIMARY KEY (`tweet_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `like_tweet_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `like_tweet_ibfk_2` FOREIGN KEY (`tweet_id`) REFERENCES `tweet` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table structure for table `tweet`
DROP TABLE IF EXISTS `tweet`;
CREATE TABLE `tweet` (
  `id` char(36) NOT NULL,
  `text` text NOT NULL,
  `user_id` char(36) NOT NULL,
  `created_at` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `created_at` (`created_at`),
  CONSTRAINT `tweet_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table structure for table `user`
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` char(36) NOT NULL,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `profile_name` varchar(255) NOT NULL,
  `birthdate` date NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_at` timestamp NOT NULL,
  `is_inactive` tinyint(1) NOT NULL DEFAULT '0',
  `is_celebrity` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
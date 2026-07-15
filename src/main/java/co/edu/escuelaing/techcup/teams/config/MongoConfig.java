package co.edu.escuelaing.techcup.teams.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Enables MongoDB auditing so @CreatedDate and @LastModifiedDate
 * are automatically populated on save.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}

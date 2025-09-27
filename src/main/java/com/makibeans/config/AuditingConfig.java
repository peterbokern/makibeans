package com.makibeans.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class to enable JPA Auditing in the application.
 * This allows automatic population of auditing fields such as createdBy, createdDate, lastModifiedBy, and lastModifiedDate
 * in entities that extend the Auditable base class.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class AuditingConfig {
}

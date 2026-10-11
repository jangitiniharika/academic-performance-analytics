/**
 * Mappers convert between JPA entities and DTOs (and back).
 *
 * <p>Keeping these conversions in one place means the entity never leaks into
 * the API response and sensitive fields (such as {@code passwordHash}) can never
 * be serialised by accident.
 */
package com.academic.performance.mapper;

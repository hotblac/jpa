package org.dontpanic.jpa.repository;

import org.dontpanic.jpa.entity.Star;
import org.springframework.data.repository.CrudRepository;

public interface StarRepository extends CrudRepository<Star, Long> {
}

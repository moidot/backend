package com.moim.backend.domain.space.Repository;

import com.moim.backend.domain.space.entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Groups, Long> {
}

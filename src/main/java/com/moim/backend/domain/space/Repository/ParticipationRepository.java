package com.moim.backend.domain.space.Repository;

import com.moim.backend.domain.space.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
}

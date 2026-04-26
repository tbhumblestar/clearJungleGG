package backend.domain.champion

import org.springframework.data.jpa.repository.JpaRepository

interface ChampionRepository : JpaRepository<Champion, String>

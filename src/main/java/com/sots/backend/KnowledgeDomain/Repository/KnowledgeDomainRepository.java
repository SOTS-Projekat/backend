package com.sots.backend.KnowledgeDomain.Repository;

import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KnowledgeDomainRepository extends JpaRepository<KnowledgeDomain, Long> {

    @Query("SELECT COUNT(d) > 0 FROM KnowledgeDomain d WHERE LOWER(d.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    //boolean existsByName(String name);
    List<KnowledgeDomain> findAllByProfessorId(Long id);

    KnowledgeDomain findByName(String name);

    @Query("SELECT COUNT(kd) > 0 FROM KnowledgeDomain kd WHERE kd.isReal = true AND kd.name = :realName")
    boolean existsRealKnowledgeDomainByName(String realName);

    @Modifying
    @Query("DELETE FROM KnowledgeDomain kd WHERE kd.isReal = true AND kd.name = :realName")
    void deleteRealKnowledgeDomainByName(String realName);
}

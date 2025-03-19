package com.makibeans.repository;

import com.makibeans.model.Size;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SizeRepository extends JpaRepository<Size, Long> {

    boolean existsByName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductVariant pv WHERE pv.size.id = :sizeId")
    void deleteBySizeId(@Param("sizeId") Long sizeId);

}

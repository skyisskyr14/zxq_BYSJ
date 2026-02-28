package com.sq.system.ws.repository;


import com.sq.system.ws.entity.KfConvRead;
import com.sq.system.ws.enums.ReadSide;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface KfConvReadRepository extends JpaRepository<KfConvRead, Long> {
    Optional<KfConvRead> findByConvIdAndSide(Long convId, ReadSide side);
}

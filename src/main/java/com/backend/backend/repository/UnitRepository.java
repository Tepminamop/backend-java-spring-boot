package com.backend.backend.repository;

import com.backend.backend.entity.Type;
import com.backend.backend.entity.UnitEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UnitRepository extends CrudRepository<UnitEntity, String> {
    UnitEntity findUnitEntityById(String id);
    UnitEntity deleteUnitEntityById(String id);
    List<UnitEntity> findUnitEntityByType(Type type);
}

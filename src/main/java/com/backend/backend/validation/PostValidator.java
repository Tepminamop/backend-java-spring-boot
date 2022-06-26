package com.backend.backend.validation;

import com.backend.backend.entity.Type;
import com.backend.backend.entity.UnitEntity;
import com.backend.backend.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PostValidator implements ConstraintValidator<PostConstraint, List<UnitEntity>> {
    @Autowired
    private UnitRepository unitRepository;

    @Override
    public boolean isValid(List<UnitEntity> value, ConstraintValidatorContext context) {
        try {
            Set<String> allId = new HashSet<>();
            Set<String> parentId = new HashSet<>();

            for (UnitEntity unit : value) {
                if (unitRepository.findById(unit.getId()).isPresent()) {
                    if (unitRepository.findById(unit.getId()).get().getType() != unit.getType()) {
                        return false;
                    }
                }

                if (unit.getType().equals(Type.CATEGORY) && unit.getPrice() != null) {
                    return false;
                }

                if (unit.getParentId() != null) {
                    if (!parentId.contains(unit.getParentId())) {
                        UnitEntity parent = unitRepository.findById(unit.getParentId()).get();
                        if (parent.getType() != Type.CATEGORY) {
                            return false;
                        }
                    }
                }

                if (unit.getType() == Type.OFFER) {
                    if (unit.getPrice() == null) {
                        return false;
                    }
                }

                String uuid = unit.getId();
                if (allId.contains(uuid)) {
                    return false;
                }
                else {
                    allId.add(uuid);
                    if (unit.getType() == Type.CATEGORY) {
                        parentId.add(unit.getId());
                    }
                }
            }
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }
}
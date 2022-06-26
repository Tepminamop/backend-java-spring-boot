package com.backend.backend.service;

import com.backend.backend.entity.Type;
import com.backend.backend.entity.UnitEntity;
import com.backend.backend.exception.UnitException;
import com.backend.backend.model.UnitModel;
import com.backend.backend.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UnitService {
    @Autowired
    private UnitRepository unitRepository;

    public void postUnits(List<UnitEntity> units, String updateDate) throws UnitException {
        for (UnitEntity unit : units) {
            updateDate(unit, updateDate);

            if (unitRepository.findById(unit.getId()).isPresent()) {
                if (unitRepository.findById(unit.getId()).get().getParentId() != null) {
                    minusCategoryPrice(unitRepository.findById(unit.getId()).get());
                    minusCountOffers(unitRepository.findById(unit.getId()).get());
                }

                if (unitRepository.findById(unit.getId()).get().getType() == Type.CATEGORY) {
                    updateCategory(unit, updateDate);
                    if (unit.getParentId() != null) {
                        plusCategoryPrice(unit);
                        plusCountOffers(unit);
                    }

                    continue;
                } else {
                    updateOffer(unit, updateDate);
                    if (unit.getParentId() != null) {
                        plusCategoryPrice(unit);
                        plusCountOffers(unit);
                    }

                    continue;
                }
            }

            Date date = transformDate(updateDate);
            unit.setDate(date);
            unitRepository.save(unit);
            if (unit.getParentId() != null) {
                unitRepository.findById(unit.getParentId()).get().getChildren().add(unit);
                unitRepository.save(unitRepository.findById(unit.getParentId()).get());

                if (unit.getType() == Type.OFFER && unit.getParentId() != null) {
                    plusCategoryPrice(unit);
                    plusCountOffers(unit);
                }
            }
        }
    }

    public UnitModel getUnit(String id) throws UnitException {
        UnitEntity unit;
        if (unitRepository.findById(id).isPresent()) {
            unit = unitRepository.findById(id).get();
        }
        else {
            throw new UnitException("Not found");
        }

        return UnitModel.toModel(unit);
    }

    public void deleteUnit(String id) throws UnitException {
        UnitEntity unit;
        if (unitRepository.findById(id).isPresent()) {
            unit = unitRepository.findById(id).get();
        }
        else {
            throw new UnitException("Not found");
        }

        if (unitRepository.findById(id).get().getParentId() != null) {
            unitRepository.findById(unitRepository.findById(id).get().getParentId()).get().removeChild(unit);
        }

        minusCountOffers(unit);
        minusCategoryPrice(unit);

        unitRepository.deleteById(id);
    }

    public List<UnitModel> getSales(String sdate) {
        List<UnitEntity> response = new ArrayList<>();

        Date date = transformDate(sdate);
        Long dateSeconds = (date.getTime()) / 1000;
        Long minSeconds = dateSeconds - 86400;
        for (UnitEntity unit : unitRepository.findUnitEntityByType(Type.OFFER)) {
            Long curSeconds = (unit.getDate().getTime()) / 1000;
            if (curSeconds >= minSeconds && curSeconds <= dateSeconds) {
                response.add(unit);
            }
        }

       return UnitModel.toModels(response);
    }

    public Date transformDate(String sdate) {
        TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(sdate);
        Instant instant = Instant.from(temporalAccessor);
        Date date = Date.from(instant);

        return date;
    }

    public void updateCategory(UnitEntity unit, String updateDate) {
        Date date = transformDate(updateDate);
        UnitEntity oldUnit = unitRepository.findById(unit.getId()).get();

        unit.setDate(date);
        unit.setChildren(oldUnit.getChildren());
        unit.setPrice(oldUnit.getPrice());
        unit.setCountOffers(oldUnit.getCountOffers());

        unitRepository.save(unit);
    }

    public void updateOffer(UnitEntity unit, String updateDate) {
        String id = unit.getId();
        Date date = transformDate(updateDate);

        if (!unit.getParentId().equals(unitRepository.findById(unit.getId()).get().getParentId())) {
            unitRepository.findById(unitRepository.findById(unit.getId()).get().getParentId()).get().removeChild(unitRepository.findById(unit.getId()).get());
        }

        unit.setDate(date);
        unitRepository.save(unit);

        if (unit.getParentId() != null) {
            unitRepository.findById(unit.getParentId()).get().getChildren().add(unit);
            unitRepository.save(unitRepository.findById(unit.getParentId()).get());
        }
    }

    public void updateDate(UnitEntity unit, String updateDate) {
        Date date = transformDate(updateDate);

        UnitEntity curUnit = unit;
        while (true) {
            if (curUnit.getParentId() != null) {
                unitRepository.findById(curUnit.getParentId()).get().setDate(date);
                unitRepository.save(unitRepository.findById(curUnit.getParentId()).get());

                curUnit = unitRepository.findById(curUnit.getParentId()).get();
            }
            else {
                break;
            }
        }

        if (unitRepository.findById(unit.getId()).isPresent()) {
            UnitEntity oldUnit = unitRepository.findById(unit.getId()).get();
            curUnit = oldUnit;

            while (true) {
                if (curUnit.getParentId() != null) {
                    unitRepository.findById(curUnit.getParentId()).get().setDate(date);
                    unitRepository.save(unitRepository.findById(curUnit.getParentId()).get());

                    curUnit = unitRepository.findById(curUnit.getParentId()).get();
                }
                else {
                    break;
                }
            }
        }
    }

    public void minusCategoryPrice(UnitEntity unit) {
        UnitEntity curUnit = unit;

        if (unit.getPrice() != null) {
            Long price = unit.getPrice();

            while (true) {
                if (curUnit.getParentId() != null) {
                    unitRepository.findById(curUnit.getParentId()).get().minusPrice(price);
                    unitRepository.save(unitRepository.findById(curUnit.getParentId()).get());

                    curUnit = unitRepository.findById(curUnit.getParentId()).get();
                } else {
                    break;
                }
            }
        }
    }

    public void plusCategoryPrice(UnitEntity unit) {
        UnitEntity curUnit = unit;
        if (unit.getPrice() != null) {
            Long price = unit.getPrice();
            while (true) {
                if (curUnit.getParentId() != null) {
                    unitRepository.findById(curUnit.getParentId()).get().plusPrice(price);
                    unitRepository.save(unitRepository.findById(curUnit.getParentId()).get());

                    curUnit = unitRepository.findById(curUnit.getParentId()).get();
                } else {
                    break;
                }
            }
        }
    }

    public void plusCountOffers(UnitEntity unit) {
        UnitEntity curUnit = unit;
        Long plus = unit.getType() == Type.OFFER ? 1L : unit.getCountOffers();

        while (true) {
            if (curUnit.getParentId() != null) {
                unitRepository.findById(curUnit.getParentId()).get().plusCountOffers(plus);
                unitRepository.save(unitRepository.findById(curUnit.getParentId()).get());

                curUnit = unitRepository.findById(curUnit.getParentId()).get();
            } else {
                break;
            }
        }
    }

    public void minusCountOffers(UnitEntity unit) {
        UnitEntity curUnit = unit;
        Long minus = unit.getType() == Type.OFFER ? 1L : unit.getCountOffers();

        while (true) {
            if (curUnit.getParentId() != null) {
                unitRepository.findById(curUnit.getParentId()).get().minusCountOffers(minus);
                unitRepository.save(unitRepository.findById(curUnit.getParentId()).get());

                curUnit = unitRepository.findById(curUnit.getParentId()).get();
            } else {
                break;
            }
        }
    }
}

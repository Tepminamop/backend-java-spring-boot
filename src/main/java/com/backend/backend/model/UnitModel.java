package com.backend.backend.model;

import com.backend.backend.entity.Type;
import com.backend.backend.entity.UnitEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class UnitModel {
    private String id;
    private String name;
    private String date;
    private String parentId;
    private Type type;
    private Long price;
    private List<UnitModel> children;

    public static UnitModel toModel(UnitEntity unitEntity) {
        UnitModel model = new UnitModel();
        model.setId(unitEntity.getId());
        model.setName(unitEntity.getName());
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(timeZone);
        String date = dateFormat.format(unitEntity.getDate());
        model.setDate(date);
        model.setParentId(unitEntity.getParentId());
        model.setType(unitEntity.getType());
        if (unitEntity.getType() == Type.OFFER) {
            model.setPrice(unitEntity.getPrice());
        }
        else if (unitEntity.getPrice() != null) {
            Long avg = Math.round((double)unitEntity.getPrice() / (double)unitEntity.getCountOffers());
            model.setPrice(avg);
        }
        else {
            model.setPrice(unitEntity.getPrice());
        }
        if (unitEntity.getChildren().size() > 0) {
            model.setChildren(unitEntity.getChildren().stream().map(UnitModel::toModel).collect(Collectors.toList()));
        }
        return model;
    }

    public static List<UnitModel> toModels(List<UnitEntity> units) {
        List<UnitModel> models = new ArrayList<>();

        for (UnitEntity unit : units) {
            UnitModel model = new UnitModel();
            model.setId(unit.getId());
            model.setName(unit.getName());
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(timeZone);
            String date = dateFormat.format(unit.getDate());
            model.setDate(date);
            model.setParentId(unit.getParentId());
            model.setType(unit.getType());
            if (unit.getType() == Type.OFFER) {
                model.setPrice(unit.getPrice());
            }
            else if (unit.getPrice() != null) {
                Long avg = Math.round((double)unit.getPrice() / (double)unit.getCountOffers());
                model.setPrice(avg);
            }
            else {
                model.setPrice(unit.getPrice());
            }
            model.setChildren(unit.getChildren().stream().map(UnitModel::toModel).collect(Collectors.toList()));

            models.add(model);
        }

        return models;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public List<UnitModel> getChildren() {
        return children;
    }

    public void setChildren(List<UnitModel> children) {
        this.children = children;
    }
}

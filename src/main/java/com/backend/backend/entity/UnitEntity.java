package com.backend.backend.entity;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class UnitEntity {
    @Id
    private String id;
    @Column(nullable = false)
    @NotNull
    private String name;
    @Column(nullable = false)
    private Date date;
    @Column(nullable = true)
    private String parentId;
    @Column(nullable = true)
    private Long countOffers;
    @Column(nullable = false)
    private Type type;
    @Column(nullable = true)
    private Long price;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentId")
    @Column(nullable = true)
    private List<UnitEntity> children = new ArrayList<>();

    public UnitEntity() {

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    public Long getCountOffers() {
        return countOffers;
    }

    public void setCountOffers(Long countOffers) {
        this.countOffers = countOffers;
    }

    public void minusCountOffers(Long minus) {
        if (this.countOffers == null) {
            countOffers = 0L;
        }
        this.countOffers -= minus;
    }

    public void plusCountOffers(Long plus) {
        if (this.countOffers == null) {
            countOffers = 0L;
        }
        this.countOffers += plus;
    }

    public void minusPrice(Long minus) {
        if (this.price == null) {
            price = 0L;
        }
        this.price -= minus;
    }

    public void plusPrice(Long plus) {
        if (this.price == null) {
            price = 0L;
        }
        this.price += plus;
    }

    public List<UnitEntity> getChildren() {
        return children;
    }

    public void setChildren(List<UnitEntity> children) {
        this.children = children;
    }

    public void removeChild(UnitEntity child) {
        this.children.remove(child);
    }
}

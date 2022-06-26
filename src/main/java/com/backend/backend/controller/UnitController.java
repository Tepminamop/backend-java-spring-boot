package com.backend.backend.controller;

import com.backend.backend.entity.UnitEntity;
import com.backend.backend.exception.UnitException;
import com.backend.backend.model.UnitModel;
import com.backend.backend.service.UnitService;
import com.backend.backend.validation.PostConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UnitController {
    @Autowired
    private UnitService unitService;

    @PostMapping("/imports")
    @Transactional
    public ResponseEntity postUnits(@Valid @RequestBody PostParam param) {
        try {
            unitService.postUnits(param.getItems(), param.getUpdateDate());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Validation Failed");
        }

        return ResponseEntity.ok("Success");
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity getUnit(@PathVariable String id) {
        try {
            UnitModel unit = unitService.getUnit(id);
            return ResponseEntity.ok(unit);
        }
        catch (UnitException e) {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Validation Failed");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteUnit(@PathVariable String id) {
        try {
            unitService.deleteUnit(id);
            return ResponseEntity.ok("Success");
        }
        catch (UnitException e) {
            return new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("Validation Failed");
        }
    }

    @GetMapping("/sales")
    public ResponseEntity getSales(@RequestParam String date) {
        try {
            List<UnitModel> offers = unitService.getSales(date);
            return ResponseEntity.ok(offers);
        }
        catch (Exception e) {

        }
        return ResponseEntity.badRequest().body("Validation Failed");
    }
}

@ControllerAdvice
class GeneralExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        return ResponseEntity.badRequest().body("Validation Failed");
    }
}

class PostParam{
    @PostConstraint
    private List<UnitEntity> items;
    private String updateDate;

    public List<UnitEntity> getItems() {
        return items;
    }

    public void setItems(List<UnitEntity> items) {
        this.items = items;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}

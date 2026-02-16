package com.keene.streaming.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.keene.service.ScalarService;
import com.keene.streaming.core.models.Scalar;

@RestController
@RequestMapping("/scalars")
public class ScalarController {

    private static final Logger logger = LoggerFactory.getLogger(ScalarController.class);

    private final ScalarService scalarService;

    @Autowired
    public ScalarController(ScalarService scalarService) {
        this.scalarService = scalarService;
    }

    @GetMapping
    public List<Scalar> getAllScalars() {
        logger.info("Getting all scalars");
        return scalarService.getAllScalars();
    }

    @GetMapping("/{id}")
    public Scalar getScalar(@NonNull @PathVariable Long id) {
        logger.info("Getting scalar by id: {}", id);
        Scalar scalar = scalarService.getScalar(id);
        if (scalar == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scalar not found");
        }
        return scalar;
    }

    @PostMapping
    public Scalar createScalar(@RequestBody Scalar scalar) {
        if (scalar == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Scalar parameter cannot be null");
        }
        logger.info("Creating scalar: {}", scalar);
        return scalarService.createScalar(scalar);
    }

    @PutMapping("/{id}")
    public Scalar updateScalar(@NonNull @PathVariable Long id, @RequestBody Scalar scalarParam) {
        if (scalarParam == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Scalar parameter cannot be null");
        }
        Scalar scalar = scalarService.getScalar(id);
        if (scalar == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scalar not found");
        }
        logger.info("Updating scalar by id: {}", scalar);
        scalar.setName(scalarParam.getName());
        scalar.setValue(scalarParam.getValue());
        scalar.setCreatedAt(scalar.getCreatedAt());        
        return scalarService.updateScalar(id, scalar);
    }

    @DeleteMapping("/{id}")
    public void deleteScalar(@NonNull @PathVariable Long id) {
        logger.info("Deleting scalar by id: {}", id);
        scalarService.deleteScalar(id);
    }
}

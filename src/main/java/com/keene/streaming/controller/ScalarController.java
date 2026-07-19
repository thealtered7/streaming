package com.keene.streaming.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.keene.service.ScalarService;
import com.keene.streaming.core.models.Scalar;
import com.keene.streaming.core.models.ScalarPage;
import com.keene.streaming.observability.Observability;

@RestController
@RequestMapping("/scalars")
public class ScalarController {

    private static final Logger logger = LoggerFactory.getLogger(ScalarController.class);

    private final ScalarService scalarService;
    private final Observability observability;

    public ScalarController(ScalarService scalarService, Observability observability) {
        this.scalarService = scalarService;
        this.observability = observability;
    }

    @GetMapping
    public ScalarPage getAllScalars(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "1000") int count) {
        return observability.observeHttp("scalar.list",
                Map.of("scalar.offset", Integer.toString(offset), "scalar.count", Integer.toString(count)), () -> {
            if (offset < 0 || count < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "offset and count must not be negative");
            }
            logger.info("Getting scalars with offset {} and count {}", offset, count);
            return scalarService.getAllScalars(offset, count);
        });
    }

    @GetMapping("/{id}")
    public Scalar getScalar(@NonNull @PathVariable Long id) {
        return observability.observeHttp("scalar.get", Map.of("scalar.id", id.toString()), () -> {
            logger.info("Getting scalar by id: {}", id);
            Scalar scalar = scalarService.getScalar(id);
            if (scalar == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scalar not found");
            }
            return scalar;
        });
    }

    @PostMapping
    public Scalar createScalar(@RequestBody Scalar scalar) {
        return observability.observeHttp("scalar.create", Map.of(), () -> {
            if (scalar == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Scalar parameter cannot be null");
            }
            logger.info("Creating scalar: {}", scalar);
            return scalarService.createScalar(scalar);
        });
    }

    @PutMapping("/{id}")
    public Scalar updateScalar(@NonNull @PathVariable Long id, @RequestBody Scalar scalarParam) {
        return observability.observeHttp("scalar.put", Map.of("scalar.id", id.toString()), () -> {
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
        });
    }

    @DeleteMapping("/{id}")
    public void deleteScalar(@NonNull @PathVariable Long id) {
        observability.observeHttpVoid("scalar.delete", Map.of("scalar.id", id.toString()), () -> {
            logger.info("Deleting scalar by id: {}", id);
            scalarService.deleteScalar(id);
        });
    }
}

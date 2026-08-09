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

import com.keene.service.WideService;
import com.keene.streaming.core.models.Wide;
import com.keene.streaming.core.models.WidePage;
import com.keene.streaming.observability.Observability;

@RestController
@RequestMapping("/wide")
public class WideController {

    private static final Logger logger = LoggerFactory.getLogger(WideController.class);

    private final WideService wideService;
    private final Observability observability;

    public WideController(WideService wideService, Observability observability) {
        this.wideService = wideService;
        this.observability = observability;
    }

    @GetMapping
    public WidePage getAllWides(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "1000") int count) {
        return observability.observeHttp("wide.list",
                Map.of("wide.offset", Integer.toString(offset), "wide.count", Integer.toString(count)), () -> {
            if (offset < 0 || count < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "offset and count must not be negative");
            }
            logger.info("Getting wide rows with offset {} and count {}", offset, count);
            return wideService.getAllWides(offset, count);
        });
    }

    @GetMapping("/{id}")
    public Wide getWide(@NonNull @PathVariable Long id) {
        return observability.observeHttp("wide.get", Map.of("wide.id", id.toString()), () -> {
            logger.info("Getting wide by id: {}", id);
            Wide wide = wideService.getWide(id);
            if (wide == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wide not found");
            }
            return wide;
        });
    }

    @PostMapping
    public Wide createWide(@RequestBody Wide wide) {
        return observability.observeHttp("wide.create", Map.of(), () -> {
            if (wide == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wide parameter cannot be null");
            }
            logger.info("Creating wide");
            return wideService.createWide(wide);
        });
    }

    @PutMapping("/{id}")
    public Wide updateWide(@NonNull @PathVariable Long id, @RequestBody Wide wideParam) {
        return observability.observeHttp("wide.put", Map.of("wide.id", id.toString()), () -> {
            if (wideParam == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wide parameter cannot be null");
            }
            Wide wide = wideService.getWide(id);
            if (wide == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wide not found");
            }
            logger.info("Updating wide by id: {}", id);
            wide.setA(wideParam.getA());
            wide.setB(wideParam.getB());
            wide.setC(wideParam.getC());
            wide.setD(wideParam.getD());
            wide.setE(wideParam.getE());
            wide.setF(wideParam.getF());
            wide.setG(wideParam.getG());
            wide.setH(wideParam.getH());
            wide.setI(wideParam.getI());
            wide.setJ(wideParam.getJ());
            wide.setK(wideParam.getK());
            wide.setL(wideParam.getL());
            wide.setM(wideParam.getM());
            wide.setN(wideParam.getN());
            wide.setO(wideParam.getO());
            wide.setP(wideParam.getP());
            wide.setQ(wideParam.getQ());
            wide.setR(wideParam.getR());
            wide.setS(wideParam.getS());
            wide.setT(wideParam.getT());
            wide.setU(wideParam.getU());
            wide.setV(wideParam.getV());
            wide.setW(wideParam.getW());
            wide.setX(wideParam.getX());
            wide.setY(wideParam.getY());
            wide.setZ(wideParam.getZ());
            wide.setCreatedAt(wide.getCreatedAt());
            return wideService.updateWide(id, wide);
        });
    }

    @DeleteMapping("/{id}")
    public void deleteWide(@NonNull @PathVariable Long id) {
        observability.observeHttpVoid("wide.delete", Map.of("wide.id", id.toString()), () -> {
            logger.info("Deleting wide by id: {}", id);
            wideService.deleteWide(id);
        });
    }
}

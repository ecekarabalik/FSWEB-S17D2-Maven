package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;     // DİKKAT: jakarta.*
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/developers")  // context-path ile: /workintech/developers
public class DeveloperController {

    private final Taxable taxable;                 // DI ile DeveloperTax gelir
    public Map<Integer, Developer> developers;    // in-memory repo

    public DeveloperController(Taxable taxable) {  // ctor injection
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new ConcurrentHashMap<>();
        developers.put(1, new JuniorDeveloper(1, "Alice",
                net(10000d, taxable.getSimpleTaxRate())));
        developers.put(2, new MidDeveloper(2, "Bob",
                net(20000d, taxable.getMiddleTaxRate())));
        developers.put(3, new SeniorDeveloper(3, "Cem",
                net(30000d, taxable.getUpperTaxRate())));
    }

    private double net(double gross, double ratePct) {
        return gross - (gross * ratePct / 100.0);
    }

    // [GET] /workintech/developers
    @GetMapping
    public List<Developer> findAll() {
        return new ArrayList<>(developers.values());
    }

    // [GET] /workintech/developers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Developer> findById(@PathVariable Integer id) {
        Developer dev = developers.get(id);
        return dev != null ? ResponseEntity.ok(dev) : ResponseEntity.notFound().build();
    }

    // [POST] /workintech/developers
    @PostMapping
    public ResponseEntity<Developer> create(@RequestBody Developer body) {
        if (body.getId() == null || body.getName() == null || body.getName().isBlank()
                || body.getSalary() == null || body.getExperience() == null) {
            return ResponseEntity.badRequest().build();
        }
        Developer created;
        switch (body.getExperience()) {
            case JUNIOR -> created = new JuniorDeveloper(body.getId(), body.getName(),
                    net(body.getSalary(), taxable.getSimpleTaxRate()));
            case MID -> created = new MidDeveloper(body.getId(), body.getName(),
                    net(body.getSalary(), taxable.getMiddleTaxRate()));
            case SENIOR -> created = new SeniorDeveloper(body.getId(), body.getName(),
                    net(body.getSalary(), taxable.getUpperTaxRate()));
            default -> throw new IllegalStateException("Unknown exp: " + body.getExperience());
        }
        developers.put(created.getId(), created);
        return ResponseEntity.ok(created);  // 200 OK (testler genelde bunu bekler)
    }

    // [PUT] /workintech/developers/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Developer> update(@PathVariable Integer id, @RequestBody Developer body) {
        if (!developers.containsKey(id)) return ResponseEntity.notFound().build();
        if (body.getName() == null || body.getName().isBlank()
                || body.getSalary() == null || body.getExperience() == null) {
            return ResponseEntity.badRequest().build();
        }

        Developer updated;
        switch (body.getExperience()) {
            case JUNIOR -> updated = new JuniorDeveloper(id, body.getName(),
                    net(body.getSalary(), taxable.getSimpleTaxRate()));
            case MID -> updated = new MidDeveloper(id, body.getName(),
                    net(body.getSalary(), taxable.getMiddleTaxRate()));
            case SENIOR -> updated = new SeniorDeveloper(id, body.getName(),
                    net(body.getSalary(), taxable.getUpperTaxRate()));
            default -> throw new IllegalStateException();
        }
        developers.put(id, updated);
        return ResponseEntity.ok(updated);
    }

    // [DELETE] /workintech/developers/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return developers.remove(id) != null ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
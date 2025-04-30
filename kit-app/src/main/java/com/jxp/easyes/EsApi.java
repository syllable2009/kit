package com.jxp.easyes;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jxp.easyes.domain.EsProduct;
import com.jxp.easyes.service.EsProductService;

/**
 * @author jiaxiaopeng
 * Created on 2025-04-30 11:28
 */

@RestController
public class EsApi {

    @Resource
    private EsProductService esProductService;

    @GetMapping("/add")
    public ResponseEntity<EsProduct> messageCallback(@RequestParam Long id) {
        final EsProduct esProduct = esProductService.create(id);
        return ResponseEntity.ok(esProduct);
    }
}

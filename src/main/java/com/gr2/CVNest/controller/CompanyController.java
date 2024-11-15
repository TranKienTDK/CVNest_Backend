package com.gr2.CVNest.controller;

import com.gr2.CVNest.dto.request.ReqCreateCompanyDTO;
import com.gr2.CVNest.dto.response.ResultPaginationDTO;
import com.gr2.CVNest.entity.Company;
import com.gr2.CVNest.service.CompanyService;
import com.gr2.CVNest.util.annotation.ApiMessage;
import com.gr2.CVNest.util.error.CompanyNotFoundException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // CREATE COMPANY
    @PostMapping("/companies")
    @ApiMessage("Create company success")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody ReqCreateCompanyDTO req) {
        Company newCompany = this.companyService.handleCreateCompany(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    // GET ALL COMPANIES
    @GetMapping("/companies")
    @ApiMessage("Get all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification<Company> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.companyService.handleGetAllCompanies(spec, pageable));
    }

    // GET A COMPANY
    @GetMapping("/companies/{companyId}")
    @ApiMessage("Get a company")
    public ResponseEntity<Company> getOneCompany(@PathVariable("companyId") Long companyId) throws CompanyNotFoundException {
        if (this.companyService.handleFindById(companyId) == null) {
            throw new CompanyNotFoundException("Company not found");
        }
        Company resCompany = this.companyService.handleGetACompany(companyId);
        return ResponseEntity.ok().body(resCompany);
    }

    // UPDATE COMPANY
    @PutMapping("/companies")
    @ApiMessage("Update company success")
    public ResponseEntity<?> updateCompany(@Valid @RequestBody Company company) throws CompanyNotFoundException {
        Company currentCompany = this.companyService.handleUpdateCompany(company);
        if (currentCompany == null) {
            throw new CompanyNotFoundException("Company not found");
        }
        return ResponseEntity.ok().body(currentCompany);
    }

    // DELETE COMPANY
    @DeleteMapping("/companies/{companyId}")
    @ApiMessage("Delete company success")
    public ResponseEntity<Void> deleteCompany(@PathVariable("companyId") Long companyId) throws CompanyNotFoundException {
        if (this.companyService.handleFindById(companyId) == null) {
            throw new CompanyNotFoundException("Company not found");
        }
        this.companyService.handleDeleteCompany(companyId);
        return ResponseEntity.ok().body(null);
    }
}

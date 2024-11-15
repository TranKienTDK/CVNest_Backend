package com.gr2.CVNest.service;

import com.gr2.CVNest.dto.request.ReqCreateCompanyDTO;
import com.gr2.CVNest.dto.response.ResultPaginationDTO;
import com.gr2.CVNest.entity.Company;
import com.gr2.CVNest.entity.User;
import com.gr2.CVNest.repository.CompanyRepository;
import com.gr2.CVNest.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleFindById(Long id) {
        Optional<Company> company = companyRepository.findById(id);
        return company.orElse(null);
    }

    public Company handleCreateCompany(ReqCreateCompanyDTO req) {
        Company company = new Company();
        company.setName(req.getName());
        company.setAddress(req.getAddress());
        company.setDescription(req.getDescription());
        company.setLogo(req.getLogo());
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());

        result.setMeta(mt);
        result.setResult(pageCompany.getContent());

        return result;
    }

    public Company handleGetACompany(long id) {
        return this.companyRepository.findById(id).orElse(null);
    }

    public Company handleUpdateCompany(Company reqCompany) {
        Optional<Company> curCompany = this.companyRepository.findById(reqCompany.getId());
        if (curCompany.isPresent()) {
            curCompany.get().setName(reqCompany.getName());
            curCompany.get().setDescription(reqCompany.getDescription());
            curCompany.get().setAddress(reqCompany.getAddress());
            curCompany.get().setLogo(reqCompany.getLogo());

            this.companyRepository.save(curCompany.get());
            return curCompany.get();
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        Optional<Company> comOptional = this.companyRepository.findById(id);
        if (comOptional.isPresent()) {
            Company com = comOptional.get();
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }

        this.companyRepository.deleteById(id);
    }
}

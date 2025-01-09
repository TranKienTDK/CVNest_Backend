package com.gr2.CVNest.service;

import com.gr2.CVNest.dto.response.ResCreateJobDTO;
import com.gr2.CVNest.dto.response.ResFetchJobDTO;
import com.gr2.CVNest.dto.response.ResUpdateJobDTO;
import com.gr2.CVNest.dto.response.ResultPaginationDTO;
import com.gr2.CVNest.entity.Company;
import com.gr2.CVNest.entity.Job;
import com.gr2.CVNest.entity.Skill;
import com.gr2.CVNest.repository.CompanyRepository;
import com.gr2.CVNest.repository.JobRepository;
import com.gr2.CVNest.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public Job handleGetJobById(Long id) {
        Optional<Job> jobOptional = this.jobRepository.findById(id);
        return jobOptional.orElse(null);
    }

    public ResFetchJobDTO convertJobToDTO(Job reqJob) {
        ResFetchJobDTO resFetchJobDTO = new ResFetchJobDTO();

        resFetchJobDTO.setId(reqJob.getId());
        resFetchJobDTO.setName(reqJob.getName());
        resFetchJobDTO.setDescription(reqJob.getDescription());
        resFetchJobDTO.setLevel(reqJob.getLevel());
        resFetchJobDTO.setLocation(reqJob.getLocation());
        resFetchJobDTO.setQuantity(reqJob.getQuantity());
        resFetchJobDTO.setSalary(reqJob.getSalary());
        resFetchJobDTO.setStartDate(reqJob.getStartDate());
        resFetchJobDTO.setEndDate(reqJob.getEndDate());
        resFetchJobDTO.setActive(reqJob.isActive());
        resFetchJobDTO.setCreatedAt(reqJob.getCreatedAt());
        resFetchJobDTO.setUpdatedAt(reqJob.getUpdatedAt());

        if (reqJob.getSkills() != null) {
            List<String> skills = reqJob.getSkills()
                    .stream().map(Skill::getName)
                    .collect(Collectors.toList());
            resFetchJobDTO.setSkills(skills);
        }

        return resFetchJobDTO;
    }

    // CREATE JOB
    public ResCreateJobDTO handleCreateJob(Job reqJob) {
        if (reqJob.getSkills() != null) {
            List<Long> reqSkills = reqJob.getSkills()
                    .stream().map(Skill::getId)
                    .toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            reqJob.setSkills(dbSkills);
        }

        // check company
        if (reqJob.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(reqJob.getCompany().getId());
            cOptional.ifPresent(reqJob::setCompany);
        }

        Job curJob = this.jobRepository.save(reqJob);

        ResCreateJobDTO resJob = new ResCreateJobDTO();

        resJob.setId(curJob.getId());
        resJob.setName(curJob.getName());
        resJob.setLocation(curJob.getLocation());
        resJob.setDescription(curJob.getDescription());
        resJob.setQuantity(curJob.getQuantity());
        resJob.setSalary(curJob.getSalary());
        resJob.setStartDate(curJob.getStartDate());
        resJob.setEndDate(curJob.getEndDate());
        resJob.setActive(curJob.isActive());

        if (curJob.getSkills() != null) {
            List<String> skills = curJob.getSkills()
                    .stream().map(Skill::getName)
                    .collect(Collectors.toList());
            resJob.setSkills(skills);
        }
        return resJob;
    }

    // GET ALL JOBS
    public ResultPaginationDTO handleGetAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageJob.getContent());

        return rs;
    }

    // UPDATE A JOB
    public ResUpdateJobDTO handleUpdateJob(Job reqJob, Job jobInDB) {
        if (reqJob.getSkills() != null) {
            List<Long> reqSkills = reqJob.getSkills()
                    .stream().map(Skill::getId)
                    .toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDB.setSkills(dbSkills);
        }

        // Check company
        if (reqJob.getCompany() != null) {
            Optional<Company> companyOptional = this.companyRepository.findById(reqJob.getCompany().getId());
            companyOptional.ifPresent(jobInDB::setCompany);
        }

        jobInDB.setName(reqJob.getName());
        jobInDB.setDescription(reqJob.getDescription());
        jobInDB.setSalary(reqJob.getSalary());
        jobInDB.setQuantity(reqJob.getQuantity());
        jobInDB.setLocation(reqJob.getLocation());
        jobInDB.setLevel(reqJob.getLevel());
        jobInDB.setStartDate(reqJob.getStartDate());
        jobInDB.setEndDate(reqJob.getEndDate());
        jobInDB.setActive(reqJob.isActive());

        Job curJob = this.jobRepository.save(reqJob);

        ResUpdateJobDTO resJob = new ResUpdateJobDTO();

        resJob.setId(curJob.getId());
        resJob.setName(curJob.getName());
        resJob.setLocation(curJob.getLocation());
        resJob.setDescription(curJob.getDescription());
        resJob.setQuantity(curJob.getQuantity());
        resJob.setSalary(curJob.getSalary());
        resJob.setStartDate(curJob.getStartDate());
        resJob.setEndDate(curJob.getEndDate());
        resJob.setActive(curJob.isActive());

        if (curJob.getSkills() != null) {
            List<String> skills = curJob.getSkills()
                    .stream().map(Skill::getName)
                    .collect(Collectors.toList());
            resJob.setSkills(skills);
        }
        return resJob;
    }

    // DELETE JOB
    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }
}

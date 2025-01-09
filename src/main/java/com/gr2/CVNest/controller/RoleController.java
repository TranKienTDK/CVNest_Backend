package com.gr2.CVNest.controller;

import com.gr2.CVNest.dto.response.ResultPaginationDTO;
import com.gr2.CVNest.entity.Role;
import com.gr2.CVNest.service.RoleService;
import com.gr2.CVNest.util.annotation.ApiMessage;
import com.gr2.CVNest.util.error.EntityNotFoundException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role r) throws EntityNotFoundException {
        // check name
        if (this.roleService.existByName(r.getName())) {
            throw new EntityNotFoundException("Role with name = " + r.getName() + " existed");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(r));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role r) throws EntityNotFoundException {
        // check id
        if (this.roleService.fetchById(r.getId()) == null) {
            throw new EntityNotFoundException("Role with id = " + r.getId() + " does not exist");
        }

        return ResponseEntity.ok().body(this.roleService.update(r));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws EntityNotFoundException {
        // check id
        if (this.roleService.fetchById(id) == null) {
            throw new EntityNotFoundException("Role with id = " + id + " does not exist");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> getPermissions(
            @Filter Specification<Role> spec, Pageable pageable) {

        return ResponseEntity.ok(this.roleService.getRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id) throws EntityNotFoundException {
        Role role = this.roleService.fetchById(id);
        if (role == null) {
            throw new EntityNotFoundException("Role with id = " + id + " does not exist");
        }
        return ResponseEntity.ok(role);
    }
}

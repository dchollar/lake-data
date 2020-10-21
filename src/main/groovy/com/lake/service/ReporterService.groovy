package com.lake.service

import com.lake.dto.ReporterDto
import com.lake.entity.Reporter
import com.lake.entity.ReporterRole
import com.lake.entity.RoleType
import com.lake.repository.ReporterRepository
import com.lake.util.ConverterUtil
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class ReporterService {
    @Autowired
    ReporterRepository repository

    @Autowired
    PasswordEncoder passwordEncoder

    @Cacheable('ReporterByUsername')
    Reporter getReporter() {
        return repository.findByUsername(SecurityContextHolder?.getContext()?.authentication?.name)
    }

    @Secured('ROLE_ADMIN')
    Collection<ReporterDto> getAllReporters() {
        ConverterUtil.convertReportersForMaintenance(repository.findAll())
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['ReporterByUsername'], allEntries = true)
    ReporterDto save(ReporterDto dto) {
        Reporter entity = ConverterUtil.convert(dto, new Reporter())
        entity.password  = passwordEncoder.encode(dto.password)
        ConverterUtil.convertForMaintenance(repository.saveAndFlush(entity))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['ReporterByUsername'], allEntries = true)
    ReporterDto update(Integer id, ReporterDto dto) {
        Reporter reporter = repository.getOne(id)
        // if there is a password, then it was changed. otherwise it would be blank
        String password = StringUtils.stripToNull(dto.password)
        if (password) {
            reporter.password = passwordEncoder.encode(password)
        }
        ConverterUtil.convert(dto, reporter)

        syncRoles(reporter, RoleType.ROLE_REPORTER, dto.roleReporter)
        syncRoles(reporter, RoleType.ROLE_POWER_USER, dto.rolePowerUser)
        syncRoles(reporter, RoleType.ROLE_ADMIN, dto.roleAdmin)

        ConverterUtil.convertForMaintenance(repository.saveAndFlush(reporter))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['ReporterByUsername'], allEntries = true)
    void delete(Integer id) {
        repository.deleteById(id)
    }

    private static void syncRoles(Reporter reporter, RoleType roleType, boolean add) {
        boolean exists = reporter.roles.any { it.role == roleType }
        if (add && !exists) {
            reporter.roles.add(new ReporterRole(reporter: reporter, role: roleType))
        } else if (!add && exists) {
            for (ReporterRole role : reporter.roles) {
                if (role.role == roleType) {
                    reporter.roles.removeElement(role)
                    break
                }
            }
        }
    }

}

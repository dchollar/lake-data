package com.lake.service

import com.lake.dto.ReporterDto
import com.lake.entity.Reporter
import com.lake.entity.ReporterRole
import com.lake.entity.RoleType
import com.lake.repository.ReporterRepository
import com.lake.util.ConverterUtil
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@CompileStatic
@Service
class ReporterService implements UserDetailsService {
    @Autowired
    ReporterRepository repository

    @Autowired
    PasswordEncoder passwordEncoder

    static String getUsername() {
        return SecurityContextHolder?.getContext()?.authentication?.name
    }

    @Override
    UserDetails loadUserByUsername(final String username) {
        Reporter reporter = this.getReporter(username)
        if (reporter) {
            List<SimpleGrantedAuthority> authorities = reporter.roles.collect {
                new SimpleGrantedAuthority(it.role.name())
            }
            return new User(
                    reporter.username,
                    reporter.password,
                    reporter.enabled,
                    true,
                    true,
                    true,
                    authorities)
        } else {
            return null
        }
    }

    @Cacheable('reportersByName')
    Reporter getReporter(final String userName) {
        if (userName) {
            return repository.findByUsername(userName)
        } else {
            return null
        }
    }

    @Secured('ROLE_ADMIN')
    @Cacheable('reporters')
    Collection<ReporterDto> getAllReporters() {
        ConverterUtil.convertReportersForMaintenance(repository.findAll())
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['reporters', 'reportersByName'], allEntries = true)
    @Transactional
    ReporterDto save(final ReporterDto dto) {
        Reporter entity = ConverterUtil.convert(dto, new Reporter())
        entity.password = passwordEncoder.encode(dto.password)
        ConverterUtil.convertForMaintenance(repository.saveAndFlush(entity))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['reporters', 'reportersByName'], allEntries = true)
    @Transactional
    ReporterDto update(final Integer id, final ReporterDto dto) {
        Reporter reporter = repository.getReferenceById(id)
        // if there is a password, then it was changed. otherwise it would be blank
        String password = StringUtils.stripToNull(dto.password)
        if (password) {
            reporter.password = passwordEncoder.encode(password)
        }
        ConverterUtil.convert(dto, reporter)

        syncRoles(reporter, RoleType.ROLE_REPORTER, dto.roleReporter)
        syncRoles(reporter, RoleType.ROLE_POWER_USER, dto.rolePowerUser)
        syncRoles(reporter, RoleType.ROLE_DOCUMENT_ADMIN, dto.roleDocumentAdmin)
        syncRoles(reporter, RoleType.ROLE_ADMIN, dto.roleAdmin)

        ConverterUtil.convertForMaintenance(repository.saveAndFlush(reporter))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['reporters', 'reportersByName'], allEntries = true)
    @Transactional
    void delete(final Integer id) {
        repository.deleteById(id)
    }

    private static void syncRoles(final Reporter reporter, final RoleType roleType, final boolean add) {
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

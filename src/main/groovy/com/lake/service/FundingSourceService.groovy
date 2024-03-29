package com.lake.service

import com.lake.dto.FundingSourceDto
import com.lake.entity.FundingSource
import com.lake.repository.FundingSourceRepository
import com.lake.util.ConverterUtil
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@CompileStatic
@Service
class FundingSourceService {
    @Autowired
    FundingSourceRepository repository

    @Cacheable('fundingSources')
    Collection<FundingSourceDto> getAll() {
        ConverterUtil.convertFundingSources(repository.findAll())
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['fundingSources'], allEntries = true)
    @Transactional
    FundingSourceDto save(final FundingSourceDto dto) {
        FundingSource fundingSource = ConverterUtil.convert(dto, new FundingSource())
        ConverterUtil.convert(repository.saveAndFlush(fundingSource))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['fundingSources', 'fundingSourceById'], allEntries = true)
    @Transactional
    FundingSourceDto update(final Integer id, final FundingSourceDto dto) {
        FundingSource fundingSource = repository.getReferenceById(id)
        ConverterUtil.convert(dto, fundingSource)
        ConverterUtil.convert(repository.saveAndFlush(fundingSource))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['fundingSources', 'fundingSourceById'], allEntries = true)
    @Transactional
    void delete(final Integer id) {
        repository.deleteById(id)
    }

    @Cacheable('fundingSourceById')
    FundingSource getOne(final Integer id) {
        if (id && id > 0) {
            return repository.findById(id).orElse(null)
        } else {
            return null
        }
    }

}

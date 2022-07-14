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
    FundingSourceDto save(FundingSourceDto dto) {
        FundingSource fundingSource = ConverterUtil.convert(dto, new FundingSource())
        ConverterUtil.convert(repository.saveAndFlush(fundingSource))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['fundingSources', 'fundingSourceById'], allEntries = true)
    FundingSourceDto update(Integer id, FundingSourceDto dto) {
        FundingSource fundingSource = repository.getReferenceById(id)
        ConverterUtil.convert(dto, fundingSource)
        ConverterUtil.convert(repository.saveAndFlush(fundingSource))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['fundingSources', 'fundingSourceById'], allEntries = true)
    void delete(Integer id) {
        repository.deleteById(id)
    }

    @Cacheable('fundingSourceById')
    FundingSource getOne(Integer id) {
        if (id && id > 0) {
            return repository.findById(id).orElse(null)
        } else {
            return null
        }
    }

}

package com.lake.service

import com.lake.dto.FundingSourceDto
import com.lake.entity.FundingSource
import com.lake.repository.FundingSourceRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service

@Service
class FundingSourceService {
    @Autowired
    FundingSourceRepository repository

    @Cacheable('fundingSources')
    Set<FundingSourceDto> getAll() {
        ConverterUtil.convertFundingSources(repository.findAll())
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['fundingSources'], allEntries = true)
    FundingSourceDto save(FundingSourceDto dto) {
        FundingSource fundingSource = ConverterUtil.convert(dto, new FundingSource())
        ConverterUtil.convert(repository.saveAndFlush(fundingSource))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['fundingSources'], allEntries = true)
    FundingSourceDto update(Integer id, FundingSourceDto dto) {
        FundingSource fundingSource = repository.getById(id)
        ConverterUtil.convert(dto, fundingSource)
        ConverterUtil.convert(repository.saveAndFlush(fundingSource))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['fundingSources'], allEntries = true)
    void delete(Integer id) {
        repository.deleteById(id)
    }

}

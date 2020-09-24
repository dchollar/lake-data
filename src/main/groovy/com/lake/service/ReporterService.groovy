package com.lake.service

import com.lake.dto.ReporterDto
import com.lake.repository.ReporterRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service

@Service
class ReporterService {
    @Autowired
    ReporterRepository repository

    @Secured('ROLE_ADMIN')
    Collection<ReporterDto> getAllReporters() {
        ConverterUtil.convertReportersForMaintenance(repository.findAll())
    }
}

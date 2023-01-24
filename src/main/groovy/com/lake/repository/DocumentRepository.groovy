package com.lake.repository


import com.lake.entity.Document
import com.lake.entity.Site
import groovy.transform.CompileStatic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

@CompileStatic
interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Query(value = 'select * from document d where d.site_id=?1', nativeQuery = true)
    List<Document> findBySite(Integer siteId)

    @Query(value = 'select * from document d where d.site_id=?1 and d.text REGEXP ?2', nativeQuery = true)
    List<Document> findBySearchWord(Integer siteId, String word)

    @Query(value = 'select * from document d where d.site_id=?1 and upper(d.path) REGEXP ?2', nativeQuery = true)
    List<Document> findByCategory(Integer siteId, String category)

    @Query(value = 'select * from document d where d.site_id=?1 and upper(d.path) REGEXP ?2 and d.text REGEXP ?3', nativeQuery = true)
    List<Document> findBySearchWordAndCategory(Integer siteId, String category, String word)

}

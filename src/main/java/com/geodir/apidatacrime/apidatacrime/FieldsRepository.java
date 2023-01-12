package com.geodir.apidatacrime.apidatacrime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FieldsRepository extends CrudRepository<FieldsEntity,Long> {

    @Query("SELECT s FROM FieldsEntity s where s.enabled <> false")
    public List<FieldsEntity> findAllFilesEnabled();
}

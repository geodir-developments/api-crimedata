package com.geodir.apidatacrime.apidatacrime;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class Test {
    @Autowired
    static
    FieldsRepository fieldsRepository;

    public static void main(String[] args) {
        List<FieldsEntity> fieldsEntityList= (List<FieldsEntity>) fieldsRepository.findAllFilesEnabled();
        System.out.println(fieldsEntityList);
    }
}

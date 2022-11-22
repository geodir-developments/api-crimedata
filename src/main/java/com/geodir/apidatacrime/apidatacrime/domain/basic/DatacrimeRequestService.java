package com.geodir.apidatacrime.apidatacrime.domain.basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatacrimeRequestService {
    @Autowired
    private DatacrimeRequestRepository datacrimeRequestRepository;

    public DatacrimeRequest save(DatacrimeRequest datacrimeRequest){
        return datacrimeRequestRepository.save(datacrimeRequest);
    }
}

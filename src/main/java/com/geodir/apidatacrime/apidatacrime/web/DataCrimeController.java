package com.geodir.apidatacrime.apidatacrime.web;

import com.geodir.apidatacrime.apidatacrime.domain.basic.DatacrimeRequest;
import com.geodir.apidatacrime.apidatacrime.domain.basic.DatacrimeRequestService;
import com.geodir.apidatacrime.apidatacrime.domain.searchbyfields.DatacrimeGroup;
import com.geodir.apidatacrime.apidatacrime.domain.searchbyfields.ResponseDatacrimeByFields;
import com.geodir.apidatacrime.apidatacrime.domain.security.UserServiceOauth2;
import com.geodir.apidatacrime.apidatacrime.domain.services.DatacrimeServiceByFields;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("v1")
@AllArgsConstructor
public class DataCrimeController {
    private DatacrimeRequestService datacrimeRequestService;
    private final DatacrimeServiceByFields datacrimeServiceByFields;

    private final UserServiceOauth2 userServiceOauth2;
    @GetMapping("/json")
    public ResponseEntity<ResponseDatacrimeByFields> getDatacrimeInformationOfLatlonAndFields(

            @RequestParam String latlon,
            @RequestParam("key") String key,
            @RequestParam(required = false) String info,

            HttpServletRequest request) {


        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        //identificar usuario
        String account = userServiceOauth2.getUserAccountByKey(key);

        List<DatacrimeGroup> datacrimeGroupList = datacrimeServiceByFields
                .search(latlon);
        String grid_code ="";
        ResponseDatacrimeByFields responseDatacrimeByFields = new ResponseDatacrimeByFields();
        if(datacrimeGroupList.size()>0){
            grid_code = datacrimeGroupList.get(0).getListDatacrimeFields().get(0).getValue().toString();

            responseDatacrimeByFields.setStatus("OK");
            responseDatacrimeByFields.setDatacrimeGroupList(datacrimeGroupList);
        }else {
            responseDatacrimeByFields.setStatus("ZERO_RESULTS");
            responseDatacrimeByFields.setDatacrimeGroupList(datacrimeGroupList);
        }

        //Guardar en db
        String[] arrayLatlon = latlon.split(",");
        String latitude = arrayLatlon[0];
        String longitude = arrayLatlon[1];
        saveDatacrimeRequest(latitude, longitude, remoteAddr, info, grid_code);


        return new ResponseEntity<>(responseDatacrimeByFields, HttpStatus.OK);
    }
    public void saveDatacrimeRequest(String latitud, String longitud, String ipRequest, String info, String grid_code) {

        DatacrimeRequest datacrimeRequest = new DatacrimeRequest();
        datacrimeRequest.setLatitude(latitud);
        datacrimeRequest.setLongitude(longitud);
        datacrimeRequest.setIpAddress(ipRequest);
        datacrimeRequest.setInfo(info);
        datacrimeRequest.setGrid_code(grid_code);
        datacrimeRequest.setDate(new Date());
//        System.out.println(demographicsRequest.toString());
        datacrimeRequestService.save(datacrimeRequest);

    }
}

package com.geodir.apidatacrime.apidatacrime.domain.findNearestCentroid;

import com.geodir.apidatacrime.apidatacrime.FieldsEntity;
import com.geodir.apidatacrime.apidatacrime.FieldsRepository;
import com.geodir.apidatacrime.apidatacrime.domain.searchbyfields.DatacrimeField;
import com.geodir.apidatacrime.apidatacrime.domain.searchbyfields.DatacrimeGroup;
import lombok.AllArgsConstructor;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.*;
import org.locationtech.spatial4j.exception.InvalidShapeException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.lucene.document.LatLonDocValuesField;
import org.apache.lucene.document.LatLonPoint;

@Service
@AllArgsConstructor
public class FindNearestCentroidService {

    private final FieldsRepository fieldsRepository;
    private final IndexesProvider indexesProvider;
    private List<DatacrimeGroup> datacrimeGroupList;

    @PostConstruct
    public void getGroupsOfFields() {
        // leer las variables
        datacrimeGroupList = getGroupWithFields();

    }

    private List<DatacrimeGroup> getGroupWithFields(){

        List<FieldsEntity> fieldsEntityList= (List<FieldsEntity>) fieldsRepository.findAll();

        List<String> groupsList
                = fieldsEntityList.stream().map(FieldsEntity::getGroup).distinct().collect(
                Collectors.toList());

        List<DatacrimeGroup> datacrimeGroupList = new ArrayList<>();

        for (String group: groupsList) {

            DatacrimeGroup datacrimeGroup = new DatacrimeGroup();
            datacrimeGroup.setName(group);

            // agrupar los fields en base al grupo
            List<DatacrimeField> datacrimeFields= fieldsEntityList.stream().filter(f -> f.getGroup().equals(group))
                    .map(f -> {
                        DatacrimeField datacrimeField = new DatacrimeField();
                        datacrimeField.setName(f.getFieldname());
                        datacrimeField.setTypeField(f.getTypeField());
                        datacrimeField.setDescription(f.getFieldDescription());
                        // el value se rellenara luego
                        datacrimeField.setValue(null);
                        // la descripcion de cada grupo tambien la tienen cada field
                        datacrimeField.setOrderField(f.getOrderField());
                        datacrimeField.setEnabled(f.isEnabled());

                        datacrimeGroup.setOrderGroup(f.getOrderGroup());
                        datacrimeGroup.setDescription(f.getGroupDescription());
                        return datacrimeField;
                    }).sorted(Comparator.comparing(DatacrimeField::getOrderField)).collect(Collectors.toList());

            //order fields
            datacrimeGroup.setListDatacrimeFields(datacrimeFields);

            // evaluar los campos
            // si la longitud de enabled = false es iguala la longitud no agregarlo
            long quantityEnabled = datacrimeGroup.getListDatacrimeFields().stream().filter(f -> !f.isEnabled()).count();

            if(quantityEnabled == datacrimeGroup.getListDatacrimeFields().size()){
                continue;
            }

            datacrimeGroupList.add(datacrimeGroup);
        }

        // order groups
        datacrimeGroupList.sort(Comparator.comparing(DatacrimeGroup::getOrderGroup));

        return datacrimeGroupList;
    }

    public List<DatacrimeGroup> search(String latlon) {

        String[] arrayLatlon = latlon.split(",");
        double latitude = Double.parseDouble(arrayLatlon[0]);
        double longitude = Double.parseDouble(arrayLatlon[1]);

        List<DatacrimeGroup>  datacrimeGroupList = new ArrayList<>();

        // search
        Query query = LatLonPoint.newDistanceQuery("latlon", latitude, longitude, 100);
        SortField sortField = LatLonDocValuesField.newDistanceSort("latlon", latitude, longitude);

        try {
            IndexSearcher indexSearcher = indexesProvider.getIndexSearcher();
            TopDocs topDocs = indexSearcher.search(query, 1, new Sort(sortField),
                    true, false);
            ScoreDoc[] documents = topDocs.scoreDocs;

            System.out.println("resultados ");
            System.out.println(documents.length);

            if (documents.length >= 1){
                int idDocument = documents[0].doc;
                System.out.println("id documento");
                System.out.println(idDocument);
                datacrimeGroupList= mappingDatacrimeDocument(indexSearcher, idDocument );
            }

        } catch (IOException | InvalidShapeException e) {
            e.printStackTrace();
        }

        return datacrimeGroupList;
    }

    private List<DatacrimeGroup> mappingDatacrimeDocument(IndexSearcher indexSearcher,
                                                              int idDocument) {

        try{
            Document document = indexSearcher.doc(idDocument);

            // recorrer grupos
            for (DatacrimeGroup datacrimeGroup: datacrimeGroupList) {

                for (DatacrimeField datacrimeField:datacrimeGroup.getListDatacrimeFields()) {
                    System.out.println("field");
                    System.out.println(datacrimeField.getName());
                    System.out.println("valor");
                    System.out.println(document.get(datacrimeField.getName()));

                    if(datacrimeField.getTypeField().equals("bigint")
                            || datacrimeField.getTypeField().equals("integer")){
                        datacrimeField.setValue(document.get(datacrimeField.getName()) != null ? Integer.parseInt(document.get(datacrimeField.getName())): null);
                        datacrimeField.setTypeField("integer");
                    }else if(datacrimeField.getTypeField().contains("character varying")){
                        datacrimeField.setValue(document.get(datacrimeField.getName()));
                        datacrimeField.setTypeField("text");
                    }else if (datacrimeField.getTypeField().equals("double precision")
                            || datacrimeField.getTypeField().equals("numeric")){
                        datacrimeField.setValue(Double.parseDouble(document.get(datacrimeField.getName())));
                        datacrimeField.setTypeField("double");
                    }
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return datacrimeGroupList;
    }
}

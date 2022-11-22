package com.geodir.apidatacrime.apidatacrime.domain.services;

import com.geodir.apidatacrime.apidatacrime.FieldsEntity;
import com.geodir.apidatacrime.apidatacrime.FieldsRepository;
import com.geodir.apidatacrime.apidatacrime.domain.searchbyfields.DatacrimeField;
import com.geodir.apidatacrime.apidatacrime.domain.searchbyfields.DatacrimeGroup;
import lombok.AllArgsConstructor;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.locationtech.spatial4j.context.SpatialContextFactory;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.exception.InvalidShapeException;
import org.locationtech.spatial4j.io.GeoJSONReader;
import org.locationtech.spatial4j.shape.Shape;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DatacrimeServiceByFields {
    private final FieldsRepository fieldsRepository;
    private final DatacrimeDictionaryService datacrimeDictionaryService;
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
                        DatacrimeField datacrimefield = new DatacrimeField();
                        datacrimefield.setName(f.getFieldname());
                        datacrimefield.setTypeField(f.getTypeField());
                        datacrimefield.setDescription(f.getFieldDescription());
                        // el value se rellenara luego
                        datacrimefield.setValue(null);
                        // la descripcion de cada grupo tambien la tienen cada field
                        datacrimefield.setOrderField(f.getOrderField());
                        datacrimefield.setEnabled(f.isEnabled());

                        datacrimeGroup.setOrderGroup(f.getOrderGroup());
                        datacrimeGroup.setDescription(f.getGroupDescription());
                        return datacrimefield;
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
        String latitude = arrayLatlon[0];
        String longitude = arrayLatlon[1];
        String textGeojsonOfPoint = "{\"type\":\"Point\",\"coordinates\":[" + longitude + "," + latitude + "]}";
        List<DatacrimeGroup>  datacrimeGroupList = new ArrayList<>();

        try {

            Shape shape = new GeoJSONReader(JtsSpatialContext.GEO, new SpatialContextFactory())
                    .read(textGeojsonOfPoint);

            Query query = new RecursivePrefixTreeStrategy(new GeohashPrefixTree(JtsSpatialContext.GEO, 11), "geojson")
                    .makeQuery(new SpatialArgs(SpatialOperation.Intersects, shape));

            TopDocs topDocs;
            IndexSearcher indexSearcher = datacrimeDictionaryService.getIndexSearcher();
            topDocs = indexSearcher.search(query, 10000);
            ScoreDoc[] scoreDoc = topDocs.scoreDocs;
            datacrimeGroupList= mappingDemographicDocument(indexSearcher, scoreDoc );

        } catch (IOException | InvalidShapeException | ParseException e) {
            e.printStackTrace();
        }

        return datacrimeGroupList;
    }

    private List<DatacrimeGroup> mappingDemographicDocument(IndexSearcher indexSearcher,
                                                              ScoreDoc[] scoreDoc) {

        try{
            int idDocument = 0;
            Document document = indexSearcher.doc(idDocument);
            // recorrer grupos
            for (DatacrimeGroup datacrimeGroup: datacrimeGroupList) {

                System.out.println("group");
                System.out.println(datacrimeGroup.getName());
                for (DatacrimeField datacrimeField:datacrimeGroup.getListDatacrimeFields()) {
                    System.out.println("field");
                    System.out.println(datacrimeField.getName());
                    if(datacrimeField.getTypeField().equals("bigint")
                            || datacrimeField.getTypeField().equals("integer")){
                        datacrimeField.setValue(Integer.parseInt(document.get(datacrimeField.getName())));
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

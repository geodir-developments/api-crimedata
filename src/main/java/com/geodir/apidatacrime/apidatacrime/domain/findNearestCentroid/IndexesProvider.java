package com.geodir.apidatacrime.apidatacrime.domain.findNearestCentroid;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
@Service
public class IndexesProvider {
    private IndexSearcher indexDemographicDictionary = null;

//    public static final String PATH_DICTIONARY_DEMOGRAPHIC = "D:\\DICTIONARIES_DEMO\\demographic";
    @Value("${data.indexDatacrime}")
    private String pathDatacrimesDictionary;

    @PostConstruct
    public void initDictionaries() {

        System.out.println("SE EJECUTA DICCIONARIOS");
        try {

            Directory directoryDemographicDictionary = FSDirectory.open(new File(pathDatacrimesDictionary).toPath());
            indexDemographicDictionary = new IndexSearcher(DirectoryReader.open(directoryDemographicDictionary));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IndexSearcher getIndexSearcher(){

        return indexDemographicDictionary;

    }
}

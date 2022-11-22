package com.geodir.apidatacrime.apidatacrime.domain.services;

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
public class DatacrimeDictionaryService {

    private IndexSearcher indexDatacrimeDictionary = null;

//    public static final String PATH_DICTIONARY_DATACRIME = "D:\\DICTIONARIES_DEMO\\datacrime";
    @Value("${data.indexDatacrime}")
    private String pathDatacrimesDictionary;
    @PostConstruct
    public void initDictionaries() {

        System.out.println("SE EJECUTA DICCIONARIOS");
        try {

            Directory directoryDatacrimeDictionary = FSDirectory.open(new File(pathDatacrimesDictionary).toPath());
            indexDatacrimeDictionary = new IndexSearcher(DirectoryReader.open(directoryDatacrimeDictionary));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IndexSearcher getIndexSearcher(){

        return indexDatacrimeDictionary;

    }



}


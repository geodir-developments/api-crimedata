package com.geodir.apidatacrime.apidatacrime;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

public class ProviderDictionaries {

    public static final String PATH_DICTIONARY_DATACRIME =  "D:\\DICTIONARIES_DEMO\\datacrime";

    private IndexSearcher indexDatacrimeDictionary = null;

    @PostConstruct
    public void initDictionaries() {

        System.out.println("SE CARGA DICCIONARIO EN MEMORIA");
        try {

            Directory directoryDemographicDictionary = FSDirectory.open(new File(PATH_DICTIONARY_DATACRIME).toPath());
            indexDatacrimeDictionary = new IndexSearcher(DirectoryReader.open(directoryDemographicDictionary));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IndexSearcher getIndexDatacrimeDictionary(){
        return indexDatacrimeDictionary;
    }

}
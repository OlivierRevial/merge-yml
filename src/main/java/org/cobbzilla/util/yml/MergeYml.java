package org.cobbzilla.util.yml;

/**
 * (c) Copyright 2013 Jonathan Cobb
 * This code is available under the Apache License, version 2: http://www.apache.org/licenses/LICENSE-2.0.html
 */
public class MergeYml {

    public static void main (String[] args) throws Exception {
        String[] sourceFiles = {"D:/docker-compose-template.yml", "D:/docker-compose-add.yml"};
        String destFile = "D:/docker-compose-merged.yml";
        new YmlMerger().mergeFiles(sourceFiles, destFile);
    }
}

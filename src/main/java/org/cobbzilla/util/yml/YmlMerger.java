package org.cobbzilla.util.yml;

import com.github.mustachejava.DefaultMustacheFactory;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * (c) Copyright 2013-2015 Jonathan Cobb This code is available under the Apache
 * License, version 2: http://www.apache.org/licenses/LICENSE-2.0.html
 */
public class YmlMerger {

    public static final DefaultMustacheFactory DEFAULT_MUSTACHE_FACTORY = new DefaultMustacheFactory();

    private final Yaml yaml = new Yaml();
    private final Map<String, Object> scope = new HashMap<String, Object>();;

    public YmlMerger() {
        init(System.getenv());
    }

    public YmlMerger(Map<String, String> env) {
        if (env != null)
            init(env);
    }

    private void init(Map<String, String> env) {
        for (String varname : env.keySet()) {
            scope.put(varname, env.get(varname));
        }
    }

    public Map<String, Object> mergeFiles(String[] sourceFiles, String destFile) throws IOException {
        // 1-2. Create merged results
        Map<String, Object> mergedResult = mergeFiles(sourceFiles);

        // 3. Write result in destination file
        FileWriter fw = new FileWriter(destFile);
        StringWriter writer = new StringWriter();
        yaml.dump(mergedResult, writer);
        fw.write(writer.toString());
        fw.close();

        return mergedResult;
    }

    public Map<String, Object> mergeFiles(String[] sourceFiles) throws IOException {
        TreeNode<Object> root = new TreeNode<>("root", null);

        // 1. Construct a tree from the different yml files
        for (String file : sourceFiles) {
            InputStream in = null;
            try {
                // read the file into a String
                in = new FileInputStream(file);
                final String entireFile = IOUtils.toString(in);

                // substitute variables
                final StringWriter writer = new StringWriter(entireFile.length() + 10);
                DEFAULT_MUSTACHE_FACTORY.compile(new StringReader(entireFile), "mergeyml_" + System.currentTimeMillis())
                        .execute(writer, scope);

                // load the YML file
                final Map<String, Object> yamlContents = (Map<String, Object>) yaml.load(writer.toString());

                // merge into results map
                System.out.println("loaded YML from " + file + ": " + yamlContents);
                createTree(root, yamlContents);
            } finally {
                if (in != null)
                    in.close();
            }
        }

        // 2. Create Snakeyaml-compatible output from merged tree
        Map<String, Object> mergedResult = convertToSnakeyamlObject(root);
        System.out.println("Resulting yaml : " + toString(mergedResult));

        return mergedResult;
    }

    /**
     * Recursive method !
     * 
     * @param treeNode
     * @param yamlContents
     */
    @SuppressWarnings("unchecked")
    private void createTree(TreeNode<Object> treeNode, Map<String, Object> yamlContents) {

        // Nominal case
        if (yamlContents == null) {
            return;
        }

        for (String key : yamlContents.keySet()) {
            Object yamlValue = yamlContents.get(key);
            if (yamlValue instanceof Map) {
                TreeNode<Object> child = treeNode.getChild(key);
                if (child == null) {
                    child = new TreeNode<>(key, null);
                    treeNode.addChild(child);
                }
                createTree(child, (Map<String, Object>) yamlValue);
            } else {
                TreeNode<Object> child = treeNode.getChild(key);
                if (child == null) {
                    child = new TreeNode<>(key, yamlValue);
                    treeNode.addChild(child);
                } else {
                    child.setValue(yamlValue);
                }
            }
        }
    }

    private Map<String, Object> convertToSnakeyamlObject(TreeNode<Object> node) {
        Map<String, Object> currentMap = new LinkedHashMap<>();

        if (node.hasChildren()) {
            if(node.getKey().equals("root")) {
                for (TreeNode<Object> child : node.children()) {
                    currentMap.putAll(convertToSnakeyamlObject(child));
                }
            } else {
                Map<String, Object> childrenMap = new LinkedHashMap<>();
                currentMap.put(node.getKey(), childrenMap);
                for (TreeNode<Object> child : node.children()) {
                    childrenMap.putAll(convertToSnakeyamlObject(child));
                }
            }
        } else {
            // Nominal case
            currentMap.put(node.getKey(), node.getValue());
        }
        return currentMap;
    }

    public String toString(Map<String, Object> merged) {
        return yaml.dump(merged);
    }

}

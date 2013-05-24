/*
 * Copyright (C) 2013 Yrom <http://www.yrom.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.yrom.builder.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class ManifestParser {

    private static final String ANDROID = "android";
    private Document            doc;
    private Element             manifest;
    private Namespace           xmlns;
    private QName               qVersionCode;
    private QName               qVersionName;
    private QName               androidName;
    private QName               androidValue;
    private Element             application;
    private File                manifestFile;

    public ManifestParser(String manifestPath) throws DocumentException {
        manifestFile = new File(manifestPath);
        try {
            FileUtils.copyFile(manifestFile, new File(manifestPath+".bak"));
        } catch (IOException e) {
        }
        doc = parseManifest(manifestPath);
        manifest = doc.getRootElement();
        xmlns = manifest.getNamespaceForPrefix(ANDROID);
        qVersionCode = new QName("versionCode", xmlns);
        qVersionName = new QName("versionName", xmlns);
        androidName = new QName("name", xmlns);
        androidValue = new QName("value", xmlns);
        application = manifest.element("application");
    }

    public static Document parseManifest(String file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        return document;
    }

    public String getVersionCode() {
        return manifest.attributeValue(qVersionCode);

    }

    public void newVersionInfo(int versionCode, String versionName) {
        Attribute code = manifest.attribute(qVersionCode);
        if(versionCode != 0 && Integer.parseInt(code.getValue()) != versionCode)
            code.setValue(String.valueOf(versionCode));
        if(versionName!=null && versionName.trim().length()>0)
            manifest.attribute(qVersionName).setValue(versionName);

    }

    public Document getDocument() {
        return doc;
    }

    public String getXml() {
        return doc.asXML();
    }

    /**
     * 替换meta-data节点，如果没有该名称的节点，则会添加上该节点
     * @param name
     * @param value
     */
    public void replaceMetaData(String name, String value) {
        replaceMetaData(name, value, true);
    }
    /**
     * 替换meta-data节点
     * @param name
     * @param value
     * @param add 没有发现该名称的meta-data，是否添加
     */
    public void replaceMetaData(String name, String value, boolean add) {
        Iterator<Element> iterator = application.elementIterator("meta-data");
        boolean hasData = false;
        for (; iterator.hasNext();) {

            Element data = iterator.next();
            String nameValue = data.attributeValue(androidName);
            if (nameValue != null && nameValue.equals(name)) {
                Attribute attribute = data.attribute(androidValue);
                hasData = attribute.getValue().equals(value);
                if (attribute != null && !hasData) {
                    attribute.setValue(value);
                    return;
                }
            }
        }
        if (add && !hasData)
            application.addElement("meta-data")
                    .addAttribute(androidName, name)
                    .addAttribute(androidValue, value);
    }
    /**
     * 保存修改
     * @throws IOException
     */
    public void write() throws IOException {
        write(manifestFile);
        
    }
    public void write(File file) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new FileWriter(file), format);
        writer.write(doc);
        writer.close();
    }

}

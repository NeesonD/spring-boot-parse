package com.neeson.springbootparse.resource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.util.List;

/**
 * Create on 2020-03-13
 * Copyright (c) 2020 by XueBang Information Technology Co.Ltd.
 * All Rights Reserved, Designed By XueBangSoft
 * Copyright:  Copyright(C) 2014-2020
 * Company:    XueBang Information Technology Co.Ltd.
 *
 * @author Administrator
 */
public class ResourceTest {

    /**
     * file: 用于访问文件系统；http: 用于通过 HTTP 协议访问资源；ftp: 用于通过 FTP 协议访问资源等
     * @throws IOException
     * @throws DocumentException
     */
    @Test
    public void testUrlResource() throws IOException, DocumentException {
        // 创建一个 Resource 对象，指定从文件系统里读取资源
        UrlResource ur = new UrlResource("file:book.xml");
        parseResource(ur);
    }

    /**
     * ClassPathResource 可自动搜索位于 WEB-INF/classes 下的资源文件
     * @throws IOException
     * @throws DocumentException
     */
    @Test
    public void testClassPathResource() throws IOException, DocumentException {
        // 创建一个 Resource 对象，从类加载路径里读取资源
        ClassPathResource cr = new ClassPathResource("book.xml");
        parseResource(cr);
    }

    private void parseResource(Resource ur) throws DocumentException, IOException {
        // 获取该资源的简单信息
        System.out.println(ur.getFilename());
        System.out.println(ur.getDescription());
        // 创建 Dom4j 的解析器
        SAXReader reader = new SAXReader();
        Document doc = reader.read(ur.getFile());
        // 获取根元素
        Element el = doc.getRootElement();
        List l = el.elements();
        // 此处省略了访问、输出 XML 文档内容的代码。
    }

}

package com.pingnotes.tools;

import com.squareup.protoparser.ProtoFile;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * mail: pd-shaobo@qq.com
 * created by shaobo on 10/10/16.
 */
public abstract class AbstractGenerator {
    private VelocityEngine velocityEngin = new VelocityEngine();
    protected Template getTemplate(String fileName){
        velocityEngin.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngin.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngin.init();
        Template t = velocityEngin.getTemplate(fileName, "utf-8");
        return t;
    }

    public abstract void gen(String src , ProtoFile protoFile);

}

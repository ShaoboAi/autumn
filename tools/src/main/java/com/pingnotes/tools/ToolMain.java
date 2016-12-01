package com.pingnotes.tools;

import com.squareup.protoparser.ProtoFile;
import com.squareup.protoparser.ProtoSchemaParser;

import java.io.File;
import java.io.IOException;

/**
 * mail: pd-shaobo@qq.com
 * created by shaobo on 10/10/16.
 */
public class ToolMain {
    public static void main(String[] args) {
        CodeGenerator generator = new CodeGenerator();
        String vm = "interface.vm";
        String protoName = "/Users/shaobo/Documents/Autumn/common/src/main/resources/hello_world.proto";
        try {
            ProtoFile protoFile = ProtoSchemaParser.parse(new File(protoName));
            generator.gen(vm, protoFile);
            System.out.println("package name :" + protoFile.getPackageName());
            System.out.println("service : " + protoFile.getServices().get(0).getMethods().get(0).getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

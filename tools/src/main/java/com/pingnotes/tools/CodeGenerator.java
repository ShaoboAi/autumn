package com.pingnotes.tools;

import com.squareup.protoparser.ProtoFile;
import com.squareup.protoparser.Service;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.*;
import java.util.List;

/**
 * mail: pd-shaobo@qq.com
 * created by shaobo on 10/10/16.
 */
public class CodeGenerator extends AbstractGenerator {

    public static String apiFileName(String serviceName) {
        return serviceName + ".java";
    }

    @Override
    public void gen(String template, ProtoFile protoFile) {
        List<Service> services = protoFile.getServices();
        VelocityContext vc = new VelocityContext();
        vc.put("packageName", protoFile.getPackageName());
        vc.put("clssUtility", new ClssUtility());

        protoFile.getPackageName();

        for (Service s : services) {
            try {
                vc.put("serviceName", s.getName());
                vc.put("methods", s.getMethods());

                Template t = getTemplate(template);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(apiFileName(s.getName())), "utf-8"));
                t.merge(vc, writer);

                writer.flush();
                writer.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

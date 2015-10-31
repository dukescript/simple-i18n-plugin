/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dukescript.simple.i18n.maven.plugin;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "i18n")
public class I18N extends AbstractMojo {

    @Parameter
    private File inputDir;

    @Parameter
    private File outputDir;

    Configuration cfg = new Configuration();

    @Override
    public void execute() {
        expandTemplate(inputDir, outputDir);
    }

    private void expandTemplate(File infile, File outfile) {
        Logger.getLogger(I18N.class.getName()).info("Processing " + infile);

        try {
            if (infile == null || infile.isFile()) {
                return;
            }
            File absoluteFile = infile.getAbsoluteFile();
            if (outfile == null) {
                outfile = infile;
            }
            cfg.setDirectoryForTemplateLoading(absoluteFile);
            if (absoluteFile.isDirectory()) {
                Logger.getLogger(I18N.class.getName()).info("Looking for template data in " + absoluteFile);

                File[] templateFiles = absoluteFile.listFiles((f, n) -> n.contains(".ftl."));
                File[] bundleFiles = absoluteFile.listFiles(
                        (f, n) -> n.startsWith("bundle") && n.endsWith("properties"));
                File[] dirs = absoluteFile.listFiles((f, n) -> f.isDirectory());

                HashMap<String, Properties> hashMap = new HashMap<>();
                for (File bundleFile : bundleFiles) {
                    Properties properties = new Properties();
                    properties.load(new FileInputStream(bundleFile));
                    String locale = bundleFile.getName().replaceAll("bundle", "").replaceAll(".properties", "");
                    hashMap.put(locale, properties);
                }
                for (File templateFile : templateFiles) {
                    Logger.getLogger(I18N.class.getName()).info("Processing " + templateFile.getName());
                    Set<Map.Entry<String, Properties>> entrySet = hashMap.entrySet();

                    Template template = cfg.getTemplate(templateFile.getName());
                    for (Map.Entry<String, Properties> entry : entrySet) {
                        Logger.getLogger(I18N.class.getName()).info("Using template data " + entry.getKey());
                        //Load template from source folder

                        Properties data = entry.getValue();

                        // Console output
                        Writer out = new OutputStreamWriter(System.out);
                        template.process(data, out);

                        // File output
                        Writer file = new FileWriter(new File(outfile, templateFile.getName().replaceAll(".ftl.html", entry.getKey() + ".html")));
                        template.process(data, file);
                        file.flush();
                        file.close();
                    }
                }
                for (File dir : dirs) {

                    expandTemplate(dir, new File(outfile, dir.getName()));
                }

            } else {
                Logger.getLogger(I18N.class.getName()).error("Not processing anything, inputDir " + infile + " is not a Directory");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }
}

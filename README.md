# simple-i18n-plugin
A simple plugin for generating localized versions of html pages and templates using Freemarker

To use this, build it locally, then add to your applications pom:
``` xml
<plugin>
    <groupId>com.dukescript</groupId>
    <artifactId>simple-i18n-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <inputDir>${basedir}/src/main/resources/webapp/pages/</inputDir>
        <outputDir>${basedir}/src/main/webapp/pages/</outputDir>
    </configuration>
    <executions>
            <execution>
                <id>bla</id>
                <phase>generate-resources</phase>
                <goals>
                    <goal>i18n</goal>
                </goals>
            </execution>
        </executions>
</plugin>
```

*inputDir* should point to a directory with freemarker template files. These files need to follow the name pattern:

    <name-of-generated-file>.ftl.<extension>

E.g. "index.ftl.html" will later be expanded to index.html, index_de.html, index_fr_FR.html, and so on.

The plugin will look in the same directory for bundle files following the name pattern:

    bundle<locale>.properties

If you put for example bundle.properties, bundle_de.properties and bundle_it.properties in the dir, you will get these files generated in outputDir:

index.html, index_de.html, index_it.html

Search is recursive, so every folder under inputDir will be treated the same way. The output will also be written in a sub dir of outputDir.
If the outdir doesn't exist it's generated. There's no checking for correct locale extensions. 

This has been created for working with DukeScript, but might also be helpful in other project types. In DukeScript to enable i18n support 
for loading pages add "locale(Locale.getDefault()) to the BrowserBuilder call: 

``` java
 public static void main(String... args) throws Exception {
        BrowserBuilder.newBrowser().
                loadPage("pages/index.html").
                locale(Locale.getDefault()).
                loadClass(Main.class).
                invoke("onPageLoad", args).
                showAndWait();
        System.exit(0);
    }
``
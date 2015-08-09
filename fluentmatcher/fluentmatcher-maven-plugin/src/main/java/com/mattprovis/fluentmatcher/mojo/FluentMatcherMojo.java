package com.mattprovis.fluentmatcher.mojo;

import com.mattprovis.fluentmatcher.FluentMatcherGenerator;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class FluentMatcherMojo extends AbstractMojo {

    @Parameter(property = "pojos", required = true)
    private String[] pojos;

    @Parameter(property = "sourceDestDir", defaultValue = "${project.build.directory}/generated-test-sources/fluentmatcher/")
    private String sourceDestDir;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (pojos == null || pojos.length == 0) {
            throw new IllegalStateException("Expected parameter: pojos");
        }

        try {
            Path generatedJavaTestSourcesPath = Paths.get(sourceDestDir, "java");
            project.addTestCompileSourceRoot(generatedJavaTestSourcesPath.toAbsolutePath().toString());

            for (Class<?> pojoClass : getPojoClasses(pojos)) {
                generateMatcher(generatedJavaTestSourcesPath, pojoClass);
            }
            copyFluentMatcherSuperclass(generatedJavaTestSourcesPath);

        } catch (IOException | DependencyResolutionRequiredException | ClassNotFoundException e) {
            throw new MojoExecutionException("", e);
        }
    }

    private List<Class<?>> getPojoClasses(String... pojos) throws ClassNotFoundException, MalformedURLException, DependencyResolutionRequiredException {
        ClassLoader classLoader = createClassLoader();
        List<Class<?>> results = new ArrayList<>();
        for (String className : pojos) {
            results.add(classLoader.loadClass(className));
        }

        return results;
    }

    private ClassLoader createClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
        List<String> runtimeClasspathElements = project.getCompileClasspathElements();
        URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];

        for (int i = 0; i < runtimeClasspathElements.size(); i++) {
            String element = runtimeClasspathElements.get(i);
            runtimeUrls[i] = new File(element).toURI().toURL();
        }
        return URLClassLoader.newInstance(runtimeUrls,
                Thread.currentThread().getContextClassLoader());
    }

    private void generateMatcher(Path generatedJavaTestSourcesPath, Class<?> pojoClass) throws IOException {
        String matcherClassName = pojoClass.getSimpleName() + "Matcher";

        String packageDirs = pojoClass.getPackage().getName()
                .replace('.', File.separatorChar);
        Path packagePath = generatedJavaTestSourcesPath.resolve(packageDirs);

        Path matcherClassPath = packagePath.resolve(matcherClassName + ".java");
        Files.deleteIfExists(matcherClassPath);
        Files.createDirectories(packagePath);
        try (Writer writer = new FileWriter(matcherClassPath.toFile())) {
            FluentMatcherGenerator fluentMatcherGenerator = new FluentMatcherGenerator(pojoClass, writer);
            fluentMatcherGenerator.generateMatcher();
        }
        getLog().info("Generated matcher: " + matcherClassPath.toAbsolutePath());
    }

    public void copyFluentMatcherSuperclass(Path generatedJavaTestSourcesPath) throws IOException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("resources/FluentMatcher.java");
        Path targetPath = generatedJavaTestSourcesPath.resolve("com/mattprovis/fluentmatcher/FluentMatcher.java");
        Files.copy(resourceAsStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

        getLog().info("Copied FluentMatcher superclass to: " + targetPath);
    }
}

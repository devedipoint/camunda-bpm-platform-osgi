package org.camunda.bpm.extension.osgi.karaf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.io.File;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class KarafIntegrationTest {

  @Inject
  protected BundleContext ctx;

  @Configuration
  public Option[] config() {
    MavenArtifactUrlReference karafUrl =
      maven()
        .groupId("org.apache.karaf")
        .artifactId("apache-karaf")
        .type("zip")
        .versionAsInProject();
    MavenUrlReference karafStandardRepo =
      maven()
        .groupId("org.apache.karaf.features")
        .artifactId("standard")
        .classifier("features")
        .type("xml")
        .versionAsInProject();

    return new Option[] {
        // logLevel(LogLevel.TRACE),
        // debugConfiguration("5005", true),
        karafDistributionConfiguration().frameworkUrl(karafUrl)
          .unpackDirectory(new File("target/exam"))
        // .useDeployFolder(false)
        ,
        // We use this option to allow the container to use artifacts found in private / local repo.
        editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg",
          "org.ops4j.pax.url.mvn.repositories",
          "file:${maven.repo.local}@id=mavenlocalrepo@snapshots"),
        editConfigurationFileExtend("etc/org.ops4j.pax.url.mvn.cfg",
          "org.ops4j.pax.url.mvn.localRepository",
          "${maven.repo.local}"),
        editConfigurationFileExtend("etc/system.properties",
          "maven.repo.local",
          System.getProperty("maven.repo.local", "")), keepRuntimeFolder(),
        features(karafStandardRepo, "scr"),
        features(new File("target/classes/features.xml").toURI().toString(),
          "camunda-bpm-karaf-feature-minimal")
    };
  }

  @Test
  public void startCamundaOsgiBundle() throws BundleException {
    assertThat(ctx, is(notNullValue()));
    Bundle[] bundles = ctx.getBundles();
    boolean found = false;
    for (Bundle b : bundles) {
      if (b.getSymbolicName().equals("org.camunda.bpm.extension.osgi")) {
        b.start();
        found = true;
      }
    }
    if (!found) {
      fail("Couldn't find bundle");
    }
  }

}

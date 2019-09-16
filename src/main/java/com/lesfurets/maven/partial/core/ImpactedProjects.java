/*
 * Copyright (C) by Courtanet, All Rights Reserved.
 */
package com.lesfurets.maven.partial.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.lesfurets.maven.partial.utils.DependencyUtils.collectDependenciesInSnapshot;
import static com.lesfurets.maven.partial.utils.DependencyUtils.collectDependents;

@Singleton
public class ImpactedProjects {

    @Inject
    private Configuration configuration;
    @Inject
    private MavenSession mavenSession;

    public List<MavenProject> get(Collection<MavenProject> changedProjects) {
        HashSet<MavenProject> changed = new HashSet<>(changedProjects);
        changed.removeAll(configuration.ignoredProjects);
        if (configuration.impacted) {
            mavenSession.getAllProjects().stream()
                    .filter(changed::contains)
                    .forEach(p -> collectDependents(mavenSession.getAllProjects(), p, changed));
        }
        if (configuration.buildSnapshotDependencies) {
            mavenSession.getAllProjects().stream()
                    .filter(changed::contains)
                    .forEach(p -> collectDependenciesInSnapshot(mavenSession.getAllProjects(), p, changed));
        }
        return mavenSession.getAllProjects().stream().filter(changed::contains).collect(Collectors.toList());
    }
}

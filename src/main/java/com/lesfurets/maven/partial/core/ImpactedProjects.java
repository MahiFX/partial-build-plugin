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
import java.util.Set;
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
        Set<MavenProject> changed = new HashSet<>(changedProjects);
        changed.removeAll(configuration.ignoredProjects);
        if (configuration.impacted) {
            Set<MavenProject> impacted = new HashSet<>(changedProjects);
            changed.forEach(p -> collectDependents(mavenSession.getAllProjects(), p, impacted));
            changed.addAll(impacted);
        }
        if (configuration.buildSnapshotDependencies) {
            mavenSession.getProjects().stream()
                    .filter(changed::contains)
                    .forEach(p -> collectDependenciesInSnapshot(mavenSession.getAllProjects(), p, changed));
        }
        return mavenSession.getProjects().stream().filter(changed::contains).collect(Collectors.toList());
    }
}

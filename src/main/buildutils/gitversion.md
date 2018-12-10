# Versioning from Git/Gradle

## Motivation / Requirements

1. Builds should be reproducible, i.e. always using the same version
   and time stamp when building the same git state at different times
   and on different hosts.

2. Builds should identify as accurately as possible their corresponding
   git state (i.e. last release version, branch, commit hash and timestamp),
   e.g., for checking bug reports against the correct versions.

3. Version identifiers should be produced in line with
   [Semantic Versioning](https://semver.org/) and convey correct chronological
   ordering according to [Maven version ordering](https://maven.apache.org/ref/3.3.9/maven-artifact/apidocs/org/apache/maven/artifact/versioning/ComparableVersion.html).


## Assumptions
  
1. Jadex versions take the form `<#major>.<#minor>.<#patch>[-[<branchname>-](<#timestamp>|SNAPSHOT)]`.
   This is roughly line with [Semantic Versioning](https://semver.org/), but
   uses branch and timestamp/SNAPSHOT as part of a single identifier called
   [pre-release version](https://semver.org/#spec-item-9).
   
2. Major and minor versions depend on the source code
   (i.e. version is bumped on significant API changes)
   and thus should be stored in code (i.e. `src/main/buildutils/jadexversion.properties`)
   and not managed by git tags and gradle commands as in
   [reckon](https://github.com/ajoberstar/reckon) or
   [nebula-release-plugin](https://github.com/nebula-plugins/nebula-release-plugin).
   
3. Patch version and suffixes (e.g. branch name, SNAPSHOT, etc.) should
   be managed automatically as their meaning does not relate to API (code)
   but to git state (commits, tags, branches). We use branch names instead of
   explicit stages (cf. reckon) so the metadata can be managed automatically
   based on git state and does not require configuration by the developer.

## Approach

There are some cases to be distinguished. Yet, the version always takes the
form `<#major>.<#minor>.n[-suffix]`, where `<#major>` and `<#minor>`
are read from `src/main/buildutils/jadexversion.properties`.

### Building from cloned Git repo

1. **Release build (always reproducible):**  A release build occurs when the `HEAD` is tagged with
   `<#major>.<#minor>.n`, i.e. prior to kicking of the release build, the
   developer should create the appropriate tag (`gradlew release`?).
   The build stores hash and the timestamp of the latest commit in
   `version.properties` and `jadexversion.properties`.

2. **Non-dirty build (always reproducible):** A non-dirty build is a non-release build in a `clean`
   repository, i.e., the workdir state corresponds to the latest commit, but
   the `HEAD` is not tagged with a release version. The build chooses patch
   version *n* as the smallest non-negative integer value such that no prior
   tag `<#major>.<#minor>.m` exists, with *m>=n*. The build stores hash
   and the timestamp of the latest commit in `version.properties` and
   `jadexversion.properties` and uses version suffix
   `-[<branchname>-]<timestamp>` using timestamp format `yyyyMMddHHmmss`.
   If the build runs on `stable` or `master` branch, the `[<branchname>-]`
   part is omitted.

3. **Dirty build (not reproducible):** A build with modified files in the
   workdir. Like in the non-dirty build, the build chooses patch version *n*
   as the smallest non-negative integer value such that no prior tag
   `<#major>.<#minor>.m` exists, with *m>=n*. The build appends 
   `-[<branchname>-]SNAPSHOT` as version suffix and stores the *current
   time* as timestamp. Commit hash of the latest commit are stored for
   reference to know on which git state the modifications are based on.

### Building from copied source folder

1. **Distribution sources (potentially reproducible):** The distribution
   sources contain a processed `src/main/buildutils/jadexversion.properties`,
   which contains build information from git state. By default, the build
   assumes a dirty (i.e. modified) source tree and thus increments *n* and
   appends `SNAPSHOT` (cf. dirty build from git). When `-Pdirty=false`
   is supplied during a build of a non-modified `sources.zip`, the build
   produces an exact copy of the original distribution (cf. `checkDist`
   task).
    
2. **Non-distribution sources (not reproducible):** When obtaining a flat copy
   of the sources, e.g., download .zip from Github, no build or git information
   is available. The build version thus always will be
   `<#major>.<#minor>.9999-SNAPSHOT`.

## Changes to previous scheme

1. Only release builds, i.e. pushed to maven central, are tagged. Intermediate
   versions are now uniquely numbered by appended timestamp of latest commit.
   Pro: reduces number of tags in repo and clearly identifies non-release builds.
   Contra: nightly build names are more ugly?

2. Version information cannot be provided from environment. It is always
   solely determined based on existing sources and git state.
## Building and testing

To test, you'll want the archetype in a local catalog so that it can be found prior to publishing.  In the archetype directory:

    mvn clean install archetype:update-local-catalog

Then run a test by executing the following elsewhere:

    mvn archetype:generate -DarchetypeCatalog=local

## Releasing

After updates or fixes are made to an Attivio archetype it needs to be published to Maven Central as a new released version.

First, test the release.  Run from your working copy directory and take the provided defaults when prompted for release version, SCM tag, and next version.

    $ mvn release:prepare -DdryRun=true

If the dry-run fails, review the recommendations given in the output and take action accordingly.

If the dry-run passes, review the simulated release run and ensure it matches expectations.  Example output snippet:

    [INFO] Full run would be commit 3 files with message: '[maven-release-plugin] prepare release archetypes-0.1.1'
    [INFO] Full run would be tagging working copy /home/mmasi/Temp/attivio-archetypes with label: 'archetypes-0.1.1'
    [INFO] Transforming 'platform/archetypes'...
    [INFO] Transforming 'platform/archetypes/attivio-archetype-module'...
    [INFO] Transforming 'platform/archetypes/attivio-archetype-client'...
    [INFO] Not removing release POMs
    [INFO] Executing completion goals - since this is simulation mode it is running against the original project, not the rewritten ones
    [INFO] Full run would be commit 3 files with message: '[maven-release-plugin] prepare for next development iteration'
    [INFO] Release preparation simulation complete.

When satisfied with the dry-run's proposals, the release can be deployed.  

    $ mvn release:clean release:prepare
    ...
    $ mvn release:perform -Prelease

Maven will have done the following:

* Set the archetypes' versions to release (by dropping SNAPSHOT)
* Tag the code in GitHub
* Deploy the artifacts to Maven Central
* Increment all poms to the next SNAPSHOT version

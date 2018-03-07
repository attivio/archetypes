## Building and testing

To test, you'll want the archetype in a local catalog so that it can be found prior to publishing.  In the archetype directory:

    mvn clean install archetype:update-local-catalog

Then run a test by executing the following elsewhere:

    mvn archetype:generate -DarchetypeCatalog=local

## Releasing

After updates or fixes are made to an Attivio archetype it needs to be published to Maven Central as a new released version.

First, test the release.  Run from your working copy directory.

    $ mvn release:prepare -DdryRun=true
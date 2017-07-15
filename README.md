## Building and testing

To test, you'll want the archetype in a local catalog so that it can be found prior to publishing.  In the archetype directory:

    mvn clean install archetype:update-local-catalog

Then run a test by executing the following elsewhere:

    mvn archetype:generate -DarchetypeCatalog=local

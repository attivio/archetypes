# Testing and Releasing

## Testing

To test, you'll want the archetype in a local catalog so that it can be found prior to publishing.  In the archetype directory:

    mvn clean install archetype:update-local-catalog

Then run a test by executing the following elsewhere:

    mvn archetype:generate -DarchetypeCatalog=local

## Releasing

### Bintray Setup

1. Request membership to https://bintray.com/attivio from an Attivio contact.
2. Use the Bintray [Set Me Up](https://www.jfrog.com/confluence/display/BT/Main+Features#MainFeatures-SetMeUp) feature to generate the appropriate Maven server settings in your `settings.xml` configuration.

### Doing the release

1. Increment the version of [`pom.xml`](pom.xml) and [`attivio-archetype-module/pom.xml`](attivio-archetype-module/pom.xml)
2. Execute `mvn deploy` from the base of this repo.
3. [Publish](https://www.jfrog.com/confluence/display/BT/Managing+Uploaded+Content#ManagingUploadedContent-Publishing) the deployed files on Bintray.
4. Commit, tag, and push to GitHub
   ```
   git commit -m "<version> release"
   git tag -a archetypes-<version>
   git push
   git push --tags
   ```

def moduleDir = new File(request.getOutputDirectory()+"/"+request.getArtifactId())

// Find Attivio installation
def attivioHome = System.getenv('ATTIVIO_HOME')
if (attivioHome == null) {
    systemPath = System.getenv('PATH')
    if (systemPath != null) {
        systemPath.split(System.getProperty("path.separator")).each { p ->
            if (new File(p+"/../conf/attivio.license").exists()) {
                attivioHome = new File(p).getParent();
            }
        }
    }
}
if (attivioHome == null) {
    println "Client requires Attivio installation and Attivio was not found on path"
    attivioHome = System.console().readLine 'Attivio Installation Directory?: '
}
if (attivioHome != null) {
    println "Attivio installed at: ${attivioHome}"
}

// handle inclusion of hadoop dependencies
def hadoopMarker = 'HADOOP-MARKER'
def pomFile = new File(moduleDir, 'pom.xml')
println "Updating "+pomFile

def pomContent = pomFile.getText('UTF-8')
def hadoopDependencies = """
    <!-- Hadoop runtime dependencies -->
    <dependency>
      <groupId>org.apache.hbase</groupId>
      <artifactId>hbase-client</artifactId>
      <version>1.1.2</version>
    </dependency>
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-hdfs</artifactId>
      <version>2.7.2</version>
    </dependency>
    <dependency>
      <groupId>org.apache.hadoop</groupId>
      <artifactId>hadoop-common</artifactId>
      <version>2.7.2</version>
    </dependency>
"""
if (hadoop.equalsIgnoreCase("yes") || hadoop.equalsIgnoreCase("y")) {
    // replace marker with dependencies
    pomContent = pomContent.replace(hadoopMarker, hadoopDependencies)
} else {
    // remove marker from pom
    pomContent = pomContent.replace(hadoopMarker, '')
}

// update home directory
pomContent = pomContent.replace('ATTIVIO-HOME', attivioHome);

// rewrite pom.xml
pomFile.newWriter().withWriter { w ->
    w << pomContent
}

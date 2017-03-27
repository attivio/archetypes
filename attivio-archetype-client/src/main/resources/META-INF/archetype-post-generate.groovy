def moduleDir = new File(request.getOutputDirectory()+"/"+request.getArtifactId())

// Find Attivio installation
def env = System.getenv()
def attivioHome = env['ATTIVIO_HOME']
if (attivioHome == null) {
    env['PATH'].split(System.getProperty("path.separator")).each { p ->
	if (new File(p+"/../conf/attivio.license").exists() && attivioHome == null) {
	    attivioHome = new File(p).getParent();
	    println "Detected Attivio installation from path: $attivioHome"
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

def pomFile = new File(moduleDir, 'pom.xml')
println "Updating "+pomFile

def pomContent = pomFile.getText('UTF-8')

// update home directory
pomContent = pomContent.replace('ATTIVIO-HOME', attivioHome);

// rewrite pom.xml
pomFile.newWriter().withWriter { w ->
    w << pomContent
}

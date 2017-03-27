/**
* Copyright 2017 Attivio Inc., All rights reserved.
*/

package ${package};

import java.io.OutputStreamWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Test;

import com.attivio.model.AttivioException;
import com.attivio.model.document.AttivioDocument;
import com.attivio.sdk.client.AieClientFactory;
import com.attivio.sdk.client.IngestClient;
import com.attivio.sdk.client.DefaultAieClientFactory;
import com.attivio.util.stream.ByteArrayInputStreamBuilder;

/**
 * <p>
 * Sample client exercises the Attivio Java client APIs.
 * <p>
 * This class contains tests with commented out @Test annotations. To run one of the tests, the specific connection information
 * must be updated to match your environment. All tests require connection to a running Attivio backend.
 */
public class SampleAttivioClientTest {

  @Test
  public void testLocal() throws AttivioException {

    initializeLogging();
    runtest();
  }

  public void runtest() throws AttivioException {

    // create a remote Ingest Client, using the baseport (17000) we started Attivio with
    AieClientFactory clientFactory = new DefaultAieClientFactory();
    IngestClient feeder = clientFactory.createIngestClient("localhost", 17000);

    // set the default workflow
    feeder.setIngestWorkflowName("ingest");

    AttivioDocument doc = new AttivioDocument("doc1");

    /* Put some bytes in a content pointer. These bytes generally come from an external file or system. This allows the document
     * to flow easily around the system without requiring the memory for the external file until it is processed. The
     * contentStoreClient pushes the content bytes to the content store which can then provide them to any node in the system.
     * Content bytes are automatically cleaned up once the document processing is complete. */
    doc.setField("bytes", feeder.put("content1", new ByteArrayInputStreamBuilder("some test content".getBytes())));

    // regular string field
    doc.setField("title", "this is my title");

    // now ingest the document
    feeder.feed(doc);

    /* Wait for ingestion to complete. If we don't wait before issuing the commit (see next step), the document may not be fully
     * processed and indexed when the commit occurs. */
    feeder.waitForCompletion();

    /* Commit the index. This commit affects the entire index. All documents indexed by all clients will be committed. */
    feeder.commit();

    // wait for the commit to finish
    feeder.waitForCompletion();

    // Disconnect content feeder.
    feeder.disconnect();    
  }

  public static void initializeLogging() {
    ConsoleAppender ca = new ConsoleAppender();
    ca.setThreshold(Level.INFO);
    ca.setWriter(new OutputStreamWriter(System.out));
    ca.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
    Logger.getRootLogger().addAppender(ca);
  }

}

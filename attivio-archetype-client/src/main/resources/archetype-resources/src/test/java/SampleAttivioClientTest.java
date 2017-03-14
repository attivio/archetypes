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

import com.attivio.sdk.AttivioException;
import com.attivio.sdk.client.ContentStoreClient;
import com.attivio.sdk.client.IngestClient;
import com.attivio.sdk.ingest.IngestDocument;
import com.attivio.service.CuratorHelper;
import com.attivio.service.JmxAuthParams;
import com.attivio.service.Platform;
import com.attivio.service.ServiceFactory;
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

    /* These commands set up the system to communicate with ZooKeeper. All Attivio configuration and project information are
     * organized within ZooKeeper by project name and the project environment (default, dev, qa, prod, etc). The settings here
     * must match the Attivio system target for this client. If these settings do not match, errors will be logged like:
     * 
     * ERROR [main]: ATTIVIO-PLATFORM-98 : Service interface com.attivio.sdk.api.ContentStoreProvider was not found within timeout
     * 10000 */
    CuratorHelper.setConnectionInfo("localhost:16980"); // zookeeper connection string
    Platform.instance.setProjectName("trigger");
    Platform.instance.setProjectEnvironment("default");

    runtest();
  }

  @Test
  public void testKerberizedHadoop() throws AttivioException {
    initializeLogging();

    /* These commands set up the system to communicate with ZooKeeper. All Attivio configuration and project information are
     * organized within ZooKeeper by project name and the project environment (default, dev, qa, prod, etc). The settings here
     * must match the Attivio system target for this client. If these settings do not match, errors will be logged like:
     * 
     * ERROR [main]: ATTIVIO-PLATFORM-98 : Service interface com.attivio.sdk.api.ContentStoreProvider was not found within timeout
     * 10000 */
    CuratorHelper.setConnectionInfo("c10-p11.eng.com:2181,c10-p12.eng.com:2181,c10-p13.eng.com:2181");
    Platform.instance.setProjectName("aie103");
    Platform.instance.setProjectEnvironment("default");

    // path to the keytab, can be anything local
    System.setProperty("security.hadoop.keytab", "/tmp/systemtest.keytab");

    /* the following properties may need to be set to match the project versions. only do so if test is not working. */
    /* hdfs.store.root hadoop.rpc.protection dfs.encryption.key.provider.uri */

    /* Note, when running this test you may see the following error when connecting to a Highly Available (HA) enabled HDFS
     * system. This error is benign and indicates that the first attempt to connect was made to the standby node. The system
     * automatically fails over to the master node.
     * 
     * INFO [main]: Exception while invoking getFileInfo of class ClientNamenodeProtocolTranslatorPB over
     * c10-p13.eng.com/10.17.49.74:8020. Trying to fail over immediately.
     * org.apache.hadoop.ipc.RemoteException(org.apache.hadoop.ipc.StandbyException): Operation category READ is not supported in
     * state standby. Visit https://s.apache.org/sbnn-error */

    /* Note, when connecting to a kerberized system, your Java installation must have the JCE Unlimited policy files installed. If
     * not you will see the following error:
     * 
     * com.attivio.sdk.AttivioException: STORE-25 : JCE Unlimited Strength Security Policy Files Not Installed: maxKeyLength=128 */
    runtest();
  }

  public void runtest() throws AttivioException {

    /* If JMX authorization has been configured, use this code to set the required username and password for your client. */
    ServiceFactory.setFactoryParams(new JmxAuthParams() {
      @Override
      public String getUsername() {
        return "aieadmin";
      }

      @Override
      public String getPassword() {
        return "attivio";
      }
    });

    IngestClient ingestClient = ServiceFactory.getService(IngestClient.class);
    ContentStoreClient contentStoreClient = ServiceFactory.getService(ContentStoreClient.class);

    IngestDocument doc = new IngestDocument("doc1");

    /* Put some bytes in a content pointer. These bytes generally come from an external file or system. This allows the document
     * to flow easily around the system without requiring the memory for the external file until it is processed. The
     * contentStoreClient pushes the content bytes to the content store which can then provide them to any node in the system.
     * Content bytes are automatically cleaned up once the document processing is complete. */
    doc.setField("bytes", contentStoreClient.store("content1", new ByteArrayInputStreamBuilder("some test content".getBytes())));

    // regular string field
    doc.setField("title", "this is my title");

    // now ingest the document
    ingestClient.feed(doc);

    /* Wait for ingestion to complete. If we don't wait before issuing the commit (see next step), the document may not be fully
     * processed and indexed when the commit occurs. */
    ingestClient.waitForCompletion();

    /* Commit the index. This commit affects the entire index. All documents indexed by all clients will be committed. */
    ingestClient.commit();

    // wait for the commit to finish
    ingestClient.waitForCompletion();
  }

  public static void initializeLogging() {
    ConsoleAppender ca = new ConsoleAppender();
    ca.setThreshold(Level.INFO);
    ca.setWriter(new OutputStreamWriter(System.out));
    ca.setLayout(new PatternLayout("%-5p [%t]: %m%n"));
    Logger.getRootLogger().addAppender(ca);
  }

}

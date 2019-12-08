//package ru.bobko.shop.frontend;
//
//import jdk.nashorn.internal.codegen.types.Type;
//import junit.framework.TestCase;
//import ru.bobko.shop.core.model.message.Message;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class RequestResponseManagerTest extends TestCase {
//  private RequestResponseManagerImpl manager;
//  private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
//
//  @Override
//  protected void setUp() throws Exception {
//    super.setUp();
//    manager = new RequestResponseManagerImpl();
//  }
//
//  @Override
//  protected void tearDown() throws Exception {
//    super.tearDown();
//    singleThreadExecutor.shutdown();
//  }
//
//  public void testSimpleRequestResponseCycle() throws InterruptedException {
//    Message request = Message.newRequest(Message.Type.HEARTH_BEAT, "", null);
//    Message response = request.respondOk(null);
//    singleThreadExecutor.submit(() -> {
//      try {
//        Thread.sleep(1000);
//      } catch (InterruptedException e) {
//        fail();
//      }
//      manager.notifyResponseCome(response);
//    });
//    Message message = manager.requestResponseCycle(request);
//    assertSame(message, response);
//  }
//}

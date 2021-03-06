/*
 * Copyright 2016-2020 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.reporter.brave;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import zipkin2.Span;
import zipkin2.junit.ZipkinRule;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class BasicUsageTest_Async extends BasicUsageTest<AsyncZipkinSpanHandler> {
  @Rule public ZipkinRule zipkin = new ZipkinRule();

  OkHttpSender sender = OkHttpSender.create(zipkin.httpUrl() + "/api/v2/spans");

  @Override AsyncZipkinSpanHandler zipkinSpanHandler(List<Span> spans) {
    return AsyncZipkinSpanHandler.newBuilder(sender)
        .messageTimeout(0, TimeUnit.MILLISECONDS) // don't spawn a thread
        .build();
  }

  @Override void triggerReport() {
    zipkinSpanHandler.flush();
    for (List<Span> trace : zipkin.getTraces()) {
      spans.addAll(trace);
    }
  }

  @Override public void close() {
    super.close();
    zipkinSpanHandler.close();
    sender.close();
  }
}

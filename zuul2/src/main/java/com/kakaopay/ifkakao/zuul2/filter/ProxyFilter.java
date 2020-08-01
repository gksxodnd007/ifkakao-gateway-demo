package com.kakaopay.ifkakao.zuul2.filter;

import com.kakaopay.ifkakao.zuul2.constant.FilterOrder;
import com.netflix.zuul.context.SessionContext;
import com.netflix.zuul.filters.http.HttpInboundFilter;
import com.netflix.zuul.message.http.HttpRequestMessage;
import com.netflix.zuul.message.http.HttpResponseMessage;
import com.netflix.zuul.message.http.HttpResponseMessageImpl;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import rx.Observable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author hubert.squid
 * @since 2020.08.01
 */
public class ProxyFilter extends HttpInboundFilter {

    @Override
    public int filterOrder() {
        return FilterOrder.PROXY.getOrder();
    }

    @Override
    public Observable<HttpRequestMessage> applyAsync(HttpRequestMessage input) {
        SessionContext context = input.getContext();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        System.out.println(">>> execute proxy filter");
        String url = extractUrl(context);

        switch (input.getMethod().toUpperCase()) {
            case "GET":
                HttpGet get = new HttpGet(url);
                try {
                    CloseableHttpResponse response = httpClient.execute(get);
                    HttpResponseMessage responseMessage = new HttpResponseMessageImpl(context, input, response.getStatusLine().getStatusCode());
                    responseMessage.setBodyAsText(readInputStream(response.getEntity().getContent()));
                    context.setStaticResponse(responseMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "POST":
            case "PUT":
            case "PATCH":
            case "DELETE":
        }

        return Observable.just(input);
    }

    @Override
    public boolean shouldFilter(HttpRequestMessage msg) {
        return true;
    }

    private String extractUrl(SessionContext context) {
        return context.getRouteHost().getProtocol() + "://" + context.getRouteHost().getHost() + ":" + context.getRouteHost().getPort() + context.getRouteVIP();
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        InputStreamReader isReader = new InputStreamReader(inputStream);
        //Creating a BufferedReader object
        BufferedReader reader = new BufferedReader(isReader);
        StringBuffer sb = new StringBuffer();
        String str;
        while((str = reader.readLine())!= null){
            sb.append(str);
        }

        return sb.toString();
    }
}

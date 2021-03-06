/*
 * Class: WebSocketConfiguration
 *
 * Created on Aug 18, 2018
 *
 * (c) Copyright Swiss Post Solutions Ltd, unpublished work
 * All use, disclosure, and/or reproduction of this material is prohibited
 * unless authorized in writing.  All Rights Reserved.
 * Rights in this program belong to:
 * Swiss Post Solution.
 * Floor 4-5-8, ICT Tower, Quang Trung Software City
 */
package vn.minhtran.study.stompandsockjs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration
		extends
			AbstractWebSocketMessageBrokerConfigurer {

	@Bean
	SimpleTextWebSocketHandler webSocketHandler() {
		return new SimpleTextWebSocketHandler();
	}
	/*
	 * 1. Browser sends http://localhost:8081/process/info to server to get
	 * websocket information 2. Browser sends
	 * ws://localhost:8081/{server-id}/{session-id}/{transport} to server to
	 * establish WS connection 3. Browser sends subscription to message
	 * 
	 * 
	 */
	@Bean
	ThreadPoolTaskScheduler sockJSHeartBeatScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(1);
		threadPoolTaskScheduler.setThreadNamePrefix("sockjs-heartbeat-");
		return threadPoolTaskScheduler;
	}

	@Bean
	ThreadPoolTaskScheduler heartBeatScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(1);
		threadPoolTaskScheduler.setThreadNamePrefix("ws-heartbeat-");
		return threadPoolTaskScheduler;
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/process").setAllowedOrigins("*").withSockJS()
				.setTaskScheduler(sockJSHeartBeatScheduler())
				.setHeartbeatTime(15000);
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app")
				.enableSimpleBroker("/topic")
				.setTaskScheduler(heartBeatScheduler())
				.setHeartbeatValue(new long[]{15000, 15000});
	}

}
package com.hackgene;


import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hackgene.event.EventListener;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.lifecycle.ResumeEvent;
import discord4j.gateway.intent.IntentSet;

@Configuration
public class BotConfiguration {

	@Value("${token}")
    private String token;
	
    @Bean
    <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
    	
    	GatewayDiscordClient client = DiscordClientBuilder.create(token)
    	          .build().gateway().setEnabledIntents(IntentSet.all())
    	          .login()
    	          .block();
    	
    	client.getEventDispatcher().on(ReadyEvent.class)
    	.subscribe(event -> System.out.println("Logged in as: " + event.getSelf().getUsername()));
    	
    	client.getEventDispatcher().on(ReconnectEvent.class)
    	.subscribe(event -> System.out.println("Reconnected"));

    	client.getEventDispatcher().on(ResumeEvent.class)
        .subscribe(event -> System.out.println("Resumed session "));

    	for(EventListener<T> listener : eventListeners ) {
    		
    		client.getEventDispatcher().on(listener.getEventType()).flatMap(listener::execute).subscribe();
    	}    	
    	
        return client;
    }    
   
}

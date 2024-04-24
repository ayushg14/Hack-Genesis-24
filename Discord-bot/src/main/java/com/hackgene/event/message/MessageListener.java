package com.hackgene.event.message;

import org.springframework.beans.factory.annotation.Value;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public abstract class MessageListener {
	
	private String echo = "!echo";
	private String cmd = "!bg";
	
	private String echoMsg;
	private String bgMsg;
	
	@Value("${roleId}")
	private String roleId;
	@Value("${bgRoleId}")
	private String bgRoleId;
	
    public Mono<Void> processCommand(Message eventMessage) {
    	String content = eventMessage.getContent();
    	try {
    		
    		if(content.startsWith(echo)) {
    			
    			echoMsg = content.substring(echo.length()).trim();
    			
    			if(echoMsg == "") {
    				echoMsg = "Write a message after !echo";
    			}
    			
    			return Mono.just(eventMessage)
    					.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
    					.filter(message -> message.getContent().startsWith(echo))
    					.flatMap(Message::getAuthorAsMember)
    					.flatMap(member -> member.getRoles()
    							.any(role -> role.getId().asString().equals(roleId))
    							.filter(hasRole -> hasRole)
    							.flatMap(hasRole -> eventMessage.getChannel()
    									.flatMap(channel -> channel.createMessage(echoMsg))))
    					.then();
    		}
    		else{
    			bgMsg = content.substring(cmd.length()).trim();
    			return scheduleTask(eventMessage);
    		}
    	}catch(Exception e) {
    		e.printStackTrace(); // Log the exception for debugging (consider a logging library)
            return Mono.empty();
        
    	}
    	
    }

	private Mono<Void> scheduleTask(Message eventMessage){
		return Mono.just(eventMessage)
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.filter(message -> message.getContent().startsWith(cmd))
				.flatMap(Message::getAuthorAsMember)
				.flatMap(member -> member.getRoles()
						.any(role -> role.getId().asString().equals(bgRoleId))
						.filter(hasRole -> hasRole)
						.flatMap(hasRole -> eventMessage.getChannel()
								.flatMap(channel -> channel.createMessage(bgMsg))))
				.then();
    }
}

package com.hackgene.event.join;

import org.springframework.beans.factory.annotation.Value;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Mono;

public abstract class JoinListener {
	@Value("${welChannel}") 
	private String welChal;
	
	@Value("${emoji}")
	private String emoji;

	    
	public Mono<Void> processCommand(MemberJoinEvent event) {
		Snowflake channelId = Snowflake.of(welChal);

        return Mono.just(event)
                .flatMap(memberJoinEvent ->
                        event.getClient()
                                .getChannelById(channelId)
                                .ofType(TextChannel.class)
                                .flatMap(channel ->
                                        channel.createMessage("Welcome **" + event.getMember().getUsername() + "**! To the server " + emoji +" ✨")
                                        )
                                
                )
                .then();
    }
}
	
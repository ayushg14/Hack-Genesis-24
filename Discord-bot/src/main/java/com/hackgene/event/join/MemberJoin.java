package com.hackgene.event.join;

import org.springframework.stereotype.Service;

import com.hackgene.event.EventListener;

import discord4j.core.event.domain.guild.MemberJoinEvent;
import reactor.core.publisher.Mono;

@Service
public class MemberJoin extends JoinListener implements EventListener<MemberJoinEvent>{

	@Override
	public Class<MemberJoinEvent> getEventType() {
		
		return MemberJoinEvent.class;
	}
	@Override
	public Mono<Void> execute(MemberJoinEvent event) {
		return processCommand(event);
	}

}

package me.ryguy.testbot;

import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import me.ryguy.discordapi.command.Command;
import me.ryguy.discordapi.util.WorkFlow;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

public class TestWorkFlow extends Command {
    public TestWorkFlow() {
        super("testworkflow", "twf");
    }

    @Override
    public Mono<Void> execute(Message message, String alias, String[] args) {

        AtomicReference<String> name = new AtomicReference<>();
        AtomicReference<String> desc = new AtomicReference<>();
        AtomicReference<String> footer = new AtomicReference<>();

        System.out.println("Executing workflow!");

        WorkFlow<EmbedCreateSpec> workFlow = new WorkFlow<EmbedCreateSpec>(new EmbedCreateSpec(), message.getChannel().block(), message.getAuthor().get());

        workFlow.addRule("!cancel", embed -> {
            message.getChannel().block().createMessage(":x: Workflow Cancelled!").block();
            workFlow.end();
        }).andThen(embed -> {
            message.getChannel().block().createEmbed(e -> {
                e.setColor(Color.GREEN);
                e.setTitle("Welcome to embed creator!");
                e.setDescription("Enter a description!");
            }).block();
        }, (embed, f, m) -> {
            desc.set(m.getContent());
            embed.setDescription(m.getContent());
            f.nextStep();
        }).andThen(embed -> {
            message.getChannel().block().createEmbed(e -> {
                e.setColor(Color.GREEN);
                e.setDescription("Enter a title!");
            }).block();
        }, (embed, f, m) -> {
            embed.setTitle(m.getContent());
            name.set(m.getContent());
            f.nextStep();
        }).andThen(embed -> {
            message.getChannel().block().createEmbed(e -> {
                e.setColor(Color.GREEN);
                e.setDescription("Enter a footer!");
            }).block();
        }, (embed, f, m) -> {
            footer.set(m.getContent());
            embed.setFooter(m.getContent(), null);

            message.getChannel().block().createEmbed(e -> {
                e.setTitle(name.get());
                e.setDescription(desc.get());
                e.setFooter(footer.get(), null);
                e.setColor(Color.GREEN);
            }).block();
            f.end();
        }).start();

        return null;
    }
}
package cn.wubo.chatbot.core.impl;

import cn.wubo.chatbot.config.ChatbotConfigurationProperties;
import cn.wubo.chatbot.core.IChatbotService;
import cn.wubo.chatbot.message.MarkdownContent;
import cn.wubo.chatbot.message.RequestContent;
import cn.wubo.chatbot.message.TextContent;
import cn.wubo.chatbot.platform.ISendService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ChatbotServiceImpl implements IChatbotService {

    ChatbotConfigurationProperties properties;
    List<ISendService> sendServices;

    public ChatbotServiceImpl(ChatbotConfigurationProperties properties,List<ISendService> sendServices) {
        this.properties = properties;
        this.sendServices = sendServices;
    }

    @Override
    public List<String> send(RequestContent content) {
        List<String> strings = new ArrayList<>();
        properties.getChatbotInfo().forEach(chatbotInfo -> {
            sendServices.stream()
                    .filter(service -> service.support(chatbotInfo.getChatbotType()) && content.getChatbotType().isEmpty() || content.getChatbotType().stream().anyMatch(service::support))
                    .filter(service -> content.getAlias().isEmpty() || content.getAlias().stream().anyMatch(e -> chatbotInfo.getAlias().equals(e)))
                    .findAny()
                    .ifPresent(service -> {
                        if (content instanceof TextContent)
                            strings.add(service.sendText(chatbotInfo, (TextContent) content));
                        else if (content instanceof MarkdownContent)
                            strings.add(service.sendMarkDown(chatbotInfo, (MarkdownContent) content));
                    });
        });
        return strings;
    }
}

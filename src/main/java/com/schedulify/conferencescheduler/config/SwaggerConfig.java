package com.schedulify.conferencescheduler.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Conference Scheduler API")
                        .version("1.0")
                        .description("The Conference Scheduler API allows you to schedule presentations for a conference, creating different tracks for the presentations. The API receives a list of presentations with their subjects and durations, and organizes them into tracks based on the following rules:\n\n" +
                                "* Morning and afternoon presentations will be held at the conference.\n" +
                                "* Multiple presentations can take place simultaneously in both morning and afternoon sessions.\n" +
                                "* Morning presentations start at 9:00 AM and end at 12:00 PM.\n" +
                                "* Lunch will be at 12:00 PM.\n" +
                                "* Afternoon presentations start at 1:00 PM and continue until networking activities begin. If there is no networking activity, they end at 5:00 PM.\n" +
                                "* Networking activities are held if time allows after the presentations. They cannot start before 4:00 PM and must end by 5:00 PM at the latest.\n" +
                                "* Presentation durations are in minutes or specified as \"lightning\" (5 minutes). For more details, see: [Lightning Talk](https://en.wikipedia.org/wiki/Lightning_talk).\n" +
                                "* There are no breaks between presentations.\n\n")
                        .contact(new Contact()
                                .name("Cem Aktas")
                                .email("cemaktas@ymail.com")));
    }
}

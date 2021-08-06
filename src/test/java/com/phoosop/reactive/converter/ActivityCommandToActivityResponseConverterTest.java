package com.phoosop.reactive.converter;

import com.phoosop.reactive.model.command.ActivityCommand;
import com.phoosop.reactive.model.response.ActivityResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ActivityCommandToActivityResponseConverterTest {

    @InjectMocks
    private ActivityCommandToActivityResponseConverterImpl activityCommandToActivityResponseConverter;

    @Test
    public void testConvert() {
        ActivityCommand source = new ActivityCommand()
                .setActivity("activity")
                .setAccessibility(1D)
                .setKey("key")
                .setLink("link")
                .setPrice(100)
                .setType("type")
                .setParticipants(3);

        ActivityResponse activityResponse = activityCommandToActivityResponseConverter.convert(source);

        assertThat(activityResponse.getActivity()).isEqualTo("activity");
        assertThat(activityResponse.getAccessibility()).isEqualTo(1D);
        assertThat(activityResponse.getKey()).isEqualTo("key");
        assertThat(activityResponse.getLink()).isEqualTo("link");
        assertThat(activityResponse.getPrice()).isEqualTo(100);
        assertThat(activityResponse.getType()).isEqualTo("type");
        assertThat(activityResponse.getParticipants()).isEqualTo(3);

    }

}
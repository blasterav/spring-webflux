package com.phoosop.reactive.converter;

import com.phoosop.reactive.model.command.ActivityCommand;
import com.phoosop.reactive.model.external.ActivityExternal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ActivityExternalToActivityCommandConverterTest {

    @InjectMocks
    private ActivityExternalToActivityCommandConverterImpl activityExternalToActivityCommandConverter;

    @Test
    public void testConvert() {
        ActivityExternal source = new ActivityExternal()
                .setActivity("activity")
                .setAccessibility(1D)
                .setKey("key")
                .setLink("link")
                .setPrice(100)
                .setType("type")
                .setParticipants(3);

        ActivityCommand activityCommand = activityExternalToActivityCommandConverter.convert(source);

        assertThat(activityCommand.getActivity()).isEqualTo("activity");
        assertThat(activityCommand.getAccessibility()).isEqualTo(1D);
        assertThat(activityCommand.getKey()).isEqualTo("key");
        assertThat(activityCommand.getLink()).isEqualTo("link");
        assertThat(activityCommand.getPrice()).isEqualTo(100);
        assertThat(activityCommand.getType()).isEqualTo("type");
        assertThat(activityCommand.getParticipants()).isEqualTo(3);
    }

}
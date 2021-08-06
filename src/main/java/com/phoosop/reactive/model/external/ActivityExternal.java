package com.phoosop.reactive.model.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityExternal {

    public String activity;

    public String type;

    public Integer participants;

    public Integer price;

    public String link;

    public String key;

    public Double accessibility;

}

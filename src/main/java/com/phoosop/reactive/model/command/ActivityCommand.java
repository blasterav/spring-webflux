package com.phoosop.reactive.model.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ActivityCommand {

    public String activity;

    public String type;

    public Integer participants;

    public Integer price;

    public String link;

    public String key;

    public Double accessibility;

}

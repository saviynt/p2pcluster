package com.saviynt.p2pcluster;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageDto implements Serializable {

    private String msg;
    private String ipAddr;

}

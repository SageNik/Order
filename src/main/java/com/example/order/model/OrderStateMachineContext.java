package com.example.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "order_state_machine_context")
public class OrderStateMachineContext {

    @Id
    @Column(
            name = "machine_id"
    )
    private String machineId;
    @Column(
            name = "state"
    )
    private String state;
    @Lob
    @Column(
            name = "state_machine_context",
            length = 10240
    )
    private byte[] stateMachineContext;
}
